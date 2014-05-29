package autobahn.android;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import autobahn.android.utils.*;
import com.example.autobahn.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.PersistentCookieStore;
import net.geant.autobahn.android.*;
import org.apache.http.client.CookieStore;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.*;

public class AutobahnClient {

    private static final String LOGIN_URL = "/autobahn-gui/j_spring_security_check";
    private static final String DOMAIN_URL = "/autobahn-gui/portal/secure/rest/domains";
    private static final String SERVICES_URL = "/autobahn-gui/portal/secure/rest/domains/{domain}/services";
    private static final String SERVICE_URL = "/autobahn-gui/portal/secure/rest/domains/{domain}/services/{service}";
    private static final String PORTS_URL = "/autobahn-gui/portal/secure/rest/ports";
    private static final String SUBMIT_URL = "/autobahn-gui/portal/secure/rest/requestReservation";
    private static final String LOGOUT_URL = "/autobahn-gui/autobahn-gui/j_spring_security_logout";
    private static final String PROVISION_URL = "/autobahn-gui/portal/secure/rest/domains/{domain}/services/{service}/provision";
    private static final String RELEASE_URL = "/autobahn-gui/portal/secure/rest/domains/{domain}/services/{service}/release";
    private static final String CANCEL_URL = "/autobahn-gui/portal/secure/rest/domains/{domain}/services/{service}/cancel";
    private String TAG = "[Autobahn-client]";

    static AutobahnClient instance = null;
    private AutobahnDataSource dataSource;
    private RestTemplate restTemplate;
    private HttpHeaders responseHeaders;
    private HttpHeaders requestHeaders;
    private HttpEntity<?> requestEntity;
    private HttpClient httpclient;
    private String scheme;
    private String host;
    private int port;
    private String username;
    private String password;
    private HttpContext localContext;
    private Context context = null;
    CookieStore cookieStore;

    public AutobahnClient(Context context) {
        httpclient = new DefaultHttpClient();
        httpclient.getParams().setParameter(ClientPNames.ALLOW_CIRCULAR_REDIRECTS, true);
        dataSource = new AutobahnDataSource(context);

        try {
            initConfigValues(context);
        } catch (XmlPullParserException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    private void initConfigValues(Context context) throws XmlPullParserException, IOException {
        Resources res = context.getApplicationContext().getResources();
        XmlResourceParser parser = res.getXml(R.xml.conf);
        int eventType = parser.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && parser.getName().equals("Property")) {
                String propertyName = parser.getAttributeValue(null, "name");
                String propertyValue = parser.getAttributeValue(null, "value");
                switch (propertyName) {
                    case "scheme":
                        scheme = propertyValue;
                        break;
                    case "host":
                        host = propertyValue;
                        break;
                    case "port":
                        port = Integer.parseInt(propertyValue);
                        break;
                }
            }
            eventType = parser.next();
        }
    }

    public static AutobahnClient getInstance(Context context) {
        if (instance == null)
            instance = new AutobahnClient(context);

        return instance;
    }

    public String getFullHost() {
        return scheme + "://" + host + ":" + port;
    }

    public void setContext(Context context) {
        this.context = context;
        cookieStore = new PersistentCookieStore(context);

        localContext = new BasicHttpContext();
        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    public String getUserName() {
        return username;
    }

    public void setUserName(String s) {
        username = s;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String pass) {
        password = pass;
    }

    public boolean hasAuthenticated() {
        cookieStore = (PersistentCookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);

        for (Cookie c : cookieStore.getCookies()) {
            if (c.getName().equals("JSESSIONID") && !c.isExpired(new Date())) {
                return true;
            }
        }
        return false;
    }

    public String getCookie() {

        cookieStore = (PersistentCookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
        List<Cookie> cookies = cookieStore.getCookies();
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("JSESSIONID"))
                return cookie.getValue();
        }
        return null;
    }

    public synchronized void logIn() throws AutobahnClientException {

        ResponseEntity<String> response;

        if (hasAuthenticated()) {
            Log.d(TAG, "Autobahn client has already authenticated");
            return;
        }

        MultiValueMap<String, String> parameters = new LinkedMultiValueMap<String, String>();
        parameters.add("j_username", username);
        parameters.add("j_password", password);

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new FormHttpMessageConverter());

        try {
            response = restTemplate.postForEntity(getFullHost() + LOGIN_URL, parameters, String.class);
            checkStatus(response);
            if (response.getStatusCode().value() == 302) {
                responseHeaders = new HttpHeaders();
                responseHeaders = response.getHeaders();
                if (responseHeaders.get("Location").get(0).contains("reservation.htm")) {
                    List<String> headers = responseHeaders.get("Set-Cookie");
                    if (headers != null) {
                        String[] cookie = headers.get(0).split(";");
                        cookieStore = (PersistentCookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
                        cookieStore.addCookie(new BasicClientCookie("JSESSIONID", cookie[0]));
                        localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
                    }
                }
            }
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        if (!hasAuthenticated()) {
            throw new AutobahnClientException(context.getString(R.string.login_failed));
        }
    }

    public synchronized void logOut() throws AutobahnClientException {

        requestHeaders = new HttpHeaders();
        requestEntity = new HttpEntity<Object>(requestHeaders);

        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        try {
            restTemplate.exchange(getFullHost() + LOGOUT_URL, HttpMethod.GET, requestEntity, String.class);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        cookieStore = (PersistentCookieStore) localContext.getAttribute(ClientContext.COOKIE_STORE);
        cookieStore.clear();

        dataSource.open();
        dataSource.deleteData();
        dataSource.close();
    }

    public synchronized void fetchDomains() throws AutobahnClientException {
        ResponseEntity<JsonData> response;
        List<Domain> domains = new ArrayList<>();
        List<String> domainsById;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + DOMAIN_URL, HttpMethod.GET, requestEntity, JsonData.class);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }
        domainsById = (ArrayList<String>) response.getBody().getData();

        dataSource.open();
        for (String domain : domainsById) {
            String[] s = domain.split(":", 5);
            domains.add(new Domain(domain, s[3]));
        }
        dataSource.insertDomains(domains);
        dataSource.close();
    }

    public synchronized void fetchDomainReservations(String domain) throws AutobahnClientException {

        List<Reservation> reservations;
        ResponseEntity<JsonData> response;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + SERVICES_URL, HttpMethod.GET, requestEntity, JsonData.class, domain);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        dataSource.open();
        Type t = new TypeToken<ArrayList<Reservation>>() {
        }.getType();
        Gson gson = new Gson();
        String jsonString = gson.toJson(response.getBody().getData());
        reservations = gson.fromJson(jsonString, t);
        dataSource.insertReservations(reservations);
        dataSource.close();
    }

    public synchronized void fetchReservationInfo(String service, String domain) throws AutobahnClientException {

        ReservationInfo reservation;
        ResponseEntity<JsonData> response;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + SERVICE_URL, HttpMethod.GET, requestEntity, JsonData.class, domain, service);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        Type t = new TypeToken<ReservationInfo>() {
        }.getType();
        Gson gson = new Gson();
        String jsonString = gson.toJson(response.getBody().getData());
        reservation = gson.fromJson(jsonString, t);
        dataSource.open();
        dataSource.insertReservation(reservation);
        dataSource.close();
    }

    public synchronized void fetchPorts() throws AutobahnClientException {

        ResponseEntity<JsonData> response;
        Map<String, ArrayList<String>> ports;
        String domain;
        List<Port> domainPorts;
        String portName;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + PORTS_URL, HttpMethod.GET, requestEntity, JsonData.class);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        ports = (Map<String, ArrayList<String>>) response.getBody().getData();


        if (ports == null) {
            Log.d(TAG, "Received NULL ports");
            String error = context.getString(R.string.net_error);
            throw new AutobahnClientException(error);
        }

        domainPorts = new ArrayList<>();
        for (String key : ports.keySet()) {
            domain = key.split(":", 5)[3];
            for (String port : ports.get(key)) {
                portName = port.split(":")[6];
                domainPorts.add(new Port(portName, port, domain));
            }
        }
        dataSource.open();
        dataSource.insertPort(domainPorts);
        dataSource.close();
    }

    public synchronized String submitReservation(ReservationInfo info) throws AutobahnClientException {

        ResponseEntity<JsonData> response;
        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());

        HttpEntity<ReservationInfo> requestEntity = new HttpEntity<>(info, requestHeaders);

        restTemplate = new RestTemplate();
        restTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + SUBMIT_URL, HttpMethod.POST, requestEntity, JsonData.class);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        String reservationID = (String) response.getBody().getData();

        return reservationID;

    }

    public synchronized void cancel(String domain, String serviceId) throws AutobahnClientException {

        ResponseEntity<JsonData> response;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + CANCEL_URL, HttpMethod.GET, requestEntity, JsonData.class, domain, serviceId);
            checkStatus(response);
            checkResponse(response);
            updateReservation(domain, serviceId);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }
    }

    public synchronized void provision(String domain, String serviceId) throws AutobahnClientException {
        ResponseEntity<JsonData> response;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + PROVISION_URL, HttpMethod.GET, requestEntity, JsonData.class, domain, serviceId);
            checkStatus(response);
            checkResponse(response);
            updateReservation(domain, serviceId);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }
    }

    public synchronized void release(String domain, String serviceId) throws AutobahnClientException {
        ResponseEntity<JsonData> response;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + RELEASE_URL, HttpMethod.GET, requestEntity, JsonData.class, domain, serviceId);
            checkStatus(response);
            checkResponse(response);
            updateReservation(domain, serviceId);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

    }

    public synchronized void updateDomains() throws AutobahnClientException {
        ResponseEntity<JsonData> response;
        List<Domain> domains = new ArrayList<>();
        List<String> domainsById;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + DOMAIN_URL, HttpMethod.GET, requestEntity, JsonData.class);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }
        domainsById = (ArrayList<String>) response.getBody().getData();

        dataSource.open();
        for (String domain : domainsById) {
            String[] s = domain.split(":", 5);
            domains.add(new Domain(domain, s[3]));
        }
        dataSource.updateDomains(domains);
        dataSource.close();
    }

    public synchronized void updateReservations(String domain) throws AutobahnClientException {

        List<Reservation> reservations;
        ResponseEntity<JsonData> response;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + SERVICES_URL, HttpMethod.GET, requestEntity, JsonData.class, domain);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        dataSource.open();
        Type t = new TypeToken<ArrayList<Reservation>>() {
        }.getType();
        Gson gson = new Gson();
        String jsonString = gson.toJson(response.getBody().getData());
        reservations = gson.fromJson(jsonString, t);

        dataSource.updateReservations(domain, reservations);
        dataSource.close();


    }

    public synchronized void updateReservation(String domain, String service) throws AutobahnClientException {
        ReservationInfo reservation;
        ResponseEntity<JsonData> response;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new StringHttpMessageConverter());
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + SERVICE_URL, HttpMethod.GET, requestEntity, JsonData.class, domain, service);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        Type t = new TypeToken<ReservationInfo>() {
        }.getType();
        Gson gson = new Gson();
        String jsonString = gson.toJson(response.getBody().getData());
        reservation = gson.fromJson(jsonString, t);
        dataSource.open();
        dataSource.updateReservation(reservation);
        dataSource.close();
    }

    public synchronized void updatePorts() throws AutobahnClientException {
        ResponseEntity<JsonData> response;
        Map<String, ArrayList<String>> ports;
        String domain;
        List<Port> domainPorts;
        String portName;

        requestHeaders = new HttpHeaders();
        requestHeaders.set("Cookie", getCookie());
        requestEntity = new HttpEntity<Object>(requestHeaders);
        restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

        try {
            response = restTemplate.exchange(getFullHost() + PORTS_URL, HttpMethod.GET, requestEntity, JsonData.class);
            checkStatus(response);
            checkResponse(response);
        } catch (HttpClientErrorException e) {
            throw new AutobahnClientException(e.getMessage());
        } catch (ResourceAccessException e) {
            throw new AutobahnClientException(context.getString(R.string.connection_error));
        }

        ports = (Map<String, ArrayList<String>>) response.getBody().getData();


        if (ports == null) {
            Log.d(TAG, "Received NULL ports");
            String error = context.getString(R.string.net_error);
            throw new AutobahnClientException(error);
        }

        domainPorts = new ArrayList<>();
        for (String key : ports.keySet()) {
            domain = key.split(":", 5)[3];
            for (String port : ports.get(key)) {
                portName = port.split(":")[6];
                domainPorts.add(new Port(portName, port, domain));
            }
        }
        dataSource.open();
        dataSource.updatePorts(domainPorts);
        dataSource.close();
    }

    private void checkStatus(ResponseEntity response) throws AutobahnClientException {
        int status = response.getStatusCode().value();

        if (status == 200 || status == 302) {
            return;
        } else if (status == 404) {
            throw new AutobahnClientException(context.getString(R.string.error_404));
        } else if (status == 500) {
            throw new AutobahnClientException(context.getString(R.string.error_500));
        } else {
            throw new AutobahnClientException(context.getString(R.string.error) + status);
        }
    }

    private void checkResponse(ResponseEntity response) throws AutobahnClientException {
        JsonData<Object> responseBody = (JsonData<Object>) response.getBody();
        if (responseBody.getError() == ErrorType.OK)
            return;
        else
            throw new AutobahnClientException(responseBody.getMessage());
    }

    class NoDataException extends Exception {

    }
}

/**
 * Retrieves username, password and autobahn url
 * from local preferences, and sets them to autobahn variables

 private void retrieveLoginInfo() {
 SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

 userName = sharedPref.getString(PreferencesActivity.USERNAME_PREFERENCE_KEY, "");
 password = sharedPref.getString(PreferencesActivity.PASSWORD_PREFERENCE_KEY, "");
 }
 */
