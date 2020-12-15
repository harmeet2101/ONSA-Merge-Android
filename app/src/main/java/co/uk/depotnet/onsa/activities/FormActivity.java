package co.uk.depotnet.onsa.activities;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.appcompat.app.AppCompatActivity;

import android.os.PersistableBundle;
import android.text.TextUtils;
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
import co.uk.depotnet.onsa.modals.briefings.BriefingsDocument;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.Form;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.ToolTipModel;
import co.uk.depotnet.onsa.modals.schedule.Schedule;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.Receipts;
import co.uk.depotnet.onsa.utils.JsonReader;

public class FormActivity extends AppCompatActivity implements
        View.OnClickListener, FromActivityListener {


    public static final String ARG_SUBMISSION = "Submission";
    public static final String ARG_RECEIPT = "Receipt";
    public static final String ARG_MY_STORE_ITEMS = "MyStoreItems";
    public static final String ARG_REPEAT_COUNT = "repeatCount";
    public static final String ARG_SCHEDULE = "Schedule";
    public static final String ARG_DOCS = "docsread";
    public static final String ARG_QUESTIONS = "Questions";
    public static final String Doc_Recipient = "Doc_Recipient";
    private Toolbar toolbar;
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
    private Schedule schedule;
    private ArrayList<BriefingsDocument> briefingsDocument;
    public ArrayList<ToolTipModel> toolTipModels;
    private ArrayList<String> recipients;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_form);
        toolbar = findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);
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
        user = DBHandler.getInstance().getUser();
        repeatCount = intent.getIntExtra(ARG_REPEAT_COUNT, -1);
        if (intent.hasExtra(ARG_RECEIPT)) {
            receipts = intent.getParcelableExtra(ARG_RECEIPT);
        }
        repeatCount = intent.getIntExtra(ARG_REPEAT_COUNT, -1);
        jsonFileName = submission.getJsonFileName();
        schedule = intent.getParcelableExtra(ARG_SCHEDULE);
        toolTipModels = intent.getParcelableArrayListExtra(ARG_QUESTIONS);
        briefingsDocument = intent.getParcelableArrayListExtra(ARG_DOCS);
        recipients = intent.getStringArrayListExtra(Doc_Recipient);
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
                    if (receipts != null) {
                        Answer answer = DBHandler.getInstance().getAnswer(submission.getID(), "MyReceiptID",
                                null, 0);

                        if (answer == null) {
                            answer = new Answer(submission.getID(), "MyReceiptID");
                        }
                        answer.setShouldUpload(false);
                        answer.setRepeatID(null);
                        answer.setRepeatCount(0);
                        answer.setAnswer(receipts.getbatchRef());
                        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
                        fragment.sendReceipts(receipts);
                    } else {
                        fragment.submit();
                    }
                }
                break;
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
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

        if (toolTipModels != null && !toolTipModels.isEmpty()) {
            AddQuestions(form);
        }
        if (schedule != null) {
            addScheduleData(form);
        }
        if (briefingsDocument != null && briefingsDocument.size() > 0) {
            addBriefingsread(form);
        }
        try {
            if (!TextUtils.isEmpty(form.getThemeColor())) {
                int themeColor = Color.parseColor(form.getThemeColor());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    toolbar.setBackgroundColor(themeColor);
                    linearProgressBar.setProgressTintList(ColorStateList.valueOf(themeColor));
                    btnBack.setBackgroundTintList(ColorStateList.valueOf(themeColor));
                    btnSubmit.setBackgroundTintList(ColorStateList.valueOf(themeColor));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        DBHandler.getInstance().replaceData(Submission.DBTable.NAME, submission.toContentValues());
        if (form.isProgressVisible()) {
            linearProgressBar.setMax(form.getScreens().size());
            progressContainer.setVisibility(View.VISIBLE);
        } else {
            progressContainer.setVisibility(View.GONE);
        }
        txtToolbarTitle.setText(form.getTitle());

        addFragment(FormFragment.newInstance(submission, form.getScreens().get(0), form.getTitle(), 0, repeatCount, form.getThemeColor() ,schedule != null , recipients));
    }


    private void addScheduleData(Form form) {
        if (form == null) {
            return;
        }
        Screen screen = form.getScreens().get(0);
        ArrayList<FormItem> formItems = screen.getItems();
        for (FormItem fm : formItems) {
            Answer answer = DBHandler.getInstance().getAnswer(submission.getID(),
                    fm.getUploadId(),
                    null, 0);
            if (answer == null) {
                answer = new Answer(submission.getID(), fm.getUploadId());
            }

            if (formItems.get(0).getUploadId().equals(fm.getUploadId())) {
                answer.setAnswer(schedule.getJobEstimateNumber());
                answer.setDisplayAnswer(schedule.getJobEstimateNumber());
                answer.setRepeatID(null);
                answer.setRepeatCount(0);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
            }
            if (formItems.get(3).getUploadId().equals(fm.getUploadId())) {
                answer.setAnswer(schedule.getInspectionTemplateId());// upload value to server on behalf post param
                answer.setDisplayAnswer(schedule.getInspectionTemplateName());
                answer.setRepeatID(null); // always check if not
                answer.setRepeatCount(0);// always check
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
            }
        }
    }

    private void addBriefingsread(Form form) {
        if (form == null) {
            return;
        }
        Screen screen = form.getScreens().get(0);
        ArrayList<FormItem> formItems = screen.getItems().get(1).getDialogItems();
        if (formItems == null || formItems.isEmpty()) {
            return;
        }
        Answer answerItemId = DBHandler.getInstance().getAnswer(submission.getID(),
                formItems.get(1).getUploadId(),
                formItems.get(1).getRepeatId(), formItems.get(1).getRepeatCount());
        if (answerItemId == null) {
            answerItemId = new Answer(submission.getID(), formItems.get(1).getUploadId());
        }
        if (answerItemId != null) {
            int repeatCount = 0;
            for (BriefingsDocument document : briefingsDocument) {
                answerItemId.setAnswer(document.getBriefingId());
                answerItemId.setDisplayAnswer(document.getBriefingName());
                answerItemId.setRepeatID(formItems.get(1).getRepeatId());
                answerItemId.setRepeatCount(repeatCount);
                answerItemId.setIsMultiList(1);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answerItemId.toContentValues());
                repeatCount++;
            }
        }

    }

    private void AddQuestions(Form form) {
        if (form == null) {
            return;
        }
        int index = 0;
        ArrayList<Screen> screenArrayList = new ArrayList<>();
        for (ToolTipModel tipModel : toolTipModels) {
            Screen screen = new Screen();
            screen.setIndex(index);
            index++;
            screen.setTitle("SLG Questions");
            screen.setUpload(false);
            ArrayList<FormItem> formItems;
            //it will return all dialogitem of item
            formItems = JsonReader.loadAmends(FormActivity.this, "questions_item.json").getDialogItems();
            //FormItem(String type, String title, String uploadID, String repeatID, boolean optional)
            if (tipModel.getQuestionIsMandatory()) {
                formItems.get(0).setType("yes_no_tooltip");
            } else {
                formItems.get(0).setType("yes_no_na_tooltip");
            }
            formItems.get(0).setUploadId(tipModel.getQuestionId());
            formItems.get(0).setTitle(tipModel.getQuestionText());
            formItems.get(0).setToolTip(tipModel.getTooltip() == null ? "This question does not have a tool tip" : tipModel.getTooltip());
            formItems.get(0).setOptional(false);// !tipModel.getQuestionIsMandatory() imp to have que ans
            //form yes no repeat id is questions for getting data
            //yes option
            formItems.get(0).getEnables().get(0).setRepeatId(tipModel.getQuestionId());
            formItems.get(0).getEnables().get(0).setOptional(!tipModel.getCommentIsMandatory());
            formItems.get(0).getEnables().get(1).setUploadId(tipModel.getQuestionId());
            formItems.get(0).getEnables().get(1).setPhotoId(tipModel.getQuestionId());
            formItems.get(0).getEnables().get(1).setPhotoRequired(tipModel.getMinimumPhotoCount());
            // na options
            formItems.get(0).getFalseEnables().get(0).setRepeatId(tipModel.getQuestionId());
            // na checkbox true
            formItems.get(0).getFalseEnables().get(0).getEnables().get(0).setRepeatId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getEnables().get(1).setRepeatId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getEnables().get(2).setUploadId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getEnables().get(2).setPhotoId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getEnables().get(2).setPhotoRequired(tipModel.getMinimumPhotoCount());
            // na checkbox false
            formItems.get(0).getFalseEnables().get(0).getFalseEnables().get(0).setRepeatId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getFalseEnables().get(1).setRepeatId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getFalseEnables().get(2).setRepeatId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getFalseEnables().get(3).setUploadId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getFalseEnables().get(3).setPhotoId(tipModel.getQuestionId());
            formItems.get(0).getFalseEnables().get(0).getFalseEnables().get(3).setPhotoRequired(tipModel.getMinimumPhotoCount());
          /*  formItems.get(1).setUploadId(tipModel.getQuestionId());//setting photos uploadid to question
            formItems.get(1).setPhotoId(tipModel.getQuestionId());//setting photosid
            formItems.get(1).setPhotoRequired(tipModel.getMinimumPhotoCount());*/
            screen.setItems(formItems);//setting formitems
            screenArrayList.add(screen); //adding screen
        }
        form.getScreens().addAll(0, screenArrayList);//passing all screens
        form.getScreens().get(form.getScreens().size() - 1).setIndex(index);// setting last screen index
        form.setThemeColor("#243d4d");//setting theme
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
        if (submission.getJsonFileName().
                equalsIgnoreCase("store_log_issue.json")) {
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
            addFragment(FormFragment.newInstance(submission, form.getScreens().get(currentScreen + 1), form.getTitle(), currentScreen + 1, repeatCount, form.getThemeColor() , schedule !=null , recipients));
        }
    }


}
