package de.unibamberg.eesys.projekt.gui.fragment;

import android.support.v4.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;
import de.unibamberg.eesys.statistics.BatterySocsReport;
import de.unibamberg.eesys.statistics.Statistic;
import de.unibamberg.eesys.statistics.StatisticsException;

/**
 * 
 * @author Robert
 * 
 */
public class AnalysisBatteryLevelFragment extends Fragment {

	public static final String TAG = "StateOfChargeFragment";
	public static final String ARG_STATUS = "status";

	public static final int BATTERY_SOCS = 3223478;

	@SuppressWarnings("rawtypes")
	// is needed for generic approach in Statistics Workspace
	Chart mChart;
	Object mChartData;
	Statistic mStatistic;

	public AnalysisBatteryLevelFragment() {
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

		View rootView = inflater.inflate(R.layout.fragment_state_charge,
				container, false);

		// gets the current context, this is used to show the Chart and
		// Exceptionhandling.
		ReportProvider reportProv = new ReportProvider(rootView.getContext());

		// gets the Parent of the fragment, which will include the graph.
		RelativeLayout parent = (RelativeLayout) rootView
				.findViewById(R.id.soc);

		// creates a new chart and adds it to the view.
		this.mChart = new LineChart(getActivity());
		parent.addView(mChart);
		mStatistic = new BatterySocsReport();

		// starts an AsyncTask, to get the data from DB
		mChartData = reportProv.execute(BATTERY_SOCS, 50);
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
			int methodParam = 0;
			if (params.length > 1) {
				methodParam = params[1];// number of values that will be shown.
				
			}
			try {
				System.out.println(BATTERY_SOCS);
				DbReturnValue = db.getReport_BatterySOCs(methodParam, true);
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
