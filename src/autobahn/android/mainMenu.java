package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.example.autobahn.R;

public class MainMenu extends Activity {
    /**
     * Called when the activity is first created.
     */

    Button aboutButton;
    Button trackCircuitButton;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        aboutButton = (Button) findViewById(R.id.about);
        trackCircuitButton = (Button) findViewById(R.id.idms);

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent aboutActivity = new Intent();
                aboutActivity.setClass(getApplicationContext(), AboutActivity.class);
                startActivity(aboutActivity);

            }
        });


        aboutButton = (Button) findViewById(R.id.idms);

        aboutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent idmsActivity = new Intent();
                idmsActivity.setClass(getApplicationContext(),IdmsActivity.class);
                startActivity(idmsActivity);

            }
        });

        trackCircuitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent circuitActivity = new Intent();
                circuitActivity.setClass(getApplicationContext(), TrackCircuitActivity.class);
                startActivity(circuitActivity);
            }
        });


    }
}
