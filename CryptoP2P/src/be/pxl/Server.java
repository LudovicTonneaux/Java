package be.pxl;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class Server implements Runnable {
    private int socketnr;
    private String path = System.getProperty("user.home") + File.separator + "CRYPTO" + File.separator;// new File("").getAbsolutePath() + "\\";

    Server(int socketnr) {
        this.socketnr = socketnr;
    }


    @Override
    public void run() {
        try {
            int receivedFiles = 0;
            System.out.println("Listening in " + socketnr + ", Still Waiting for a connection");
            ServerSocket myServerSocket = new ServerSocket(socketnr);
            int i = 0;
            while (i < 1) {
                Socket mySocket = myServerSocket.accept();
                System.out.println("Connected Successfully");

                BufferedInputStream in = new BufferedInputStream(mySocket.getInputStream());
                DataInputStream d = new DataInputStream(in);
                String fileName = d.readUTF();
                if (fileName.equals("KEYREQUEST")) {
                    //isDirectory checks if the folder exists and if it's a folder
                    if (!new File(path).exists()) {
                        Files.createDirectory(Paths.get(path));
                    }
                    if (!RSA.GetAreKeysGenerated()) {
                        RSA.GenerateKeys(path,System.getProperty("user.name"));
                    }
                    Client.Send(path + File.separator + "public.key", ServerLocalHost.parameters[0], 13502);
                }
                if (fileName.equals("public.key")) {
                    receivedFiles++;
                    RSA.GenerateKeys(path, "A");
                    Hasher.CheckSumSHA256(path + "File", path + "hash");
                    FileHelper.StoreFile(
                            RSA.Encrypt(
                                    RSA.Encrypt(Files.readAllBytes(
                                            Paths.get(path + "Hash")), path + "Private_A.key", RSA.KeyType.PRIVATE),path+"Public_B.key", RSA.KeyType.PUBLIC),path + File.separator + "File_3");

                    DES.GenerateKeys(path + "DesKey");
                    SecretKey myDesKey = DES.openFile(path + "DesKey");
                    DES.Encrypt(myDesKey, new FileInputStream(path + "File"), new FileOutputStream(path + "File_1"));
                    FileHelper.StoreFile(RSA.Encrypt((myDesKey.getEncoded()), path + "Public_B.key", RSA.KeyType.PUBLIC), path + "File_2");


                    Client.Send(path + File.separator  + fileName, ServerLocalHost.parameters[1]);
                    Client.Send(path + File.separator  + "hashsigned", ServerLocalHost.parameters[1]);
                    Client.Send(path + File.separator + "deskey", ServerLocalHost.parameters[1]);

                } else {
                    Files.copy(d, new File(path + fileName).toPath());//
                    System.out.println("Data Received " + fileName);
                    receivedFiles++;
                    //als alle bestanden ontvangen ziJn kan het bestand ontciJfert worden
                    if (receivedFiles == 3) {
                        String path2 = System.getProperty("user.home") + File.separator + "CRYPTODECRYPTED" + File.separator;
                        if (!new File(path2).isDirectory()) {
                            Files.createDirectory(Paths.get(path2));
                        }

                        byte[] originalDes = RSA.Decrypt(Files.readAllBytes(Paths.get(path + "File_2")), path + "Private_B.key", RSA.KeyType.PRIVATE);
                        SecretKey myDesKey = new SecretKeySpec(originalDes, 0, originalDes.length, "DES");
                        DES.Decrypt(myDesKey, new FileInputStream(path + "File_1"), new FileOutputStream(path2 + "File"));
                        FileHelper.StoreFile(
                                RSA.Decrypt(
                                        RSA.Decrypt(
                                                Files.readAllBytes(Paths.get(path + "File_3")), path + "Public_A.key", RSA.KeyType.PUBLIC),path+"Private_B.key",RSA.KeyType.PRIVATE), path2 + "Hash");
                    }
                }
                mySocket.close();
            }
            myServerSocket.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
