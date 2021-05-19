package co.uk.depotnet.onsa.fragments.store;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;


import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.JWTErrorDialog;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.views.DropdownNumberBottomSheet;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreDetailActivity extends AppCompatActivity implements View.OnClickListener {


    private static final String ARG_BARCODE_RESULT = "barcode_result";
    private static final String ARG_STAID = "StaId";

    private String barcode;
    private String staId;

    private StockItems store;

    private TextView txtToolbarTitle;
    private TextView txtAltName;
    private TextView txtDesc;
    private TextView imgBarcode;
    private TextView txtMapId;
    private TextView txtSupplierName;
    private TextView txtUnit;
    private TextView txtUnitType;
    private TextView txtWarehouse;
    private TextView txtDepartName;
    private TextView txtStockLevel;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_store_detail);
        barcode = getIntent().getStringExtra(ARG_BARCODE_RESULT);
        staId = getIntent().getStringExtra(ARG_STAID);
        txtToolbarTitle = findViewById(R.id.txt_toolbar_title);
        txtAltName = findViewById(R.id.txt_alt_name);
        txtDesc = findViewById(R.id.txt_desc);
        imgBarcode = findViewById(R.id.txt_barcode);
        txtMapId = findViewById(R.id.txt_map_id);
        txtSupplierName = findViewById(R.id.txt_supplier);
        txtUnit = findViewById(R.id.txt_unit);
        txtUnitType = findViewById(R.id.txt_unit_type);
        txtWarehouse = findViewById(R.id.txt_ware_house);
        txtDepartName = findViewById(R.id.txt_department);
        txtStockLevel = findViewById(R.id.txt_stock_level);
        CircleImageView imgStore = findViewById(R.id.img_store);


        findViewById(R.id.btn_accept).setOnClickListener(this);
        findViewById(R.id.btn_reject).setOnClickListener(this);
        findViewById(R.id.btn_img_cancel).setOnClickListener(this);

        getItems();
    }

    private void setDataInView(){
        if(store == null){
            return;
        }

        txtToolbarTitle.setText(store.getaltName());
        txtAltName.setText(store.getaltName());
        txtDesc.setText(store.getdescription());
        imgBarcode.setText(store.getbarcode());
        txtMapId.setText(String.valueOf(store.getmapID()));
        txtSupplierName.setText(store.getsupplierName());
        txtDepartName.setText(store.getdepartmentName());
        txtUnit.setText(String.valueOf(store.getunit()));
        txtUnitType.setText(store.getunitName());
        txtWarehouse.setText(store.getWarehouseStaName());
        txtStockLevel.setText(String.valueOf(store.getStockLevelUnit()));
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_accept:
                DBHandler.getInstance().replaceData(StockItems.DBTable.NAME , store.toContentValues());
                showNumber();
                break;
            case R.id.btn_reject:
            case R.id.btn_img_cancel:
                finish();
                break;
        }
    }





    private void showNumber(){
        final DropdownNumberBottomSheet dropdownMenu =
                new DropdownNumberBottomSheet(this, number -> {
                    if(TextUtils.isEmpty(number)){
                        return;
                    }

                    double value = 0;

                    try{
                        value = Double.parseDouble(number);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(value == 0){
                        return;
                    }

                    if(value > store.getStockLevelUnit()){
                        return;
                    }


                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("stock_quantity", value);
                    resultIntent.putExtra("StaId", getIntent().getStringExtra("StaId"));
                    resultIntent.putExtra("stockItemId", store.getstockItemId());
                    setResult(RESULT_OK, resultIntent);
                    finish();

                });
        dropdownMenu.show();

    }

    private void getItems(){
        if(!CommonUtils.validateToken(StoreDetailActivity.this)){
            return;
        }

        User user = DBHandler.getInstance().getUser();
        CallUtils.enqueueWithRetry(APICalls.getItem(user.gettoken() , barcode , staId),new Callback<StockItems>() {
            @Override
            public void onResponse(@NonNull Call<StockItems> call, @NonNull Response<StockItems> response) {

                if(CommonUtils.onTokenExpired(StoreDetailActivity.this , response.code())){
                    return;
                }
                if(response.isSuccessful()){
                    store = response.body();


                    if(store == null || TextUtils.isEmpty(store.getstockItemId()) || store.getstockItemId().startsWith("0000")){
                        Toast.makeText(StoreDetailActivity.this , "Scanned Stock Item not found" , Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    setDataInView();
                }
            }

            @Override
            public void onFailure(@NonNull Call<StockItems> call, @NonNull Throwable t) {

            }
        });
    }

}
