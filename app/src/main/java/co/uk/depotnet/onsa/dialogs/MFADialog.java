package co.uk.depotnet.onsa.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.DisclaimerActivity;
import co.uk.depotnet.onsa.activities.VerificationActivity;
import co.uk.depotnet.onsa.modals.httprequests.ActiveMfa;

public class MFADialog extends Dialog implements View.OnClickListener {


    private Context context;
    private TextView txtSharedKey;
//    private TextView txtAuthUri;
    private ActiveMfa activeMfa;

    public MFADialog(@NonNull Context context , ActiveMfa activeMfa ) {
        super(context);
        this.context = context;
        this.activeMfa = activeMfa;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_mfa, null);
        setContentView(dialogLayout);

        setCancelable(false);

        txtSharedKey = dialogLayout.findViewById(R.id.txt_shared_key);
//        txtAuthUri= dialogLayout.findViewById(R.id.txt_auth_uri);
        txtSharedKey.setText(activeMfa.getSharedKey());
//        txtAuthUri.setText(activeMfa.getAuthenticatorUri());
        dialogLayout.findViewById(R.id.btn_ok).setOnClickListener(this);
//        dialogLayout.findViewById(R.id.btn_txt_auth_uri).setOnClickListener(this);
        dialogLayout.findViewById(R.id.btn_txt_share_key).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                dismiss();
                context.startActivity(new Intent(context , VerificationActivity.class));
                ((Activity)context).finish();
                break;
            case R.id.btn_txt_share_key:
                ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                String key = txtSharedKey.getText().toString().replaceAll(" " , "").toUpperCase();
                ClipData clip = ClipData.newPlainText("SharedKey", key);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(context , "Key Copied" , Toast.LENGTH_SHORT).show();
                break;
        }
    }

}
