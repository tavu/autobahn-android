package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.autobahn.R;

import java.io.IOException;
import java.net.URISyntaxException;


/**
 * Created with IntelliJ IDEA.
 * User: Nl0st
 * Date: 22/7/2013
 * Time: 11:41 πμ
 * To change this template use File | Settings | File Templates.
 */
public class Login extends Activity implements  View.OnClickListener {

    Button loginButton;
    EditText usernameField;
    EditText passwordField;
    AutobahnClient client;


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
        //TODO
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

        loginButton.setOnClickListener(this);


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
        String username = usernameField.getText().toString();
        String password = passwordField.getText().toString();

        client.setUserName(username);
        client.setPassword(password);
        AsyncTask<Void, Void, Void> async=new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... type) {

                try {
                    AutobahnClient.getInstance().logIn();
                } catch (URISyntaxException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                } catch (IOException e) {
                    e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
                }
                return null;
            }

            @Override
            protected void onProgressUpdate(Void... progress) {
                super.onProgressUpdate(progress);
            }

            @Override
            protected void onPostExecute(Void result) {
                Intent menuActivity = new Intent();
                menuActivity.setClass(getApplicationContext(),MainMenu.class);
                startActivity(menuActivity);
            }
        };

    }
}