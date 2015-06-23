package de.unibamberg.eesys.projekt.gui;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import de.unibamberg.eesys.projekt.AppContext;
import de.unibamberg.eesys.projekt.R;
import de.unibamberg.eesys.projekt.businessobjects.ChargingStation;
import de.unibamberg.eesys.projekt.businessobjects.ChargingType;
import de.unibamberg.eesys.projekt.businessobjects.GeoCoordinate;
import de.unibamberg.eesys.projekt.database.DBImplementation;
import de.unibamberg.eesys.projekt.database.DatabaseException;

/**
 * Class to create new charging stations in the settings menu
 * 
 * @author Julia & Ufuk
 *
 */
public class ChargingStationActivity extends Activity implements
		LocationListener, OnItemSelectedListener {

	Location location;
	protected LocationManager locationManager;
	private static final long UPDATE_TIME = 1000;
	private static final long UPDATE_DISTANCE = 10;
	Context context;

	// Textview to edit the charching station name
	private EditText mEdit;
	// Spinner to show the charching types
	private Spinner spinner;
	// Selected ChargingType
	private ChargingType selectedType = null;
	// Database Connection
	private DBImplementation db;
	private AppContext appContext;

	/**
	 * onCreate method is called when the user selects the create charging
	 * station option in the settings
	 * 
	 * @param savedInstanceStat
	 */

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_change_cs);
		
		// set action bar icon to white car on green background 
		getActionBar().setIcon(R.drawable.drawer_icon);				

		context = this.getApplicationContext();
		LocationManager mlocManager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		LocationListener mlocListener = this;
		mlocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				UPDATE_TIME, UPDATE_DISTANCE, mlocListener);
		appContext = (AppContext) getApplicationContext();
		// set database connection
		db = new DBImplementation(appContext);

		// Set textviews and spinner
		mEdit = (EditText) findViewById(R.id.editText_csName);
		spinner = (Spinner) findViewById(R.id.chargingTypes_Spinner);
		spinner.setOnItemSelectedListener(this);

		// get List of Charging Types to fill the spinner
		List<ChargingType> chargingTypes = new ArrayList<ChargingType>();
		try {
			chargingTypes = db.getChargingTypes();
		} catch (DatabaseException e) {
			Toast.makeText(
					appContext.getApplicationContext(),
					"An unexpected error has occurred.",
					Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
		// List<Object> chargingTypeNames = new ArrayList<Object>();
		if (chargingTypes != null) {
			for (int i = 0; i < chargingTypes.size(); i++) {
				chargingTypes.get(i).toString();
			}
		}
		// Create an ArrayAdapter using the string array and a default spinner
		// layout
		ArrayAdapter<ChargingType> dataAdapter = new ArrayAdapter<ChargingType>(
				this, android.R.layout.simple_spinner_item, chargingTypes);

		// Specify the layout to use when the list of choices appears
		dataAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(dataAdapter);

		// Set the button and click Listener
		final Button button = (Button) findViewById(R.id.cs_save);
		button.setOnClickListener(new OnClickListener() {

			/**
			 * Method is called when the Button is clicked A new charging
			 * station is created and stored in database
			 * 
			 * @param v
			 *            = view in which the button is located
			 */
			public void onClick(View v) {
				appContext = (AppContext) getApplicationContext();

				ChargingStation chargingStation = new ChargingStation();
				chargingStation.setName(mEdit.getText().toString());
				
				// attempt to set GPS coordinates 
				int gpsStatus = appContext.getCurrentGpsStatus();
				GeoCoordinate geoCoordinate = appContext.getCurrentPosition();
				if(geoCoordinate.getLatitude() == 0 && geoCoordinate.getLongitude() == 0 && geoCoordinate.getAltitude() == 0){
					Toast.makeText(context, "GPS singal not available. Charging station cannot be created!",
							Toast.LENGTH_SHORT).show();
				}
				else{
					chargingStation.setGeoCoordinate(geoCoordinate);
					
					// charging type
					if (selectedType != null) {
						chargingStation.setChargingType(selectedType);
						
						// GPS coordinates + charging type ok => store in DB
						try {
							appContext.getDb().storeChargingStation(chargingStation);
						} catch (DatabaseException e) {
							Toast.makeText(context, "Charging station could not be stored in database.",
									Toast.LENGTH_SHORT).show();
							e.printStackTrace();
						}
						Toast.makeText(context, "Charging station created!",Toast.LENGTH_SHORT).show();
						
						finish();
					} else {
						Toast.makeText(context,
								"Please select a charging station type!",
								Toast.LENGTH_SHORT).show();
					}					
				}

			}
		});
	}

	/**
	 * Method is called when user selects an element of the spinner The selected
	 * Item is saved and shown to the user as a toast
	 * 
	 * @param parent
	 *            the parent view of "view"
	 * @param view
	 *            the view in which the spinner is located
	 * @param position
	 *            is the position value of the selected item within the spinner
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		// On selecting a spinner item
		String selected = parent.getItemAtPosition(position).toString();

		this.selectedType = (ChargingType) ((Spinner) findViewById(R.id.chargingTypes_Spinner))
				.getSelectedItem();

	}

	/**
	 * This method is called when the user selects nothing from the spinner
	 * 
	 * @param parent
	 *            the parent view of the view in which the spinner is located
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent) {
		Toast.makeText(parent.getContext(),
				"Please select a charging station type!", Toast.LENGTH_SHORT)
				.show();

	}

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

}
