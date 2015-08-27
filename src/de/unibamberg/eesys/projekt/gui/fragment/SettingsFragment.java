package de.unibamberg.eesys.projekt.gui.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.L;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.Battery;
import de.unibamberg.eesys.projekt.businessobjects.Ecar;
import de.unibamberg.eesys.projekt.businessobjects.VehicleType;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;

/**
 * Class SettingsFragment to create a Settings Menu (Preferences)
 * 
 * @author Julia, Pascal
 *
 */
public class SettingsFragment extends PreferenceFragment  {

	private DBImplementation db;
	private AppContext appContext;
	private List<String> vTypeNames = new ArrayList<String>();
	private List<String> entryVal = new ArrayList<String>();
	private List<VehicleType> vehicleTypes = new ArrayList<VehicleType>();

	private static final String TAG = "Settings Fragment";

	/**
	 * Method is called when the Settings menu is opened Method sets the
	 * listPrefence to show all available eCar models
	 * 
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get DatabaseConnection
		appContext = (AppContext) getActivity().getApplicationContext();
		db = appContext.getDb();
		
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);

		// todo: check how to set listener for AFTER user has changed option
		
		try {
			vehicleTypes = db.getVehicleTypes();
		} catch (DatabaseException e) {
			Toast.makeText(appContext.getApplicationContext(),
					"An unexpected error has occurred.", Toast.LENGTH_SHORT)
					.show();
			e.printStackTrace();
		}

		if (vehicleTypes != null) {
			for (int i = 0; i < vehicleTypes.size(); i++) {
				vehicleTypes.get(i).toString();
				vTypeNames.add(vehicleTypes.get(i).getName());
			}
		}

		final CharSequence[] entries = vTypeNames
				.toArray(new CharSequence[vehicleTypes.size()]);

		for (int x = 0; x < vehicleTypes.size(); x++) {
			int z = x + 1;
			entryVal.add(Integer.toString(z));
		}
		final CharSequence[] entryValues = entryVal
				.toArray(new CharSequence[entryVal.size()]);
		
		// Instantiate the listPreference and set eCar values
		ListPreference listPreference = (ListPreference) findPreference("display.car_name");
		listPreference
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
						// Called when the ecar model is changed by the user
							Ecar ecar = new Ecar(appContext);
							for (int x = 0; x < entryVal.size(); x++) {
								if (newValue.equals(entryVal.get(x))) {
									VehicleType selectedType = vehicleTypes
											.get(x);
									ecar.setVehicleType(selectedType);
									ecar.setName(selectedType.getName());
									Battery battery = new Battery();
									battery.setCurrentSoc(selectedType
											.getBatteryCapacity());
									battery.setCharging(false);
									ecar.setBattery(battery);
								}
							}
							appContext.setEcar(ecar);
							return true;
					}
				});		

		listPreference.setEntries(entries);
		listPreference.setEntryValues(entryValues);

		// Export Database to on Android phone and make a security copy
		Preference exportDB = this.findPreference("testing.exportDB");
		exportDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(final Preference preference) {
				Log.v(SettingsFragment.TAG, "Export DB");
				appContext.getDb().exportDb();
				Toast.makeText(appContext, "Database exported successfully",
						Toast.LENGTH_LONG).show();
				return true;
			}
		});
		
		Preference shutdown = this.findPreference("shutdown");
		shutdown.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(final Preference preference) {
				
				// quit app and stop running in the background
				appContext.shutdown();
				getActivity().finish();
//				Warning: do not call getActivity().finish() as it will cause a PreferenceManager exception  
				
				return true;
			}
		});		
		
		// load timeout for ending trips
		ListPreference prefMaxVehicleTimeout2 = (ListPreference) findPreference("testing.maxvehiclestillduration");
		prefMaxVehicleTimeout2
				.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
					@Override
					// use on onPreferenceChange instead of onPreferenceClick 
					// to get code AFTER the preference is changed
					// Note: this is still executed before the change is stored! 
					// only way to update is seems to be to access newValue! 
					public boolean onPreferenceChange(Preference preference,
							Object newValue) {
							appContext.getParams().setMaxVehicleStillLocation(newValue);
							return true;
					}
				});			

		// Following options are only for testing!
		// Import Database for testing
		Preference importDB = this.findPreference("testing.importDB");
		importDB.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(final Preference preference) {
				Log.v(SettingsFragment.TAG, "Import DB");
				appContext.getDb().importDb();
				Toast.makeText(appContext, "Database imported successfully",
						Toast.LENGTH_LONG).show();
				return true;
			}
		});
		
		Preference resetBatteryButton = this.findPreference("testing.resetBattery");
		resetBatteryButton.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			
			public boolean onPreferenceClick(final Preference preference) {
				L.v("resetting battery to 100%");
				appContext.getEcar().getBattery().setCurrentSoc(
						appContext.getEcar().getVehicleType().getBatteryCapacity());
				return true;
			}
		});		
		
		// load Gpx for testing
		Preference loadGpx = this.findPreference("testing.loadGpx");
		loadGpx.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			public boolean onPreferenceClick(final Preference preference) {
				appContext.getMobilityManager().loadTestDataFromGpx();
				return true;
			}
		});
		
	}

}
