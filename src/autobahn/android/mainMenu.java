package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.autobahn.R;

public class mainMenu extends Activity {
    /**
     * Called when the activity is first created.
     */

    Button button;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        button = (Button) findViewById(R.id.about);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutActivity = new Intent();
                aboutActivity.setClass(getApplicationContext(),AboutActivity.class);
                startActivity(aboutActivity);

            }
        });


        button = (Button) findViewById(R.id.idms);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent idmsActivity = new Intent();
                idmsActivity.setClass(getApplicationContext(),IdmsActivity.class);
                startActivity(idmsActivity);

            }
        });


    }
}
