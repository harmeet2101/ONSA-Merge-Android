package co.uk.depotnet.onsa.activities;

import android.app.SearchManager;
import android.app.SearchableInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SearchView;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.AdapterStoreListItem;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;

public class ListStoreItemActivity extends AppCompatActivity {

    private AdapterStoreListItem adapter;
    private ArrayList<MyStore> items;
    private long submissionId;
    private String repeatId;
    private boolean isStoackLevelCheck;
    private LinearLayout llUiBlocker;
    private Handler handler;
    private  SearchView searchView;
    private ImageView btnImageSearch;
    private TextView txtToolbarTitle;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Intent intent = getIntent();

        txtToolbarTitle = findViewById(R.id.txt_toolbar_title);
        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        submissionId = intent.getLongExtra("submissionID", 0);
        FormItem formItem = intent.getParcelableExtra("formItem");
        repeatId = formItem.getRepeatId();
        isStoackLevelCheck = formItem.isStoackLevelCheck();

        HashMap<String, String> selectedValues = new HashMap<>();
        items = new ArrayList<>();

        ArrayList<Answer> staStockItemIds = DBHandler.getInstance().
                getRepeatedAnswers(submissionId, "StaStockItemId", repeatId);

        Answer staId = DBHandler.getInstance().
                getAnswer(submissionId, "StaId", repeatId, 0);



        if (staStockItemIds != null && !staStockItemIds.isEmpty()) {
            for (Answer staStockItemId :
                    staStockItemIds) {
                if (staStockItemId != null) {
                    Answer quantity = DBHandler.getInstance().getAnswer(submissionId, "Quantity", repeatId, staStockItemId.getRepeatCount());
                    if (quantity != null &&
                            !TextUtils.isEmpty(quantity.getAnswer())) {
                        selectedValues.put(staStockItemId.getAnswer(), quantity.getAnswer());
                    }
                }
            }
        }



        if (staId != null && !TextUtils.isEmpty(staId.getAnswer())) {
            items = DBHandler.getInstance().getMyStoresByStaId(staId.getAnswer());
        } else {
            items = DBHandler.getInstance().getMyStores();
        }


        adapter = new AdapterStoreListItem(this, items, selectedValues, true);
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(10));
        recyclerView.setAdapter(adapter);
        btnImageSearch = findViewById(R.id.btn_img_search);
        btnImageSearch.setOnClickListener(v -> {
            btnImageSearch.setVisibility(View.GONE);
            txtToolbarTitle.setVisibility(View.GONE);
            searchView.setVisibility(View.VISIBLE);
            searchView.setIconified(false);
        });
        findViewById(R.id.btn_done).setOnClickListener(v -> {

            if (adapter.getFocusedEditText() != null) {
                adapter.getFocusedEditText().clearFocus();
            }

            onDonePressed();

        });


        searchView = findViewById(R.id.simpleSearchView);
        searchView.setIconified(true);


        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        if(searchManager != null) {
            SearchableInfo searchableInfo = searchManager.getSearchableInfo(getComponentName());
            searchView.setSearchableInfo(searchableInfo);
        }


        searchView.setIconifiedByDefault(true);

        EditText searchEditText = searchView.findViewById(androidx.appcompat.R.id.search_src_text);
        searchEditText.setTextColor(getResources().getColor(R.color.white));
        searchEditText.setHintTextColor(getResources().getColor(R.color.white));

        ImageView searchMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_button);
        searchMagIcon.setImageResource(R.drawable.ic_search);
        ImageView closeMagIcon = searchView.findViewById(androidx.appcompat.R.id.search_close_btn);
        closeMagIcon.setImageResource(R.drawable.ic_close_white);
        closeMagIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeSearch();
            }
        });

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
                ArrayList<MyStore> keys = new ArrayList<>();

                for (MyStore itemType : items) {
                    String description = itemType.getdescription();
                    String barcode = itemType.getbarcode();

                    boolean isMatched = false;

                    if (!TextUtils.isEmpty(description) ) {
                        description = description.toLowerCase();
                        isMatched = description.startsWith(s);
                    }

                    if(!isMatched && !TextUtils.isEmpty(barcode)){
                        barcode = barcode.toLowerCase();
                        isMatched = barcode.startsWith(s);
                    }

                    if(isMatched){
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
    }

    private void closeSearch() {
        btnImageSearch.setVisibility(View.VISIBLE);
//        btnImageCancel.setVisibility(View.VISIBLE);
        txtToolbarTitle.setVisibility(View.VISIBLE);
        searchView.setVisibility(View.GONE);
        searchView.setQuery("", false);
//        adapter.resetSearch();
    }

    public void showProgressBar() {
        llUiBlocker.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    public void hideProgressBar() {
        llUiBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void showErrorDialog(String title, String message) {
        if(getSupportFragmentManager().isStateSaved()){
            return;
        }
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> dialog1.dismiss())
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }

    private void onDonePressed() {
        showProgressBar();
        handler = new Handler();
        new Thread(() -> {
            ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionId, "StaStockItemId", repeatId);
            if (answers != null && !answers.isEmpty()) {
                for (Answer answer :
                        answers) {
                    DBHandler.getInstance().removeAnswer(answer);
                }
            }

            HashMap<String, String> selectedKeywords = adapter.getSelectedKeywords();
            if (selectedKeywords == null || selectedKeywords.isEmpty()) {
                handler.post(() -> {
                    hideProgressBar();
                    finish();
                });
                return;
            }

            Set<String> keys = selectedKeywords.keySet();


            for (String key : keys) {
                String value = selectedKeywords.get(key);
                if (TextUtils.isEmpty(value)) {
                    handler.post(() -> {
                        hideProgressBar();
                        showErrorDialog("Error", "Please add Quantity");
                    });

                    return;
                }
                double qty = 0;
                try {
                    qty = Double.parseDouble(value);
                } catch (Exception e) {

                }

                if (qty <= 0) {
                    handler.post(() -> {
                        hideProgressBar();
                        showErrorDialog("Error", "Please add quantity greater than zero");
                    });

                    return;
                }

                if (isStoackLevelCheck) {
                    updateStockLevel(keys);
                    for (MyStore item : items) {
                        if (item.getStaStockItemId().equalsIgnoreCase(key)) {

                            if(item.getquantity() == 0){
                                handler.post(() -> {
                                    hideProgressBar();
                                    showErrorDialog("Error", "This item is not in Stock.");
                                });
                                return;
                            }

                            if (qty > item.getquantity()) {
                                handler.post(() -> {
                                    hideProgressBar();
                                    showErrorDialog("Error", "Quantity is greater than stocklevel");
                                });
                                return;
                            }
                        }
                    }
                }
            }


            int repeatCount = 0;


            for (String key : keys) {
                Answer staStockItemId = DBHandler.getInstance().getAnswer(submissionId, "StaStockItemId",
                        repeatId, repeatCount);

                if (staStockItemId == null) {
                    staStockItemId = new Answer(submissionId, "StaStockItemId");
                }


                StockItems items = DBHandler.getInstance().getStockItems(key);
                staStockItemId.setAnswer(key);
                if (items != null) {
                    staStockItemId.setDisplayAnswer(items.getaltName());
                } else {
                    staStockItemId.setDisplayAnswer(key);
                }

                staStockItemId.setRepeatID(repeatId);
                staStockItemId.setRepeatCount(repeatCount);

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, staStockItemId.toContentValues());

                Answer quantity = DBHandler.getInstance().getAnswer(submissionId, "Quantity",
                        "Quantity", repeatCount);
                if (quantity == null) {
                    quantity = new Answer(submissionId, "Quantity");
                }

                quantity.setAnswer(selectedKeywords.get(key));
                quantity.setDisplayAnswer(selectedKeywords.get(key));
                quantity.setRepeatID(repeatId);
                quantity.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, quantity.toContentValues());
                repeatCount++;
            }

            handler.post(() -> {
                hideProgressBar();
                finish();
            });
        }).start();


    }

    private void updateStockLevel(final Set<String> keys) {
        /*User user = DBHandler.getInstance().getUser();
        if (user != null && !items.isEmpty()) {
            for (String key : keys) {
                for (int i = 0; i < items.size(); i++) {
                    final MyStore si = items.get(i);
                    if (si.getStaStockItemId().equalsIgnoreCase(key)) {
                        String url = Constants.BASE_URL + "appstores/getstocklevel?StaId=" + si.getStaId() +
                                "&StockItemId=" + si.getstockItemId();

                        okhttp3.Response response = new ConnectionHelper(ListStoreItemActivity.this).performNetworking(url, null);
                        if (response != null && response.isSuccessful()) {
                            ResponseBody body = response.body();
                            if (body != null) {
                                try {
                                    String data = body.string();
                                    if (!TextUtils.isEmpty(data)) {
                                        StockLevel stockLevel = new Gson().fromJson(data, StockLevel.class);
                                        if (stockLevel != null) {
                                            si.setStockLevel(stockLevel.getStockLevel());
                                        }
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                }
            }
        }*/
    }

}
