package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.autobahn.R;

import java.util.ArrayList;
import java.util.List;


public class TrackCircuitActivity extends Activity {

    private List<String> reservationID = new ArrayList<String>();
    private ListView reservationList;
    private ArrayAdapter<String> adapter;
    private AutobahnClientException exception = null;
    private String domainName;

    private class TrackCircuitTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... type) {
            exception = null;
            try {
                AutobahnClient.getInstance().fetchTrackCircuit(domainName);
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
    }

    ;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        domainName = extras.getString("DOMAIN_NAME");

        if (!AutobahnClient.getInstance().hasAuthenticate()) {
            Intent logInIntent = new Intent();
            logInIntent.setClass(getApplicationContext(), LoginActivity.class);
            logInIntent.putExtra(LoginActivity.BACK, true);
            startActivityForResult(logInIntent, LoginActivity.LOGIN_AND_GO_BACK);
        } else {
            TrackCircuitTask async = new TrackCircuitTask();
            async.execute();
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (LoginActivity.LOGIN_AND_GO_BACK == requestCode && resultCode == RESULT_OK) {
            TrackCircuitTask async = new TrackCircuitTask();
            async.execute();
        }
    }


    public void showReservations() {

        TextView header;

        reservationID = NetCache.getInstance().getTrackCircuits(domainName);

        if (exception != null) {
            Log.d("WARN", "circuit error");
            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        if (reservationID == null) {
            //TODO
            return;
        }

        if (reservationID.isEmpty()) {
            setContentView(R.layout.no_data);
            header = (TextView) findViewById(R.id.header);
            header.setText(R.string.noReservations);
            Button button = (Button) findViewById(R.id.menuButton);
            button.setText(R.string.backToDomains);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent prevActivity = new Intent();
                    prevActivity.setClass(getApplicationContext(), IdmsActivity.class);
                    startActivity(prevActivity);
                    finish();
                }
            });
        } else {
            setContentView(R.layout.domain_reservation_list);

            header = (TextView) findViewById(R.id.header);
            header.setText("Past reservations for domain " + domainName);

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
                    singleCircuitActivity.putExtra("DOMAIN_NAME", domainName);
                    startActivity(singleCircuitActivity);
                }
            });
        }

    }

}