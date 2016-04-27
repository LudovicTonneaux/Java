package be.pxl;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Created by Samy Coenen on 29/03/2016.
 */
public class Server implements Runnable {
    private int socketnr;
    private String directory;
    Server() {
        socketnr = 13501;
    }

    Server(int socketnr) {
        this.socketnr = socketnr;
    }

    public void SetDirectory(String path) {
        directory = path;
    }
    @Override
    public void run() {
        try {


       /* ServerSocket servsock = new ServerSocket(socketnr);

        File myFile = new File("C://abc.txt");

        while (true) {
            Socket sock = servsock.accept();
            byte[] mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            sock.close();
           if (myFile.exists())
            System.exit(-1);
        }*/
            System.out.println("Listening in 8888, Still Waiting for a connection");
            ServerSocket myServerSocket = new ServerSocket(8888);
            int i = 0;
            while (i < 1) {
                Socket mySocket = myServerSocket.accept();
                System.out.println("Connected Successfully");

                BufferedInputStream in = new BufferedInputStream(mySocket.getInputStream());
                DataInputStream d = new DataInputStream(in);
                String fileName = d.readUTF();
                Files.copy(d, new File(directory + fileName).toPath());
               /* InputStream ins = mySocket.getInputStream();
                String s = "";
                byte[] b = new byte[10];

                while (ins.read(b) > 0) {
                    s = s + (new String(b));
                    b = new byte[10];
                }
                */

                mySocket.close();
                System.out.println("Message Received:: \n");
                i++;
            }

            myServerSocket.close();

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
        }
}
