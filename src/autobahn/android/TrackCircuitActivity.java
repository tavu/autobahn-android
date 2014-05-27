package autobahn.android;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.widget.*;
import autobahn.android.utils.AutobahnClientException;
import autobahn.android.utils.AutobahnDataSource;
import autobahn.android.utils.ReservationsArrayAdapter;
import com.example.autobahn.R;
import net.geant.autobahn.android.Reservation;

import java.util.ArrayList;


public class TrackCircuitActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {

    public final String TAG = "[Autobahn-client]";
    private ListView reservationList;
    private ProgressDialog progressDialog = null;
    private ReservationsArrayAdapter adapter;
    private String domainName;
    private ArrayList<Reservation> reservations;
    private AutobahnDataSource dataSource;
    private AutobahnClientException exception;
    private Toast toast;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        domainName = extras.getString("DOMAIN_NAME");

        dataSource = new AutobahnDataSource(this);
        dataSource.open();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Reservations");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();
        new ReservationsTask().execute();
    }

    @Override
    protected void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    protected void onResume() {
        dataSource.open();
        super.onResume();
    }

    public synchronized void showReservations() {
        ArrayList<String> reservationIds = new ArrayList<>();

        if (reservations.isEmpty()) {
            setContentView(R.layout.no_data);
            swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(this);
            swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);
            ((TextView)findViewById(R.id.header)).setText(R.string.noReservations);

            Button button = (Button) findViewById(R.id.menuButton);
            button.setText(R.string.backToDomains);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    finish();
                }
            });
        } else {
            setContentView(R.layout.domain_reservation_list);
            swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(this);
            swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            ((TextView) findViewById(R.id.header)).setText("Past reservations for domain " + domainName);

            reservationList = (ListView) findViewById(R.id.listView);
            adapter = new ReservationsArrayAdapter(this, R.layout.list_item, reservations);
            reservationList.setAdapter(adapter);
            reservationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView item = (TextView) view;
                    String serviceID = item.getText().toString();
                    Intent singleCircuitActivity = new Intent();
                    singleCircuitActivity.setClass(getApplicationContext(), SingleCircuitActivity.class);
                    singleCircuitActivity.putExtra("SERVICE_ID", serviceID);
                    singleCircuitActivity.putExtra("DOMAIN_NAME", domainName);
                    startActivity(singleCircuitActivity);
                }
            });
            reservationList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int topRowVerticalPosition =
                            (reservationList == null || reservationList.getChildCount() == 0) ?
                                    0 : reservationList.getChildAt(0).getTop();
                    swipeLayout.setEnabled(topRowVerticalPosition >= 0);
                }
            });
        }

    }

    private class UpdateTask extends AsyncTask<Void, Void, Void> {

        public UpdateTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                AutobahnClient.getInstance(getApplicationContext()).updateReservations(domainName);
            } catch (AutobahnClientException e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {

            swipeLayout.setRefreshing(false);

            if (exception != null) {
                toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
                toast.show();
                return;
            }
            new ReservationsTask().execute();
        }
    }

    private class ReservationsTask extends AsyncTask<String, Void, Void> {

        public ReservationsTask() {
        }

        @Override
        protected Void doInBackground(String... params) {
            reservations = new ArrayList<>();
            try {
                reservations = (ArrayList<Reservation>) dataSource.getReservations(domainName);
            } catch (AutobahnClientException e) {
                exception = e;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {

            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            if (exception != null) {
                toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
                toast.show();
                finish();
            }
            showReservations();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        showReservations();
    }

    @Override
    public void onRefresh() {
        new UpdateTask().execute();
    }

}