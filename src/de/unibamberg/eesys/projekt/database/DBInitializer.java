package de.unibamberg.eesys.projekt.database;

import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import de.unibamberg.eesys.projekt.AppContext;

/**
 * The Database Initializer provides the functionality to
 * create, update or downgrade the database (during the app start)
 * and establishes/closes the connection to the database.
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBInitializer {


	public static final String DATABASE_NAME = "eCarSimulation.db";
	public static final int DATABASE_VERSION = 22;
	
    private final AppContext context; 
    private DatabaseHelper DBHelper;
    protected SQLiteDatabase db;
    

    /**
     * Constructor
     * 
     * @param ctx
     */
    public DBInitializer(AppContext ctx)
    {
        this.context = ctx;
        this.DBHelper = new DatabaseHelper(this.context);
    }
	
    /**
     * Helper class to provide create, update or downgrade functionality
     * using the SQLiteOpenHelper class.
     * 
     * @author Stefan
     * @version 1.0
     *
     */
    public class DatabaseHelper extends SQLiteOpenHelper {

    	public DatabaseHelper(AppContext context) {
    		super(context, DATABASE_NAME, null, DATABASE_VERSION);
    	}

    	@Override
    	public void onCreate(SQLiteDatabase db) {
    		Table_Address.onCreate(db);
    		Table_Battery.onCreate(db);
    		Table_ChargeSequence.onCreate(db);
    		
    		Table_GeoCoordinate.onCreate(db);  
    		Table_ChargingType.onCreate(db);
    		Table_ChargingStation.onCreate(db, context);
    		
    		Table_Country.onCreate(db);
    		Table_DriveSequence.onCreate(db);
    		Table_eCar.onCreate(db);
    		
    		Table_SequenceType.onCreate(db);
    		Table_VehicleType.onCreate(db);
    		Table_WayPoint.onCreate(db);
    	}

    	@Override
    	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		Table_Address.onUpgrade(db, oldVersion, newVersion);
    		Table_Battery.onUpgrade(db, oldVersion, newVersion);
    		Table_ChargeSequence.onUpgrade(db, oldVersion, newVersion);
    		Table_ChargingStation.onUpgrade(db, oldVersion, newVersion);
    		Table_ChargingType.onUpgrade(db, oldVersion, newVersion);
    		Table_Country.onUpgrade(db, oldVersion, newVersion);
    		Table_DriveSequence.onUpgrade(db, oldVersion, newVersion);
    		Table_eCar.onUpgrade(db, oldVersion, newVersion);
    		Table_GeoCoordinate.onUpgrade(db, oldVersion, newVersion);
    		Table_SequenceType.onUpgrade(db, oldVersion, newVersion);
    		Table_VehicleType.onUpgrade(db, oldVersion, newVersion);
    		Table_WayPoint.onUpgrade(db, oldVersion, newVersion);
    	}

    	@Override
    	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    		Table_Address.onDowngrade(db, oldVersion, newVersion);
    		Table_Battery.onDowngrade(db, oldVersion, newVersion);
    		Table_ChargeSequence.onDowngrade(db, oldVersion, newVersion);
    		Table_ChargingStation.onDowngrade(db, oldVersion, newVersion);
    		Table_ChargingType.onDowngrade(db, oldVersion, newVersion);
    		Table_Country.onDowngrade(db, oldVersion, newVersion);
    		Table_DriveSequence.onDowngrade(db, oldVersion, newVersion);
    		Table_eCar.onDowngrade(db, oldVersion, newVersion);
    		Table_GeoCoordinate.onDowngrade(db, oldVersion, newVersion);
    		Table_SequenceType.onDowngrade(db, oldVersion, newVersion);
    		Table_VehicleType.onDowngrade(db, oldVersion, newVersion);
    		Table_WayPoint.onDowngrade(db, oldVersion, newVersion);
    	}
    }
    
    /**
     * Opens the database. If it cannot be opened, try to create a new
     * instance of the database. If it cannot be created, throw an exception to
     * signal the failure
     * 
     * @return this (self reference, allowing this to be chained in an
     *         initialization call)
     * @throws SQLException
     *             if the database could be neither opened nor created
     */
    public DBInitializer open() throws SQLException 
    {
        this.db = this.DBHelper.getWritableDatabase();
        return this;
    }

    /**
     * close the db 
     * return type: void
     */
    public void close() 
    {
        this.DBHelper.close();
    }
}