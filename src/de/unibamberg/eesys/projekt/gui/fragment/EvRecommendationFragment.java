package de.unibamberg.eesys.projekt.gui.fragment;

import android.content.Context;
import android.location.LocationProvider;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.text.SpannableString;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.NoTripsException;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.Recommender;
import de.unibamberg.eesys.projekt.businessobjects.Ecar;
import de.unibamberg.eesys.projekt.businessobjects.VehicleType;
import de.unibamberg.eesys.projekt.businessobjects.Ecar.CarState;
import de.unibamberg.eesys.projekt.businessobjects.WayPoint;
import de.unibamberg.eesys.projekt.gui.MainActivity;

/**
 * Fragment for Main Overview with battery display View Overview in three
 * different versions according to the car state (driving, charging, parking)
 * 
 * @author Pascal
 *
 */
public class EvRecommendationFragment extends Fragment implements 
		OnTouchListener, OnClickListener {

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
		
		TextView txt_vehicle1 = (TextView) rootView.findViewById(R.id.vehicle1name);
		TextView txt_vehicle2 = (TextView) rootView.findViewById(R.id.vehicle2name);
		TextView txt_vehicle1specs = (TextView) rootView.findViewById(R.id.vehicle1specs);
		TextView txt_vehicle2specs = (TextView) rootView.findViewById(R.id.vehicle2specs);
		TextView txt_alternativeRec = (TextView) rootView.findViewById(R.id.textView2);
		ImageView image1 = (ImageView) rootView.findViewById(R.id.imageView1);
		ImageView image2 = (ImageView) rootView.findViewById(R.id.imageView2);
		
		Recommender recommender = new Recommender(appContext);
		
		VehicleType v1;
		VehicleType v2;
		try {
			v1 = recommender.getRecommendation100PercentOfTrips();
			v2 = recommender.getAlternativeRecommendationWithTripAdaptation(v1);
			
			if (v1 == null)	{
				txt_vehicle1.setText("No suitable vehicle found.");
				image1.setVisibility(View.INVISIBLE);
			}
			else {
				txt_vehicle1.setText(v1.getName() + ":" + v1.getBatteryCapacity() + "kWh");
				txt_vehicle1specs.setText(v1.getPrice());
				int imageRes = getVehicleImage(v1); 
				if (imageRes != -1) {
					image1.setVisibility(View.VISIBLE);
					image1.setImageResource(imageRes);
				}
			}
			
			if (v2 == null) {
				L.i("No suitable alternative vehicle was found.");
				// hide everything related to alternative recommendation
				txt_alternativeRec.setVisibility(View.INVISIBLE);
				txt_vehicle2.setVisibility(View.INVISIBLE);
				txt_vehicle2specs.setVisibility(View.INVISIBLE);
				image2.setVisibility(View.INVISIBLE);
			}
			else { 
				String alternativeRecTxt =  "" + txt_alternativeRec.getText();
				String nAdaptationsRequired = v2.getnTripAdaptationsrequired() + "";
				txt_alternativeRec.setText(
						alternativeRecTxt.replace("N_TRIPS", nAdaptationsRequired));
				
				txt_vehicle2.setText(v2.getName() + ":" + v2.getBatteryCapacity() + "kWh");
				txt_vehicle2specs.setText(v2.getPrice());
				int imageRes = getVehicleImage(v2); 
				if (imageRes != -1) {
					image2.setImageResource(imageRes);
					image2.setVisibility(View.VISIBLE);
				}
			}
		}
		catch (NoTripsException e) {
			L.e(e.getMessage() + e.getCause());
			e.printStackTrace();
			txt_vehicle1.setText("Please record more trips to receive a recommendation.");
		}
		
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
	
	public int getVehicleImage(VehicleType v1) {
		int picture = -1;
		String vehicleName = v1.getName(); 

		if (vehicleName.equals("BMW i3"))
			picture = R.drawable.bmwi3;
		else if (vehicleName.equals("VW eUP!"))
			picture = R.drawable.vweup;
		else if (vehicleName.equals("Smart ED"))
			picture = R.drawable.smarted;
		else if (vehicleName.equals("Tesla Model S (85 kWh)"))
			picture = R.drawable.teslamodels;
		else if (vehicleName.equals("Tesla Model S (70 kWh)"))
			picture = R.drawable.teslamodels;
		
		return picture; 
	}		

}
