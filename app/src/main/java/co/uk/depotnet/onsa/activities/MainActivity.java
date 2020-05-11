package co.uk.depotnet.onsa.activities;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.tonyodev.fetch2.DefaultFetchNotificationManager;
import com.tonyodev.fetch2.Error;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2.Request;
import com.tonyodev.fetch2core.Downloader;
import com.tonyodev.fetch2core.Func;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.FragmentHome;
import co.uk.depotnet.onsa.fragments.FragmentKitBag;
import co.uk.depotnet.onsa.fragments.FragmentQueue;
import co.uk.depotnet.onsa.fragments.store.FragmentStore;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.listeners.HomeBottomBarListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.networking.NetworkStateReceiver;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import co.uk.depotnet.onsa.views.PopupMenu;

public class MainActivity extends AppCompatActivity
        implements HomeBottomBarListener,
        FragmentActionListener, View.OnClickListener,
        PopupMenu.OnSearchListener , NetworkStateReceiver.NetworkStateReceiverListener {

    private static final int STORAGE_PERMISSION_CODE = 1;
    private ProgressBar progressBar;
    private TextView txtToolbarTitle;
    private ImageView btnImageSearch;
    private ImageView btnImageSettings;
    private ImageView btnImageCancel;
    private TextView btnSearchCancel;
    private User user;
    private Fetch fetch;
    private boolean isSearchEnable;
    private HomeBottomBarHandler bottomBarHandler;
    private NetworkStateReceiver networkStateReceiver;

    public Fetch getFetch() {
        return fetch;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);

        user = getIntent().getParcelableExtra("User");
        progressBar = findViewById(R.id.progress_bar);
        txtToolbarTitle = findViewById(R.id.txt_toolbar_title);
        btnImageSearch = findViewById(R.id.btn_img_search);

        btnImageSearch.setOnClickListener(this);
        btnImageSettings = findViewById(R.id.btn_img_settings);
        btnImageSettings.setOnClickListener(this);
        btnImageCancel = findViewById(R.id.btn_img_cancel);
        btnImageCancel.setOnClickListener(this);
        btnImageCancel.setVisibility(View.GONE);
        btnSearchCancel = findViewById(R.id.btn_cancel_search);
        btnSearchCancel.setOnClickListener(this);
        btnSearchCancel.setVisibility(View.GONE);
        btnImageSettings.setVisibility(View.VISIBLE);

        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(4)
                .setHttpDownloader(new OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
                .setNamespace("OptinonsDownloader")
                .setNotificationManager(new DefaultFetchNotificationManager(this))
                .build();
//        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch = Fetch.Impl.getDefaultInstance();


        bottomBarHandler = new HomeBottomBarHandler(this, findViewById(R.id.bottom_bar), this);
        Fragment fragment;
        fragment = FragmentHome.newInstance(user);
        addFragment(fragment, false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(networkStateReceiver , new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void addFragment(Fragment fragment, boolean isHorizontalAnim) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!(fragment instanceof FragmentHome)) {
            if (!isHorizontalAnim) {
                transaction.setCustomAnimations(R.anim.fragment_open_up,
                        R.anim.fragment_close_down, R.anim.fragment_open_up,
                        R.anim.fragment_close_down);
            } else {
                transaction.setCustomAnimations(R.anim.fragment_open_from_left,
                        R.anim.fragment_close_to_right, R.anim.fragment_open_from_right,
                        R.anim.fragment_close_to_left);
            }
        }
        transaction.replace(R.id.container, fragment, fragment.getClass().getName());
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_CODE);
            }
        } else {
            openKitBag();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case STORAGE_PERMISSION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openKitBag();
                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void setReceiptsBadge(String number) {
        bottomBarHandler.setNotificationText(number);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }

        bottomBarHandler.onBackPress();
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        fetch.close();
    }

    @Override
    public void setTitle(String title) {
        if (txtToolbarTitle != null) {
            txtToolbarTitle.setText(title);
        }
    }

    @Override
    public void onFragmentHomeVisible(boolean isVisible) {
        if (isVisible) {
            btnImageCancel.setVisibility(View.GONE);
            btnImageSearch.setVisibility(View.VISIBLE);
            btnImageSettings.setVisibility(View.VISIBLE);

            if (isSearchEnable) {
                btnSearchCancel.setVisibility(View.VISIBLE);
                btnImageSettings.setVisibility(View.GONE);
            } else {
                btnImageSettings.setVisibility(View.VISIBLE);
                btnSearchCancel.setVisibility(View.GONE);
            }
        } else {
            btnImageCancel.setVisibility(View.VISIBLE);
            btnImageSearch.setVisibility(View.GONE);
            btnImageSettings.setVisibility(View.GONE);


        }
    }


    @Override
    public void onMyWorkClick() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
        }
    }

    @Override
    public void onKitBagClick() {
        btnSearchCancel.setVisibility(View.GONE);
        btnImageSettings.setVisibility(View.VISIBLE);
        checkStoragePermission();


    }

    private void openKitBag() {
        FragmentKitBag fragmentKitBag = FragmentKitBag.newInstance();
        addFragment(fragmentKitBag, false);
    }


    public void showErrorDialog(String title, String message) {

        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int i) {
                        dialog.dismiss();
                    }
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public void onOnsaStoreClick() {
        btnSearchCancel.setVisibility(View.GONE);
        btnImageSettings.setVisibility(View.GONE);
        FragmentStore fragmentStore = FragmentStore.newInstance(user);
        addFragment(fragmentStore, false);
    }

    private void openVehicleCheck() {
        String jsonFileName = "vehicle_check.json";

        Submission submission = new Submission(jsonFileName, "", "");
        long submissionID = DBHandler.getInstance().insertData(Submission.DBTable.NAME, submission.toContentValues());
        submission.setId(submissionID);
        Intent intent = new Intent(this, FormActivity.class);
        intent.putExtra(FormActivity.ARG_USER, user);
        intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
        hideProgressBar();
        startActivity(intent);
    }

    @Override
    public void onOfflineQueueClick() {
        btnSearchCancel.setVisibility(View.GONE);
        btnImageSettings.setVisibility(View.VISIBLE);
        FragmentQueue fragmentQueue = FragmentQueue.newInstance(false);
        addFragment(fragmentQueue, true);
    }


    private void openSearchDialog() {
        PopupMenu popupMenu = new PopupMenu(this, null, this);
        popupMenu.show(btnImageSearch);
    }

    @Override
    public void onSearch(String keyword) {
        FragmentManager fm = getSupportFragmentManager();


        FragmentHome fragmentHome = (FragmentHome) (fm.findFragmentByTag(FragmentHome.class.getName()));
        if (fragmentHome != null && fragmentHome.isVisible()) {
            fragmentHome.search(keyword);
            isSearchEnable = true;
            btnImageSettings.setVisibility(View.GONE);
            btnSearchCancel.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_cancel_search:
                closeSearch();
                break;
            case R.id.btn_img_search:
                openSearchDialog();
                break;
            case R.id.btn_img_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                intent.putExtra("User", user);
                startActivity(intent);
                break;
            case R.id.btn_img_cancel:
                onBackPressed();
        }
    }

    private void closeSearch() {
        FragmentManager fm = getSupportFragmentManager();


        FragmentHome fragmentHome = (FragmentHome) (fm.findFragmentByTag(FragmentHome.class.getName()));
        if (fragmentHome != null && fragmentHome.isVisible()) {
            fragmentHome.clearSearch();
            isSearchEnable = false;
            btnImageSettings.setVisibility(View.VISIBLE);
            btnSearchCancel.setVisibility(View.GONE);
        }

    }

    private boolean isNetworkDialogVisible;

    @Override
    public void networkAvailable() {
        ArrayList<Submission> submissions = DBHandler.getInstance().getQueuedSubmissions();
        if(submissions.isEmpty()){
            return;
        }

        if(!isNetworkDialogVisible) {
            showNetworkDialog("Network Available", "Do you want to upload Offline queue");
        }
    }

    @Override
    public void networkUnavailable() {

    }

    public void showNetworkDialog(String title, String message) {

        isNetworkDialogVisible = true;
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {
                    hideProgressBar();
//                    isNetworkDialogVisible = false;
                    dialog1.dismiss();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentByTag(FragmentQueue.class.getName());

                    if (fragment != null && fragment.isVisible() ) {
                        return;
                    }
                    btnSearchCancel.setVisibility(View.GONE);
                    btnImageSettings.setVisibility(View.VISIBLE);
                    FragmentQueue fragmentQueue = FragmentQueue.newInstance(true);
                    addFragment(fragmentQueue, true);
                }).setNegative(getString(R.string.generic_cancel), (dialog12, which) -> {
//                    isNetworkDialogVisible = false;
                    dialog12.dismiss();
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }
}
