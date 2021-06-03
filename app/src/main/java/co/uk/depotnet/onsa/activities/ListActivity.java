package co.uk.depotnet.onsa.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterList;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.DCRReasons;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.hseq.OperativeTemplate;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.timesheet.TimesheetOperative;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class ListActivity extends ThemeBaseActivity
        implements View.OnClickListener, SearchView.OnQueryTextListener {

    public final static String ARGS_THEME_COLOR = "_args_theme_color";

    private AdapterList adapter;
    private ArrayList<HashMap<String, String>> items;
    private long submissionId;
    private String repeatId;
    private String uploadId;
    private int repeatCount;
    private String keyItemType;
    private boolean isMultiSelection;
    private boolean isConcatDisplayText;
    private String themeColor;
    private SearchView searchView;
    private TextView txtToolBarTitle;
    private ImageView btnImageSearch;
    private String estimateGangId;
    private ArrayList<String> recipients;
    private DBHandler dbHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();
        this.dbHandler = DBHandler.getInstance(this);
        submissionId = intent.getLongExtra("submissionID", 0);
        repeatId = intent.getStringExtra("repeatId");
        uploadId = intent.getStringExtra("uploadId");
        keyItemType = intent.getStringExtra("keyItemType");
        repeatCount = intent.getIntExtra("repeatCount", 0);
        isMultiSelection = intent.getBooleanExtra("isMultiSelection", false);
        isConcatDisplayText = intent.getBooleanExtra("isConcatDisplayText", false);
        estimateGangId = intent.getStringExtra("estimateGangId");
        recipients = intent.getStringArrayListExtra("recipients");
        themeColor = intent.getStringExtra(ARGS_THEME_COLOR);

        String title = "Store Items";
        if (!keyItemType.isEmpty()) {
            if (keyItemType.equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)) {
                title = "DFE Items";
            } else if (keyItemType.equalsIgnoreCase(JobWorkItem.DBTable.NAME)) {
                title = "Work Items";
            } else if (keyItemType.equalsIgnoreCase(DatasetResponse.DBTable.bookOperatives)) {
                title = "Pick Your Crew";
            } else if (keyItemType.equalsIgnoreCase(HseqDataset.DBTable.OperativesHseq) || keyItemType.equalsIgnoreCase(TimesheetOperative.DBTable.NAME)) {
                title = "Operatives";
            }
            txtToolBarTitle = findViewById(R.id.txt_toolbar_title);
            txtToolBarTitle.setText(title);
        }

        ArrayList<String> selectedValues = new ArrayList<>();
        items = new ArrayList<>();

        if (!isMultiSelection) {
            Answer answer = dbHandler.getAnswer(submissionId, uploadId,
                    repeatId, repeatCount);
            if (answer != null) {
                String value = answer.getAnswer();
                if (!TextUtils.isEmpty(value)) {
                    selectedValues.add(value.trim());
                }
            }
        } else {
            ArrayList<Answer> answers;
            if(!TextUtils.isEmpty(uploadId) && uploadId.equalsIgnoreCase("additionalOperatives")){
                answers = dbHandler.getRepeatedMultiArrayAnswers(submissionId, uploadId, repeatId, repeatCount);
            }else{
                answers = dbHandler.getRepeatedAnswers(submissionId, uploadId, repeatId);
            }
                if (answers != null && !answers.isEmpty()) {
                    for (Answer answer : answers) {
                        if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                            String value = answer.getAnswer();
                            if (!TextUtils.isEmpty(value)) {
                                selectedValues.add(value.trim());
                            }
                        }
                    }
                }
        }

        if (keyItemType.equalsIgnoreCase(DCRReasons.DBTable.NAME)) {
            getDCRReasons();
        } else if (keyItemType.equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)) {
            getDfeItems();
        } else if (keyItemType.equalsIgnoreCase(JobWorkItem.DBTable.NAME)) {
            getJobWorkItem();
        } else if (keyItemType.equalsIgnoreCase(HseqDataset.DBTable.OperativesHseq)) {
            getOperativeHseqItem();
        } else if (keyItemType.equalsIgnoreCase(TimesheetOperative.DBTable.NAME)) {
            getTimeSheetOperatives();
        } else {
            ArrayList<ItemType> itemTypes = dbHandler.getItemTypes(keyItemType);
            for (ItemType w :
                    itemTypes) {
                HashMap<String, String> map = new HashMap<>();
                map.put("text", w.getDisplayItem());
                map.put("value", w.getUploadValue());
                map.put("type", w.gettype());
                items.add(map);
            }
        }


        adapter = new AdapterList(this, items, selectedValues, isMultiSelection);
//        rlSearchLayout = findViewById(R.id.rl_search_layout);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        searchView = findViewById(R.id.simpleSearchView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(adapter);

        Button btnDone = findViewById(R.id.btn_done);
        btnDone.setOnClickListener(this);
        btnImageSearch = findViewById(R.id.btn_img_search);
        btnImageSearch.setOnClickListener(this);

        searchView.setIconifiedByDefault(true);
//        searchView.setIconified(true);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

        searchView.setSearchableInfo(searchableInfo);
        searchView.setIconifiedByDefault(true);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        ImageView searchMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchMagIcon.setImageResource(R.drawable.ic_search);
        searchMagIcon.setOnClickListener(v -> {
            txtToolBarTitle.setVisibility(View.GONE);
            searchView.setIconified(false);
        });


        ImageView closeMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeMagIcon.setImageResource(R.drawable.ic_close_white);
        searchView.setOnCloseListener(() -> {
            txtToolBarTitle.setVisibility(View.VISIBLE);
            return false;
        });

        searchView.setOnQueryTextListener(this);
        if(!TextUtils.isEmpty(this.themeColor)) {
            int themeColor = Color.parseColor(this.themeColor);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                btnDone.setBackgroundTintList(ColorStateList.valueOf(themeColor));
                findViewById(R.id.toolbar).setBackgroundColor(themeColor);
                searchView.setBackgroundColor(themeColor);
            }
        }
    }

    private void getDfeItems() {
        items.clear();
        Job job = dbHandler.getJob(String.valueOf(dbHandler.getSubmission(String.valueOf(submissionId)).getJobID()));
        String contract = job.getcontract();
        int revisionNo = job.getRateIssueNumber();
        ArrayList<WorkItem> itemTypes = dbHandler.getWorkItem(keyItemType, contract, revisionNo ,WorkItem.DBTable.itemCode);
        for (WorkItem w : itemTypes) {
            HashMap<String, String> map = new HashMap<>();
            map.put("text", w.getDisplayItem());
            map.put("value", w.getUploadValue());
            map.put("type", w.gettype());
            items.add(map);
        }
    }
    private void getDCRReasons() {
            items.clear();
        ArrayList<DCRReasons> itemTypes = dbHandler.getDCRReason(dbHandler.getSubmission(String.valueOf(submissionId)).getJobID());

            for (DCRReasons w : itemTypes) {
                HashMap<String, String> map = new HashMap<>();
                map.put("text", w.getDisplayItem());
                map.put("value", w.getUploadValue());
                items.add(map);
            }
        }

    private void getJobWorkItem() {
        items.clear();

        ArrayList<JobWorkItem> itemTypes = dbHandler.getJob(dbHandler.getSubmission(String.valueOf(submissionId)).getJobID()).getworkItems();
        Collections.sort(itemTypes, (o1, o2) -> o1.getitemCode().compareTo(o2.getitemCode()));
        for (JobWorkItem w : itemTypes) {
            HashMap<String, String> map = new HashMap<>();
            String text = w.getDisplayItem() + "\n" +
                    "Quantity: " + w.getAvailableToMeasureQuantity() /*+ "\n" +
                    "Measured quantity: " + w.getMeasuredQuantity() + "\n" +
                    "Available to measure quantity: " + w.getAvailableToMeasureQuantity()*/;

            map.put("text", text);
            if(uploadId.equalsIgnoreCase("synthCode") || uploadId.equalsIgnoreCase("itemCode")){
                map.put("value", w.getitemCode());
            }else{
                map.put("value", w.getUploadValue());
            }
            map.put("itemCode", w.getitemCode());

            map.put("type", JobWorkItem.DBTable.NAME);
            items.add(map);
        }
    }

    private void getTimeSheetOperatives() {
        items.clear();
        ArrayList<TimesheetOperative> itemTypes = new ArrayList<>();
        itemTypes.addAll(dbHandler.getTimeSheetOperatives());


        for (TimesheetOperative w : itemTypes) {
            HashMap<String, String> map = new HashMap<>();
            map.put("text", w.getDisplayItem());
            map.put("value", w.getUploadValue());
            map.put("type", TimesheetOperative.DBTable.NAME);
            items.add(map);
        }
    }

    private void getOperativeHseqItem() {
        items.clear();
        ArrayList<OperativeTemplate> itemTypes = new ArrayList<>();
        if (estimateGangId != null && !estimateGangId.isEmpty()) {
            itemTypes.addAll(dbHandler.getOperativeTemplateByGangId(estimateGangId));
        } else {
            itemTypes.addAll(dbHandler.getOperativeHseq());
            ArrayList<OperativeTemplate> temp = new ArrayList<>();
            if (recipients != null) {
                for (String key : recipients) {
                    for (OperativeTemplate itemType : itemTypes) {
                        if (itemType.getUploadValue() != null && itemType.getUploadValue().equalsIgnoreCase(key)) {
                            temp.add(itemType);
                            break;
                        }
                    }
                }
                itemTypes.clear();
                itemTypes.addAll(temp);
            }
        }
        for (OperativeTemplate w : itemTypes) {

            HashMap<String, String> map = new HashMap<>();
            map.put("text", w.getDisplayItem());
            map.put("value", w.getUploadValue());
            map.put("type", HseqDataset.DBTable.OperativesHseq);
            items.add(map);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_done:
                onDonePressed();
                break;
            case R.id.btn_img_search:
                btnImageSearch.setVisibility(View.GONE);
                txtToolBarTitle.setVisibility(View.GONE);
                searchView.setVisibility(View.VISIBLE);
                break;
        }
    }

    private void onDonePressed() {
        ArrayList<HashMap<String, String>> itemTypes = adapter.getSelectedKeywords();
        if (itemTypes == null || itemTypes.isEmpty()) {

            ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionId, uploadId, repeatId);
            if (answers != null && !answers.isEmpty()) {
                for (Answer a : answers) {
                    dbHandler.removeAnswer(a);
                }
            }
            finish();
            return;
        }

        if (!isMultiSelection) {
            HashMap<String, String> itemType = itemTypes.get(0);
            saveAnswer(submissionId, uploadId,
                    repeatId, repeatCount, 0, itemType.get("value"), itemType.get("text"),0);
            finish();
            return;
        }

        StringBuilder displayBuilder = new StringBuilder();
        String displayText = "";

        if (isConcatDisplayText) {
            for (int i = 0; i < itemTypes.size(); i++) {
                HashMap<String, String> itemType = itemTypes.get(i);
                displayBuilder.append(itemType.get("text"));
                if (i < itemTypes.size() - 1) {
                    displayBuilder.append(", ");
                }
            }
            displayText = displayBuilder.toString();
        }

        ArrayList<Answer> answers;
        if(!TextUtils.isEmpty(uploadId) && uploadId.equalsIgnoreCase("additionalOperatives")){
            answers = dbHandler.getRepeatedMultiArrayAnswers(submissionId, uploadId, repeatId, repeatCount);
        }else{
            answers = dbHandler.getRepeatedAnswers(submissionId, uploadId, repeatId);;
        }

            if (answers != null && !answers.isEmpty()) {
                for (Answer a : answers) {
                    dbHandler.removeAnswer(a);
                }
            }

            for (int i = 0; i < itemTypes.size(); i++) {
                HashMap<String, String> itemType = itemTypes.get(i);
                int newRepeatCounter = repeatCount;
                int arrayRepeatCounter = 0;
                if(!TextUtils.isEmpty(uploadId) && uploadId.equalsIgnoreCase("additionalOperatives")){
                    arrayRepeatCounter = i;
                }else{
                    newRepeatCounter = i;
                }
                saveAnswer(submissionId, uploadId,
                        repeatId, newRepeatCounter , arrayRepeatCounter, itemType.get("value") , isConcatDisplayText ? displayText : itemType.get("text") , 1);
            }
        finish();
    }

    private void saveAnswer(long submissionID, String uploadID, String repeatID, int repeatCounter,int arrayRepeatCounter, String value, String displayText , int isMultiList) {
        Answer answer;

        if(!TextUtils.isEmpty(uploadID) && uploadID.equalsIgnoreCase("additionalOperatives")){
            answer = dbHandler.getAnswer(submissionID, uploadID, repeatID, repeatCounter , arrayRepeatCounter);
        }else{
            answer = dbHandler.getAnswer(submissionID, uploadID,
                    repeatID, repeatCounter);
        }

        if (answer == null) {
            answer = new Answer(submissionID, uploadID, repeatID, repeatCounter);
        }
        answer.setArrayRepeatCounter(arrayRepeatCounter);
        answer.setAnswer(value);
        answer.setDisplayAnswer(displayText);
        answer.setIsMultiList(isMultiList);
        dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        if (TextUtils.isEmpty(s)) {
            adapter.update(items);
            return true;
        }

        s = s.toLowerCase();
        ArrayList<HashMap<String, String>> keys = new ArrayList<>();

        for (HashMap<String, String> itemType : items) {
            String keyword = itemType.get("text");
            if(!TextUtils.isEmpty(keyword)){
                keyword = keyword.toLowerCase(Locale.ENGLISH);
                if (keyword.startsWith(s)) {
                    keys.add(itemType);
                }
            }
        }

        if (keys.isEmpty()) {
            adapter.update(items);
            return true;
        }


        adapter.update(keys);
        return true;
    }
}
