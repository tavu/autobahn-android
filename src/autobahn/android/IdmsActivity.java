package autobahn.android;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Toast;
import com.example.autobahn.R;

import java.util.List;


public class IdmsActivity extends Activity {

    AutobahnClient autobahnClient = AutobahnClient.getInstance();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.idmSelectionActivity);




        AsyncTask<Void, Void, Void> async=new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... type) {

                AutobahnClient.getInstance().fetchIdms();
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... progress) {
                super.onProgressUpdate(progress);
            }

            @Override
            protected void onPostExecute(Void result) {
                //TODO show data
            }
        };
    }
}