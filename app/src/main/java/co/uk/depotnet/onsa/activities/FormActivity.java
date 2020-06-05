package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.fragments.FormFragment;
import co.uk.depotnet.onsa.listeners.FromActivityListener;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Form;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.utils.JsonReader;

public class FormActivity extends AppCompatActivity implements
        View.OnClickListener, FromActivityListener {


    public static final String ARG_SUBMISSION = "Submission";
    public static final String ARG_RECEIPT = "Receipt";
    public static final String ARG_MY_STORE_ITEMS = "MyStoreItems";
    public static final String ARG_REPEAT_COUNT = "repeatCount";
    public static final String ARG_USER = "User";

    private TextView txtToolbarTitle;
    private LinearLayout llBtnContainer;
    private TextView btnBack;
    private TextView btnSubmit;
    private LinearLayout llUiBlocker;
    private ProgressBar linearProgressBar;
    private LinearLayout progressContainer;
    private Submission submission;
    private User user;
    private String jsonFileName;
    private TextView txtScreenTitle;
    private Form form;
    private int repeatCount;
    private Receipts receipts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        llUiBlocker = findViewById(R.id.ll_ui_blocker);
        progressContainer = findViewById(R.id.ll_progress_container);
        txtToolbarTitle = findViewById(R.id.txt_toolbar_title);
        ImageView btnImgCancel = findViewById(R.id.btn_img_cancel);
        llBtnContainer = findViewById(R.id.ll_btn_container);
        btnBack = findViewById(R.id.btn_back);
        btnSubmit = findViewById(R.id.btn_submit);
        linearProgressBar = findViewById(R.id.linear_progress_bar);
        txtScreenTitle = findViewById(R.id.txt_screen_title);

        btnImgCancel.setOnClickListener(this);
        btnBack.setOnClickListener(this);
        btnSubmit.setOnClickListener(this);

        Intent intent = getIntent();
        submission = intent.getParcelableExtra(ARG_SUBMISSION);
        user = intent.getParcelableExtra(ARG_USER);
        repeatCount = intent.getIntExtra(ARG_REPEAT_COUNT, -1);
        if(intent.hasExtra(ARG_RECEIPT)){
            receipts = intent.getParcelableExtra(ARG_RECEIPT);
        }
        repeatCount = intent.getIntExtra(ARG_REPEAT_COUNT, -1);
        jsonFileName = submission.getJsonFileName();
        startFromFragment();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_img_cancel:
                finish();
                break;
            case R.id.btn_back:
                popFragmentImmediate();
                break;
            case R.id.btn_submit:

                FragmentManager fragmentManager = getSupportFragmentManager();
                FormFragment fragment = (FormFragment) fragmentManager.findFragmentById(R.id.container);
                if (fragment != null) {
                    if(receipts != null){
                        Answer answer = DBHandler.getInstance().getAnswer(submission.getID() ,"MyReceiptID" ,
                                null , 0);

                        if(answer == null) {
                            answer = new Answer(submission.getID(), "MyReceiptID");
                        }
                        answer.setShouldUpload(false);
                        answer.setRepeatID(null);
                        answer.setRepeatCount(0);
                        answer.setAnswer(receipts.getbatchRef());
                        DBHandler.getInstance().replaceData(Answer.DBTable.NAME , answer.toContentValues());
                        fragment.sendReceipts(receipts);
                    }else {
                        fragment.submit();
                    }
                }

                break;
        }
    }

    @Override
    public void onScreenChange(Screen screen) {
        int progress = screen.getIndex();
        if (form != null && form.isProgressVisible()) {
            txtScreenTitle.setText(screen.getTitle());
            linearProgressBar.setProgress(progress);
        }
        btnBack.setVisibility(progress == 0 ? View.GONE : View.VISIBLE);
        btnSubmit.setText(screen.isUpload() ? R.string.submit : R.string.next);
    }

    @Override
    public void addFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.setCustomAnimations(R.anim.fragment_open_from_right,
                R.anim.fragment_close_to_left, R.anim.fragment_open_from_left,
                R.anim.fragment_close_to_right);

        transaction.replace(R.id.container, fragment, fragment.getClass().getName());
        transaction.addToBackStack(fragment.getClass().getName());
        transaction.commit();
    }

    @Override
    public void showBtnContainer(boolean isVisible) {
        if (isVisible) {
            llBtnContainer.setVisibility(View.VISIBLE);
        } else {
            llBtnContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void setTitle(String title) {
        txtToolbarTitle.setText(title);
    }


    @Override
    public void showProgressBar() {
        llUiBlocker.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

    }

    @Override
    public void hideProgressBar() {
        llUiBlocker.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void startFromFragment() {
        form = JsonReader.loadForm(FormActivity.this, jsonFileName);

        addMyStoreItems(form);
        DBHandler.getInstance().replaceData(Submission.DBTable.NAME, submission.toContentValues());
        if (form.isProgressVisible()) {
            linearProgressBar.setMax(form.getScreens().size());
            progressContainer.setVisibility(View.VISIBLE);
        } else {
            progressContainer.setVisibility(View.GONE);
        }
        txtToolbarTitle.setText(form.getTitle());

        addFragment(FormFragment.newInstance(submission, user, form.getScreens().get(0), form.getTitle(), 0, repeatCount));
    }

    private void addMyStoreItems(Form form) {
        Intent getIntent = getIntent();
        if (!getIntent.hasExtra(ARG_MY_STORE_ITEMS)) {
            return;
        }

        HashMap<String, Object> map = (HashMap<String, Object>)
                (getIntent.getSerializableExtra(ARG_MY_STORE_ITEMS));
        if (map == null) {
            return;
        }

        String repeatId = "Items";
        if(submission.getJsonFileName().
                equalsIgnoreCase("store_log_issue.json")){
            repeatId = null;
        }


        Set<String> keys = map.keySet();
        int repeatCount = 0;

        ArrayList<FormItem> storeItems = new ArrayList<>();

        for (String key : keys) {
            if (!key.endsWith("_qty")) {
                FormItem fi = new FormItem("current_store", "", "StaStockItemId", repeatId, false);
                storeItems.add(fi);
                fi.setMyStore((MyStore) map.get(key));
                fi.setMyStoreQuantity((Integer) map.get(key + "_qty"));
                fi.setRepeatCount(repeatCount);
                repeatCount++;
            }
        }

        Screen screen = form.getScreens().get(0);
        ArrayList<FormItem> formItems = screen.getItems();
        formItems.addAll(0, storeItems);
    }


    @Override
    public void onChangeChamberCount(int chamberCount) {
        ArrayList<Screen> screens = form.getScreens();

        int count = screens.size() - 18;
        for (int i = 0; i < count; i++) {
            screens.remove(17);
        }

        Screen screen = screens.get(screens.size() - 1);
        screen.setIndex(chamberCount + 17);

        if (chamberCount != 0) {
            String[] suffix = new String[]{"st", "nd", "rd", "th"};
            ArrayList<Screen> newScreens = new ArrayList<>(chamberCount);
            for (int i = 0; i < chamberCount; i++) {
                Screen newScreen = JsonReader.loadScreen(this, "gas_chamber.json");
                String title = (i + 1) + (i > 2 ? suffix[3] : suffix[i]) + " chamber number";
                for (int j = 0; j < newScreen.getItems().size(); j++) {
                    newScreen.getItems().get(j).setRepeatCount(i);
                }
                newScreen.getItems().get(0).setTitle(title);
                newScreen.setIndex(17 + i);
                newScreens.add(newScreen);
            }
            screens.addAll(17, newScreens);
        }
    }

    @Override
    public void popFragmentImmediate() {
        onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == 1234) {
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        showBtnContainer(true);
        super.onBackPressed();
    }

    @Override
    public void goToNextScreen(int currentScreen) {
        if (currentScreen < form.getScreens().size() - 1) {
            addFragment(FormFragment.newInstance(submission, user, form.getScreens().get(currentScreen + 1), form.getTitle(), currentScreen + 1, repeatCount));
        }
    }


}
