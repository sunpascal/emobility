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
import de.unibamberg.eesys.projekt.EcoDrivingScoreCalculator;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.DriveSequence;
import de.unibamberg.eesys.projekt.businessobjects.EcoDrivingScore;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;
import de.unibamberg.eesys.projekt.gui.EcoDrivingTechniqueAdapter;
import de.unibamberg.eesys.statistics.BatterySocsReport;
import de.unibamberg.eesys.statistics.Statistic;
import de.unibamberg.eesys.statistics.StatisticsException;

/**
 * 
 * @author Pascal
 * 
 */
public class EcoDrivingFeedbackTeqniqueFragment extends android.support.v4.app.Fragment {

	AppContext appContext;
	View rootView;	
	
	/**
	 * trip for which to display feedback
	 * if null, use last trip
	 */
	DriveSequence trip = null; 
	
	public void setTrip(DriveSequence trip) {
		this.trip = trip;
	}
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_ecodriving_feedback_technique,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
		List<DriveSequence> trips = null;
		if (trip == null) {
			try {
				trips = appContext.getDb().getDriveSequences(true);
				// use last trip
				if (trips != null && trips.size() > 0)
					this.trip = trips.get(0);
			} catch (DatabaseException e) {
				Toast.makeText(appContext, "Could not load trips.", Toast.LENGTH_SHORT);
				e.printStackTrace();
			}
		}
		
		if (trip != null ) {
		
		    ListView listView = (ListView) rootView.findViewById(R.id.listViewDoingWell);
		    
		    EcoDrivingScoreCalculator scoreCalculator = new EcoDrivingScoreCalculator(appContext); 
		    
		    scoreCalculator.calculateScores(trip);
		    
		    EcoDrivingScore[] values = scoreCalculator.getScores();
		    EcoDrivingTechniqueAdapter adapter = new EcoDrivingTechniqueAdapter(appContext, values);
		    listView.setAdapter(adapter);
		}
		
		else Toast.makeText(appContext, "No trips recored yet.", Toast.LENGTH_SHORT);
	    
	    return rootView;	    
	    
	}
	
	

}
