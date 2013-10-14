package autobahn.android;
import java.util.Calendar;

public class ReservationInfo {
    public ReservationInfo(){

    }


    private String reservationState;
    private String provisionState;
    private String lifecycleState;
    private Calendar startTime = null;
    private Calendar endTime = null;
    private String id;
    private long capacity;
    private int mtu;
    private int startVlan;
    private int endVlan;
    private String startPort;
    private String endPort;
    private String description = null;


    public String getReservationState() {
        return reservationState;
    }

    public String getProvisionState() {
        return provisionState;
    }

    public String getLifecycleState() {
        return lifecycleState;
    }

    public void setProvisionState(String provisionState) {
        this.provisionState = provisionState;
    }

    public void setLifecycleState(String lifecycleState) {
        this.lifecycleState = lifecycleState;
    }

    public void setReservationState(String reservationState) {
        this.reservationState = reservationState;
    }

    public void setStartTime(Calendar startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(Calendar endTime) {
        this.endTime = endTime;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCapacity(long capacity) {
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

    public void setStartPort(String startPort) {
        this.startPort = startPort;
    }

    public void setEndPort(String endPort) {
        this.endPort = endPort;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Calendar getStartTime() {
        return startTime;
    }

    public Calendar getEndTime() {
        return endTime;
    }

    public String getId() {
        return id;
    }

    public long getCapacity() {
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

    public String getStartPort() {
        return startPort;
    }

    public String getEndPort() {
        return endPort;
    }

    public String getDescription() {
        return description;
    }

}
