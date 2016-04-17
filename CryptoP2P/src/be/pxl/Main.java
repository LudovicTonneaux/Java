package be.pxl;

import java.util.Timer;
import java.util.TimerTask;

public class Main {
    /**
     * Created by Samy Coenen on 01/03/2016.
     */
    public static void main(String[] args) {
       /* Thread serverThread = new Thread(new Server());
        serverThread.start();
        Timer timer1 = new Timer();
//the timertask is only for testing
        TimerTask task1 = new TimerTask() {
            @Override
            public void run() {
                System.out.println("ok");
                Thread clientThread = new Thread(new Client());
                clientThread.start();
            }
        };
        timer1.schedule(task1, 1000,1000);

*/
        RSA.GenerateKeys();
    }
}
