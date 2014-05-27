package autobahn.android.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.example.autobahn.R;

import java.util.*;

/**
 * Created by Nl0st on 6/5/2014.
 */
public class CustomExpandableListAdapter extends BaseExpandableListAdapter {

    private final String TAG = "[Autobahn-client]";
    private Context context;
    private List<String> listGroups;
    private List<List<Map<String, String>>> children;
    private ArrayList<boolean[]> checkedStates = new ArrayList<>();


    public CustomExpandableListAdapter(Context context, List<String> listGroups, List<List<Map<String, String>>> children) {
        this.context = context;
        this.listGroups = listGroups;
        this.children = children;
        for (int i = 0; i < listGroups.size(); i++) {
            boolean state[] = new boolean[children.get(i).size()];
            Arrays.fill(state, false);
            checkedStates.add(state);
        }
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final Map<String, String> child = (HashMap<String, String>) getChild(groupPosition, childPosition);
        LayoutInflater inflater;
        TextView entry = new TextView(context);

        switch (groupPosition) {
            case 0:
            case 2:
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.child_row, null);
                entry = (CheckedTextView) convertView.findViewById(R.id.grp_child);
                entry.setText(child.get("CHILD"));
                if (checkedStates.get(groupPosition)[childPosition]) {
                    ((CheckedTextView) entry).setChecked(true);
                }
                break;

            case 1:
            case 3:
                inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                if (child.containsKey("SUBGROUP")) {
                    convertView = inflater.inflate(R.layout.subgroup_row, null);
                    entry = (TextView) convertView.findViewById(R.id.subgroup_name);
                    entry.setText(child.get("SUBGROUP"));
                } else {
                    inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    convertView = inflater.inflate(R.layout.child_row, null);
                    entry = (CheckedTextView) convertView.findViewById(R.id.grp_child);
                    entry.setText(child.get("CHILD"));
                    if (checkedStates.get(groupPosition)[childPosition]) {
                        ((CheckedTextView) entry).toggle();
                    }
                }
                break;
        }
        return entry;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        return listGroups.size();
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
        return listGroups.get(groupPosition);
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String headerTitle = (String) getGroup(groupPosition);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        convertView = inflater.inflate(R.layout.group_row, null);

        TextView groupText = (TextView) convertView.findViewById(R.id.row_name);
        groupText.setText(headerTitle);

        return convertView;
    }

    public void toggleCheckedState(int groupPosition, int childPosition, boolean state) {

        checkedStates.get(groupPosition)[childPosition] = state;
    }

    public ArrayList getCheckedStates() {
        return checkedStates;
    }

}
