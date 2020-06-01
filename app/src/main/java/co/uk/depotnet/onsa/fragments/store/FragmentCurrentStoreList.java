package co.uk.depotnet.onsa.fragments.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SearchView;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.adapters.store.AdapterStore;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.JWTErrorDialog;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.store.DataMyStores;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;
import co.uk.depotnet.onsa.views.CurrentStoreFilterBottomSheet;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FragmentCurrentStoreList extends Fragment
        implements OnItemClickListener<MyStore>, View.OnClickListener,
        AdapterStore.CurrentStoreListener , CurrentStoreFilterBottomSheet.FilterListener {


    private static final String ARG_USER = "user";

    private User user;
    private Context context;
    private FragmentActionListener listener;
    private AdapterStore adapter;
    private ArrayList<MyStore> stores;
    private ImageView btnFilter;
    private TextView txtSelectAll;
    private boolean isFilterOn;

    private SearchView searchView;
    private RelativeLayout rlBottomPanel;
    private HorizontalScrollView actionScrollView;
    private TextView txtResetFilter;
    private TextView txtFilterBy;
    private TextView txtFilterValue;
    private ImageView btnImageSearch;
    private ImageView btnImageCancel;
    private TextView txtToolbarTitle;


    public static FragmentCurrentStoreList newInstance(User user) {
        FragmentCurrentStoreList fragment = new FragmentCurrentStoreList();
        Bundle args = new Bundle();
        args.putParcelable(ARG_USER, user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
        if (context instanceof FragmentActionListener) {
            listener = (FragmentActionListener) context;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            user = args.getParcelable(ARG_USER);
        }

        stores = new ArrayList<>();
        adapter = new AdapterStore(context, stores, this, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_current_store_list, container, false);

        searchView = view.findViewById(R.id.simpleSearchView);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        VerticalSpaceItemDecoration itemDecoration = new VerticalSpaceItemDecoration(24);
        recyclerView.addItemDecoration(itemDecoration);

        recyclerView.setAdapter(adapter);
        txtSelectAll = view.findViewById(R.id.txt_select_all);
        txtResetFilter = view.findViewById(R.id.btn_reset_filter);
        txtFilterBy = view.findViewById(R.id.txt_filter_by);
        txtFilterValue = view.findViewById(R.id.txt_filter_value);
        txtSelectAll.setOnClickListener(this);
        txtResetFilter.setOnClickListener(this);
        btnImageSearch = view.findViewById(R.id.btn_img_search);
        btnImageSearch.setOnClickListener(this);
        btnImageCancel = view.findViewById(R.id.btn_img_cancel);
        btnImageCancel.setOnClickListener(this);
        btnFilter = view.findViewById(R.id.btn_filter);
        btnFilter.setOnClickListener(this);

        if(isFilterOn){
            txtResetFilter.setVisibility(View.VISIBLE);
            btnFilter.setVisibility(View.GONE);
        }else{
            txtResetFilter.setVisibility(View.GONE);
            btnFilter.setVisibility(View.VISIBLE);
        }
        view.findViewById(R.id.btn_add).setOnClickListener(this);

        txtToolbarTitle = view.findViewById(R.id.txt_toolbar_title);
        view.findViewById(R.id.btn_txt_request).setOnClickListener(this);
        view.findViewById(R.id.btn_txt_goods_in).setOnClickListener(this);
        view.findViewById(R.id.btn_txt_transfer).setOnClickListener(this);
        view.findViewById(R.id.btn_txt_issue).setOnClickListener(this);
        view.findViewById(R.id.btn_txt_goods_out).setOnClickListener(this);
        view.findViewById(R.id.btn_txt_estimate).setOnClickListener(this);

        int searchCloseButtonId = searchView.getContext().getResources()
                .getIdentifier("android:id/search_close_btn", null, null);
        ImageView closeButton = this.searchView.findViewById(searchCloseButtonId);
        closeButton.setImageResource(R.drawable.ic_close_white);

        int id = searchView.getContext()
                .getResources()
                .getIdentifier("android:id/search_src_text", null, null);
        TextView textView = searchView.findViewById(id);
        textView.setTextColor(Color.WHITE);
        textView.setHintTextColor(Color.WHITE);


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                onSearch(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        closeButton.setOnClickListener(v -> closeSearch());

        rlBottomPanel = view.findViewById(R.id.bottom_panel);
        actionScrollView = view.findViewById(R.id.hs_action_panel);

        getCurrentStoreList();
        return view;
    }

    private void closeSearch() {
            btnImageSearch.setVisibility(View.VISIBLE);
            btnImageCancel.setVisibility(View.VISIBLE);
            txtToolbarTitle.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
            searchView.setQuery("", false);
            adapter.resetSearch();
    }

    private void onSearch(String query) {
        adapter.onSearch(query);
    }


    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }


    @Override
    public void onItemClick(MyStore store, int position) {
        listener.addFragment(FragmentCurrentStoreDetail.newInstance(store), false);
    }

    @Override
    public void onClick(View v) {
        adapter.clearFocus();
        switch (v.getId()) {
            case R.id.btn_reset_filter:
                adapter.reset(stores);
                isFilterOn = false;
                txtFilterValue.setText("");
                txtResetFilter.setVisibility(View.GONE);
                txtFilterBy.setVisibility(View.GONE);
                txtFilterValue.setVisibility(View.GONE);
                btnFilter.setVisibility(View.VISIBLE);
                break;
            case R.id.btn_img_cancel:
                if (adapter.isMultiSelectionEnabled()) {
                    adapter.cancelMultiSelect();
                } else {
                    ((Activity)context).onBackPressed();
                }

                break;
            case R.id.txt_select_all:
                adapter.selectAll();
                break;
            case R.id.btn_add:
                onStandardRequest("store_log_standard_request.json" , "Request");
                break;
            case R.id.btn_filter:

                final ArrayList<DropDownItem> itemsSta = new ArrayList<>();
                final ArrayList<DropDownItem> itemsbatchRef = DBHandler.getInstance().getUniqueBatchrefBy();

                itemsSta.addAll(DBHandler.getInstance().getItemTypes(StoreDataset.DBTable.stas));
                if (itemsSta.isEmpty()) {
                    return;
                }

                CurrentStoreFilterBottomSheet filterBottomSheet = CurrentStoreFilterBottomSheet.newInstance(itemsSta,itemsbatchRef);
                filterBottomSheet.setListener(this);
                filterBottomSheet.show(getChildFragmentManager() ,
                                CurrentStoreFilterBottomSheet.class.getName());
                break;
            case R.id.btn_txt_request:
                openFormActivity("store_log_request_multi.json", "Request");
                break;
            case R.id.btn_txt_goods_in:
                openFormActivity("store_log_goods_in.json", "Goods In");
                break;
            case R.id.btn_txt_transfer:
                openFormActivity("store_log_transfer.json", "Transfer");
                break;
            case R.id.btn_txt_issue:
                HashMap<String, Object> map = adapter.getSelectedItems();
                if(map!= null && map.keySet().size() > 2) {
                    showErrorDialog("Validation Error" , "Issues can be raised at one item at a time.");
                    return;
                }
                openFormActivity("store_log_issue.json", "Issue");
                break;
            case R.id.btn_txt_goods_out:
                openFormActivity("store_log_goods_out.json", "Goods Out");
                break;
            case R.id.btn_txt_estimate:
                openFormActivity("store_log_estimate.json", "Estimate");
                break;
            case R.id.btn_img_search:
                openSearchDialog();
                break;
        }
    }

    private void openSearchDialog() {
        btnImageSearch.setVisibility(View.GONE);
        btnImageCancel.setVisibility(View.GONE);
        txtToolbarTitle.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        searchView.setIconified(false);
       /* PopupMenu popupMenu = new PopupMenu(getContext(), null , this);
        popupMenu.show(btnImageSearch);*/
    }

    private boolean validate(String title) {
        HashMap<String, Object> map = adapter.getSelectedItems();
        if (map.isEmpty()) {
            return false;
        }

        Set<String> keys = map.keySet();

        String staId = null;

        for (String key : keys) {
            if (!key.endsWith("_qty")) {
                Integer qty = (Integer) map.get(key + "_qty");
                if (qty == null || qty <= 0) {
                    showErrorDialog("Validation Error" , "Please enter quantity greater than 0");
                    return false;
                }

                MyStore store = (MyStore) map.get(key);
                if(store != null) {
                    if(!title.equalsIgnoreCase("Request") && qty > store.getquantity()){
                        return false;
                    }

                    if (staId != null && !staId.equalsIgnoreCase(store.getstaId())) {
                        showErrorDialog("Validation Error", "Different staID");
                        return false;
                    }
                    staId = store.getstaId();
                }
            }
        }

        return true;
    }

    private void openFormActivity(String jsonFileName, String title) {
        adapter.clearFocus();

        if (!validate(title)) {
            return;
        }

        HashMap<String, Object> map = adapter.getSelectedItems();

        Submission submission = new Submission(jsonFileName, title, "");
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, DBHandler.getInstance().getUser());
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        intent.putExtra(FormActivity.ARG_MY_STORE_ITEMS, map);
        startActivityForResult(intent, 1000);

    }


    private void onStandardRequest(String jsonFileName, String title) {
        Submission submission = new Submission(jsonFileName, title, "");
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        Intent intent = new Intent(context, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, DBHandler.getInstance().getUser());
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        startActivityForResult(intent, 1000);
    }


    @Override
    public void onMultiSelectionEnabled(boolean isEnabled) {
        if (isEnabled) {
            rlBottomPanel.setVisibility(View.GONE);
            actionScrollView.setVisibility(View.VISIBLE);
            txtSelectAll.setVisibility(View.VISIBLE);
            btnImageSearch.setVisibility(View.GONE);
            btnImageCancel.setVisibility(View.VISIBLE);
            txtToolbarTitle.setVisibility(View.VISIBLE);
            searchView.setVisibility(View.GONE);
        } else {
            rlBottomPanel.setVisibility(View.VISIBLE);
            actionScrollView.setVisibility(View.GONE);
            txtSelectAll.setVisibility(View.GONE);
            btnImageSearch.setVisibility(View.VISIBLE);
        }
    }

    private void getCurrentStoreList() {

        if (!CommonUtils.isNetworkAvailable(context)) {
            stores.clear();
            stores.addAll(DBHandler.getInstance().getMyStores());
            adapter.reset(stores);
            return;
        }

        listener.showProgressBar();
        APICalls.getMyStore(user.gettoken()).enqueue(new Callback<DataMyStores>() {


            @Override
            public void onResponse(@NonNull Call<DataMyStores> call,
                                   @NonNull Response<DataMyStores> response) {

                if(CommonUtils.onTokenExpired(context , response.code())){
                    return;
                }

                if (response.isSuccessful()) {
                    DataMyStores dataMyStores = response.body();
                    if (dataMyStores != null) {
                        dataMyStores.toContentValues();
                    }
                    stores.clear();
                    stores.addAll(DBHandler.getInstance().getMyStores());
                    adapter.reset(stores);
                    listener.hideProgressBar();
                }
            }

            @Override
            public void onFailure(@NonNull Call<DataMyStores> call, @NonNull Throwable t) {
                listener.hideProgressBar();
                stores.clear();
                stores.addAll(DBHandler.getInstance().getMyStores());
                adapter.reset(stores);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        adapter.cancelMultiSelect();
    }

    public void showErrorDialog(String title, String message) {

        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> dialog1.dismiss())
                .build();

        dialog.setCancelable(false);
        dialog.show(getChildFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public void onFilterBatchSelected(String batchId , String batchName) {
        adapter.filterByBatch(batchId);
        isFilterOn = true;
        txtFilterValue.setText(batchName);
        txtFilterBy.setVisibility(View.VISIBLE);
        txtFilterValue.setVisibility(View.VISIBLE);
        txtResetFilter.setVisibility(View.VISIBLE);
        btnFilter.setVisibility(View.GONE);
    }

    @Override
    public void onFilterStaSelected(String sta , String staName) {
        adapter.filterBySta(sta);
        isFilterOn = true;
        txtFilterValue.setText(staName);
        txtFilterBy.setVisibility(View.VISIBLE);
        txtFilterValue.setVisibility(View.VISIBLE);
        txtResetFilter.setVisibility(View.VISIBLE);
        btnFilter.setVisibility(View.GONE);
    }

    @Override
    public void onFilterFavSelected() {
        adapter.filterByFav();
        isFilterOn = true;
        txtFilterValue.setText(R.string.favourite);
        txtFilterBy.setVisibility(View.VISIBLE);
        txtFilterValue.setVisibility(View.VISIBLE);
        txtResetFilter.setVisibility(View.VISIBLE);
        btnFilter.setVisibility(View.GONE);
    }
}
