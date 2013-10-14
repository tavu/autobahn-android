package autobahn.android;
import java.util.Calendar;

public class ReservationInfo {
    public ReservationInfo(){

    }


    public enum ReservationState {
        UNKNOWN,
        SCHEDULED,
        ACTIVE,
        FINISHED,
        CANCELLED,
        FAILED;

        public static ReservationState fromInteger(int x) {
            switch(x) {
                case 0:
                    return UNKNOWN;
                case 1:
                    return SCHEDULED;
                case 2:
                    return ACTIVE;
                case 3:
                    return FINISHED;
                case 4:
                    return CANCELLED;
                case 5:
                    return FAILED;
            }
            return null;
        }
    }

    private ReservationState state = ReservationState.UNKNOWN;
    private Calendar startTime = null;
    private Calendar endTime = null;
    private long id;
    private long capacity;
    private int mtu;
    private int startVlan;
    private int endVlan;
    private Port startPort = null;
    private Port endPort = null;
    private String description = null;

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

    public void setStartPort(Port startPort) {
        this.startPort = startPort;
    }

    public void setEndPort(Port endPort) {
        this.endPort = endPort;
    }

    public void setDescription(String description) {
        this.description = description;
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

    public Port getStartPort() {
        return startPort;
    }

    public Port getEndPort() {
        return endPort;
    }

    public String getDescription() {
        return description;
    }

    public String getStartDomain() {
        return startPort.getDomain();
    };

    public String getEndDomain() {
        return endPort.getDomain();
    }
}
