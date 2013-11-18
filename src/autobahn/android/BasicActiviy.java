package autobahn.android;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.example.autobahn.R;
import net.geant.autobahn.android.ReservationInfo;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 10/23/13
 * Time: 4:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class BasicActiviy extends Activity {


    enum Call {
        DOMAINS,
        PORTS,
        RESERV,
        RES_IFO,
        CUSTOM  ,
        SUBMIT_RES ,
        LOG_OUT
    }

    private Call call;
    private Object param;
    public final String TAG = "Autobahn2";
    AutobahnClientException e=null;

    private int LOG_IN_REQ=9;
    private ProgressDialog progressDialog=null;

    public BasicActiviy() {
    }

    protected synchronized void showData(Object data,Call c,String param) {

    }

    protected synchronized void showError(AutobahnClientException e,Call c,String param) {
       // Toast toast = Toast.makeText(getApplicationContext(), e.getVisibleMsg(this), Toast.LENGTH_LONG);
        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        toast.show();
        return;
    }


    public synchronized void getData(Call c,Object param) {
        e=null;
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
            showData(obj,c,(String)param);
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

        if(LoginActivity.LOGIN_AND_GO_BACK==requestCode && resultCode==RESULT_OK) {
            BasicAsyncTask async=new BasicAsyncTask();
            progressDialog = new ProgressDialog(this);
            progressDialog.setMessage( getString(R.string.loading) );
            progressDialog.show();
            async.execute(param);
        }
        else {
            AutobahnClientException e=new AutobahnClientException(AutobahnClientException.Error.NO_LOG_IN);
            showError(e,call,(String)param);
        }
    }


    private class BasicAsyncTask extends AsyncTask<Object, Void, Void> {



        public BasicAsyncTask() {
        }


        @Override
        protected Void doInBackground(Object... type) {

            try {
                switch (call) {
                    case DOMAINS:
                        AutobahnClient.getInstance().fetchIdms();
                        break;
                    case PORTS:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM);
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchPorts((String)type[0]);
                        break;
                    case RESERV:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM);
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchTrackCircuit((String)type[0]);
                        break;
                    case  RES_IFO:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM);
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchReservationInfo((String)type[0]);
                        break;
                    case SUBMIT_RES:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM);
                            return null;
                        }
                        ReservationInfo res=(ReservationInfo)type[0];
                        AutobahnClient.getInstance().submitReservation(res);
                        break;
                    case LOG_OUT:
                        AutobahnClient.getInstance().logOut();
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
            if(call==Call.SUBMIT_RES) {
                if(e!=null) {
                    showError(e,call,null);
                    Log.d(TAG,e.getMessage());
                    return ;
                }
                else {
                    showData(null,call,null);
                    return ;
                }
            }

            if(e!=null) {
                showError(e,call,(String)param);
                return ;
            }

            if(call==Call.SUBMIT_RES) {
                showData(null,call,null);
                return ;
            }

            Object obj=dataFromCache(call,param);
            if(obj==null) {
                e=new AutobahnClientException(AutobahnClientException.Error.UNKNOWN);
                showError(e,call,(String)param);
                return;
            }
            showData(obj,call,(String)param);
        }
    }

}
