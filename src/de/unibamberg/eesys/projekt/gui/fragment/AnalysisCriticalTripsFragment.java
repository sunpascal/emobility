	package de.unibamberg.eesys.projekt.gui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;

public class AnalysisCriticalTripsFragment extends Fragment {

	/** View that displays the fragment. */
	private View rootView;
	/** Instance of the SQL database. */
	
	private TableLayout tableLayout;
	
	private DBImplementation dBImplementation;
	/** Displays an list */
	private ListView listView;
	/** Is needed for handling the fragment lifecycle. */
	private FragmentManager fragmentManager;
	/** Is needed for handling the fragment lifecycle. */
	private FragmentTransaction fragmentTransaction;
	/** Fragment for displaying a drive sequence. */
	private AnalysisTripMapFragment driveGraphFragment;
	/** Id of the select ListViewItem. */
	private int containerId;
	/** Position of the List Element. */
	int position;

	/**
	 * Fragment for displaying an list of all recorded drive sequences.
	 * 
	 * @author Matthias
	 * 
	 * */
	public AnalysisCriticalTripsFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_driverslog, container,
				false);
		AppContext appContext = (AppContext) getActivity().getApplicationContext();
		
		tableLayout = (TableLayout) rootView.findViewById(R.id.tableLayout1);
		
		// header row
		addHeaderRow();
		
		dBImplementation = new DBImplementation(appContext);

		List<DriveSequence> listDriveSequences = new ArrayList<DriveSequence>();
		try {
			listDriveSequences = dBImplementation.getDriveSequences(true);
		} catch (DatabaseException e) {
			Toast.makeText(appContext, "An unexpected error has occurred.", Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		
		for (final DriveSequence d : listDriveSequences) {
		
			TextView t1 = new TextView(rootView.getContext());
			t1.setText(d.getTimeStartFormatted());
			
			TextView t3 = new TextView(rootView.getContext());
			t3.setText(appContext.round(d.getCoveredDistanceInKm(), 0) + " km");
			
			TextView t4 = new TextView(rootView.getContext());
			t4.setText(appContext.round(d.calcSumkWh()) + " kWh");
			t4.setTextColor(Color.GREEN);			
			
			TableRow row = new TableRow(rootView.getContext()); 
			row.addView(t1);
			row.addView(t3);
			row.addView(t4);
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
	
	private void addHeaderRow() {
		TableRow headerRow = new TableRow(rootView.getContext());
		TextView t1 = new TextView(rootView.getContext());
		t1.setText("Date");
		TextView t2 = new TextView(rootView.getContext());
		t2.setText("Distance");
		TextView t3 = new TextView(rootView.getContext());
		t3.setText("Energy");
		
		headerRow.addView(t1);
		headerRow.addView(t2);
		headerRow.addView(t3);			
		headerRow.setPadding(0, 3, 0, 3);	
		
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
		driveGraphFragment = new AnalysisTripMapFragment(ds);
		containerId = ((ViewGroup) getView().getParent()).getId();

		driveGraphFragment.setArguments(getActivity().getIntent().getExtras());
		Bundle args = new Bundle();
		args.putInt(driveGraphFragment.toString(), position);

		driveGraphFragment.setArguments(args);

		fragmentTransaction.replace(containerId, driveGraphFragment);
//		fragmentTransaction.addToBackStack(null);
		fragmentTransaction.commit();
	}
	

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
