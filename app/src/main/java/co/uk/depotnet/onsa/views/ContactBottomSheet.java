package co.uk.depotnet.onsa.views;

import android.content.Context;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class ContactBottomSheet extends BottomSheetDialog
        implements View.OnClickListener {

    private Context context;
    private OnContactListener listener;

    public interface OnContactListener{
        void onPhoneClick(String phone);
        void onEmailClick(String email);
    }

    public ContactBottomSheet(@NonNull Context context ,
                              OnContactListener listener){
        super(context);
        setCancelable(true);
        this.context = context;
        this.listener = listener;

        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_contact, null);
        TextView txtPhone = view.findViewById(R.id.txt_phone);
        TextView txtEmail = view.findViewById(R.id.txt_email);
        txtPhone.setOnClickListener(this);
        txtEmail.setOnClickListener(this);
        setContentView(view);
    }


    @Override
    public void onClick( View view) {
        dismiss();
        switch (view.getId()){
            case R.id.txt_phone:
                listener.onPhoneClick("0845 2573 549");
                break;
            case R.id.txt_email:
                listener.onEmailClick("support@depotnet.co.uk");
                break;
        }
    }
}