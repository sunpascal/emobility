package de.unibamberg.eesys.statistics;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;

/**
 * @author r
 * abstract class for all statistic reports
 *
 */
public abstract class Statistic {
	
	//// central Color Definition
	protected int backroundColor = Color.rgb(168,168,168 );
	protected int textColor = Color.WHITE;
	
	// PieCharColors
	protected int colorPiePart1 =  Color.rgb(80,211,36);    //Green
	protected int colorPiePart2 =  Color.rgb(255, 174, 73); //Orange / Yellow
	protected int colorPiePart3 =  Color.rgb(219, 73, 73);  //Red
	
	// LineChartColor
	protected int lineColor =  Color.rgb(138, 197, 255);

	/**
	 * This Method is the main interface to the statistics component.
	 * @param dbReturnValue - the rawdata from DB
	 * @param chart  reference to the GUI Object.
	 * @throws StatisticsException thrown if creating and formating the chart, or its data fails.
	 */
	public abstract void drawGraph(Object dbReturnValue, @SuppressWarnings("rawtypes") Chart chart) throws StatisticsException ;
}