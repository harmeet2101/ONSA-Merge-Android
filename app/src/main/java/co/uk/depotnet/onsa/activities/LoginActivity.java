package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;


import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.ErrorDialog;
import co.uk.depotnet.onsa.dialogs.MFADialog;
import co.uk.depotnet.onsa.modals.ErrorBody;
import co.uk.depotnet.onsa.modals.ItemType;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.httprequests.ActiveMfa;
import co.uk.depotnet.onsa.modals.httprequests.UserRequest;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText etUserName;
    private EditText etPassword;
    private LinearLayout llUiBlocker;
    private DBHandler dbHandler;
    
    private Callback<ActiveMfa> mfaCallback = new Callback<ActiveMfa>() {
        @Override
        public void onResponse(@NonNull Call<ActiveMfa> call, @NonNull Response<ActiveMfa> response) {
            hideProgressBar();

            if(CommonUtils.onTokenExpired(LoginActivity.this , response.code())){
                return;
            }

            if(response.isSuccessful()){
                ActiveMfa activeMfa = response.body();
                if(activeMfa != null){
                    showMFADialog(activeMfa);
                    return;
                }
            }
            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "Please try again.","Some Error occurred");
            dialog.show();
        }

        @Override
        public void onFailure(@NonNull Call<ActiveMfa> call, @NonNull Throwable t) {
            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "Please try again.","Some Error occurred");
            dialog.show();
        }
    };

    private void showMFADialog(ActiveMfa mfa) {
        MFADialog mfaDialog = new MFADialog(this , mfa);
        mfaDialog.show();
    }

    private void createItemTypes(){
        ItemType itemType = new ItemType("Site Clear" , "TaskType" , "6");
        ItemType itemType1 = new ItemType("Muckaway" , "TaskType" , "3");
        ItemType itemType2 = new ItemType("Service/Material Drop off" , "TaskType" , "1");
        ItemType itemType3 = new ItemType("Reinstatement" , "TaskType" , "5");
        ItemType itemType4 = new ItemType("Backfill" , "TaskType" , "4");

        dbHandler.replaceData(ItemType.DBTable.NAME , itemType.toContentValues());
        dbHandler.replaceData(ItemType.DBTable.NAME , itemType1.toContentValues());
        dbHandler.replaceData(ItemType.DBTable.NAME , itemType2.toContentValues());
        dbHandler.replaceData(ItemType.DBTable.NAME , itemType3.toContentValues());
        dbHandler.replaceData(ItemType.DBTable.NAME , itemType4.toContentValues());
    }

    private Callback<User> loginCallback = new Callback<User>() {
        @Override
        public void onResponse(@NonNull Call<User> call, @NonNull Response<User> response) {
            hideProgressBar();
            String errorMessage = "Please try again.";
            String errorTitle = "Login Unsuccessful";

            createItemTypes();

            if (response.isSuccessful()) {
                User user = response.body();
                if (user != null && !TextUtils.isEmpty(user.getuserId())) {
                    if(!isSameUserLoggedIn(user)){
                        CommonUtils.clearAppData(dbHandler);
                    }
//                    dbHandler.clearTable(User.DBTable.NAME);
                    user.setLoggedIn(true);
                    dbHandler.replaceData(User.DBTable.NAME, user.toContentValues());
                    AppPreferences.putString("UserName" , etUserName.getText().toString().trim());
                    AppPreferences.putString("UserPassword" , etPassword.getText().toString().trim());

                    if(!user.isTwoFactorMandatory()){
                        Intent intent = new Intent(LoginActivity.this , DisclaimerActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                        return;
                    }

                    if(user.isTwoFactorEnabled()){
                        if(user.isCaptureMfaChallenge()){

                            startActivity(new Intent(LoginActivity.this, VerificationActivity.class));
                        }else{
                            AppPreferences.putInt("isOtpVerified" , 1);
                            Intent intent = new Intent(LoginActivity.this , DisclaimerActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP| Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                            startActivity(intent);
                            finish();
                        }
                        return;
                    }
                    
                    showProgressBar();
                    CallUtils.enqueueWithRetry(APICalls.activeMFA(user.gettoken()),mfaCallback);
                    return;
                }
            }else{
                ResponseBody responseBody = response.errorBody();
                if(responseBody != null){
                    try {
                        String res = responseBody.string();
                        if(!TextUtils.isEmpty(res)){
                            ErrorBody errorBody = new Gson().fromJson(res , ErrorBody.class);
                            errorMessage = errorBody.getMessage();
                            errorTitle = errorBody.getStatus();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , errorMessage,errorTitle);
            dialog.show();
        }

        @Override
        public void onFailure(@NonNull Call<User> call, @NonNull Throwable t) {
            hideProgressBar();
            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "Please try again.","Login Unsuccessful");
            dialog.show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etUserName = findViewById(R.id.et_user_name);
        etPassword = findViewById(R.id.et_password);
        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        TextView txtVersionCode = findViewById(R.id.txt_version_code);
        txtVersionCode.setText(String.format("version %s", BuildConfig.VERSION_NAME));
        findViewById(R.id.btn_login).setOnClickListener(LoginActivity.this);
        findViewById(R.id.txt_btn_forgot_password).setOnClickListener(LoginActivity.this);

        this.dbHandler = DBHandler.getInstance(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btn_login:
                login();
                break;
            case R.id.txt_btn_forgot_password:
                forgotPassword();
                break;
        }
    }

    private void forgotPassword() {
        startActivity(new Intent(this, ForgotPasswordActivity.class));
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

    private void login() {
        showProgressBar();

//      etUserName.setText("kimtest1");
//      etPassword.setText("Whistle1!");
//      etUserName.setText("harriet.abbott");
//      etPassword.setText("Testing2!");
//        etUserName.setText("AidanApp");
//        etPassword.setText("Testing1!");
//        etUserName.setText("sam1");
//        etPassword.setText("PopPop247!");
        if(!CommonUtils.isNetworkAvailable(LoginActivity.this)){
            hideProgressBar();
            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "Please check your Internet connection","Error");
            dialog.show();
            return;
        }

        if (validate()) {
            UserRequest userRequest = new UserRequest(etUserName.getText().toString().trim(),
                    etPassword.getText().toString().trim());

            User user = dbHandler.getUser();
            String token = null;
            if(user != null){
                token = user.gettoken();
            }
            APICalls.callLogin(userRequest , token).enqueue(loginCallback);
            return;
        }
        hideProgressBar();
    }

    private boolean validate() {
        String userName = etUserName.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (userName.isEmpty()) {
            Toast.makeText(this, "Please enter valid username", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (password.isEmpty()) {
            Toast.makeText(this, "Please enter valid password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private boolean isSameUserLoggedIn(User user){
        if(user == null || TextUtils.isEmpty(user.getuserId())){
            return false;
        }
        User lastUser = dbHandler.getUser();
        if(lastUser == null || TextUtils.isEmpty(lastUser.getuserId())) {
            return false;
        }

        return lastUser.getuserId().equalsIgnoreCase(user.getuserId());

    }

}
