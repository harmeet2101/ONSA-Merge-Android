package co.uk.depotnet.onsa.activities;

import android.content.Context;
import android.content.res.ColorStateList;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ImageViewCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.listeners.HomeBottomBarListener;
import co.uk.depotnet.onsa.networking.Constants;

public class HomeBottomBarHandler implements View.OnClickListener {

    private Context context;
    private HomeBottomBarListener listener;

    private LinearLayout llBtnMyWork;
    private LinearLayout llBtnKitBag;
    private LinearLayout llBtnBookOff;
    private LinearLayout llBtnOnsaStore;
    private LinearLayout llBtnOfflineCheck;


    private ImageView imgMyWork;
    private ImageView imgKitBag;
    private ImageView imgBookOff;
    private ImageView imgOnsaStore;
    private ImageView imgOfflineQueue;

    private TextView txtMyWork;
    private TextView txtKitBag;
    private TextView txtBookOff;
    private TextView txtOnsaStore;
    private TextView txtOfflineQueue;

    private LinearLayout llLastSelected;
    private ImageView imgLastSelected;
    private TextView txtLastSelected;

    private TextView txtStoreNotification;

    private int colorUnselected;
    private int colorSelected;

    public HomeBottomBarHandler(Context context, View view,
                                HomeBottomBarListener listener) {
        this.context = context;
        this.listener = listener;

        colorSelected = ContextCompat.getColor(context,
                R.color.colorPrimary);

        colorUnselected = ContextCompat.getColor(context,
                R.color.txt_color_light_grey);

        llBtnMyWork = view.findViewById(R.id.ll_btn_my_work);
        llBtnKitBag = view.findViewById(R.id.ll_btn_kit_bag);
        llBtnBookOff = view.findViewById(R.id.ll_btn_book_off);
        llBtnOnsaStore = view.findViewById(R.id.ll_btn_onsa_store);
        llBtnOfflineCheck = view.findViewById(R.id.ll_btn_offline_queue);

//        llBtnOnsaStore.setVisibility(View.GONE);

        llBtnMyWork.setOnClickListener(this);
        llBtnKitBag.setOnClickListener(this);
        llBtnBookOff.setOnClickListener(this);
        llBtnOnsaStore.setOnClickListener(this);
        llBtnOfflineCheck.setOnClickListener(this);

        imgMyWork = view.findViewById(R.id.img_my_work);
        imgKitBag = view.findViewById(R.id.img_kit_bag);
        imgBookOff = view.findViewById(R.id.img_book_off);
        imgOnsaStore = view.findViewById(R.id.img_vehicle_check);
        imgOfflineQueue = view.findViewById(R.id.img_offline_queue);
        txtMyWork = view.findViewById(R.id.txt_my_work);
        txtBookOff = view.findViewById(R.id.txt_book_off);
        txtKitBag = view.findViewById(R.id.txt_kit_bag);
        txtOnsaStore = view.findViewById(R.id.txt_onsa_store);
        txtOfflineQueue = view.findViewById(R.id.txt_offline_queue);
        txtStoreNotification = view.findViewById(R.id.txt_store_notification);

        llLastSelected = llBtnMyWork;
        imgLastSelected = imgMyWork;
        txtLastSelected = txtMyWork;
        ImageViewCompat.setImageTintList(imgLastSelected,
                ColorStateList.valueOf(colorSelected));
        txtLastSelected.setTextColor(colorSelected);

        if(Constants.isStoreEnabled){
            llBtnOnsaStore.setVisibility(View.VISIBLE);
        }else{
            llBtnOnsaStore.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {

        if(llLastSelected.getId() == view.getId()){
            return;
        }

        ImageViewCompat.setImageTintList(imgLastSelected,
                ColorStateList.valueOf(colorUnselected));
        txtLastSelected.setTextColor(colorUnselected);

        switch (view.getId()) {
            case R.id.ll_btn_my_work:
                llLastSelected = llBtnMyWork;
                imgLastSelected = imgMyWork;
                txtLastSelected = txtMyWork;
                ImageViewCompat.setImageTintList(imgLastSelected,
                        ColorStateList.valueOf(colorSelected));
                txtLastSelected.setTextColor(colorSelected);
                listener.onMyWorkClick();
                break;
            case R.id.ll_btn_book_off:
                llLastSelected = llBtnBookOff;
                imgLastSelected = imgBookOff;
                txtLastSelected = txtBookOff;
                ImageViewCompat.setImageTintList(imgLastSelected,
                        ColorStateList.valueOf(colorSelected));
                txtLastSelected.setTextColor(colorSelected);
                listener.onBookOff();
                break;
            case R.id.ll_btn_kit_bag:
                llLastSelected = llBtnKitBag;
                imgLastSelected = imgKitBag;
                txtLastSelected = txtKitBag;
                ImageViewCompat.setImageTintList(imgLastSelected,
                        ColorStateList.valueOf(colorSelected));
                txtLastSelected.setTextColor(colorSelected);
                listener.onKitBagClick();
                break;
            case R.id.ll_btn_onsa_store:
                llLastSelected = llBtnOnsaStore;
                imgLastSelected = imgOnsaStore;
                txtLastSelected = txtOnsaStore;
                ImageViewCompat.setImageTintList(imgLastSelected,
                        ColorStateList.valueOf(colorSelected));
                txtLastSelected.setTextColor(colorSelected);

                listener.onOnsaStoreClick();
                break;
            case R.id.ll_btn_offline_queue:
                llLastSelected = llBtnOfflineCheck;
                imgLastSelected = imgOfflineQueue;
                txtLastSelected = txtOfflineQueue;

                ImageViewCompat.setImageTintList(imgLastSelected,
                        ColorStateList.valueOf(colorSelected));
                txtLastSelected.setTextColor(colorSelected);
                listener.onOfflineQueueClick();
                break;
        }
    }

    public void onBackPress(){
        if(llLastSelected.getId() == R.id.ll_btn_my_work){
            return;
        }

        ImageViewCompat.setImageTintList(imgLastSelected,
                ColorStateList.valueOf(colorUnselected));
        txtLastSelected.setTextColor(colorUnselected);

        llLastSelected = llBtnMyWork;
        imgLastSelected = imgMyWork;
        txtLastSelected = txtMyWork;
        ImageViewCompat.setImageTintList(imgLastSelected,
                ColorStateList.valueOf(colorSelected));
        txtLastSelected.setTextColor(colorSelected);
    }

    public void setNotificationText(String text){
        txtStoreNotification.setText(text);
    }
}
