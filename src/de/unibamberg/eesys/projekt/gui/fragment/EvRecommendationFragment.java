package de.unibamberg.eesys.projekt.gui.fragment;

import android.content.Context;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.TextView;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.Ecar;
import de.unibamberg.eesys.projekt.businessobjects.Ecar.CarState;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;
import de.unibamberg.eesys.projekt.gui.MainActivity;

/**
 * Fragment for Main Overview with battery display View Overview in three
 * different versions according to the car state (driving, charging, parking)
 * 
 * @author Julia
 *
 */
public class EvRecommendationFragment extends Fragment implements 
		OnTouchListener, OnClickListener {
	public static final String TAG = "StatusFragment";
	public static final String ARG_STATUS = "status";
	public String GPS_NO_SIGNAL = "GPS has no signal.";
	public String GPS_NO_SIGNAL_TEMPORARILY = "GPS has no signal (temporarily).";
	public String GPS_DISABLED = "GPS is currently disabled in Android settings.";

	AppContext appContext;
	View rootView;

	/**
	 * Fragment Class Constructor
	 */
	public EvRecommendationFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	/** 
	 * called when switching to status fragment (i.e. after having viewed another menu tab (i.e. drivegraph))
	 * Status Fragment is initial view after app is started 
	 */
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_evrecommendation,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
		// set on click listener for link
		
		TextView link1 = (TextView) rootView.findViewById(R.id.link1);
		link1.setOnClickListener( new View.OnClickListener() {
		    @Override
		    public void onClick(View v) {
		    	// open fragment drawer analysis and show Table + graph of critical trips, 3 = third item in drawer menu 
		    	MainActivity mA = (MainActivity) getActivity();
		    	mA.selectItem(3);
		    }
		});		
		
		return rootView;
	}
	
	

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		v.performClick();
		L.d("onTouch()");
		return false;
	}

	@Override
	public void onClick(View v) {
		L.d("onClick()");
	}

}
