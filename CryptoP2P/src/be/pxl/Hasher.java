package be.pxl;

import java.io.*;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * Created by Samy on 29/03/2016.
 */

public class Hasher {
    /**
     *
     * @param fileLocation
     * @return
     * @throws Exception
     */
    static public String CheckSumSHA256(String fileLocation) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        FileInputStream fis = new FileInputStream(fileLocation);
        byte[] dataBytes = new byte[1024];
        int nread = 0;
        while ((nread = fis.read(dataBytes)) != -1) {
            md.update(dataBytes, 0, nread);
        }
        byte[] mdbytes = md.digest();

        //convert the byte to hex format m
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < mdbytes.length; i++) {
            hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        }
        return hexString.toString();
    }

    /**
     *
     * @param fileLocation
     * @param hashOutputLocation
     * @throws Exception
     */
    static public void CheckSumSHA256(String fileLocation,String hashOutputLocation) throws Exception {
        saveToFile(hashOutputLocation,CheckSumSHA256(fileLocation));
    }


    /**
     *
     * @param filePath
     * @param hash
     */
    private static void saveToFile(String filePath, String hash) {
        ObjectOutputStream oout = null;
        try {
            oout = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(filePath)));
            oout.writeObject(hash);
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
}
