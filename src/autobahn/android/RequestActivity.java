package autobahn.android;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.*;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 9/24/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestActivity extends BasicActivity implements View.OnFocusChangeListener,
		DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
		AdapterView.OnItemSelectedListener,
		CompoundButton.OnCheckedChangeListener {

	private View lastClickedView;
	private AutobahnClientException exception = null;
    private Intent optionalIntent=null;
    private static final int OPTINAL_REQ=10;

	@Override
	protected void showData(Object data, Call c, Object param) {

		if (c == Call.DOMAINS) {
			ArrayList<String> l = ((HashMap<String,ArrayList<String>>)data).get("name");
			if (!l.isEmpty()) {
				getData(Call.PORTS, l.get(0));
			} else {
                showError(new AutobahnClientException(getString(R.string.no_domains)),c,param);
			}

			//setDomains((List<String>)data);
		} else if (c == Call.PORTS) {
			Spinner sp = (Spinner) findViewById(R.id.startDomain);
			if (sp.getAdapter() == null)
				setDomains(NetCache.getInstance().getIdms().get("name"));

			setPorts(((HashMap<String,ArrayList<String>>)data).get("name"), (String) param);
		}
	}

	protected void setPorts(List<String> data, String domain) {
		Spinner sp = (Spinner) findViewById(R.id.endDomain);

		int pos = sp.getSelectedItemPosition();
		if (pos != AdapterView.INVALID_POSITION) {
			String s = (String) sp.getItemAtPosition(pos);
			if (s != null && s.equals(domain)) {
				sp = (Spinner) findViewById(R.id.endPort);
				ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp.setAdapter(adapter);
			}
		}

		sp = (Spinner) findViewById(R.id.startDomain);

		pos = sp.getSelectedItemPosition();
		if (pos != AdapterView.INVALID_POSITION) {
			String s = (String) sp.getItemAtPosition(pos);
			if (s != null && s.equals(domain)) {
				sp = (Spinner) findViewById(R.id.startPort);
				ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, data);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sp.setAdapter(adapter);
			}
		}


	}

	protected void setDomains(List<String> data) {

		ArrayList<String> a1 = new ArrayList<>(data);
		ArrayAdapter<String> startDomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, a1);
		Spinner sp1 = (Spinner) findViewById(R.id.startDomain);
		startDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp1.setAdapter(startDomAdapter);

		ArrayList<String> a2 = new ArrayList<>(data);
		ArrayAdapter<String> endDomAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, a2);
		Spinner sp2 = (Spinner) findViewById(R.id.endDomain);
		endDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp2.setAdapter(endDomAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		NetCache.getInstance().setLastSubmittedId(null);

		setContentView(R.layout.request_reservation_activity);
		CheckBox ch = (CheckBox) findViewById(R.id.endVlanAuto);
		ch.setOnCheckedChangeListener(this);
		ch = (CheckBox) findViewById(R.id.startVlanAuto);
		ch.setOnCheckedChangeListener(this);
		ch = (CheckBox) findViewById(R.id.startNow);
		ch.setOnCheckedChangeListener(this);

		Bundle bundle = getIntent().getExtras();
		if (bundle != null && bundle.containsKey("RESUBMIT_SERVICE")) {
			ReservationInfo resubmissionInfo = (ReservationInfo) bundle.get("RESUBMIT_SERVICE");
			insertResubmissionData(resubmissionInfo);
		} else {
			((CheckBox) findViewById(R.id.startVlanAuto)).setChecked(true);
			((CheckBox) findViewById(R.id.endVlanAuto)).setChecked(true);

			getActionBar().setDisplayHomeAsUpEnabled(true);

			Spinner sp = (Spinner) findViewById(R.id.startDomain);
			sp.setOnItemSelectedListener(this);
			sp = (Spinner) findViewById(R.id.endDomain);
			sp.setOnItemSelectedListener(this);


			getData(Call.DOMAINS, null);

			ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
			sp = (Spinner) findViewById(R.id.startPort);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(adapter);

			adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
			sp = (Spinner) findViewById(R.id.endPort);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp.setAdapter(adapter);
		}

		findViewById(R.id.startDate).setOnFocusChangeListener(this);
		findViewById(R.id.endDate).setOnFocusChangeListener(this);
		findViewById(R.id.startTime).setOnFocusChangeListener(this);
		findViewById(R.id.endTime).setOnFocusChangeListener(this);


	}

	public void insertResubmissionData(ReservationInfo resubmissionInfo) {
		ArrayList<String> optionsList = new ArrayList<>();
		Spinner spinner;
		ArrayAdapter<String> adapter;
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		Date date;

		optionsList.add(resubmissionInfo.getStartNsa());
		spinner = (Spinner) findViewById(R.id.startDomain);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setEnabled(false);

		optionsList = new ArrayList<>();
		optionsList.add(resubmissionInfo.getStartPort());
		spinner = (Spinner) findViewById(R.id.startPort);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setEnabled(false);

		optionsList = new ArrayList<>();
		optionsList.add(resubmissionInfo.getEndNsa());
		spinner = (Spinner) findViewById(R.id.endDomain);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setEnabled(false);

		optionsList = new ArrayList<>();
		optionsList.add(resubmissionInfo.getEndPort());
		spinner = (Spinner) findViewById(R.id.endPort);
		adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, optionsList);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		spinner.setAdapter(adapter);
		spinner.setEnabled(false);

		date = new Date(resubmissionInfo.getStartTime());
		((TextView) findViewById(R.id.startDate)).setText(dateFormat.format(date));
		((TextView) findViewById(R.id.startTime)).setText(timeFormat.format(date));
		date = new Date(resubmissionInfo.getEndTime());
		((TextView) findViewById(R.id.endDate)).setText(dateFormat.format(date));
		((TextView) findViewById(R.id.endTime)).setText(timeFormat.format(date));

		if (resubmissionInfo.getStartVlan() == 0)
			((CheckBox) findViewById(R.id.startVlanAuto)).setChecked(true);
		else
			((TextView) findViewById(R.id.startVlan)).setText(resubmissionInfo.getStartVlan());

		if (resubmissionInfo.getEndVlan() == 0)
			((CheckBox) findViewById(R.id.endVlanAuto)).setChecked(true);
		else
			((TextView) findViewById(R.id.endVlan)).setText(resubmissionInfo.getEndVlan());

		((TextView) findViewById(R.id.capacity)).setText(String.valueOf(resubmissionInfo.getCapacity() / 1000000));
		((TextView) findViewById(R.id.description)).setText(resubmissionInfo.getDescription());

	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		if (view.hasFocus()) {
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

	public void disableCheckboxForms2(CompoundButton view, boolean checked) {
		switch (view.getId()) {
			case R.id.startVlanAuto:
				if (checked) {
					findViewById(R.id.startVlan).setEnabled(false);
				} else {
					findViewById(R.id.startVlan).setEnabled(true);
				}
				break;
			case R.id.endVlanAuto:
				if (checked) {
					findViewById(R.id.endVlan).setEnabled(false);
				} else {
					findViewById(R.id.endVlan).setEnabled(true);
				}
				break;
			case R.id.startNow:
				if (checked) {
					findViewById(R.id.startDate).setEnabled(false);
					findViewById(R.id.startTime).setEnabled(false);
				} else {
					findViewById(R.id.startDate).setEnabled(true);
					findViewById(R.id.startTime).setEnabled(true);
				}
		}
	}

	public void showDatePicker(View view) {
		DatePickerDialog dialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		lastClickedView = view;
		dialog.show();
	}

	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		EditText dateDisplay = (EditText) lastClickedView;

		Date date = new GregorianCalendar(year, monthOfYear, dayOfMonth).getTime();

		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
		dateDisplay.setText(dateFormat.format(date));
	}

	public void showTimePicker(View view) {
		TimePickerDialog dialog = new TimePickerDialog(this, this, Calendar.getInstance().get(Calendar.HOUR_OF_DAY), Calendar.getInstance().get(Calendar.MINUTE), true);
		lastClickedView = view;
		dialog.show();
	}

	public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
		EditText timeDisplay = (EditText) lastClickedView;

		SimpleDateFormat sdf = new SimpleDateFormat("kk:mm");
		Date time = null;

		try {
			time = sdf.parse(hourOfDay + ":" + minute);
		} catch (ParseException ignored) {
		}


		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		timeDisplay.setText(timeFormat.format(time));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.action_bar, menu);

		return true;
	}


	public void submitRequest(View view) {

		ReservationInfo res = new ReservationInfo();
        Map<String, ArrayList<String>> domains = NetCache.getInstance().getIdms();
        Map<String, ArrayList<String>> ports;
        try {
			String s = spinnerData(res, R.id.startDomain);
            //int index = domains.get("name").indexOf(s);
			res.setStartNsa(s);
            try {
                String startPort = spinnerData(res, R.id.startPort);
                ports = NetCache.getInstance().getPorts(s);
                int index = ports.get("name").indexOf(startPort);
                res.setStartPort(ports.get("value").get(index));
            } catch (Exception e1) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.select_start_port, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
		} catch (Exception e1) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.select_start_dom, Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		try {
			String s = spinnerData(res, R.id.endDomain);
            //int index = domains.get("name").indexOf(s);
			res.setEndNsa(s);
            try {
                String endPort = spinnerData(res, R.id.endPort);
                ports = NetCache.getInstance().getPorts(s);
                int index = ports.get("name").indexOf(endPort);
                res.setEndPort(ports.get("value").get(index));
            } catch (Exception e1) {
                Toast toast = Toast.makeText(getApplicationContext(), R.string.select_end_port, Toast.LENGTH_LONG);
                toast.show();
                return;
            }
        } catch (Exception e1) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.select_end_dom, Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		CheckBox checkBox = (CheckBox) findViewById(R.id.startVlanAuto);
		if (checkBox.isChecked()) {
			res.setStartVlan(0);
		} else {
			try {
				int vlan = txtData(res, R.id.startVlan);
				res.setStartVlan(vlan);
			} catch (Exception e1) {
				Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_start_vlan, Toast.LENGTH_LONG);
				toast.show();
				return;
			}
		}

		checkBox = (CheckBox) findViewById(R.id.endVlanAuto);
		if (checkBox.isChecked()) {
			res.setEndVlan(0);
		} else {
			try {
				int vlan = txtData(res, R.id.startVlan);
				res.setEndVlan(vlan);
			} catch (Exception e1) {
				Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_end_vlan, Toast.LENGTH_LONG);
				toast.show();
				return;
			}
		}

		checkBox = (CheckBox) findViewById(R.id.startNow);
		if (checkBox.isChecked()) {
			res.setProcessNow(true);
		} else {
			try {
				long d = dateFromStr(R.id.startDate, R.id.startTime);
				res.setStartTime(d);
			} catch (Exception e) {
				Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_start_time, Toast.LENGTH_LONG);
				toast.show();
				return;
			}
		}

		try {
			long d = dateFromStr(R.id.endDate, R.id.endTime);
			res.setEndTime(d);
		} catch (Exception e) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_end_time, Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		try {
			int capacity = txtData(res, R.id.capacity);
			res.setCapacity((long) capacity);
		} catch (Exception e1) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_capacity, Toast.LENGTH_LONG);
			toast.show();
			return;
		}

		EditText txt = (EditText) findViewById(R.id.description);
		String s = txt.getText().toString();
		if (s == null || s.isEmpty()) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.empty_description, Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		res.setDescription(s);

		if (checkReservation(res)) {
			postData(Call.SUBMIT_RES, res);
		}

	}

	@Override
	protected synchronized void postSucceed(Call c, Object param) {
		String id = NetCache.getInstance().getLastSubmittedId();
		//probably that will never happen
		if (id == null) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.response_error, Toast.LENGTH_LONG);
			toast.show();
			return;
		}
		Toast toast = Toast.makeText(getApplicationContext(), R.string.reservation_success, Toast.LENGTH_LONG);
		toast.show();

		Intent circuitActivity = new Intent();
		circuitActivity.setClass(getApplicationContext(), SingleCircuitActivity.class);

		ReservationInfo res = (ReservationInfo) param;
		circuitActivity.putExtra("SERVICE_ID", id);
		circuitActivity.putExtra("DOMAIN_NAME", res.getStartNsa());
		startActivity(circuitActivity);
	}

	private boolean checkReservation(ReservationInfo res) {

		if (res.getStartNsa().equals(res.getEndNsa())) {
			if (res.getStartPort().equals(res.getEndPort())) {
				Toast toast = Toast.makeText(getApplicationContext(), R.string.port_equals, Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
		}

		Date now = new Date();
		Date ed = new Date(res.getEndTime());
		if (ed.before(now)) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.end_time_past, Toast.LENGTH_LONG);
			toast.show();
			return false;
		}

		if (!res.getProcessNow()) {
			Date sd = new Date(res.getStartTime());
			if (sd.before(now)) {
				Toast toast = Toast.makeText(getApplicationContext(), R.string.start_time_past, Toast.LENGTH_LONG);
				toast.show();
				return false;
			}
			if (sd.after(ed)) {
				Toast toast = Toast.makeText(getApplicationContext(), R.string.end_before_start, Toast.LENGTH_LONG);
				toast.show();
				return false;
			}

		}

		if (res.getCapacity() <= 0) {
			Toast toast = Toast.makeText(getApplicationContext(), R.string.invalid_capacity, Toast.LENGTH_LONG);
			toast.show();
		}

		return true;
	}

	private long dateFromStr(int dateId, int timeId) throws Exception {

		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);

		String startDateStr = ((EditText) findViewById(dateId)).getText().toString();
		Date startDate = dateFormat.parse(startDateStr);

		String startTimeStr = ((EditText) findViewById(timeId)).getText().toString();
		Date startTime = timeFormat.parse(startTimeStr);

		startDate.setHours(startTime.getHours());
		startDate.setMinutes(startTime.getMinutes());

		return startDate.getTime();
	}

	private int txtData(ReservationInfo res, int id) throws Exception {
		EditText txt = (EditText) findViewById(id);
		String s = txt.getText().toString();
		int ret = Integer.parseInt(s);
		return ret;

	}

	private String spinnerData(ReservationInfo res, int id) throws Exception {
		Spinner sp = (Spinner) findViewById(id);
		int pos = sp.getSelectedItemPosition();

		if (pos == AdapterView.INVALID_POSITION) {
			throw new Exception("data not selected at spinner " + sp.toString());
		}

		String s = (String) sp.getItemAtPosition(pos);
		return s;
	}

	public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
		if (parent.getId() == R.id.endDomain ||
				parent.getId() == R.id.startDomain) {

			String s = (String) parent.getItemAtPosition(pos);
			Log.d(TAG, s);

			if (s != null && !s.isEmpty())
				getData(Call.PORTS, s);
		}

	}

	public void onNothingSelected(AdapterView<?> parent) {
		// Another interface callback
	}

	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		setContentView(R.layout.request_reservation_activity);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		this.finish();
	}

	@Override
	public void onCheckedChanged(CompoundButton view, boolean checked) {
		Log.d(TAG, view.toString());
		switch (view.getId()) {
			case R.id.startVlanAuto:
				if (checked) {
					findViewById(R.id.startVlan).setEnabled(false);
				} else {
					findViewById(R.id.startVlan).setEnabled(true);
				}
				break;
			case R.id.endVlanAuto:
				if (checked) {
					findViewById(R.id.endVlan).setEnabled(false);
				} else {
					findViewById(R.id.endVlan).setEnabled(true);
				}
				break;
			case R.id.startNow:
				if (checked) {
					findViewById(R.id.startDate).setEnabled(false);
					findViewById(R.id.startTime).setEnabled(false);
				} else {
					findViewById(R.id.startDate).setEnabled(true);
					findViewById(R.id.startTime).setEnabled(true);
				}
		}
	}

    public void goToOptional(View v) {
        if(optionalIntent==null) {
            optionalIntent = new Intent();
        }
        optionalIntent.setClass(getApplicationContext(), RequestOptional.class);
        startActivityForResult(optionalIntent, OPTINAL_REQ);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode,resultCode,data);

        Log.d(TAG,"optional done");
        if(requestCode==OPTINAL_REQ && resultCode==RESULT_OK) {
            optionalIntent=data;
        }

    }

}
