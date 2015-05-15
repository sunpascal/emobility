package de.unibamberg.eesys.statistics;

/**
 * @author robert
 * This Exception is used for Exception in the Statistic component / Workspace
 * it has the known subexceptions StaticsChartException and InvalidChartException
 *
 */
public class StatisticsException extends Exception {
	private static final long serialVersionUID = 7958948051452599152L;

	public StatisticsException(String message)
	{
		super(message);
	}
	
	public StatisticsException(String message, Throwable cause)
	{
		super(message,cause);
	}
}
