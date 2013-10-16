package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.autobahn.R;

import java.util.ArrayList;
import java.util.List;


public class TrackCircuitActivity extends Activity {

	List<String> reservationID = new ArrayList<String>();
	ListView reservationList;
	ArrayAdapter<String> adapter;
	AutobahnClientException exception = null;
	String idmName;

	AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>() {
		@Override
		protected Void doInBackground(Void... type) {
			try {
				AutobahnClient.getInstance().fetchTrackCircuit(idmName);
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
			showReservations();
		}
	};

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.domain_reservation_activity);
		Bundle extras = getIntent().getExtras();
		idmName = extras.getString("DOMAIN_NAME");
		async.execute();
	}

	public void showReservations() {

        TextView header;
        if (exception != null) {
			Log.d("WARN", "circuit error");
			Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		reservationID = AutobahnClient.getInstance().getTrackCircuits();
		setContentView(R.layout.domain_reservation_activity);

        header = (TextView)findViewById(R.id.header);
        header.setText("Past reservations for " + idmName);

		reservationList = (ListView) findViewById(R.id.listView);
		adapter = new ArrayAdapter<String>(this, R.layout.list_item, reservationID);
		reservationList.setAdapter(adapter);
		reservationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
				TextView item = (TextView) view;
				String serviceID = item.getText().toString();
				Intent singleCircuitActivity = new Intent();
				singleCircuitActivity.setClass(getApplicationContext(), SingleCircuitActivity.class);
				singleCircuitActivity.putExtra("SERVICE_ID", serviceID);
				singleCircuitActivity.putExtra("DOMAIN_NAME", idmName);
				startActivity(singleCircuitActivity);
			}
		});
	}
}