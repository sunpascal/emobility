package de.unibamberg.eesys.projekt.gui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.FragmentTransaction;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.EcoDrivingProgress;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;
import de.unibamberg.eesys.projekt.gui.EcoDrivingTechniqueAdapter;
import de.unibamberg.eesys.statistics.BatterySocsReport;
import de.unibamberg.eesys.statistics.Statistic;
import de.unibamberg.eesys.statistics.StatisticsException;

/**
 * 
 * @author Robert
 * 
 */
public class EcoDrivingFeedbackTeqniqueFragment extends android.support.v4.app.Fragment {

	public static final String ARG_STATUS = "status";

	public static final int BATTERY_SOCS = 3223478;
	
	AppContext appContext;
	View rootView;	
	
	public EcoDrivingFeedbackTeqniqueFragment() {
	}
	

	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_ecodriving_feedback_technique,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
	    ListView listView = (ListView) rootView.findViewById(R.id.listViewDoingWell);
	    
	    EcoDrivingProgress[] values = new EcoDrivingProgress[] { 
	    	    		new EcoDrivingProgress("Constant speed", 90, "Your are doing well: "),
	    	    		new EcoDrivingProgress("Moderate acceleration", 80),
	    	    		new EcoDrivingProgress("Avoiding high speeds", 66),
	    	    		new EcoDrivingProgress("Anticipating stops", 30, "Your could improve on: ")    };
	    EcoDrivingTechniqueAdapter adapter = new EcoDrivingTechniqueAdapter(appContext, values);
	    listView.setAdapter(adapter);
	    
	    return rootView;	    
	    
	}
	
	

}