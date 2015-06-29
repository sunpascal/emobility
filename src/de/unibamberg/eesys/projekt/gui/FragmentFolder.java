package de.unibamberg.eesys.projekt.gui;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.NavigableMap;
import java.util.Set;
import java.util.TreeMap;

import com.google.android.gms.wallet.wobs.c;

import de.unibamberg.eesys.projekt.gui.fragment.*;
import android.support.v4.app.Fragment;

public class FragmentFolder {
	
	private MODE currentDrawerMode = null; 	// pointer to current mode 
	private int positionWithinMode = 0;					// pointer to current tab
	
	private TreeMap<MODE, ArrayList<Fragment>> drawers = new TreeMap<FragmentFolder.MODE, ArrayList<Fragment>>();
	
	public static enum MODE {
		DASHBOARD, EV_RECOMMENDATION, ECO_DRIVING, ANALYSIS, BLANK
	};
	
	public FragmentFolder() {
		
		// Mode Dashboard
		ArrayList<Fragment> dashboardFragments = new ArrayList();
		dashboardFragments.add(new DashboardFragment());
		dashboardFragments.add(new DashboardOldFragment());			// alter Startbildschirm
		drawers.put(MODE.DASHBOARD, dashboardFragments);
		
		// Mode EV Recommendation
		ArrayList<Fragment> evRecommendationFragments = new ArrayList();
		evRecommendationFragments.add(new EvRecommendationFragment());
		evRecommendationFragments.add(new EvRecommendationFragment1());
		evRecommendationFragments.add(new EvRecommendationFragment2());
		drawers.put(MODE.EV_RECOMMENDATION, evRecommendationFragments);		
		
		// Mode Eco-driving
		ArrayList<Fragment> ecodriving = new ArrayList();
		ecodriving.add(new EcoDrivingFeedbackTeqniqueFragment());
		ecodriving.add(new EcoDrivingTableFragment());
		ecodriving.add(new EcoDrivingBackgroundInfoFragment());
		ecodriving.add(new AnalysisSpeedFragment());
		ecodriving.add(new EcoDrivingGoalFragment());
		ecodriving.add(new EcoDrivingAvgConsumptionFragment());
		drawers.put(MODE.ECO_DRIVING, ecodriving);		
		
		// Mode Analysis		
		ArrayList<Fragment> analysis = new ArrayList();
		analysis.add(new AnalysisCriticalTripsFragment());
		analysis.add(new AnalysisTripDistanceFragment());
		analysis.add(new AnalysisDrivePossibilityFragment());  	// pie chart
		analysis.add(new AnalysisBatteryLevelFragment());		// batteriestand
		analysis.add(new AnalysisTripMapFragment());			// Karte mit allen Fahrten
		drawers.put(MODE.ANALYSIS, analysis);		
	}

	/** 
	 * returns the previous tab in the current mode
	 * @return
	 */
	public Fragment getPreviousTab() {
		ArrayList<Fragment> tabsInDrawer = drawers.get((currentDrawerMode));
		if (positionWithinMode > 0) {
			positionWithinMode--;  
			return tabsInDrawer.get(positionWithinMode);
		}
		else return null; 
	}
	
	/** 
	 * returns the next tab in the current mode
	 * @return
	 */
	public Fragment getNextTab() {
		ArrayList<Fragment> tabsInDrawer = drawers.get((currentDrawerMode));
		if (positionWithinMode < tabsInDrawer.size()-1) {
			positionWithinMode ++;  
			return tabsInDrawer.get(positionWithinMode);	
		}
		else return null;
	}	
	
	/** 
	 * gets the first fragment of the mode
	 * will be null if fragment cannot be found or is already active
	 */
	public Fragment getDrawerFromPosition(int position) {
		switch (position) {
	        case 0:
	        	return getDrawer(MODE.DASHBOARD);
	        case 1: 
	        	return getDrawer(MODE.EV_RECOMMENDATION);
	        case 2:
	        	return getDrawer(MODE.ECO_DRIVING);
	        case 3:
	        	return getDrawer(MODE.ANALYSIS);
	        default: 
	        	return null;
		}
		
	}
	
	/** 
	 * returns the first tab in the current mode
	 * @return
	 */
	public Fragment getFirstTab() {
		ArrayList<Fragment> tabsInDrawer = drawers.get((currentDrawerMode));
			positionWithinMode = 0;  
			return tabsInDrawer.get(0);	
	}		
	
	/** 
	 * returns the first tab in the current mode
	 * @return
	 */
	public Fragment getAnalysisSpeedTab() {
		ArrayList<Fragment> tabsInDrawer = drawers.get((currentDrawerMode));
			positionWithinMode = 0;  
			// todo: 3 should not be fixed in case fragments are added or removed!
			return tabsInDrawer.get(3);	
	}	
	
	/** returns the first fragment tab of the current drawer mode 
	 * 
	 * @param position
	 * @return
	 */
	public Fragment getDrawer(MODE mode) {
		
		// if the fragment is already active, do not do anything (prevent IllegalStateException)
		if (mode.equals(currentDrawerMode))
			return null; 
		
		currentDrawerMode = mode;
		positionWithinMode = 0; 
		
		ArrayList<Fragment> tabsInDrawer = drawers.get(currentDrawerMode);
		Fragment firstFragmentofMode = tabsInDrawer.get(0);		
//		Class c = DashboardFragment.class;
//		Fragment f = (Fragment) c.newInstance();
		
		return firstFragmentofMode;
	}

	public MODE getCurrentDrawerMode() {
		return currentDrawerMode;
	}
	
}
