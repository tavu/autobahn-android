package autobahn.android;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import com.example.autobahn.R;

import java.util.Calendar;

/**
 * Created with IntelliJ IDEA.
 * User: tavu
 * Date: 9/24/13
 * Time: 2:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class RequestActivity2  extends Activity implements  DatePickerDialog.OnDateSetListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.request_reservation_activity2);

        EditText txt = (EditText) this.findViewById(R.id.startVlan);
        txt.setInputType(InputType.TYPE_NULL);

        txt = (EditText) this.findViewById(R.id.endVlan);
        txt.setInputType(InputType.TYPE_NULL);

        txt = (EditText) this.findViewById(R.id.startTime);
        txt.setInputType(InputType.TYPE_NULL);
        txt = (EditText) this.findViewById(R.id.startDate);
        txt.setInputType(InputType.TYPE_NULL);
        txt = (EditText) this.findViewById(R.id.endTime);
        txt.setInputType(InputType.TYPE_NULL);
        txt = (EditText) this.findViewById(R.id.endDate);
        txt.setInputType(InputType.TYPE_NULL);
    }

    public void showNumberPicker(View v) {
        NumberPicker numb=new NumberPicker(this);

        Dialog dialog=new Dialog(this );
        ViewGroup.LayoutParams lp =new  ViewGroup.LayoutParams( ViewGroup.LayoutParams.WRAP_CONTENT,  ViewGroup.LayoutParams.WRAP_CONTENT);

        dialog.addContentView(numb,lp);
        dialog.show();

    }

    public void showDatePicker(View v) {
        DatePickerDialog dialog=new DatePickerDialog(this , null,1,1,1);
        dialog.show();
        DatePicker d=dialog.getDatePicker();
    }

    public void	 onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

    }
}
