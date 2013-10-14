package autobahn.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.autobahn.R;

/**
 * Created with IntelliJ IDEA.
 * User: Nl0st
 * Date: 31/7/2013
 * Time: 5:43 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SingleCircuitActivity extends Activity {

	private Circuit circuit;
	private String currentIdm;
	private String serviceID;
	private AutobahnClientException exception;

	private AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>() {
		@Override
		protected Void doInBackground(Void... type) {
			try {
				AutobahnClient.getInstance().fetchReservationInfo(currentIdm, serviceID);
			} catch (AutobahnClientException e) {
				exception = e;
			}
			return null;
		}

		@Override
		protected void onProgressUpdate(Void... progress) {
			super.onProgressUpdate(progress);
		}

		@Override
		protected void onPostExecute(Void result) {
			showReservationInfo();
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.single_circuit_activity);
		async.execute();

	}

	public void showReservationInfo() {

		if (exception != null) {
			Log.d("WARN", "circuit error");
			Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		circuit = AutobahnClient.getInstance().getReservationInfo();


	}
}