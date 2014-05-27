package autobahn.android;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import autobahn.android.utils.AutobahnClientException;
import autobahn.android.utils.AutobahnDataSource;
import autobahn.android.utils.CustomExpandableListAdapter;
import com.example.autobahn.R;
import net.geant.autobahn.android.Domain;
import net.geant.autobahn.android.Port;
import net.geant.autobahn.android.ReservationInfo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Nl0st on 24/4/2014.
 */

public class BasicParametersFragment extends android.support.v4.app.Fragment implements EditText.OnFocusChangeListener,
        CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener,
        DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener {

    public final String TAG = "[Autobahn-client]";
    private View view;
    private View lastClickedView;
    private AutobahnClientException exception;
    private List<Domain> domains;
    private Map<Domain, List<Port>> data;
    private ProgressDialog progressDialog;
    private AutobahnDataSource dataSource;
    private ReservationInfo res;
    private String reservationID;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.request_reservation_activity, container, false);

        dataSource = new AutobahnDataSource(getActivity().getApplicationContext());
        dataSource.open();

        ((CheckBox) view.findViewById(R.id.endVlanAuto)).setOnCheckedChangeListener(this);
        ((CheckBox) view.findViewById(R.id.startVlanAuto)).setOnCheckedChangeListener(this);
        ((CheckBox) view.findViewById(R.id.startNow)).setOnCheckedChangeListener(this);

        view.findViewById(R.id.submitButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitRequest(view);
            }
        });

        Bundle bundle = getActivity().getIntent().getExtras();
        if (bundle != null && bundle.containsKey("RESUBMIT_SERVICE")) {
            ReservationInfo resubmissionInfo = (ReservationInfo) bundle.getSerializable("RESUBMIT_SERVICE");
            insertResubmissionData(resubmissionInfo);
        } else {
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setMessage(getString(R.string.loading));
            progressDialog.show();
            new PortTask().execute();

            ((CheckBox) view.findViewById(R.id.startVlanAuto)).setChecked(true);
            ((CheckBox) view.findViewById(R.id.endVlanAuto)).setChecked(true);

            ((Spinner) view.findViewById(R.id.startDomain)).setOnItemSelectedListener(this);
            ((Spinner) view.findViewById(R.id.endDomain)).setOnItemSelectedListener(this);

            ((Spinner) view.findViewById(R.id.startPort)).setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item));
            ((Spinner) view.findViewById(R.id.endPort)).setAdapter(new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item));
        }

        view.findViewById(R.id.startDate).setOnFocusChangeListener(this);
        view.findViewById(R.id.endDate).setOnFocusChangeListener(this);
        view.findViewById(R.id.startTime).setOnFocusChangeListener(this);
        view.findViewById(R.id.endTime).setOnFocusChangeListener(this);

        return view;
    }

    @Override
    public void onPause() {
        dataSource.close();
        super.onPause();
    }

    @Override
    public void onResume() {
        dataSource.open();
        super.onResume();
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
                showError(exception);
                return;
            }
            setDomains();
        }
    }

    private class SubmitTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                reservationID = AutobahnClient.getInstance(view.getContext()).submitReservation(res);
            } catch (AutobahnClientException e) {
                showError(e);
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (progressDialog != null) {
                progressDialog.dismiss();
                progressDialog = null;
            }

            if (exception != null) {
                Log.d(TAG, exception.getMessage());
                showError(exception);
            } else {
                Intent intent = new Intent();
                intent.setClass(getActivity().getApplicationContext(), SingleCircuitActivity.class);
                intent.putExtra("SERVICE_ID", reservationID);
                intent.putExtra("DOMAIN_NAME", res.getStartNsa());
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

    protected synchronized void setDomains() {
        ArrayAdapter<Domain> adapter;

        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, domains);
        Spinner sp1 = (Spinner) view.findViewById(R.id.startDomain);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp1.setAdapter(adapter);

        adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, domains);
        Spinner sp2 = (Spinner) view.findViewById(R.id.endDomain);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp2.setAdapter(adapter);
    }

    protected synchronized void setPorts(int parentId) {
        ArrayAdapter<Port> adapter;
        ArrayList<Port> ports;

        Spinner sp = (Spinner) view.findViewById(parentId);
        int pos = sp.getSelectedItemPosition();

        if (pos != AdapterView.INVALID_POSITION) {
            Domain domain = (Domain) sp.getItemAtPosition(pos);
            if (parentId == R.id.startDomain)
                sp = (Spinner) view.findViewById(R.id.startPort);
            else
                sp = (Spinner) view.findViewById(R.id.endPort);

            ports = (ArrayList<Port>) data.get(domain);

            adapter = new ArrayAdapter<>(view.getContext(), android.R.layout.simple_spinner_item, ports);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter);
        }
    }

    protected synchronized void showError(AutobahnClientException e) {
        getLayoutInflater(this.getArguments()).inflate(R.layout.error_layout, (ViewGroup) view.getParent());
        ((TextView) view.findViewById(R.id.errorText)).setText(e.getMessage());
    }

    public void insertResubmissionData(ReservationInfo resubmissionInfo) {
        ArrayList<Domain> domainList = new ArrayList<>();
        ArrayList<Port> portList = new ArrayList<>();
        Spinner spinner;
        ArrayAdapter<Domain> domainAdapter;
        ArrayAdapter<Port> portAdapter;

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
        Date date;

        domainList.add(new Domain(resubmissionInfo.getStartNsa(), resubmissionInfo.getStartNsa().split(":")[3]));
        spinner = (Spinner) view.findViewById(R.id.startDomain);
        domainAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, domainList);
        domainAdapter.setDropDownViewResource(R.layout.list_item);
        spinner.setAdapter(domainAdapter);
        spinner.setEnabled(false);

        portList.add(new Port(resubmissionInfo.getStartPort().split(":")[6], resubmissionInfo.getStartPort()));
        spinner = (Spinner) view.findViewById(R.id.startPort);
        portAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, portList);
        spinner.setAdapter(portAdapter);
        spinner.setEnabled(false);

        domainList = new ArrayList<>();
        domainList.add(new Domain(resubmissionInfo.getEndNsa(), resubmissionInfo.getEndNsa().split(":")[3]));
        spinner = (Spinner) view.findViewById(R.id.endDomain);
        domainAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, domainList);
        spinner.setAdapter(domainAdapter);
        spinner.setEnabled(false);

        portList = new ArrayList<>();
        portList.add(new Port(resubmissionInfo.getEndPort().split(":")[6], resubmissionInfo.getEndPort()));
        spinner = (Spinner) view.findViewById(R.id.endPort);
        portAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, portList);
        spinner.setAdapter(portAdapter);
        spinner.setEnabled(false);

        date = new Date(resubmissionInfo.getStartTime());
        ((TextView) view.findViewById(R.id.startDate)).setText(dateFormat.format(date));
        ((TextView) view.findViewById(R.id.startTime)).setText(timeFormat.format(date));
        date = new Date(resubmissionInfo.getEndTime());
        ((TextView) view.findViewById(R.id.endDate)).setText(dateFormat.format(date));
        ((TextView) view.findViewById(R.id.endTime)).setText(timeFormat.format(date));

        if (resubmissionInfo.getStartVlan() == 0)
            ((CheckBox) view.findViewById(R.id.startVlanAuto)).setChecked(true);
        else
            ((TextView) view.findViewById(R.id.startVlan)).setText(String.valueOf(resubmissionInfo.getStartVlan()));

        if (resubmissionInfo.getEndVlan() == 0)
            ((CheckBox) view.findViewById(R.id.endVlanAuto)).setChecked(true);
        else
            ((TextView) view.findViewById(R.id.endVlan)).setText(String.valueOf(resubmissionInfo.getEndVlan()));

        ((TextView) view.findViewById(R.id.capacity)).setText(String.valueOf(resubmissionInfo.getCapacity() / 1000000));
        ((TextView) view.findViewById(R.id.description)).setText(resubmissionInfo.getDescription());
    }

    @Override
    public void onCheckedChanged(CompoundButton button, boolean checked) {
        switch (button.getId()) {
            case R.id.startVlanAuto:
                if (checked) {
                    view.findViewById(R.id.startVlan).setEnabled(false);
                } else {
                    view.findViewById(R.id.startVlan).setEnabled(true);
                }
                break;
            case R.id.endVlanAuto:
                if (checked) {
                    view.findViewById(R.id.endVlan).setEnabled(false);
                } else {
                    view.findViewById(R.id.endVlan).setEnabled(true);
                }
                break;
            case R.id.startNow:
                if (checked) {
                    view.findViewById(R.id.startDate).setEnabled(false);
                    view.findViewById(R.id.startTime).setEnabled(false);
                } else {
                    view.findViewById(R.id.startDate).setEnabled(true);
                    view.findViewById(R.id.startTime).setEnabled(true);
                }
        }
    }

    @Override
    public void onFocusChange(View view, boolean focus) {
        if (focus) {
            switch (view.getId()) {
                case R.id.startDate:
                    showDatePicker(view);
                    break;
                case R.id.startTime:
                    showTimePicker(view);
                    break;
                case R.id.endDate:
                    showDatePicker(view);
                    break;
                case R.id.endTime:
                    showTimePicker(view);
                    break;
            }
        }
    }

    public void showDatePicker(View view) {
        DatePickerDialog dialog = new DatePickerDialog(view.getContext(), this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
        lastClickedView = view;
        dialog.show();
    }

    public void showTimePicker(View view) {
        TimePickerDialog dialog = new TimePickerDialog(view.getContext(), this, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
        lastClickedView = view;
        dialog.show();
    }

    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        EditText dateDisplay = (EditText) lastClickedView;

        Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());
        dateDisplay.setText(dateFormat.format(date));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        EditText timeDisplay = (EditText) lastClickedView;

        SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
        Date time = null;

        try {
            time = sdf.parse(hourOfDay + ":" + minute);
        } catch (ParseException ignored) {
        }


        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(getActivity().getApplicationContext());
        timeDisplay.setText(timeFormat.format(time));
    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (parent.getId() == R.id.endDomain || parent.getId() == R.id.startDomain) {
            Domain s = (Domain) parent.getItemAtPosition(pos);
            if (s != null)
                setPorts(parent.getId());
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {
    }

    public void submitRequest(View view) {

        res = new ReservationInfo();
        CheckBox checkBox;
        Toast toast;
        ArrayList<String> includedDomains = new ArrayList<>();
        ArrayList<String> includedStps = new ArrayList<>();
        ArrayList<String> excludedDomains = new ArrayList<>();
        ArrayList<String> excludedStps = new ArrayList<>();

        try {
            res.setStartNsa(spinnerData(R.id.startDomain));
        } catch (Exception e1) {
            toast = Toast.makeText(view.getContext(), R.string.select_start_port, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            res.setStartPort(spinnerData(R.id.startPort));
        } catch (Exception e1) {
            toast = Toast.makeText(view.getContext(), R.string.select_start_dom, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            res.setEndNsa(spinnerData(R.id.endDomain));
        } catch (Exception e1) {
            toast = Toast.makeText(view.getContext(), R.string.select_end_port, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            res.setEndPort(spinnerData(R.id.endPort));
        } catch (Exception e1) {
            toast = Toast.makeText(view.getContext(), R.string.select_end_dom, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        checkBox = (CheckBox) view.findViewById(R.id.startVlanAuto);
        if (checkBox.isChecked()) {
            res.setStartVlan(0);
        } else {
            try {
                res.setStartVlan(txtData(R.id.startVlan));
            } catch (Exception e1) {
                toast = Toast.makeText(view.getContext(), R.string.invalid_start_vlan, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }

        checkBox = (CheckBox) view.findViewById(R.id.endVlanAuto);
        if (checkBox.isChecked()) {
            res.setEndVlan(0);
        } else {
            try {
                res.setEndVlan(txtData(R.id.endVlan));
            } catch (Exception e1) {
                toast = Toast.makeText(view.getContext(), R.string.invalid_end_vlan, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }

        checkBox = (CheckBox) view.findViewById(R.id.startNow);
        if (checkBox.isChecked()) {
            res.setProcessNow(true);
        } else {
            try {
                res.setStartTime(dateFromStr(R.id.startDate, R.id.startTime));
            } catch (Exception e) {
                toast = Toast.makeText(view.getContext(), R.string.invalid_start_time, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        }

        try {
            res.setEndTime(dateFromStr(R.id.endDate, R.id.endTime));
        } catch (Exception e) {
            toast = Toast.makeText(view.getContext(), R.string.invalid_end_time, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        try {
            res.setCapacity((long) txtData(R.id.capacity));
        } catch (Exception e1) {
            toast = Toast.makeText(view.getContext(), R.string.invalid_capacity, Toast.LENGTH_LONG);
            toast.show();
            return;
        }

        EditText txt = (EditText) view.findViewById(R.id.description);
        String s = txt.getText().toString();
        if (s == null || s.isEmpty()) {
            toast = Toast.makeText(view.getContext(), R.string.empty_description, Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        res.setDescription(s);

        ExpandableListView optionalList = (ExpandableListView) getActivity().findViewById(R.id.pathConstraints);
        CustomExpandableListAdapter adapter = (CustomExpandableListAdapter) optionalList.getExpandableListAdapter();
        ArrayList<boolean[]> checkedStates = adapter.getCheckedStates();

        for (int i = 0; i < adapter.getChildrenCount(0); i++) {
            if (checkedStates.get(0)[i])
                includedDomains.add(((HashMap<String, String>) adapter.getChild(0, i)).get("CHILD"));
        }

        for (int i = 0; i < adapter.getChildrenCount(1); i++) {
            if (checkedStates.get(1)[i])
                includedStps.add(((HashMap<String, String>) adapter.getChild(1, i)).get("CHILD"));
        }

        for (int i = 0; i < adapter.getChildrenCount(2); i++) {
            if (checkedStates.get(2)[i])
                excludedDomains.add(((HashMap<String, String>) adapter.getChild(2, i)).get("CHILD"));
        }

        for (int i = 0; i < adapter.getChildrenCount(3); i++) {
            if (checkedStates.get(3)[i])
                excludedStps.add(((HashMap<String, String>) adapter.getChild(3, i)).get("CHILD"));
        }


        res.setIncludedDomains(includedDomains);
        res.setExcludedDomains(excludedDomains);
        res.setIncludedStps(includedStps);
        res.setExcludedStps(excludedStps);

        if (checkReservation(res)) {
            progressDialog = new ProgressDialog(view.getContext());
            progressDialog.setMessage(getString(R.string.submitting));
            progressDialog.show();
            new SubmitTask().execute();
        }
    }

    private String spinnerData(int id) throws Exception {
        Spinner sp = (Spinner) view.findViewById(id);
        int pos = sp.getSelectedItemPosition();

        if (pos == AdapterView.INVALID_POSITION) {
            throw new Exception("Data not selected at spinner " + sp.toString());
        }

        switch (id) {
            case R.id.startDomain:
            case R.id.endDomain:
                return ((Domain) sp.getItemAtPosition(pos)).getDomainName();
            case R.id.startPort:
            case R.id.endPort:
                return ((Port) sp.getItemAtPosition(pos)).getPortId();
        }
        return null;
    }

    private int txtData(int id) throws Exception {
        EditText text = (EditText) view.findViewById(id);
        return Integer.parseInt(text.getText().toString());
    }

    private long dateFromStr(int dateView, int timeView) throws Exception {

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(view.getContext());
        DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(view.getContext());

        String startDateStr = ((EditText) view.findViewById(dateView)).getText().toString();
        Date startDate = dateFormat.parse(startDateStr);

        String startTimeStr = ((EditText) view.findViewById(timeView)).getText().toString();
        Date startTime = timeFormat.parse(startTimeStr);

        startDate.setHours(startTime.getHours());
        startDate.setMinutes(startTime.getMinutes());

        return startDate.getTime();
    }

    private boolean checkReservation(ReservationInfo res) {
        Toast toast;

        if (res.getStartPort().equals(res.getEndPort())) {
            toast = Toast.makeText(view.getContext(), R.string.port_equals, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        Date now = new Date();
        Date ed = new Date(res.getEndTime());
        if (ed.before(now)) {
            toast = Toast.makeText(view.getContext(), R.string.end_time_past, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        if (!res.getProcessNow()) {
            Date sd = new Date(res.getStartTime());
            if (sd.before(now)) {
                toast = Toast.makeText(view.getContext(), R.string.start_time_past, Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
            if (sd.after(ed)) {
                toast = Toast.makeText(view.getContext(), R.string.end_before_start, Toast.LENGTH_LONG);
                toast.show();
                return false;
            }
        }

        if (res.getCapacity() <= 0) {
            toast = Toast.makeText(view.getContext(), R.string.invalid_capacity, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        if (res.getExcludedDomains().contains(res.getStartNsa()) || res.getExcludedDomains().contains(res.getEndNsa())) {
            toast = Toast.makeText(view.getContext(), R.string.excluded_domains_error, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        if (res.getExcludedStps().contains(res.getStartPort()) || res.getExcludedStps().contains(res.getEndPort())) {
            toast = Toast.makeText(view.getContext(), R.string.excluded_ports_error, Toast.LENGTH_LONG);
            toast.show();
            return false;
        }

        return true;
    }

}