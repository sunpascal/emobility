package de.unibamberg.eesys.projekt;

public class NoTripsException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7186332170343202002L;

	public NoTripsException() {
    }

    public NoTripsException(String message) {
        super (message);
    }

    public NoTripsException(Throwable cause) {
        super (cause);
    }

    public NoTripsException(String message, Throwable cause) {
        super (message, cause);
    }
}
