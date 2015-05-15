package de.unibamberg.eesys.statistics;

import java.util.ArrayList;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.XLabels;
import com.github.mikephil.charting.utils.XLabels.XLabelPosition;

public class DriveDistancesReport extends Statistic {

	public DriveDistancesReport() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see de.unibamberg.eesys.statistics.Statistic#drawGraph(java.lang.Object,
	 * com.github.mikephil.charting.charts.Chart)
	 */
	@Override
	public void drawGraph(Object dbReturnValue,
			@SuppressWarnings("rawtypes") Chart chart)
			throws StatisticsException {
		try {
			BarData data = createBarGraphData(dbReturnValue, "dataSetName");
			drawBarChart(data, (BarChart) chart);
		} catch (StatisticsException e) {
			throw new StatisticsException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * this method is used to convert and format the data for the graph.
	 * 
	 * @param dbReturnValue
	 *            - the rawdata from DB
	 * @param dataSetname
	 *            - name of the Set of Data used in the graph.
	 * @return converted reportdata, they are used to fill the graph.
	 * @throws InvalidStatisticDataException
	 *             is thrown, if the data are corrupt or are not in the correct
	 *             format.
	 * 
	 */
	public BarData createBarGraphData(Object dbReturnValue, String dataSetname)
			throws InvalidStatisticDataException {
		try {

			// converting data to correct type and format.
			int[][] dbData = (int[][]) dbReturnValue;

			// yVals
			ArrayList<BarEntry> yVals = new ArrayList<BarEntry>();
			for (int i = 0; i < dbData.length; i++) {
				BarEntry entr = new BarEntry(dbData[i][1], i);
				yVals.add(entr);
			}

			BarDataSet dataSet = new BarDataSet(yVals, dataSetname);

			// Sets the space between the bars - has no influance to the
			// possible length of lables.
			// dataSet.setBarSpacePercent(1);

			// Sets the color of the Bar.
			dataSet.setColor(this.lineColor);

			ArrayList<BarDataSet> dataSets = new ArrayList<BarDataSet>();
			dataSets.add(dataSet);

			String[] xVals = new String[dbData.length];
			for (int i = 0; i < dbData.length; i++) {

				if (i == dbData.length - 1) {
					// The last column includes all values greater than the
					// column before, so we need a different lable
					xVals[i] = dbData[i - 1][0] + "+";
				} else {
					xVals[i] = "< " + dbData[i][0] + "";
				}

			}
			BarData data = new BarData(xVals, dataSets);
			return data;
		} catch (Exception e) {
			throw new InvalidStatisticDataException(e.getMessage(),
					e.getCause());
		}
	}

	/**
	 * This Method formats the Graph
	 * 
	 * @param data
	 *            formated graphdata
	 * @param chart
	 *            reference to the GUI Object.
	 * @throws StatisticsChartException
	 *             thrown if creating and formating the chart fails.
	 */
	private void drawBarChart(BarData data, BarChart chart)
			throws StatisticsChartException {
		try {
			// enable the drawing of values
			chart.setDrawYValues(true);

			chart.setDrawValueAboveBar(true);

			// if more than 60 entries are displayed in the chart, no values
			// will be
			// drawn
			chart.setMaxVisibleValueCount(60);

			// Helps, that the Graph is drawn without a click in the Grid!
			chart.setActivated(true);

			// Disables the Legend
			chart.setDrawLegend(false);

			// Disables the Description
			chart.setDescription("");

			// disable 3D
			chart.set3DEnabled(false);

			// animation
			chart.animateY(1000);

			// scaling can now only be done on x- and y-axis separately
			chart.setPinchZoom(false);

			// draw shadows for each bar that show the maximum value
			// mChart.setDrawBarShadow(true);

			// chart.setUnit("km"); No Unit needed.

			// mChart.setDrawXLabels(false);

			chart.setDrawGridBackground(false);
			chart.setBackgroundColor(this.backroundColor);
			chart.setDrawHorizontalGrid(true);
			chart.setDrawVerticalGrid(false);
			// mChart.setDrawYLabels(false);

			// sets the text size of the values inside the chart
			chart.setValueTextSize(10f);

			chart.setDrawBorder(false);

			XLabels xl = chart.getXLabels();
			xl.setPosition(XLabelPosition.BOTTOM);
			xl.setCenterXLabelText(true);
			chart.setData(data);
			chart.bringToFront();
			chart.buildLayer();
		} catch (Exception e) {
			throw new StatisticsChartException(e.getMessage(), e.getCause());
		}
	}
}
