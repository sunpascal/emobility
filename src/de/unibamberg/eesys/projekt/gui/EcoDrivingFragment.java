package de.unibamberg.eesys.projekt.gui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
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
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
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

	public static final String TAG = "EcoDrivingFragment";
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
		rootView = inflater.inflate(R.layout.fragment_ecodriving, container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
		// CODE FOR DRIVER'S LOG TABLE // 
		
		dBImplementation = new DBImplementation(appContext);

		List<DriveSequence> listDriveSequences = new ArrayList<DriveSequence>();
		try {
			listDriveSequences = dBImplementation.getDriveSequences(true);
		} catch (DatabaseException e) {
			Toast.makeText(appContext, "An unexpected error has occurred.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		for (final DriveSequence d : listDriveSequences) {
			
			// Todo: calculate avergage consumption on trip basis, not vehicle basis
			double kWhPerKm = appContext.getEcar().getVehicleType().getEnergyConsumption_perKM();
			double kWhPer100Km = kWhPerKm * 100;
		
			TableLayout tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout1);
			TextView t1 = new TextView(rootView.getContext());
			t1.setText(d.getTimeStartFormatted());
			
			TextView t2 = new TextView(rootView.getContext());
			t2.setText(appContext.round(kWhPer100Km) + " kWh");
			
			TextView t3 = new TextView(rootView.getContext());
			if (kWhPer100Km > 20 )  { // todo: use personal goal
				t3.setText("☹" + " kWh");
				t3.setTextColor(Color.RED);
			}
			else {
				t3.setText("☺");
				t3.setTextColor(Color.GREEN);
			}
			
			TableRow row = new TableRow(rootView.getContext()); 
			row.addView(t1);
			row.addView(t2);
			row.addView(t3);			
			row.setPadding(0, 3, 0, 3);
			
			row.setOnClickListener(new OnClickListener() {
	            @Override
	             public void onClick(View v) {
	            	loadDriveGraphFragment(d);
	             }   
			});
			
			tableLayout.addView(row);
			
		}
	        
	    
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
	
	
}	

	
