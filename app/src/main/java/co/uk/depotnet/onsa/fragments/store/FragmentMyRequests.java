package co.uk.depotnet.onsa.fragments.store;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.adapters.store.AdapterMyRequests;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.store.MyRequest;
import co.uk.depotnet.onsa.utils.VerticalSpaceItemDecoration;


public class FragmentMyRequests extends Fragment {

    private ArrayList<MyRequest> myRequests;
    private FragmentActionListener listener;
    private AdapterMyRequests adapter;
    private Context context;


    public FragmentMyRequests() {
    }


    public static FragmentMyRequests newInstance() {
        FragmentMyRequests fragment = new FragmentMyRequests();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
        }

        myRequests = DBHandler.getInstance().getMyRequest();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_receipts_list, container, false);

        TextView txtToolbarTitle=  view.findViewById(R.id.txt_toolbar_title);
        txtToolbarTitle.setText(R.string.my_requests);
        RecyclerView recyclerView = view.findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        adapter = new AdapterMyRequests(myRequests, listener, context);
        recyclerView.setAdapter(adapter);
        VerticalSpaceItemDecoration decoration = new VerticalSpaceItemDecoration(16);
        recyclerView.addItemDecoration(decoration);

        view.findViewById(R.id.btn_img_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((Activity)context).onBackPressed();
            }
        });

        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
        if(context instanceof FragmentActionListener){
            listener = (FragmentActionListener)context;
        }
    }


}
