package co.uk.depotnet.onsa.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.ErrorDialog;
import co.uk.depotnet.onsa.dialogs.MFADialog;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.httprequests.ActiveMfa;
import co.uk.depotnet.onsa.modals.httprequests.VerificationRequest;
import co.uk.depotnet.onsa.modals.httpresponses.VerificationResult;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VerificationActivity extends AppCompatActivity {

    private EditText etOTP;
    private LinearLayout llUiBlocker;
    private User user;
    private Callback<ActiveMfa> mfaCallback = new Callback<ActiveMfa>() {
        @Override
        public void onResponse(@NonNull Call<ActiveMfa> call, Response<ActiveMfa> response) {
            hideProgressBar();

            if(CommonUtils.onTokenExpired(VerificationActivity.this , response.code())){
                return;
            }

            if(response.isSuccessful()){
                ActiveMfa activeMfa = response.body();
                if(activeMfa != null){
                    showMFADialog(activeMfa);
                    return;
                }
            }

            ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Please try again.","Some Error occurred");
            dialog.show();
        }

        @Override
        public void onFailure(@NonNull Call<ActiveMfa> call, @NonNull Throwable t) {
            ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Please try again.","Some Error occurred");
            dialog.show();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verification);
        etOTP = findViewById(R.id.et_otp);
        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        user = DBHandler.getInstance().getUser();
        TextView txtVersionCode = findViewById(R.id.txt_version_code);
        txtVersionCode.setText(String.format("version %s", BuildConfig.VERSION_NAME));

        findViewById(R.id.btn_verify).setOnClickListener(v -> {
            Editable editable = etOTP.getText();
            if(editable != null ){
                String otp = editable.toString();
                if(user.isTwoFactorEnabled()){
                    verify2FAChallenge(otp);
                }else {
                    verifyOTP(otp);
                }
            }
        });

        findViewById(R.id.txt_btn_shared_key).setOnClickListener(v -> getMFASharedKey());
    }

    private void getMFASharedKey(){
        if(!CommonUtils.isNetworkAvailable(this)){
            return;
        }

        User user = DBHandler.getInstance().getUser();
        if(user == null ){
            return;
        }

        showProgressBar();
        APICalls.activeMFA(user.gettoken()).enqueue(mfaCallback);
    }

    private void showMFADialog(ActiveMfa mfa) {
        MFADialog mfaDialog = new MFADialog(this , mfa);
        mfaDialog.show();
    }


    private void showProgressBar(){
        llUiBlocker.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }


    private void hideProgressBar(){
        llUiBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void verifyOTP(String otp){
        if(TextUtils.isEmpty(otp)){
            Toast.makeText(this, "Please enter valid username", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!CommonUtils.isNetworkAvailable(VerificationActivity.this)){
            ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Please check your Internet connection","Error");
            dialog.show();
            return;
        }

        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setRememberMe(true);
        verificationRequest.setCode(otp);

        final User user = DBHandler.getInstance().getUser();
        if(user == null ){
            return;
        }

        showProgressBar();

        APICalls.verifyCode(verificationRequest , user.gettoken()).enqueue(new Callback<VerificationResult>() {
            @Override
            public void onResponse(@NonNull Call<VerificationResult> call, @NonNull Response<VerificationResult> response) {
                hideProgressBar();

                if(CommonUtils.onTokenExpired(VerificationActivity.this , response.code())){
                    return;
                }



                if(response.isSuccessful()){

                    AppPreferences.putInt("isOtpVerified" , 1);
                    Intent intent = new Intent(VerificationActivity.this, DisclaimerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

                ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Verification failed.","Error");
                dialog.show();

            }

            @Override
            public void onFailure(@NonNull Call<VerificationResult> call, @NonNull Throwable t) {
                hideProgressBar();
                ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Verification failed.","Error");
                dialog.show();
            }
        });


    }


    private void verify2FAChallenge(String otp){
        if(TextUtils.isEmpty(otp)){
            Toast.makeText(this, "Please enter valid username", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!CommonUtils.isNetworkAvailable(VerificationActivity.this)){
            ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Please check your Internet connection","Error");
            dialog.show();
            return;
        }

        VerificationRequest verificationRequest = new VerificationRequest();
        verificationRequest.setRememberMe(true);
        verificationRequest.setCode(otp);




        showProgressBar();

        APICalls.verify2FAChallenge(verificationRequest , user.gettoken()).enqueue(new Callback<User>() {
            @Override
            public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
                hideProgressBar();
                if(CommonUtils.onTokenExpired(VerificationActivity.this , response.code())){
                    return;
                }

                if(response.isSuccessful()){
                    User user = response.body();
                    if(user != null){
                        user.setLoggedIn(true);
                        DBHandler.getInstance().replaceData(User.DBTable.NAME , user.toContentValues());
                    }
                    VerificationActivity.this.user = user;
                    AppPreferences.putInt("isOtpVerified" , 1);
                    Intent intent = new Intent(VerificationActivity.this, DisclaimerActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    return;
                }

                ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Verification failed.","Error");
                dialog.show();

            }

            @Override
            public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
                hideProgressBar();
                ErrorDialog dialog = new ErrorDialog(VerificationActivity.this , "Verification failed.","Error");
                dialog.show();
            }
        });


    }
}
