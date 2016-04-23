package be.pxl;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

public class Main {
    /**
     * Created by Samy Coenen on 01/03/2016.
     */
    public static void main(String[] args) {
       /* Thread serverThread = new Thread(new Server());
        serverThread.start();
        Timer timer1 = new Timer();
//the timertask is only for testing
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("ok");
                Thread clientThread = new Thread(new Client());
                clientThread.start();
            }
        };
        timer1.schedule(task1, 1000,1000);

*/

        RSA.GenerateKeys();
        try {
            byte[] encrDesPass;
            byte[] originalDes;

            String path = new File("").getAbsolutePath() + "\\";
            System.out.println(Hasher.CheckSumSHA256(path + "CryptoP2P\\s.txt"));

            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            SecretKey myDesKey = keyGenerator.generateKey();
            System.out.println(Base64.getEncoder().encodeToString(myDesKey.getEncoded()));

            DES.Encrypt(myDesKey, new FileInputStream(path + "CryptoP2P\\s.txt"), new FileOutputStream(path + "CryptoP2P\\d.txt"));
            encrDesPass = RSA.Encrypt(Base64.getEncoder().encodeToString(myDesKey.getEncoded()).getBytes(), path + "public.key", RSA.KeyType.PUBLIC);
            originalDes = RSA.Decrypt(encrDesPass, path + "private.key", RSA.KeyType.PRIVATE);
            System.out.println("encrypted pass= " + new String(encrDesPass));
            System.out.println("originele pass= " + new String(originalDes));
            DES.Decrypt(myDesKey, new FileInputStream(path + "CryptoP2P\\d.txt"), new FileOutputStream(path + "CryptoP2P\\de.txt"));
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }

    }
}
