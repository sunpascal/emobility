package de.unibamberg.eesys.projekt.gui;

import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.Chart;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;
import de.unibamberg.eesys.statistics.DriveDistancesReport;
import de.unibamberg.eesys.statistics.Statistic;
import de.unibamberg.eesys.statistics.StatisticsException;

/**
 * 
 * @author Robert
 * 
 */
public class DriveDistancesFragment extends Fragment {
	public static final String TAG = "DriveDistancesFragment";
	public static final String ARG_STATUS = "status";

	/**
	 * Was used for a generic approach, in which all graphs are used in a single
	 * fragment, this wasn´t possible cause of GUI problems.
	 */
	public static final int DRIVE_DISTANCES = 2837492;

	@SuppressWarnings("rawtypes")
	// is needed for generic approach in Statistics Workspace
	Chart mChart;
	Object mChartData;
	Statistic mStatistic;

	public DriveDistancesFragment() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Fragment#onCreateView(android.view.LayoutInflater,
	 * android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_drive_distances,
				container, false);

		// gets the current context, this is used to show the Chart and
		// Exceptionhandling.
		ReportProvider reportProv = new ReportProvider(rootView.getContext());

		// gets the Parent of the fragment, which will include the graph.
		RelativeLayout parent = (RelativeLayout) rootView
				.findViewById(R.id.drvDistances);

		// creates a new chart and adds it to the view.
		this.mChart = new BarChart(getActivity());
		parent.addView(mChart);

		mStatistic = new DriveDistancesReport();

		mChartData = reportProv.execute(DRIVE_DISTANCES);
		mChart.setVisibility(View.VISIBLE);
		return rootView;
	}

	/**
	 * @author Robert AsyncTask, which is used to get Reportdata from DB.
	 *         <Input-Type, Update-Type, Return-Type>
	 */
	private class ReportProvider extends AsyncTask<Integer, Void, Object> {
		private Context mContext;
		private DBImplementation db;
		Object DbReturnValue;

		/**
		 * @param context
		 *            is used to get the DB-Reference, which is placed in
		 *            Appcontext.
		 */
		public ReportProvider(Context context) {
			mContext = context;
			AppContext appContext = (AppContext) context
					.getApplicationContext();
			db = appContext.getDb();
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Object doInBackground(Integer... params) {
			// this method is processed in a async Task and calls the needed
			// report in DB.
			// in case of an exception it shows the message in a toast
			try {
				System.out.println(DRIVE_DISTANCES);
				DbReturnValue = db.getReport_DriveSeqDistances(true);
			} catch (DatabaseException e) {
				Toast.makeText(mContext.getApplicationContext(),
						e.getMessage(), Toast.LENGTH_SHORT).show();
				e.printStackTrace();
			}
			return DbReturnValue;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Object result) {
			// this code is processed in the main Thread, it calls the Statistic
			// class and returns the gathered data from DB
			Log.v(TAG, "onPostExecute");
			recivedData(DbReturnValue);
		}
	}

	/**
	 * @param DbReturnValue
	 *            : gathered Reportdata from DB.
	 * 
	 *            this Method calls the Statistics class.
	 */
	public void recivedData(Object DbReturnValue) {
		try {
			mStatistic.drawGraph(DbReturnValue, mChart);
		} catch (StatisticsException e) {
			Toast.makeText(this.getActivity(), e.getMessage(),
					Toast.LENGTH_SHORT).show();
		}
	}
}
