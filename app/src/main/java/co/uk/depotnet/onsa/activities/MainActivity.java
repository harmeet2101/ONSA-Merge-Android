package co.uk.depotnet.onsa.activities;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.tonyodev.fetch2.Fetch;

import java.util.ArrayList;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.FragmentHome;
import co.uk.depotnet.onsa.fragments.FragmentKitBag;
import co.uk.depotnet.onsa.fragments.FragmentQueue;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.listeners.GetFetchListener;
import co.uk.depotnet.onsa.listeners.HomeBottomBarListener;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.networking.NetworkStateReceiver;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import co.uk.depotnet.onsa.views.PopupMenu;

public class MainActivity extends AppCompatActivity
        implements HomeBottomBarListener,
        FragmentActionListener, View.OnClickListener,
        PopupMenu.OnSearchListener , NetworkStateReceiver.NetworkStateReceiverListener , GetFetchListener {

    private static final int STORAGE_PERMISSION_CODE = 1;
    private ProgressBar progressBar;

    private HomeBottomBarHandler bottomBarHandler;
    private NetworkStateReceiver networkStateReceiver;
    private Fetch fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);

        progressBar = findViewById(R.id.progress_bar);

        bottomBarHandler = new HomeBottomBarHandler(this, findViewById(R.id.bottom_bar), this);
        Fragment fragment;
        fragment = FragmentHome.newInstance();
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
    public void setTitle(String title) {
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
        checkStoragePermission();
    }

    private void openKitBag() {
        FragmentKitBag fragmentKitBag = FragmentKitBag.newInstance(0);
        addFragment(fragmentKitBag, false);
    }

    @Override
    public void openKitbagFolder(int parentId) {
        FragmentKitBag fragmentKitBag = FragmentKitBag.newInstance(parentId);
        addFragment(fragmentKitBag, false);
    }

    @Override
    public void onOfflineQueueClick() {
        FragmentQueue fragmentQueue = FragmentQueue.newInstance(false);
        addFragment(fragmentQueue, true);
    }


    @Override
    public void onSearch(String keyword) {
        FragmentManager fm = getSupportFragmentManager();

        FragmentHome fragmentHome = (FragmentHome) (fm.findFragmentByTag(FragmentHome.class.getName()));
        if (fragmentHome != null && fragmentHome.isVisible()) {
            fragmentHome.search(keyword);
        }

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_cancel_search){
            closeSearch();
        }else if(view.getId() == R.id.btn_img_settings){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        }else if(view.getId() == R.id.btn_img_cancel){
            onBackPressed();
        }
    }

    private void closeSearch() {
        FragmentManager fm = getSupportFragmentManager();

        FragmentHome fragmentHome = (FragmentHome) (fm.findFragmentByTag(FragmentHome.class.getName()));
        if (fragmentHome != null && fragmentHome.isVisible()) {
            fragmentHome.clearSearch();
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
                    dialog1.dismiss();
                    FragmentManager fragmentManager = getSupportFragmentManager();
                    Fragment fragment = fragmentManager.findFragmentByTag(FragmentQueue.class.getName());

                    if (fragment != null && fragment.isVisible() ) {
                        return;
                    }
                    FragmentQueue fragmentQueue = FragmentQueue.newInstance(true);
                    addFragment(fragmentQueue, true);
                }).setNegative(getString(R.string.generic_cancel), (dialog12, which) -> {
                    dialog12.dismiss();
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }

    @Override
    public Fetch getFetch() {
        if(fetch == null || fetch.isClosed()){
            fetch = Fetch.Impl.getDefaultInstance();
        }
        return fetch;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == 1000){
            finish();
        }
    }
}
