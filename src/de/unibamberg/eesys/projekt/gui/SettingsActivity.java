package de.unibamberg.eesys.projekt.gui;

import de.unibamberg.eesys.projekt.gui.fragment.SettingsFragment;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.support.v4.app.NavUtils;
import android.support.v4.app.TaskStackBuilder;
import android.view.MenuItem;

/**
 * SettingsActivity for displaying and managing the Settings menu
 * @author Julia
 *
 */
public class SettingsActivity extends PreferenceActivity {

	@Override
	/**
	 * Called when Settings menu is opened
	 * Sets the back navigation button
	 */
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// addPreferencesFromResource(R.xml.preferences);
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		this.getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment()).commit();
	}

	@Override
	/**
	 * Method handles the back navigation 
	 *
	 */
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			// This is called when the Home (Up) button is pressed in the action
			// bar.
			// Create a simple intent that starts the hierarchical parent
			// activity and
			// use NavUtils in the Support Package to ensure proper handling of
			// Up.
			Intent upIntent = new Intent(this, MainActivity.class);
			if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
				// This activity is not part of the application's task, so
				// create a new task
				// with a synthesized back stack.
				TaskStackBuilder.from(this)
				// If there are ancestor activities, they should be added here.
						.addNextIntent(upIntent).startActivities();
				finish();
			} else {
				// This activity is part of the application's task, so simply
				// navigate up to the hierarchical parent activity.
				NavUtils.navigateUpTo(this, upIntent);
			}
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

}
