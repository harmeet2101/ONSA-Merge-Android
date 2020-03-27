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
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import java.io.Serializable;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.ColorAdapter;
import co.uk.depotnet.onsa.listeners.ColorListener;
import co.uk.depotnet.onsa.listeners.RecyclerItemClickListener;
import co.uk.depotnet.onsa.modals.Color;


public class FilterBottomSheet extends BottomSheetDialogFragment implements View.OnClickListener {

   // private RangeSeekBar<Integer> priceSeekBar;
    private TextView txtMinPrice;
    private TextView txtMaxPrice;
    private ColorAdapter colorAdapter;
    private List<Color> colors;
    private int minPrice = 0;
    private int maxPrice = 100;
    private FilterListener listener;
    private ColorListener colorListener;
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

    public static FilterBottomSheet newInstance(ArrayList<Color> colors, ColorListener colorListener){
        FilterBottomSheet bottomSheet = new FilterBottomSheet();
        Bundle bundle = new Bundle();
        bundle.putSerializable("colors", colors);
        bundle.putSerializable("Listener", (Serializable) colorListener);
        bottomSheet.setArguments(bundle);
        return bottomSheet;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        colors = (ArrayList<Color>) getArguments().getSerializable("colors");
        colorListener= (ColorListener) getArguments().getSerializable("Listener");
    }

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(final Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        dialog.setCancelable(true);
        View rootview = LayoutInflater.from(getContext()).inflate(R.layout.filter_screen, null);
        dialog.setContentView(rootview);
        rootview.findViewById(R.id.btn_back).setOnClickListener(this);
        rootview.findViewById(R.id.btn_clear).setOnClickListener(this);
        rootview.findViewById(R.id.btn_apply).setOnClickListener(this);
        RecyclerView colorRecyclerView = rootview.findViewById(R.id.color_recycler_view);

        colorRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 4));
        colorRecyclerView.setHasFixedSize(true);
        colorRecyclerView.addItemDecoration(new ItemDecorationAlbumColumns(
                getResources().getDimensionPixelSize(R.dimen._16sdp), 4));
        colorAdapter = new ColorAdapter(getContext(), colors);


        colorRecyclerView.setAdapter(colorAdapter);

        colorRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String colorCode=colors.get(position).getRgb_code();
                colorListener.colorSelected(colorCode);
                dismiss();
            }
        }));


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
       // params.height = screenHeight + titleBarHeight;
        params.height = 700 + titleBarHeight;
        parent.setLayoutParams(params);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_back:
                dismiss();
                break;
            case R.id.btn_clear:

               // priceSeekBar.resetSelectedValues();
             //   rangeSeekbar.resetPivot();
                NumberFormat format = NumberFormat.getInstance(Locale.US);
                if (colors != null && !colors.isEmpty()) {
                    for (Color Color : colors) {
                        Color.setSelected(false);
                    }
                    colorAdapter.notifyDataSetChanged();
                }
                break;
            case R.id.btn_apply:
                if(listener != null) {
                    List<Integer> selectedColors = new ArrayList<>();
                    for(int i = 0 ; i < colors.size() ; i++){
                        if(colors.get(i).isSelected()) {
                            selectedColors.add(colors.get(i).getId());
                        }
                    }

                }
                dismiss();
                break;
        }
    }

    public interface FilterListener {
        void onFilterApplied(List<Integer> selectedColors);
    }
}
