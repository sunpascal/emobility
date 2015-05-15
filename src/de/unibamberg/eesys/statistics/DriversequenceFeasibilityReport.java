package de.unibamberg.eesys.statistics;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;

public class DriversequenceFeasibilityReport extends Statistic {

	public static final String TAG = "DriversequenceFeasibilityReport";

	public DriversequenceFeasibilityReport() {

	}

	/**
	 * @param dbReturnValue
	 *            has to be a int[] with the length of three. [0] = number of
	 *            feasible drives; [1] = number of drives that are feasible with
	 *            100% battery; [2] = number of not feasible drives
	 * @param chart
	 *            Reference to the PieChart in GUI.
	 */
	@Override
	public void drawGraph(Object dbReturnValue,
			@SuppressWarnings("rawtypes") Chart chart)
			throws StatisticsException {
		try {
			PieData data = createPieGraphData(dbReturnValue, "dataSetName");
			drawGraph(data, (PieChart) chart);
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
	public PieData createPieGraphData(Object dbReturnValue, String dataSetname)
			throws InvalidStatisticDataException {
		// converting data to correct type and format.
		int[] dbValues = null;
		if (dbReturnValue instanceof int[]) {
			dbValues = (int[]) dbReturnValue;

			// Names:
			ArrayList<String> arrListString = new ArrayList<String>();

			// Information for DataSets
			String[] dataSetLable = new String[] { "driveable",
					"driveable with 100%", "not drivable" };
			int[] dataSetColor = new int[] { this.colorPiePart1,
					this.colorPiePart2, this.colorPiePart3 };

			// Number of Values > 0;
			int valuesGreaterZero = 0;

			List<Integer> usedColor = new ArrayList<Integer>();

			// Values: This Construct is used to hide empty sets and their
			// lables
			ArrayList<Entry> arrList = new ArrayList<Entry>();
			try {
				for (int i = 0; i < dbValues.length; i++) {
					if ((dbValues[i]) > 0) {
						valuesGreaterZero++;
						arrList.add(new Entry(dbValues[i], i));
						arrListString.add(dataSetLable[i]);
						usedColor.add(dataSetColor[i]);
					}
				}
			} catch (Exception e) {
				throw new InvalidStatisticDataException(
						"Data do not have the correct format.");
			}

			if (valuesGreaterZero > 0) {
				PieDataSet pieSet = new PieDataSet(arrList, "");
				pieSet.setSliceSpace(10f);
				pieSet.setColors(convertIntegers(usedColor));
				PieData data = new PieData(arrListString, pieSet);
				return data;
			} else {
				//TODO
				// no chartdata availible
				throw new InvalidStatisticDataException("no data");
			}

		} else {
			Log.e(TAG, "drawGraph: object is not instance of int[]");
			throw new InvalidStatisticDataException(
					"drawGraph: object is not instance of int[]");
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
	private void drawGraph(PieData data, PieChart chart)
			throws StatisticsChartException {
		try {

			// Chart Settings:
			chart.setDescription("");
			chart.setTouchEnabled(true);
			chart.setBackgroundColor(this.backroundColor);
			chart.setHoleColor(this.backroundColor);
			chart.setUsePercentValues(true);
			chart.setDrawLegend(false);

			chart.animateXY(1000, 1000);

			chart.setData(data);
		} catch (Exception e) {
			throw new StatisticsChartException(e.getMessage(), e.getCause());
		}
	}

	/**
	 * help method to convert a List of Integers to an int[]
	 * 
	 * @param integers
	 *            List of Integers
	 * @return Int[]
	 */
	public static int[] convertIntegers(List<Integer> integers) {
		int[] ret = new int[integers.size()];
		Iterator<Integer> iterator = integers.iterator();
		for (int i = 0; i < ret.length; i++) {
			ret[i] = iterator.next().intValue();
		}
		return ret;
	}
}
