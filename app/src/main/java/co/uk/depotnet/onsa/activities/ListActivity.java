package co.uk.depotnet.onsa.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;

import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ui.HSEQActivity;
import co.uk.depotnet.onsa.adapters.AdapterList;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.hseq.OperativeTemplate;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class ListActivity extends AppCompatActivity
        implements View.OnClickListener, SearchView.OnQueryTextListener {

    private AdapterList adapter;
    private ArrayList<HashMap<String, String>> items;
    private long submissionId;
    private String repeatId;
    private String uploadId;
    private int repeatCount;
    private String keyItemType;
    private boolean isMultiSelection;
    private boolean isConcatDisplayText;
    private SearchView searchView;
    private TextView txtToolBarTitle;
    private ImageView btnImageSearch;
    private String estimateGangId;
    private ArrayList<String> recipients;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        Intent intent = getIntent();

        submissionId = intent.getLongExtra("submissionID", 0);
        repeatId = intent.getStringExtra("repeatId");
        uploadId = intent.getStringExtra("uploadId");
        keyItemType = intent.getStringExtra("keyItemType");
        repeatCount = intent.getIntExtra("repeatCount", 0);
        isMultiSelection = intent.getBooleanExtra("isMultiSelection", false);
        isConcatDisplayText = intent.getBooleanExtra("isConcatDisplayText", false);
        estimateGangId = intent.getStringExtra("estimateGangId");
        recipients = intent.getStringArrayListExtra("recipients");
        String title = "Store Items";
        if(!keyItemType.isEmpty()){
            if (keyItemType.equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)){
                title = "DFE Items";
            }else if(keyItemType.equalsIgnoreCase(JobWorkItem.DBTable.NAME)) {
                title = "Work Items";
            }else if( keyItemType.equalsIgnoreCase(DatasetResponse.DBTable.bookOperatives)){
                title = "Pick Your Crew";
            }else if( keyItemType.equalsIgnoreCase(HseqDataset.DBTable.OperativesHseq)){
                title = "Operatives";
            }
            txtToolBarTitle = findViewById(R.id.txt_toolbar_title);
            txtToolBarTitle.setText(title);
        }

        ArrayList<String> selectedValues = new ArrayList<>();
        items = new ArrayList<>();

        if (!isMultiSelection) {
            Answer answer = DBHandler.getInstance().getAnswer(submissionId, uploadId,
                    repeatId, repeatCount);
            if (answer != null) {
                String value = answer.getAnswer();
                if (!TextUtils.isEmpty(value)) {
                    selectedValues.add(value.trim());
                }
            }
        } else {

            ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionId, uploadId, repeatId);

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

        if (keyItemType.equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)) {
            getDfeItems();
        }else if(keyItemType.equalsIgnoreCase(JobWorkItem.DBTable.NAME)){
            getJobWorkItem();
        }else if(keyItemType.equalsIgnoreCase(HseqDataset.DBTable.OperativesHseq)) {
            getOperativeHseqItem();
        } else {
            ArrayList<ItemType> itemTypes = DBHandler.getInstance().getItemTypes(keyItemType);
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

        findViewById(R.id.btn_done).setOnClickListener(this);
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
        searchMagIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtToolBarTitle.setVisibility(View.GONE);
                searchView.setIconified(false);
            }
        });


        ImageView closeMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeMagIcon.setImageResource(R.drawable.ic_close_white);
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                txtToolBarTitle.setVisibility(View.VISIBLE);
//                btnImageSearch.setVisibility(View.VISIBLE);
//                searchView.setIconified(true);
//                searchView.setVisibility(View.VISIBLE);
                return false;
            }
        });

        searchView.setOnQueryTextListener(this);
    }

    private void getDfeItems() {
        items.clear();
        String contract = DBHandler.getInstance().getJob(DBHandler.getInstance().getSubmission(String.valueOf(submissionId)).getJobID()).getcontract();
        ArrayList<WorkItem> itemTypes = DBHandler.getInstance().getWorkItem(keyItemType, contract ,WorkItem.DBTable.itemCode);
        for (WorkItem w : itemTypes) {
            HashMap<String, String> map = new HashMap<>();
            map.put("text", w.getDisplayItem());
            map.put("value", w.getUploadValue());
            map.put("type", w.gettype());
            items.add(map);
        }
    }

    private void getJobWorkItem() {
        items.clear();

        ArrayList<JobWorkItem> itemTypes = DBHandler.getInstance().getJob(DBHandler.getInstance().getSubmission(String.valueOf(submissionId)).getJobID()).getworkItems();
        Collections.sort(itemTypes, new Comparator<JobWorkItem>() {
            @Override
            public int compare(JobWorkItem o1, JobWorkItem o2) {
                return o1.getitemCode().compareTo(o2.getitemCode());
            }
        });
        for (JobWorkItem w : itemTypes) {
            HashMap<String, String> map = new HashMap<>();
            String text = w.getDisplayItem()+"\n" +
                    "Quantity: "+w.getquantity()+"\n" +
                    "Measured quantity: "+w.getMeasuredQuantity()+"\n" +
                    "Available to measure quantity: "+w.getAvailableToMeasureQuantity();

            map.put("text", text);
            map.put("value", w.getUploadValue());
            map.put("type", JobWorkItem.DBTable.NAME);
            items.add(map);
        }
    }

    private void getOperativeHseqItem() {
        items.clear();
        ArrayList<OperativeTemplate> itemTypes=new ArrayList<>();
        if (estimateGangId!=null && !estimateGangId.isEmpty()) {
            itemTypes.addAll(DBHandler.getInstance().getOperativeTemplateByGangId(estimateGangId));
        }
        else {
            itemTypes.addAll(DBHandler.getInstance().getOperativeHseq());
            ArrayList<OperativeTemplate> temp = new ArrayList<>();
            if(recipients != null){
                for (String key : recipients){
                    for(OperativeTemplate itemType : itemTypes){
                        if(itemType.getUploadValue() != null && itemType.getUploadValue().equalsIgnoreCase(key)){
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


        if (itemTypes != null && !itemTypes.isEmpty()) {
            if (!isMultiSelection) {
                Answer answer = DBHandler.getInstance().getAnswer(submissionId, uploadId,
                        repeatId, repeatCount);
                if (answer == null) {
                    answer = new Answer(submissionId, uploadId);
                }

                HashMap<String, String> itemType = itemTypes.get(0);

                answer.setAnswer(itemType.get("value"));
                answer.setDisplayAnswer(itemType.get("text"));
                answer.setRepeatID(repeatId);
                answer.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
            } else {
                String displayText = "";
                if(isConcatDisplayText) {

                    for (int i = 0; i < itemTypes.size(); i++) {
                        HashMap<String, String> itemType = itemTypes.get(i);
                        displayText += itemType.get("text")+", ";
                    }

                    displayText = displayText.substring(0, displayText.lastIndexOf(","));
                }


                ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionId, uploadId, repeatId);

                if (answers != null && !answers.isEmpty()) {
                    for (Answer a : answers) {
                        DBHandler.getInstance().removeAnswer(a);
                    }
                }

                for (int i = 0; i < itemTypes.size(); i++) {
                    Answer answer = DBHandler.getInstance().getAnswer(submissionId, uploadId,
                            repeatId, i);
                    if (answer == null) {
                        answer = new Answer(submissionId, uploadId);
                    }

                    HashMap<String, String> itemType = itemTypes.get(i);
                    answer.setAnswer(itemType.get("value"));
                    answer.setDisplayAnswer(isConcatDisplayText?displayText:itemType.get("text"));
                    answer.setRepeatID(repeatId);
                    answer.setIsMultiList(1);
                    answer.setRepeatCount(i);
                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
                }
            }
        }else{
            ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionId, uploadId, repeatId);

            if (answers != null && !answers.isEmpty()) {
                for (Answer a : answers) {
                    DBHandler.getInstance().removeAnswer(a);
                }
            }
        }

        finish();
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
            String keyword = itemType.get("text").toLowerCase();
            if (keyword.startsWith(s)) {
                keys.add(itemType);
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
