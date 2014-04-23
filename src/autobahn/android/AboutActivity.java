package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.example.autobahn.R;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 7/17/13
 * Time: 3:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class AboutActivity extends Activity {

    public final static Uri GEANT_URL = Uri.parse("http://www.geant.net");
    public final static Uri DANTE_URL = Uri.parse("http://www.dante.net");
    public final static Uri EC_URL = Uri.parse("http://ec.europa.eu");
    public final static Uri BOD_URL = Uri.parse("http://bod.geant.net");
    public final static Uri AUTOBAHN_URL = Uri.parse("http://autobahn.geant.net");
    public final static Uri ISSUES_URL = Uri.parse("https://issues.geant.net/jira/browse/MDSD");

    Intent launchBrowser;
    TextView link;
    ImageButton button;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.about);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("About BoD");
        link = (TextView) findViewById(R.id.link_1);

        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser = new Intent(Intent.ACTION_VIEW,GEANT_URL);
                startActivity(launchBrowser);
            }
        });

        button = (ImageButton) findViewById(R.id.geantLogo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser = new Intent(Intent.ACTION_VIEW,GEANT_URL);
                startActivity(launchBrowser);
            }
        });

        button = (ImageButton) findViewById(R.id.danteLogo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser =  new Intent(Intent.ACTION_VIEW,DANTE_URL);
                startActivity(launchBrowser);
            }
        });

        button = (ImageButton) findViewById(R.id.euLogo);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser = new Intent(Intent.ACTION_VIEW,EC_URL);
                startActivity(launchBrowser);
            }
        });

        link = (TextView) findViewById(R.id.ecLink);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser = new Intent(Intent.ACTION_VIEW,EC_URL);
                startActivity(launchBrowser);
            }
        });

        link = (TextView) findViewById(R.id.link_2);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser = new Intent(Intent.ACTION_VIEW,BOD_URL);
                startActivity(launchBrowser);
            }
        });

        link = (TextView) findViewById(R.id.link_3);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser = new Intent(Intent.ACTION_VIEW,AUTOBAHN_URL);
                startActivity(launchBrowser);
            }
        });


        link = (TextView) findViewById(R.id.link_4);
        link.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchBrowser = new Intent(Intent.ACTION_VIEW,ISSUES_URL);
                startActivity(launchBrowser);
            }
        });

    }

    // Called when an options item is clicked
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.preferences:
                startActivity(new Intent(this, PreferencesActivity.class));
                break;
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}