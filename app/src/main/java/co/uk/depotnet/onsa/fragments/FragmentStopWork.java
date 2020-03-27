package co.uk.depotnet.onsa.fragments;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import co.uk.depotnet.onsa.R;

import co.uk.depotnet.onsa.listeners.FromActivityListener;
import co.uk.depotnet.onsa.modals.forms.Submission;


public class FragmentStopWork extends Fragment implements View.OnClickListener {

    private static final String ARG_SUBMISSION = "Submission";
    private Context context;

    private String jobID;
    private Submission submission;
    private FromActivityListener listener;


    public static FragmentStopWork newInstance(Submission submission) {
        FragmentStopWork fragment = new FragmentStopWork();
        Bundle args = new Bundle();
        args.putParcelable(ARG_SUBMISSION, submission);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            submission = args.getParcelable(ARG_SUBMISSION);
        }

        jobID = submission.getJobID();
    }



    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_stop_work, container, false);
        rootView.findViewById(R.id.btn_back).setOnClickListener(this);
        rootView.findViewById(R.id.btn_submit).setOnClickListener(this);
        return rootView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if(context instanceof FromActivityListener){
            listener = (FromActivityListener) context;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_back:
                listener.popFragmentImmediate();
//                ((FormActivity)context).popFragmentImmediate();
                break;
            case R.id.btn_submit:
                submit();
                break;
        }
    }

    public void submit() {
        /*DBHandler.getInstance().removeAnswers(submission);
        ((Activity)context).finish();*/

        listener.popFragmentImmediate();
//        ((FormActivity)context).popFragmentImmediate();
    }

}
