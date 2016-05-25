package be.pxl;

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

                /*
                We maken de CRYPTO directory en onze RSA keys
                isDirectory checks of de folder bestaat en of het een folder is
                */
                if (!new File(path).exists()) {
                    Files.createDirectory(Paths.get(path));
                }
                if (!RSA.GetAreKeysGenerated()) {
                    RSA.GenerateKeys(path, System.getProperty("user.name"));
                }

                BufferedInputStream in = new BufferedInputStream(mySocket.getInputStream());
                DataInputStream d = new DataInputStream(in);
                String fileName = d.readUTF(); //de bestandsnaam wordt in utf verstuurd, op deze manier is bestandsnaam en bestand inhoud appart identificeerbaar

                /*
                Indien een verzender onze Public key nodig heeft stuurt hij ons een "KEYREQUEST" en ook tegelijk zijn public key zodat we achteraf kunnen controleren of het van hem komt
                 */
                if (fileName.equals("KEYREQUEST")) {
                    Files.copy(d, new File(path + "Public.key").toPath()); // we slagen zijn public key op
                    Client.SendWithOtherName("Public.key", path + "Public_" + System.getProperty("user.name") + ".key", ServerLocalHost.parameters[0], 13502); //we sturen onze Public Key
                }
                if (fileName.equals("Public.key")) {
                    /*
                     Encryption starts here
                    path + new File(ServerLocalHost.parameters[2]).getName()) = encrypted file
                    file2 = encrypted DES key
                    file3 = encrypted signed hash
                    */
                    Hasher.CheckSumSHA256(ServerLocalHost.parameters[2], path + "Hash"); //hash maken van bestand
                    byte[] signedHash = RSA.Encrypt(Files.readAllBytes(Paths.get(path + "Hash")), path + "Private_" + System.getProperty("user.name") + ".key", RSA.KeyType.PRIVATE); //hash signen
                    byte[] encryptedSignedHash = RSA.Encrypt(signedHash, path + "Public.key", RSA.KeyType.PUBLIC); // encrypteren
                    FileHelper.StoreFile(encryptedSignedHash, path + File.separator + "File_3"); //opslaan

                    DES.GenerateKeys(path + "DesKey"); //deskey genereren
                    SecretKey myDesKey = DES.openFile(path + "DesKey"); //Deskey uit bestand uitlezen en er terug een SecretKey van maken
                    byte[] encryptedDes = RSA.Encrypt((myDesKey.getEncoded()), path + "Public.key", RSA.KeyType.PUBLIC); //DES key door RSA halen
                    FileHelper.StoreFile(encryptedDes, path + "File_2"); //DES encrypted opslaan

                    //Bestand uit parameters van GUI encrypteren met DES en dat bestand opslagen in home/$user/CRYPTO/bestandsnaam
                    DES.Encrypt(myDesKey, new FileInputStream(ServerLocalHost.parameters[2]), new FileOutputStream(path + new File(ServerLocalHost.parameters[2]).getName()));

                    /*
                    De 3 bestanden opsturen naar de persoon die ons zijn Public.key heeft gestuurd
                     */
                    Client.Send(path + new File(ServerLocalHost.parameters[2]).getName(), myServerSocket.getInetAddress().getHostAddress());
                    Client.Send(path + "File_2", myServerSocket.getInetAddress().getHostAddress());
                    Client.Send(path + "File_3", myServerSocket.getInetAddress().getHostAddress());

                } else {
                    receivedFiles++;
                    Files.copy(d, new File(path + fileName).toPath());//
                    System.out.println("File " + fileName + "Received");
                    if ((fileName.equals("File_2")||fileName.equals("File_3"))==false){
                        bestandsNaam = fileName;
                    }
                    //als alle bestanden ontvangen ziJn kan het bestand ontciJfert worden
                    if (receivedFiles == 3) {
                        //Een subfolder van CRYPTO om de plaintext bstanden te zetten
                        String path2 = path + "CRYPTODECRYPTED" + File.separator;
                        if (!new File(path2).isDirectory()) {
                            Files.createDirectory(Paths.get(path2));
                        }

                        byte[] originalDes = RSA.Decrypt(Files.readAllBytes(Paths.get(path + "File_2")), path + "Private_" + System.getProperty("user.name") + ".key", RSA.KeyType.PRIVATE);
                        SecretKey myDesKey = new SecretKeySpec(originalDes, 0, originalDes.length, "DES");//Deskey uit bestand uitlezen en er terug een SecretKey van maken
                        byte[] signedHash =  RSA.Decrypt(Files.readAllBytes(Paths.get(path + bestandsNaam)), path + "Private_" + System.getProperty("user.name") + ".key", RSA.KeyType.PRIVATE);
                        byte [] hash = RSA.Decrypt(signedHash,path+"Public.key", RSA.KeyType.PUBLIC);

                        DES.Decrypt(myDesKey, new FileInputStream(path + bestandsNaam), new FileOutputStream(path2 + "File")); // het bestand
                        FileHelper.StoreFile(hash, path2 + "originalHash"); //de hash die ontvangen is
                        Hasher.CheckSumSHA256(path2+bestandsNaam, path + "Hash"); //de hash van het ontvangen bestand
                        
                        if (Files.readAllBytes(Paths.get(path2+"originalHash")).equals(Files.readAllBytes(Paths.get(path2+"Hash")))){
                            Client.Send("De hash is OK","127.0.0.1",8889);
                        } else {
                            Client.Send("De hash is NIET OK","127.0.0.1",8889);
                        }
                    }
                    receivedFiles=0;
                }
                mySocket.close();
            }
            myServerSocket.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        } finally {
            if (myServerSocket == null) {
                try {
                    myServerSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }
        }
    }
}
