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

    public static void Send(String filePath, String ip) {

        try {
            String msg = "Iam waiting for message";
            Socket s = new Socket(ip, 13501);
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            try (DataOutputStream d = new DataOutputStream(out)) {
                File file = new File(filePath);
                d.writeUTF(file.getName());
                Files.copy(file.toPath(), d);
            }
            OutputStream os = s.getOutputStream();
            os.write(msg.getBytes());
            os.flush();
            os.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }

    public static void Send(String message, String ip, int socket) {

        try {
            String msg = "Iam waiting for message";
            Socket s = new Socket(ip, socket);
            BufferedOutputStream out = new BufferedOutputStream(s.getOutputStream());
            try (DataOutputStream d = new DataOutputStream(out)) {
                d.writeUTF(message);
            }
            OutputStream os = s.getOutputStream();
            os.write(msg.getBytes());
            os.flush();
            os.close();
            s.close();
        } catch (Exception e) {
            System.out.println(e.getCause());
        }
    }
}
