package autobahn.android.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;
import com.example.autobahn.R;
import net.geant.autobahn.android.Reservation;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;

/**
 * Created by Nl0st on 22/5/2014.
 */
public class ReservationsArrayAdapter extends ArrayAdapter<Reservation> {

    public ReservationsArrayAdapter(Context context, int textViewResourceId, List<Reservation> objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public void add(Reservation object) {
        super.add(object);
    }

    @Override
    public void addAll(Collection<? extends Reservation> collection) {
        super.addAll(collection);
    }

    @Override
    public void addAll(Reservation... items) {
        super.addAll(items);
    }

    @Override
    public void insert(Reservation object, int index) {
        super.insert(object, index);
    }

    @Override
    public void remove(Reservation object) {
        super.remove(object);
    }

    @Override
    public void clear() {
        super.clear();
    }

    @Override
    public void sort(Comparator<? super Reservation> comparator) {
        super.sort(comparator);
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public void setNotifyOnChange(boolean notifyOnChange) {
        super.setNotifyOnChange(notifyOnChange);
    }

    @Override
    public Context getContext() {
        return super.getContext();
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public Reservation getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getPosition(Reservation item) {
        return super.getPosition(item);
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Reservation reservation = getItem(position);
        LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        convertView = inflater.inflate(R.layout.list_item, parent, false);
        TextView text = (TextView) convertView.findViewById(R.id.textView);
        text.setText(reservation.getReservationId());

        if (reservation.isActive())
            text.setTextColor(getContext().getResources().getColor(R.color.green));
        else
            text.setTextColor(getContext().getResources().getColor(R.color.red));

        return convertView;
    }

    @Override
    public void setDropDownViewResource(int resource) {
        super.setDropDownViewResource(resource);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }

    @Override
    public Filter getFilter() {
        return super.getFilter();
    }
}
