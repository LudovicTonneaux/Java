package be.pxl;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Base64;

public class Main {
    /**
     * Created by Samy Coenen on 01/03/2016.
     */
    public static void main(String[] args) {
        Server s1 = new Server(8888);
        new Thread(s1).start();
        Server s2 = new Server(13501);
        new Thread(s2).start();
    }
}
