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
	private Map<String,ArrayList<String>> idms = new HashMap<>();
	private List<String> reservations = new ArrayList();
	private ReservationInfo lastResInfo;
	private ArrayList<String> lastResId = new ArrayList<>();
	private String lastDomRes;
	private Map<String, Map<String,ArrayList<String>>> ports = new HashMap();
    private String lastSubmittedId=null;

	private NetCache() {
		idms = null;
		reservations = null;
		lastDomRes = null;
		lastResId = null;
		lastResInfo = null;

	}

    public void setLastSubmittedId(String id) {
        lastSubmittedId=id;
    }

    public String getLastSubmittedId() {
        return lastSubmittedId;
    }

	public static NetCache getInstance() {
		if (instance == null) {
			instance = new NetCache();
		}

		return instance;
	}

	List<String> getTrackCircuits(String dom) {
		if (dom.equals(lastDomRes)) {
			return reservations;
		} else {
			lastDomRes = null;
			reservations = null;
		}
		return null;
	}

	public Map<String,ArrayList<String>> getIdms() {
		if (idms == null) {
			return null;
		}
		return idms;
	}

	public void setIdms(Map<String,ArrayList<String>> idms) {
		this.idms = idms;
	}

	public void setLastResInfo(String id,String domain, ReservationInfo res) {
		lastResInfo = res;
        lastResId = new ArrayList<>();
		lastResId.add(id);
        lastResId.add(domain);

	}

	public ReservationInfo getLastReservation(ArrayList<String> lastResId) {
		if (lastResId.equals(this.lastResId)) {
			return lastResInfo;
		} else {
			lastResInfo = null;
			return null;
		}
	}

	public void setReservations(List<String> res, String domain) {
		reservations = res;
		lastDomRes = domain;
	}

	public Map<String, ArrayList<String>> getPorts(String dom) {
		if (ports.containsKey(dom)) {
			return new HashMap<>(ports.get(dom));
		}
		return null;
	}

	public boolean hasPorts(String dom) {
		return ports.containsKey(dom);
	}

	public void addPorts(String dom, List<String> ports) {
		ArrayList<String> encodedPorts = new ArrayList<String>(ports);
        ArrayList<String> array = new ArrayList<>();
        Map<String,ArrayList<String>> portMap = new HashMap<>();
        for(int i = 0; i<encodedPorts.size(); i++){
            String[] tokens = encodedPorts.get(i).split(":");
            array.add(tokens[6]);
        }
        portMap.put("name",array);
        portMap.put("value",encodedPorts);
		this.ports.put(dom, portMap);
	}

	public void clear() {
		ports.clear();
		idms = null;
		reservations = null;
		lastDomRes = null;
		lastResId = null;
		lastResInfo = null;
	}
}


