package autobahn.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 10/23/13
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicActivity extends Activity {


    enum Call {
        DOMAINS,
        PORTS,
        RESERV,
        RES_IFO,
        CUSTOM  ,
        SUBMIT_RES ,
        LOG_OUT ,
        PROVISION,
        CANCEL_REQ
    }

    private Call call;
    private Object param;
    public final String TAG = "Autobahn2";
    AutobahnClientException e=null;
    Boolean onPost=false;

    protected int LOG_IN_REQ=9;
    private ProgressDialog progressDialog=null;

    public BasicActivity() {
    }

    protected synchronized void postSucceed(Call c,Object param) {

    }

    protected synchronized void showError(AutobahnClientException e,Call c,Object param) {
        setContentView(R.layout.error_layout);
        ((TextView)findViewById(R.id.errorText)).setText(e.getMessage());
        return;
    }

    protected synchronized void showData(Object data,Call c,Object param) {
    }

    public synchronized void postData(Call c,Object param) {
        this.call=c;
        this.param=param;
        this.onPost=true;
        e=null;
        if( !AutobahnClient.getInstance().hasAuthenticate() ) {
            Intent logInIntent = new Intent();
            logInIntent.setClass(getApplicationContext(), LoginActivity.class);
            logInIntent.putExtra(LoginActivity.MSG, getString(R.string.Log_in_first));
            startActivityForResult(logInIntent,LOG_IN_REQ);
        }else {
            BasicAsyncTask async=new BasicAsyncTask();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage( getString(R.string.loading) );
            progressDialog.show();
            async.execute(param);
        }
    }

    public synchronized void getData(Call c,Object param) {
        e=null;
        onPost=false;
        Object obj=dataFromCache(c,param);

        if(obj==null) {
            this.call=c;
            this.param=param;
            if( !AutobahnClient.getInstance().hasAuthenticate() ) {
                Intent logInIntent = new Intent();
                logInIntent.setClass(getApplicationContext(), LoginActivity.class);
                logInIntent.putExtra(LoginActivity.MSG, getString(R.string.Log_in_first));
                startActivityForResult(logInIntent,LOG_IN_REQ);
            }else {
                BasicAsyncTask async=new BasicAsyncTask();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage( getString(R.string.loading) );
                progressDialog.show();
                async.execute(param);
            }
        }
        else {
            showData(obj,c,param);
        }
    }


    private Object dataFromCache(Call call,Object param) {

        Object obj=null;
        switch (call) {
            case DOMAINS:
                obj=NetCache.getInstance().getIdms();
                break;
            case PORTS:
                obj= NetCache.getInstance().getPorts((String)param);
                break;
            case RESERV:
                obj=NetCache.getInstance().getTrackCircuits((String)param);
                break;
            case  RES_IFO:
                obj=NetCache.getInstance().getLastReservation((String)param);
                break;
            case SUBMIT_RES:
                obj=null;
                break;
        }
        return obj;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode,resultCode,data);

        if(LOG_IN_REQ==requestCode ){
            if(resultCode==RESULT_OK) {
                BasicAsyncTask async=new BasicAsyncTask();
                progressDialog = new ProgressDialog(this);
                progressDialog.setMessage( getString(R.string.loading) );
                progressDialog.show();
                async.execute(param);
            }
            else {
                AutobahnClientException e=new AutobahnClientException(getString(R.string.no_log_in) );
                showError(e,call,param);
            }
        }
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


    private class BasicAsyncTask extends AsyncTask<Object, Void, Void> {

        public BasicAsyncTask() {
        }


        @Override
        protected Void doInBackground(Object... type) {
            List<String> l;
            try {
                switch (call) {
                    case DOMAINS:
                        AutobahnClient.getInstance().fetchIdms();
                        break;
                    case PORTS:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM,getString(R.string.internal_err));
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchPorts((String)type[0]);
                        break;
                    case RESERV:
                        if(type.length==0) {
                           e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM,getString(R.string.internal_err));
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchTrackCircuit((String)type[0]);
                        break;
                    case  RES_IFO:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM,getString(R.string.internal_err));
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchReservationInfo((String)type[0]);
                        break;
                    case SUBMIT_RES:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM,getString(R.string.internal_err));
                            return null;
                        }
                        ReservationInfo res=(ReservationInfo)type[0];
                        AutobahnClient.getInstance().submitReservation(res);
                        break;
                    case LOG_OUT:
                        AutobahnClient.getInstance().logOut();
                        break;
                    case PROVISION:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM,getString(R.string.internal_err));
                            return null;
                        }
                        l =(ArrayList<String>)type[0];
                        AutobahnClient.getInstance().provision(l.get(0),l.get(1));
                        break;
                    case CANCEL_REQ:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM,getString(R.string.internal_err));
                            return null;
                        }
                        l =(ArrayList<String>)type[0];
                        AutobahnClient.getInstance().cancelRequest(l.get(0),l.get(1));
                        break;

                }
            }catch(AutobahnClientException ex) {
                e=ex;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {

            if(progressDialog!=null) {
                progressDialog.dismiss();
                progressDialog=null;
            }

            if(e!=null) {
                Log.d(TAG,e.getMessage());
                showError(e,call,param);
                return ;
            }

            if(onPost) {
              postSucceed(call,param);
            } else {
                Object obj=dataFromCache(call,param);
                if(obj==null) {
                    e=new AutobahnClientException(AutobahnClientException.Error.UNKNOWN,getString(R.string.internal_err));
                    showError(e,call,param);
                    return;
                }
                showData(obj,call,(String)param);
            }
        }
    }

}
