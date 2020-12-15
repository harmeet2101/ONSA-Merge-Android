package co.uk.depotnet.onsa.views;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;

import java.util.ArrayList;

public class MaterialAlertDialog extends DialogFragment {
    private Context context;
    private String title, message, positiveLabel, negativeLabel;
    private ArrayList<Button> additionalButtons = new ArrayList<>();
    private DialogInterface.OnClickListener
            positiveListener, negativeListener;
    private OnDismissListener onDismissListener;

    private LinearLayout llButtons;

    public static MaterialAlertDialog newInstance() {
        MaterialAlertDialog dialog = new MaterialAlertDialog();
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

        View v = inflater.inflate(R.layout.dialog_material_alert_dialog, container, false);

        llButtons = v.findViewById(R.id.llButtons);

        ((TextView) v.findViewById(R.id.tvTitle)).setText(title);
        ((TextView) v.findViewById(R.id.tvMessage)).setText(message);

        if (additionalButtons != null) {
            for (int i = additionalButtons.size(); i > 0; i--) {
                llButtons.addView(additionalButtons.get(i - 1));
            }
        }

        if (negativeListener != null) {
            Button negativeButton = generateButton(context, negativeLabel, negativeListener, false);
            llButtons.addView(negativeButton);
        }

        if (positiveListener != null) {
            Button positiveButton = generateButton(context, positiveLabel, positiveListener, true);
            llButtons.addView(positiveButton);
        }

        if (llButtons.getChildCount() == 0) {
            positiveLabel = getString(android.R.string.ok);
            positiveListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            };
        }
        return v;
    }

//    @Override
//    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
//        super.show(manager, tag);
//
//        FragmentTransaction ft = manager.beginTransaction();
//        ft.add(this, tag).addToBackStack(null);
//        ft.commitAllowingStateLoss();
//    }

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

    public void addButton(Context context, String title, DialogInterface.OnClickListener listener) {
        Button button = generateButton(context, title, listener, false);
        if (additionalButtons == null) {
            additionalButtons = new ArrayList<>();
        }
        additionalButtons.add(button);
    }

    public void setPositiveButton(String label, DialogInterface.OnClickListener listener) {
        this.positiveLabel = label;
        this.positiveListener = listener;

    }

    public void setNegativeButton(String label, DialogInterface.OnClickListener listener) {
        this.negativeLabel = label;
        this.negativeListener = listener;
    }

    private Button generateButton(Context context, String label, final DialogInterface.OnClickListener listener, boolean acceptButton) {

        Button button = (Button) ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.include_dialog_button, llButtons, false);
        button.setText(label);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onClick(MaterialAlertDialog.this.getDialog(), 0);
            }
        });

        if (acceptButton) {
            button.setTextColor(getResources().getColor(R.color.colorAccent));
        }

        return button;
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
        private MaterialAlertDialog dialog;

        public Builder(Context context) {
            this.context = context;
            dialog = MaterialAlertDialog.newInstance();
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

        public Builder setPositive(String label, DialogInterface.OnClickListener listener) {
            dialog.setPositiveButton(label, listener);
            return this;
        }

        public Builder setNegative(String label, DialogInterface.OnClickListener listener) {
            dialog.setNegativeButton(label, listener);
            return this;
        }

        public Builder addAction(String title, DialogInterface.OnClickListener listener) {
            dialog.addButton(context, title, listener);
            return this;
        }

        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            dialog.setOnDismissListener(onDismissListener);
            return this;
        }

        public MaterialAlertDialog build() {
            return dialog;
        }
    }

    public interface OnDismissListener {
        void onDismiss();
    }
}
