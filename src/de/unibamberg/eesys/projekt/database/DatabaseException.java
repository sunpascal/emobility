package de.unibamberg.eesys.projekt.database;

/**
 * The DatabaseException class provides a custom Exception object
 * for all types of exceptions in the database package.
 * DatabaseExceptions are thrown because data processing errors
 * which are related to the database. This includes logical errors,
 * SQLExceptions or SQLiteExceptions.
 * 
 * @author Stefan
 * @version 1.0
 *
 */
public class DatabaseException extends Exception {

	private static final long serialVersionUID = 1L;

	public DatabaseException() {
    }

    public DatabaseException(String message) {
        super (message);
    }

    public DatabaseException(Throwable cause) {
        super (cause);
    }

    public DatabaseException(String message, Throwable cause) {
        super (message, cause);
    }
}
