package co.uk.depotnet.onsa.activities;

import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.utils.AppPreferences;

public class ThemeBaseActivity extends AppCompatActivity {
    private final static int THEME_HSEQ = 1;
    private final static int THEME_BRIEFINGS = 2;
    private final static int THEME_ACTIONS = 3;
    private final static int THEME_ALERTS = 4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        updateTheme();
    }

    public void updateTheme() {
        if (AppPreferences.getTheme() <= THEME_HSEQ) {
            setTheme(R.style.AppTheme_Hseq);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(), R.color.ColorHseq));
            }
        } else if (AppPreferences.getTheme() == THEME_BRIEFINGS) {
            setTheme(R.style.AppTheme_Briefings);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.ColorBriefing));
            }
        }  else if (AppPreferences.getTheme() == THEME_ACTIONS) {
            setTheme(R.style.AppTheme_Actions);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.ColorActions));
            }
        } else {
            setTheme(R.style.AppTheme);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(getApplicationContext(),R.color.depotnet_color));
            }
        }
    }
}
