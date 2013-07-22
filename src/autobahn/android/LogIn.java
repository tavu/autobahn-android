package autobahn.android;

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
}