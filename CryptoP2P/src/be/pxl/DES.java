package be.pxl;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class DES {

    public static void GenerateKeys() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
            SecretKey myDesKey = keyGenerator.generateKey();

        } catch (NoSuchAlgorithmException ex){
            System.out.println(ex.getMessage());
        }
    }

    public static SecretKey openFile(String filePath){
        SecretKey SecretKey=null;
        ObjectInputStream oin = null;
        try {
             oin = new ObjectInputStream(new BufferedInputStream(new FileInputStream(filePath)));
            SecretKey = (SecretKey) oin.readObject();

        } catch (Exception ex){
            System.out.printf(ex.getMessage());
        } finally {
            if (oin!=null){
                try{
                    oin.close();
                }catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }

            }

        }
        return SecretKey;
    }

    private static void saveToFile(String filePath, SecretKey deskey) {
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
            oout.writeObject(deskey);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (oout != null) {
                try {
                    oout.close();
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }

    /**
     *
     * @param password
     * @param sourceFile
     * @param locationFile
     */
    public static void Encrypt(SecretKey password, FileInputStream sourceFile, FileOutputStream locationFile) {
        try {
            EncryptOrDecrypt(password, Cipher.ENCRYPT_MODE, sourceFile, locationFile);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            System.out.println(ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     *
     * @param password
     * @param sourceFile
     * @param locationFile
     */
    public static void Decrypt(SecretKey password, FileInputStream sourceFile, FileOutputStream locationFile) {
        try {
            EncryptOrDecrypt(password, Cipher.DECRYPT_MODE, sourceFile, locationFile);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeySpecException ex) {
            System.out.println(ex.getMessage());
        } catch (NoSuchPaddingException ex) {
            System.out.println(ex.getMessage());
        } catch (InvalidKeyException ex) {
            System.out.println(ex.getMessage());
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    /**
     *
     * @param password
     * @param mode
     * @param is
     * @param os
     * @throws NoSuchAlgorithmException
     * @throws InvalidKeyException
     * @throws InvalidKeySpecException
     * @throws NoSuchPaddingException
     * @throws IOException
     */
    private static void EncryptOrDecrypt(SecretKey password, int mode, InputStream is, OutputStream os) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IOException {
        DESKeySpec dks = new DESKeySpec(password.getEncoded());
        SecretKeyFactory skf = SecretKeyFactory.getInstance("DES");
        SecretKey desKey = skf.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES"); // DES/ECB/PKCS5Padding for SunJCE

        if (mode == Cipher.ENCRYPT_MODE) {
            cipher.init(Cipher.ENCRYPT_MODE, desKey);
            CipherInputStream cis = new CipherInputStream(is, cipher);
            Copy(cis, os);
        } else if (mode == Cipher.DECRYPT_MODE) {
            cipher.init(Cipher.DECRYPT_MODE, desKey);
            CipherOutputStream cos = new CipherOutputStream(os, cipher);
            Copy(is, cos);
        }
    }

    /**
     *
     * @param is
     * @param os
     * @throws IOException
     */
    private static void Copy(InputStream is, OutputStream os) throws IOException {
        byte[] bytes = new byte[64];
        int numBytes;
        while ((numBytes = is.read(bytes)) != -1) {
            os.write(bytes, 0, numBytes);
        }
        os.flush();
        os.close();
        is.close();
    }

}
