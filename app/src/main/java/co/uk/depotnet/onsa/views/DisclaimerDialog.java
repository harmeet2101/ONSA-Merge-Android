package co.uk.depotnet.onsa.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

public class DisclaimerDialog extends DialogFragment {

    private Context context;


    private DialogInterface.OnClickListener
            positiveListener, negativeListener;
    private TextView btnAccept;
    private TextView btnDecline;
    private OnDismissListener onDismissListener;

    public static DisclaimerDialog newInstance() {
        DisclaimerDialog dialog = new DisclaimerDialog();
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

        View v = inflater.inflate(R.layout.dialog_disclaimer, container, false);

        btnAccept = v.findViewById(R.id.btn_accept);
        btnDecline = v.findViewById(R.id.btn_decline);

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                positiveListener.onClick(getDialog() , v.getId());
            }
        });

        btnDecline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                negativeListener.onClick(getDialog() , v.getId());
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        dismiss();
    }


    public void setPositiveButton( DialogInterface.OnClickListener listener) {
        this.positiveListener = listener;

    }

    public void setNegativeButton( DialogInterface.OnClickListener listener) {
        this.negativeListener = listener;
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
        private DisclaimerDialog dialog;

        public Builder(Context context) {
            this.context = context;
            dialog = DisclaimerDialog.newInstance();
        }


        public Builder setPositive(DialogInterface.OnClickListener listener) {
            dialog.setPositiveButton(listener);
            return this;
        }

        public Builder setNegative(DialogInterface.OnClickListener listener) {
            dialog.setNegativeButton(listener);
            return this;
        }


        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            dialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public DisclaimerDialog build() {
            return dialog;
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
