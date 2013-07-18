package autobahn.android;

import android.util.Log;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;


/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 7/18/13
 * Time: 2:08 PM
 * To change this template use File | Settings | File Templates.
 */
public class LogIn {

    public LogIn() {

    }

    public void logInToServer() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();


        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            nameValuePairs.add(new BasicNameValuePair("j_username", "demoadmin"));
            nameValuePairs.add(new BasicNameValuePair("j_password", "demoadmin"));
            URI url= new URI("http" , null , "62.217.125.174" ,8080, "/autobahn-gui/portal/login.htm",null,null);
            Log.d("WARN", url.toString());

            HttpPost httppost = new HttpPost(url);
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));


            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            Log.d("WARN", response.toString());
        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Log.d("WARN", "1 "+e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("WARN", "2 "+e.toString());
        } catch (URISyntaxException e) {
            Log.d("WARN", "3 "+e.getMessage());
        }
    }


}
