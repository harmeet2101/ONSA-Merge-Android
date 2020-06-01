package co.uk.depotnet.onsa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.LoginActivity;

public class JWTErrorDialog extends Dialog implements View.OnClickListener {

    private final String message,title;
    private Context context;

    public JWTErrorDialog(@NonNull Context context , String message, String title) {
        super(context);
        this.context = context;
        this.message = message;
        this.title=title;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_error, null);
        setContentView(dialogLayout);

        TextView txtMessage = dialogLayout.findViewById(R.id.txt_message);
        TextView txtTitle = dialogLayout.findViewById(R.id.txtTitle);
        txtMessage.setText(message);
        txtTitle.setText(title);
        dialogLayout.findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                Intent intent = new Intent(context , LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intent);
                dismiss();
                break;
        }
    }

}
