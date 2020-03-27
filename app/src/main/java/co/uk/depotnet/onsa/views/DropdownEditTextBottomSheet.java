package co.uk.depotnet.onsa.views;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import co.uk.depotnet.onsa.R;

public class DropdownEditTextBottomSheet extends BottomSheetDialog {

    private Context context;

    private EditText editText;
    private OnSubmit listener;

    public interface OnSubmit{
        void onCompleted(String number);
    }



    public DropdownEditTextBottomSheet(@NonNull Context context , OnSubmit listener){
        super(context);
        setCancelable(true);
        this.context = context;
        this.listener = listener;

        View view = LayoutInflater.from(context).inflate(R.layout.dropdown_number, null);
        editText = view.findViewById(R.id.et_number);
        view.findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DropdownEditTextBottomSheet.this.listener.onCompleted(editText.getText().toString());
                dismiss();
            }
        });
        setContentView(view);
    }


}