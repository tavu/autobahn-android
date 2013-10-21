package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Nl0st
 * Date: 31/7/2013
 * Time: 5:43 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SingleCircuitActivity extends Activity {

    private ReservationInfo reservationInfo;
    private String serviceID;
    private AutobahnClientException exception;

    private class CircuitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... type) {
            try{
                AutobahnClient.getInstance().fetchReservationInfo(serviceID);
            } catch (AutobahnClientException e) {
                exception=e;
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
        setContentView(R.layout.single_reservation_activity);
        Bundle bundle = getIntent().getExtras();
        serviceID = bundle.getString("SERVICE_ID");


        exception=null;
        if(NetCache.getInstance().getTrackCircuits(serviceID)==null) {
            if(!AutobahnClient.getInstance().hasAuthenticate()) {
                Intent logInIntent = new Intent();
                logInIntent.setClass(getApplicationContext(), LoginActivity.class);
                logInIntent.putExtra(LoginActivity.BACK, true);
                startActivityForResult(logInIntent,LoginActivity.LOGIN_AND_GO_BACK);
            } else {
                CircuitTask async=new CircuitTask();
                async.execute();
            }
        } else {
            showReservationInfo();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(LoginActivity.LOGIN_AND_GO_BACK==requestCode && resultCode==RESULT_OK) {
            CircuitTask async=new CircuitTask();
            async.execute();
        }
    }

    public void showReservationInfo(){

        TextView textView;

        if (exception != null) {

            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        reservationInfo = NetCache.getInstance().getLastReservation(serviceID);
        if(reservationInfo==null) {
            Log.wtf("WARN", "null res info");
        }
        textView = (TextView)findViewById(R.id.service);
        textView.setText(reservationInfo.getId());
        textView = (TextView)findViewById(R.id.description);
        textView.setText(reservationInfo.getDescription());
        textView = (TextView)findViewById(R.id.reservationState);
        textView.setText(reservationInfo.getReservationState());
        textView = (TextView)findViewById(R.id.provisionState);
        textView.setText(reservationInfo.getProvisionState());
        textView = (TextView)findViewById(R.id.lifecycleState);
        textView.setText(reservationInfo.getLifecycleState());
        /*textView = (TextView)findViewById(R.id.startTime);
        textView.setText(reservationInfo.getStartTime());
        textView = (TextView)findViewById(R.id.endTime);
        textView.setText(reservationInfo.getEndTime());*/
        textView = (TextView)findViewById(R.id.endPort);
        textView.setText(reservationInfo.getEndPort());
        textView = (TextView)findViewById(R.id.startPort);
        textView.setText(reservationInfo.getStartPort());
        textView = (TextView)findViewById(R.id.startVlan);
        textView.setText(String.valueOf(reservationInfo.getStartVlan()));
        textView = (TextView)findViewById(R.id.endVlan);
        textView.setText(String.valueOf(reservationInfo.getEndVlan()));
        textView = (TextView)findViewById(R.id.capacity);
        textView.setText(String.valueOf(reservationInfo.getCapacity()));
        textView = (TextView)findViewById(R.id.mtuSize);
        textView.setText(String.valueOf(reservationInfo.getMtu()));
        /*textView = (TextView)findViewById(R.id.endVlan);
        textView.setText(reservationInfo.getEndVlan()); */



    }
}