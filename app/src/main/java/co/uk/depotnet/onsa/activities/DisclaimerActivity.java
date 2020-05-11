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
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.Disclaimer;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.store.FeatureResult;
import co.uk.depotnet.onsa.modals.store.StoreDataset;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
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

    private Callback<DatasetResponse> dataSetCallback = new Callback<DatasetResponse>() {
        @Override
        public void onResponse(@NonNull Call<DatasetResponse> call,
                               Response<DatasetResponse> response) {

            if (response.isSuccessful() && response.body() != null) {
                response.body().toContentValues();
                user.setDisclaimerAccepted(true);
                DBHandler.getInstance().replaceData(User.DBTable.NAME, user.toContentValues());

            }
            actionOnStoreStatus();
        }

        @Override
        public void onFailure(@NonNull Call<DatasetResponse> call, @NonNull Throwable t) {
            actionOnStoreStatus();
        }
    };

    private void actionOnStoreStatus(){
        Constants.isStoreEnabled = DBHandler.getInstance().isFeatureActive(Constants.FEATURE_NAME);
        if(Constants.isStoreEnabled) {
            APICalls.getStoreDataSet(user.gettoken()).enqueue(storeDataSetCallback);
            return;
        }
        hideProgressBar();
        startMainActivity();
    }

    public void startMainActivity(){
                hideProgressBar();
                Intent intent = new Intent(DisclaimerActivity.this, MainActivity.class);
                intent.putExtra("User", DBHandler.getInstance().getUser());
                startActivity(intent);
                finish();
    }

    private Callback<StoreDataset> storeDataSetCallback = new Callback<StoreDataset>() {
        @Override
        public void onResponse(@NonNull Call<StoreDataset> call,
                               @NonNull Response<StoreDataset> response) {

            if (response.isSuccessful() && response.body() != null) {
                response.body().toContentValues();
                user.setDisclaimerAccepted(true);
                DBHandler.getInstance().replaceData(User.DBTable.NAME, user.toContentValues());
                startMainActivity();
            } else {
                hideProgressBar();
            }

        }

        @Override
        public void onFailure(@NonNull Call<StoreDataset> call, @NonNull Throwable t) {
            hideProgressBar();
        }
    };


    private okhttp3.Callback disclaimerCallbackLogo = new okhttp3.Callback() {
        @Override
        public void onFailure(@NonNull okhttp3.Call call, @NonNull IOException e) {
            new Handler(Looper.getMainLooper()).post(() -> {
                hideProgressBar();
                btnAccept.setVisibility(View.VISIBLE);
                btnDecline.setVisibility(View.VISIBLE);
            });

        }

        @Override
        public void onResponse(@NonNull okhttp3.Call call, @NonNull okhttp3.Response response) throws IOException {
            ResponseBody responseBody = response.body();
            if(responseBody != null){
                InputStream is = responseBody.byteStream();

                final Bitmap bitmap = BitmapFactory.decodeStream(is);


                    new Handler(Looper.getMainLooper()).post(() -> {
                        if (bitmap != null) {
                            imgDisclaimer.setImageBitmap(bitmap);

                        }
                        hideProgressBar();
                        btnAccept.setVisibility(View.VISIBLE);
                        btnDecline.setVisibility(View.VISIBLE);

                    });
            }

        }
    };


    private Callback<Disclaimer> disclaimerCallback = new Callback<Disclaimer>() {
        @Override
        public void onResponse(@NonNull Call<Disclaimer> call, Response<Disclaimer> response) {

            if (response.isSuccessful()) {
                Disclaimer disclaimer = response.body();
                if (disclaimer != null) {
                    txtDisclaimer.setText(disclaimer.getDisclaimerText());
                    APICalls.getDisclaimerLogo(user.gettoken(), disclaimerCallbackLogo);
                    return;
                }
            }else if(response.code() == 429){
                showErrorDialog("Error" , "JWT Rewoked");
            }

            hideProgressBar();
            btnAccept.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.VISIBLE);
        }

        @Override
        public void onFailure(@NonNull Call<Disclaimer> call,
                              @NonNull Throwable t) {
            btnAccept.setVisibility(View.VISIBLE);
            btnDecline.setVisibility(View.VISIBLE);
            hideProgressBar();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_disclaimer);

        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        txtDisclaimer = findViewById(R.id.txt_disclaimer);
        imgDisclaimer = findViewById(R.id.img_disclaimer);
        btnAccept = findViewById(R.id.btn_accept);
        btnAccept.setOnClickListener(DisclaimerActivity.this);
        btnDecline = findViewById(R.id.btn_decline);
        btnDecline.setOnClickListener(DisclaimerActivity.this);

        btnAccept.setVisibility(View.GONE);
        btnDecline.setVisibility(View.GONE);
        user = DBHandler.getInstance().getUser();
        showProgressBar();
        if (user != null && !TextUtils.isEmpty(user.gettoken())) {
            APICalls.getDisclaimer(user.gettoken()).enqueue(disclaimerCallback);
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
        showProgressBar();
        DBHandler.getInstance().replaceData(User.DBTable.NAME, user.toContentValues());
        getFeatures();

    }

    private void getFeatures(){
        APICalls.featureResultCall(user.gettoken()).enqueue(new Callback<FeatureResult>() {
            @Override
            public void onResponse(@NonNull Call<FeatureResult> call, @NonNull Response<FeatureResult> response) {
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

    public void showErrorDialog(String title, String message) {

        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(DisclaimerActivity.this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> {dialog1.dismiss();
                    Intent intent = new Intent(DisclaimerActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                })
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }
}
