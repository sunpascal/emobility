package de.unibamberg.eesys.projekt.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
* The Table_SequenceType class is used to define the name and columns
* of the database table. Further it defines the functionality to
* create, upgrade and downgrade this database table.
* This functionality is used by the DBInitializer class.
* 
* @author Stefan
* @version 1.0
*
*/
public class Table_SequenceType {

	// table name
	public static final String TABLE_NAME = "SequenceType";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;
		
	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + Table_SequenceType.TABLE_NAME + "( " + Table_SequenceType.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Table_SequenceType.COLUMN_NAME + " TEXT, " + Table_SequenceType.COLUMN_DESCRIPTION + "  TEXT );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_SequenceType.TABLE_NAME + ";";

 	/**
	 * Creates the database table "SequenceType" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( Table_SequenceType.TABLE_CREATE );
		
		// insert initial data
		bulkInsert(db);
	}

	/**
	 * Upgrades the database table "SequenceType" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_SequenceType.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_SequenceType.TABLE_CREATE );
		
		// insert initial data
		bulkInsert(db);
	}

	/**
	 * Downgrades the database table "SequenceType" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_SequenceType.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_SequenceType.TABLE_CREATE );
		
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
		ContentValues args;
		List<ContentValues> cvBulk = new ArrayList<ContentValues>();

		args = new ContentValues();
        args.put(Table_SequenceType.COLUMN_NAME, "DRIVE");
        args.put(Table_SequenceType.COLUMN_DESCRIPTION, "Eine Fahrt mit dem Fahrzeug.");
        cvBulk.add(args);

		args = new ContentValues();
        args.put(Table_SequenceType.COLUMN_NAME, "CHARGE");
        args.put(Table_SequenceType.COLUMN_DESCRIPTION, "Ein Ladevorgang.");
        cvBulk.add(args);

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
}