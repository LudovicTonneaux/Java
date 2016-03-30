package be.pxl;

import javax.crypto.*;
import javax.crypto.spec.DESKeySpec;
import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class DES {
    public static String Encrypt(String password, String text) {
        return null;
    }

    public static void Encrypt(String password, FileInputStream sourceFile, FileOutputStream locationFile) {
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


    private static void EncryptOrDecrypt(String password, int mode, InputStream is, OutputStream os) throws NoSuchAlgorithmException, InvalidKeyException, InvalidKeySpecException, NoSuchPaddingException, IOException {
        DESKeySpec dks = new DESKeySpec(password.getBytes());
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
