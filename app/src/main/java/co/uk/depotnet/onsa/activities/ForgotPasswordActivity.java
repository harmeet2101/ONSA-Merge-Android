package co.uk.depotnet.onsa.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.dialogs.ErrorDialog;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.httprequests.ResetPassword;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ForgotPasswordActivity extends AppCompatActivity
        implements View.OnClickListener {

    private EditText etEmail;
    private LinearLayout llUiBlocker;

    private Callback<User> forgetPasswordCallback = new Callback<User>() {
        @Override
        public void onResponse(Call<User> call, Response<User> response) {
            if (response.isSuccessful()) {
                User user = response.body();
                if (user != null) {
                    return;

                }
            }

            ErrorDialog dialog = new ErrorDialog(ForgotPasswordActivity.this, "Password can't reset.","");
            dialog.show();
            hideProgressBar();
        }

        @Override
        public void onFailure(Call<User> call, Throwable t) {
            hideProgressBar();
            ErrorDialog dialog = new ErrorDialog(ForgotPasswordActivity.this, "Password can't reset.","");
            dialog.show();
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
        etEmail = findViewById(R.id.et_email_address);
        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        Intent intent=getIntent();
        String reset_password=intent.getStringExtra("reset_password");
        if(!TextUtils.isEmpty(reset_password)&& reset_password.equalsIgnoreCase("RESET_PASSWORD")){
            findViewById(R.id.txt_btn_go_to_login).setVisibility(View.GONE);
        }

        findViewById(R.id.btn_submit).setOnClickListener(ForgotPasswordActivity.this);
        findViewById(R.id.txt_btn_go_to_login).setOnClickListener(ForgotPasswordActivity.this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_submit:
                submitRequest();
                break;

            case R.id.txt_btn_go_to_login:
                finish();
                break;
        }

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

    private void submitRequest() {
        showProgressBar();

        if (!CommonUtils.isNetworkAvailable(ForgotPasswordActivity.this)) {
            hideProgressBar();
            ErrorDialog dialog = new ErrorDialog(ForgotPasswordActivity.this, "Please check your Internet connection","Error");
            dialog.show();
            return;
        }

        if (validate()) {
            APICalls.resetPassword(new ResetPassword(etEmail.toString().trim())).enqueue(forgetPasswordCallback);

            return;
        }
        hideProgressBar();
    }

    private boolean validate() {
        String userName = etEmail.getText().toString().trim();
        if (TextUtils.isEmpty(userName)) {
            Toast.makeText(this, "Please enter valid username", Toast.LENGTH_SHORT).show();
            return false;
        }


        if (!Patterns.EMAIL_ADDRESS.matcher(userName).matches()) {
            Toast.makeText(this, "Please enter valid username", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


}
