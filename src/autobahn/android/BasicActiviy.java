package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;
import com.example.autobahn.R;

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
        CUSTOM
    }

    private Call call;
    private String param;
    public final String TAG = "Autobahn2";
    AutobahnClientException e=null;

    private int LOG_IN_REQ=9;

    public BasicActiviy() {

    }


    protected synchronized void showData(Object data,Call c,String param) {
        Log.d(TAG, data.toString() + " " + c.toString());
    }

    protected synchronized void showError(AutobahnClientException e,Call c,String param) {
       // Toast toast = Toast.makeText(getApplicationContext(), e.getVisibleMsg(this), Toast.LENGTH_LONG);
        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        toast.show();
        return;
    }

    public synchronized void getData(Call c,String param) {
        e=null;
        Object obj=dataFromCache(c,param);

        if(obj==null) {
            this.call=c;
            this.param=param;
            if(!AutobahnClient.getInstance().hasAuthenticate()) {
                Intent logInIntent = new Intent();
                logInIntent.setClass(getApplicationContext(), LoginActivity.class);
                logInIntent.putExtra(LoginActivity.MSG, getString(R.string.Log_in_first));
                startActivityForResult(logInIntent,LOG_IN_REQ);
            }else {
                BasicAsyncTask async=new BasicAsyncTask();
                async.execute(param);
            }
        }
        else {
            showData(obj,c,param);
        }
    }


    private Object dataFromCache(Call call,String param) {

        Object obj=null;
        switch (call) {
            case DOMAINS:
                obj=NetCache.getInstance().getIdms();
                break;
            case PORTS:
                obj= NetCache.getInstance().getPorts(param);
                break;
            case RESERV:
                obj=NetCache.getInstance().getTrackCircuits(param);
                break;
            case  RES_IFO:
                obj=NetCache.getInstance().getLastReservation(param);
                break;
        }
        return obj;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode,resultCode,data);

        if(LoginActivity.LOGIN_AND_GO_BACK==requestCode && resultCode==RESULT_OK) {
            BasicAsyncTask async=new BasicAsyncTask();
            async.execute(param);
        }
        else {
            AutobahnClientException e=new AutobahnClientException(AutobahnClientException.Error.NO_LOG_IN);
            showError(e,call,param);
        }
    }


    private class BasicAsyncTask extends AsyncTask<String, Void, Void> {



        public BasicAsyncTask() {
        }


        @Override
        protected Void doInBackground(String... type) {

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
                        AutobahnClient.getInstance().fetchPorts(type[0]);
                        break;
                    case RESERV:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM);
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchTrackCircuit(type[0]);
                        break;
                    case  RES_IFO:
                        if(type.length==0) {
                            e=new AutobahnClientException(AutobahnClientException.Error.INVALID_PARAM);
                            return null;
                        }
                        param=type[0];
                        AutobahnClient.getInstance().fetchReservationInfo(type[0]);
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

            Log.d(TAG,"EX ");
            if(e!=null) {
                showError(e,call,param);
                return ;
            }
            Log.d(TAG,"EX2 "+param);

            Object obj=dataFromCache(call,param);
            if(obj==null) {
                e=new AutobahnClientException(AutobahnClientException.Error.UNKNOWN);
                showError(e,call,param);
                return;
            }
            Log.d(TAG,"EX3 ");
            showData(obj,call,param);
        }
    }

}
