package de.unibamberg.eesys.projekt.gui;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.ActionBar.TabListener;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;

/**
 * Handles the Drawer Handles the Fragments based on the main activity from the
 * drawer Is the Main Activity in the app and is called first when app is
 * initially loaded
 * 
 * @author Julia
 *
 */
public class MainActivity extends FragmentActivity implements TabListener{

	static final String TAG = "MainActivity";
	static final String WAYPOINT = "waypoint";

	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;
	private CharSequence mDrawerTitle;
	private CharSequence mTitle;
	private String[] mDrawerOptions;

	private AppContext appContext;
	private boolean gpsEnabled;
	private boolean open;

	private MODE shownFragment = MODE.DASHBOARD;

	DashboardFragment gpsUpdateListener;
	
	// tabs 
	private DemoCollectionPagerAdapter mDemoCollectionPagerAdapter;
	private ViewPager mViewPager;
	private ActionBar actionBar; 
	
	private static enum MODE {
		DASHBOARD, EV_RECOMMENDATION, ECO_DRIVING, ANALYSIS, BLANK
	};
	
	/**
	 * Manages the options from the drawer and inflates the selected Fragment
	 * 
	 * @param position of the option in the drawer
	 */
	public void selectItem(int position) {

		// check that the activity is using the layout version with the
		// content_frame FrameLayout
		if (this.findViewById(R.id.content_frame) != null) {

			// create a new Fragment to be placed in the activity layout
			Fragment selectedFragment = null;
			switch (position) {
			
			case 0:
				selectedFragment = new DashboardFragment();
				shownFragment = MODE.DASHBOARD;
				actionBar.removeAllTabs();
//				actionBar.hide();
				break;	
			case 1:			
				selectedFragment = new EvRecommendationFragment();
				shownFragment = MODE.EV_RECOMMENDATION;
		    	actionBar.removeAllTabs();
//		    	actionBar.hide();
				break;		
			case 2:
				selectedFragment = new EcoDrivingFragment();
				shownFragment = MODE.ECO_DRIVING;
				break;	
			case 3:
				selectedFragment = new AnalysisFragment();
				shownFragment = MODE.ANALYSIS;
				break;		
				
			default: 
					selectedFragment = new DashboardFragment();
					shownFragment = MODE.DASHBOARD;
					actionBar.removeAllTabs();
//					actionBar.hide();
				break;
			}
			
//				showBlankFragment();
				
				// in case this activity was started with special instructions from
				// an Intent, pass the Intent's extras to the fragment as arguments
				selectedFragment.setArguments(this.getIntent().getExtras());
				Bundle args = new Bundle();
				args.putInt(selectedFragment.toString(), position);
				selectedFragment.setArguments(args);
	
				// add the fragment to the 'content_frame' FrameLayout
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, selectedFragment).commit();
				getSupportFragmentManager().executePendingTransactions();
	
				// update selected item and title, then close the drawer
				this.mDrawerList.setItemChecked(position, true);
				this.setTitle(this.mDrawerOptions[position]);
				this.mDrawerLayout.closeDrawer(mDrawerList);
				
				// hier tabs für entsprechendes Drawer menü vorbereiten
				if (shownFragment == MODE.ANALYSIS) {
				
					// Add n tabs, specifying the tab's text and TabListener
				    for (int i = 0; i < 2; i++) {
				    	actionBar.addTab(
				                actionBar.newTab()
				                        .setText("" + (i + 1))
				                        .setTabListener(this));
				    }
				    actionBar.show();				
				}
		}
	}


	// Since this is an object collection, use a FragmentStatePagerAdapter,
	// and NOT a FragmentPagerAdapter.
	public class DemoCollectionPagerAdapter extends FragmentStatePagerAdapter {
	    public DemoCollectionPagerAdapter(FragmentManager fm) {
	        super(fm);
	    }

	    @Override
	    /** open tab */ 
	    public Fragment getItem(int i) {
	    	
	    	Fragment fragment =  null;
	    	
	    	// todo: hier unterscheiden welches Drawer menü offen ist!
	    	if (shownFragment == MODE.ANALYSIS) {
	    	
		    	switch (i) {
		    	
		    		case 0: {
		    			fragment =  new DashboardFragment();
		    			break; 
		    		}	   	    	
		    	
		    		case 7: {
			    		fragment =  new EcoDrivingFeedbackTeqniqueFragment();
			    		break; 
		    		}	    	
		    	
		    		case 1: {
		    			fragment = new DriversLogFragment();
		    			break; 
		    		}
		    		case 2: {
		    			fragment = new StateOfChargeFragment();
		    			break; 
		    		}	  
		    		case 3: {
		    			fragment = new DrivePossibilityFragment();
		    			break; 
		    		}	   
		    		case 4: {
		    			fragment = new DriveGraphFragment();
		    			break; 
		    		}	 	
		    		case 5: {
		    			fragment = new DriveDistancesFragment();
		    			break; 
		    		}		 
		    		case 6: {
		    			fragment = new AvgConsumptionFragment();
		    			break; 
		    		}	
		    	}
	    		
	    		
	    	}
	    	
//	    	showBlankFragment();
	    	
	    	if (fragment != null) { 
	    		Bundle args = new Bundle();
	    		args.putString("tabName", fragment.toString());
	    		fragment.setArguments(args);
	    	}
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

	public final void showBlankFragment() {
			Fragment selectedFragment = new BlankFragment();
			selectedFragment.setArguments(this.getIntent().getExtras());
			Bundle args = new Bundle();
			args.putInt(selectedFragment.toString(), 0);
			selectedFragment.setArguments(args);

			this.getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, selectedFragment).commit();
			shownFragment = MODE.BLANK;
	}	
	
	@Override
	/** 
	 *  When swiping between pages, select the corresponding tab.
	 *  When the tab is selected, switch to the corresponding page in the ViewPager.
	 */
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
        
	    	if ( tab != null) {
	    		
	    		// take current drawer into account: 
	    		if (shownFragment == MODE.ANALYSIS) {
	    			mViewPager.setCurrentItem(tab.getPosition());
	    		}
	    	}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub
		
	}	
	
	
	/**
	 * onCreate called when application is initially started. Sets drawer
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		appContext = (AppContext) getApplicationContext();
		appContext.setMainActivity(this);  // used to access drawer via appContent from other fragments
		
		L.v("MainActivity onCreate()");
		setContentView(R.layout.activity_main);

		mTitle = mDrawerTitle = getTitle();
		mDrawerOptions = getResources().getStringArray(R.array.drawer_options);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		// set a custom shadow that overlays the main content when the drawer
		// opens
		mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow,
				GravityCompat.START);
		// set up the drawer's list view with items and click listener
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.drawer_list_item, mDrawerOptions));
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
		
		actionBar = getActionBar(); 

		// enable ActionBar app icon to behave as action to toggle nav drawer
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setHomeButtonEnabled(true);

		// ActionBarDrawerToggle ties together the the proper interactions
		// between the sliding drawer and the action bar app icon
		mDrawerToggle = new ActionBarDrawerToggle(this, /* host Activity */
		mDrawerLayout, /* DrawerLayout object */
		R.drawable.ic_drawer, /* nav drawer image to replace 'Up' caret */
		R.string.drawer_open, /* "open drawer" description for accessibility */
		R.string.drawer_close /* "close drawer" description for accessibility */
		) {
			public void onDrawerClosed(View view) {
				actionBar.setTitle(mTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}

			public void onDrawerOpened(View drawerView) {
				actionBar.setTitle(mDrawerTitle);
				invalidateOptionsMenu(); // creates call to
											// onPrepareOptionsMenu()
			}
		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			selectItem(0);
		}

		mDrawerLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				L.d("MainActivity.onClick()");
			}
		});

		String showDebugMessages = PreferenceManager
				.getDefaultSharedPreferences(appContext).getString(
						"testing.show_debug_messages", "disabled");
		// only show green debug messages if option is activated in app settings
		if (showDebugMessages.equals("show"))
			appContext.setShowDebugMessages(true);
		else
			appContext.setShowDebugMessages(false);

//		gpsAlert();
		
		
		// Action bar / tabs 
		
	    mViewPager = (ViewPager) findViewById(R.id.viewPager);

	    // Specify that tabs should be displayed in the action bar.
	    actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // ViewPager and its adapters use support library
        // fragments, so use getSupportFragmentManager.
        mDemoCollectionPagerAdapter =
                new DemoCollectionPagerAdapter(
                		getSupportFragmentManager());
        mViewPager.setAdapter(mDemoCollectionPagerAdapter);
	    
	   	    
	    // respond to user swipes
	    mViewPager.setOnPageChangeListener(
	            new ViewPager.SimpleOnPageChangeListener() {
	                @Override
	                public void onPageSelected(int position) {
	                    // When swiping between pages, select the corresponding tab.
	                	if (shownFragment == MODE.ANALYSIS) {	                	
	                		if (position < actionBar.getTabCount()-1) {
	                			actionBar.setSelectedNavigationItem(position);
	                		}
	                	}
	                }
	            });	    
	    
	    getSupportFragmentManager().executePendingTransactions();
		
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// If the nav drawer is open, hide action items related to the content
		// view
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.menu_preferences).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// The action bar home/up action should open or close the drawer.
		// ActionBarDrawerToggle will take care of this.
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		int itemId = item.getItemId();
		if (itemId == R.id.menu_preferences) {
			Intent settingsIntent = new Intent(this, SettingsActivity.class);
			this.startActivity(settingsIntent);
			return true;
		}
		// Handle action
		return super.onOptionsItemSelected(item);
	}

	/**
	 * The click listner for ListView in the navigation drawer
	 * */
	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			selectItem(position);
		}
	}


	@Override
	/**
	 * Handles the back button of Android phones 
	 * Used to handle back navigation between the fragments when selecting out of the drawer menu 
	 * Finishes the app after back button is pressed in the main overview
	 * ToDo: also select Dashboard drawer!!
	 */
	public final void onBackPressed() {
		if (shownFragment.equals("BlankShown")) {
			// We're in the MAIN Fragment.
			finish();
		} else {
			// We're somewhere else, reload the MAIN Fragment.

			Fragment selectedFragment = new DashboardFragment();
			selectedFragment.setArguments(this.getIntent().getExtras());
			Bundle args = new Bundle();
			args.putInt(selectedFragment.toString(), 0);
			selectedFragment.setArguments(args);

			this.getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, selectedFragment).commit();
			shownFragment = MODE.DASHBOARD;
		}
	}
	

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		getActionBar().setTitle(mTitle);
	}

	@SuppressWarnings("deprecation")
	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
		appContext.updateGui();
	}

	public void onDestroy() {
		// stopService (new Intent(getBaseContext(), BackgroundService.class));
		super.onDestroy();
	}

	@Override
	protected void onResume() {
		super.onResume();
		L.d("onResume()");
//		gpsAlert();
		// todo: klären, ob das auch nach der settingsactivity aufgerufen wird
		appContext.getEcar().processLifecycle();
		appContext.updateGui();
	}
	

	
	
	/**
	 * Displays alert dialog if GPS is not enabled when app is started or
	 * resumed.
	 * 
	 */
	public void gpsAlert() {
		appContext = (AppContext) getApplicationContext();
		gpsEnabled = appContext.isGpsEnabled();
		if (gpsEnabled == false && open == false) {
			open = true;
			final AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(
					"Your GPS seems to be disabled, do you want to enable it?")
					.setCancelable(true)
					.setPositiveButton("Yes",
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									startActivity(new Intent(
											Settings.ACTION_LOCATION_SOURCE_SETTINGS));
									dialog.cancel();
									open = false;
								}
							})
					.setNegativeButton("No",
							new DialogInterface.OnClickListener() {
								public void onClick(
										final DialogInterface dialog,
										final int id) {
									dialog.cancel();
									open = false;
								}
							});
			final AlertDialog alert = builder.create();
			try {
				alert.show();
			} catch (Exception e) {
				Log.d(TAG, "Bad Token Exception");
			}

		}
	}

	
}
