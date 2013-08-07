package autobahn.android;
import java.util.Calendar;

public class Circuit {
    public Circuit(){

    }

    public enum ReservationState {
        UNKNOWN,
        SCHEDULED,
        ACTIVE,
        FINISHED,
        CANCELLED,
        FAILED
    }

    private ReservationState state;
    private Calendar startTime;
    private Calendar endTime;
    private long id;
    private int capacity;
    private int mtu;
    private int startVlan;
    private int endVlan;
    private Port startPort;
    private Port endPort;
    private String justf;

    public void setState(ReservationState state) {
        this.state = state;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    public void setMtu(int mtu) {
        this.mtu = mtu;
    }

    public void setStartVlan(int startVlan) {
        this.startVlan = startVlan;
    }

    public void setEndVlan(int endVlan) {
        this.endVlan = endVlan;
    }

    public void setStartPort(Port startPort) {
        this.startPort = startPort;
    }

    public void setEndPort(Port endPort) {
        this.endPort = endPort;
    }

    public void setJustf(String justf) {
        this.justf = justf;
    }

    public ReservationState getState() {
        return state;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public long getId() {
        return id;
    }

    public int getCapacity() {
        return capacity;
    }

    public int getMtu() {
        return mtu;
    }

    public int getStartVlan() {
        return startVlan;
    }

    public  int getEndVlan() {
        return endVlan;
    }

    public Port getStartPort() {
        return startPort;
    }

    public Port getEndPort() {
        return endPort;
    }

    public String getJustification() {
        return justf;
    }

    public String getStartDomain() {
        return startPort.getDomain();
    };

    public String getEndDomain() {
        return endPort.getDomain();
    }
}
