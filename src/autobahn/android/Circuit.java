package autobahn.android;
import autobahn.android.Port;

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

    public ReservationState state;
    public Calendar startTime;
    public Calendar endTime;
    public long id;
    public int capacity;
    public int mtu;
    public int startVlan;
    public int endVlan;

    public Port startPort;
    public Port endPort;
    public String justf;

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

    public String justification() {
        return justf;
    }

    public String getStartDomain() {
        return startPort.getDomain();
    };

    public String getEndDomain() {
        return endPort.getDomain();
    }
}
