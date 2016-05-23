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
        ServerSocket myServerSocket = null;

        try {
            int receivedFiles = 0;
            System.out.println("Listening in " + socketnr + ", Still Waiting for a connection");
            myServerSocket = new ServerSocket(socketnr);
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
                        RSA.GenerateKeys(path, ServerLocalHost.parameters[2]);
                    }
                    Client.Send(path + File.separator + "public.key", ServerLocalHost.parameters[0], 13502);
                }
                if (fileName.equals("public.key")) {
                    receivedFiles++;
                    Hasher.CheckSumSHA256(ServerLocalHost.parameters[2], ServerLocalHost.parameters[2] + "hash");
                    RSA.Encrypt(Files.readAllBytes(Paths.get(path + File.separator + "Encrypted" + "hash")),
                            path + File.separator + "hashsigned", RSA.KeyType.PRIVATE);

                    SecretKey myDesKey = DES.openFile(path + File.separator + "deskey");
                    DES.Encrypt(myDesKey, new FileInputStream(ServerLocalHost.parameters[2]), new FileOutputStream(path + File.separator + "Encrypted" + fileName));
                    DES.Encrypt(myDesKey, new FileInputStream(ServerLocalHost.parameters[2] + "hashsigned"), new FileOutputStream(path + File.separator + "Encrypted" + "encryptedhashsigned"));
                    RSA.Encrypt(Base64.getEncoder().encodeToString(myDesKey.getEncoded()).getBytes(), path + File.separator + "deskey", RSA.KeyType.PUBLIC);


                    Client.Send(path + File.separator + "Encrypted" + fileName, ServerLocalHost.parameters[1]);
                    Client.Send(path + File.separator + "Encrypted" + "hashsigned", ServerLocalHost.parameters[1]);
                    Client.Send(path + File.separator + "Encrypted" + "deskey", ServerLocalHost.parameters[1]);

                } else {
                    Files.copy(d, new File(path + fileName).toPath());//
                    System.out.println("Data Received " + fileName);
                    receivedFiles++;
                    //als alle bestanden ontvangen ziJn kan het bestand ontciJfert worden
                    if (receivedFiles == 3) {
                        byte[] originalDes = RSA.Decrypt(Files.readAllBytes(Paths.get(path + File.separator + "Encrypted" + "deskey")), path + File.separator + "private.key", RSA.KeyType.PRIVATE);
                        //SecretKey myDesKey = DES.openFile(path + File.separator +  "deskey");
                        SecretKey myDesKey = new SecretKeySpec(originalDes, 0, originalDes.length, "DES");
                        DES.Decrypt(myDesKey, new FileInputStream(path + File.separator + "Encrypted" + fileName), new FileOutputStream(path + File.separator + fileName));
                        Client.Send("gelukt, hash is: " + Hasher.CheckSumSHA256(path + "Encrypted" + "hash"), "127.0.0.1", 8888);
                    }
                }
                    /* testing the encryption with strings
                        byte[] encrDesPass;
                        byte[] originalDes;
                        encrDesPass = RSA.Encrypt(Base64.getEncoder().encodeToString(myDesKey.getEncoded()).getBytes(), path + "public.key", RSA.KeyType.PUBLIC);
                        originalDes = RSA.Decrypt(encrDesPass, path + "private.key", RSA.KeyType.PRIVATE);
                        System.out.println("encrypted pass= " + new String(encrDesPass));
                        System.out.println("originele pass= " + new String(originalDes));
                    */
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
