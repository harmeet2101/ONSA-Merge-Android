package co.uk.depotnet.onsa.fragments.hseq;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.ui.ScheduleInspectionActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.utils.AppPreferences;

public class HseqHomeFragment extends Fragment implements View.OnClickListener {

    private Context context;

    public static HseqHomeFragment newInstance() {
        HseqHomeFragment fragment = new HseqHomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_hseq_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        view.findViewById(R.id.img_new_inspection).setOnClickListener(this);
        view.findViewById(R.id.txt_new_inspection).setOnClickListener(this);
        view.findViewById(R.id.schedule_hseq).setOnClickListener(this);
    }

    private void startInspection(){
        AppPreferences.setTheme(1);// to reset value for default theme
        String jsonFileName = "schedule_inspection.json";
        Submission submission = new Submission(jsonFileName, "New Inspection", "null");
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Answer answerjobID = DBHandler.getInstance().getAnswer(submissionID,
                "scheduledInspectionId", null, 0);

        if (answerjobID == null) {
            answerjobID = new Answer(submissionID, "scheduledInspectionId");
        }

        answerjobID.setAnswer(null);
        answerjobID.setDisplayAnswer("Due id");
        answerjobID.setRepeatCount(0);
        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answerjobID.toContentValues());
        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivity(intent);
    }

    private void loadSchedule() {
        Intent schedu = new Intent(context, ScheduleInspectionActivity.class);
        startActivity(schedu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_new_inspection:
            case R.id.txt_new_inspection:
                startInspection();
                break;
            case R.id.schedule_hseq:
                loadSchedule();
                break;

        }
    }
}