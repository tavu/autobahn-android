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
public class RequestActivity extends BasicActiviy implements View.OnFocusChangeListener,
                DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener,
                AdapterView.OnItemSelectedListener {


    private View lastClickedView;
	private AutobahnClientException exception = null;


    @Override
    protected void showData(Object data,Call c,String param) {

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
        else {
            Log.d(TAG,"EDDDDFAS");
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


        findViewById(R.id.endTime).setOnFocusChangeListener(this);
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

        ReservationInfo res=new ReservationInfo();

        String startPort;
        String endPort;

        try {
            String s=spinnerData(res,R.id.startDomain);
            res.setStartNsa(s);
        } catch (AutobahnClientException e1) {
            //TODO
            return;
        }
        try {
            String s=spinnerData(res,R.id.endDomain);
            res.setEndNsa(s);
        } catch (AutobahnClientException e1) {
            //TODO
            return;
        }
        try {
            startPort=spinnerData(res,R.id.startPort);
            res.setStartPort(startPort);
        } catch (AutobahnClientException e1) {
            //TODO
            return;
        }
        try {
            endPort=spinnerData(res,R.id.endPort);
            res.setEndPort(endPort);
        } catch (AutobahnClientException e1) {
            //TODO
            return;
        }

        if(endPort.equals(startPort)) {
            //TODO
        }

        CheckBox checkBox = (CheckBox) findViewById(R.id.startVlanAuto);
        if(checkBox.isChecked() ) {
             res.setStartVlan(0);
        } else {
            try {
                int vlan =txtData(res,R.id.startVlan);
                res.setStartVlan(vlan);
            } catch (AutobahnClientException e1) {
                //TODO
                return ;
            }
        }

        checkBox = (CheckBox) findViewById(R.id.endVlanAuto);
        if(checkBox.isChecked() ) {
            res.setEndVlan(0);
        } else {
            try {
                int vlan =txtData(res,R.id.startVlan);
                res.setEndVlan(vlan);
            } catch (AutobahnClientException e1) {
                //TODO
                return ;
            }
        }

        try {
            long d=dateFromStr(R.id.startDate,R.id.startTime);
            res.setStartTime(d);
        } catch (AutobahnClientException e) {
            //TODO
            return ;
        }

        try {
            long d=dateFromStr(R.id.endDate,R.id.endTime);
            res.setEndTime(d);
        } catch (AutobahnClientException e) {
            //TODO
            return ;
        }

        res.setId("e");
        res.setTimeZone("GMT");
        res.setDescription("DIs");
        res. setCapacity(789);
        res.setReservationState("t");
        res.setProvisionState("sg");
        res.setLifecycleState("sh");
        res.setMtu(9);
        res.setMaxDelay(9);
        res.setProcessNow(false);

        getData(Call.SUBMIT_RES,res);
	}

    private long dateFromStr(int dateId,int timeId) throws AutobahnClientException {
        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(this);

        String startDateStr =( (EditText) findViewById(dateId) ).getText().toString();
        Date startDate;
        try {
            startDate = dateFormat.parse(startDateStr);
        } catch (ParseException e) {
            //TODO
            throw new AutobahnClientException(dateId);
        }

        String startTimeStr =( (EditText) findViewById(timeId) ).getText().toString();
        Date startTime;
        try {
            startTime = dateFormat.parse(startDateStr);
        } catch (ParseException ignored) {
            //TODO
            throw new AutobahnClientException(timeId);
        }

        startDate.setHours(startTime.getHours());
        startDate.setMinutes(startTime.getMinutes());
        return startDate.getTime();
    }

    private int txtData(ReservationInfo res,int id) throws AutobahnClientException{
        EditText txt=(EditText) findViewById(id);
        String s=txt.getText().toString();
        int vlan=0;
        try{
            vlan=Integer.parseInt(s);
        } catch (NumberFormatException e) {
            throw new AutobahnClientException(id);
        }

        switch (id) {
            case R.id.startVlan:
                res.setStartVlan(vlan);
                break;
            case R.id.endVlan:
                res.setStartVlan(vlan);
                break;
            default:
                throw  new AutobahnClientException();
        }
        return vlan;

    }

    private String spinnerData(ReservationInfo res,int id) throws AutobahnClientException{
        Spinner sp=(Spinner)findViewById(id);
        int pos=sp.getSelectedItemPosition();

        if(pos == AdapterView.INVALID_POSITION) {
            throw  new AutobahnClientException(id);
        }

        String s=(String)sp.getItemAtPosition(pos);

        switch (id) {
            case R.id.startDomain:
                res.setStartNsa(s);
                break;
            case R.id.startPort:
                res.setStartPort(s);
                break;
            case R.id.endDomain:
                res.setEndNsa(s);
                break;
            case R.id.endPort:
                res.setEndPort(s);
                break;
            default:
                throw  new AutobahnClientException();

        }
        return s;
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
