package be.pxl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class ServerLocalHost implements Runnable {
    public static String[] parameters;
    private int socketnr;
    private String path = new File("").getAbsolutePath() + "\\";

    ServerLocalHost(int socketnr) {
        this.socketnr = socketnr;
    }

    @Override
    public void run() {
        ServerSocket myServerSocket = null;
        try {
            int i = 0;
            System.out.println("Listening in " + socketnr + ", Still Waiting for a connection");
            myServerSocket = new ServerSocket(socketnr);

            while (i < 1) {
                Socket mySocket = myServerSocket.accept();
                System.out.println("Connected Successfully");

                BufferedInputStream in = new BufferedInputStream(mySocket.getInputStream());
                String UTF8 = "utf8";
                int BUFFER_SIZE = 8192;

                BufferedReader br = new BufferedReader(new InputStreamReader(in,
                        UTF8), BUFFER_SIZE);
                System.out.println(br.readLine());
                //parameters = br.readLine().split(";");

                //0 is source ip
                //1 is destination ip
                //2 is file path
                //3 is name

                //doorgegeven string bevat EXIT dus deze thread moet stoppen/niet meer loopen
                // if (parameters[0].contains("EXIT")) {
                //   System.exit(0);
                // }


                //Client.Send("KEYREQUEST", parameters[1], 13501);


                /*while ((incomingString = ) != null) {
                   parameters= incomingString.split(";");
                }*/
                //werkt niet met c#, utf8 eigen implementatie
                /*DataInputStream d = new DataInputStream(in);
                String fileName = d.readUTF();
                new String(d,"UTF-8");
                System.out.println(fileName);
                //als er bestanden moeten verzonden worden stuurt de front-end alle parameters
                String[] dataArray = fileName.split(";");

                //doorgegeven string bevat EXIT dus deze thread moet stoppen/niet meer loopen
                    if(!dataArray[0].contains("EXIT")){
                        i++;
                    } else {
                    */
                System.out.println("Data Received");
                mySocket.close();

            }



        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } catch (Exception ex) {
            ex.printStackTrace();

        } finally {
            if (myServerSocket != null) {
                try {
                    myServerSocket.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }

            }

        }
    }
}
