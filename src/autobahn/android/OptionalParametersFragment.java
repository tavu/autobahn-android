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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Nl0st on 24/4/2014.
 */

public class OptionalParametersFragment extends android.support.v4.app.Fragment {

    private final String TAG = "[Autobahn-client]";
    private final List<String> groupData = new ArrayList<String>() {{
        add("Included Domains");
        add("Included Ports");
        add("Excluded Domains");
        add("Excluded Ports");
    }};

    private View view;
    private CustomExpandableListAdapter listAdapter;
    private ExpandableListView listView;
    private List<List<Map<String, String>>> listDataChild = new ArrayList<>();
    private AutobahnClientException exception;
    private List<Domain> domains;
    private Map<Domain, List<Port>> data;
    private ProgressDialog progressDialog;
    private AutobahnDataSource dataSource;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.request_optional_params, container, false);

        dataSource = new AutobahnDataSource(getActivity().getApplicationContext());
        dataSource.open();

        progressDialog = new ProgressDialog(view.getRootView().getContext());
        progressDialog.setMessage(getString(R.string.loading));
        progressDialog.show();

        new PortTask().execute();

        return view;
    }

    public synchronized void populateLists() {
        List<Map<String, String>> domainChildren = new ArrayList<>();
        List<Map<String, String>> portChildren = new ArrayList<>();

        for (Domain domain : domains) {
            Map<String, String> entry = new HashMap<>();
            entry.put("CHILD", domain.getDomainName());
            domainChildren.add(entry);
        }

        for (Domain domain : data.keySet()) {
            List<Port> domainPorts = data.get(domain);
            Map<String, String> subGroupEntry = new HashMap<>();
            subGroupEntry.put("SUBGROUP", domain.getDomainName());
            portChildren.add(subGroupEntry);
            for (Port port : domainPorts) {
                Map<String, String> entry = new HashMap<>();
                entry.put("CHILD", port.getPortId());
                portChildren.add(entry);
            }
        }

        listDataChild.add(domainChildren);
        listDataChild.add(portChildren);
        listDataChild.add(domainChildren);
        listDataChild.add(portChildren);

        listView = (ExpandableListView) view.findViewById(R.id.pathConstraints);

        listAdapter = new CustomExpandableListAdapter(getActivity(), groupData, listDataChild);
        listView.setAdapter(listAdapter);
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        listView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Map<String, String> child = (HashMap<String, String>) listView.getExpandableListAdapter().getChild(groupPosition, childPosition);
                int position = parent.getFlatListPosition(ExpandableListView.getPackedPositionForChild(groupPosition, childPosition));
                if (child.containsKey("CHILD")) {
                    ((CheckedTextView) v).toggle();
                    ((CustomExpandableListAdapter) listView.getExpandableListAdapter()).toggleCheckedState(groupPosition, childPosition, ((CheckedTextView) v).isChecked());
                    listView.setItemChecked(position, ((CheckedTextView) v).isChecked());
                }
                return false;
            }
        });
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