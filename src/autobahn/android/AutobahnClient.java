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
import java.util.ArrayList;
import java.util.Calendar;
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

        List<Circuit> list=new ArrayList<Circuit>();

        Circuit c=new Circuit();
        c.id=1;
        c.capacity=10000;
        c.mtu=-1;
        c.state=Circuit.ReservationState.ACTIVE;
        c.startTime= Calendar.getInstance();
        c.endTime=Calendar.getInstance();
        c.startVlan=0;
        c.endVlan=0;
        c.startPort=new Port("Port_1","GARR");
        c.endPort=new Port("Port_2","GARR");
        list.add(c);

        c=new Circuit();
        c.id=2;
        c.capacity=1000;
        c.mtu=500;
        c.state=Circuit.ReservationState.FAILED;
        c.startTime= Calendar.getInstance();
        c.endTime=Calendar.getInstance();
        c.startPort=new Port("GARR.Port_1","GARR","client_1");
        c.endPort=new Port("GRNET.Port_2","GRNET");
        c.startVlan=10;
        c.endVlan=4000;
        list.add(c);



        return list;
    }

    public List<String> getIdms() {
        List<String> list=new ArrayList<String>();
        list.add("GARR");
        list.add("GRNET");
        return list;
    }


}
