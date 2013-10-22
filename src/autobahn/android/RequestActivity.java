package autobahn.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.autobahn.R;

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
public class RequestActivity extends Activity implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
	private View lastClickedView;
	private boolean enableStartTime = true;
	private AutobahnClientException exception = null;

    static private  final int LOG_IN_FOR_IDMS=2;
    static private  final int LOG_IN_FOR_PORTS=3;

	protected void setDomains() {
		if (exception != null) {
            Toast toast = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
            toast.show();
            return;
        }
			Log.d("domains", NetCache.getInstance().getIdms().toString());
			ArrayList<String> a1 = new ArrayList<String>(NetCache.getInstance().getIdms());
			ArrayAdapter<String> startDomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, a1);
			Spinner sp1 = (Spinner) findViewById(R.id.startDomain);
			startDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp1.setAdapter(startDomAdapter);

			ArrayList<String> a2 = new ArrayList<String>(NetCache.getInstance().getIdms());
			ArrayAdapter<String> endDomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, a2);
			Spinner sp2 = (Spinner) findViewById(R.id.endDomain);
			endDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp2.setAdapter(endDomAdapter);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.request_reservation_activity);

		((EditText) findViewById(R.id.startVlan)).setRawInputType(Configuration.KEYBOARD_12KEY);
		((EditText) findViewById(R.id.endVlan)).setRawInputType(Configuration.KEYBOARD_12KEY);

		findViewById(R.id.startDate).setOnFocusChangeListener(this);
		findViewById(R.id.endDate).setOnFocusChangeListener(this);
		findViewById(R.id.startTime).setOnFocusChangeListener(this);
		findViewById(R.id.endTime).setOnFocusChangeListener(this);


        Log.d("REQUE2","edo");


        if(NetCache.getInstance().getIdms()==null) {
            Log.d("REQUE2","edo2");
            if(!AutobahnClient.getInstance().hasAuthenticate()) {
                Intent logInIntent = new Intent();
                logInIntent.setClass(getApplicationContext(), LoginActivity.class);
                logInIntent.putExtra(LoginActivity.BACK, true);
                startActivityForResult(logInIntent,LOG_IN_FOR_IDMS);
            } else {
                Log.d("REQUE2","thr");
                DomainAsyncTask async=new DomainAsyncTask();
                async.execute();
            }
        }else {
            setDomains();
        }


	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(LOG_IN_FOR_IDMS==requestCode && resultCode==RESULT_OK) {
            DomainAsyncTask async=new DomainAsyncTask();
            async.execute();
        }
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

	public void disableCheckboxForms(View view) {
		boolean checked = ((CheckBox) view).isChecked();
		switch (view.getId()) {
			case R.id.startVlanAuto:
				if (checked) {
					findViewById(R.id.startVlan).setFocusable(false);
				} else {
					findViewById(R.id.startVlan).setFocusableInTouchMode(true);
				}
				break;
			case R.id.endVlanAuto:
				if (checked) {
					findViewById(R.id.endVlan).setFocusable(false);
				} else {
					findViewById(R.id.endVlan).setFocusableInTouchMode(true);
				}
				break;
			case R.id.startNow:
				if (checked) {
					enableStartTime = false;
					findViewById(R.id.startDate).setFocusable(false);
					findViewById(R.id.startTime).setFocusable(false);
				} else {
					enableStartTime = true;
					findViewById(R.id.startDate).setFocusableInTouchMode(true);
					findViewById(R.id.startTime).setFocusableInTouchMode(true);
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

	private class DomainAsyncTask extends AsyncTask<Void, Void, Void> {

		@Override
		protected Void doInBackground(Void... type) {
			try {
				AutobahnClient.getInstance().fetchIdms();
                List<String> idms=NetCache.getInstance().getIdms();
                if(idms!=null && idms.size()>0 ) {
                    List<String> ports=NetCache.getInstance().getPorts(idms.get(0));
                    if(ports==null)
                        AutobahnClient.getInstance().fetchPorts(idms.get(0));
                }
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
			setDomains();
		}
	}
}
