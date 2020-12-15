package co.uk.depotnet.onsa.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterAssets;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.JobModuleStatus;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.utils.JsonReader;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class AssetDataActivity extends AppCompatActivity
        implements View.OnClickListener, OnItemClickListener<FormItem> {

    public static final String ARG_SUBMISSION = "Submission";


    private ArrayList<FormItem> poleItems;
    private AdapterAssets adapter;
    private RelativeLayout rlWarning;
    private RecyclerView recyclerView;
    private LinearLayout llBtnContainer;
    private Submission submission;
    private Screen screen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_asset_data);

        Intent intent = getIntent();
        submission = intent.getParcelableExtra(ARG_SUBMISSION);

        poleItems = new ArrayList<>();
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_go_to_asset).setOnClickListener(this);
        findViewById(R.id.btn_back).setOnClickListener(this);
        findViewById(R.id.btn_submit).setOnClickListener(this);
        rlWarning = findViewById(R.id.rl_warning);
        recyclerView = findViewById(R.id.recycler_view);
        llBtnContainer = findViewById(R.id.ll_btn_container);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(layoutManager);
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(20);
        recyclerView.addItemDecoration(itemDecoration);

        screen = JsonReader.loadForm(this, "asset_survey.json").getScreens().get(1);
        adapter = new AdapterAssets(this, submission.getID(), poleItems, this, screen);
        recyclerView.setAdapter(adapter);

        rlWarning.setVisibility(View.GONE);
        llBtnContainer.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.VISIBLE);


    }

    private void initPoleItems() {
        int poleCount = getPoleCount();
        if (poleCount == 0) {
            return;
        }

        poleItems.clear();
        for (int i = 1; i <= poleCount; i++) {
            FormItem qItem = new FormItem("dialog_screen", "Pole " + i,
                    "", "", true);
            qItem.setRepeatCount(i - 1);
            poleItems.add(qItem);

        }

        adapter.notifyDataSetChanged();
    }

    private int getPoleCount() {

        Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "NoOfPoles",
                "majorStoreItems", 0);
        if (answer == null) {
            return 0;
        }

        String value = answer.getAnswer();
        if (TextUtils.isEmpty(value)) {
            return 0;
        }


        try {
            return Integer.parseInt(value);
        } catch (Exception e) {

        }


        return 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        initPoleItems();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_go_to_asset:
                rlWarning.setVisibility(View.GONE);
                llBtnContainer.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_back: {
                for (int i = 0; i < poleItems.size(); i++) {
                    if (!adapter.isValidate(i)) {
                        setResult(RESULT_CANCELED);
                        finish();
                        return;
                    }
                }

                JobModuleStatus jobModuleStatus = new JobModuleStatus();
                jobModuleStatus.setStatus(true);
                jobModuleStatus.setJobId(submission.getJobID());
                jobModuleStatus.setModuleName("poling_asset_data.json");
                jobModuleStatus.setSubmissionId(submission.getID());
                DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                        jobModuleStatus.toContentValues());

                setResult(RESULT_OK);
                finish();
            }
            break;
            case R.id.btn_submit:
                for (int i = 0; i < poleItems.size(); i++) {
                    if (!adapter.isValidate(i)) {
                        Toast.makeText(this, "Please enter all poles data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }
                JobModuleStatus jobModuleStatus = new JobModuleStatus();
                jobModuleStatus.setStatus(true);
                jobModuleStatus.setJobId(submission.getJobID());
                jobModuleStatus.setModuleName("poling_asset_data.json");
                jobModuleStatus.setSubmissionId(submission.getID());
                DBHandler.getInstance().replaceData(JobModuleStatus.DBTable.NAME,
                        jobModuleStatus.toContentValues());

                setResult(RESULT_OK);
                finish();
                break;
        }
    }


    @Override
    public void onItemClick(FormItem formItem, int position) {
        submission.setJsonFile("asset_survey.json");
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        intent.putExtra(FormActivity.ARG_REPEAT_COUNT, position);
        startActivityForResult(intent, 1234);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1234) {
            setResult(Activity.RESULT_OK);
            finish();
        }
    }
}
