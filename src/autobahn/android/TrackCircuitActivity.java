package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.autobahn.R;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;


public class TrackCircuitActivity extends Activity {

    List<String> reservationID = new ArrayList<String>();
    ListView reservationList;
    ArrayAdapter<String> adapter;
    AsyncTask<Void, Void, Void> async = new AsyncTask<Void, Void, Void>() {
        @Override
        protected Void doInBackground(Void... type) {

            AutobahnClient.getInstance().fetchTrackCircuit(idmName);
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {
            //TODO: populate with circuits
            showReservations();
        }
    };
    private List<Circuit> circuitList;
    private String idmName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.domain_reservation_activity);
        Bundle extras = getIntent().getExtras();
        idmName = extras.getString("DOMAIN_NAME");
        async.execute();
    }

    public void showReservations() {
        circuitList = AutobahnClient.getInstance().getTrackCircuits();

        setContentView(R.layout.domain_reservation_activity);
        for (Circuit c : circuitList) {
            String route = c.getStartDomain() + " " + c.getStartPort().name() + "-" + c.getEndDomain() + " " + c.getEndPort().name();
            reservationID.add(route);
        }

        reservationList = (ListView) findViewById(R.id.listView);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, reservationID);
        reservationList.setAdapter(adapter);
        reservationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String JSONObject = (new Gson()).toJson(circuitList.get(i));
                Intent singleCircuitActivity = new Intent();
                singleCircuitActivity.setClass(getApplicationContext(), SingleCircuitActivity.class);
                singleCircuitActivity.putExtra("JSON_OBJECT", JSONObject);
                startActivity(singleCircuitActivity);
            }
        });
    }
}