package co.uk.depotnet.onsa.activities;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import co.uk.depotnet.onsa.R;

public class UserProfileActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
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
