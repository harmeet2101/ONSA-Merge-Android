package co.uk.depotnet.onsa.views;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import co.uk.depotnet.onsa.R;

public class DropdownTimer extends BottomSheetDialog {

    private Context context;

    private EditText etHours;
    private EditText etMinutes;
    private OnTimeSelected listener;

    public interface OnTimeSelected{
        void timeSelected(String hours , String minutes);
    }



    public DropdownTimer(@NonNull Context context , OnTimeSelected listener){
        super(context);
        setCancelable(true);
        this.context = context;
        this.listener = listener;

        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_timer, null);
        etHours = view.findViewById(R.id.et_hours);
        etMinutes = view.findViewById(R.id.et_minutes);
        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropdownTimer.this.listener.timeSelected(etHours.getText().toString() , etMinutes.getText().toString());
                dismiss();
            }
        });
        setContentView(view);
    }


}