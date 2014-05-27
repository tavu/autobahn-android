package autobahn.android;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import autobahn.android.utils.AutobahnClientException;
import autobahn.android.utils.AutobahnDataSource;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: Nl0st
 * Date: 31/7/2013
 * Time: 5:43 μμ
 * To change this template use File | Settings | File Templates.
 */
public class SingleCircuitActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {

    public final String TAG = "[Autobahn-client]";
    private ProgressDialog progressDialog = null;
    private AutobahnDataSource dataSource;
    private ReservationInfo reservationInfo;
    private String reservationId;
    private String domainName;
    private AutobahnClientException exception;
    private Toast toast;
    private SwipeRefreshLayout swipeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.single_reservation_activity);

        swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Reservation Info");

        Bundle bundle = getIntent().getExtras();
        reservationId = bundle.getString("SERVICE_ID");
        domainName = bundle.getString("DOMAIN_NAME");

        dataSource = new AutobahnDataSource(this);
        dataSource.open();

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        new SingleReservationTask().execute();
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

    private class SingleReservationTask extends AsyncTask<Void, Void, Void> {

        public SingleReservationTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            reservationInfo = new ReservationInfo();
            try {
                reservationInfo = dataSource.getReservation(reservationId, domainName);
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
            showReservationInfo();
        }
    }

    private class ProvisionTask extends AsyncTask<Void, Void, Void> {

        public ProvisionTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                AutobahnClient.getInstance(getApplicationContext()).provision(domainName, reservationId);
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
                return;
            }
            new SingleReservationTask().execute();
        }
    }

    private class ReleaseTask extends AsyncTask<Void, Void, Void> {

        public ReleaseTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                AutobahnClient.getInstance(getApplicationContext()).release(domainName, reservationId);
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
                return;
            }
            new SingleReservationTask().execute();
        }
    }


    private class CancelTask extends AsyncTask<Void, Void, Void> {

        public CancelTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                AutobahnClient.getInstance(getApplicationContext()).cancel(domainName, reservationId);
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
                return;
            }

            new SingleReservationTask().execute();
        }
    }

    private class UpdateTask extends AsyncTask<Void, Void, Void> {

        public UpdateTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                AutobahnClient.getInstance(getApplicationContext()).updateReservation(domainName, reservationId);
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

            new SingleReservationTask().execute();
        }
    }

    public synchronized void showReservationInfo() {

        ArrayList<String> info = new ArrayList<>();
        info.add(reservationId);
        info.add(domainName);

        ((TextView)findViewById(R.id.service)).setText(reservationInfo.getId());
        ((TextView)findViewById(R.id.description)).setText(reservationInfo.getDescription());
        ((TextView)findViewById(R.id.reservationState)).setText(reservationInfo.getReservationState());
        ((TextView)findViewById(R.id.provisionState)).setText(reservationInfo.getProvisionState());
        ((TextView)findViewById(R.id.lifecycleState)).setText(reservationInfo.getLifecycleState());
        ((TextView)findViewById(R.id.startTime)).setText(new Date(reservationInfo.getStartTime()).toGMTString());
        ((TextView)findViewById(R.id.endTime)).setText(new Date(reservationInfo.getEndTime()).toGMTString());
        ((TextView)findViewById(R.id.endPort)).setText(reservationInfo.getEndPort());
        ((TextView)findViewById(R.id.startPort)).setText(reservationInfo.getStartPort());
        ((TextView)findViewById(R.id.startVlan)).setText(String.valueOf(reservationInfo.getStartVlan()));
        ((TextView)findViewById(R.id.endVlan)).setText(String.valueOf(reservationInfo.getEndVlan()));
        ((TextView)findViewById(R.id.capacity)).setText(String.valueOf(reservationInfo.getCapacity()/1000000));
        ((TextView)findViewById(R.id.mtuSize)).setText(String.valueOf(reservationInfo.getMtu()));

        Button button = (Button) findViewById(R.id.provision);
        if (reservationInfo.getReservationState().equals("ReserveStart") && reservationInfo.getProvisionState().equals("Released") && reservationInfo.getLifecycleState().equals("Created"))
            button.setVisibility(View.VISIBLE);
        else
            button.setVisibility(View.GONE);

        button = (Button) findViewById(R.id.release);
        if (reservationInfo.getReservationState().equals("ReserveStart") && reservationInfo.getProvisionState().equals("Provisioned") && reservationInfo.getLifecycleState().equals("Created"))
            button.setVisibility(View.VISIBLE);
        else
            button.setVisibility(View.GONE);

        button = (Button) findViewById(R.id.cancel);
        if (reservationInfo.getLifecycleState().equals("PassedEndTime") || reservationInfo.getReservationState().equals("ReserveFailed") || reservationInfo.getReservationState().equals("ReserveTimeout"))
            button.setVisibility(ViewGroup.GONE);

    }

    public void resubmitService(View v){
        Intent intent = new Intent();
        intent.setClass(getApplicationContext(), RequestReservation.class);
        intent.putExtra("RESUBMIT_SERVICE", reservationInfo);
        startActivity(intent);
    }

    public void provisionService(View v) {

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        new ProvisionTask().execute();
    }

    public void releaseService(View v) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        new ReleaseTask().execute();

    }

    public void cancelService(View v) {
        final View view = v;
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
        myAlertDialog.setTitle(getString(R.string.cancelService));
        myAlertDialog.setMessage(getString( R.string.cancel_question));

        myAlertDialog.setNegativeButton(getString( R.string.no), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
            }
        });

        myAlertDialog.setPositiveButton(getString( R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                progressDialog = new ProgressDialog(view.getContext());
                progressDialog.setMessage(getString(R.string.loading));
                progressDialog.show();

                new CancelTask().execute();
            }
        });

        myAlertDialog.show();
    }

    @Override
    public void onRefresh() {
        new UpdateTask().execute();
    }
}