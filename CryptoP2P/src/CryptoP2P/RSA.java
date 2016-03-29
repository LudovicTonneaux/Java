package CryptoP2P;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class RSA {

    public static void GenerateKeys() {
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

    public static void saveToFile(String fileName, BigInteger mod, BigInteger exp) {
        ObjectOutputStream oout;
        boolean ooutMade = false;
        try {
            oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(fileName)));
            ooutMade = true;
            oout.writeObject(mod);
            oout.writeObject(exp);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        } finally {
            if (ooutMade == true)
                oout.close();
        }
    }
}
