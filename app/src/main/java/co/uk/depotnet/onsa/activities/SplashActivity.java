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
import co.uk.depotnet.onsa.RefreshDatasetService;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class SplashActivity extends AppCompatActivity {

    private User user;

    private void startWelcomeActivity(){
        Intent intent = new Intent(SplashActivity.this, WelcomeActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        AppCenter.start(getApplication(), "135e3d47-d004-4619-8ba3-da341e88b408",
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
                startActivity(intent);
                finish();
                return;
            }


            DBHandler.getInstance().replaceData(User.DBTable.NAME, user.toContentValues());
            if (!CommonUtils.isNetworkAvailable(SplashActivity.this)) {
                Constants.isStoreEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_STORE);
                Constants.isHSEQEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_HSEQ);
                intent = new Intent(SplashActivity.this, WelcomeActivity.class);
                startActivity(intent);
                finish();
                return;
            }
            getAPIData();
        }, 1000);
    }

    private void getAPIData(){
        RefreshDatasetService.startAction(this , user.gettoken());
        getFeatures();
    }

    private void getFeatures() {
        if(!CommonUtils.validateToken(SplashActivity.this)){
            return;
        }
        APICalls.featureResultCall(user.gettoken()).enqueue(new Callback<FeatureResult>() {
            @Override
            public void onResponse(@NonNull Call<FeatureResult> call, @NonNull Response<FeatureResult> response) {
                if (CommonUtils.onTokenExpired(SplashActivity.this, response.code())) {
                    return;
                }

                if (response.isSuccessful()) {
                    FeatureResult featureResult = response.body();
                    if (featureResult != null) {
                        featureResult.toContentValues();
                    }
                }

                Constants.isStoreEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_STORE);
                Constants.isHSEQEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_HSEQ);
                startWelcomeActivity();
            }

            @Override
            public void onFailure(@NonNull Call<FeatureResult> call, @NonNull Throwable t) {
                Constants.isStoreEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_STORE);
                Constants.isHSEQEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_HSEQ);
                startWelcomeActivity();
            }
        });
    }


    private boolean isLogin() {
        return user != null && !TextUtils.isEmpty(user.gettoken()) && user.isLoggedIn();
    }

    private boolean isVerifiedOTP() {
        return isLogin() && (!user.isTwoFactorMandatory() || AppPreferences.getInt("isOtpVerified" , 0) == 1);
    }
}
