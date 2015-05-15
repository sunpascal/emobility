package de.unibamberg.eesys.statistics;
/**
 * @author robert
 * This Exception is used, if formating of the Chart or something with presenting the chart went wrong.
 *
 */
public class StatisticsChartException extends StatisticsException {
	private static final long serialVersionUID = -2283121568234534559L;

	public StatisticsChartException(String message)
	{
		super(message);
	}
	
	public StatisticsChartException(String message, Throwable cause)
	{
		super(message,cause);
	}

}
