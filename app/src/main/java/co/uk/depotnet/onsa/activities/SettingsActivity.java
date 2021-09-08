package co.uk.depotnet.onsa.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import co.uk.depotnet.onsa.BuildConfig;
import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.FragmentQueue;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.httprequests.ActiveMfa;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.networking.Constants;
import co.uk.depotnet.onsa.utils.AppPreferences;

import co.uk.depotnet.onsa.views.ContactBottomSheet;
import co.uk.depotnet.onsa.views.MaterialAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView imgQrCode;
    private LinearLayout llCompetencies;
    private User user;
    private ProgressBar progressBar;
    private DBHandler dbHandler;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        this.dbHandler = DBHandler.getInstance(this);
        user = dbHandler.getUser();


        llCompetencies = findViewById(R.id.ll_competencies);
        imgQrCode = findViewById(R.id.img_qr_code);
        TextView txtUserName = findViewById(R.id.txt_user_name);
        TextView txtVersion = findViewById(R.id.txt_version_code);
        TextView txtBuild = findViewById(R.id.txt_build_name);
        progressBar = findViewById(R.id.progress_bar);

        StringBuilder userName = new StringBuilder();
        userName.append(user.getuserName());

        if(TextUtils.isEmpty(user.getroleName())){
            userName.append(" (");
            userName.append(user.getroleName());
            userName.append(")");
        }

        txtUserName.setText(userName.toString());
        txtVersion.setText(String.valueOf(BuildConfig.VERSION_NAME));
        txtBuild.setText("BUILD("+String.valueOf(BuildConfig.VERSION_CODE)+")");
        if(Constants.isHSEQEnabled) {
            if (user.getQrCodeBase64() != null) {
                byte[] imageByteArray = Base64.decode(user.getQrCodeBase64(), Base64.DEFAULT);
                Glide.with(this)
                        .asBitmap().load(imageByteArray).into(imgQrCode);
            }
        }else{
            llCompetencies.setVisibility(View.GONE);
        }


        findViewById(R.id.btn_img_cancel).setOnClickListener(this);
        findViewById(R.id.btn_contact_support).setOnClickListener(this);
        findViewById(R.id.btn_logout).setOnClickListener(this);
        findViewById(R.id.btn_user_profile).setOnClickListener(this);
//        findViewById(R.id.btn_password_reset).setOnClickListener(this);
        findViewById(R.id.btn_password_reset).setVisibility(View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_contact_support:
                contactSupport();
                break;
            case R.id.btn_logout:
                logout();
//                callLogout();
                break;
            case R.id.btn_user_profile:
                callUserProfile();
                break;
            case R.id.btn_password_reset:
                passwordReset();
                break;
        }
    }


    private void callUserProfile(){
        Intent intent=new Intent(SettingsActivity.this,UserProfileActivity.class);
        startActivity(intent);

    }
     private void  passwordReset(){
        Intent intent=new Intent(SettingsActivity.this,ForgotPasswordActivity.class);
        intent.putExtra("reset_password","RESET_PASSWORD");
        startActivity(intent);
        finish();
     }
    private void contactSupport(){
        ContactBottomSheet bottomSheet = new ContactBottomSheet(this, new ContactBottomSheet.OnContactListener() {
            @Override
            public void onPhoneClick(String phone) {
                try {
                    Intent my_callIntent = new Intent(Intent.ACTION_DIAL);
                    my_callIntent.setData(Uri.parse("tel:"+phone));
                    //here the word 'tel' is important for making a call...
                    startActivity(my_callIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getApplicationContext(), "Error in your phone call"+e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onEmailClick(String email) {
                final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                emailIntent.setType("text/plain");
                emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{ email});
                emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Hello There");
                emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "Add Message here");


                emailIntent.setType("message/rfc822");

                try {
                    startActivity(Intent.createChooser(emailIntent,
                            "Send email using..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(SettingsActivity.this,
                            "No email clients installed.",
                            Toast.LENGTH_SHORT).show();
                }

            }
        });

        bottomSheet.show();
    }

    private void logout(){
        showProgressBar();
//        AppPreferences.clear();
//        dbHandler.resetDatabase();

        user.setLoggedIn(false);
        dbHandler.replaceData(User.DBTable.NAME , user.toContentValues());
//        CommonUtils.clearAppData(dbHandler);

        hideProgressBar();
        Intent intent = new Intent(this , LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void callLogout(){
        if(!CommonUtils.isNetworkAvailable(this)){
            showErrorDialog("Network Error" , "Internet not available. Please check your internet connection.");
            return;
        }

        showProgressBar();
        APICalls.signOut(user.getTokenId() , user.gettoken()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

                hideProgressBar();
                if(CommonUtils.onTokenExpired(SettingsActivity.this , response.code())){
                    return;
                }

                if(response.isSuccessful()){
                    logout();
                    return;
                }
                showErrorDialog("Logout Error" , "Some error during logout. Please try again!");
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                hideProgressBar();
                showErrorDialog("Logout Error" , "Some error during logout. Please try again!");
            }
        });
    }

    private void hideProgressBar() {
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showProgressBar() {
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void showErrorDialog(String title, String message) {

        if(getSupportFragmentManager().isStateSaved()){
            return;
        }
        MaterialAlertDialog dialog = new MaterialAlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositive(getString(R.string.ok), (dialog1, i) -> dialog1.dismiss())
                .build();

        dialog.setCancelable(false);
        dialog.show(getSupportFragmentManager(), "_ERROR_DIALOG");
    }
}
