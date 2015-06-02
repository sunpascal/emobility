package de.unibamberg.eesys.projekt.gui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
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
public class EcoDrivingFragment extends Fragment {

	public static final String TAG = "StateOfChargeFragment";
	public static final String ARG_STATUS = "status";

	public static final int BATTERY_SOCS = 3223478;
	
	AppContext appContext;
	private FragmentActivity myContext;
	
	/** View that displays the fragment. */
	private View rootView;
	
	/** Instance of the SQL database. */
	private DBImplementation dBImplementation;
	
	/** Displays an list */
	private ListView listView;
	/** Is needed for handling the fragment lifecycle. */
	
	private FragmentManager fragmentManager;
	/** Is needed for handling the fragment lifecycle. */
	
	private FragmentTransaction fragmentTransaction;
	/** Fragment for displaying a drive sequence. */
	
	private DriveGraphFragment driveGraphFragment;
	
	/** Id of the select ListViewItem. */
	private int containerId;
	
	/** Position of the List Element. */
	int position;		
	
	
	
	/** needed for consumption chart  */
	@SuppressWarnings("rawtypes")
	// is needed for generic approach in Statistics Workspace
	Chart mChart;
	Object mChartData;
	Statistic mStatistic;	
	
	public EcoDrivingFragment() {
	}
	
	@Override
	public void onAttach(Activity activity) {
	    myContext=(FragmentActivity) activity;
	    super.onAttach(activity);
	}	
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_ecodriving,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
		// CODE FOR DRIVER'S LOG TABLE // 
		
		dBImplementation = new DBImplementation(appContext);
		listView = (ListView) rootView.findViewById(R.id.listView);

		List<DriveSequence> listDriveSequences = new ArrayList<DriveSequence>();
		try {
			listDriveSequences = dBImplementation.getDriveSequences(true);
		} catch (DatabaseException e) {
			Toast.makeText(appContext, "An unexpected error has occurred.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}

		/** Contains the listIndexName that is actually displayed. */
		ArrayAdapter<DriveSequence> objAdapter = new ArrayAdapter<DriveSequence>(
				this.getActivity(), R.layout.list_item,
				listDriveSequences);
		

		L.d("listDriveSequences");

		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				DriveSequence d = (DriveSequence) parent
						.getItemAtPosition(position);
				loadDriveGraphFragment(d);
			}
		});

		View header = getLayoutInflater(savedInstanceState).inflate(R.layout.header_ecodriving, null);
	    listView.addHeaderView(header);
		
		View footer = getLayoutInflater(savedInstanceState).inflate(R.layout.footer_ecodriving, null);
		ArrayList<View> subViews = new ArrayList<View>();
		subViews.add(mChart);
		footer.addChildrenForAccessibility(subViews);
	    listView.addFooterView(footer);
	    
	    listView.setAdapter(objAdapter);
		
		// END CODE FOR DRIVER'S LOG TABLE
	    
	    
	    // START CODE FOR CONSUMPTION CHART // 
	    
		// gets the current context, this is used to show the Chart and
		// Exceptionhandling.
		ReportProvider reportProv = new ReportProvider(rootView.getContext());

		// gets the Parent of the fragment, which will include the graph.
		RelativeLayout parent = (RelativeLayout) footer.findViewById(R.id.soc);

		// creates a new chart and adds it to the view.
		this.mChart = new LineChart(getActivity());
//		parent.addView(mChart);
		mStatistic = new BatterySocsReport();

		// starts an AsyncTask, to get the data from DB
		mChartData = reportProv.execute(BATTERY_SOCS, 50);
		mChart.setVisibility(View.VISIBLE);	    
		
		// END CODE FOR CONSUMPTION CHART 
	    
	    
	    return rootView;	
	    
	}
	

	/**
	 * This method loads an specific DriveGraphFragment that is filled with an
	 * single drive sequence. The position of the clicked ListViewItem is
	 * responsible for selecting the drive sequence number.
	 * 
	 * @param position
	 *            Integer number of the selected ListViewItem index
	 */
	private void loadDriveGraphFragment(DriveSequence ds) {

		fragmentManager = getFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		driveGraphFragment = new DriveGraphFragment(ds);
		containerId = ((ViewGroup) getView().getParent()).getId();

		driveGraphFragment.setArguments(getActivity().getIntent().getExtras());
		Bundle args = new Bundle();
		args.putInt(driveGraphFragment.toString(), position);

		driveGraphFragment.setArguments(args);

		fragmentTransaction.replace(containerId, driveGraphFragment);
		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
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

	
