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
public class Login extends Activity {

    Button loginButton;
    EditText usernameField;
    EditText passwordField;
    String username;
    String password;

    public final TextWatcher watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
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

        usernameField.addTextChangedListener(watcher);
        passwordField.addTextChangedListener(watcher);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });


    }

    public void checkFields(){

        username = usernameField.getText().toString();
        password = passwordField.getText().toString();

        if(username.equals("") || password.equals(""))
            loginButton.setEnabled(false);
        else
            loginButton.setEnabled(true);
    }

}