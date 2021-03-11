package co.uk.depotnet.onsa.views;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.DropDownAdapter;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.OnItemClickListener;

import java.util.ArrayList;

public class DropdownMenu extends BottomSheetDialogFragment implements OnItemClickListener<DropDownItem> {

    public static final String ARG_ITEMS = "items";


    private Context context;
    private DropDownAdapter dropdownAdapter;
    private ArrayList<DropDownItem> items;
    private DropDownAdapter.OnItemSelectedListener listener;

    private final BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {
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



    public static DropdownMenu newInstance(ArrayList<DropDownItem> items){
        DropdownMenu bottomSheet = new DropdownMenu();
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(ARG_ITEMS, items);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
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
        if(args != null){
            items = args.getParcelableArrayList(ARG_ITEMS);
        }

        dropdownAdapter = new DropDownAdapter(context , items , this);
    }


    @Override
    public void setupDialog(@NonNull final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setCancelable(true);


        View view = LayoutInflater.from(context).inflate(R.layout.popup_dropdown, null);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(dropdownAdapter);
        dialog.setContentView(view);

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) ((View) view.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();

        if (behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        View parent = (View) view.getParent();
        parent.setFitsSystemWindows(true);
        BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(parent);
        view.measure(0, 0);
        DisplayMetrics displaymetrics = new DisplayMetrics();
        Window window = ((Activity)context).getWindow();
        ((Activity)context).getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int screenHeight = displaymetrics.heightPixels;
        bottomSheetBehavior.setPeekHeight(screenHeight);

        if (params.getBehavior() instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) params.getBehavior()).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }

        Rect rectangle = new Rect();

        window.getDecorView().getWindowVisibleDisplayFrame(rectangle);
        int statusBarHeight = rectangle.top;
        int contentViewTop =
                window.findViewById(Window.ID_ANDROID_CONTENT).getTop();
        int titleBarHeight = contentViewTop - statusBarHeight;
        params.height = screenHeight + titleBarHeight;
//        params.height = 700 + titleBarHeight;
        parent.setLayoutParams(params);


    }

    public void setListener(DropDownAdapter.OnItemSelectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void onItemClick( DropDownItem o ,int position) {
        dismiss();
        listener.onItemSelected(position);
    }
}