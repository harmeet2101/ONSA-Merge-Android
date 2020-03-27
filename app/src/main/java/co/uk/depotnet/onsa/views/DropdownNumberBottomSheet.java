package co.uk.depotnet.onsa.views;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import co.uk.depotnet.onsa.R;

public class DropdownNumberBottomSheet extends BottomSheetDialog {

    private Context context;

    private EditText etNumber;
    private OnNumberSelected listener;

    public interface OnNumberSelected{
        void numberSelected(String number);
    }



    public DropdownNumberBottomSheet(@NonNull Context context , OnNumberSelected listener){
        super(context);
        setCancelable(true);
        this.context = context;
        this.listener = listener;

        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_number, null);
        etNumber = view.findViewById(R.id.et_number);
        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropdownNumberBottomSheet.this.listener.numberSelected(etNumber.getText().toString());
                dismiss();
            }
        });
        setContentView(view);
    }


}