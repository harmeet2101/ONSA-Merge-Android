package co.uk.depotnet.onsa.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.modals.User;


public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView txtUserName;
    private TextView txtGang;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        txtUserName = findViewById(R.id.txt_user_name);
        txtGang = findViewById(R.id.txt_gang);
        User user = DBHandler.getInstance().getUser();

        if(user != null){
            txtUserName.setText(user.getuserName());
            txtGang.setText(user.getroleName());
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.img_btn_cancel:
                finish();
                break;
        }
    }
}
