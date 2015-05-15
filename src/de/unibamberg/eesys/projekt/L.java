package de.unibamberg.eesys.projekt;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Calendar;

import android.os.Environment;
import android.util.Log;

/**
 * Class for Logging functionalities and logging methods
 * @author Julia
 *
 */
public class L {
	static String TAG = "--";
	static boolean logToFile = true;
	static private File logFile;

	public static void writeFile(String string) {

		String state = Environment.getExternalStorageState();
		if (!Environment.MEDIA_MOUNTED.equals(state)) {
			Log.e("--", "cannot log - sdcard notwritable not writable");
			return;
		}

		File root = Environment.getExternalStorageDirectory();

		// Log.v("--", "Writing file...");
		String filename = "aaaa-test.txt";
		FileOutputStream outputStream;
		String date = java.text.DateFormat.getTimeInstance().format(
				Calendar.getInstance().getTime());
		string = date + " " + string + "\n";

		try {
			boolean append = true;
			if (logFile == null) {
				logFile = new File(root, filename);
				if (logFile.exists())
					logFile.delete(); // start with fresh file
			}
			outputStream = new FileOutputStream(new File(root, filename),
					append);
			outputStream.write(string.getBytes());
			outputStream.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void d(String tag, String txt) {
		Log.d(tag, txt);
	}

	private static String whoCalledMe() {
		String callingClass = "";
		String callingMethod = "";
		String callingLine = "";
		try {
			throw new Exception("Who called me?");
		} catch (Exception e) {
			callingClass = e.getStackTrace()[2].getClassName();
			String t = e.getStackTrace()[2].getClass().getName();
			int lastDot = callingClass.lastIndexOf(".");
			// only keep actual classname, w/o package
			callingClass = callingClass.substring(lastDot + 1,
					callingClass.length());
			callingMethod = e.getStackTrace()[2].getMethodName();
			callingLine = e.getStackTrace()[2].getLineNumber() + "";
		}
		String whoCalledMe = callingClass + "." + callingMethod + "():"
				+ callingLine;
		return TAG + whoCalledMe;
	}

	public static void d(String txt) {

		Log.d(whoCalledMe(), txt);
		if (logToFile)
			writeFile(txt);
	}

	public static void v(String txt) {
		Log.v(whoCalledMe(), txt);
		if (logToFile)
			writeFile(txt);
	}

	public static void e(String txt) {
		Log.e(whoCalledMe(), txt);
		if (logToFile)
			writeFile(txt);
	}

	public static void e(String tag, String txt) {
		Log.e(tag, txt);
	}

	public static void i(String txt) {
		Log.i(whoCalledMe(), txt);
		if (logToFile)
			writeFile(txt);
	}

	public static void i(String tag, String txt) {
		Log.i(whoCalledMe(), tag);
		if (logToFile)
			writeFile(txt);
	}

	public static void w(String txt) {
		Log.w(whoCalledMe(), txt);
		if (logToFile)
			writeFile(txt);
	}

	public static void wtf(String txt) {
		Log.wtf(whoCalledMe(), txt);
		if (logToFile)
			writeFile(txt);
	}

}
