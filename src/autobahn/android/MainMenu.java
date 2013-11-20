package autobahn.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.autobahn.R;

public class MainMenu extends Activity implements  View.OnClickListener {
	/**
	 * Called when the activity is first created.
	**/

    private AutobahnClient client;
    private AutobahnClientException exception = null;
    private ProgressDialog progressDialog=null;

    @Override
	public void onCreate(Bundle savedInstanceState) {

        startupCheck();
		super.onCreate(savedInstanceState);

        Button button;

        setContentView(R.layout.main);

		button = (Button) findViewById(R.id.about);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent aboutActivity = new Intent();
				aboutActivity.setClass(getApplicationContext(), AboutActivity.class);
				startActivity(aboutActivity);

			}
		});

		button = (Button) findViewById(R.id.idms);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent idmsActivity = new Intent();
				idmsActivity.setClass(getApplicationContext(), IdmsActivity.class);
				startActivity(idmsActivity);

			}
		});

		button = (Button) findViewById(R.id.request);

		button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				Intent requestActivity = new Intent();
				requestActivity.setClass(getApplicationContext(), RequestActivity.class);
				startActivity(requestActivity);

			}
		});


        button = (Button) findViewById(R.id.logOut);

        button.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.action_bar, menu);

		return true;
	}

	// Called when an options item is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.preferences:
				startActivity(new Intent(this, PreferencesActivity.class));
				break;
		}
		return true;
	}


    /*
        Stores the host and the port in the Autobahn Client Instance
     */
    private void initClient() {
        client = AutobahnClient.getInstance();
        client.setContext(this);

    }

    private void startupCheck(){
        initClient();
        if(!AutobahnClient.getInstance().hasAuthenticate()){
            Intent logInActivity = new Intent();
            logInActivity.setClass(getApplicationContext(),LoginActivity.class);
            startActivityForResult(logInActivity,LoginActivity.LOGIN_AND_GO_BACK);
        }

    }


    public void onClick(View view) {
        if(view==findViewById(R.id.logOut))
        {
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage( getString(R.string.loading) );
            progressDialog.show();
            LogOutTask task=new LogOutTask();
            task.execute();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setContentView(R.layout.main);
    }

    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data)
    {
        if (resultCode == RESULT_CANCELED)
            finish();
    }


    private class LogOutTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... type) {
            exception = null;
            try {
                AutobahnClient.getInstance().logOut();
            } catch (AutobahnClientException e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {

            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog=null;
            }

            Intent logInActivity = new Intent();
            logInActivity.setClass(getApplicationContext(),LoginActivity.class);
            logInActivity.putExtra(LoginActivity.MSG, getString(R.string.log_out_msg));
            startActivityForResult(logInActivity, LoginActivity.LOGIN_AND_GO_BACK);
        }
    }

}
