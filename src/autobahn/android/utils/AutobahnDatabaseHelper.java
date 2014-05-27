package autobahn.android.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Nl0st on 30/4/2014.
 */
public class AutobahnDatabaseHelper extends SQLiteOpenHelper {

    /*Database constants*/
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "autobahn.db";

    /*Domain Table */
    public static final String DOMAINS = "domains";
    public static final String DOMAIN_NAME = "domain_name";
    public static final String DOMAIN_ID = "domain_id";

    /*Reservations Table*/
    public static final String RESERVATIONS = "reservations";
    public static final String RESERVATION_ID = "id";
    public static final String RESERVATION_DOMAIN = "domain_name";
    public static final String RESERVATION_IS_ACTIVE = "is_active";

    /*Ports Table*/
    public static final String PORTS = "ports";
    public static final String PORT_NAME = "port_name";
    public static final String PORT_ID = "port_id";
    public static final String PORT_DOMAIN = "domain_name";

    /*Reservation Information Table */
    public static final String RESERVATION_INFO = "reservation";
    public static final String RESERVATION_STATE = "reservation_state";
    public static final String PROVISION_STATE = "provision_state";
    public static final String LIFECYCLE_STATE = "lifecycle_state";
    public static final String CAPACITY = "capacity";
    public static final String MTU = "mtu";
    public static final String START_VLAN = "start_vlan";
    public static final String END_VLAN = "end_vlan";
    public static final String START_NSA = "start_nsa";
    public static final String END_NSA = "end_nsa";
    public static final String START_PORT = "start_port";
    public static final String END_PORT = "end_port";
    public static final String DESCRIPTION = "description";
    public static final String MAX_DELAY = "max_delay";
    public static final String PROCESS_NOW = "process_now";
    public static final String START_TIME = "start_time";
    public static final String END_TIME = "end_time";
    public static final String TIMEZONE = "timezone";

    private static final String CREATE_TABLE_DOMAINS = "CREATE TABLE " + DOMAINS +
            "(" + DOMAIN_ID + " VARCHAR(255) PRIMARY KEY, " +
            DOMAIN_NAME + " VARCHAR(255));";
    private static final String CREATE_TABLE_RESERVATIONS = "CREATE TABLE " + RESERVATIONS +
            "(" + RESERVATION_ID + " VARCHAR(255) PRIMARY KEY, " +
            RESERVATION_DOMAIN + " VARCHAR(255), " +
            RESERVATION_IS_ACTIVE + " NUMERIC, " +
            "FOREIGN KEY(" + RESERVATION_DOMAIN + ") REFERENCES " + DOMAINS + "(" + DOMAIN_NAME + ")" + ");";
    private static final String CREATE_TABLE_PORTS = "CREATE TABLE " + PORTS + "(" +
            PORT_ID + " VARCHAR(255) PRIMARY KEY, " +
            PORT_NAME + " VARCHAR(255), " +
            PORT_DOMAIN + " VARCHAR(255), " +
            "FOREIGN KEY(" + PORT_DOMAIN + ") REFERENCES " + DOMAINS + "(" + DOMAIN_NAME + ")" + ");";
    private static final String CREATE_TABLE_RESERVATION = "CREATE TABLE " + RESERVATION_INFO +
            "(" + RESERVATION_ID + " VARCHAR(255) PRIMARY KEY, " +
            DESCRIPTION + " VARCHAR(255), " +
            RESERVATION_STATE + " VARCHAR(255), " +
            PROVISION_STATE + " VARCHAR(255), " +
            LIFECYCLE_STATE + " VARCHAR(255), " +
            CAPACITY + " INTEGER, " +
            MTU + " INTEGER, " +
            START_VLAN + " INTEGER, " +
            END_VLAN + " INTEGER, " +
            START_NSA + " VARCHAR(255), " +
            END_NSA + " VARCHAR(255), " +
            START_PORT + " VARCHAR(255), " +
            END_PORT + " VARCHAR(255), " +
            MAX_DELAY + " INTEGER, " +
            PROCESS_NOW + " NUMERIC, " +
            START_TIME + " INTEGER, " +
            END_TIME + " INTEGER, " +
            TIMEZONE + " VARCHAR(255) );";

    public AutobahnDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(CREATE_TABLE_DOMAINS);
        database.execSQL(CREATE_TABLE_RESERVATIONS);
        database.execSQL(CREATE_TABLE_PORTS);
        database.execSQL(CREATE_TABLE_RESERVATION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, int oldVesrion, int newVersion) {
        database.execSQL("DROP TABLE IF EXISTS " + DOMAINS + ";" +
                "DROP TABLE IF EXISTS " + RESERVATIONS + ";" +
                "DROP TABLE IF EXISTS " + PORTS + ";" +
                "DROP TABLE IF EXISTS " + RESERVATION_INFO + ";");
        onCreate(database);
    }


}
