package co.uk.depotnet.onsa.activities.ui;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
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
import com.tonyodev.fetch2.DefaultFetchNotificationManager;
import com.tonyodev.fetch2.Fetch;
import com.tonyodev.fetch2.FetchConfiguration;
import com.tonyodev.fetch2core.Downloader;
import com.tonyodev.fetch2okhttp.OkHttpDownloader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ThemeBaseActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fcm.NotifyUtils;
import co.uk.depotnet.onsa.fragments.FragmentKitBag;
import co.uk.depotnet.onsa.fragments.FragmentQueue;
import co.uk.depotnet.onsa.fragments.briefings.BriefingFragment;
import co.uk.depotnet.onsa.listeners.GetFetchListener;
import co.uk.depotnet.onsa.modals.actions.Action;
import co.uk.depotnet.onsa.modals.actions.ActionResponse;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BriefingsActivity extends ThemeBaseActivity implements  GetFetchListener {

    private static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1095;
    private BottomNavigationView mBottomNavigationView;
    private Fragment fragment;
    private Fetch fetch;
    private ProgressBar progressBar;
    private final List<String> actionList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_briefings);

        final FetchConfiguration fetchConfiguration = new FetchConfiguration.Builder(this)
                .setDownloadConcurrentLimit(4)
                .setHttpDownloader(new OkHttpDownloader(Downloader.FileDownloaderType.PARALLEL))
                .setNamespace("OptinonsDownloader")
                .setNotificationManager(new DefaultFetchNotificationManager(this))
                .build();
//        fetch = Fetch.Impl.getInstance(fetchConfiguration);
        fetch = Fetch.Impl.getDefaultInstance();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.ColorBriefing));
        }

        setupBottomNavigation();
        if (savedInstanceState == null) {
            fragment= BriefingFragment.newInstance();
            ABHIKRCall(fragment,false);
        }


        progressBar=findViewById(R.id.progress_bar_actions);
        progressBar.setVisibility(View.VISIBLE);

        try {
            if(CommonUtils.isNetworkAvailable(this)) {
                GetActionsCall();
            }
            else
            {
                actionList.clear();
                getActionsFromDb();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        NotifyUtils.scheduleJob(getApplicationContext(),true); // schedule the job for notify
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        fragment= BriefingFragment.newInstance();
        ABHIKRCall(fragment,false);
    }

    private void setupBottomNavigation() {
        mBottomNavigationView = findViewById(R.id.bottom_navigation);
        mBottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_briefing:
                        loadbriefingsFragment();
                        return true;
                    case R.id.action_actions:
                        Intent hseq = new Intent(BriefingsActivity.this, ActionsActivity.class);
                        startActivity(hseq);
                        return true;
                    case R.id.action_schedule:
                        Intent hseqac = new Intent(BriefingsActivity.this, ScheduleInspectionActivity.class);
                        startActivity(hseqac);
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

    private void loadbriefingsFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        while (fragmentManager.getBackStackEntryCount() > 1) {
            fragmentManager.popBackStackImmediate();
        }
    }

    private void loadkitbagFragment() {
        FragmentKitBag fragment = FragmentKitBag.newInstance(0);
        ABHIKRCall(fragment,true);
    }

    private void loadofflinequeueFragment() {
        FragmentQueue fragment = FragmentQueue.newInstance(false);
        ABHIKRCall(fragment,false);
    }
    public void ABHIKRCall(Fragment fragment, boolean isHorizontalAnim)
    {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();

        if (!(fragment instanceof BriefingFragment)) {
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
        transaction.replace(R.id.briefings_container, fragment, fragment.getClass().getName());
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }
    public  boolean checkAndRequestPermissions() {
        int permissionPhoneState = ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET);
        int permissionStorage = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionStorageRead = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
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
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]),REQUEST_ID_MULTIPLE_PERMISSIONS);
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
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    if (perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                        Log.d("welcome", " Storage Services permission granted");
                        // process the normal flow
                        //loadkitbagFragment();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d("welcome", "Some permissions are not granted ask again ");
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE) || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            showDialogOK("Storage Read/Write Permission required to run app Smoothly, without these permissions some of the app features would be unavailable.",
                                    (dialog, which) -> {
                                        switch (which) {
                                            case DialogInterface.BUTTON_POSITIVE:
                                                checkAndRequestPermissions();
                                                break;
                                            case DialogInterface.BUTTON_NEGATIVE:
                                                // proceed with logic by disabling the related features or quit the app.
                                                Toast.makeText(this, "Storage Read/Write Permission required. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    });
                        } else {
                            showDialogOK("Storage Read/Write Permission required to run app Smoothly, without these permissions some of the app features would be unavailable. Please allow in App Settings for additional functionality.",
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
                                                Toast.makeText(this, "Storage Read/Write Permission required. Please allow in App Settings for additional functionality.", Toast.LENGTH_LONG).show();
                                                break;
                                        }
                                    });
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
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        super.onBackPressed();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppPreferences.setTheme(5);
    }

    @Override
    public Fetch getFetch() {
        if(fetch == null || fetch.isClosed()){
            fetch = Fetch.Impl.getDefaultInstance();
        }
        return fetch;
    }

    @Override
    public void openKitbagFolder(int parentId) {
        FragmentKitBag fragment = FragmentKitBag.newInstance(parentId);
        ABHIKRCall(fragment,true);
    }

    private void GetActionsCall()
    {
        CallUtils.enqueueWithRetry(APICalls.getActionsOutstandingList(DBHandler.getInstance().getUser().gettoken()),new Callback<List<ActionResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ActionResponse>> call, @NonNull Response<List<ActionResponse>> response) {
                if(CommonUtils.onTokenExpired(BriefingsActivity.this, response.code())){
                    return;
                }
                if (response.isSuccessful()) {
                    dbHandler.clearTable(Action.DBTable.NAME);
                    actionList.clear();
                    List<ActionResponse> actionsresponse=response.body();
                    if (actionsresponse!=null && !actionsresponse.isEmpty()) {
                        for (ActionResponse modal : actionsresponse)
                        {
                            modal.setActionType("Outstanding");
                            modal.toContentValues();
                            actionList.add("Outstanding");
                        }
                        getActionsFromDb();
                    }
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onFailure(@NonNull Call<List<ActionResponse>> call, @NonNull Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }

    private void getActionsFromDb()
    {
        List<String> dueDates = DBHandler.getInstance().getActionDueDates("Outstanding");

        if (dueDates!=null && !dueDates.isEmpty())
        {
            actionList.addAll(dueDates);
            mBottomNavigationView.getOrCreateBadge(R.id.action_actions).setNumber(actionList.size());
        }
        progressBar.setVisibility(View.GONE);
    }
}