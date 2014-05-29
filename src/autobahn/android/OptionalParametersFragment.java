package autobahn.android;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import autobahn.android.utils.*;
import com.example.autobahn.R;
import net.geant.autobahn.android.Domain;
import net.geant.autobahn.android.Port;
import net.geant.autobahn.android.ReservationInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nl0st on 24/4/2014.
 */

public class OptionalParametersFragment extends android.support.v4.app.Fragment {

    private final String TAG = "[Autobahn-client]";
    private View view;
    private CustomExpandableListAdapter listAdapter;
    private ExpandableListView listView;
    private List<Category> listDataChild = new ArrayList<>();
    private AutobahnClientException exception;
    private List<Domain> domains;
    private List ports;
    private Map<Domain, List<Port>> data;
    private ProgressDialog progressDialog;
    private AutobahnDataSource dataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.request_optional_params, container, false);
        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("RESUBMIT_SERVICE")) {
            ReservationInfo resubmissionInfo = (ReservationInfo) bundle.getSerializable("RESUBMIT_SERVICE");
            insertResubmissionData(resubmissionInfo);

        } else {
            dataSource = new AutobahnDataSource(getActivity().getApplicationContext());
            dataSource.open();

            progressDialog = new ProgressDialog(view.getRootView().getContext());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();

            new PortTask().execute();


        }
        return view;
    }

    public synchronized void populateLists() {

        ports = new ArrayList<>();
        for (Domain domain : data.keySet()) {
            ports.add(domain);
            ports.addAll(data.get(domain));
        }

        listDataChild.add(new Category<>("Included Domains", domains));
        listDataChild.add(new Category<>("Included Ports", ports));
        listDataChild.add(new Category<>("Excluded Domains", domains));
        listDataChild.add(new Category<>("Excluded Ports", ports));

        listView = (ExpandableListView) view.findViewById(R.id.pathConstraints);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        listAdapter = new CustomExpandableListAdapter(getActivity(), listDataChild);

        listView.setAdapter(listAdapter);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                switch (groupPosition) {
                    case 0:
                    case 2:
                        listAdapter.setClicked(groupPosition, childPosition);
                        ((CheckBox) v.findViewById(R.id.checkBox)).setChecked(listAdapter.getCheckedPositions().get(groupPosition).get(childPosition));
                        break;
                    case 1:
                    case 3:
                        if (listAdapter.getChild(groupPosition, childPosition) instanceof Port) {
                            listAdapter.setClicked(groupPosition, childPosition);
                            ((CheckBox) v.findViewById(R.id.checkBox)).setChecked(listAdapter.getCheckedPositions().get(groupPosition).get(childPosition));
                        }
                        break;
                }

                return false;
            }
        });
    }

    public synchronized void insertResubmissionData(ReservationInfo reservation) {

        ArrayList<Domain> includedDomains = new ArrayList<>();
        ArrayList<Domain> excludedDomains = new ArrayList<>();
        ArrayList<Port> includedPorts = new ArrayList<>();
        ArrayList<Port> excludedPorts = new ArrayList<>();

        if (reservation.getIncludedDomains() != null)
            for (String domain : reservation.getIncludedDomains())
                includedDomains.add(new Domain(domain));

        if (reservation.getExcludedDomains() != null)
            for (String domain : reservation.getExcludedDomains())
                excludedDomains.add(new Domain(domain));

        if (reservation.getIncludedStps() != null)
            for (String port : reservation.getIncludedStps())
                includedPorts.add(new Port(port));

        if (reservation.getExcludedStps() != null)
            for (String port : reservation.getExcludedStps())
                excludedPorts.add(new Port(port));

        listDataChild.add(new Category<>("Included Domains", includedDomains));
        listDataChild.add(new Category<>("Included Ports", includedPorts));
        listDataChild.add(new Category<>("Excluded Domains", excludedDomains));
        listDataChild.add(new Category<>("Excluded Ports", excludedPorts));

        listView = (ExpandableListView) view.findViewById(R.id.pathConstraints);

        listAdapter = new CustomExpandableListAdapter(getActivity(), listDataChild);

        listView.setAdapter(listAdapter);
        listView.setClickable(false);


    }

    private class PortTask extends AsyncTask<Void, Void, Void> {

        public PortTask() {
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                data = dataSource.getPorts();
            } catch (AutobahnClientException e) {
                exception = e;
            }

            domains = new ArrayList<>();
            for (Domain domain : data.keySet())
                domains.add(domain);
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
                Log.d(TAG, exception.getMessage());
                showError(exception);
                return;
            }
            populateLists();
        }
    }

    protected synchronized void showError(AutobahnClientException e) {
        getLayoutInflater(this.getArguments()).inflate(R.layout.error_layout, (ViewGroup) view.getParent());
        ((TextView) view.findViewById(R.id.errorText)).setText(e.getMessage());
    }
}