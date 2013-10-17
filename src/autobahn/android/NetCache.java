package autobahn.android;

import net.geant.autobahn.android.ReservationInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 10/17/13
 * Time: 3:15 PM
 * To change this template use File | Settings | File Templates.
 */
public class NetCache {
    static NetCache instance = null;

    private String TAG = "CACHE";

    public static NetCache getInstance() {
        if (instance == null) {
            instance = new NetCache();
        }

        return instance;
    }

    private List<String> idms = new ArrayList();
    private List<String> reservations = new ArrayList();
    private ReservationInfo lastResInfo;
    private String lastResId;
    private String lastDomRes;
    private Map<String,ArrayList<String>> ports;

    List<String> getTrackCircuits(String dom) {
        if(dom.equals(lastDomRes) ) {
            return reservations;
        }else {
            lastDomRes=null;
            reservations=null;
        }
        return null;
    }

    private NetCache() {
        idms=null;
        reservations=null;
        lastDomRes=null;
        lastResId=null;
        lastResInfo=null;

        ports=new HashMap<String, ArrayList<String>>();
    }

    public List<String> getIdms() {
        return idms;
    }

    public void setLastResInfo(String id ,ReservationInfo res) {
        lastResInfo=res;
        lastResId=id;

    }

    public ReservationInfo getLastReservation(String lastResId) {
        if(lastResId.equals(this.lastResId) ) {
            return lastResInfo;
        }else {
            lastResInfo=null;
            return null;
        }
    }

    public void setIdms(List<String> idms) {
        this.idms=idms;
    }

    public void setReservations(List<String> res,String domain) {
        reservations=res;
        lastDomRes=domain;
    }

    public List<String> getPorts(String dom) {
        if(ports.containsKey(dom)) {
            return new ArrayList<String>(ports.get(dom) );
        }
        return null;
    }

    public boolean hasPorts(String dom) {
        return ports.containsKey(dom);
    }

    public void addPorts(String dom,List<String> ports) {
        ArrayList<String> array=new ArrayList<String>(ports);
        this.ports.put(dom,array);
    }

    public void clear() {
        ports.clear();
        idms=null;
        reservations=null;
        lastDomRes=null;
        lastResId=null;
        lastResInfo=null;
    }
}


