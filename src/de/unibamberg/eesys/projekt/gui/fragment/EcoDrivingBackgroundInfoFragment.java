package de.unibamberg.eesys.projekt.gui.fragment;

import android.support.v4.app.Fragment;
import android.text.InputType;
import android.app.ActionBar;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

/**
 * Fragment for Main Overview with battery display View Overview in three
 * different versions according to the car state (driving, charging, parking)
 * 
 * @author Julia
 *
 */
public class EcoDrivingBackgroundInfoFragment extends Fragment {

	AppContext appContext;


	/**
	 * Fragment Class Constructor
	 */
	public EcoDrivingBackgroundInfoFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	/** 
	 * called when switching to status fragment (i.e. after having viewed another menu tab (i.e. drivegraph))
	 * Status Fragment is initial view after app is started 
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_ecodriving_background_info,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
		TextView txt = (TextView) rootView.findViewById(R.id.textView1);
		
//	    txt.setInputType(txt.getInputType() | InputType.TYPE_TEXT_FLAG_MULTI_LINE);

		
		return rootView;
	}

}
