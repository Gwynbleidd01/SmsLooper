package app.sms.com.smslooper;


import android.os.Environment;
import android.util.Log;

import java.io.FileOutputStream;


public class File {

    public static String DEFAULT_PATH = Environment.getExternalStorageDirectory()
            .getAbsolutePath();

    public static void FileWriter(String str, String path) {
        FileOutputStream outputStream;

        String date = new TimeClass().getCurrentDateAndTime();

        try {
            outputStream = new FileOutputStream(path, true);
            if (outputStream != null) {

                outputStream.write(date.getBytes());
                outputStream.write(str.getBytes());
                outputStream.write("\n".getBytes());
                outputStream.close();

            }

        } catch (Exception e) {
            LogWindow.logWindow.error("Write to file error" , false);
            Log.i("Error" , e.toString());
        }
    }
}
