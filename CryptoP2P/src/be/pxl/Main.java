package be.pxl;

public class Main {
    /**
     * Created by Samy Coenen on 01/03/2016.
     */
    public static void main(String[] args) {
        //Server gets started first because it generates the RSA keys
        Server s2 = new Server(13501);
        new Thread(s2).start();
        ServerLocalHost s1 = new ServerLocalHost(8888);
        new Thread(s1).start();
    }
}
