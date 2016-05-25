package be.pxl;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.file.Files;

/**
 * Created by Buzz Lightyear on 29/03/2016.
 */
public class Client {
    /**
     * @param filePath
     * @param ip
     */
    public static void Send(String filePath, String ip) {

        try {

            Socket s = new Socket(ip, 13501);
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            try (DataOutputStream d = new DataOutputStream(out)) {
                File file = new File(filePath);
                d.writeUTF(file.getName());
                Files.copy(file.toPath(), d);
            }
           // OutputStream os = s.getOutputStream();
            //os.write(filePath.getBytes());

            //os.flush();
            //os.close();
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param message
     * @param ip
     * @param socket
     */
    public static void Send(String message, String ip, int socket) {

        try {
            Socket s = new Socket(ip, socket);
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            try (DataOutputStream d = new DataOutputStream(out)) {
                d.writeUTF(message);
            }
            /*OutputStream os = s.getOutputStream();
            os.write(message.getBytes());
            os.flush();
            os.close();*/
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /**
     * @param name
     * @param path
     * @param ip
     * @param socket
     */
    public static void SendWithOtherName(String name,String path, String ip, int socket) {

        try {

            Socket s = new Socket(ip, socket);
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            try (DataOutputStream d = new DataOutputStream(out)) {
                d.writeUTF(name);
                File file = new File(path);
                Files.copy(file.toPath(), d);
            }
            /*OutputStream os = s.getOutputStream();
            os.write(name.getBytes());
            os.flush();
            os.close();*/
            s.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
