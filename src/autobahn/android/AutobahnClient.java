package autobahn.android;

import android.content.Context;
import android.util.Log;
import com.example.autobahn.R;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.loopj.android.http.PersistentCookieStore;
import net.geant.autobahn.android.ReservationInfo;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

public class AutobahnClient {

    private static String LOGIN_URL = "/autobahn-gui/j_spring_security_check";
    private static String DOMAIN_URL = "/autobahn-gui/portal/secure/android/idms";
    private static String SERVICES_URL = "/autobahn-gui/portal/secure/android/services";
    private static String SERVICE_URL = "/autobahn-gui/portal/secure/android/service";
    private static String PORTS_URL = "/autobahn-gui/portal/secure/android/ports";

    private HttpClient httpclient;
    private HttpGet httpget;
    private String scheme;
    private String host;
    private int port;
    //boolean isLogIn;
    private String userName;
    private String password;
    private HttpContext localContext;
    private Context context = null;

    private List<String> idms = new ArrayList();
    private List<String> circuits = new ArrayList();

    private Map<String, List<String>> domainsPorts =new HashMap<String, List<String>>();

    private ReservationInfo reservationInfo;


    private String TAG = "CLIENT";

    static AutobahnClient instance = null;

    public static AutobahnClient getInstance() {
        if (instance == null)
            instance = new AutobahnClient();

        return instance;
    }

    public AutobahnClient() {
        httpclient = new DefaultHttpClient();
        scheme = "http";
        //CookieStore cookieStore = new BasicCookieStore();

        //TODO get the host from a property
        host = "62.217.125.174";
        port = 8080;
    }

    public void setContext(Context context) {
        this.context = context;
        CookieStore cookieStore = new PersistentCookieStore(context);
        localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public void setHost(String s) {
        host = s;
    }

    public void setPort(int p) {
        port = p;
    }

    public void setUserName(String s) {
        userName = s;
    }

    public String getUserName() {
        return userName;
    }

    public void setPassword(String pass) {
        password = pass;
    }

    public String getPassword() {
        return password;
    }

    public boolean hasAuthenticate() {
        CookieStore cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
        for (Cookie c : cookieStore.getCookies()) {
            Log.d(TAG,c.toString());
            if (c.getName().equals("SPRING_SECURITY_REMEMBER_ME_COOKIE") && !c.isExpired(new Date() ))  {
                return false;
            }
        }
        return false;
    }

    public void clearData() {
        idms.clear();
        circuits.clear();
        reservationInfo=null;
        domainsPorts.clear();
    }

    public synchronized void logIn() throws AutobahnClientException {

        if(hasAuthenticate()) {
            Log.d(TAG,"Autobahn client has already authenticate");
            //return ;
        }

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("j_username",userName));
        params.add(new BasicNameValuePair("j_password",password));
        params.add(new BasicNameValuePair("_spring_security_remember_me","true"));
        String query = URLEncodedUtils.format(params, "utf-8");

        //String query = "j_username=" + userName + "&j_password=" + password + "&_spring_security_remember_me=true";
        URI url = null;
        HttpPost httppost = null;
        HttpResponse response=null;
        try {
            url = new URI(scheme, null, host, port, LOGIN_URL, query, null);
            httppost = new HttpPost(url);
            response = httpclient.execute(httppost, localContext);

        } catch (URISyntaxException e) {
            String error = e.getMessage();
            Log.d(TAG,error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        } catch (ClientProtocolException e) {
            String error = e.getMessage();
            Log.d(TAG,error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        } catch (IOException e) {
            String error = e.getMessage();
            Log.d(TAG,error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        int status=response.getStatusLine().getStatusCode();


        if(status == 200 ) {

            if (!hasAuthenticate() && false) {
                String error = context.getString(R.string.login_failed);
                AutobahnClientException ex = new AutobahnClientException(error);
                throw ex;
            }
        } else if(status==404) {
            String error = context.getString(R.string.error_404);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        } else if(status==500) {
            String error = context.getString(R.string.error_404);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        } else {
            String error = context.getString(R.string.error);
            AutobahnClientException ex = new AutobahnClientException(error+status);
            throw ex;
        }
    }


    private String handleGetRequest(URI url) throws AutobahnClientException{

        httpget = new HttpGet(url);
        HttpResponse response=null;
        try{
            response = httpclient.execute(httpget,localContext);
        } catch (ClientProtocolException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        } catch (IOException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        String json=null;
        int status=response.getStatusLine().getStatusCode();
        if(status == 200 ) {
            try {
                json = EntityUtils.toString(response.getEntity());
            } catch (IOException e) {
                String errorStr = context.getString(R.string.net_error);
                AutobahnClientException ex = new AutobahnClientException(errorStr);
            }

            Log.d(TAG,json);

            if(json==null) {
                String errorStr = context.getString(R.string.net_error);
                AutobahnClientException ex = new AutobahnClientException(errorStr);
                throw ex;
            }

            return json;

        } else if(status==404) {
            String error = context.getString(R.string.error_404);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        } else if(status==500) {
            String error = context.getString(R.string.error_404);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        } else {
            String error = context.getString(R.string.error);
            AutobahnClientException ex = new AutobahnClientException(error+status);
            throw ex;
        }


    }

    public List<String> getTrackCircuits() {
        return circuits;
    }

    public synchronized void fetchTrackCircuit(String domain) throws AutobahnClientException{

        circuits.clear();
        URI url=null;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("currentIdm",domain));
        String query = URLEncodedUtils.format(params, "utf-8");

        try{

            url = new URI(scheme, null, host, port, SERVICES_URL, query, null);
        }
        catch (URISyntaxException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        String json=handleGetRequest(url);

        Gson gson = new Gson();
        try {
            circuits = gson.fromJson(json, circuits.getClass());
        } catch (JsonParseException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }
    }

    public synchronized void fetchIdms() throws AutobahnClientException {

        idms.clear();
        URI url=null;
        try {
            url = new URI(scheme, null, host, port, DOMAIN_URL, null, null);
        } catch (URISyntaxException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        String json=handleGetRequest(url);

        Gson gson = new Gson();
        ArrayList<String> l = new ArrayList<String>();

        try {
            l = gson.fromJson(json, l.getClass());
        } catch (JsonParseException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        NetCache.getInstance().setIdms(l);
        idms = l;
    }

    public ReservationInfo getReservationInfo()
    {
        return reservationInfo;
    }

    public List<String> getIdms() {
        return idms;
    }


    public synchronized void fetchPorts(String domain) throws  AutobahnClientException {
        List<NameValuePair> params = new ArrayList<NameValuePair>();

        params.add(new BasicNameValuePair("domain",domain));
        String query = URLEncodedUtils.format(params, "utf-8");

        URI url=null;
        try {
            url = new URI(scheme, null, host, port, PORTS_URL, query, null);
        }catch (URISyntaxException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        String json=handleGetRequest(url);

        Gson gson = new Gson();
        ArrayList<String> ports=new ArrayList<String>();
        try{
            ports = gson.fromJson(json,ports.getClass());
        } catch (JsonParseException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        if(ports==null) {
            Log.d(TAG,"received null ports");
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        Log.d(TAG,ports.toString())         ;

        NetCache.getInstance().addPorts(domain,ports);
    }

    public synchronized void fetchReservationInfo(String serviceID) throws AutobahnClientException  {
        ReservationInfo reservationInfo = new ReservationInfo();

        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("serviceID", serviceID));
        String query = URLEncodedUtils.format(params, "utf-8");
        URI url=null;
        try {
             url = new URI(scheme, null, host, port, SERVICE_URL, query, null);
        } catch (URISyntaxException e) {
                        String error = context.getString(R.string.net_error);
                        AutobahnClientException ex = new AutobahnClientException(error);
                        throw ex;
        }
        String json=handleGetRequest(url);
        Gson gson = new Gson();
        try {
            reservationInfo = gson.fromJson(json, reservationInfo.getClass());
        } catch (JsonParseException e) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        if(reservationInfo==null) {
            String error = context.getString(R.string.net_error);
            AutobahnClientException ex = new AutobahnClientException(error);
            throw ex;
        }

        NetCache.getInstance().setLastResInfo(serviceID,reservationInfo);
    }
}
