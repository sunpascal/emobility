package de.unibamberg.eesys.projekt.gui.fragment;

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
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
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

	private AppContext appContext;
	
	private NumberPicker np;
	private Button okButton; 
	private TextView currentGoal; 
	private ProgressBar progressBar;
	
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
		
		okButton = (Button) rootView.findViewById(R.id.button1);  
		currentGoal = (TextView) rootView.findViewById(R.id.currentGoal);
		currentGoal.setText(appContext.getGoal() + "");
		
		progressBar = (ProgressBar) rootView.findViewById(R.id.progressBar1);
		progressBar.setVisibility(View.VISIBLE);
		updateProgressBar();
		
		np = (NumberPicker) rootView.findViewById(R.id.numberPicker1);
		np.setMaxValue(30);
		np.setMinValue(10);
		
		// set value of goal number picker - default is defined in Params class
		np.setValue(appContext.getGoal()); 
		
		okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
    			int newGoal = np.getValue();
    			appContext.setGoal(newGoal);
    			double currentCons = appContext.getLastTrip().calcAveragekWhPer100Km();
    			appContext.setConsumptionT0(currentCons);
    			currentGoal.setText(newGoal + "");
    			updateProgressBar();
            }   
		});
		
		return rootView;
	}


	
	private void updateProgressBar() {
		
/* 		Beispiel: 		
		Verbrauch t0: 24	=> Ziel: 20 kWh		
				Quotient:    100% =~   24-20 => 4

		Nach einigen Fahrten: 			
		Verbrauch t1: 23    => (24-23) / 4 => 25%
			*/ 
		
		double consT0 = appContext.getConsumptionT0();
		int goal = appContext.getGoal();
		double quotient = consT0 - goal; 
		
		double currentCons = appContext.getLastTrip().calcAveragekWhPer100Km();
		
		int progress = (int) Math.round( ((consT0 - currentCons) / quotient) * 100 );
		
		if (progress <0)
			progress = 0; 
		else if (progress > 100)
			progress = 100; 
		progressBar.setProgress(progress);
	}

}
