package autobahn.android;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.autobahn.R;

public class PreferencesActivity extends Activity {

	public static final String USERNAME_PREFERENCE_KEY = "username";
	public static final String PASSWORD_PREFERENCE_KEY = "password";
	public static final String HOST_PREFERENCE_KEY = "url";

	// TODO: add preferences listener on login info change and redirect to login screen

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Display the fragment as the main content.
		getFragmentManager().beginTransaction()
				.replace(android.R.id.content, new SettingsFragment())
				.commit();
	}

	public static class SettingsFragment extends PreferenceFragment implements SharedPreferences.OnSharedPreferenceChangeListener {
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);

			// Load the preferences from XML resource
			addPreferencesFromResource(R.xml.prefs);
			findPreference(USERNAME_PREFERENCE_KEY).setSummary(getPreferenceManager().getSharedPreferences().getString(USERNAME_PREFERENCE_KEY, ""));
			findPreference(PASSWORD_PREFERENCE_KEY).setSummary(maskPassword(getPreferenceManager().getSharedPreferences().getString(PASSWORD_PREFERENCE_KEY, "")));
			findPreference(HOST_PREFERENCE_KEY).setSummary(getPreferenceManager().getSharedPreferences().getString(HOST_PREFERENCE_KEY, ""));
		}

		@Override
		public void onResume() {
			super.onResume();
			getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
		}

		@Override
		public void onPause() {
			super.onPause();
			getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
		}

		/**
		 * Called when a shared preference is changed, added, or removed. This
		 * may be called even if a preference is set to its existing value.
		 * <p/>
		 * <p>This callback will be run on your main thread.
		 *
		 * @param sharedPreferences The {@link android.content.SharedPreferences} that received
		 *                          the change.
		 * @param key               The key of the preference that was changed, added, or
		 */
		@Override
		public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
			switch (key) {
				case USERNAME_PREFERENCE_KEY:
					findPreference(USERNAME_PREFERENCE_KEY).setSummary(sharedPreferences.getString(USERNAME_PREFERENCE_KEY, ""));
					break;
				case PASSWORD_PREFERENCE_KEY:
					findPreference(PASSWORD_PREFERENCE_KEY).setSummary(maskPassword(sharedPreferences.getString(PASSWORD_PREFERENCE_KEY, "")));
					break;
				case HOST_PREFERENCE_KEY:
					findPreference(HOST_PREFERENCE_KEY).setSummary(sharedPreferences.getString(HOST_PREFERENCE_KEY, ""));
					break;
			}
		}

		private StringBuilder maskPassword(String password) {
			StringBuilder maskedPassword = new StringBuilder();
			;
			if (!password.isEmpty()) {
				for (int i = 0; i < password.length(); i++) {
					maskedPassword.append("*");
				}
			}

			return maskedPassword;
		}
	}
}
