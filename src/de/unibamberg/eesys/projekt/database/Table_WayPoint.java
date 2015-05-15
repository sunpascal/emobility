package de.unibamberg.eesys.projekt.database;

import android.database.sqlite.SQLiteDatabase;

/**
* The Table_WayPoint class is used to define the name and columns
* of the database table. Further it defines the functionality to
* create, upgrade and downgrade this database table.
* This functionality is used by the DBInitializer class.
* 
* @author Stefan
* @version 1.0
*
*/
public class Table_WayPoint {

	// table name
	public static final String TABLE_NAME = "WayPoint";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_DRIVE_SEQ_ID = "driveSequence_ID";
	public static final String COLUMN_GEO_ID = "geo_ID";
	public static final String COLUMN_VELOCITY = "velocity";
	public static final String COLUMN_ACCELERATION = "acceleration";
	public static final String COLUMN_TIMESTAMP = "timestamp";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;

	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + Table_WayPoint.TABLE_NAME + "( " + Table_WayPoint.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Table_WayPoint.COLUMN_DRIVE_SEQ_ID + " INTEGER, " + Table_WayPoint.COLUMN_GEO_ID + "  INTEGER, " + Table_WayPoint.COLUMN_VELOCITY + "  REAL, " + Table_WayPoint.COLUMN_ACCELERATION + "  REAL, " + Table_WayPoint.COLUMN_TIMESTAMP + "  INTEGER );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_WayPoint.TABLE_NAME + ";";

 	/**
	 * Creates the database table "WayPoint" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( Table_WayPoint.TABLE_CREATE );
	}

	/**
	 * Upgrades the database table "WayPoint" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_WayPoint.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_WayPoint.TABLE_CREATE );
	}

	/**
	 * Downgrades the database table "WayPoint" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_WayPoint.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_WayPoint.TABLE_CREATE );
	}
}