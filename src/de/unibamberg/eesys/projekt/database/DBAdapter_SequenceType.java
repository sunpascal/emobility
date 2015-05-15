package de.unibamberg.eesys.projekt.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Database Adapter to provide the SQL related methods for
 * operating on the database table "SequenceType".
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DBAdapter_SequenceType {
	public static final String ROW_ID = Table_SequenceType.COLUMN_ID;
	public static final String NAME = Table_SequenceType.COLUMN_NAME;
	public static final String DESCRIPTION = Table_SequenceType.COLUMN_DESCRIPTION;
	
    private static final String TABLE_SEQTYPE = Table_SequenceType.TABLE_NAME;

    private SQLiteDatabase mDb;

    /**
     * Constructor - reference the provided DB
     * 
     * @param dbAdapterDB
     * 		the database opened by the DBInitializer
     */
    public DBAdapter_SequenceType(SQLiteDatabase dbAdapterDB) {
        this.mDb = dbAdapterDB;
    }
    
    /**
     * Create a new SequenceType. If the SequenceType is successfully created return the new
     * rowId for that SequenceType, otherwise return a -1 to indicate failure.
     * 
     * @param name
     * @param description
     * @return rowId or -1 if failed
     */
    public long createSequenceType(String name, String description) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(DESCRIPTION, description);
        return this.mDb.insert(TABLE_SEQTYPE, null, args);
    }

    /**
     * Delete the SequenceType with the given rowId
     * 
     * @param rowId
     * @return true if deleted, false otherwise
     */
    public boolean deleteSequenceType(long rowId) {

        return this.mDb.delete(TABLE_SEQTYPE, ROW_ID + "=" + rowId, null) > 0;
    }

    /**
     * Return a Cursor over the list of all SequenceTypes in the table
     * 
     * @return Cursor over all SequenceTypes
     */
    public Cursor getAllSequenceTypes() {

        return this.mDb.query(TABLE_SEQTYPE,
        		new String[] {ROW_ID, NAME, DESCRIPTION},
        		null, null, null, null, null);
    }

    /**
     * Return a Cursor positioned at the SequenceType that matches the given rowId
     * @param rowId
     * @return Cursor positioned to matching SequenceType (if found) or empty cursor
     */
    public Cursor getSequenceType(long rowId) {

        Cursor mCursor =

        this.mDb.query(true, TABLE_SEQTYPE, 
        		new String[] {ROW_ID, NAME, DESCRIPTION},
        		ROW_ID + "=" + rowId, null, null, null, null, null);

        return mCursor;
    }

    /**
     * Return a Cursor positioned at the SequenceType that matches the given name.
     * @param name
     * @return Cursor positioned to matching SequenceType, if found
     */
    public Cursor getSequenceType(String name) {

        Cursor mCursor =

        this.mDb.query(true, TABLE_SEQTYPE, 
        		new String[] {ROW_ID, NAME, DESCRIPTION},
        		NAME + "='" + name + "'", null, null, null, null, null);
        if (mCursor != null) {
            mCursor.moveToFirst();
        }
        return mCursor;
    }

    /**
     * Update the SequenceType.
     * 
     * @param rowId
     * @param name
     * @param description
     * @return true if the note was successfully updated, false otherwise
     */
    public boolean updateSequenceType(long rowId, String name, String description) {
        ContentValues args = new ContentValues();
        args.put(NAME, name);
        args.put(DESCRIPTION, description);

        return this.mDb.update(TABLE_SEQTYPE, args, ROW_ID + "=" + rowId, null) >0; 
    }

}