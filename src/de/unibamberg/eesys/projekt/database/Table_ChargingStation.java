package de.unibamberg.eesys.projekt.database;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.businessobjects.ChargingStation;
import de.unibamberg.eesys.projekt.businessobjects.ChargingType;
import de.unibamberg.eesys.projekt.businessobjects.GeoCoordinate;

/**
* The Table_ChargingStation class is used to define the name and columns
* of the database table. Further it defines the functionality to
* create, upgrade and downgrade this database table.
* This functionality is used by the DBInitializer class.
* 
* @author Stefan
* @version 1.0
*
*/
public class Table_ChargingStation {
	private static AppContext context; 
	private static ArrayList<ChargingStation> chargeStations;

	// table name
	public static final String TABLE_NAME = "ChargingStation";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CHARGING_TYPE_ID = "chargingType_ID";
	public static final String COLUMN_GEO_ID = "geo_ID";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;
	
	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + Table_ChargingStation.TABLE_NAME + "( " + Table_ChargingStation.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Table_ChargingStation.COLUMN_CHARGING_TYPE_ID + " INTEGER, " + Table_ChargingStation.COLUMN_GEO_ID + "  INTEGER, " + Table_ChargingStation.COLUMN_NAME + "  TEXT, " + Table_ChargingStation.COLUMN_DESCRIPTION + "  TEXT );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_ChargingStation.TABLE_NAME + ";";

 	/**
	 * Creates the database table "ChargingStation" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db, AppContext context ) {
		db.execSQL( Table_ChargingStation.TABLE_CREATE );

		Table_ChargingStation.context = (AppContext) context; 
		chargeStations = loadFromCSV();		
		
		// insert initial data
		bulkInsert(db);
	}	

	/**
	 * Upgrades the database table "ChargingStation" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_ChargingStation.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_ChargingStation.TABLE_CREATE );
		
		// insert initial data
		bulkInsert(db);
	}

	/**
	 * Downgrades the database table "ChargingStation" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_ChargingStation.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_ChargingStation.TABLE_CREATE );
		
		// insert initial data
		bulkInsert(db);
	}
	
	 /**
	 * The bulkInsert is used to store predefined database values
	 * during the database creation process.
	 * 
	 * @param db: SQLiteDatabase object
	 */
	public static void bulkInsert( final SQLiteDatabase db ) {
		
		// Charging Types: 1 = Home Charging (3.5 kWh), 2 = Public Charging (22 kWh), 3 = Tesla Supercharger (120 kWh), 4 = Tesla Supercharger (600 kWh) - just for testing
		
		ContentValues args;
		List<ContentValues> cvBulk = new ArrayList<ContentValues>();

		args = new ContentValues();
        args.put(Table_ChargingStation.COLUMN_CHARGING_TYPE_ID, 2);
        args.put(Table_ChargingStation.COLUMN_GEO_ID, 1);
        args.put(Table_ChargingStation.COLUMN_NAME, "Uni Bamberg - Public Charging 22kWh");
        args.put(Table_ChargingStation.COLUMN_DESCRIPTION, "Fiktive Ladestation Uni Bamberg");
        cvBulk.add(args);
        
		args = new ContentValues();
        args.put(Table_ChargingStation.COLUMN_CHARGING_TYPE_ID, 2);
        args.put(Table_ChargingStation.COLUMN_GEO_ID, 2);
        args.put(Table_ChargingStation.COLUMN_NAME, "RWE Effizienz GmbH");
        args.put(Table_ChargingStation.COLUMN_DESCRIPTION, "RWE E-Mobility Hotline - 96050 Bamberg Geisfelder Straße 4");
        cvBulk.add(args);        
        
		args = new ContentValues();
        args.put(Table_ChargingStation.COLUMN_CHARGING_TYPE_ID, 2);
        args.put(Table_ChargingStation.COLUMN_GEO_ID, 3);
        args.put(Table_ChargingStation.COLUMN_NAME, "Stadtwerke - Public Charging 22kWh");
        args.put(Table_ChargingStation.COLUMN_DESCRIPTION, "In genymotion \bamberg stadtwerke\" suchen");
        cvBulk.add(args);        
        
		args = new ContentValues();
        args.put(Table_ChargingStation.COLUMN_CHARGING_TYPE_ID, 3);
        args.put(Table_ChargingStation.COLUMN_GEO_ID, 4);
        args.put(Table_ChargingStation.COLUMN_NAME, "Bamberg Erba - Tesla 120kWh");
        args.put(Table_ChargingStation.COLUMN_DESCRIPTION, "Fiktive Supercharging Ladestation Uni Bamberg: in genymotion nach \"bamberg erba\" suchen");
        cvBulk.add(args);    
        
		args = new ContentValues();
        args.put(Table_ChargingStation.COLUMN_CHARGING_TYPE_ID, 4);
        args.put(Table_ChargingStation.COLUMN_GEO_ID, 5);
        args.put(Table_ChargingStation.COLUMN_NAME, "Bamberg Hafen - Tesla 600kWh");
        args.put(Table_ChargingStation.COLUMN_DESCRIPTION, "Fiktive 600kWh Ladestation: in genymotion nach \"bamberg hafen\" suchen");
        cvBulk.add(args);         
        
		args = new ContentValues();
        args.put(Table_ChargingStation.COLUMN_CHARGING_TYPE_ID, 1);
        args.put(Table_ChargingStation.COLUMN_GEO_ID, 6);
        args.put(Table_ChargingStation.COLUMN_NAME, "Bamberg Haus - Home Charging 3.5kWh");
        args.put(Table_ChargingStation.COLUMN_DESCRIPTION, "Fiktive Ladestation: in genymotion nach \"bamberg haus\" suchen");
        cvBulk.add(args);                  


//        import prepared but not used at the moment
//		for (ChargingStation c : chargeStations) {
//			args = new ContentValues();
//
//			args.put(Table_ChargingStation.COLUMN_NAME, c.getName());
//			args.put(Table_ChargingStation.COLUMN_DESCRIPTION,
//					c.getDescription());
//
//			DBAdapter_GeoCoordinate dbGeo = new DBAdapter_GeoCoordinate(db);
//			long geoId = dbGeo.createGeoCoordinate(c.getGeoCoordinate()
//					.getLatitude(), c.getGeoCoordinate().getLongitude(), 0);
//			args.put(Table_ChargingStation.COLUMN_GEO_ID, geoId);
//
//			DBAdapter_ChargingType dbChargingType = new DBAdapter_ChargingType(
//					db);
//			Cursor cChargingType = dbChargingType.getAllChargingTypes();
//
//			if (cChargingType != null) {
//				if (cChargingType.moveToFirst()) {
//					args.put(Table_ChargingStation.COLUMN_CHARGING_TYPE_ID,
//							cChargingType.getLong(0));
//				}
//			}
//
//			cvBulk.add(args);
//		}
        
		db.beginTransaction();
		
		try {
			for (ContentValues contentValues : cvBulk) {
				db.insert(TABLE_NAME, null, contentValues);
			}

			//commit the transaction
			// if something went wrong, no commit is done
			db.setTransactionSuccessful();
		}
		finally {
			//end the transaction
			//if there was a commit before --> successful transaction
			//if there was NO commit before --> "automatic" rollback
			db.endTransaction();
//			db.close(); --> not needed here
		}
		
	}

	/**
	 * Method to load a predefined set of charging stations.
	 * CSV import of a ressource file.
	 * 
	 * @return ArrayList of ChargingStation
	 */
	public static ArrayList<ChargingStation> loadFromCSV() {
		ArrayList<ChargingStation> result = new ArrayList<ChargingStation>();

		try {
			InputStream instream = context.getApplicationContext()
					.getResources().getAssets().open("etankstellen.csv");

			// if file is available for reading
			if (instream != null) {
				// prepare the file for reading
				InputStreamReader inputreader = new InputStreamReader(instream);
				BufferedReader buffreader = new BufferedReader(inputreader);
				String line;

				// read every line of the file into the line-variable, on line
				// at the time
				while ((line = buffreader.readLine()) != null) {
					String[] commas = line.split(",");
					if (commas.length >= 3) {
						L.v(commas[0] + "|" + commas[1] + "|" + commas[2]);
						GeoCoordinate coord = new GeoCoordinate(
								Double.parseDouble(commas[0]),
								Double.parseDouble(commas[1]));
						L.v(coord.getLatitude() + "," + coord.getLongitude());
						ChargingStation cs = new ChargingStation();
						cs.setGeoCoordinate(coord);

						String[] minus = commas[2].split("-");
						cs.setName(minus[0]);
						cs.setDescription(minus[1]);

						ChargingType ct = new ChargingType();
						ct.setName("");
						ct.setDescription("");
						ct.setChargingPower(16 * 240);
						cs.setChargingType(ct);

						result.add(cs);
					}
				}
			}
			// close the file 
			instream.close();
		} catch (NumberFormatException e) {
			// log if the myfilename.txt does not exist
			Log.e("--", "Could not parse values from csv: " + e.getMessage());
		} catch (java.io.FileNotFoundException e) {
			// log if the myfilename.txt does not exist
			Log.e("--", "File not found." + e.getMessage());
		} catch (Exception e) {
			Log.e("--",
					"Unknown exception while reading file: " + e.getMessage()
							+ " " + e.getCause());
			e.printStackTrace();
		}

		return result;
	}		
	
}