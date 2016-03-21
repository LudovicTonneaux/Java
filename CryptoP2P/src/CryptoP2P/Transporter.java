package CryptoP2P;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by samy on 21-3-16.
 */
public class Transporter {

    private int socketnr = 13501;

    Transporter() {

    }

    Transporter(int socketnr) {
        this.socketnr = socketnr;
    }

    public void SetupServer() throws IOException {

        ServerSocket servsock = new ServerSocket(socketnr);
        File myFile = new File("s.pdf");
        while (true) {
            Socket sock = servsock.accept();
            byte[] mybytearray = new byte[(int) myFile.length()];
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(myFile));
            bis.read(mybytearray, 0, mybytearray.length);
            OutputStream os = sock.getOutputStream();
            os.write(mybytearray, 0, mybytearray.length);
            os.flush();
            sock.close();
        }
    }

    public void SetupClient() throws IOException {
        Socket sock = new Socket("127.0.0.1", 13501);
        byte[] mybytearray = new byte[1024];
        InputStream is = sock.getInputStream();
        FileOutputStream fos = new FileOutputStream("s.pdf");
        BufferedOutputStream bos = new BufferedOutputStream(fos);
        int bytesRead = is.read(mybytearray, 0, mybytearray.length);
        bos.write(mybytearray, 0, bytesRead);
        bos.close();
        sock.close();
    }

}
