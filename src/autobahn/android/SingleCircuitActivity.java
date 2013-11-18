package autobahn.android;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

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

        reservationInfo = NetCache.getInstance().getLastReservation(serviceID);
        if(reservationInfo==null) {
            Log.wtf("WARN", "null res info");
        }
        ((TextView)findViewById(R.id.service)).setText(reservationInfo.getId());
        ((TextView)findViewById(R.id.description)).setText(reservationInfo.getDescription());
        ((TextView)findViewById(R.id.reservationState)).setText(reservationInfo.getReservationState());
        ((TextView)findViewById(R.id.provisionState)).setText(reservationInfo.getProvisionState());
        ((TextView)findViewById(R.id.lifecycleState)).setText(reservationInfo.getLifecycleState());
        ((TextView)findViewById(R.id.startTime)).setText(new Date(reservationInfo.getStartTime()).toGMTString());
        ((TextView)findViewById(R.id.endTime)).setText(new Date(reservationInfo.getEndTime()).toGMTString());
        ((TextView)findViewById(R.id.endPort)).setText(reservationInfo.getEndPort());
        ((TextView)findViewById(R.id.startPort)).setText(reservationInfo.getStartPort());
        ((TextView)findViewById(R.id.startVlan)).setText(String.valueOf(reservationInfo.getStartVlan()));
        ((TextView)findViewById(R.id.endVlan)).setText(String.valueOf(reservationInfo.getEndVlan()));
        ((TextView)findViewById(R.id.capacity)).setText(String.valueOf(reservationInfo.getCapacity()));
        ((TextView)findViewById(R.id.mtuSize)).setText(String.valueOf(reservationInfo.getMtu()));

    }
}