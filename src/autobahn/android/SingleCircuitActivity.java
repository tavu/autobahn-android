package autobahn.android;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;

import java.util.ArrayList;
import java.util.Date;

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
    private String domainName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_reservation_activity);
        Bundle bundle = getIntent().getExtras();
        serviceID = bundle.getString("SERVICE_ID");
        domainName = bundle.getString("DOMAIN_NAME");

        getData(Call.RES_IFO,serviceID);

    }

    @Override
    protected synchronized void showData(Object data,Call c,Object param) {
        showReservationInfo();
    }

    @Override
    protected void showError(AutobahnClientException e,Call c,Object param) {
        Toast toast = Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG);
        toast.show();
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
        ((TextView)findViewById(R.id.capacity)).setText(String.valueOf(reservationInfo.getCapacity()/1000000));
        ((TextView)findViewById(R.id.mtuSize)).setText(String.valueOf(reservationInfo.getMtu()));
    }

    public void resubmitService(View v){
        Intent resubmitServiceIntent = new Intent();
        resubmitServiceIntent.setClass(getApplicationContext(),RequestActivity.class);
        resubmitServiceIntent.putExtra("RESUBMIT_SERVICE",reservationInfo);
        startActivity(resubmitServiceIntent);
    }

    public void provision(View v) {
        String serviceId=((TextView)findViewById(R.id.service)).getText().toString();
        ArrayList<String> l=new ArrayList<>();
        l.add(domainName);
        l.add(serviceId);
        postData(Call.PROVISION, l);
    }

    @Override
    protected synchronized void postSucceed(Call c,Object param) {
        if(c==Call.PROVISION) {
            Toast toast = Toast.makeText(this, getString( R.string.provision_succeeded), Toast.LENGTH_LONG);
            toast.show();
        }
    }
}