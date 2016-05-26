package be.pxl;

import org.apache.commons.io.FileUtils;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class Server implements Runnable {
    private int socketnr;

    Server(int socketnr) {
        this.socketnr = socketnr;
    }


    @Override
    public void run() {
        int receivedFiles = 0; // we houden bij hoeveel bestanden er ontvangen zijn
        String path = System.getProperty("user.home") + File.separator + "CRYPTO" + File.separator; //Dit werkt op alle platformen met een home directory
        ServerSocket myServerSocket = null;
        String bestandsNaam=null;

        try {


            System.out.println("Listening in " + socketnr + ",  Waiting for a connection");
            myServerSocket = new ServerSocket(socketnr);
            //de server moet meerdere aanvragen kunnen behandelen dus we steken de accept functie in een while loop zodat hij blijf accepten
            int i = 0;
            while (i < 1) {
                Socket mySocket = myServerSocket.accept();
                System.out.println("Connected Successfully");

                BufferedInputStream in = new BufferedInputStream(mySocket.getInputStream());
                try (DataInputStream d = new DataInputStream(in)) {
                    String fileName = d.readUTF(); //de bestandsnaam wordt in utf verstuurd, op deze manier is bestandsnaam en bestand inhoud appart identificeerbaar

                /*
                Indien een verzender onze Public key nodig heeft stuurt hij ons een "KEYREQUEST" en ook tegelijk zijn public key zodat we achteraf kunnen controleren of het van hem komt
                 */
                    System.out.println(mySocket.getInetAddress().getHostAddress());
                    if (fileName.equals("KEYREQUEST")) {
                        FileUtils.deleteDirectory(new File(path));
                        if (!new File(path).exists()) {
                            Files.createDirectory(Paths.get(path));
                        }
                        RSA.GenerateKeys(path, "B");
                        Files.copy(d, new File(path + "Public.key").toPath()); // we slagen zijn public key op
                        System.out.println("sending public: " + path + "Public_B.key");
                        Client.SendWithOtherName("Public.key", path + "Public_B.key", mySocket.getInetAddress().getHostAddress(), 13501); //we sturen onze Public Key
                        System.out.println("send public");
                    } else if (fileName.equals("Public.key")) {
                    /*
                     Encryption starts here
                    path + new File(ServerLocalHost.parameters[2]).getName()) = encrypted file
                    file2 = encrypted DES key
                    file3 = encrypted signed hash
                    */
                        Files.copy(d, new File(path + fileName).toPath());
                        System.out.println("starting to hash");
                        Hasher.CheckSumSHA256(ServerLocalHost.parameters[1], path + "Hash"); //hash maken van bestand en opslaan
                        byte[] signedHash = RSA.Encrypt(Files.readAllBytes(Paths.get(path + "Hash")), path + "Private_B.key", RSA.KeyType.PRIVATE); //hash signen
                        // byte[] encryptedSignedHash = RSA.Encrypt(signedHash, path + "Public.key", RSA.KeyType.PUBLIC); // encrypteren
                        FileHelper.StoreFile(signedHash, path + File.separator + "File_3"); //opslaan

                        DES.GenerateKeys(path + "DesKey"); //deskey genereren
                        SecretKey myDesKey = DES.openFile(path + "DesKey"); //Deskey uit bestand uitlezen en er terug een SecretKey van maken
                        byte[] encryptedDes = RSA.Encrypt((myDesKey.getEncoded()), path + "Public.key", RSA.KeyType.PUBLIC); //DES key door RSA halen
                        FileHelper.StoreFile(encryptedDes, path + "File_2"); //DES encrypted opslaan

                        //Bestand uit parameters van GUI encrypteren met DES en dat bestand opslagen in home/$user/CRYPTO/bestandsnaam
                        DES.Encrypt(myDesKey, new FileInputStream(ServerLocalHost.parameters[1]), new FileOutputStream(path + new File(ServerLocalHost.parameters[1]).getName()));

                    /*
                    De 3 bestanden opsturen naar de persoon die ons zijn Public.key heeft gestuurd
                     */

                        Client.Send(path + new File(ServerLocalHost.parameters[1]).getName(), mySocket.getInetAddress().getHostAddress());
                        Client.Send(path + "File_2", mySocket.getInetAddress().getHostAddress());
                        Client.Send(path + "File_3", mySocket.getInetAddress().getHostAddress());

                    } else {
                        receivedFiles++;
                        Files.copy(d, new File(path + fileName).toPath());//
                        System.out.println("File " + fileName + " Received");
                        if ((fileName.equals("File_2") || fileName.equals("File_3")) == false) {
                            bestandsNaam = fileName;
                        }
                        //als alle bestanden ontvangen ziJn kan het bestand ontciJfert worden
                        if (receivedFiles == 3) {
                            //Een subfolder van CRYPTO om de plaintext bstanden te zetten
                            String path2 = path + "CRYPTODECRYPTED" + File.separator;
                            if (!new File(path2).isDirectory()) {
                                Files.createDirectory(Paths.get(path2));
                            }

                            byte[] originalDes = RSA.Decrypt(Files.readAllBytes(Paths.get(path + "File_2")), path + "Private_B.key", RSA.KeyType.PRIVATE);
                            SecretKey myDesKey = new SecretKeySpec(originalDes, 0, originalDes.length, "DES");//Deskey uit bestand uitlezen en er terug een SecretKey van maken
                            // byte[] signedHash = RSA.Decrypt(Files.readAllBytes(Paths.get(path + bestandsNaam)), path + "Private_B.key", RSA.KeyType.PRIVATE);
                            byte[] hash = RSA.Decrypt((Files.readAllBytes(Paths.get(path + "File_3"))), path + "Public.key", RSA.KeyType.PUBLIC);

                            DES.Decrypt(myDesKey, new FileInputStream(path + bestandsNaam), new FileOutputStream(path2 + bestandsNaam)); // het bestand
                            FileHelper.StoreFile(hash, path2 + "originalHash"); //de hash die ontvangen is
                            Hasher.CheckSumSHA256(path2 + bestandsNaam, path2 + "Hash"); //de hash van het ontvangen bestand

                            //Desktop.getDesktop().open(new File(path2 ));
                            if (FileUtils.contentEquals(new File(path2 + "originalHash"), new File(path2 + "Hash"))) {

                                Client.SendToCsharp("De text hash is OK", "127.0.0.1");
                            } else {

                                Client.SendToCsharp("De hash is NIET OK\n Hash 1 is: " + Paths.get(path2 + "originalHash").toFile().length() + "\n de andere hash is" + Paths.get(path2 + "Hash").toFile().length(), "127.0.0.1");
                            }
                            receivedFiles = 0;
                            System.out.println("afgerond");
                        }
                    }
                }
                mySocket.close();
            }

        } catch (IOException ex) {
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (myServerSocket != null) {
                try {
                    myServerSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }
}
