package autobahn.android;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.*;
import com.example.autobahn.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 12/5/13
 * Time: 2:36 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestOptional extends BasicActivity implements ExpandableListView.OnChildClickListener {

    public static final String INCL_STP="INCL_STP";
    public static final String EXCL_STP="EXCL_STP";
    public static final String INCL_NET="INCL_NET";
    public static final String EXCL_NET="EXCL_NET";

    private SimpleExpandableListAdapter expListAdapter;
    private ArrayList<ArrayList<HashMap<String, String>>> children ;
    private ArrayList<HashMap<String, String>> groups;
    static private final String CHILD_KEY="Sub_Item";
    private int currentGroup=-1;
    private int currentChild=-1;
    private Dialog dialog;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.request_optional_params);

        /* Set ExpandableListView adapter */
        groups = new ArrayList<>();
        HashMap<String, String> m;

        m = new HashMap<>();
        m.put("Group Item",getString(R.string.includedNetworks));
        groups.add(m);
        m = new HashMap<>();
        m.put("Group Item",getString(R.string.excludedNetworks));
        groups.add(m);
        m = new HashMap<>();
        m.put("Group Item",getString(R.string.includedStp));
        groups.add(m);
        m = new HashMap<>();
        m.put("Group Item",getString(R.string.excludedStp));
        groups.add(m);

        children = new ArrayList<>();
        for (int i = 0; i < 4; ++i) {
            ArrayList<HashMap<String, String>> secList = new ArrayList<>();
            children.add(secList);
        }
        expListAdapter =
                new SimpleExpandableListAdapter(
                        this,
                        groups,                         // Creating group List.
                        R.layout.group_row,             // Group item layout XML.
                        new String[]{"Group Item"},     // the key of group item.
                        new int[]{R.id.row_name},       // ID of each group item.-Data under the key goes into this TextView.
                        children,                       // childData describes second-level entries.
                        R.layout.child_row,             // Layout for sub-level entries(second level).
                        new String[]{CHILD_KEY},        // Keys in childData maps to display.
                        new int[]{R.id.grp_child}       // Data under the keys above go into these TextViews.
                );

        addChildren(getString(R.string.includeNetwork), 0);
        addChildren(getString(R.string.excludeNetwork),1);
        addChildren(getString(R.string.includeStp),2);
        addChildren(getString(R.string.excludeStp),3);

        ExpandableListView expandableListView = (ExpandableListView) findViewById(R.id.pathConstraints);
        expandableListView.setAdapter(expListAdapter);       // setting the adapter in the list.
        expandableListView.setOnChildClickListener(this);

    }

    private void addChildren(String name,int group) {
        ArrayList<HashMap<String, String>> secList = children.get(group);
        HashMap<String, String> child = new HashMap<>();
        child.put(CHILD_KEY, name);
        secList.add(child);
        expListAdapter.notifyDataSetChanged();
    }

    private void removeChild(int group,int child) {
        ArrayList<HashMap<String, String>> secList = children.get(group);
        secList.remove(child);
        expListAdapter.notifyDataSetChanged();
    }

    private String getChildText(int group,int child) {
        return  children.get(group).get(child).get(CHILD_KEY);
    }

    private List<String> getNetworks() {

        List<String> idms = NetCache.getInstance().getIdms().get("name");

        if(idms == null) {
            return idms;
        }

        ArrayList<HashMap<String, String>> secList;

        /*if(currentGroup == 0 || currentGroup == 2) {
            secList = children.get(1);
        } else if(currentGroup == 1 || currentGroup == 3) {
            secList = children.get(0);
        } else {
            //TODO show error
            Log.d(TAG,"Wrong current group " + currentGroup);
            return null;
        }


        for(HashMap<String, String> map : secList) {
            idms.remove(map.get(CHILD_KEY));
        }*/

        if(currentGroup == 0 || currentGroup == 1) {
            secList = children.get(currentGroup);
            for(HashMap<String, String> map : secList) {
                idms.remove(map.get(CHILD_KEY));
            }
        }

        return idms;
    }

    @Override
    public boolean onChildClick(ExpandableListView expandableListView, View view,  int group, int child, long id) {

        Log.d(TAG, "C " + group + " G " + child);

        currentGroup = group;
        currentChild = child;
        /*if(group < 0 || group >3 ) {
            //TODO show error
            return false;
        }*/
        if( (group >= 0 && group < 4) && child == 0) {
            getData(Call.DOMAINS, null);
        }
        else {
            String s = getChildText(group,child);
            String msg;
            if(group == 0) {
                msg = "Do you want to remove " + s + " from " + getString(R.string.includedNetworks) + "?";
            } else if(group == 1) {
                msg = "Do you want to remove " + s + " from " + getString(R.string.excludedNetworks) + "?";
            } else if(group == 2) {
                msg = "Do you want to remove " + s + " from " + getString(R.string.includedStp) + "?";
            } else{
                msg = "Do you want to remove " + s + " from " + getString(R.string.excludedStp) + "?";
            }

            AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(this);
            myAlertDialog.setTitle(s);
            myAlertDialog.setMessage(msg);
            myAlertDialog.setNegativeButton(getString( R.string.no), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface arg0, int arg1) {
                //do nothing
            }
        });
            myAlertDialog.setPositiveButton(getString( R.string.yes), new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                removeChild(currentGroup,currentChild);
                }
              });

            myAlertDialog.show();
        }

        return true;
    }

    @Override
    protected void showData(Object data, Call c, Object param) {

        if(currentGroup==-1) {
            return ;
        }
        if(c == Call.PORTS) {

            int otherGroup;
            if(currentGroup == 2) {
                otherGroup = 3;
            } else if(currentGroup == 3) {
                otherGroup=2;
            } else {
                //TODO show error
                Log.d(TAG,"wrong current group:"+currentGroup);
                return ;
            }
            ArrayList<String> ports=(ArrayList<String>)data;
            ArrayList<HashMap<String, String>> secList = children.get(2);
            for(HashMap<String, String> map : secList) {
                ports.remove(map.get(CHILD_KEY));
            }
            secList = children.get(3);
            for(HashMap<String, String> map : secList) {
                ports.remove(map.get(CHILD_KEY));
            }

            Spinner sp = (Spinner) dialog.findViewById(R.id.stpSpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(dialog.getContext(), android.R.layout.simple_spinner_item,ports);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            sp.setAdapter(adapter);
            return ;
        }


        if(NetCache.getInstance().getIdms() == null) {
            showError(new AutobahnClientException(getString(R.string.no_domains)),Call.RESERV,new Object());
            currentGroup=-1;
            return;
        }

        if(currentGroup==0) {
            dialog = getNetworkDialog();
            dialog.setTitle(R.string.includeNetwork);
            dialog.show();
        } else if(currentGroup==1) {
            dialog= getNetworkDialog();
            dialog.setTitle(R.string.excludeNetwork);
            dialog.show();

        }  else if(currentGroup==2) {
            dialog=new stpDialog(this);
            dialog.setTitle(R.string.includeStp);
            dialog.show();
        }  else if(currentGroup==3) {
            dialog=new stpDialog(this);
            dialog.setTitle(R.string.excludeStp);
            dialog.show();
        }
    }

    private Dialog getNetworkDialog() {

        List<String> idms = getNetworks();

        if(idms.isEmpty() ) {
            showError(new AutobahnClientException(getString(R.string.no_domains)), Call.RESERV, new Object() );
            return null;
        }

        dialog = new Dialog(this);
        dialog.setContentView(R.layout.network_dialog);
        dialog.setTitle(R.string.includeNetwork);
        TextView text = (TextView) dialog.findViewById(R.id.dialogText);
        Spinner spinner = (Spinner) dialog.findViewById(R.id.dialogSpinner);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,idms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        Button dialogButton = (Button) dialog.findViewById(R.id.dialogButtonOk);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Spinner spinner = (Spinner) dialog.findViewById(R.id.dialogSpinner);
                int pos = spinner.getSelectedItemPosition();
                if (pos != AdapterView.INVALID_POSITION) {
                    String s = (String) spinner.getItemAtPosition(pos);
                    addChildren(s,currentGroup);
                }
                currentGroup=-1;
                dialog.dismiss();
            }
        });

        dialogButton = (Button) dialog.findViewById(R.id.dialogButtonCancel);
        dialogButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                currentGroup=-1;
                dialog.dismiss();
            }
        });

        return dialog;
    }

    private Dialog getStpDialog() {
        return null;
    }

    @Override
    protected synchronized void showError(AutobahnClientException e,Call c,Object param) {
        Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
        toast.show();
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode == KeyEvent.KEYCODE_BACK){
            Log.d(TAG, "Back key pressed");
            Intent returnIntent = new Intent();
            setResult(RESULT_OK, returnIntent);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }


    class stpDialog extends Dialog implements AdapterView.OnItemSelectedListener{

        public stpDialog (Context context) {
            super(context);
        }

        @Override
        protected void onStop(){
            currentGroup=-1;
            dialog=null;
        }

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            List<String> idms = getNetworks();

            if(idms == null) {
                idms = new ArrayList<>();
            }

            if(idms.isEmpty()) {
                showError(new AutobahnClientException(getString(R.string.no_domains)), Call.SUBMIT_RES , new Object());
            }

            setContentView(R.layout.stp_dialog);
            setTitle(R.string.includedStp);
            Spinner spinner = (Spinner) findViewById(R.id.domainSpinner);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item,idms);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            spinner.setOnItemSelectedListener(this);

            Button dialogButton = (Button) findViewById(R.id.stpDialogOk);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Spinner spinner = (Spinner) findViewById(R.id.stpSpinner);
                    int pos = spinner.getSelectedItemPosition();
                    if (pos != AdapterView.INVALID_POSITION) {
                        String s = (String) spinner.getItemAtPosition(pos);
                        addChildren(s,currentGroup);
                    }
                    dialog.dismiss();
                }
            });

            dialogButton = (Button) dialog.findViewById(R.id.stpDialogCancel);
            dialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            String s = (String) parent.getItemAtPosition(pos);
            Log.d(TAG, s);

            if (s != null && !s.isEmpty())
                getData(Call.PORTS, s);

        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //To change body of implemented methods use File | Settings | File Templates.
        }

    }
}
