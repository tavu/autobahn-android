package autobahn.android;

import android.content.Context;
import android.util.Log;

import com.example.autobahn.R;
import com.google.gson.Gson;
import com.google.gson.JsonParseException;

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
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class AutobahnClient {

	private static String LOGIN_URL = "/autobahn-gui/j_spring_security_check";
	private static String DOMAIN_URL = "/autobahn-gui/portal/secure/android/idms";
	private static String SERVICES_URL = "/autobahn-gui/portal/secure/android/services";
	private static String SERVICE_URL = "/autobahn-gui/portal/secure/android/service";

	private HttpClient httpclient;
	private HttpGet httpget;
	private HttpResponse response;
	private URI url;
	private Gson gson;
	private String json;
	private String scheme;
	private String host;
	private int port;
	boolean isLogIn;
	private String userName;
	private String password;
	private HttpContext localContext;
	private Context context = null;
	private List<String> idms = new ArrayList();
	private List<String> circuits = new ArrayList();
	private ReservationInfo reservationInfo;

	private String TAG = "WARN";

	static AutobahnClient instance = null;

	public static AutobahnClient getInstance() {
		if (instance == null)
			instance = new AutobahnClient();

		return instance;
	}

	public AutobahnClient() {
		httpclient = new DefaultHttpClient();
		scheme = "http";
		isLogIn = false;
		CookieStore cookieStore = new BasicCookieStore();
		localContext = new BasicHttpContext();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		//TODO get the host from a property
		host = "62.217.124.241";
		port = 8080;
	}

	public void setContext(Context context) {
		this.context = context;
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
		return isLogIn;
	}

	public void logIn() throws AutobahnClientException {
		String query = "j_username=" + userName + "&j_password=" + password + "&_spring_security_remember_me=true";
		URI url = null;
		HttpPost httppost = null;
		try {
			url = new URI(scheme, null, host, port, LOGIN_URL, query, null);
			httppost = new HttpPost(url);
			HttpResponse response = httpclient.execute(httppost, localContext);

		} catch (URISyntaxException e) {
			String error = e.getMessage();
			Log.d(TAG, error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (ClientProtocolException e) {
			String error = e.getMessage();
			Log.d(TAG, error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (IOException e) {
			String error = e.getMessage();
			Log.d(TAG, error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

		CookieStore cookieStore = (CookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
		for (Cookie c : cookieStore.getCookies()) {
			if (c.getName().equals("SPRING_SECURITY_REMEMBER_ME_COOKIE"))
				isLogIn = true;
		}
		if (!isLogIn) {
			String error = context.getString(R.string.login_failed);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}
	}

	/*
		returns the track circuit than have been fetched previously from fetchTrackCircuit
	 */
	public List<String> getTrackCircuits() {
		return circuits;
	}

	public void fetchTrackCircuit(String domain) throws AutobahnClientException {

		circuits.clear();
		try {
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("currentIdm", domain));

			String query = URLEncodedUtils.format(params, "utf-8");

			url = new URI(scheme, null, host, port, SERVICES_URL, query, null);
			httpget = new HttpGet(url);

			response = httpclient.execute(httpget, localContext);
		} catch (URISyntaxException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (ClientProtocolException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (IOException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

		try {
			json = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

		gson = new Gson();
		try {
			circuits = gson.fromJson(json, circuits.getClass());
		} catch (JsonParseException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}
	}

	public void fetchIdms() throws AutobahnClientException {

		idms.clear();
		try {
			url = new URI(scheme, null, host, port, DOMAIN_URL, null, null);
			httpget = new HttpGet(url);
			response = httpclient.execute(httpget, localContext);

		} catch (URISyntaxException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (ClientProtocolException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (IOException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}


		try {
			json = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

		Gson gson = new Gson();
		ArrayList<String> l = new ArrayList<String>();

		try {
			l = gson.fromJson(json, l.getClass());
		} catch (JsonParseException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

		idms = l;
	}

	public void fetchReservationInfo(String serviceID) throws AutobahnClientException {

		reservationInfo = new ReservationInfo();

		try {

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("serviceID", serviceID));

			String query = URLEncodedUtils.format(params, "utf-8");
			url = new URI(scheme, null, host, port, SERVICE_URL, query, null);
			httpget = new HttpGet(url);
			response = httpclient.execute(httpget, localContext);

		} catch (URISyntaxException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (ClientProtocolException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		} catch (IOException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

		try {
			json = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

		gson = new Gson();
		try {
			reservationInfo = gson.fromJson(json, reservationInfo.getClass());
		} catch (JsonParseException e) {
			String error = context.getString(R.string.net_error);
			AutobahnClientException ex = new AutobahnClientException(error);
			throw ex;
		}

	}

	public ReservationInfo getReservationInfo() {
		return reservationInfo;
	}

	public List<String> getIdms() {
		return idms;
	}

}
