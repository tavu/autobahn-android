package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.example.autobahn.R;


public class LoginActivity extends Activity implements  View.OnClickListener {

    Button loginButton;
    EditText usernameField;
    EditText passwordField;
    AutobahnClient client;
    boolean goBack=false;
    AutobahnClientException exception=null;

    public static final String BACK="COME_BACK";
    public static final int LOGIN_AND_GO_BACK=1;


    private class LoginTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... type) {
            exception=null;
            try{
                AutobahnClient.getInstance().logIn();
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
            afterLogIn();
        }
    };


    private void afterLogIn() {

        if(exception!=null) {
            Toast toast = Toast.makeText(this, exception.getMessage(), Toast.LENGTH_LONG);
            loginButton.setEnabled(true);
            toast.show();
            return ;
    }

    if(goBack ) {
        setResult(RESULT_OK);
        finish();
    } else{
        Intent menuActivity = new Intent();
        menuActivity.setClass(getApplicationContext(),MainMenu.class);
        startActivity(menuActivity);
    }

}

    public final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

        @Override
        public void afterTextChanged(Editable editable) {
            checkFields();
        }
    };

    /*
        stores at client the host and the port
     */
    private void initClient() {
        client=AutobahnClient.getInstance();
        client.setContext(this);

    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initClient();
        setContentView(R.layout.login);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);

        usernameField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);

        usernameField.getText().append(prefs.getString("username","") );
        passwordField.getText().append(prefs.getString("password","") );


        loginButton.setOnClickListener(this);

        Bundle bundle = getIntent().getExtras();

        if(bundle!=null && bundle.getBoolean(BACK,false) ) {
            goBack=true;
            Toast toast = Toast.makeText(this, getString(R.string.LogInFirst), Toast.LENGTH_LONG);
            loginButton.setEnabled(true);
            toast.show();
        }


    }

    public void checkFields(){

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        if(username.equals("") || password.equals(""))
            loginButton.setEnabled(false);
        else
            loginButton.setEnabled(true);
    }


    public void onClick(View view) {
        loginButton.setEnabled(false);

        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        client.setUserName(username);
        client.setPassword(password);

        LoginTask task=new LoginTask();
        task.execute();
    }
}