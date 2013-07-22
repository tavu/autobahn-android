package autobahn.android;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.autobahn.R;

/**
 * Created with IntelliJ IDEA.
 * User: Nl0st
 * Date: 22/7/2013
 * Time: 11:41 πμ
 * To change this template use File | Settings | File Templates.
 */
public class login extends Activity {

    //LoginHandler l = new LoginHandler();
    //LologInToServer();
    Button loginButton;
    EditText usernameField;
    EditText passwordField;

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

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login);

        loginButton = (Button) findViewById(R.id.loginButton);
        usernameField = (EditText) findViewById(R.id.username);
        passwordField = (EditText) findViewById(R.id.password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


    }

    public void checkFields(){


    }





}