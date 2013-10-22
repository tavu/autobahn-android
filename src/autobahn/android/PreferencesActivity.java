package autobahn.android;

import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.autobahn.R;

public class PreferencesActivity extends Activity {

	// TODO: add preferences listener on login info change and redirect to login screen

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();
	}

	public static class SettingsFragment extends PreferenceFragment {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from XML resource
			addPreferencesFromResource(R.xml.prefs);
		}
	}
}
