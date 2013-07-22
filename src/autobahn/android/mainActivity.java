package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.autobahn.R;

public class mainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */

    Button aboutButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        aboutButton = (Button) findViewById(R.id.about);
        LogIn l=new LogIn();
        l.logInToServer();

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutActivity = new Intent();
                aboutActivity.setClass(getApplicationContext(),about.class);
                startActivity(aboutActivity);
            }
        });


    }
}
