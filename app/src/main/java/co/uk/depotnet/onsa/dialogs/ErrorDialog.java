package co.uk.depotnet.onsa.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class ErrorDialog extends Dialog implements View.OnClickListener {

    private final String message,title;
    private Context context;
    private TextView txtMessage,txtTitle;

    public ErrorDialog(@NonNull Context context , String message,String title) {
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

        txtMessage = dialogLayout.findViewById(R.id.txt_message);
        txtTitle= dialogLayout.findViewById(R.id.txtTitle);
        txtMessage.setText(message);
        txtTitle.setText(title);
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
