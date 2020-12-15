package co.uk.depotnet.onsa.fragments.store;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.store.AdapterRequestItems;
import co.uk.depotnet.onsa.modals.User;

import co.uk.depotnet.onsa.modals.store.MyRequest;
import co.uk.depotnet.onsa.modals.store.RequestItem;

import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;



public class RequestItemsFragment extends Fragment {

    private static final String ARG_REQUEST = "Request";
    private ArrayList<RequestItem> requestItems;
    private AdapterRequestItems adapterRequestItems;
    private Context context;


    public RequestItemsFragment() {
    }


    public static RequestItemsFragment newInstance(MyRequest request) {
        RequestItemsFragment fragment = new RequestItemsFragment();
        Bundle args = new Bundle();
        args.putParcelable(ARG_REQUEST , request);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        if (args != null) {
            MyRequest request = args.getParcelable(ARG_REQUEST);
            if(request != null) {
                requestItems = request.getItems();
            }
        }

        if(requestItems == null){
            requestItems = new ArrayList<>();
        }
        adapterRequestItems = new AdapterRequestItems(requestItems);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipt_items_list, container, false);
        TextView txtToolbarTitle=  view.findViewById(R.id.txt_toolbar_title);
        txtToolbarTitle.setText(R.string.my_requests);
        LinearLayout linearLayout = view.findViewById(R.id.ll_bottom);
        linearLayout.setVisibility(View.GONE);
        view.findViewById(R.id.btn_img_cancel).setOnClickListener(v -> ((Activity)context).onBackPressed());
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        recyclerView.setAdapter(adapterRequestItems);
        VerticalSpaceItemDecoration decoration = new VerticalSpaceItemDecoration(16);
        recyclerView.addItemDecoration(decoration);

        return view;
    }


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }
}
