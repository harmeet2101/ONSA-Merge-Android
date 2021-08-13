package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.RefreshDatasetService;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Disclaimer;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.Constants;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DisclaimerActivity extends AppCompatActivity
        implements View.OnClickListener {


    private LinearLayout llUiBlocker;
    private TextView txtDisclaimer;
    private ImageView imgDisclaimer;
    private Button btnAccept;
    private Button btnDecline;
    private User user;
    private int apiCounter;
    private DBHandler dbHandler;

    public void startWelcomeActivity() {
        hideProgressBar();
        Intent intent = new Intent(DisclaimerActivity.this, WelcomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private okhttp3.Callback disclaimerCallbackLogo = new okhttp3.Callback() {
        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {

            new Handler(Looper.getMainLooper()).post(() -> {
                apiCounter++;
                showButtons();
            });
        }

        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
            ResponseBody responseBody = response.body();
            apiCounter++;
            if (response.isSuccessful() && responseBody != null) {
                InputStream is = responseBody.byteStream();
                final Bitmap bitmap = BitmapFactory.decodeStream(is);
                new Handler(Looper.getMainLooper()).post(() -> {
                    if (bitmap != null) {
                        imgDisclaimer.setImageBitmap(bitmap);
                    }
                    showButtons();

                });
            }else{
                new Handler(Looper.getMainLooper()).post(() -> {
                    showButtons();
                });
            }
        }
    };


    private Callback<Disclaimer> disclaimerCallback = new Callback<Disclaimer>() {
        @Override
        public void onResponse(@NonNull Call<Disclaimer> call, Response<Disclaimer> response) {
            apiCounter++;
            if (CommonUtils.onTokenExpired(DisclaimerActivity.this, response.code())) {
                return;
            }
            if (response.isSuccessful()) {
                Disclaimer disclaimer = response.body();
                if (disclaimer != null) {
                    txtDisclaimer.setText(disclaimer.getDisclaimerText());
                }
            }

           showButtons();
        }

        @Override
        public void onFailure(@NonNull Call<Disclaimer> call,
                              @NonNull Throwable t) {
            apiCounter++;
            showButtons();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        this.dbHandler = DBHandler.getInstance(this);
        this.user = dbHandler.getUser();
        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        txtDisclaimer = findViewById(R.id.txt_disclaimer);
        imgDisclaimer = findViewById(R.id.img_disclaimer);
        btnAccept = findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(DisclaimerActivity.this);
        btnDecline = findViewById(R.id.btn_decline);
        btnDecline.setOnClickListener(DisclaimerActivity.this);

        btnAccept.setVisibility(View.GONE);
        btnDecline.setVisibility(View.GONE);

        showProgressBar();
        if(CommonUtils.isNetworkAvailable(this)){

            if (CommonUtils.validateToken(DisclaimerActivity.this) && user != null && !TextUtils.isEmpty(user.gettoken())) {
                RefreshDatasetService.startAction(this , user.gettoken());
                CallUtils.enqueueWithRetry(APICalls.getDisclaimer(user.gettoken()) , disclaimerCallback);
                APICalls.getDisclaimerLogo(user.gettoken(), disclaimerCallbackLogo);
                getFeatures();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_accept:
                onAccept();
                break;
            case R.id.btn_decline:
                onDecline();
                break;
        }

    }

    private void onDecline() {
        startActivity(new Intent(DisclaimerActivity.this,
                LoginActivity.class));
        finish();
    }

    private void showProgressBar() {
        llUiBlocker.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void hideProgressBar() {
        llUiBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void onAccept() {
        user.setDisclaimerAccepted(true);
        dbHandler.replaceData(User.DBTable.NAME, user.toContentValues());
        startWelcomeActivity();
    }

    private void getFeatures() {

        CallUtils.enqueueWithRetry(APICalls.featureResultCall(user.gettoken()), new Callback<FeatureResult>() {
            @Override
            public void onResponse(@NonNull Call<FeatureResult> call, @NonNull Response<FeatureResult> response) {
                apiCounter++;
                    if (CommonUtils.onTokenExpired(DisclaimerActivity.this, response.code())) {
                    return;
                }

                if (response.isSuccessful()) {
                    FeatureResult featureResult = response.body();
                    if (featureResult != null) {
                        featureResult.toContentValues();
                    }
                }
                CommonUtils.activeFeatures(dbHandler);
                showButtons();
            }

            @Override
            public void onFailure(@NonNull Call<FeatureResult> call, @NonNull Throwable t) {
                apiCounter++;
                CommonUtils.activeFeatures(dbHandler);
                showButtons();
            }
        });
    }

    private void showButtons(){
        if(apiCounter == 3){
            btnAccept.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.VISIBLE);
            hideProgressBar();
        }
    }
}
