package de.unibamberg.eesys.projekt.database;

import android.database.sqlite.SQLiteDatabase;

/**
* The Table_Country class is used to define the name and columns
* of the database table. Further it defines the functionality to
* create, upgrade and downgrade this database table.
* This functionality is used by the DBInitializer class.
* 
* @author Stefan
* @version 1.0
*
*/
public class Table_Country {

	// table name
	public static final String TABLE_NAME = "Country";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;
	
	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + Table_Country.TABLE_NAME + "( " + Table_Country.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Table_Country.COLUMN_NAME + "  TEXT );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_Country.TABLE_NAME + ";";

 	/**
	 * Creates the database table "Country" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( Table_Country.TABLE_CREATE );
	}

	/**
	 * Upgrades the database table "Country" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_Country.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_Country.TABLE_CREATE );
	}

	 
	/**
	 * Downgrades the database table "Country" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_Country.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_Country.TABLE_CREATE );
	}
}