package de.unibamberg.eesys.projekt.database;

import android.database.sqlite.SQLiteDatabase;

/**
 * The Table_Address class is used to define the name and columns
 * of the database table. Further it defines the functionality to
 * create, upgrade and downgrade this database table.
 * This functionality is used by the DBInitializer class.
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class Table_Address {

	// table name
	public static final String TABLE_NAME = "Address";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_GEO_ID = "geo_ID";
	public static final String COLUMN_COUNTRY_ID = "country_ID";
	public static final String COLUMN_STREET1 = "street1";
	public static final String COLUMN_STREET2 = "street2";
	public static final String COLUMN_NUMBER = "number";
	public static final String COLUMN_ZIP_CODE = "zipCode";
	public static final String COLUMN_CITY = "city";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;
	
	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + Table_Address.TABLE_NAME + "( " + Table_Address.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Table_Address.COLUMN_GEO_ID + " INTEGER, " + Table_Address.COLUMN_COUNTRY_ID + "  INTEGER, " + Table_Address.COLUMN_STREET1 + "  TEXT, " + Table_Address.COLUMN_STREET2 + "  TEXT, " + Table_Address.COLUMN_NUMBER + "  INTEGER, " + Table_Address.COLUMN_ZIP_CODE + "  TEXT, " + Table_Address.COLUMN_CITY + "  TEXT );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_Address.TABLE_NAME + ";";

	/**
	 * Creates the database table "Address" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( Table_Address.TABLE_CREATE );
	}

	/**
	 * Upgrades the database table "Address" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_Address.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_Address.TABLE_CREATE );
	}

	/**
	 * Downgrades the database table "Address" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_Address.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_Address.TABLE_CREATE );
	}
}