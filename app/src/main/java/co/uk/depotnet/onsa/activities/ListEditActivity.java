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
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterEditList;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;

public class ListEditActivity extends AppCompatActivity {

    private AdapterEditList adapter;
    private ArrayList<HashMap<String, String>> items;
    private long submissionId;
    private String repeatId;
    private String uploadId;
    private int repeatCount;
    private String keyItemType;
    private boolean isMultiSelection;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
try {
    Intent intent = getIntent();

    submissionId = intent.getLongExtra("submissionID", 0);
    repeatId = intent.getStringExtra("repeatId");
    uploadId = intent.getStringExtra("uploadId");
    keyItemType = intent.getStringExtra("keyItemType");
    repeatCount = intent.getIntExtra("repeatCount", 0);
    isMultiSelection = intent.getBooleanExtra("isMultiSelection", false);

    HashMap<String, String> selectedValues = new HashMap<>();
    items = new ArrayList<>();
    Answer answer = DBHandler.getInstance().getAnswer(submissionId, uploadId,
            repeatId, repeatCount);
    Answer quantity = DBHandler.getInstance().getAnswer(submissionId, "quantity",
            uploadId, repeatCount);
    if (answer != null && quantity != null &&
            !TextUtils.isEmpty(answer.getAnswer()) &&
            !TextUtils.isEmpty(quantity.getAnswer())) {
        String value = answer.getAnswer().trim();
        String quantities = quantity.getAnswer().trim();
        if (!isMultiSelection) {
            selectedValues.put(value, quantity.getAnswer());
        } else {
            String[] values = value.split(",");
            String[] qtyies = quantities.split(",");

            for (int i = 0; i < values.length && i < qtyies.length; i++) {
                String vl = values[i];
                String qty = qtyies[i];
                if (!TextUtils.isEmpty(vl)) {
                    vl = vl.trim();
                    selectedValues.put(vl, qty);
                }
            }

        }
    }

    ArrayList<ItemType> itemTypes = DBHandler.getInstance().getItemTypes(keyItemType);
    for (ItemType w :
            itemTypes) {
        HashMap<String, String> map = new HashMap<>();
        map.put("text", w.getDisplayItem());
        map.put("value", w.getUploadValue());
        map.put("type", w.gettype());

        if (selectedValues.containsKey(w.getUploadValue())) {
            map.put("quantity", selectedValues.get(w.getUploadValue()));
        }

        items.add(map);

    }


    adapter = new AdapterEditList(this, items, selectedValues, isMultiSelection);
    RecyclerView recyclerView = findViewById(R.id.recycler_view);
    SearchView searchView = findViewById(R.id.simpleSearchView);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
    recyclerView.setAdapter(adapter);

    findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if (adapter.getFocusedEditText() != null) {
                adapter.getFocusedEditText().clearFocus();
            }
            ArrayList<HashMap<String, String>> itemTypes = adapter.getSelectedKeywords();


            if (itemTypes != null && !itemTypes.isEmpty()) {

                for (int i = 0; i < itemTypes.size(); i++) {
                    HashMap<String, String> itemType = itemTypes.get(i);
                    String qt = itemType.get("quantity");
                    if (TextUtils.isEmpty(qt)) {
                        Toast.makeText(ListEditActivity.this, "Please enter quantity in all selected items", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    qt = qt.trim();
                    int q = 0;
                    try {
                        q = Integer.parseInt(qt);
                    } catch (Exception e) {
                        q = 0;
                    }
                    if (q <= 0) {
                        Toast.makeText(ListEditActivity.this, "Please enter quantity greater than zero for all selected items", Toast.LENGTH_SHORT).show();
                        return;
                    }

                }

                String displayText = "";
                String value = "";
                String quantities = "";

                if (!isMultiSelection) {
                    HashMap<String, String> itemType = itemTypes.get(0);
                    value = itemType.get("value");
                    displayText = itemType.get("text");
                    quantities = itemType.get("quantity");
                } else {

                    for (int i = 0; i < itemTypes.size(); i++) {
                        HashMap<String, String> itemType = itemTypes.get(i);
                        displayText += itemType.get("text") + "(" + itemType.get("quantity") + ")" + ", ";
                        value += itemType.get("value") + ",";
                        quantities += itemType.get("quantity") + ",";
                    }

                    displayText = displayText.substring(0, displayText.lastIndexOf(","));
                    value = value.substring(0, value.lastIndexOf(","));
                    quantities = quantities.substring(0, quantities.lastIndexOf(","));

                }

                Answer answer = DBHandler.getInstance().getAnswer(submissionId, uploadId,
                        repeatId, repeatCount);

                if (answer == null) {
                    answer = new Answer(submissionId, uploadId);
                }

                Answer quantity = DBHandler.getInstance().getAnswer(submissionId, "quantity",
                        uploadId, repeatCount);
                if (quantity == null) {
                    quantity = new Answer(submissionId, "quantity");
                }

                answer.setAnswer(value);
                answer.setDisplayAnswer(displayText);
                answer.setRepeatID(repeatId);
                answer.setIsMultiList(1);
                answer.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());

                quantity.setAnswer(quantities);
                quantity.setDisplayAnswer(quantities);
                quantity.setRepeatID(uploadId);
                quantity.setIsMultiList(1);
                quantity.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, quantity.toContentValues());


            }

            finish();
        }
    });

    searchView.setIconified(true);


    SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
    SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());

    searchView.setSearchableInfo(searchableInfo);
    searchView.setIconifiedByDefault(true);

    EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
    searchEditText.setTextColor(getResources().getColor(R.color.white));
    searchEditText.setHintTextColor(getResources().getColor(R.color.white));

    ImageView searchMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
    searchMagIcon.setImageResource(R.drawable.ic_search);
    ImageView closeMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
    closeMagIcon.setImageResource(R.drawable.ic_close_white);


    searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
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
    });
}catch(Exception e){

}
    }

}
