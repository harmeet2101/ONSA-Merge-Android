package co.uk.depotnet.onsa.activities;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import co.uk.depotnet.onsa.database.DBHandler;

public class BaseActivity extends AppCompatActivity {

    public DBHandler dbHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dbHandler = DBHandler.getInstance(this);

    }
}
