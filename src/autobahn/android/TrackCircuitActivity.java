package autobahn.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import com.example.autobahn.R;

import java.util.ArrayList;
import java.util.List;


public class TrackCircuitActivity extends BasicActivity {

    private List<String> reservationID = new ArrayList<String>();
    private ListView reservationList;
    private ArrayAdapter<String> adapter;
    private AutobahnClientException exception = null;
    private String domainName;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        domainName = extras.getString("DOMAIN_NAME");

        getData(Call.RESERV,domainName);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        showReservations(NetCache.getInstance().getTrackCircuits(domainName));
    }

    @Override
    protected synchronized void showData(Object data,Call c,Object param) {
        showReservations((List<String>)data );
    }

    public void showReservations(List<String> reservationID) {

        if (reservationID.isEmpty()) {
            setContentView(R.layout.no_data);
            ((TextView)findViewById(R.id.header)).setText(R.string.noReservations);

            Button button = (Button) findViewById(R.id.menuButton);
            button.setText(R.string.backToDomains);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            setContentView(R.layout.domain_reservation_list);

            ((TextView) findViewById(R.id.header)).setText("Past reservations for domain " + domainName);

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