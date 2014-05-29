package autobahn.android.utils;

import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.example.autobahn.R;
import net.geant.autobahn.android.Domain;
import net.geant.autobahn.android.Port;

import java.util.*;

/**
 * Created by Nl0st on 6/5/2014.
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private final String TAG = "[Autobahn-client]";
    private Context context;
    private List<Category> children;
    private LayoutInflater inflater;
    private SparseArray<SparseBooleanArray> checkedPositions;

    public CustomExpandableListAdapter(Context context, List<Category> children) {
        this.context = context;
        this.children = children;
        inflater = LayoutInflater.from(context);
        checkedPositions = new SparseArray<>();
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return ((ArrayList) children.get(groupPosition).getChildren()).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Object child = getChild(groupPosition, childPosition);

        Log.d(TAG, "Getting the child view for child " + childPosition + " in the group " + groupPosition);

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

/*        if (convertView == null){
            switch (groupPosition){
                case 0:
                case 2:
                    convertView = inflater.inflate(R.layout.child_row,null);
                    break;
                case 1:
                case 3:
                    if (child instanceof Domain)
                        convertView = inflater.inflate(R.layout.subgroup_row, null);
                    else
                        convertView = inflater.inflate(R.layout.child_row, null);
                    break;
            }
        }*/

        switch (groupPosition) {
            case 0:
            case 2:
                convertView = inflater.inflate(R.layout.child_row, null);
                ((TextView) convertView.findViewById(R.id.grp_chld)).setText(child.toString());
                if (checkedPositions.get(groupPosition) != null) {
                    Log.d(TAG, "\t \t The child checked position has been saved");
                    boolean isChecked = checkedPositions.get(groupPosition).get(childPosition);
                    Log.d(TAG, "\t \t \t Is child checked: " + isChecked);
                    ((CheckBox) convertView.findViewById(R.id.checkBox)).setChecked(isChecked);
                } else {
                    ((CheckBox) convertView.findViewById(R.id.checkBox)).setChecked(false);
                }
                break;
            case 1:
            case 3:
                if (child instanceof Port) {
                    convertView = inflater.inflate(R.layout.child_row, null);
                    ((TextView) convertView.findViewById(R.id.grp_chld)).setText(child.toString());
                    if (checkedPositions.get(groupPosition) != null) {
                        Log.d(TAG, "\t \t The child checked position has been saved");
                        boolean isChecked = checkedPositions.get(groupPosition).get(childPosition);
                        Log.d(TAG, "\t \t \t Is child checked: " + isChecked);
                        ((CheckBox) convertView.findViewById(R.id.checkBox)).setChecked(isChecked);
                    } else {
                        ((CheckBox) convertView.findViewById(R.id.checkBox)).setChecked(false);
                    }
                } else {
                    convertView = inflater.inflate(R.layout.subgroup_row, null);
                    ((TextView) convertView).setText(child.toString());
                }
                break;
        }
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return children.size();
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return children.get(groupPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return ((List) (children.get(groupPosition).getChildren())).size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.group_row, null);

        TextView groupText = (TextView) convertView.findViewById(R.id.row_name);
        groupText.setText(((Category) getGroup(groupPosition)).getName());

        return convertView;
    }

    /**
     * Update the list of the positions checked and update the view
     *
     * @param groupPosition The position of the group which has been checked
     * @param childPosition The position of the child which has been checked
     */
    public void setClicked(int groupPosition, int childPosition) {

        SparseBooleanArray checkedChildPositionsMultiple = checkedPositions.get(groupPosition);
        // if in the group there was not any child checked
        if (checkedChildPositionsMultiple == null) {
            checkedChildPositionsMultiple = new SparseBooleanArray();
            // By default, the status of a child is not checked
            // So a click will enable it
            checkedChildPositionsMultiple.put(childPosition, true);
            checkedPositions.put(groupPosition, checkedChildPositionsMultiple);
        } else {
            boolean oldState = checkedChildPositionsMultiple.get(childPosition);
            checkedChildPositionsMultiple.put(childPosition, !oldState);
        }


        // Notify that some data has been changed
        notifyDataSetChanged();
        Log.d(TAG, "List position updated");
    }

    /**
     * Method used to get the actual state of the checked lists
     *
     * @return The list of the all the positions checked
     */
    public SparseArray<SparseBooleanArray> getCheckedPositions() {
        return checkedPositions;
    }
}
