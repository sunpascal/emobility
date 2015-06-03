package de.unibamberg.eesys.projekt.gui;

import java.util.ArrayList;

import android.app.ActionBar;
import android.app.AlertDialog;
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
import android.widget.Toast;
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
public class MainActivity extends SwipeActivity {

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

	DashboardFragment gpsUpdateListener;
	
	private ActionBar actionBar; 
	
	protected static enum MODE {
		DASHBOARD, EV_RECOMMENDATION, ECO_DRIVING, ANALYSIS, BLANK
	};
	
	private FragmentFolder fragmentFolder;
	
	/**
	 * Manages the options from the drawer and inflates the selected Fragment
	 * 
	 * @param position of the option in the drawer
	 */
	public void selectItem(int position) {

		// will be null if fragment cannot be found or is already active
		Fragment selectedFragment = fragmentFolder.getDrawerFromPosition(position);
		
		// check that the activity is using the layout version with thecontent_frame FrameLayout
		// and only continue if fragment could be found
		if (this.findViewById(R.id.content_frame) != null && selectedFragment != null) {

			// in case this activity was started with special instructions from
			// an Intent, pass the Intent's extras to the fragment as arguments
			selectedFragment.setArguments(this.getIntent().getExtras());
			Bundle args = new Bundle();
			args.putInt(selectedFragment.toString(), position);
			selectedFragment.setArguments(args);

//			// add the fragment to the 'content_frame' FrameLayout
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, selectedFragment).commit();
			getSupportFragmentManager().executePendingTransactions();
			
			// update selected item and title, then close the drawer
			this.mDrawerList.setItemChecked(position, true);
			this.setTitle(this.mDrawerOptions[position]);
		}
			this.mDrawerLayout.closeDrawer(mDrawerList);
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
		
		fragmentFolder = new FragmentFolder(); 

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
	 * The click listener for ListView in the navigation drawer
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
		if (fragmentFolder.getCurrentDrawerMode() == MODE.DASHBOARD) {
			// We're in the MAIN Fragment.
			finish();
		} else {
			// We're somewhere else, reload the MAIN Fragment.

			Fragment selectedFragment =fragmentFolder.getDrawer(MODE.DASHBOARD);;
			selectedFragment.setArguments(this.getIntent().getExtras());
			Bundle args = new Bundle();
			args.putInt(selectedFragment.toString(), 0);
			selectedFragment.setArguments(args);

			getSupportFragmentManager().beginTransaction()
					.replace(R.id.content_frame, selectedFragment).commit();
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


	@Override
	protected void previous() {
		
		Fragment selectedFragment = fragmentFolder.getPreviousTab();
		
		// getNextTab() returns null if there is no previous tab to swipe to!
		if (selectedFragment != null) {
			selectedFragment.setArguments(this.getIntent().getExtras());
	
			// add the fragment to the 'content_frame' FrameLayout
			getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, selectedFragment).commit();
			getSupportFragmentManager().executePendingTransactions();	
		}
		
	}


	@Override
	protected void next() {
			Fragment selectedFragment = fragmentFolder.getNextTab();
			
			// getNextTab() returns null if there are no more tabs to swipe to!
			if (selectedFragment != null) {
				selectedFragment.setArguments(this.getIntent().getExtras());

			// 	add the fragment to the 'content_frame' FrameLayout
				getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, selectedFragment).commit();
				getSupportFragmentManager().executePendingTransactions();
			}
			
		}		

	
}
