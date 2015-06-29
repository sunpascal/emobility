package de.unibamberg.eesys.statistics;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.graphics.Color;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Legend;
import com.github.mikephil.charting.utils.Legend.LegendForm;
/**
 * this class contains the logic for formating and creating the Average ConsumtionReport
 * @author robert
 */
public class SpeedReport extends Statistic {

	public SpeedReport() {

	}

	/* (non-Javadoc)
	 * @see de.unibamberg.eesys.statistics.Statistic#drawGraph(java.lang.Object, com.github.mikephil.charting.charts.Chart)
	 */
	@Override
	public void drawGraph(Object dbReturnValue, @SuppressWarnings("rawtypes") Chart chart)
			throws StatisticsException {
		try {
			LineData graphData = extractLineGraphData(dbReturnValue,
					"timestamp", "soc", "State of Charge");
			this.drawLineGraph(graphData, (LineChart) chart);// draws the graph on GUI.
		} catch (StatisticsException e) {
			throw new StatisticsException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * this method is used to convert and format the data for the graph.
	 * 
	 * @param dbReturnValue - the rawdata from DB
	 * @param xValsName - names of x-axisValues
	 * @param yValsName - names of y-axisValues
	 * @param dataSetName - name of the Set of Data used in the graph.
	 * @return converted reportdata, they are used to fill the graph.
	 * @throws InvalidStatisticDataException is thrown, if the data are corrupt or are not in the correct format.
	 */
	public LineData extractLineGraphData(Object dbReturnValue,
			String xValsName, String yValsName, String dataSetName)
			throws InvalidStatisticDataException {

		try {
			// converting data to correct type and format.
			@SuppressWarnings("unchecked")
			List<Map<String, Object>> reportData = (List<Map<String, Object>>) dbReturnValue;
			int numberOfListEntries = reportData.size();
			String[] xVals = new String[numberOfListEntries];
			ArrayList<Entry> yVals_list = new ArrayList<Entry>();

			for (int i = 0; i < reportData.size(); i++) {
				@SuppressWarnings("rawtypes")
				Map map = reportData.get(i);

				// xVals: Converting TimeStamps to Stringlables.
				long timestamp = (Long) map.get(xValsName);
				java.sql.Timestamp time = new java.sql.Timestamp(timestamp);
				@SuppressWarnings("deprecation")
				String format = String.valueOf(time.getDate()) + "."
						+ String.valueOf(time.getMonth() + 1);
				xVals[i] = format;

				// yVals:
				double soc = (Double) map.get(yValsName);

				yVals_list.add(new Entry((float) soc, i));
			}
			//creating dataset 
			LineDataSet lineDataSet = new LineDataSet(yVals_list, dataSetName);
			lineDataSet.setColor(this.lineColor);
			ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
			dataSets.add(lineDataSet);
			LineData data = new LineData(xVals, dataSets);
			return data;
		} catch (Exception e) {
			throw new InvalidStatisticDataException(e.getMessage(),
					e.getCause());
		}

	}

	/**
	 * This Method formats the Graph
	 * @param data formated graphdata
	 * @param chart reference to the GUI Object.
	 * @throws StatisticsChartException thrown if creating and formating the chart fails.
	 */
	public void drawLineGraph(LineData data, LineChart chart)
			throws StatisticsChartException {
		try {
			
			// setting Format.
			chart.setUnit(" %");
			chart.setDrawUnitsInChart(true);

			// if enabled, the chart will always start at zero on the y-axis
			chart.setStartAtZero(false);

			// no description text
			chart.setDescription("");
			chart.setNoDataTextDescription("You need to provide data for the chart.");

			// enable value highlighting
			chart.setHighlightEnabled(true);

			// enable touch gestures
			chart.setTouchEnabled(true);

			// Helps, that the Graph is drawn without a click in the Grid!
			chart.setActivated(true);

			// Disables the Description
			chart.setDescription("");
			
			// enable scaling and dragging
			chart.setDragEnabled(true);
			chart.setScaleEnabled(true);
			chart.setDrawGridBackground(false);
			chart.setDrawVerticalGrid(false);
			chart.setDrawHorizontalGrid(false);

			// set Backgroundcolor
			chart.setBackgroundColor(this.backroundColor);

			// if disabled, scaling can be done on x- and y-axis separately
			chart.setPinchZoom(true);

			// set an alternative background color
			chart.setBackgroundColor(this.backroundColor);

			// create a dataset and give it a type
			LineDataSet set1 = data.getDataSetByIndex(0);
			
			// Setting of visual settings.
			set1.setColor(ColorTemplate.getHoloBlue());
			set1.setCircleColor(ColorTemplate.getHoloBlue());
			set1.setLineWidth(2f);
			set1.setCircleSize(4f);
			set1.setFillAlpha(65);
			set1.setFillColor(ColorTemplate.getHoloBlue());
			set1.setHighLightColor(Color.rgb(244, 117, 117));

			ArrayList<LineDataSet> dataSets = new ArrayList<LineDataSet>();
			dataSets.add(set1); // add the datasets

			// add data
			chart.setData(data);
			
			//animationTest
			chart.animateX(1500);
			

			// get the legend (only possible after setting data)
			Legend l = chart.getLegend();

			// modify the legend ...
			l.setForm(LegendForm.LINE);
			l.setTextColor(Color.WHITE);
		} catch (Exception e) {
			throw new StatisticsChartException(e.getMessage(), e.getCause());
		}
	}

}
