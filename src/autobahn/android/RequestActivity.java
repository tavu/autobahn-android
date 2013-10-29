package autobahn.android;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
public class RequestActivity extends BasicActiviy implements View.OnFocusChangeListener,
                DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
                AdapterView.OnItemSelectedListener {


    private View lastClickedView;
	private AutobahnClientException exception = null;


    @Override
    protected void showData(Object data,Call c,String param) {
        Log.d(TAG,data.toString()+" "+c.toString());

        if(c==Call.DOMAINS) {
            List<String> l=(List<String>)data;
            if( !l.isEmpty() ) {
                getData(Call.PORTS,l.get(0));
            }else {
                //TODO there is no domain show error
            }

              //setDomains((List<String>)data);
          } else if(c==Call.PORTS){
              Spinner sp = (Spinner) findViewById(R.id.startDomain);
              if(sp.getAdapter()==null)
                  setDomains(NetCache.getInstance().getIdms());

              setPorts( (List<String>)data ,param);
          }
    }

    protected void setPorts(List<String> data,String domain) {
        Spinner sp = (Spinner) findViewById(R.id.endDomain);

        int pos=sp.getSelectedItemPosition();
        if(pos != AdapterView.INVALID_POSITION) {
            String s=(String)sp.getItemAtPosition(pos);
            if( s!=null && s.equals(domain) ) {
                sp = (Spinner) findViewById(R.id.endPort);
                ArrayAdapter<String> adapter =(ArrayAdapter<String>) sp.getAdapter();
                adapter.clear();
                adapter.addAll(data);
            }
        }

        sp = (Spinner) findViewById(R.id.startDomain);

        pos=sp.getSelectedItemPosition();
        if(pos != AdapterView.INVALID_POSITION) {
            String s=(String)sp.getItemAtPosition(pos);
            if( s!=null && s.equals(domain) ) {
                sp = (Spinner) findViewById(R.id.startPort);
                ArrayAdapter<String> adapter =(ArrayAdapter<String>) sp.getAdapter();
                adapter.clear();
                adapter.addAll(data);
            }
        }


    }

    protected void setDomains(List<String> data) {
			Log.d(TAG, NetCache.getInstance().getIdms().toString());

			ArrayList<String> a1 = new ArrayList<String>(data);
			ArrayAdapter<String> startDomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, a1);
			Spinner sp1 = (Spinner) findViewById(R.id.startDomain);
			startDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp1.setAdapter(startDomAdapter);

			ArrayList<String> a2 = new ArrayList<String>(data);
			ArrayAdapter<String> endDomAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, a2);
			Spinner sp2 = (Spinner) findViewById(R.id.endDomain);
			endDomAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			sp2.setAdapter(endDomAdapter);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.request_reservation_activity);
		getActionBar().setDisplayHomeAsUpEnabled(true);

		findViewById(R.id.startDate).setOnFocusChangeListener(this);
		findViewById(R.id.endDate).setOnFocusChangeListener(this);
		findViewById(R.id.startTime).setOnFocusChangeListener(this);
		findViewById(R.id.endTime).setOnFocusChangeListener(this);


        Spinner sp=(Spinner) findViewById(R.id.startDomain);
        sp.setOnItemSelectedListener(this);
        sp=(Spinner) findViewById(R.id.endDomain);
        sp.setOnItemSelectedListener(this);
        getData(Call.DOMAINS,null);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        sp = (Spinner) findViewById(R.id.startPort);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        sp = (Spinner) findViewById(R.id.endPort);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(adapter);
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

	// Called when an options item is clicked
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.preferences:
				startActivity(new Intent(this, PreferencesActivity.class));
				break;
			case android.R.id.home:
				NavUtils.navigateUpFromSameTask(this);
				return true;
		}
		return super.onOptionsItemSelected(item);
	}

	public void submitRequest(View view) {
		// TODO: Submit request to autobahn
	}

    public void onItemSelected(AdapterView<?> parent, View view,
                               int pos, long id) {
        if(parent.getId()==R.id.endDomain ||
                parent.getId()==R.id.startDomain) {

            String s=(String)parent.getItemAtPosition(pos);
            Log.d(TAG,s);

            if(s!=null && !s.isEmpty())
                getData(Call.PORTS,s);
        }

    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }

}
