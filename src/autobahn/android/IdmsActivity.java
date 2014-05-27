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
import com.example.autobahn.R;
import net.geant.autobahn.android.Domain;

import java.util.ArrayList;
import java.util.List;


public class IdmsActivity extends FragmentActivity implements SwipeRefreshLayout.OnRefreshListener {

    public final String TAG = "[Autobahn-client]";
    private ProgressDialog progressDialog = null;
    private List<Domain> domains;
    private ListView domainList;
    private ArrayAdapter<Domain> adapter;
    private AutobahnDataSource dataSource;
    private AutobahnClientException exception;
    private Toast toast;
    private SwipeRefreshLayout swipeLayout;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dataSource = new AutobahnDataSource(this);
        dataSource.open();

        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setTitle("Domain Selection");

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        new DomainsAsyncTask().execute();
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

    private synchronized void showData() {

        if (domains == null) {
            return;
        }

        if (domains.isEmpty()) {
            setContentView(R.layout.no_data);
            swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_container);
            swipeLayout.setOnRefreshListener(this);
            swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                    android.R.color.holo_green_light,
                    android.R.color.holo_orange_light,
                    android.R.color.holo_red_light);

            ((TextView) findViewById(R.id.header)).setText(R.string.no_domains);

            Button menuButton = (Button) findViewById(R.id.menuButton);
            menuButton.setText(R.string.back_to_menu);
            menuButton.setOnClickListener(new View.OnClickListener() {
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

            ((TextView) findViewById(R.id.header)).setText(R.string.idm_title);
            domainList = (ListView) findViewById(R.id.listView);
            adapter = new ArrayAdapter<>(this, R.layout.list_item, domains);

            domainList.setAdapter(adapter);
            domainList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    TextView item = (TextView) view;
                    String domain = item.getText().toString();
                    Intent domainActivity = new Intent();
                    domainActivity.setClass(getApplicationContext(), TrackCircuitActivity.class);
                    domainActivity.putExtra("DOMAIN_NAME", domain);
                    startActivity(domainActivity);
                }
            });

            domainList.setOnScrollListener(new AbsListView.OnScrollListener() {
                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {

                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    int topRowVerticalPosition =
                            (domainList == null || domainList.getChildCount() == 0) ?
                                    0 : domainList.getChildAt(0).getTop();
                    swipeLayout.setEnabled(topRowVerticalPosition >= 0);
                }
            });
        }
    }

    private class DomainsAsyncTask extends AsyncTask<Void, Void, Void> {

        public DomainsAsyncTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            domains = new ArrayList<>();
            try {
                domains = dataSource.getDomains();
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
            showData();
        }
    }

    private class UpdateTask extends AsyncTask<Void, Void, Void> {

        public UpdateTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                AutobahnClient.getInstance(getApplicationContext()).updateDomains();
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
            new DomainsAsyncTask().execute();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        showData();
    }

    @Override
    public void onRefresh() {
        new UpdateTask().execute();
    }


}