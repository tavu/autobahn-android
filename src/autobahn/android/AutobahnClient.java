package autobahn.android;

import com.example.autobahn.R;
import android.content.res.Resources;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;


import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;


public class AutobahnClient {

    private HttpClient httpclient;
    private String scheme;
    private String host;
    private int port;
    boolean isLogIn;

    public AutobahnClient(String host,int port){
        httpclient= new DefaultHttpClient();
        scheme="http";
        this.host=host;
        this.port=port;
        isLogIn=false;
    }

    public boolean hasAuthenticate() {
        return isLogIn;
    }

    public void logIn(String name,String pass) throws URISyntaxException, IOException {

        String query="j_username="+name+"&j_password="+pass;

        URI url= new URI("http" , null , "62.217.125.174" ,8080, Resources.getSystem().getString(R.string.loginPage) ,query,null);
        HttpPost httppost = new HttpPost(url);
        HttpResponse response = httpclient.execute(httppost);

        //TODO check if login is correct

        isLogIn=true;
        return ;
    }

    public List<Circuit> getTrackCircuit(String idm) {
        return null;
    }

    public List<String> getIdms() {
        return null;
    }


}
