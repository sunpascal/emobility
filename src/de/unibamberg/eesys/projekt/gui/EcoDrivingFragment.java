package de.unibamberg.eesys.projekt.gui;

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
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.app.FragmentTransaction;

import com.github.mikephil.charting.charts.Chart;
import com.github.mikephil.charting.charts.LineChart;

import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;
import de.unibamberg.eesys.statistics.BatterySocsReport;
import de.unibamberg.eesys.statistics.Statistic;
import de.unibamberg.eesys.statistics.StatisticsException;

/**
 * 
 * @author Robert
 * 
 */
public class EcoDrivingFragment extends Fragment {

	public static final String TAG = "StateOfChargeFragment";
	public static final String ARG_STATUS = "status";

	public static final int BATTERY_SOCS = 3223478;
	
	AppContext appContext;
	View rootView;	
	private FragmentActivity myContext;
	
	DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	ViewPager mViewPager;

	private int numberOfTabs = 2; 
	
	public EcoDrivingFragment() {
	}
	
	@Override
	public void onAttach(Activity activity) {
	    myContext=(FragmentActivity) activity;
	    super.onAttach(activity);
	}	
	
	@Override
	public View onCreateView(final LayoutInflater inflater, final ViewGroup container,
			Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.fragment_ecodriving,
				container, false);

		appContext = (AppContext) getActivity().getApplicationContext();
		
	    
	    mViewPager = (ViewPager) rootView.findViewById(R.id.viewPagerEcoDriving);
	    final ActionBar actionBar = getActivity().getActionBar();

	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                		myContext.getSupportFragmentManager());
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
	    
	    
	    // Create a tab listener that is called when the user changes tabs.
	    ActionBar.TabListener tabListener = new ActionBar.TabListener() {
	        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // When the tab is selected, switch to the
	            // corresponding page in the ViewPager.
	        	
            	int t = getActivity().getActionBar().getTabCount();
                // When swiping between pages, select the corresponding tab.
            	
            	if (tab != null) {     	
            		mViewPager.setCurrentItem(tab.getPosition());
            	}
	        }

	        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // hide the given tab
	        }

	        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
	            // probably ignore this event
	        }
	    };

	    
    	// todo: figure out how to manage tab lifecycle
    	actionBar.removeAllTabs();
	    
	    // Add n tabs, specifying the tab's text and TabListener
	    for (int i = 0; i < numberOfTabs; i++) {
	        
	    	actionBar.addTab(
	                actionBar.newTab()
	                        .setText("Tab " + (i + 1))
	                        .setTabListener(tabListener));
	        
	    }
	    
	    
	    // respond to user swipes
	    mViewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                	int t = getActivity().getActionBar().getTabCount();
	                    // When swiping between pages, select the corresponding tab.
	                	if (position < getActivity().getActionBar().getTabCount()) {
	                		getActivity().getActionBar().setSelectedNavigationItem(position);
	                	}
	                }
	            });	    
	    
	    return rootView;	    
	    
	}
	
	// Since this is an object collection, use a FragmentStatePagerAdapter,
	// and NOT a FragmentPagerAdapter.
	public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
	    public DemoCollectionPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    public android.support.v4.app.Fragment getItem(int i) {
	    	
	    	android.support.v4.app.Fragment fragment = (android.support.v4.app.Fragment) new EcoDrivingFeedbackTableFragment();
	    	
	    	if (i == 1) 
	    		fragment = (android.support.v4.app.Fragment) new EcoDrivingFeedbackTeqniqueFragment();
	    		
	        Bundle args = new Bundle();
	        // Our object is just an integer :-P
	        args.putInt(EcoDrivingBatteryStateFragment.ARG_OBJECT, i + 1);
	        fragment.setArguments(args);
	        return fragment;
	    }

	    @Override
	    public int getCount() {
	        return 100;
	    }

	    @Override
	    public CharSequence getPageTitle(int position) {
	        return "OBJECT " + (position + 1);
	    }

	}



	

}
