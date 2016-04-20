package be.pxl;

import javax.crypto.Cipher;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import static sun.security.x509.CertificateAlgorithmId.ALGORITHM;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class RSA {

    private boolean keysCreated=false;

    public  void GenerateKeys() {
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
            saveToFile("public.key", pub.getModulus(),
                    pub.getPublicExponent());
            saveToFile("private.key", priv.getModulus(),
                    priv.getPrivateExponent());
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }

    private  void saveToFile(String fileName, BigInteger mod, BigInteger exp) {
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
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

    private  PublicKey readPubKeyFromFile(String keyFileName) throws IOException {
        InputStream in = RSA.class.getResourceAsStream(keyFileName);
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
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

    private PrivateKey readPrivKeyFromFile(String keyFileName) throws IOException {
        InputStream in = RSA.class.getResourceAsStream(keyFileName);
        ObjectInputStream oin =
                new ObjectInputStream(new BufferedInputStream(in));
        try {
            BigInteger m = (BigInteger) oin.readObject();
            BigInteger e = (BigInteger) oin.readObject();
            RSAPublicKeySpec keySpec = new RSAPublicKeySpec(m, e);
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
    public byte[] Encrypt(byte[] data, String keyPath, KeyType keyType) throws Exception {
        if (!keysCreated){
            GenerateKeys();
        }
        byte[] encryptedData = null;
        try {
        PublicKey pubKey = readPubKeyFromFile("/public.key");
            // get an RSA cipher object and print the provider
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            // encrypt the plain data using the public key and return it
            encryptedData= cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
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
    public byte[] Decrypt(byte[] data, String keyPath, KeyType keyType) throws Exception {
        if (!keysCreated){
            GenerateKeys();
        }
        byte[] decryptedData = null;
        try {
            PrivateKey privKey = readPrivKeyFromFile("/public.key");
            // get an RSA cipher object and print the provider
            final Cipher cipher = Cipher.getInstance(ALGORITHM);
            // decrypt the text using the private key
            cipher.init(Cipher.DECRYPT_MODE, privKey);
            decryptedData = cipher.doFinal(data);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return decryptedData;
    }

    private enum KeyType {
        PUBLIC, PRIVATE
    }

}
