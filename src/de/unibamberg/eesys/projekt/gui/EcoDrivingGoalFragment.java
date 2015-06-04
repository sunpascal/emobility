package de.unibamberg.eesys.projekt.gui;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
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
import android.widget.ArrayAdapter;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.Ecar;
import de.unibamberg.eesys.projekt.businessobjects.Ecar.CarState;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;


public class EcoDrivingGoalFragment extends Fragment {
	public static final String TAG = "EcoDrivingGoalFragment";
	public static final String ARG_STATUS = "status";

	AppContext appContext;

 
	/**
	 * Fragment Class Constructor
	 */
	public EcoDrivingGoalFragment() {
		// Empty constructor required for fragment subclasses
	}

	@Override
	/** 
	 * called when switching to status fragment (i.e. after having viewed another menu tab (i.e. drivegraph))
	 * Status Fragment is initial view after app is started 
	 */
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_ecodriving_goal,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
		// spinner reference: http://androidexample.com/Spinner_Basics_-_Android_Example/index.php?view=article_discription&aid=82&aaid=105
		// Todo: Listener
		Spinner spinner1 = (Spinner) rootView.findViewById(R.id.spinner1);
        List<String> list = new ArrayList<String>();
        list.add("100 kWh");
        list.add("150 kWh");
        list.add("200 kWh");
        list.add("250 kWh");
        
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>
                     (this.getActivity(), android.R.layout.simple_spinner_item,list);
                      
        dataAdapter.setDropDownViewResource
                     (android.R.layout.simple_spinner_dropdown_item);
                      
        spinner1.setAdapter(dataAdapter);        
		
		NumberPicker np = (NumberPicker) rootView.findViewById(R.id.numberPicker1);
		np.setMaxValue(300);
		np.setMinValue(0);
		np.setValue(200);
		
		return rootView;
	}

}
