package de.unibamberg.eesys.projekt.gui;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;

public class DriversLogFragment extends Fragment {

	public static final String TAG = "DriversLogFragment";
	public static final String ARG_STATUS = "status";

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

	/**
	 * Fragment for displaying an list of all recorded drive sequences.
	 * 
	 * @author Matthias
	 * 
	 * */
	public DriversLogFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_driverslog, container,
				false);

		AppContext appContext = (AppContext) getActivity()
				.getApplicationContext();
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

		listView.setAdapter(objAdapter);

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

	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
