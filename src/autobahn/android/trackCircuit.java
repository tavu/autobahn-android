package autobahn.android;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 7/17/13
 * Time: 3:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class trackCircuit extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void getDomains() {
        URI url;
        try {
            url = new URI("http", null, "62.217.125.174", 8080, "/autobahn-gui/portal/Login.htm", null, null);
            Log.d("WARN", url.toString());

            HttpPost httppost = new HttpPost(url);
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}