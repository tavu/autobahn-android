package autobahn.android.utils;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;
import autobahn.android.AutobahnClient;
import net.geant.autobahn.android.Domain;
import net.geant.autobahn.android.Port;
import net.geant.autobahn.android.Reservation;
import net.geant.autobahn.android.ReservationInfo;

import java.util.*;

/**
 * Created by Nl0st on 30/4/2014.
 */
public class AutobahnDataSource {

    private SQLiteDatabase database;
    private AutobahnDatabaseHelper helper;
    private Context context;

    public AutobahnDataSource(Context context) {
        this.context = context;
        helper = new AutobahnDatabaseHelper(context);
    }

    public void open() throws SQLException {
        database = helper.getWritableDatabase();
    }

    public void close() {
        helper.close();
    }

    public void insertDomains(List<Domain> domains) {
        for (Domain domain : domains) {
            ContentValues values = new ContentValues();
            values.put(AutobahnDatabaseHelper.DOMAIN_ID, domain.getDomainId());
            values.put(AutobahnDatabaseHelper.DOMAIN_NAME, domain.getDomainName());
            database.insert(AutobahnDatabaseHelper.DOMAINS, null, values);
        }
    }

    public void updateDomains(List<Domain> domains) {
        database.delete(AutobahnDatabaseHelper.DOMAINS, null, null);
        insertDomains(domains);
    }

    public void insertReservations(List<Reservation> reservations) {
        for (Reservation reservation : reservations) {
            ContentValues values = new ContentValues();
            values.put(AutobahnDatabaseHelper.RESERVATION_ID, reservation.getReservationId());
            values.put(AutobahnDatabaseHelper.RESERVATION_DOMAIN, reservation.getDomainName());
            values.put(AutobahnDatabaseHelper.RESERVATION_IS_ACTIVE, (reservation.isActive()) ? 1 : 0);
            database.insert(AutobahnDatabaseHelper.RESERVATIONS, null, values);
        }
    }

    public void updateReservations(String domain, List<Reservation> reservations) {
        String selection = AutobahnDatabaseHelper.RESERVATION_DOMAIN + "='" + domain + "'";
        database.delete(AutobahnDatabaseHelper.RESERVATIONS, selection, null);
        insertReservations(reservations);
    }

    public void insertReservation(ReservationInfo reservation) {
        ContentValues values = new ContentValues();
        values.put(AutobahnDatabaseHelper.RESERVATION_ID, reservation.getId());
        values.put(AutobahnDatabaseHelper.DESCRIPTION, reservation.getDescription());
        values.put(AutobahnDatabaseHelper.RESERVATION_STATE, reservation.getReservationState());
        values.put(AutobahnDatabaseHelper.PROVISION_STATE, reservation.getProvisionState());
        values.put(AutobahnDatabaseHelper.LIFECYCLE_STATE, reservation.getLifecycleState());
        values.put(AutobahnDatabaseHelper.CAPACITY, reservation.getCapacity());
        values.put(AutobahnDatabaseHelper.MTU, reservation.getMtu());
        values.put(AutobahnDatabaseHelper.START_VLAN, reservation.getStartVlan());
        values.put(AutobahnDatabaseHelper.END_VLAN, reservation.getEndVlan());
        values.put(AutobahnDatabaseHelper.START_NSA, reservation.getStartNsa());
        values.put(AutobahnDatabaseHelper.END_NSA, reservation.getEndNsa());
        values.put(AutobahnDatabaseHelper.START_PORT, reservation.getStartPort());
        values.put(AutobahnDatabaseHelper.END_PORT, reservation.getEndPort());
        values.put(AutobahnDatabaseHelper.MAX_DELAY, reservation.getMaxDelay());
        values.put(AutobahnDatabaseHelper.PROCESS_NOW, reservation.getProcessNow());
        values.put(AutobahnDatabaseHelper.START_TIME, reservation.getStartTime());
        values.put(AutobahnDatabaseHelper.END_TIME, reservation.getEndTime());
        values.put(AutobahnDatabaseHelper.TIMEZONE, reservation.getTimeZone());
        database.insert(AutobahnDatabaseHelper.RESERVATION_INFO, null, values);
    }

    public void updateReservation(ReservationInfo reservation) {
        String selection = AutobahnDatabaseHelper.RESERVATION_ID + "='" + reservation.getId() + "'";
        ContentValues values = new ContentValues();

        values.put(AutobahnDatabaseHelper.RESERVATION_ID, reservation.getId());
        values.put(AutobahnDatabaseHelper.DESCRIPTION, reservation.getDescription());
        values.put(AutobahnDatabaseHelper.RESERVATION_STATE, reservation.getReservationState());
        values.put(AutobahnDatabaseHelper.PROVISION_STATE, reservation.getProvisionState());
        values.put(AutobahnDatabaseHelper.LIFECYCLE_STATE, reservation.getLifecycleState());
        values.put(AutobahnDatabaseHelper.CAPACITY, reservation.getCapacity());
        values.put(AutobahnDatabaseHelper.MTU, reservation.getMtu());
        values.put(AutobahnDatabaseHelper.START_VLAN, reservation.getStartVlan());
        values.put(AutobahnDatabaseHelper.END_VLAN, reservation.getEndVlan());
        values.put(AutobahnDatabaseHelper.START_NSA, reservation.getStartNsa());
        values.put(AutobahnDatabaseHelper.END_NSA, reservation.getEndNsa());
        values.put(AutobahnDatabaseHelper.START_PORT, reservation.getStartPort());
        values.put(AutobahnDatabaseHelper.END_PORT, reservation.getEndPort());
        values.put(AutobahnDatabaseHelper.MAX_DELAY, reservation.getMaxDelay());
        values.put(AutobahnDatabaseHelper.PROCESS_NOW, reservation.getProcessNow());
        values.put(AutobahnDatabaseHelper.START_TIME, reservation.getStartTime());
        values.put(AutobahnDatabaseHelper.END_TIME, reservation.getEndTime());
        values.put(AutobahnDatabaseHelper.TIMEZONE, reservation.getTimeZone());
        database.update(AutobahnDatabaseHelper.RESERVATION_INFO, values, selection, null);
    }

    public void insertPort(List<Port> ports) {
        for (Port port : ports) {
            ContentValues values = new ContentValues();
            values.put(AutobahnDatabaseHelper.PORT_ID, port.getPortId());
            values.put(AutobahnDatabaseHelper.PORT_NAME, port.getPortName());
            values.put(AutobahnDatabaseHelper.PORT_DOMAIN, port.getDomainName());
            database.insert(AutobahnDatabaseHelper.PORTS, null, values);
        }
    }

    public List<Domain> getDomains() throws AutobahnClientException {
        List<Domain> domains = new ArrayList<>();
        String[] columns = {AutobahnDatabaseHelper.DOMAIN_ID, AutobahnDatabaseHelper.DOMAIN_NAME};
        Cursor cursor = database.query(AutobahnDatabaseHelper.DOMAINS, columns, null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            try {
                AutobahnClient.getInstance(context).fetchDomains();
            } catch (AutobahnClientException e) {
                throw new AutobahnClientException(e.getError(), e.getMessage());
            }
        }
        cursor = database.query(AutobahnDatabaseHelper.DOMAINS, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Domain domain = new Domain(cursor.getString(0), cursor.getString(1));
                domains.add(domain);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return domains;
    }

    public List<Reservation> getReservations(String domain) throws AutobahnClientException {
        List<Reservation> reservations = new ArrayList<>();
        String[] columns = {AutobahnDatabaseHelper.RESERVATION_ID, AutobahnDatabaseHelper.RESERVATION_IS_ACTIVE};
        String selection = AutobahnDatabaseHelper.RESERVATION_DOMAIN + "='" + domain + "'";
        Cursor cursor = database.query(AutobahnDatabaseHelper.RESERVATIONS, columns, selection, null, null, null, null);
        if (!cursor.moveToFirst()) {
            try {
                AutobahnClient.getInstance(context).fetchDomainReservations(domain);
            } catch (AutobahnClientException e) {
                throw new AutobahnClientException(e.getError(), e.getMessage());
            }
        }
        cursor = database.query(AutobahnDatabaseHelper.RESERVATIONS, columns, selection, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Reservation reservation = new Reservation(cursor.getString(0), domain, cursor.getInt(1) == 1);
                reservations.add(reservation);
                cursor.moveToNext();
            }
        }
        cursor.close();
        return reservations;
    }

    public ReservationInfo getReservation(String reservationID, String domain) throws AutobahnClientException {
        ReservationInfo reservation = new ReservationInfo();
        String selection = AutobahnDatabaseHelper.RESERVATION_ID + "='" + reservationID + "'";
        Cursor cursor = database.query(AutobahnDatabaseHelper.RESERVATION_INFO, null, selection, null, null, null, null);
        if (!cursor.moveToFirst()) {
            try {
                AutobahnClient.getInstance(context).fetchReservationInfo(reservationID, domain);
            } catch (AutobahnClientException e) {
                throw new AutobahnClientException(e.getError(), e.getMessage());
            }
        }
        cursor = database.query(AutobahnDatabaseHelper.RESERVATION_INFO, null, selection, null, null, null, null);

        if (cursor.moveToFirst()) {
            reservation.setId(cursor.getString(0));
            reservation.setDescription(cursor.getString(1));
            reservation.setReservationState(cursor.getString(2));
            reservation.setProvisionState(cursor.getString(3));
            reservation.setLifecycleState(cursor.getString(4));
            reservation.setCapacity(cursor.getLong(5));
            reservation.setMtu(cursor.getInt(6));
            reservation.setStartVlan(cursor.getInt(7));
            reservation.setEndVlan(cursor.getInt(8));
            reservation.setStartNsa(cursor.getString(9));
            reservation.setEndNsa(cursor.getString(10));
            reservation.setStartPort(cursor.getString(11));
            reservation.setEndPort(cursor.getString(12));
            reservation.setMaxDelay(cursor.getInt(13));
            if (cursor.getInt(14) == 1)
                reservation.setProcessNow(true);
            else
                reservation.setProcessNow(false);
            reservation.setStartTime(cursor.getLong(15));
            reservation.setEndTime(cursor.getLong(16));
            reservation.setTimeZone(cursor.getString(17));
        }
        cursor.close();
        return reservation;
    }

    public Map<Domain, List<Port>> getPorts() throws AutobahnClientException {
        Map<Domain, List<Port>> ports = new HashMap<>();
        String[] columns = {AutobahnDatabaseHelper.PORT_NAME, AutobahnDatabaseHelper.PORT_ID, AutobahnDatabaseHelper.PORT_DOMAIN};
        List<Domain> domains = getDomains();

        if (domains.isEmpty())
            return ports;

        for (Domain domain : domains) {
            ports.put(domain, new ArrayList<Port>());
        }

        Cursor cursor = database.query(AutobahnDatabaseHelper.PORTS, columns, null, null, null, null, null);
        if (!cursor.moveToFirst()) {
            try {
                AutobahnClient.getInstance(context).fetchPorts();
            } catch (AutobahnClientException e) {
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
            }
        }

        cursor = database.query(AutobahnDatabaseHelper.PORTS, columns, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                Port port = new Port(cursor.getString(0), cursor.getString(1), cursor.getString(2));
                for (Domain domain : ports.keySet()) {
                    if (domain.getDomainName().equals(cursor.getString(2)))
                        ports.get(domain).add(port);
                }
                cursor.moveToNext();
            }
        }

        Map<Domain, List<Port>> temp = new HashMap<>(ports);

        for (Domain domain : temp.keySet()) {
            if (temp.get(domain).isEmpty()) {
                ports.remove(domain);
            }
        }

        cursor.close();
        return ports;
    }

    public void deleteData() {
        database.delete(AutobahnDatabaseHelper.PORTS, null, null);
        database.delete(AutobahnDatabaseHelper.RESERVATIONS, null, null);
        database.delete(AutobahnDatabaseHelper.DOMAINS, null, null);
        database.delete(AutobahnDatabaseHelper.RESERVATION_INFO, null, null);
    }
}
