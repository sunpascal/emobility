package de.unibamberg.eesys.projekt.database;

import android.database.sqlite.SQLiteDatabase;

/**
* The Table_DriveSequence class is used to define the name and columns
* of the database table. Further it defines the functionality to
* create, upgrade and downgrade this database table.
* This functionality is used by the DBInitializer class.
* 
* @author Stefan
* @version 1.0
*
*/
public class Table_DriveSequence {

	// table name
	public static final String TABLE_NAME = "DriveSequence";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SEQUENCE_TYPE_ID = "sequenceType_ID";
	public static final String COLUMN_VEHICLE_TYPE_ID = "vehicleType_ID";
	public static final String COLUMN_SOC_START = "socStart";
	public static final String COLUMN_SOC_END = "socEnd";
	public static final String COLUMN_TIME_START = "timeStart";
	public static final String COLUMN_TIME_STOP = "timeStop";
	public static final String COLUMN_ACTIVE = "active";
	public static final String COLUMN_COVERED_DISTANCE = "coveredDistance";
	public static final String COLUMN_SUM_CO2 = "sumCO2";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;
	
	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + Table_DriveSequence.TABLE_NAME + "( " + Table_DriveSequence.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Table_DriveSequence.COLUMN_SEQUENCE_TYPE_ID + " INTEGER, " + Table_DriveSequence.COLUMN_VEHICLE_TYPE_ID + " INTEGER, " + Table_DriveSequence.COLUMN_SOC_START + "  REAL, " + Table_DriveSequence.COLUMN_SOC_END + "  REAL, " + Table_DriveSequence.COLUMN_TIME_START + "  INTEGER, " + Table_DriveSequence.COLUMN_TIME_STOP + "  INTEGER, " + Table_DriveSequence.COLUMN_ACTIVE + "  INTEGER, " + Table_DriveSequence.COLUMN_COVERED_DISTANCE + "  REAL, " + Table_DriveSequence.COLUMN_SUM_CO2 + "  REAL );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_DriveSequence.TABLE_NAME + ";";

 	/**
	 * Creates the database table "DriveSequence" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( Table_DriveSequence.TABLE_CREATE );
	}

	/**
	 * Upgrades the database table "DriveSequence" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_DriveSequence.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_DriveSequence.TABLE_CREATE );
	}

	/**
	 * Downgrades the database table "DriveSequence" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_DriveSequence.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_DriveSequence.TABLE_CREATE );
	}
}