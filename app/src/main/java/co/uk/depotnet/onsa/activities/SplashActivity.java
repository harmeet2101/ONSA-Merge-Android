package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.widget.TextView;

import com.microsoft.appcenter.AppCenter;
import com.microsoft.appcenter.analytics.Analytics;
import com.microsoft.appcenter.crashes.Crashes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.JWTErrorDialog;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {

    private User user;
    private Callback<StoreDataset> storeDataSetCallback = new Callback<StoreDataset>() {
        @Override
        public void onResponse(@NonNull Call<StoreDataset> call,
                               Response<StoreDataset> response) {

            if(CommonUtils.onTokenExpired(SplashActivity.this , response.code())){
                return;
            }

            if (response.body() != null) {
                response.body().toContentValues();
            }
            startMainActivity();
        }

        @Override
        public void onFailure(@NonNull Call<StoreDataset> call, @NonNull Throwable t) {
            startMainActivity();
        }
    };




    private Callback<DatasetResponse> dataSetCallback = new Callback<DatasetResponse>() {
        @Override
        public void onResponse(@NonNull Call<DatasetResponse> call,
                               Response<DatasetResponse> response) {
            if(CommonUtils.onTokenExpired(SplashActivity.this , response.code())){
                return;
            }
            if (response.body() != null) {
                DBHandler.getInstance().resetDatasetTable();
                response.body().toContentValues();

            }
            Constants.isStoreEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_NAME);

            if(Constants.isStoreEnabled) {
                APICalls.getStoreDataSet(user.gettoken()).enqueue(storeDataSetCallback);
                return;
            }
            startMainActivity();

        }

        @Override
        public void onFailure(@NonNull Call<DatasetResponse> call, @NonNull Throwable t) {
            Constants.isStoreEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_NAME);

            if(Constants.isStoreEnabled) {
                APICalls.getStoreDataSet(user.gettoken()).enqueue(storeDataSetCallback);
                return;
            }
            startMainActivity();
        }
    };

    private void getFeatures(){
        APICalls.featureResultCall(user.gettoken()).enqueue(new Callback<FeatureResult>() {
            @Override
            public void onResponse(@NonNull Call<FeatureResult> call, @NonNull Response<FeatureResult> response) {
                if(CommonUtils.onTokenExpired(SplashActivity.this , response.code())){
                    return;
                }

                if(response.isSuccessful()){
                    FeatureResult featureResult = response.body();
                    if(featureResult != null){
                        featureResult.toContentValues();
                    }
                }
                APICalls.getDataSet(user.gettoken()).enqueue(dataSetCallback);
            }

            @Override
            public void onFailure(@NonNull Call<FeatureResult> call, @NonNull Throwable t) {
                APICalls.getDataSet(user.gettoken()).enqueue(dataSetCallback);
            }
        });
    }

    private void startMainActivity(){
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        intent.putExtra("User", user);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCenter.start(getApplication(), "96c34dd5-a1d3-4ff1-b5b3-cfb9a90756bf",
                Analytics.class, Crashes.class);
        DBHandler.getInstance().init(getApplicationContext());
        user = DBHandler.getInstance().getUser();

        TextView textView = findViewById(R.id.txt_version_code);
        textView.setText(BuildConfig.VERSION_NAME);
        new Handler().postDelayed(() -> {
            Intent intent;

            if (!isLogin()) {
                intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            if(!isVerifiedOTP()){
                intent = new Intent(SplashActivity.this, VerificationActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            if (!user.isDisclaimerAccepted()) {
                intent = new Intent(SplashActivity.this, DisclaimerActivity.class);
                intent.putExtra("User", user);
                startActivity(intent);
                finish();
                return;
            }


            DBHandler.getInstance().replaceData(User.DBTable.NAME, user.toContentValues());
            if (!CommonUtils.isNetworkAvailable(SplashActivity.this)) {
                Constants.isStoreEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_NAME);
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra("User", user);
                startActivity(intent);
                finish();
                return;
            }
            getFeatures();
        }, 2000);
    }


    private boolean isLogin() {
        return user != null && !TextUtils.isEmpty(user.gettoken());
    }

    private boolean isVerifiedOTP() {
        return isLogin() && (!user.isTwoFactorMandatory() || AppPreferences.getInt("isOtpVerified" , 0) == 1);
    }


}
