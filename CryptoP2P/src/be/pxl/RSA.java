package be.pxl;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class RSA {

    private static boolean keysCreated = false;

    public static void GenerateKeys(String path) {
        try {

            KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
            kpg.initialize(2048);
            KeyPair kp = kpg.genKeyPair();
            Key publicKey = kp.getPublic();
            Key privateKey = kp.getPrivate();
            KeyFactory fact = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec pub = fact.getKeySpec(publicKey,
                    RSAPublicKeySpec.class);
            RSAPrivateKeySpec priv = fact.getKeySpec(privateKey,
                    RSAPrivateKeySpec.class);
            saveToFile(path+"public.key", pub.getModulus(),
                    pub.getPublicExponent());
            saveToFile(path+"private.key", priv.getModulus(),
                    priv.getPrivateExponent());
            keysCreated = true;
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private static void saveToFile(String filePath, BigInteger mod, BigInteger exp) {
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
            oout.writeObject(mod);
            oout.writeObject(exp);
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

    private static PublicKey readPubKeyFromFile(String keyFileName) throws IOException {
        FileInputStream in = new FileInputStream(keyFileName);
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePublic(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    private static PrivateKey readPrivKeyFromFile(String keyFileName) throws IOException {
        FileInputStream in = new FileInputStream(keyFileName);
        ObjectInputStream oin = new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(m, e);
            KeyFactory fact = KeyFactory.getInstance("RSA");
            return fact.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Spurious serialisation error", e);
        } finally {
            oin.close();
        }
    }

    /**
     * Encrypt the plain data.
     *
     * @param data : original plain data
     * @param keyPath  :The key path
     * @param keyType  :The type of key
     * @return Encrypted text
     * @throws java.lang.Exception
     */
    public static byte[] Encrypt(byte[] data, String keyPath, KeyType keyType) throws Exception {
        byte[] encryptedData = null;
        try {
            PublicKey pubKey = readPubKeyFromFile(keyPath);
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            // encrypt the plain data using the public key and return it
            encryptedData= cipher.doFinal(data);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
        return encryptedData;
    }

    /**
     * Decrypt text using private key.
     *
     * @param data :encrypted text
     * @param keyPath  :The key path
     * @param keyType  :The type of key
     * @return plain text
     * @throws java.lang.Exception
     */
    public static byte[] Decrypt(byte[] data, String keyPath, KeyType keyType) throws Exception {
        byte[] decryptedData = null;
        try {
            PrivateKey privKey = readPrivKeyFromFile(keyPath);
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance("RSA");
            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            decryptedData = cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decryptedData;
    }

    public static boolean GetAreKeysGenerated(){
        return keysCreated;
    }

    public enum KeyType {
        PUBLIC, PRIVATE
    }

}
