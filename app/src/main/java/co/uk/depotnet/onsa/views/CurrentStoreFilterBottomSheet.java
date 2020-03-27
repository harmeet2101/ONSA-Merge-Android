package co.uk.depotnet.onsa.views;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.RelativeLayout;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.DropDownAdapter;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;


public class CurrentStoreFilterBottomSheet extends BottomSheetDialogFragment
        implements View.OnClickListener , OnItemClickListener<DropDownItem> {

    private DropDownAdapter dropDownAdapter;
    private ArrayList<DropDownItem> itemsSta;
    private ArrayList<DropDownItem> itemsBatchref;
    private FilterListener listener;
    private RecyclerView recyclerView;
    private RelativeLayout btnFavFilter;
    private RelativeLayout btnBatchFilter;
    private RelativeLayout btnStaFilter;

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
        }

        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {

        }
    };

    public void setListener(FilterListener listener) {
        this.listener = listener;
    }

    public static CurrentStoreFilterBottomSheet newInstance(ArrayList<DropDownItem> dropDownItems,ArrayList<DropDownItem> dropDownBatchRef){
        CurrentStoreFilterBottomSheet bottomSheet = new CurrentStoreFilterBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("dropDownStaItems", dropDownItems);
        bundle.putSerializable("dropDownBatchItems", dropDownBatchRef);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        itemsSta = (ArrayList<DropDownItem>) getArguments().getSerializable("dropDownStaItems");
        itemsBatchref = (ArrayList<DropDownItem>) getArguments().getSerializable("dropDownBatchItems");
        dropDownAdapter = new DropDownAdapter(getContext(), new ArrayList<>() , this);
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setCancelable(true);
        View rootview = LayoutInflater.from(getContext()).inflate(R.layout.filter_current_store, null);
        dialog.setContentView(rootview);
        rootview.findViewById(R.id.btn_back).setOnClickListener(this);

        recyclerView = rootview.findViewById(R.id.recycler_view);
        recyclerView.setVisibility(View.GONE);
        btnBatchFilter = rootview.findViewById(R.id.btn_filter_by_batch);
        btnStaFilter = rootview.findViewById(R.id.btn_filter_by_sta);
        btnFavFilter = rootview.findViewById(R.id.btn_filter_by_fav);

        btnBatchFilter.setOnClickListener(this);
        btnFavFilter.setOnClickListener(this);
        btnStaFilter.setOnClickListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL , false));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(
                getResources().getDimensionPixelSize(R.dimen._16sdp)));


        recyclerView.setAdapter(dropDownAdapter);


        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) rootview.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        View parent = (View) rootview.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        rootview.measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);

        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        Rect rectangle = new Rect();
        Window window = getActivity().getWindow();
        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        params.height = screenHeight + titleBarHeight;
        parent.setLayoutParams(params);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                if(recyclerView.getVisibility() == View.VISIBLE){
                    recyclerView.setVisibility(View.GONE);
                    btnBatchFilter.setVisibility(View.VISIBLE);
                    btnFavFilter.setVisibility(View.VISIBLE);
                    btnStaFilter.setVisibility(View.VISIBLE);
                }else {
                    dismiss();
                }
                break;
            case R.id.btn_filter_by_fav:
                dropDownAdapter.setFilterBatch(false);
                listener.onFilterFavSelected();
                dismiss();
                break;
            case R.id.btn_filter_by_batch:
                dropDownAdapter.updateData(itemsBatchref);
                dropDownAdapter.setFilterBatch(true);
                recyclerView.setVisibility(View.VISIBLE);
                btnBatchFilter.setVisibility(View.GONE);
                btnFavFilter.setVisibility(View.GONE);
                btnStaFilter.setVisibility(View.GONE);
                break;
            case R.id.btn_filter_by_sta:
                dropDownAdapter.updateData(itemsSta);
                dropDownAdapter.setFilterBatch(false);
                recyclerView.setVisibility(View.VISIBLE);
                btnBatchFilter.setVisibility(View.GONE);
                btnFavFilter.setVisibility(View.GONE);
                btnStaFilter.setVisibility(View.GONE);
                break;
        }
    }

    public interface FilterListener {
        void onFilterBatchSelected(String batchId , String batchName);
        void onFilterStaSelected(String sta , String staName);
        void onFilterFavSelected();

    }

    @Override
    public void onItemClick(DropDownItem dropDownItem, int position) {
        if(dropDownAdapter.isFilterBatch()) {
            listener.onFilterBatchSelected(dropDownItem.getUploadValue(), "batch: "+dropDownItem.getDisplayItem());
        }else{
            listener.onFilterStaSelected(dropDownItem.getUploadValue(), "STAs: "+dropDownItem.getDisplayItem());
        }
        dismiss();
    }
}
