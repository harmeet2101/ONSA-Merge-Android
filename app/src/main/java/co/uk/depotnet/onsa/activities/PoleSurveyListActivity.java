package co.uk.depotnet.onsa.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterUnSubmittedSurvey;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class PoleSurveyListActivity extends AppCompatActivity implements View.OnClickListener,
        OnItemClickListener<Submission> {

    public static final String ARG_USER = "User";
    public static final String ARG_JOB_ID = "JOB_ID";


    private User user;
    private ArrayList<Submission> submissions;
    private String jobID;
    private AdapterUnSubmittedSurvey adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pole_survey_list);

        Intent intent = getIntent();
        user = intent.getParcelableExtra(ARG_USER);
        jobID = intent.getStringExtra(ARG_JOB_ID);
        submissions = new ArrayList<>();
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_add_new_survey).setOnClickListener(this);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);


        adapter = new AdapterUnSubmittedSurvey(this, submissions, this);
        recyclerView.setAdapter(adapter);

    }


    @Override
    protected void onResume() {
        super.onResume();
        submissions.clear();
        submissions.addAll(DBHandler.getInstance().getSubmissionsByJobAndTitle("poling_survey" ,
                jobID));
        adapter.notifyDataSetChanged();

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_add_new_survey:
                Submission submission = new Submission("poling_job_data.json" , "poling_survey" , jobID);
                long ID = DBHandler.getInstance().insertData(Submission.DBTable.NAME , submission.toContentValues());
                submission.setId(ID);
                Intent intent = new Intent(this, PollingSurveyActivity.class);
                intent.putExtra(PollingSurveyActivity.ARG_USER, user);
                intent.putExtra(PollingSurveyActivity.ARG_SUBMISSION, submission);
                startActivityForResult(intent , 1234);
                break;
        }
    }


    @Override
    public void onItemClick(Submission submission, int position) {
        Intent intent = new Intent(this, PollingSurveyActivity.class);
        intent.putExtra(FormActivity.ARG_USER, user);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        intent.putExtra(FormActivity.ARG_REPEAT_COUNT, position);
        startActivityForResult(intent , 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1234){
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
