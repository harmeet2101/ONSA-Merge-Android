package co.uk.depotnet.onsa.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class MaterialAlertProgressDialog extends DialogFragment {

    private Context context;
    private String title, message;

    private OnDismissListener onDismissListener;


    public static MaterialAlertProgressDialog newInstance() {
        MaterialAlertProgressDialog dialog = new MaterialAlertProgressDialog();
        dialog.setStyle(DialogFragment.STYLE_NO_TITLE, R.style.DialogTheme);
        return dialog;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View v = inflater.inflate(R.layout.dialog_material_progress, container, false);
        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        ((TextView) v.findViewById(R.id.tvMessage)).setText(message);

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (title == null && message == null) {
            dismiss();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismiss();
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setMessage(Spanned message) {
        this.message = message.toString();
    }


    public void setOnDismissListener(OnDismissListener onDismissListener) {
        this.onDismissListener = onDismissListener;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (onDismissListener != null) {
            onDismissListener.onDismiss();
        }
    }

    public static class Builder {
        private Context context;
        private MaterialAlertProgressDialog dialog;

        public Builder(Context context) {
            this.context = context;
            dialog = MaterialAlertProgressDialog.newInstance();
        }

        public Builder setTitle(String title) {
            dialog.setTitle(title);
            return this;
        }

        public Builder setMessage(String message) {
            dialog.setMessage(message);
            return this;
        }

        public Builder setMessage(Spanned message) {
            dialog.setMessage(message);
            return this;
        }


        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            dialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public MaterialAlertProgressDialog build() {
            return dialog;
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
