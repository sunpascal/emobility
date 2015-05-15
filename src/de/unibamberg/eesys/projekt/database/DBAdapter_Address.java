package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "Address".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_Address {
	public static final String ROW_ID = Table_Address.COLUMN_ID;
	public static final String GEO_ID = Table_Address.COLUMN_GEO_ID;
	public static final String COUNTRY_ID = Table_Address.COLUMN_COUNTRY_ID;
	public static final String STREET1 = Table_Address.COLUMN_STREET1;
	public static final String STREET2 = Table_Address.COLUMN_STREET2;
	public static final String NUMBER = Table_Address.COLUMN_NUMBER;
	public static final String ZIP_CODE = Table_Address.COLUMN_ZIP_CODE;
	public static final String CITY = Table_Address.COLUMN_CITY;

    private static final String TABLE_ADDRESS = Table_Address.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_Address(SQLiteDatabase dbAdapterDB) {
      this.mDb = dbAdapterDB;
    }

    /**
     * Create a new Address. If the Address is successfully created return the new
     * rowId for that Address, otherwise return a -1 to indicate failure.
     * 
     * @param geoId
     * @param countryId
     * @param street1
     * @param street2
     * @param number
     * @param zipCode
     * @param city
     * @return rowId or -1 if failed
     */
    public long createAddress(long geoId, long countryId, String street1, String street2,
    		int number, String zipCode, String city) {
        ContentValues args = new ContentValues();
        args.put(GEO_ID, geoId);
        args.put(COUNTRY_ID, countryId);
        args.put(STREET1, street1);
        args.put(STREET2, street2);
        args.put(NUMBER, number);
        args.put(ZIP_CODE, zipCode);
        args.put(CITY, city);
        return this.mDb.insert(TABLE_ADDRESS, null, args);
    }

    /**
     * Delete the Address with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteAddress(long rowId) {

        return this.mDb.delete(TABLE_ADDRESS, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all Addresses in the table
     * 
     * @return Cursor over all Addresses
     */
    public Cursor getAllAddresses() {

        return this.mDb.query(TABLE_ADDRESS,
        		new String[] {ROW_ID, GEO_ID, COUNTRY_ID, STREET1, STREET2, NUMBER, ZIP_CODE, CITY },
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the Address that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching Address (if found) or empty cursor
     */
    public Cursor getAddress(long rowId) {

        Cursor mCursor =

        this.mDb.query(true, TABLE_ADDRESS, 
        		new String[] {ROW_ID, GEO_ID, COUNTRY_ID, STREET1, STREET2, NUMBER, ZIP_CODE, CITY },
        		ROW_ID + "=" + rowId, null, null, null, null, null);

        return mCursor;
    }

    /**
     * Update the Address.
     * 
     * @param rowId
     * @param geoId
     * @param countryId
     * @param street1
     * @param street2
     * @param number
     * @param zipCode
     * @param city
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateAddress(long rowId, long geoId, long countryId, String street1, String street2,
    		int number, String zipCode, String city) {
        ContentValues args = new ContentValues();
        args.put(GEO_ID, geoId);
        args.put(COUNTRY_ID, countryId);
        args.put(STREET1, street1);
        args.put(STREET2, street2);
        args.put(NUMBER, number);
        args.put(ZIP_CODE, zipCode);
        args.put(CITY, city);

        return this.mDb.update(TABLE_ADDRESS, args, ROW_ID + "=" + rowId, null) >0; 
    }

}