package de.unibamberg.eesys.projekt.gui.fragment;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
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
import de.unibamberg.eesys.projekt.gui.FragmentFolder.MODE;
import de.unibamberg.eesys.statistics.BatterySocsReport;
import de.unibamberg.eesys.statistics.Statistic;
import de.unibamberg.eesys.statistics.StatisticsException;

/**
 * 
 * @author Robert
 * 
 */
public class EcoDrivingTableFragment extends Fragment {

	AppContext appContext;
	private FragmentActivity myContext;
	private TableLayout tableLayout;
	
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
	
	/** Id of the select ListViewItem. */
	private int containerId;
	
	/** Position of the List Element. */
	int position;		
	
	public EcoDrivingTableFragment() {
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
		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout1);
		tableLayout.setColumnShrinkable(1, false);
		tableLayout.setColumnShrinkable(2, true);
		
		tableLayout.setColumnStretchable(0, true);
		
		// header row
		addHeaderRow();
		
		// main data
		
		List<DriveSequence> listDriveSequences = new ArrayList<DriveSequence>();
		try {
			listDriveSequences = appContext.getDb().getDriveSequences(true);
		} catch (DatabaseException e) {
			Toast.makeText(appContext, "An unexpected error has occurred.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		for (final DriveSequence d : listDriveSequences) {
			
			double kWhPer100Km = d.calcAveragekWhPer100Km();
		
			TextView t1 = new TextView(rootView.getContext());
			t1.setText(d.getTimeStartFormatted());
			
			TextView t2 = new TextView(rootView.getContext());
			t2.setText(AppContext.round(kWhPer100Km, 0) + " kWh");
			
			TextView t3 = new TextView(rootView.getContext());
			// todo: this use current ecar type... 
			t3.setText(Math.round(d.calcPersonalRange(appContext.getEcar().getVehicleType().getBatteryCapacity())) + " km");
			
			TextView t4 = new TextView(rootView.getContext());
			if (kWhPer100Km > 20 )  { // todo: use personal goal
				t4.setText(":(");		// ☹
				t4.setTextColor(Color.RED);
			}
			else {
				t4.setText("☺");
				t4.setTextColor(Color.GREEN);
			}
			
			TableRow row = new TableRow(rootView.getContext()); 
			row.addView(t1);
			row.addView(t2);
			row.addView(t3);
			row.addView(t4);	
			row.setPadding(0, 3, 0, 3);
			
			t1.setOnClickListener(new OnClickListener() {
	            @Override
	             public void onClick(View v) {
	            	loadDriveGraphFragment(d);
	             }   
			});
			
			t2.setOnClickListener(new OnClickListener() {
	            @Override
	             public void onClick(View v) {
	            	loadSpeedFragment(d);
	             }   
			});	
			
			t3.setOnClickListener(new OnClickListener() {
	            @Override
	             public void onClick(View v) {
	            	loadAccelerationFragment(d);
	             }   
			});
			
			t4.setOnClickListener(new OnClickListener() {
	            @Override
	             public void onClick(View v) {
	            	loadDriveGraphFragment(d);
	             }   
			});			
			
			tableLayout.addView(row);
		}
	    
	    return rootView;	
	    
	}
	

	private void addHeaderRow() {
		TableRow headerRow = new TableRow(rootView.getContext());
		TextView t1 = new TextView(rootView.getContext());
		t1.setText("Date");
		TextView t2 = new TextView(rootView.getContext());
		t2.setText("Consumption ");				// todo:  "(per 100km)"
		TextView t3 = new TextView(rootView.getContext());
		t3.setText("Personal Range");		
		TextView t4 = new TextView(rootView.getContext());
		t4.setText("");
		
		headerRow.addView(t1);
		headerRow.addView(t2);
		headerRow.addView(t3);
		headerRow.addView(t4);	
		headerRow.setPadding(0, 3, 0, 3);	
		headerRow.setShowDividers(1);
		
		tableLayout.addView(headerRow);
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
		
		EcoDrivingFeedbackTeqniqueFragment fragment = (EcoDrivingFeedbackTeqniqueFragment) appContext.getFragmentFolder().getFirstTab();
		fragment.setTrip(ds);
		
		containerId = ((ViewGroup) getView().getParent()).getId();

		fragment.setArguments(getActivity().getIntent().getExtras());
		
		fragment.setArguments(getActivity().getIntent().getExtras());
		Bundle args = new Bundle();
		args.putInt(fragment.toString(), 0);
		fragment.setArguments(args);

		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}	
	
	private void loadSpeedFragment(DriveSequence ds) {

		fragmentManager = getFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		
		AnalysisSpeedFragment fragment = (AnalysisSpeedFragment) appContext.getFragmentFolder().getAnalysisSpeedTab();
		fragment.setTrip(ds);
		
		containerId = ((ViewGroup) getView().getParent()).getId();

		fragment.setArguments(getActivity().getIntent().getExtras());
		
		fragment.setArguments(getActivity().getIntent().getExtras());
		Bundle args = new Bundle();
		args.putInt(fragment.toString(), 0);
		fragment.setArguments(args);

		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}		
	
	private void loadAccelerationFragment(DriveSequence ds) {

		fragmentManager = getFragmentManager();
		fragmentTransaction = fragmentManager.beginTransaction();
		
		AnalysisAccelerationFragment fragment = (AnalysisAccelerationFragment) appContext.getFragmentFolder().getAnalysisAccelerationTab();
		fragment.setTrip(ds);
		
		containerId = ((ViewGroup) getView().getParent()).getId();

		fragment.setArguments(getActivity().getIntent().getExtras());
		
		fragment.setArguments(getActivity().getIntent().getExtras());
		Bundle args = new Bundle();
		args.putInt(fragment.toString(), 0);
		fragment.setArguments(args);

		getFragmentManager().beginTransaction()
				.replace(R.id.content_frame, fragment).commit();
	}		
	
}	

	
