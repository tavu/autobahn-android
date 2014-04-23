package autobahn.android;

import android.app.AlertDialog;
import android.content.DialogInterface;
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
public class SingleCircuitActivity extends BasicActivity {

    private ReservationInfo reservationInfo;
    private String serviceID;
    private String domainName;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.single_reservation_activity);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Reservation Info");
        Bundle bundle = getIntent().getExtras();
        serviceID = bundle.getString("SERVICE_ID");
        domainName = bundle.getString("DOMAIN_NAME");
        ArrayList<String> parameters = new ArrayList<>();
        parameters.add(serviceID);
        parameters.add(domainName);
        getData(Call.RES_IFO,parameters);

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

        ArrayList<String> info = new ArrayList<>();
        info.add(serviceID);
        info.add(domainName);
        reservationInfo = NetCache.getInstance().getLastReservation(info);
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
        } else if(c==Call.CANCEL_REQ) {
            Toast toast = Toast.makeText(this, getString( R.string.cancel_succeeded), Toast.LENGTH_LONG);
            toast.show();
        }
    }

    public void cancelService(View v) {
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle( getString( R.string.cancel_service));
        myAlertDialog.setMessage(getString( R.string.cancel_question));

        myAlertDialog.setNegativeButton(getString( R.string.no), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                //do nothing
            }
        });

        myAlertDialog.setPositiveButton(getString( R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                String serviceId=((TextView)findViewById(R.id.service)).getText().toString();
                ArrayList<String> l=new ArrayList<>();
                l.add(domainName);
                l.add(serviceId);
                postData(Call.CANCEL_REQ, l);
            }
        });

        myAlertDialog.show();
    }

    public void modifyService(View v) {
       //TODO after server implementation
    }
}