package co.uk.depotnet.onsa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.NumberPicker;
import android.widget.TextView;

import androidx.annotation.NonNull;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.networking.CommonUtils;

public class HourPickerDialog extends Dialog implements View.OnClickListener, NumberPicker.OnValueChangeListener {

    private static final String[] HOUR_VALS = new String[]{
            "0" , "1" , "2" , "3", "4" , "5" , "6",
            "7" , "8" , "9" , "10", "11" , "12" , "13",
            "14" , "15" , "16", "17" , "18" , "19" , "20" ,
             "21" , "22" , "23"
    };

    private static final String[] MIN_VALS = new String[]{
            "00" , "01" , "02" , "03", "04", "05", "06", "07", "08", "09",
            "10" , "11" , "12" , "13", "14", "15", "16", "17", "18", "19",
            "20" , "21" , "22" , "23", "24", "25", "26", "27", "28", "29",
            "30" , "31" , "32" , "33", "34", "35", "36", "37", "38", "39",
            "40" , "41" , "42" , "43", "44", "45", "46", "47", "48", "49",
            "50" , "51" , "52" , "53", "54", "55", "56", "57", "58", "59"
    };

    private final Context context;
    private NumberPicker hourPicker;
    private NumberPicker minutePicker;
    private TextView txtBtnPositive;
    private TextView txtBtnNegative;
    private final TimePickerListener listener;

    private String hours;
    private String minutes;

    @Override
    public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
        if(picker.getId() == R.id.hour_picker){
            hours = HOUR_VALS[picker.getValue()];
        }else if(picker.getId() == R.id.minute_picker){
            minutes = MIN_VALS[picker.getValue()];
        }
    }

    public interface TimePickerListener{
        void onTimeChosen(int timeInMinutes , String displayValue);
    }

    public static class Builder{
        private String hours;
        private String minutes;
        private TimePickerListener listener;
        private Context context;

        public Builder(Context context){
            this.context = context;
        }

        public Builder setListener(TimePickerListener listener) {
            this.listener = listener;
            return this;
        }

        public Builder setTimeInMinutes(int timeInMinutes) {
            this.hours = String.valueOf(timeInMinutes/60);
            this.minutes = String.valueOf(timeInMinutes%60);
            return this;
        }

        public HourPickerDialog build(){
            return new HourPickerDialog(context , listener , hours , minutes);
        }
    }

    private HourPickerDialog(@NonNull Context context, TimePickerListener listener , String hours , String minutes) {
        super(context);
        this.context = context;
        this.listener = listener;
        this.hours = hours;
        this.minutes = minutes;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.time_picker, null);
        setContentView(dialogLayout);

        hourPicker = dialogLayout.findViewById(R.id.hour_picker);
        minutePicker = dialogLayout.findViewById(R.id.minute_picker);
        txtBtnPositive = dialogLayout.findViewById(R.id.txt_btn_positive);
        txtBtnNegative = dialogLayout.findViewById(R.id.txt_btn_negative);
        txtBtnPositive.setOnClickListener(this);
        txtBtnNegative.setOnClickListener(this);

        hourPicker.setDisplayedValues(HOUR_VALS);
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(HOUR_VALS.length-1);
        hourPicker.setWrapSelectorWheel(true);

        minutePicker.setDisplayedValues(MIN_VALS);
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(MIN_VALS.length-1);
        minutePicker.setWrapSelectorWheel(true);

        int selectedHourPosition = CommonUtils.parseInt(hours);
        hourPicker.setValue(selectedHourPosition);
        int selectedMinutePosition = CommonUtils.parseInt(minutes);
        minutePicker.setValue(selectedMinutePosition);

        hourPicker.setOnValueChangedListener(this);
        minutePicker.setOnValueChangedListener(this);
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();
        if(viewId == R.id.txt_btn_positive){
            onPositiveBtnClick();
        }else if(viewId == R.id.txt_btn_negative){
            dismiss();
        }
    }

    private void onPositiveBtnClick(){
        listener.onTimeChosen(getValuesInMinutes(), getDisplayValue());
        dismiss();
    }

    private String getDisplayValue(){
        return CommonUtils.getDisplayTime(getValuesInMinutes());

    }

    private int getValuesInMinutes(){
        return (CommonUtils.parseInt(hours) * 60) + CommonUtils.parseInt(minutes);
    }



}
