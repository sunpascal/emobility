package de.unibamberg.eesys.projekt.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

/**
* The Table_VehicleType class is used to define the name and columns
* of the database table. Further it defines the functionality to
* create, upgrade and downgrade this database table.
* This functionality is used by the DBInitializer class.
* 
* @author Stefan
* @version 1.0
*
*/
public class Table_VehicleType {

	// table name
	public static final String TABLE_NAME = "VehicleType";

	// table columns
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_NAME = "name";
	public static final String COLUMN_DESCRIPTION = "description";
	public static final String COLUMN_BATTERY_CAPACITY = "batteryCapacity";
	public static final String COLUMN_ENERGY_CONSUMPTION_PER_KM = "energyConsumption_perKM";
	public static final String COLUMN_RECUPERATION_EFFICIENCY = "recuperationEfficiency";
	public static final String COLUMN_MASS = "mass";
	public static final String COLUMN_FRONT_AREA = "frontArea";
	public static final String COLUMN_PRICE = "price";
	
	public static final String FULL_ID = TABLE_NAME + "." + COLUMN_ID;
	
	// create and drop statements
	private static final String TABLE_CREATE = "CREATE TABLE " + 
												Table_VehicleType.TABLE_NAME + "( " + 
												Table_VehicleType.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + 
												Table_VehicleType.COLUMN_NAME + " TEXT, " +
												Table_VehicleType.COLUMN_DESCRIPTION + "  TEXT, " + 
												Table_VehicleType.COLUMN_BATTERY_CAPACITY + "  REAL, " + 
												Table_VehicleType.COLUMN_ENERGY_CONSUMPTION_PER_KM + "  REAL, " + 
												Table_VehicleType.COLUMN_RECUPERATION_EFFICIENCY + "  REAL, " + 
												Table_VehicleType.COLUMN_MASS + "  REAL, " + 
												Table_VehicleType.COLUMN_FRONT_AREA + "  REAL, " +
												Table_VehicleType.COLUMN_PRICE + " TEXT );";
	private static final String TABLE_DROP = "DROP TABLE IF EXISTS " + Table_VehicleType.TABLE_NAME + ";";
	
 	/**
	 * Creates the database table "VehicleType" during the application start.
	 * Called when the database is created for the first time.
	 * Used by DBInitializer.
	 *  
	 * @param db: SQLiteDatabase object
	 */
	public static void onCreate( final SQLiteDatabase db ) {
		db.execSQL( Table_VehicleType.TABLE_CREATE );
		
		// insert initial data
		bulkInsert(db);	
	}

	/**
	 * Upgrades the database table "VehicleType" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onUpgrade( final SQLiteDatabase db, final int oldVersion, final int newVersion ) {
		// the only upgrade option is to delete the old database...
		db.execSQL( Table_VehicleType.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_VehicleType.TABLE_CREATE );
		
		// insert initial data
		bulkInsert(db);
	}

	/**
	 * Downgrades the database table "VehicleType" during the application start.
	 * Used by DBInitializer.
	 * 
	 * @param db: SQLiteDatabase object
	 * @param oldVersion: number (int) of the old database version
	 * @param newVersion: number (int) of the new database version
	 */
	public static void onDowngrade( SQLiteDatabase db, int oldVersion, int newVersion ) {
		// the only downgrade option is to delete the old database...
		db.execSQL( Table_VehicleType.TABLE_DROP );

		// ... and to create a new one
		db.execSQL( Table_VehicleType.TABLE_CREATE );
		
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

        //Important: The first VehicleType is used to create the initial eCar.
		//Consider required changes in bulkInsert (Table_Battery)!
		args = new ContentValues();
        args.put(Table_VehicleType.COLUMN_NAME, "BMW i3");
        args.put(Table_VehicleType.COLUMN_DESCRIPTION, "Freude am Fahren.");
        args.put(Table_VehicleType.COLUMN_BATTERY_CAPACITY, 18.8);
        args.put(Table_VehicleType.COLUMN_ENERGY_CONSUMPTION_PER_KM, 0.129);
        args.put(Table_VehicleType.COLUMN_RECUPERATION_EFFICIENCY, 0.5);
        args.put(Table_VehicleType.COLUMN_MASS, 1195);
        args.put(Table_VehicleType.COLUMN_FRONT_AREA, 2.7966);
        args.put(Table_VehicleType.COLUMN_PRICE, "34950€");
        cvBulk.add(args);

		args = new ContentValues();
        args.put(Table_VehicleType.COLUMN_NAME, "VW eUP!");
        args.put(Table_VehicleType.COLUMN_DESCRIPTION, "Das Auto.");
        args.put(Table_VehicleType.COLUMN_BATTERY_CAPACITY, 18.7);
        args.put(Table_VehicleType.COLUMN_ENERGY_CONSUMPTION_PER_KM, 0.117);
        args.put(Table_VehicleType.COLUMN_RECUPERATION_EFFICIENCY, 0.5);
        args.put(Table_VehicleType.COLUMN_MASS, 1214);
        args.put(Table_VehicleType.COLUMN_FRONT_AREA, 2.4272);
        args.put(Table_VehicleType.COLUMN_PRICE, "26900€");
        cvBulk.add(args);

		args = new ContentValues();
        args.put(Table_VehicleType.COLUMN_NAME, "Smart ED");
        args.put(Table_VehicleType.COLUMN_DESCRIPTION, "open your mind.");
        args.put(Table_VehicleType.COLUMN_BATTERY_CAPACITY, 17.6);
        args.put(Table_VehicleType.COLUMN_ENERGY_CONSUMPTION_PER_KM, 0.151);
        args.put(Table_VehicleType.COLUMN_RECUPERATION_EFFICIENCY, 0.5);
        args.put(Table_VehicleType.COLUMN_MASS, 975);
        args.put(Table_VehicleType.COLUMN_FRONT_AREA, 2.4492);
        args.put(Table_VehicleType.COLUMN_PRICE, "19610€");
        cvBulk.add(args);
        
//        ToDo: complete parameters
		args = new ContentValues();
        args.put(Table_VehicleType.COLUMN_NAME, "Tesla Model S (85 kWh)");
        args.put(Table_VehicleType.COLUMN_DESCRIPTION, "");
        args.put(Table_VehicleType.COLUMN_BATTERY_CAPACITY, 85);
        args.put(Table_VehicleType.COLUMN_ENERGY_CONSUMPTION_PER_KM, 0.181);
        args.put(Table_VehicleType.COLUMN_RECUPERATION_EFFICIENCY, 0.5);
        args.put(Table_VehicleType.COLUMN_MASS, 900);
        args.put(Table_VehicleType.COLUMN_FRONT_AREA, 2.4492);
        args.put(Table_VehicleType.COLUMN_PRICE, "90100€");
        cvBulk.add(args);

		args = new ContentValues();
        args.put(Table_VehicleType.COLUMN_NAME, "Tesla Model S (70 kWh)");
        args.put(Table_VehicleType.COLUMN_DESCRIPTION, "");
        args.put(Table_VehicleType.COLUMN_BATTERY_CAPACITY, 70);
        args.put(Table_VehicleType.COLUMN_ENERGY_CONSUMPTION_PER_KM, 0.181);
        args.put(Table_VehicleType.COLUMN_RECUPERATION_EFFICIENCY, 0.5);
        args.put(Table_VehicleType.COLUMN_MASS, 2108);
        args.put(Table_VehicleType.COLUMN_FRONT_AREA, 2.4492);
        args.put(Table_VehicleType.COLUMN_PRICE, "79500€");
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