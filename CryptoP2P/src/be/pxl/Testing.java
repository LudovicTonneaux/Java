package be.pxl;


import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.interfaces.RSAKey;
import java.util.Scanner;


/**
 * Created by Buzz Lightyear on 21/05/2016.
 */
public class Testing {
    public static void main(String[] args) {
        Server s2 = new Server(13501);
        new Thread(s2).start();
        ServerLocalHost s1 = new ServerLocalHost(8888);
        new Thread(s1).start();
        Scanner scan1 = new Scanner(System.in);
        Scanner scan2 = new Scanner(System.in);//scanner voor enkel integers
        System.out.print("Geef het ip:");
        String ip = scan1.nextLine();

        //System.out.print("Geef de poort:");
        //int poort = scan2.nextInt();

        //System.out.print("Geef het bericht:");
        //String bericht = scan1.nextLine();

        //Client.Send("KEYREQEUST", ip,poort);

/*
        try {
            String msg = "Iam waiting for message";
            Socket s = new Socket(ip, poort);
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            try (DataOutputStream d = new DataOutputStream(out)) {
                d.write(bericht.getBytes("UTF-8"));

            }
            OutputStream os = s.getOutputStream();
            os.write(msg.getBytes());
            os.flush();
            os.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
        */

        String path = System.getProperty("user.home") + File.separator + "CRYPTO" + File.separator;
        try {

            if (!new File(path).isDirectory()) {
                Files.createDirectory(Paths.get(path));
            }
Client.Send(path+"test","127.0.0.1");
            /*
            Encryption starts here
            file1 = encrypted file
            file2 = encrypted key
            file3 = signed hash
            A = Person1
            B = Person2
            */
/*
            RSA.GenerateKeys(path, "A");
            RSA.GenerateKeys(path, "B");
            Hasher.CheckSumSHA256(path + "File", path + "hash");
            FileHelper.StoreFile(
                    RSA.Encrypt(
                            RSA.Encrypt(Files.readAllBytes(
                                    Paths.get(path + "Hash")), path + "Private_A.key", RSA.KeyType.PRIVATE),path+"Public_B.key", RSA.KeyType.PUBLIC),path + File.separator + "File_3");

            DES.GenerateKeys(path + "DesKey");
            SecretKey myDesKey = DES.openFile(path + "DesKey");
            DES.Encrypt(myDesKey, new FileInputStream(path + "File"), new FileOutputStream(path + "File_1"));
            FileHelper.StoreFile(RSA.Encrypt((myDesKey.getEncoded()), path + "Public_B.key", RSA.KeyType.PUBLIC), path + "File_2");


            //Decryption starts here


            String path2 = System.getProperty("user.home") + File.separator + "CRYPTODECRYPTED" + File.separator;
            if (!new File(path2).isDirectory()) {
                Files.createDirectory(Paths.get(path2));
            }

            byte[] originalDes = RSA.Decrypt(Files.readAllBytes(Paths.get(path + "File_2")), path + "Private_B.key", RSA.KeyType.PRIVATE);
            myDesKey = new SecretKeySpec(originalDes, 0, originalDes.length, "DES");
            DES.Decrypt(myDesKey, new FileInputStream(path + "File_1"), new FileOutputStream(path2 + "File"));
            FileHelper.StoreFile(
                    RSA.Decrypt(
                            RSA.Decrypt(
                                    Files.readAllBytes(Paths.get(path + "File_3")), path + "Public_A.key", RSA.KeyType.PUBLIC),path+"Private_B.key",RSA.KeyType.PRIVATE), path2 + "Hash");

            File file1 = new File("test1.txt");
            File file2 = new File("test2.txt");
            File file3 = new File("test3.txt");

            boolean compare1and2 = FileUtils.contentEquals(file1, file2);
            boolean compare2and3 = FileUtils.contentEquals(file2, file3);
            boolean compare1and3 = FileUtils.contentEquals(file1, file3);

            System.out.println("Are test1.txt and test2.txt the same? " + compare1and2);
            System.out.println("Are test2.txt and test3.txt the same? " + compare2and3);
            System.out.println("Are test1.txt and test3.txt the same? " + compare1and3);*/
        } catch (IOException ex) {
            System.out.println("couldn't create folder or file");
            ex.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
