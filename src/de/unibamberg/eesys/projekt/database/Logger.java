package de.unibamberg.eesys.projekt.database;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.logging.FileHandler;
import android.util.Log;

/**
 * Simple text file logger for long term error evaluation.
 * Writes logs to a text file in the app home directory.
 * Easy to extend for multiple log files. For writing to external storage prepared.
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class Logger {

    public static  FileHandler logger = null;
    private static String filename = "Log";
        
//    static boolean isExternalStorageAvailable = false;
//    static boolean isExternalStorageWriteable = false; 
//    static String state = Environment.getExternalStorageState();
//
    public static void addRecordToLog(String message) {

//
//        if (Environment.MEDIA_MOUNTED.equals(state)) { 
//            // We can read and write the media 
//            isExternalStorageAvailable = isExternalStorageWriteable = true; 
//        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) { 
//            // We can only read the media 
//            isExternalStorageAvailable = true; 
//            isExternalStorageWriteable = false; 
//        } else { 
//            // Something else is wrong. It may be one of many other states, but all we need 
//            //  to know is we can neither read nor write 
//            isExternalStorageAvailable = isExternalStorageWriteable = false; 
//        }

        File dir = new File("/data/data/de.unibamberg.eesys.projekt");      
//        if (Environment.MEDIA_MOUNTED.equals(state)) {  
            if(!dir.exists()) {
                Log.d("Dir created ", "Dir created ");
                dir.mkdirs();
            }

            File logFile = new File("/data/data/de.unibamberg.eesys.projekt/"+filename+".txt");

            if (!logFile.exists())  {
                try  {
                    Log.d("File created ", "File created ");
                    logFile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try { 
                //BufferedWriter for performance, true to set append to file flag
                BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true)); 

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDateTime = sdf.format(new Date(System.currentTimeMillis()));
                
                buf.write(currentDateTime + ": " + message + "\r\n");
                //buf.append(message);
                buf.newLine();
                buf.flush();
                buf.close();
            } catch (IOException e) {

                e.printStackTrace();
            }
//        }
    }
}

