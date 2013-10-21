package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.autobahn.R;

import java.util.List;


public class IdmsActivity extends Activity {

    List<String> domains;
    ListView domainList;
    ArrayAdapter<String> adapter;
    AutobahnClientException exception=null;

    private class idmTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... type) {

            try {
                AutobahnClient.getInstance().fetchIdms();

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
            Log.d("WARN","Done Fetching Domains!");
            showData();
        }
    };

    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        showData();
    }

    private void showData() {

        if(exception != null) {
            Toast toast  = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
            toast.show();
        }

        domains = NetCache.getInstance().getIdms();

        if(domains==null) {
            //TODO
            return ;
        }

        if (domains.isEmpty()) {
            setContentView(R.layout.no_domains);
            Button menuButton = (Button) findViewById(R.id.menuButton);
            menuButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent menu = new Intent();
                    menu.setClass(getApplicationContext(), MainMenu.class);
                    startActivity(menu);
                    finish();
                }
            });
        } else {
            setContentView(R.layout.idm_selection_activity);
            domainList = (ListView) findViewById(R.id.listView);
            adapter = new ArrayAdapter<String>(this, R.layout.list_item, domains);

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("create2","i have created");

        exception=null;
        if(NetCache.getInstance().getIdms() == null) {
            if(!AutobahnClient.getInstance().hasAuthenticate()) {
                Log.d("IDMS","not auth");
                Intent logInIntent = new Intent();
                logInIntent.setClass(getApplicationContext(), LoginActivity.class);
                Log.d("IDMS","edo2");
                logInIntent.putExtra(LoginActivity.BACK, true);
                startActivityForResult(logInIntent,LoginActivity.LOGIN_AND_GO_BACK);
            }
            else {
                Log.d("IDMS","edo");
                idmTask async=new idmTask();
                async.execute();
            }
        } else {
            showData();
        }



    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Log.d("IDMS", " "+resultCode+" "+RESULT_OK);
        if(LoginActivity.LOGIN_AND_GO_BACK==requestCode && resultCode==RESULT_OK) {
            idmTask async=new idmTask();
            async.execute();
        }
    }
}