package co.uk.depotnet.onsa.fragments;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.CameraActivity;
import co.uk.depotnet.onsa.adapters.FrokFormAdapter;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.FromActivityListener;
import co.uk.depotnet.onsa.listeners.LocationListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;


public class ForkFormFragment extends Fragment implements FormAdapterListener {
    public static final String ARGS_FORM = "form";
    public static final String ARGS_REPEAT_COUNT = "repeat_count";
    private static final String ARG_SUBMISSION = "Submission";
    private static final String ARG_USER = "User";

    private Context context;
    private FormItem formItem;
    private FromActivityListener listener;
    private User user;
    private FrokFormAdapter formAdapter;
    private Submission submission;
    private Handler handler;
    private int repeatCount;


    public static ForkFormFragment newInstance(Submission submission,
                                               User user, FormItem formItem,
                                               int index) {
        ForkFormFragment fragment = new ForkFormFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SUBMISSION, submission);
        args.putParcelable(ARG_USER, user);
        args.putParcelable(ARGS_FORM, formItem);
        args.putInt(ARGS_REPEAT_COUNT, index);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onResume() {
        super.onResume();
        listener.showBtnContainer(false);
        formAdapter.addListItems();
        formAdapter.notifyDataSetChanged();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            submission = getArguments().getParcelable(ARG_SUBMISSION);
            user = getArguments().getParcelable(ARG_USER);
            formItem = getArguments().getParcelable(ARGS_FORM);
            repeatCount = getArguments().getInt(ARGS_REPEAT_COUNT);
        }

        this.handler = new Handler();
        formAdapter = new FrokFormAdapter(context, submission , formItem , repeatCount , this);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_form_fork, container, false);
        RecyclerView recyclerView = rootView.findViewById(R.id.recycler_view);
        rootView.findViewById(R.id.btn_add).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                submit();

            }
        });
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(10);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(formAdapter);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
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
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    public void openSignature(FormItem formItem, long submissionId, int repeatCount) {

    }

    @Override
    public void openForkFragment(FormItem formItem, long submissionId, int repeatCount) {
        listener.showBtnContainer(false);
        ForkFormFragment fragment = ForkFormFragment.newInstance(submission, user, formItem, repeatCount);
        listener.addFragment(fragment);

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

    @Override
    public void showBottomSheet(BottomSheetDialogFragment dialogFragment) {
        dialogFragment.show(getChildFragmentManager() , dialogFragment.getClass().getSimpleName());
    }

    @Override
    public void startActivityForResultFromAdapter(Intent intent, int requestCode) {

    }
}
