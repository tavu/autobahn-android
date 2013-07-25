package autobahn.android;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import com.example.autobahn.R;

import java.util.List;


public class IdmsActivity extends Activity {

    AutobahnClient autobahnClient = AutobahnClient.getInstance();
    List<String> domains;
    ListView domainList;
    ArrayAdapter<String> adapter;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        autobahnClient.fetchIdms();
        domains = autobahnClient.getIdms();

        if(domains.isEmpty()){
            setContentView(R.layout.no_domains);
            Button menuButton = (Button) findViewById(R.id.menuButton);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent menu = new Intent();
                    menu.setClass(getApplicationContext(),mainMenu.class);
                    startActivity(menu);
                    finish();
                }
            });
        }

        setContentView(R.layout.idm_selection_activity);
        domainList = (ListView) findViewById(R.id.list);
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,domains);
        domainList.setAdapter(adapter);

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