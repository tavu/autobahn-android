package autobahn.android;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;

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
        STPS,
        RESERV,
        RES_IFO,
        CUSTOM
    }

    private Call call;
    private String param;


    public BasicActiviy() {

    }


    public void showData(Object data,Call c,String param) {

    }

    public void showError(AutobahnClientException e,Call c,String param) {

    }

    public void getData(Call c,String param) {

        Object obj=dataFromCache(c,param);

        if(obj==null) {
            if(!AutobahnClient.getInstance().hasAuthenticate()) {
                Intent logInIntent = new Intent();
                logInIntent.setClass(getApplicationContext(), LoginActivity.class);
                logInIntent.putExtra(LoginActivity.BACK, true);
                startActivityForResult(logInIntent,LoginActivity.LOGIN_AND_GO_BACK);
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

        Object obj;
        switch (call) {
            case DOMAINS:
                obj=NetCache.getInstance().getIdms();
                break;
            case STPS:
                obj= NetCache.getInstance().getPorts(param);
                break;
            case RESERV:
                obj=NetCache.getInstance().getTrackCircuits(param);
                break;
            case  RES_IFO:
                obj=NetCache.getInstance().getLastReservation(param);
                break;
        }
        return null;
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

        AutobahnClientException e=null;

        public BasicAsyncTask() {
        }


        @Override
        protected Void doInBackground(String... type) {

            try {
                switch (call) {
                    case DOMAINS:
                        AutobahnClient.getInstance().fetchIdms();
                        break;
                    case STPS:
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
            }catch(AutobahnClientException e) {
                this.e=e;
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... progress) {
            super.onProgressUpdate(progress);
        }

        @Override
        protected void onPostExecute(Void result) {

            if(e!=null) {
                showError(e,call,param);
                return ;
            }

            Object obj=dataFromCache(call,param);
            if(obj==null) {
                e=new AutobahnClientException(AutobahnClientException.Error.UNKNOWN);
                showError(e,call,param);
                return;
            }

            showData(obj,call,param);
        }
    }

}
