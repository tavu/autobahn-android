package autobahn.android;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import com.example.autobahn.R;

public class mainActivity extends Activity {
    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        Button b = (Button) findViewById(R.id.about);

        LogIn l=new LogIn();
        l.logInToServer();
    }
}
