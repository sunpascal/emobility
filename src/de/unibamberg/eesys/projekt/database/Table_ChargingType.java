package de.unibamberg.eesys.projekt.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
* The Table_ChargingType class is used to define the name and columns
* of the database table. Further it defines the functionality to
* create, upgrade and downgrade this database table.
* This functionality is used by the DBInitializer class.
* 
* @author Stefan
* @version 1.0
*
*/
public class Table_ChargingType {

	// table name
	public static final String TABLE_NAME = "ChargingType";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_CHARGING_POWER = "chargingPower";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;
	
	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + Table_ChargingType.TABLE_NAME + "( " + Table_ChargingType.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + Table_ChargingType.COLUMN_NAME + " TEXT, " + Table_ChargingType.COLUMN_DESCRIPTION + "  TEXT, " + Table_ChargingType.COLUMN_CHARGING_POWER + "  REAL );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_ChargingType.TABLE_NAME + ";";

 	/**
	 * Creates the database table "ChargingType" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( Table_ChargingType.TABLE_CREATE );

		// insert initial data
		bulkInsert(db);
	}

	/**
	 * Upgrades the database table "ChargingType" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_ChargingType.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_ChargingType.TABLE_CREATE );
		
		// insert initial data
		bulkInsert(db);
	}

	/**
	 * Downgrades the database table "ChargingType" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_ChargingType.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_ChargingType.TABLE_CREATE );
		
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
        args.put(Table_ChargingType.COLUMN_NAME, "Home Charging");
        args.put(Table_ChargingType.COLUMN_DESCRIPTION, "Typische Ladeleistung in privaten Haushalten.");
        args.put(Table_ChargingType.COLUMN_CHARGING_POWER, 3.5);
        cvBulk.add(args);

		args = new ContentValues();
        args.put(Table_ChargingType.COLUMN_NAME, "Public Charging");
        args.put(Table_ChargingType.COLUMN_DESCRIPTION, "Typische Ladeleistung an öffentlichen Stromtankstellen.");
        args.put(Table_ChargingType.COLUMN_CHARGING_POWER, 22);
        cvBulk.add(args);
        
		args = new ContentValues();
        args.put(Table_ChargingType.COLUMN_NAME, "Tesla Supercharger");
        args.put(Table_ChargingType.COLUMN_DESCRIPTION, "Nur für Tesla Fahrzeuge");
        args.put(Table_ChargingType.COLUMN_CHARGING_POWER, 120);
        cvBulk.add(args);
        
		args = new ContentValues();
        args.put(Table_ChargingType.COLUMN_NAME, "SuperSuperCharger (nur für Testzwecke)");
        args.put(Table_ChargingType.COLUMN_DESCRIPTION, "Gibt es nicht wirklich, aber man kann die App damit testen ohne lange warten zu müssen - pro Sekunde wird 1.67 kWh geladen. ");
        args.put(Table_ChargingType.COLUMN_CHARGING_POWER, 600);
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