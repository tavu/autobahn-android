package autobahn.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 9/24/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestActivity2 extends Activity implements View.OnFocusChangeListener, DatePickerDialog.OnDateSetListener,
        TimePickerDialog.OnTimeSetListener, AdapterView.OnItemSelectedListener  {


	private View lastClickedView;
    private AutobahnClientException exception=null;
    final private String TAG="REQUEST_RES";

    private class AsTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... type) {

            Log.d(TAG,"Async "+type[0]);
            if(type[0].equals("DOMAINS")) {
                try{
                    AutobahnClient.getInstance().fetchIdms();
                } catch (AutobahnClientException e) {
                    exception=e;
                }
                return type[0];
            }
            else {
                try{
                    AutobahnClient.getInstance().fetchPorts(type[0]);
                } catch (AutobahnClientException e) {
                    exception=e;
                }
                return type[0];
            }

        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(String result) {
            if(exception!=null) {
                Toast toast  = Toast.makeText(getApplicationContext(), exception.getMessage(), Toast.LENGTH_LONG);
                toast.show();
                return ;
            }

            if(result.equals("DOMAINS")) {
                setDomains();
            }else {
                setPorts(result);
            }
        }
    };



    protected void setDomains() {

            ArrayList<String> a1=new ArrayList<String>( AutobahnClient.getInstance().getIdms())    ;
            ArrayAdapter<String> startDomAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, a1 );
            Spinner sp1= (Spinner) findViewById(R.id.startDomain);
            startDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp1.setAdapter(startDomAdapter);


            ArrayList<String> a2=new ArrayList<String>( AutobahnClient.getInstance().getIdms())    ;
            ArrayAdapter<String> endDomAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, a2 );
            Spinner sp2= (Spinner) findViewById(R.id.endDomain);
            endDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp2.setAdapter(endDomAdapter);

    }

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.request_reservation_activity2);

		((EditText) findViewById(R.id.startVlan)).setRawInputType(Configuration.KEYBOARD_12KEY);
		((EditText) findViewById(R.id.endVlan)).setRawInputType(Configuration.KEYBOARD_12KEY);

		findViewById(R.id.startDate).setOnFocusChangeListener(this);
		findViewById(R.id.endDate).setOnFocusChangeListener(this);
		findViewById(R.id.startTime).setOnFocusChangeListener(this);
		findViewById(R.id.endTime).setOnFocusChangeListener(this);

        Spinner sp=(Spinner)(findViewById(R.id.startDomain) );
        sp.setOnItemSelectedListener(this);
        sp=(Spinner)(findViewById(R.id.endDomain) );
        sp.setOnItemSelectedListener(this);


        sp=(Spinner)(findViewById(R.id.startPort) );
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new ArrayList<String>() );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        sp=(Spinner)(findViewById(R.id.endPort) );
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,new ArrayList<String>() );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);


        AsTask  ast=new AsTask ();
        exception=null;
        ast.execute("DOMAINS");
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
					findViewById(R.id.startDate).setFocusable(false);
					findViewById(R.id.startTime).setFocusable(false);
				} else {
					findViewById(R.id.startDate).setFocusableInTouchMode(true);
					findViewById(R.id.startTime).setFocusableInTouchMode(true);
				}
		}
	}

	public void showDatePicker(View view) {
		// TODO: change min API level to 11
		DatePickerDialog dialog = new DatePickerDialog(this, this, Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH), Calendar.getInstance().get(Calendar.DAY_OF_MONTH));
		lastClickedView = view;
		dialog.show();
	}

	public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		EditText dateDisplay = (EditText) lastClickedView;

        DateFormat sdf=DateFormat.getDateInstance();
		Date date = new Date(year,monthOfYear,dayOfMonth);

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
		} catch (ParseException e) {
		}
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		timeDisplay.setText(timeFormat.format(time));
	}

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {

        Log.d(TAG,"edo");
        String s=(String )parent.getItemAtPosition(pos);

        Log.d(TAG, "domain:" + s+" have been selected");

        AsTask as=new AsTask();
        exception=null;
        as.execute(s);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            //do nothing
    }


    protected void setPorts(String domain) {


        if(!NetCache.getInstance().hasPorts(domain)) {
                //TODO
            return;
        }

        Log.d(TAG, NetCache.getInstance().getPorts(domain).toString());

        Spinner spinner= (Spinner) findViewById(R.id.startDomain);
        String dom=null;
        if(spinner.getSelectedItem()!=null) {
            dom=spinner.getSelectedItem().toString();
        }

        if(dom!=null && dom.equals(domain) )  {

            Log.d(TAG,"ports changed for start domain "+dom);
            spinner=    (Spinner) findViewById(R.id.startPort);
            ArrayAdapter<String> adapter=(ArrayAdapter<String>) spinner.getAdapter();
            adapter.clear();
            adapter.addAll(NetCache.getInstance().getPorts(dom));
        }

        dom=null;
        spinner= (Spinner) findViewById(R.id.endDomain);
        if(spinner.getSelectedItem()!=null) {
            dom=spinner.getSelectedItem().toString();
        }

        if(dom!=null && dom.equals(domain) )  {
            Log.d(TAG,"ports changed for end domain "+dom);
            spinner=    (Spinner) findViewById(R.id.endPort);
            ArrayAdapter<String> adapter=(ArrayAdapter<String>) spinner.getAdapter();
            adapter.clear();
            adapter.addAll(NetCache.getInstance().getPorts(dom));
        }
    }

}
