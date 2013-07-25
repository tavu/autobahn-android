package autobahn.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import com.example.autobahn.R;

import java.util.List;


public class TrackCircuitActivity extends Activity  {

    private List<Circuit> circuitList;

    //TODO to be implemented
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.circuits_display);

        Bundle extras = getIntent().getExtras();
        final String idmName = extras.getString("currentIdm");

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
                circuitList = AutobahnClient.getInstance().getTrackCircuits();
            }
        };
    }


}