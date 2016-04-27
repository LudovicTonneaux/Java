package be.pxl;

import java.io.File;

public class Main {
    /**
     * Created by Samy Coenen on 01/03/2016.
     */
    public static void main(String[] args) {
        String path = new File("").getAbsolutePath() + "\\";

        Server s1 = new Server();
        s1.SetDirectory(path);
        Thread serverThread = new Thread(s1);
        serverThread.start();



        RSA.GenerateKeys();
        try {
            byte[] encrDesPass;
            byte[] originalDes;


            Client.Send(path + "CryptoP2P\\test.txt", "127.0.0.1");

           /* KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            SecretKey myDesKey = keyGenerator.generateKey();
            System.out.println(Base64.getEncoder().encodeToString(myDesKey.getEncoded()));

            DES.Encrypt(myDesKey, new FileInputStream(path + "CryptoP2P\\s.txt"), new FileOutputStream(path + "CryptoP2P\\d.txt"));
            encrDesPass = RSA.Encrypt(Base64.getEncoder().encodeToString(myDesKey.getEncoded()).getBytes(), path + "public.key", RSA.KeyType.PUBLIC);
            originalDes = RSA.Decrypt(encrDesPass, path + "private.key", RSA.KeyType.PRIVATE);
            System.out.println("encrypted pass= " + new String(encrDesPass));
            System.out.println("originele pass= " + new String(originalDes));
            DES.Decrypt(myDesKey, new FileInputStream(path + "CryptoP2P\\d.txt"), new FileOutputStream(path + "CryptoP2P\\de.txt"));
           */
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
