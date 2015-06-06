package de.unibamberg.eesys.projekt.gui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;

/**
 * The DriveGraphFragment visualizes finished DriveSequences
 * on a Google map fragment 
 * 
 * @author Pascal, Matthias, Robert, Julia, Stefan
 * @version 1.0
 *
 */
public class AnalysisTripMapFragment extends Fragment {

	private static GoogleMap map;
	public static SupportMapFragment mapFragment;
	private static FragmentTransaction fragmentTransaction;

	/**
	 * Sets the center and zoom level of the map when shown. Throws an exception
	 * when database is empty.
	 */
	List<DriveSequence> listDriveSequences;
	
	/** View that displays the fragment. */
	private View rootView;
	
	/** Instance of the SQL database. */
	private DBImplementation db;
	
	/** Color of the drawn line on the google map fragment. */
	private int color;
	
	/** Latitude of a coordinate. */
	  
	private double latitude;
	
	/** Longitude of a coordinate. */
	private double longitude;

	public static final String TAG = "DriveGraphFragment";
	public static final String ARG_STATUS = "status";

	/**
	 * Fragment for displaying one or more drive sequences.
	 */
	public AnalysisTripMapFragment() {
		L.d("DriveGraphFragment");
	}

	/**
	 * Fragment for displaying one or more drive sequences.
	 * 
	 * @author Matthias
	 * 
	 * @param trackNumber
	 *            is necessary for selecting an specific drive sequence to be
	 *            displayed.
	 * 
	 */
	public AnalysisTripMapFragment(DriveSequence ds) {

		L.d("DriveGraphFragment(DriveSequence ds)");

		// show only one drive sequence, do not load entire list
		listDriveSequences = new ArrayList<DriveSequence>();
		listDriveSequences.add(ds);
	}
	
	/**
	 * This map fragment can only be displayed by Api level 17 or later.
	 *   
	 * @return true if Api level 17 or later, false if Api level < 17. 
	 */
	private boolean isAtLeastApi17() {
		return VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		L.d("oncreate");

		rootView = inflater.inflate(R.layout.fragment_drivegraph, container,
				false);

		AppContext appContext = (AppContext) getActivity()
				.getApplicationContext();

		db = new DBImplementation(appContext);
		
		if (listDriveSequences == null)
			try {
				listDriveSequences = db.getDriveSequences(true);
			} catch (DatabaseException e1) {
				Toast.makeText(appContext, "An unexpected error has occurred.",
						Toast.LENGTH_SHORT).show();
				
				e1.printStackTrace();
			}		

		try {
			for (DriveSequence ds : listDriveSequences) {
				ds.setWayPoints(db.getWayPoints(ds));
			}
		} catch (DatabaseException e) {
			Toast.makeText(appContext, "An unexpected error has occurred.",
			Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		if(isAtLeastApi17()){
			loadMap();
			map = mapFragment.getMap();
			if (map != null) {
				showMap();
			}else{
		
			}
		}

		// do not use async task - creates crashes
		// ToDo (further development): find alternate way to increase performance
		// start async task
		// ResourceProvider resProvider = new
		// ResourceProvider(rootView.getContext());
		// resProvider.execute();

		return rootView;
	}


	@SuppressLint({ "NewApi", "CommitTransaction" })
	/**
	 * loads Google Map without any waypoints
	 */
	public void loadMap() {
		if(isAtLeastApi17()){
			FragmentManager fragmentManager = getChildFragmentManager();
			fragmentTransaction = fragmentManager.beginTransaction();
			mapFragment = new SupportMapFragment();
			fragmentTransaction.replace(R.id.map, mapFragment);
			fragmentTransaction.addToBackStack(null);
			fragmentTransaction.commit();
		}
	}

	private class ResourceProvider extends
			AsyncTask<Integer, Void, List<DriveSequence>> {
		private DBImplementation db;
		List<DriveSequence> DbReturnValue;

		public ResourceProvider(Context context) {
			AppContext appContext = (AppContext) context
					.getApplicationContext();
			db = appContext.getDb();
		}

		@Override
		protected List<DriveSequence> doInBackground(Integer... params) {
			L.d(TAG, "doInBackground");

			AppContext appContext = (AppContext) getActivity()
					.getApplicationContext();

			/* Load all drive sequences
			 * only execute if not coming from driver's log
			   if called by drivers log, listDriveSequence already contains the DriveSequence
			   that should be called
			 */
			if (listDriveSequences == null) {
				listDriveSequences = new ArrayList<DriveSequence>();
				// load all drive sequences
				try {
					listDriveSequences = db.getDriveSequences(true);

					for (DriveSequence ds : listDriveSequences) {
						ds.setWayPoints(db.getWayPoints(ds));
					}
				} catch (DatabaseException e) {
					Toast.makeText(appContext,
					"An unexpected error has occurred.",
					Toast.LENGTH_SHORT).show();
					e.printStackTrace();
				}
			}

			DbReturnValue = listDriveSequences;
			return DbReturnValue;
		}

		@Override
		protected void onPostExecute(List<DriveSequence> result) {
			L.d(TAG, "onPostExecute");
			receivedData(DbReturnValue);
		}
	}

	/**
	 * execute on asyntask is done (Google Maps loaded)
	 * 
	 * @param dbReturnValue
	 */
	public void receivedData(List<DriveSequence> dbReturnValue) {
		L.d("Async thread is done.");

		map = mapFragment.getMap();
		if (map != null) {
			showMap();
		} else {
			L.wtf("View not ready.");
		}

	}

	@Override
	public void onResume() {
		L.d("resume");
		if(isAtLeastApi17()){
			map = mapFragment.getMap();
			if (map != null) {
				showMap();
			}
		}else{
			TextView msg;
			msg = (TextView) getView().findViewById(R.id.textView1);
			
			msg.setVisibility(View.VISIBLE);
		}
		super.onResume();
	}
	
	/**
	 * Indicates drivesequences on the displayed map
	 */
	private void showMap() {

		if (map == null || isAdded() == false) {
			L.wtf("View is not yet ready!");
		} else {

			// will be executed only when Google Maps is ready

			if (listDriveSequences.size() > 0) {
				// List that includes all coordinates of a selected drive
				List<WayPoint> wayPoints = (listDriveSequences.get(0).getWayPoints());
				
				/* make sure trip contains waypoints (empty trip does not make sense, but could 
				have been stored during testing... */
				if (wayPoints.size() > 0) {  
					LatLng coordinate;
					/** Coordinate made out of latitude and longitude. */
					coordinate = new LatLng(wayPoints.get(0).getGeoCoordinate().getLatitude(), 
											wayPoints.get(0).getGeoCoordinate().getLongitude());
					map.addMarker(new MarkerOptions().position(coordinate)).setTitle("Home");
					map.animateCamera(CameraUpdateFactory.newLatLngZoom(coordinate,	12));
				}

				// draw all driveSequences (trips) which are in
				// listDriveSequences
				drawMultiplePolyLineOnMap();
			}
			else {
				Log.e(null, "No drive sequences within the database yet");
			}
		}
	}

	/**
	 * Draws multiple lines made out of polygons on a google map fragment.
	 */
	private void drawMultiplePolyLineOnMap() {
		L.d("drawMultiplePolyLineOnMap");
		for (DriveSequence ds : listDriveSequences) {
			drawSinglePolyLineOnMap(ds);
		}
	}

	/**
	 * Draws a single line made out of polygons on a google map fragment.
	 */
	private void drawSinglePolyLineOnMap(DriveSequence driveSequence) {

		List<WayPoint> wayPoints = driveSequence.getWayPoints();
		// if waypoints = null, it means that the trip's waypoints could not be loaded
		if (driveSequence.getWayPoints() != null) {

			/** Single coordinate of a selected drive sequence. */
			ArrayList<LatLng> listDriveSequenceCoordinates = new ArrayList<LatLng>();
			int i = 0;
			for (WayPoint wp : wayPoints) {
				i++;
				// for performance reasons, do not draw EVERY waypoint on graph
				if (i % 20 == 0) {
					latitude = wp.getGeoCoordinate().getLatitude();
					longitude = wp.getGeoCoordinate().getLongitude();

					/** Coordinate made out of latitude and longitude. */
					LatLng coordinate = new LatLng(latitude, longitude);

					listDriveSequenceCoordinates.add(coordinate);
					//State of charge at the end of the drive sequence
					double stateOfCharge = driveSequence.getSocEnd();
				if (stateOfCharge < 0) {
					color = Color.RED;
				} else {
					color = Color.GREEN;
				}

				// draw line to next point
				map.addPolyline(new PolylineOptions()
						.addAll(listDriveSequenceCoordinates).width(5)
						.color(color));
				}
			}
		} else {
			L.e("No waypoints in database yet.");
		}
		L.i("Trip drawn on map.");
	}

	@SuppressLint("CommitTransaction")	

	@Override
	public void onDestroyView() {
		mapFragment = ((SupportMapFragment) getFragmentManager().findFragmentById(
				R.id.map));
		if (mapFragment != null) {
			fragmentTransaction = getActivity().getSupportFragmentManager()
					.beginTransaction();
			fragmentTransaction.remove(mapFragment);
			fragmentTransaction.commit();
		}
		super.onDestroyView();
	}
}
