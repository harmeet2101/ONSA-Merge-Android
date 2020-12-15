package co.uk.depotnet.onsa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;

import co.uk.depotnet.onsa.R;

public class ToolTipDialog extends Dialog implements View.OnClickListener {

    private final String message;
    private Context context;
    private TextView txtMessage;
    public ToolTipDialog(@NonNull Context context , String message) {
        super(context);
        this.context = context;
        this.message = message;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogLayout = LayoutInflater.from(context).inflate(R.layout.dialog_tooltip, null);
        setContentView(dialogLayout);

        txtMessage = dialogLayout.findViewById(R.id.txt_message);
        txtMessage.setText(message);
        dialogLayout.findViewById(R.id.btn_ok).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_ok:
                dismiss();
                break;
        }
    }
}

