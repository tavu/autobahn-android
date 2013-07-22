package autobahn.android;

<<<<<<< HEAD
import android.util.Log;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

=======
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import com.example.autobahn.R;
>>>>>>> f36230a8f435fe887fff6c346878a8b682c39b1a

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

<<<<<<< HEAD
    public void logInToServer() {
        // Create a new HttpClient and Post Header
        HttpClient httpclient = new DefaultHttpClient();
         String sid=null;

        /*
        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("j_username", "demoadmin"));
            //nameValuePairs.add(new BasicNameValuePair("j_password", "demoadmin"));

            URI url= new URI("http" , null , "62.217.125.174" ,8080, "/autobahn-gui/portal/login.htm",null,null);

            Log.d("WARN",url.toString());
            HttpGet httppost = new HttpGet(url);
            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            //Log.d("WARN", EntityUtils.toString((response.getEntity())) );
            //Log.d("WARN", response.);

            for (Header h : response.getAllHeaders() ) {
          //      Log.d("COOKIES","name: "+h.getName() + " value: " + h.getValue() );
            }

            Header[] cookies = response.getHeaders("Set-Cookie");
            for (int i = 0; i < cookies.length; i++)
            {
                Log.d("WARN",cookies[i].toString());

                String[] cookie = cookies[i].getValue().split("=");
                if(cookie[0].equals("JSESSIONID"))
                {
                    String id = cookie[1].substring(0,cookie[1].indexOf(";"));
                    Log.d("WARN","ied: " + id );
                    sid=id;
                    break ;
                }
            }
            //String s = response.getFirstHeader("Location").toString();
            //boolean isError = s.contains("login_error");

        } catch (ClientProtocolException e) {
            // TODO Auto-generated catch block
            Log.d("WARN", "1 "+e.getMessage());
        } catch (IOException e) {
            // TODO Auto-generated catch block
            Log.d("WARN", "2 "+e.toString());
        } catch (URISyntaxException e) {
            Log.d("WARN", "3 "+e.getMessage());
        }

*/

        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("j_username", "demoadmin"));
            //nameValuePairs.add(new BasicNameValuePair("j_password", "demoadmin"));
            //String q="j_username=demoadmin&j_password=demoadmin&JSESSIONID="+sid ;
            String q="j_username=demoadmin&j_password=demoadmine" ;
            URI url= new URI("http" , null , "62.217.125.174" ,8080, "/autobahn-gui/j_spring_security_check",q,null);

            Log.d("WARN",url.toString());
            HttpPost httppost = new HttpPost(url);
            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httppost);
            //Log.d("WARN", EntityUtils.toString((response.getEntity())) );
            //Log.d("WARN", response.);

            for (Header h : response.getAllHeaders() ) {
                Log.d("COOKIES","name: "+h.getName() + " value: " + h.getValue() );
            }

            //String s = response.getFirstHeader("Location").toString();
            //boolean isError = s.contains("login_error");
=======
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
>>>>>>> f36230a8f435fe887fff6c346878a8b682c39b1a

    public void checkFields(){


    }





<<<<<<< HEAD





        }
        Log.d("WARN","id:" + sid);


        try {
            // Add your data
            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
            //nameValuePairs.add(new BasicNameValuePair("j_username", "demoadmin"));
            //nameValuePairs.add(new BasicNameValuePair("j_password", "demoadmin"));
          //  URI url= new URI("http" , null , "62.217.125.174" ,8080, "/autobahn-gui/portal/secure/myprofile.htm",null,null);
            String q="JSESSIONID="+sid;
            URI url= new URI("http" , null , "62.217.125.174" ,8080, "/autobahn-gui/portal/secure/request-service.htm",null,null);
            HttpGet httpget = new HttpGet(url);
            //httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

            // Execute HTTP Post Request
            HttpResponse response = httpclient.execute(httpget);
            Log.d("WARN", EntityUtils.toString((response.getEntity())) );

            for (Header h : response.getAllHeaders()) {
             //   Log.d("COOKIES","name: "+h.getName() + " value: " + h.getValue() );
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }


    }
=======
>>>>>>> f36230a8f435fe887fff6c346878a8b682c39b1a
}