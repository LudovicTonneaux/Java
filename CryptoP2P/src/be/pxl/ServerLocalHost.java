package be.pxl;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class ServerLocalHost implements Runnable {
    public static String[] parameters;
    private int socketnr;
    private String path = System.getProperty("user.home") + File.separator + "CRYPTO" + File.separator;// new File("").getAbsolutePath() + "\\";

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

             /*
                We maken de CRYPTO directory en onze RSA keys
                isDirectory checks of de folder bestaat en of het een folder is
                */
            if (!new File(path).exists()) {
                Files.createDirectory(Paths.get(path));
            }
            if (!RSA.GetAreKeysGenerated()) {
                RSA.GenerateKeys(path, "B");
            }

            while (i < 1) {


                Socket mySocket = myServerSocket.accept();
                System.out.println("Connected Successfully to java backend");

                BufferedInputStream in = new BufferedInputStream(mySocket.getInputStream());
                String UTF8 = "utf8";
                int BUFFER_SIZE = 8192;

                BufferedReader br = new BufferedReader(new InputStreamReader(in,
                        UTF8), BUFFER_SIZE);
                //System.out.println(br.readLine());
                parameters = br.readLine().split(";");
                //0 is source ip
                //1 is destination ip
                //2 is file path
                //3 is name

                //doorgegeven string bevat EXIT dus deze thread moet stoppen/niet meer loopen
                if (parameters[0].contains("EXIT")) {
                    System.exit(0);
                }
                System.out.println(parameters[0] + " " + parameters[1] + " " + parameters[2]);


                //ik vraag de public key aan en stuur miin public key ook ineens mee zodat de ontvanger kan zien dat het van mii komt door het signen
                Client.SendWithOtherName("KEYREQUEST", path + "Public_" + System.getProperty("user.name") + ".key", parameters[0], 13501);

                System.out.println("Data Received from GUI");
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
