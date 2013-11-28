package autobahn.android;

import android.content.Context;
import android.util.Log;
import com.example.autobahn.R;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.loopj.android.http.PersistentCookieStore;
import net.geant.autobahn.android.ErrorType;
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
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AutobahnClient {

	static AutobahnClient instance = null;
	private static final String LOGIN_URL = "/autobahn-gui/j_spring_security_check";
	private static final String DOMAIN_URL = "/autobahn-gui/portal/secure/rest/idms";
	private static final String SERVICES_URL = "/autobahn-gui/portal/secure/rest/services";
	private static final String SERVICE_URL = "/autobahn-gui/portal/secure/rest/service";
	private static final String PORTS_URL = "/autobahn-gui/portal/secure/rest/ports";
    private static final String SUBMIT_URL="/autobahn-gui/portal/secure/rest/requestReservation";
    private static final String LOGOUT_URL = "/autobahn-gui/j_spring_security_logout";
    private static final String PROVISION_URL="/autobahn-gui/portal/secure/rest/provision";
    private static final String CANCEL_URL="/autobahn-gui/portal/secure/rest/cancel";

	private HttpClient httpclient;
	private HttpGet httpget;
	private String scheme;
	private String host;
	private int port;
	private String userName;
	private String password;
	private HttpContext localContext;
	private Context context = null;
    CookieStore cookieStore;
	private String TAG = "[Autobahn-client]";

	public AutobahnClient() {
		httpclient = new DefaultHttpClient();
		scheme = "http";
        host="62.217.125.174";
        port=8080;

	}

	public static AutobahnClient getInstance() {
		if (instance == null)
			instance = new AutobahnClient();

		return instance;
	}

	public void setContext(Context context) {
		this.context = context;
		cookieStore = new PersistentCookieStore(context);

		localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
	}

	public void setHost(String s) {
		host = s;
	}

	public void setPort(int p) {
		port = p;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String s) {
		userName = s;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String pass) {
		password = pass;
	}

	public boolean hasAuthenticate() {
		CookieStore cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
		for (Cookie c : cookieStore.getCookies()) {
			if (c.getName().equals("SPRING_SECURITY_REMEMBER_ME_COOKIE") && !c.isExpired(new Date())) {
				return true;
			}
		}
		return false;
	}

    public synchronized void logOut() throws AutobahnClientException {
        CookieStore cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
        cookieStore.clear();

        NetCache.getInstance().clear();

        URI url;
        HttpPost httppost;

        try {
            url = new URI(scheme, null, host, port, LOGOUT_URL, null, null);
            httppost = new HttpPost(url);

        } catch (URISyntaxException e) {
            String error = e.getMessage();
            Log.d(TAG, error);
            throw new AutobahnClientException(error);
        }

        handlePostRequest(httppost);

    }

	public synchronized void logIn() throws AutobahnClientException {

		if (hasAuthenticate()) {
			Log.d(TAG, "Autobahn client has already authenticate");
			return;
		}

		//retrieveLoginInfo();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("j_username", userName));
		params.add(new BasicNameValuePair("j_password", password));
		params.add(new BasicNameValuePair("_spring_security_remember_me", "true"));
		String query = URLEncodedUtils.format(params, "utf-8");

		URI url;
		HttpPost httppost;
		HttpResponse response;
		try {
			url = new URI(scheme, null, host, port, LOGIN_URL, query, null);
			httppost = new HttpPost(url);

		} catch (URISyntaxException e) {
			String error = e.getMessage();
			Log.d(TAG, error);
			throw new AutobahnClientException(error);
		}
        Log.d(TAG,url.toString());
        handlePostRequest(httppost);


        if (!hasAuthenticate()) {
            String error = context.getString(R.string.login_failed);
            throw new AutobahnClientException(error);
        }

	}

	/**
	 * Retrieves username, password and autobahn url
	 * from local preferences, and sets them to autobahn variables

	private void retrieveLoginInfo() {
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		host = sharedPref.getString(PreferencesActivity.HOST_PREFERENCE_KEY, "");
		port = 8080;

		userName = sharedPref.getString(PreferencesActivity.USERNAME_PREFERENCE_KEY, "");
		password = sharedPref.getString(PreferencesActivity.PASSWORD_PREFERENCE_KEY, "");
	}
     */
    private void checkStatus(HttpResponse response) throws AutobahnClientException {
        int status = response.getStatusLine().getStatusCode();

        if (status == 200) {
            return ;
        } else if (status == 404) {
            String error = context.getString(R.string.error_404);
            throw new AutobahnClientException(error);
        } else if (status == 500) {
            String error = context.getString(R.string.error_500);
            throw new AutobahnClientException(error);
        } else {
            String error = context.getString(R.string.error);
            throw new AutobahnClientException(error + status);
        }
    }

    private String checkAnswer(HttpResponse response) throws AutobahnClientException,NoDataException {
        String responseStr=null;
        try {
            responseStr = EntityUtils.toString(response.getEntity());
        } catch (IOException e) {
            Log.d(TAG, e.getMessage());
            String errorStr = context.getString(R.string.response_error);
            throw new AutobahnClientException(errorStr);
        }

        Log.d(TAG, responseStr);

        if (responseStr == null) {
            String errorStr = context.getString(R.string.response_error);
            throw new AutobahnClientException(errorStr);
        }

        JSONObject json= null;
        int err=0;
        try {
            json = new JSONObject(responseStr);
            err=json.getInt("error");
        } catch (JSONException e) {
            String errorStr = context.getString(R.string.response_error);
            throw new AutobahnClientException(errorStr);
        }

        String msg=null;
        if(err ==  ErrorType.OK ) {
            return responseStr;
        } else if(err == ErrorType.NO_DATA ) {
            throw new NoDataException();
        } else {
            try {
                msg=json.getString("message");
                Log.d(TAG,"ERROR:"+err+" " + msg);
            } catch (JSONException e) {
                String errorStr = context.getString(R.string.response_error);
                throw new AutobahnClientException(errorStr);
            }
            throw new AutobahnClientException(msg);
        }
    }

    private synchronized HttpResponse handlePostRequest(HttpPost httppost) throws AutobahnClientException {

        HttpResponse response=null;
        try {
            response = httpclient.execute(httppost, localContext);
        } catch (ClientProtocolException e) {
            Log.d(TAG, "ClientProtocolException");
            String errorStr = context.getString(R.string.connection_error);
            throw new AutobahnClientException(errorStr);
        } catch (IOException e) {
            Log.d(TAG, "IOException");
            String errorStr = context.getString(R.string.no_internet);
            throw new AutobahnClientException(errorStr);
        }

        checkStatus(response);//if the http status code is different from 200 this function will throw.
        return response;
    }

	private String handleGetRequest(URI url) throws AutobahnClientException,NoDataException  {

		httpget = new HttpGet(url);
        Log.d(TAG,url.toString());
		HttpResponse response =null;
		try {
			response = httpclient.execute(httpget, localContext);
		} catch (ClientProtocolException e) {
			String error = context.getString(R.string.net_error);
            Log.d(TAG, e.getMessage());
			throw new AutobahnClientException(error);
		} catch (IOException e) {
            Log.d(TAG, e.getMessage());
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}

        checkStatus(response);//if the http status code is different from 200 this function will throw.
        String responseStr=checkAnswer(response);//if the server anser with an error this function will throw.

        String data=null;
        JSONObject json= null;
        try {
            json=new JSONObject(responseStr);
            data=json.getString("data");
            Log.d(TAG, data);
        } catch (JSONException e) {
            String errorStr = context.getString(R.string.response_error);
            throw new AutobahnClientException(errorStr);
        }
        return data;
	}

	public synchronized void fetchTrackCircuit(String domain) throws AutobahnClientException {

		ArrayList<String> circuits = new ArrayList<String>();
		URI url;
		url = null;
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("currentIdm", domain));
		String query = URLEncodedUtils.format(params, "utf-8");

		try {

			url = new URI(scheme, null, host, port, SERVICES_URL, query, null);
		} catch (URISyntaxException e) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}

		Gson gson = new Gson();
		try {
            String json = handleGetRequest(url);
			circuits = gson.fromJson(json, circuits.getClass());
		} catch (JsonParseException e) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}  catch ( NoDataException e) {
            //Do nothing
        }

        NetCache.getInstance().setReservations(circuits, domain);
	}

	public synchronized void fetchIdms() throws AutobahnClientException {

        Log.d(TAG, "Fetching Domains...");
        URI url;
		url = null;
		try {
			url = new URI(scheme, null, host, port, DOMAIN_URL, null, null);
		} catch (URISyntaxException e) {
            Log.d(TAG,e.getMessage());
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}

        Gson gson = new Gson();
		ArrayList<String> l = new ArrayList<String>();
		try {
            String json = handleGetRequest(url);
			l = gson.fromJson(json, l.getClass());
		} catch (JsonParseException e) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		} catch ( NoDataException e) {
            //Do nothing
        }

		NetCache.getInstance().setIdms(l);
	}

	public synchronized void fetchPorts(String domain) throws AutobahnClientException {
		List<NameValuePair> params = new ArrayList<NameValuePair>();

		params.add(new BasicNameValuePair("domain", domain));
		String query = URLEncodedUtils.format(params, "utf-8");

		URI url;
		url = null;
		try {
			url = new URI(scheme, null, host, port, PORTS_URL, query, null);
		} catch (URISyntaxException e) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}


		Gson gson = new Gson();
		ArrayList<String> ports = new ArrayList<String>();
		try {
            String json = handleGetRequest(url);
			ports = gson.fromJson(json, ports.getClass());
		} catch (JsonParseException e) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		} catch ( NoDataException e) {
            //Do nothing
        }

		if (ports == null) {
			Log.d(TAG, "received null ports");
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}

		Log.d(TAG, ports.toString());

		NetCache.getInstance().addPorts(domain, ports);
	}

	public synchronized void fetchReservationInfo(String serviceID) throws AutobahnClientException {
		ReservationInfo reservationInfo = new ReservationInfo();

		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("serviceID", serviceID));
		String query = URLEncodedUtils.format(params, "utf-8");
		URI url;
		try {
			url = new URI(scheme, null, host, port, SERVICE_URL, query, null);
		} catch (URISyntaxException e) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}

		Gson gson = new Gson();
		try {
            String json = handleGetRequest(url);
			reservationInfo = gson.fromJson(json, reservationInfo.getClass());
		} catch (JsonParseException e) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		} catch ( NoDataException e) {
            //Do nothing
        }

		if (reservationInfo == null) {
			String error = context.getString(R.string.net_error);
			throw new AutobahnClientException(error);
		}

		NetCache.getInstance().setLastResInfo(serviceID, reservationInfo);
	}

    public synchronized void submitReservation(ReservationInfo info) throws AutobahnClientException {

        URI url;
        HttpPost httppost;
        try {
            url = new URI(scheme, null, host, port, SUBMIT_URL, null, null);
            httppost = new HttpPost(url);
            Gson gson = new Gson();
            String json=gson.toJson(info);
            Log.d(TAG,json);
            StringEntity se = new StringEntity(json);

            httppost.addHeader("content-type", "application/json");

            se.setContentEncoding(new BasicHeader(HTTP.CONTENT_TYPE, "application/json"));
            httppost.setEntity(se);

        } catch (URISyntaxException e) {
            String error = e.getMessage();
            Log.d(TAG, error);
            throw new AutobahnClientException(error);
        } catch (IOException e) {
            String error = e.getMessage();
            Log.d(TAG, error);
            throw new AutobahnClientException(error);
        }

        HttpResponse response = handlePostRequest(httppost);
        String resId;
        JSONObject json= null;
        try {
            json=new JSONObject( checkAnswer(response) );
            resId=json.getString("data");
        }catch (Exception e) {
            String errorStr = context.getString(R.string.response_error);
            throw new AutobahnClientException(errorStr);
        }

        NetCache.getInstance().setLastSubmittedId(resId);
    }

    public synchronized void provision(String idm,String serviceId) throws AutobahnClientException {
        URI url;
        HttpPost httppost;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("currentIdm", idm));
        params.add(new BasicNameValuePair("serviceId", serviceId));
        String query = URLEncodedUtils.format(params, "utf-8");

        try {
            url = new URI(scheme, null, host, port, PROVISION_URL, query, null);
            httppost = new HttpPost(url);
        } catch (URISyntaxException e) {
            String error = e.getMessage();
            Log.d(TAG, error);
            throw new AutobahnClientException(error);
        }

        HttpResponse response = handlePostRequest(httppost);

        try {
            checkAnswer(response);
        }catch (NoDataException e) {
            //do nothing
        }
    }

    public synchronized void cancelRequest(String idm,String serviceId) throws AutobahnClientException {
        URI url;
        HttpPost httppost;
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        params.add(new BasicNameValuePair("currentIdm", idm));
        params.add(new BasicNameValuePair("serviceId", serviceId));
        String query = URLEncodedUtils.format(params, "utf-8");

        try {
            url = new URI(scheme, null, host, port, CANCEL_URL, query, null);
            httppost = new HttpPost(url);
        } catch (URISyntaxException e) {
            String error = e.getMessage();
            Log.d(TAG, error);
            throw new AutobahnClientException(error);
        }

        HttpResponse response = handlePostRequest(httppost);

        try {
            checkAnswer(response);
        }catch (NoDataException e) {
            //do nothing
        }
    }

    class NoDataException extends Exception {

    }
}
