package be.pxl;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class Server implements Runnable {
    private int socketnr;
    private String path= new File("").getAbsolutePath() + "\\";

    Server(int socketnr) {
        this.socketnr = socketnr;
    }

    @Override
    public void run() {
        try {
int receivedFiles=0;
            System.out.println("Listening in "+socketnr+", Still Waiting for a connection");
            ServerSocket myServerSocket = new ServerSocket(socketnr);
            int i = 0;
            while (i < 1) {
                Socket mySocket = myServerSocket.accept();
                System.out.println("Connected Successfully");

                BufferedInputStream in = new BufferedInputStream(mySocket.getInputStream());
                DataInputStream d = new DataInputStream(in);
                String fileName = d.readUTF();
                //als er bestanden moeten verzonden worden stuurt de front-end alle parameters
                String[] dataArray = fileName.split(";");
                //als er enkel een bestand ontvangen wordt moet er niks anders gedaan worden
                if (dataArray.length==1){
                    //doorgegeven string bevat EXIT dus deze thread moet stoppen/niet meer loopen
                    if(!dataArray[0].contains("EXIT")){
                        i++;
                    } else {
                        //als alle bestanden ontvangen ziJn kan het bestand ontciJfert worden
                        if (receivedFiles==3){
                            byte[] originalDes=RSA.Decrypt(Files.readAllBytes(Paths.get(path + "CryptoP2P\\d.txt")), path + "private.key", RSA.KeyType.PRIVATE);
                            SecretKey myDesKey = new SecretKeySpec(originalDes, 0, originalDes.length, "DES");
                            DES.Decrypt(myDesKey, new FileInputStream(path + "CryptoP2P\\d.txt"), new FileOutputStream(path + "CryptoP2P\\de.txt"));
                            Client.Send("gelukt, hash is: " + Hasher.CheckSumSHA256(path + "CryptoP2P\\de.txt"),"127.0.0.1",8888);
                        }
                        Files.copy(d, new File(path + fileName).toPath());
                        receivedFiles++;
                    }
                } else if (fileName.contains("ip")) {
                    System.out.println("ip werd doorgestuurd");
                    if (!RSA.GetAreKeysGenerated()){
                        RSA.GenerateKeys("");
                    }
                    /* testing the encryption with strings
                        byte[] encrDesPass;
                        byte[] originalDes;
                        encrDesPass = RSA.Encrypt(Base64.getEncoder().encodeToString(myDesKey.getEncoded()).getBytes(), path + "public.key", RSA.KeyType.PUBLIC);
                        originalDes = RSA.Decrypt(encrDesPass, path + "private.key", RSA.KeyType.PRIVATE);
                        System.out.println("encrypted pass= " + new String(encrDesPass));
                        System.out.println("originele pass= " + new String(originalDes));
                    */

                        KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
                        SecretKey myDesKey = keyGenerator.generateKey();
                        DES.Encrypt(myDesKey, new FileInputStream(path + "CryptoP2P\\s.txt"), new FileOutputStream(path + "CryptoP2P\\d.txt"));
                        Client.Send(path + "CryptoP2P\\d.txt", dataArray[1]);

                }


                mySocket.close();
                System.out.println("Data Received");
            }

            myServerSocket.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex){
            System.out.println(ex.getMessage());
        }
        }
}
