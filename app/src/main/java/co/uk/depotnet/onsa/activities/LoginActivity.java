package co.uk.depotnet.onsa.activities;

import android.content.Context;
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

import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.ErrorDialog;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.httprequests.UserRequest;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText etUserName;
    private EditText etPassword;
    private LinearLayout llUiBlocker;
    private User user;

    private Callback<User> loginCallback = new Callback<User>() {
        @Override
        public void onResponse(@NonNull Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                user = response.body();
                if (user != null && !TextUtils.isEmpty(user.getuserId())) {
                    DBHandler.getInstance().insertData(User.DBTable.NAME, user.toContentValues());
                    AppPreferences.putString("UserName" , etUserName.getText().toString().trim());
                    AppPreferences.putString("UserPassword" , etPassword.getText().toString().trim());

                    startActivity(new Intent(LoginActivity.this , DisclaimerActivity.class));
                    finish();
                    return;
                }
            }

           // ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "User name or Password not matching.","Wrong Password");
            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "Please try again.","Wrong Password");
            dialog.show();
            hideProgressBar();
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            hideProgressBar();
            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "Please try again.","Wrong Password");
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
        txtVersionCode.setText("version " + BuildConfig.VERSION_NAME);

        findViewById(R.id.btn_login).setOnClickListener(LoginActivity.this);
        findViewById(R.id.txt_btn_forgot_password).setOnClickListener(LoginActivity.this);
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

        if(!CommonUtils.isNetworkAvailable(LoginActivity.this)){
            hideProgressBar();
            ErrorDialog dialog = new ErrorDialog(LoginActivity.this , "Please check your Internet connection","Error");
            dialog.show();
            return;
        }

        if (validate()) {
            UserRequest userRequest = new UserRequest(etUserName.getText().toString().trim(),
                    etPassword.getText().toString().trim());

            APICalls.callLogin(userRequest).enqueue(loginCallback);
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
}
