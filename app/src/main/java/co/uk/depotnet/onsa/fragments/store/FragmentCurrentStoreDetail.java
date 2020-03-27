package co.uk.depotnet.onsa.fragments.store;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import com.bumptech.glide.Glide;

import java.util.HashMap;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.MyStoreFav;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.views.DropdownNumberBottomSheet;
import de.hdodenhof.circleimageview.CircleImageView;

public class FragmentCurrentStoreDetail extends Fragment implements View.OnClickListener {


    private static final String ARG_CURRENT_STORE = "CurrentStore";
    private Context context;
    private FragmentActionListener listener;


    private ImageView imgFav;
    private MyStore store;

    public FragmentCurrentStoreDetail() {

    }


    public static FragmentCurrentStoreDetail newInstance(MyStore myStore) {
        FragmentCurrentStoreDetail fragment = new FragmentCurrentStoreDetail();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        args.putParcelable(ARG_CURRENT_STORE , myStore);
        return fragment;
    }

    @Override
    public void onAttach(Context context) {
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
            store = args.getParcelable(ARG_CURRENT_STORE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_current_store_detail, container, false);

        TextView txtToolbarTitle = view.findViewById(R.id.txt_toolbar_title);
        TextView txtAltName = view.findViewById(R.id.txt_alt_name);
        TextView txtDesc = view.findViewById(R.id.txt_desc);
        TextView imgBarcode = view.findViewById(R.id.txt_barcode);
        TextView txtMapId = view.findViewById(R.id.txt_map_id);
        TextView txtPackaging = view.findViewById(R.id.txt_packaging);
        TextView txtUnit = view.findViewById(R.id.txt_unit);
        TextView txtUnitType = view.findViewById(R.id.txt_unit_type);
        TextView txtWarehouse = view.findViewById(R.id.txt_ware_house);
        TextView txtSerialise = view.findViewById(R.id.txt_serialised);
        TextView txtQuantity = view.findViewById(R.id.txt_quantity);
        CircleImageView imgStore = view.findViewById(R.id.img_store);
        imgFav = view.findViewById(R.id.img_fav);

        boolean isFavorite = DBHandler.getInstance().isMyStoreFav(store.getstockItemId());
        imgFav.setSelected(isFavorite);
//        view.findViewById(R.id.ll_btn_fav).setOnClickListener(this);
//        view.findViewById(R.id.ll_btn_request).setOnClickListener(this);
//        view.findViewById(R.id.ll_btn_transfer).setOnClickListener(this);
        view.findViewById(R.id.btn_img_cancel).setOnClickListener(this);

        txtToolbarTitle.setText(store.getaltName());
        txtAltName.setText(store.getaltName());
        txtDesc.setText(store.getdescription());
        imgBarcode.setText(store.getbarcode());
        txtMapId.setText(String.valueOf(store.getmapID()));
        txtPackaging.setText(store.getpackagingName());
        txtUnit.setText(String.valueOf(store.getunit()));
        txtUnitType.setText(store.getunitName());
        txtWarehouse.setText(store.getwarehouseStaName());
        txtSerialise.setText(String.valueOf(store.isserialised()));
        txtQuantity.setText(String.valueOf(store.getquantity()));
        if(!TextUtils.isEmpty(store.getphotoPath())) {
            String imgUrl = BuildConfig.BASE_URL + store.getphotoPath().substring(1);
            Glide.with(context).load(imgUrl).into(imgStore);
        }

        return view;
    }





    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.ll_btn_fav:
                boolean isFavorite = DBHandler.getInstance().isMyStoreFav(store.getstockItemId());
                if(isFavorite) {
                    DBHandler.getInstance().deleteMyStoreFav(store.getstockItemId());
                    imgFav.setSelected(false);
                }else{
                    DBHandler.getInstance().insertData(MyStoreFav.DBTable.NAME , new MyStoreFav(store.getstockItemId() , true).toContentValues());
                    imgFav.setSelected(true);
                }
                break;
            case R.id.ll_btn_request:
                openFormActivity("store_log_request_multi.json", "Request");
                break;
            case R.id.ll_btn_transfer:
                openFormActivity("store_log_transfer.json", "Transfer");
                break;
            case R.id.btn_img_cancel:
                ((Activity)context).onBackPressed();
                break;

        }
    }

    private void openFormActivity(String jsonFileName, String title) {




        Submission submission = new Submission(jsonFileName, title, "");
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);

        showNumber(submission);

    }

    private void showNumber(final Submission submission){
        final DropdownNumberBottomSheet dropdownMenu = new DropdownNumberBottomSheet(context, new DropdownNumberBottomSheet.OnNumberSelected() {

            @Override
            public void numberSelected(String number) {
                if(TextUtils.isEmpty(number)){
                    return;
                }
                int value = 0;

                try{
                    value = Integer.parseInt(number);
                }catch (Exception e){

                }

                if(value == 0){
                    return;
                }
                HashMap<String, Object> map = new HashMap<>();
                map.put(store.getstockItemId()+"_qty" , value);
                map.put(store.getstockItemId() , store);


                Intent intent = new Intent(context, FormActivity.class);
                intent.putExtra(FormActivity.ARG_USER, DBHandler.getInstance().getUser());
                intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
                intent.putExtra(FormActivity.ARG_MY_STORE_ITEMS, map);
                startActivityForResult(intent, 1000);

            }
        });
        dropdownMenu.show();

    }

}
