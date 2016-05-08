package be.pxl;

import java.io.File;
import java.util.Scanner;

public class Main {
    /**
     * Created by Samy Coenen on 01/03/2016.
     */
    public static void main(String[] args) {
        //ServerLocalHost s1 = new ServerLocalHost(8888);
        //new Thread(s1).start();
        // String path= new File("").getAbsolutePath() + "\\";
        String path= new File("").getAbsolutePath() + "\\";
        Server s2 = new Server(13501);
        //new Thread(s2).start();
        Scanner s1 = new Scanner(System.in);
        String ip = s1.next();
        Client.Send(path + "CryptoP2P\\s.txt", ip);
    }
}
