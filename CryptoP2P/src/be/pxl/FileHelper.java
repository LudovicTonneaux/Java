package be.pxl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by Buzz Lightyear on 21/05/2016.
 */
public class FileHelper {

    // inputArray is the content in bytes
    public static void StoreFile(byte[] inputArray, String filePath) {
        File file = new File(filePath);
        try {
            FileOutputStream fop = new FileOutputStream(file);
            fop.write(inputArray);
            fop.flush();
            fop.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
