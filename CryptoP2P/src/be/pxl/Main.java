package be.pxl;

public class Main {
    /**
     * Created by Samy Coenen on 01/03/2016.
     */
    public static void main(String[] args) {
        ServerLocalHost s1 = new ServerLocalHost(8888);
        new Thread(s1).start();
        Server s2 = new Server(13501);
        new Thread(s2).start();


        /*
         Scanner scan1 = new Scanner(System.in);
        String ip = scan1.next();
        Client.Send("KEYREQEUST",ip,scan1.nextInt());
  */
    }
}
