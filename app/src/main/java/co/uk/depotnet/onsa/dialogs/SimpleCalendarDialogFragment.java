package co.uk.depotnet.onsa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheet;
import co.uk.depotnet.onsa.networking.CommonUtils;

public class SimpleCalendarDialogFragment extends DialogFragment
        implements OnDateSelectedListener , View.OnClickListener {

    private MaterialCalendarView calendarView;
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("dd/MM/yyyy");
    private Context context;
    private TextView txtBtnPositive;
    private TextView txtBtnNegative;
    private OnDateSelected listener;
    private Date date;

    public interface OnDateSelected{
        void onDateSelected(Date date);
    }

    public SimpleCalendarDialogFragment(Context context , OnDateSelected listener) {
        this.context = context;
        this.listener = listener;
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);


        View view = inflater.inflate(R.layout.calender_dialog, null);

        calendarView = view.findViewById(R.id.calendarView);
        txtBtnPositive = view.findViewById(R.id.txt_btn_positive);
        txtBtnNegative = view.findViewById(R.id.txt_btn_negative);
        txtBtnPositive.setOnClickListener(this);
        txtBtnNegative.setOnClickListener(this);
        calendarView.removeDecorators();
        this.date = Calendar.getInstance().getTime();
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH , -3);
        calendarView.state().edit()
                .setMinimumDate(c.getTime())
                .setMaximumDate(date)
                .setFirstDayOfWeek(CommonUtils.getWeekCommencingDayInt()).commit();
        ArrayList<TimeSheet> timeSheets = DBHandler.getInstance().getTimeSheets();
        HashMap<String, String> map = new HashMap<>();
        if (!timeSheets.isEmpty()) {
            for (TimeSheet ts : timeSheets) {
                map.putAll(ts.getAllWeekDates());
            }
            Set<String> keySet = map.keySet();
            for (String key : keySet) {
                calendarView.addDecorator(new StatusDayDecorator(context, key, map.get(key)));
            }
        }
        calendarView.setOnDateChangedListener(this);
        return view;
    }

    @Override
    public void onDateSelected(
            @NonNull MaterialCalendarView widget,
            @NonNull CalendarDay date,
            boolean selected) {
        this.date = date.getDate();
//        textView.setText(FORMATTER.format(date.getDate()));
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();
        if(viewId == R.id.txt_btn_positive){
            onPositiveBtnClick();
        }else if(viewId == R.id.txt_btn_negative){
            dismiss();
        }
    }

    private void onPositiveBtnClick(){
        if(date == null){
            return;
        }
        listener.onDateSelected(date);
        dismiss();
    }
}