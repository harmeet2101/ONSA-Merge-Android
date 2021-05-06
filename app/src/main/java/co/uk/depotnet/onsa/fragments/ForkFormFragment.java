package co.uk.depotnet.onsa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.datepicker.MaterialDatePicker;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.CameraActivity;
import co.uk.depotnet.onsa.activities.SignatureActivity;
import co.uk.depotnet.onsa.adapters.ForkFormAdapter;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.FromActivityListener;
import co.uk.depotnet.onsa.listeners.LocationListener;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;


public class ForkFormFragment extends Fragment implements FormAdapterListener {

    private static final int SIGNATURE_REQUEST = 2;
    private static final String ARGS_FORM = "form";
    private static final String ARGS_REPEAT_COUNT = "repeat_count";
    private static final String ARG_SUBMISSION = "Submission";
    private static final String ARG_COLOR = "Color";
    private static final String ARG_RECIPIENTS = "ARG_RECIPIENTS";
    private Context context;
    private FormItem formItem;
    private FromActivityListener listener;
    private ForkFormAdapter formAdapter;
    private Submission submission;
    private String themeColor;
    private int repeatCount;
    private ArrayList<String> recipients;

    public static ForkFormFragment newInstance(Submission submission, FormItem formItem,
                                               int index , String themeColor , ArrayList<String> recipients) {
        ForkFormFragment fragment = new ForkFormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SUBMISSION, submission);
        args.putParcelable(ARGS_FORM, formItem);
        args.putInt(ARGS_REPEAT_COUNT, index);
        args.putString(ARG_COLOR, themeColor);
        args.putStringArrayList(ARG_RECIPIENTS, recipients);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.showBtnContainer(false);
        formAdapter.reInflateItems(true);
//        formAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            submission = getArguments().getParcelable(ARG_SUBMISSION);
            formItem = getArguments().getParcelable(ARGS_FORM);
            repeatCount = getArguments().getInt(ARGS_REPEAT_COUNT);
            themeColor=getArguments().getString(ARG_COLOR);
            recipients=getArguments().getStringArrayList(ARG_RECIPIENTS);
        }

        formAdapter = new ForkFormAdapter(context, submission , formItem , repeatCount , this, themeColor , recipients);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form_fork, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        Button btnAdd = rootView.findViewById(R.id.btn_add);
        btnAdd.setOnClickListener(view -> submit());
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(10);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(formAdapter);

        if (!TextUtils.isEmpty(themeColor)) {
            int themeColor = Color.parseColor(this.themeColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btnAdd.setBackgroundTintList(ColorStateList.valueOf(themeColor));
            }
        }
        return rootView;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FromActivityListener) {
            listener = (FromActivityListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void submit() {
        if (formAdapter.validate()) {
            listener.showBtnContainer(false);
            listener.popFragmentImmediate();
        }
    }

    private static final int CAMERA_REQUEST = 1;

    @Override
    public void openCamera( long submissionId, FormItem formItem,  int repeatCount) {
        Intent intent = new Intent(context, CameraActivity.class);
        intent.putExtra(CameraActivity.ARG_FORM_ITEM, formItem);
        intent.putExtra(CameraActivity.ARG_SUBMISSION_ID, submissionId);
        intent.putExtra(CameraActivity.ARG_REPEAT, repeatCount);
        intent.putExtra(CameraActivity.ARG_COLOR, themeColor);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void openSignature(FormItem formItem, long submissionId, int repeatCount) {
        Intent intent = new Intent(context, SignatureActivity.class);
        intent.putExtra(SignatureActivity.ARG_FORM_ITEM, formItem);
        intent.putExtra(SignatureActivity.ARG_SUBMISSION_ID, submissionId);
        intent.putExtra(SignatureActivity.ARG_REPEAT_COUNT, repeatCount);
        intent.putExtra(SignatureActivity.ARG_COLOR, themeColor);
        startActivityForResult(intent, SIGNATURE_REQUEST);
    }

    @Override
    public void openForkFragment(FormItem formItem, long submissionId, int repeatCount) {
        listener.showBtnContainer(false);
        ForkFormFragment fragment = ForkFormFragment.newInstance(submission, formItem, repeatCount , themeColor , recipients);
        listener.addFragment(fragment);

    }

    @Override
    public void showProgressBar() {
        listener.showProgressBar();
    }

    @Override
    public void hideProgressBar() {
        listener.hideProgressBar();
    }


    @Override
    public void getLocation(LocationListener listener) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                formAdapter.notifyDataSetChanged();
            }
        }
    }

    @Override
    public void openFragment(Fragment fragment) {

    }

    public void showValidationDialog(String title, String message) {
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {
                    dialog1.dismiss();

                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public void showBottomSheet(BottomSheetDialogFragment dialogFragment) {
        dialogFragment.show(getChildFragmentManager() , dialogFragment.getClass().getSimpleName());
    }

    @Override
    public void startActivityForResultFromAdapter(Intent intent, int requestCode) {

    }

    @Override
    public void showErrorDialog(String title, String message, boolean shouldActivityFinished) {

    }

    @Override
    public void openTaskAmendment(FormItem formItem, long submissionId, int repeatCount) {
        listener.showBtnContainer(false);
        ForkFormFragment fragment = ForkFormFragment.newInstance(submission, formItem, repeatCount , themeColor , recipients);
        listener.addFragment(fragment);
    }

    @Override
    public void getEstimateOperative(String estno, int position , boolean isSubmit) {

    }

    @Override
    public void showDatePicker(DialogFragment dialogFragment) {
        dialogFragment.show(getChildFragmentManager() , dialogFragment.toString());
    }
}
