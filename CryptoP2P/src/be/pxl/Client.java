package be.pxl;

import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by Buzz Lightyear on 29/03/2016.
 */
public class Client implements Runnable {
    @Override
    public void run() {
        /* Socket sock = new Socket("127.0.0.1", 13501);
        byte[] mybytearray = new byte[1024];
        String filePath = new File("").getAbsolutePath();
        filePath.concat("s.txt");
        File file = new File(filePath);
        System.out.println(file.exists());
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream(filePath);
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        sock.close();*/
        try {


            String msg = "Iam waiting for message";
            Socket s = new Socket("localhost", 8888);
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