package co.uk.depotnet.onsa.activities;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.tonyodev.fetch2.Fetch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.depotnet.onsa.fragments.FragmentKitBag;
import co.uk.depotnet.onsa.fragments.FragmentQueue;
import co.uk.depotnet.onsa.listeners.GetFetchListener;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.fcm.NotifyUtils;
import co.uk.depotnet.onsa.fragments.hseq.WelcomeHomeFragment;
import co.uk.depotnet.onsa.listeners.FragmentActionListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.notify.NotifyModel;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.NetworkStateReceiver;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;

public class WelcomeActivity extends BaseActivity implements
        NetworkStateReceiver.NetworkStateReceiverListener, FragmentActionListener, GetFetchListener {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1093;
    private boolean isNetworkDialogVisible;
    private ProgressBar progressBar;
    private User user;
    private NetworkStateReceiver networkStateReceiver;
    private Fragment fragment;
    private Fetch fetch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        networkStateReceiver = new NetworkStateReceiver();
        networkStateReceiver.addListener(this);

        fetch = Fetch.Impl.getDefaultInstance();
        try {
            user = dbHandler.getUser();

        setupBottomNavigation();

        if (savedInstanceState == null) {
            fragment = WelcomeHomeFragment.newInstance();
            addFragment(fragment, false);
        }
        APICalls.getTags(user.gettoken()).enqueue(new Callback<ArrayList<String>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<String>> call, @NonNull Response<ArrayList<String>> response) {
                if (response.isSuccessful()) {
                    dbHandler.resetTags();
                    ArrayList<String> body1 = response.body();
                    if(body1 == null){
                        return;
                    }
                    for (int i = 0; i < body1.size(); i++) {
                        ContentValues cv = new ContentValues();
                        cv.put("tagslist", body1.get(i));
                        dbHandler.replaceData("Tags", cv);
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<String>> call, @NonNull Throwable t) {
            }
        });
        APICalls.getNotifications(user.gettoken()).enqueue(new Callback<ArrayList<NotifyModel>>() {
            @Override
            public void onResponse(@NonNull Call<ArrayList<NotifyModel>> call, @NonNull Response<ArrayList<NotifyModel>> response) {
                if (CommonUtils.onTokenExpired(WelcomeActivity.this, response.code())) {
                    return;
                }
                if (response.isSuccessful()) {
                    dbHandler.resetNotification();
                    ArrayList<NotifyModel> list = response.body();
                    if (list != null && list.size() > 0) {
                        for (NotifyModel modal : list) {
                            dbHandler.replaceData(NotifyModel.DBTable.NAME, modal.toContentValues());
                        }
                        NotifyUtils.scheduleJob(getApplicationContext(), true); // reschedule the job
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<ArrayList<NotifyModel>> call, @NonNull Throwable t) {
            }
        });
        }catch (Exception e){

        }
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragment = WelcomeHomeFragment.newInstance();
        addFragment(fragment, false);
    }

    private void setupBottomNavigation() {
        progressBar = findViewById(R.id.progress_bar);
        BottomNavigationView mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_home:
                        setTitle("Welcome");
                        loadHomeFragment();
                        return true;
                    case R.id.action_kit_bag:
                        if (checkAndRequestPermissions()) {
                            loadkitbagFragment();
                        }
                        return true;
                    case R.id.action_offline_queue:
                        loadofflinequeueFragment();
                        return true;
                }
                return false;
            }
        });
    }

    private void loadHomeFragment() {

        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
        }
    }

    private void loadkitbagFragment() {
        FragmentKitBag fragment = FragmentKitBag.newInstance(0);
        addFragment(fragment, false);
    }

    @Override
    public void openKitbagFolder(int parentId) {
        FragmentKitBag fragment = FragmentKitBag.newInstance(parentId);
        addFragment(fragment, false);
    }

    private void loadofflinequeueFragment() {
        FragmentQueue fragment = FragmentQueue.newInstance(false);
        addFragment(fragment, true);
    }

    private boolean checkAndRequestPermissions() {
        int permissionPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorageRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        int permissionCamera = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        List<String> listPermissionsNeeded = new ArrayList<>();
        if (permissionStorage != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionStorageRead != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (permissionPhoneState != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.INTERNET);
        }
        if (permissionCamera != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CAMERA);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.READ_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for all permissions
                    if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                        loadkitbagFragment();
                    } else {
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
                            showDialogOK("Storage Read/Write and Camera Permission required to run app Smoothly, without these permissions some of the app features would be unavailable.",
                                    (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                Toast.makeText(this, "Storage Read/Write and Camera Permission required. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    });
                        } else {
                            showDialogOK("Storage Read/Write and Camera Permission required to run app Smoothly, without these permissions some of the app features would be unavailable. Please allow in App Settings for additional functionality.",
                                    (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.fromParts("package", getPackageName(), null));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                Toast.makeText(this, "Storage Read/Write and Camera Permission required. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    });
                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }
    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new MaterialAlertDialogBuilder(this)
                .setMessage(message)
                .setPositiveButton("Grant permission", okListener)
                .setNegativeButton("Later", okListener)
                .create()
                .show();
    }

    @Override
    public void networkAvailable() {
        ArrayList<Submission> submissions = dbHandler.getQueuedSubmissions();
        if (submissions.isEmpty()) {
            return;
        }
        if (!isNetworkDialogVisible) {
            showNetworkDialog("Network Available", "Do you want to upload Offline queue");
        }
    }

    @Override
    public void networkUnavailable() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        this.registerReceiver(networkStateReceiver, new IntentFilter(android.net.ConnectivityManager.CONNECTIVITY_ACTION));

    }

    @Override
    protected void onPause() {
        super.onPause();
        this.unregisterReceiver(networkStateReceiver);
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }

    @Override
    public void addFragment(Fragment fragment, boolean isHorizontalAnim) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!(fragment instanceof WelcomeHomeFragment)) {
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
        transaction.replace(R.id.welcome_container, fragment, fragment.getClass().getName());
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void setTitle(String title) {
    }

    @Override
    public void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    @Override
    public void setReceiptsBadge(String number) {
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

                    if (fragment != null && fragment.isVisible()) {
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

    public Fetch getFetch() {
        return fetch;
    }
}