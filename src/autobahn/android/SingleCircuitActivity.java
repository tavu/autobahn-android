package autobahn.android;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;

/**
 * Created with IntelliJ IDEA.
 * User: Nl0st
 * Date: 31/7/2013
 * Time: 5:43 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SingleCircuitActivity extends BasicActiviy {

    private ReservationInfo reservationInfo;
    private String serviceID;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_reservation_activity);
        Bundle bundle = getIntent().getExtras();
        serviceID = bundle.getString("SERVICE_ID");

        getData(Call.RES_IFO,serviceID);

    }

    protected synchronized void showData(Object data,Call c,String param) {
        showReservationInfo();
    }

    public void showReservationInfo(){

        TextView textView;

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