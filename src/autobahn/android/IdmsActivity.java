package autobahn.android;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import com.example.autobahn.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class IdmsActivity extends BasicActivity {

    private Map<String, ArrayList<String>> domains;
    private ListView domainList;
    private ArrayAdapter<String> adapter;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "Creating Domain Selection Activity...");

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Domain Selection");
        getData(Call.DOMAINS,null);
    }

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        showData();
    }

    private void showData() {

        domains = NetCache.getInstance().getIdms();

        if (domains == null) {
            return;
        }

        if (domains.isEmpty()) {
            setContentView(R.layout.no_data);
            ((TextView) findViewById(R.id.header)).setText(R.string.no_domains);

            Button menuButton = (Button) findViewById(R.id.menuButton);
            menuButton.setText(R.string.back_to_menu);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            setContentView(R.layout.domain_reservation_list);
           ((TextView) findViewById(R.id.header)).setText(R.string.idm_title);;

            domainList = (ListView) findViewById(R.id.listView);
            adapter = new ArrayAdapter<String>(this, R.layout.list_item, domains.get("name"));

            domainList.setAdapter(adapter);
            domainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView item = (TextView) view;
                    String domain = item.getText().toString();
                    Intent domainActivity = new Intent();
                    domainActivity.setClass(getApplicationContext(), TrackCircuitActivity.class);
                    domainActivity.putExtra("DOMAIN_NAME", domain);
                    startActivity(domainActivity);
                }
            });
        }
    }

    @Override
    protected synchronized void showData(Object data,Call c,Object param) {
        showData();
    }
}