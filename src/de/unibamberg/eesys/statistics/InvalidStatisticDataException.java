package de.unibamberg.eesys.statistics;

/**
 * @author robert
 * This Exception is used, if the Data of the Statistic is invalid oder out of format.
 *
 */
public class InvalidStatisticDataException extends StatisticsException {
	private static final long serialVersionUID = 6995395307022979942L;

	public InvalidStatisticDataException(String message)
	{
		super(message);
	}
	
	public InvalidStatisticDataException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
