package co.uk.depotnet.onsa.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;

import android.os.Build;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.FormActivity;
import co.uk.depotnet.onsa.activities.ListActivity;
import co.uk.depotnet.onsa.activities.ListEditActivity;
import co.uk.depotnet.onsa.activities.ListStockItemActivity;
import co.uk.depotnet.onsa.activities.ListStoreItemActivity;
import co.uk.depotnet.onsa.activities.ThemeBaseActivity;
import co.uk.depotnet.onsa.barcode.ScannedBarcodeActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.SimpleCalendarDialogFragment;
import co.uk.depotnet.onsa.dialogs.ToolTipDialog;
import co.uk.depotnet.onsa.formholders.BoldTextHolder;
import co.uk.depotnet.onsa.formholders.BriefingSignHolder;
import co.uk.depotnet.onsa.formholders.Briefingtextholder;
import co.uk.depotnet.onsa.formholders.CalenderHolder;
import co.uk.depotnet.onsa.formholders.CheckBoxHolder;
import co.uk.depotnet.onsa.formholders.CurrentStoreHolder;
import co.uk.depotnet.onsa.formholders.DFEItemHolder;
import co.uk.depotnet.onsa.formholders.DateTimeHolder;
import co.uk.depotnet.onsa.formholders.DayViewHolder;
import co.uk.depotnet.onsa.formholders.DigMeasureItemHolder;
import co.uk.depotnet.onsa.formholders.DropDownHolder;
import co.uk.depotnet.onsa.formholders.DropDownNumber;
import co.uk.depotnet.onsa.formholders.EstimateSearchHolder;
import co.uk.depotnet.onsa.formholders.ForkCardHolder;
import co.uk.depotnet.onsa.formholders.ForkHolder;
import co.uk.depotnet.onsa.formholders.LocationHolder;
import co.uk.depotnet.onsa.formholders.LogDayHoursViewHolder;
import co.uk.depotnet.onsa.formholders.LogDigBackFillItemHolder;
import co.uk.depotnet.onsa.formholders.LogDigForkHolder;
import co.uk.depotnet.onsa.formholders.LogMeasureItemHolder;
import co.uk.depotnet.onsa.formholders.LogMuckAwayHolder;
import co.uk.depotnet.onsa.formholders.LogReinstatementItemHolder;
import co.uk.depotnet.onsa.formholders.LogServiceHolder;
import co.uk.depotnet.onsa.formholders.LongTextHolder;
import co.uk.depotnet.onsa.formholders.NumberHolder;
import co.uk.depotnet.onsa.formholders.PassFailHolder;
import co.uk.depotnet.onsa.formholders.PhotoHolder;
import co.uk.depotnet.onsa.formholders.RFNAToggleHolder;
import co.uk.depotnet.onsa.formholders.RepeatItemHolder;
import co.uk.depotnet.onsa.formholders.RiskElementHolder;
import co.uk.depotnet.onsa.formholders.ShortTextHolder;
import co.uk.depotnet.onsa.formholders.SignatureHolder;
import co.uk.depotnet.onsa.formholders.SiteClearHolder;
import co.uk.depotnet.onsa.formholders.StockItemHolder;
import co.uk.depotnet.onsa.formholders.StopWatchHolder;
import co.uk.depotnet.onsa.formholders.StoreHolder;
import co.uk.depotnet.onsa.formholders.SwitchHolder;
import co.uk.depotnet.onsa.formholders.TakePhotoHolder;
import co.uk.depotnet.onsa.formholders.TimePickerHolder;
import co.uk.depotnet.onsa.formholders.TimeTotalWorkedHoursHolder;
import co.uk.depotnet.onsa.formholders.VisitorSignHolder;
import co.uk.depotnet.onsa.formholders.YesNoHolder;
import co.uk.depotnet.onsa.formholders.YesNoNAHolder;
import co.uk.depotnet.onsa.formholders.YesNoNAToolTipHolder;
import co.uk.depotnet.onsa.formholders.YesNoToolTipHolder;
import co.uk.depotnet.onsa.fragments.FragmentStopWork;
import co.uk.depotnet.onsa.listeners.DropDownCalls;
import co.uk.depotnet.onsa.listeners.DropDownDataListner;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.FromActivityListener;
import co.uk.depotnet.onsa.listeners.LocationListener;
import co.uk.depotnet.onsa.listeners.OnChangeChamberCount;
import co.uk.depotnet.onsa.listeners.PhotoAdapterListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.LogMeasureForkItem;
import co.uk.depotnet.onsa.modals.Notice;
import co.uk.depotnet.onsa.modals.User;
import co.uk.depotnet.onsa.modals.forms.Amends;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.hseq.OperativeTemplate;
import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.schedule.JobEstimate;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.modals.timesheet.LogHourItem;
import co.uk.depotnet.onsa.modals.timesheet.LogHourTime;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHour;
import co.uk.depotnet.onsa.modals.timesheet.TimeSheetHours;
import co.uk.depotnet.onsa.modals.timesheet.TimesheetOperative;
import co.uk.depotnet.onsa.networking.APICalls;
import co.uk.depotnet.onsa.networking.CallUtils;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.AppPreferences;
import co.uk.depotnet.onsa.utils.JsonReader;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.views.DropdownMenu;
import co.uk.depotnet.onsa.views.DropdownNumberBottomSheet;
import co.uk.depotnet.onsa.views.DropdownTimer;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements PhotoAdapterListener, AdapterLogMeasure.AdapterListener {

    private final Context context;
    private final Screen screen;
    private final List<FormItem> formItems;
    private final List<FormItem> originalItems;
    private final FormAdapterListener listener;
    private final OnChangeChamberCount changeChamberListener;
    private final long submissionID;
    private int repeatCount;
    private final Submission submission;
    private EditText focusedEditText;
    private boolean missingAnswerMode;
    private final GradientDrawable redBG;
    private boolean isTimerRunning;
    private final List<BaseTask> selectableTasks;
    private boolean estFlag = true;
    private String estimateGangId;
    private final String themeColor;
    private final boolean isScheduledInspection;
    private ArrayList<Notice> notices;
    private final DBHandler dbHandler;
    private Job job;


    public FormAdapter(Context context, Submission submission,
                       Screen screen, String themeColor, boolean isScheduledInspection, FormAdapterListener listener, OnChangeChamberCount changeChamberListener) {
        this.context = context;
        this.screen = screen;
        this.listener = listener;
        this.changeChamberListener = changeChamberListener;
        this.originalItems = screen.getItems();
        this.submission = submission;
        this.submissionID = submission.getID();
        this.themeColor = themeColor;
        this.isScheduledInspection = isScheduledInspection;
        this.formItems = new ArrayList<>();
        this.selectableTasks = new ArrayList<>();
        this.job = DBHandler.getInstance().getJob(submission.getJobID());

        redBG = new GradientDrawable();
        redBG.setColor(Color.parseColor("#e24444"));
        this.dbHandler = DBHandler.getInstance();

        getNotices();
        reInflateItems(false);
    }


    private void addRepeatedVisitorSignature(ArrayList<FormItem> formItems) {
        this.formItems.clear();
        ArrayList<FormItem> listItems = new ArrayList<>();
        for (int c = 0; c < formItems.size(); c++) {
            FormItem item = formItems.get(c);
            this.formItems.add(item);

            if (item.getFormType() == FormItem.TYPE_FORK) {
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                    if (answers != null) {
                        for (int i = 0; i < answers.size(); i++) {
                            repeatCount = answers.get(i).getRepeatCount();
                            FormItem qItem = new FormItem("visitor_signature", "", "", "", true);
                            qItem.setFields(fields);
                            qItem.setRepeatId(item.getRepeatId());
                            qItem.setRepeatCount(repeatCount);
                            listItems.add(qItem);
                        }
                    }
                }
            }
        }

        this.formItems.addAll(listItems);
        notifyDataSetChanged();

        if (!listItems.isEmpty()) {
            repeatCount++;
        }
    }

    private void addStockItems(ArrayList<FormItem> inflatedItems) {

        formItems.clear();
        int forkPosition = 0;

        ArrayList<FormItem> listItems = new ArrayList<>();
        for (int c = 0; c < inflatedItems.size(); c++) {
            FormItem item = inflatedItems.get(c);
            formItems.add(item);

            if (item.getFormType() == FormItem.TYPE_ADD_STORE_ITEM) {
                forkPosition = c;
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                    if (answers != null) {
                        for (int i = 0; i < answers.size(); i++) {
                            repeatCount = answers.get(i).getRepeatCount();
                            FormItem qItem = new FormItem(item.getListItemType(), "", "", item.getRepeatId(), true);
                            qItem.setRepeatCount(repeatCount);
                            listItems.add(qItem);
                        }
                    }
                }
            }
        }

        formItems.addAll(forkPosition, listItems);
        notifyDataSetChanged();

    }


    private void addListItems(ArrayList<FormItem> inflatedItems) {
        formItems.clear();
        int forkPosition;
        boolean ifItemAdded = false;
        boolean ifPosDFEAdded = false;
        boolean ifNegDFEAdded = false;
        boolean isLogMeasureAdded = false;
        boolean isDigMeasureAdded = false;
        boolean isLogHoursAdded = false;
        boolean isSiteClearAdded = false;
        int repeatCount;
        ArrayList<FormItem> listItems = new ArrayList<>();
        for (int c = 0; c < inflatedItems.size(); c++) {
            FormItem item = inflatedItems.get(c);
            formItems.add(item);

            if (!ifItemAdded && item.getFormType() == FormItem.TYPE_FORK_CARD) {
                forkPosition = c;
                ifItemAdded = true;
                listItems.clear();
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                    if (answers != null) {
                        for (int i = 0; i < answers.size(); i++) {
                            repeatCount = answers.get(i).getRepeatCount();
                            FormItem qItem = new FormItem(item.getListItemType(), "", "", item.getRepeatId(), true);
                            qItem.setFields(fields);
                            qItem.setDialogItems(item.getDialogItems());
                            qItem.setRepeatCount(repeatCount);
                            listItems.add(qItem);
                        }
                        formItems.addAll(forkPosition, listItems);
                    }
                }
            } else if (!ifPosDFEAdded && item.getFormType() == FormItem.TYPE_ADD_POS_DFE) {
                forkPosition = formItems.size() - 1;
                ifPosDFEAdded = true;
                listItems.clear();
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                    if (answers != null) {
                        for (int i = 0; i < answers.size(); i++) {
                            repeatCount = answers.get(i).getRepeatCount();
                            FormItem qItem = new FormItem(item.getListItemType(), "", "", item.getRepeatId(), true);
                            qItem.setFields(fields);
                            qItem.setDialogItems(item.getDialogItems());
                            qItem.setRepeatCount(repeatCount);
                            listItems.add(qItem);
                        }
                        formItems.addAll(forkPosition, listItems);
                    }
                }
            } else if (!ifNegDFEAdded && item.getFormType() == FormItem.TYPE_ADD_NEG_DFE) {
                forkPosition = formItems.size() - 1;
                ifNegDFEAdded = true;
                listItems.clear();
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                    if (answers != null) {
                        for (int i = 0; i < answers.size(); i++) {
                            repeatCount = answers.get(i).getRepeatCount();
                            FormItem qItem = new FormItem(item.getListItemType(), "", "", item.getRepeatId(), true);
                            qItem.setFields(fields);
                            qItem.setDialogItems(item.getDialogItems());
                            qItem.setRepeatCount(repeatCount);
                            listItems.add(qItem);
                        }
                        formItems.addAll(forkPosition, listItems);
                    }
                }
            } else if (!isLogMeasureAdded && item.getFormType() == FormItem.TYPE_ADD_LOG_MEASURE) {
                isLogMeasureAdded = true;
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(1), item.getRepeatId());

                    ArrayList<FormItem> toBeAdded = new ArrayList<>();
                    if (answers != null && !answers.isEmpty()) {
                        for (int i = 0; i < answers.size(); i++) {
                            FormItem qItem = new FormItem("item_log_measure", "", "", item.getRepeatId(), true);
                            qItem.setFields(fields);
                            qItem.setRepeatCount(answers.get(i).getRepeatCount());
                            qItem.setDialogItems(item.getDialogItems());
                            toBeAdded.add(qItem);
                        }

                        formItems.addAll(c, toBeAdded);
                    }

                }
            } else if (!isDigMeasureAdded && item.getFormType() == FormItem.TYPE_ADD_DIG_MEASURE) {


                isDigMeasureAdded = true;
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());

                    ArrayList<FormItem> toBeAdded = new ArrayList<>();
                    if (answers != null && !answers.isEmpty()) {
                        for (int i = 0; i < answers.size(); i++) {
                            FormItem qItem = new FormItem("item_dig_measure", "", "", item.getRepeatId(), true);
                            qItem.setFields(fields);
                            qItem.setRepeatCount(answers.get(i).getRepeatCount());
                            qItem.setDialogItems(item.getDialogItems());
                            toBeAdded.add(qItem);
                        }

                        formItems.addAll(c, toBeAdded);
                    }


                }
            } else if (!isLogHoursAdded && item.getFormType() == FormItem.TYPE_ADD_LOG_HOURS) {
                isLogHoursAdded = true;
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());

                    ArrayList<FormItem> toBeAdded = new ArrayList<>();
                    if (answers != null && !answers.isEmpty()) {
                        for (int i = 0; i < answers.size(); i++) {
                            FormItem qItem = new FormItem("log_hours_item", "", "", item.getRepeatId(), true);
                            qItem.setFields(fields);
                            qItem.setRepeatCount(answers.get(i).getRepeatCount());
                            qItem.setDialogItems(item.getDialogItems());
                            toBeAdded.add(qItem);
                        }

                        formItems.addAll(c, toBeAdded);
                    }
                }
            }else if (!isSiteClearAdded && item.getFormType() == FormItem.TYPE_ADD_SITE_CLEAR) {
                isSiteClearAdded = true;
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());

                    ArrayList<FormItem> toBeAdded = new ArrayList<>();
                    if (answers != null && !answers.isEmpty()) {
                        for (int i = 0; i < answers.size(); i++) {
                            FormItem qItem = new FormItem("list_site_clear_item", "", "", item.getRepeatId(), true);
                            qItem.setFields(fields);
                            qItem.setRepeatCount(answers.get(i).getRepeatCount());
                            qItem.setDialogItems(item.getDialogItems());
                            toBeAdded.add(qItem);
                        }

                        formItems.addAll(c, toBeAdded);
                    }
                }
            }
        }
        notifyDataSetChanged();

    }


    private ArrayList<LogMeasureForkItem> getLogRepeatedItems(FormItem item) {

        ArrayList<LogMeasureForkItem> items = new ArrayList<>();
        ArrayList<String> fields = item.getFields();
        if (fields != null) {
            JsonArray jsonArray = new JsonArray();
            ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0),
                    item.getRepeatId());
            if (answers != null && !answers.isEmpty()) {
                for (int i = 0; i < answers.size(); i++) {
                    String repeatId = answers.get(i).getRepeatID();
                    int repeatCount = answers.get(i).getRepeatCount();

                    JsonObject jsonObject = new JsonObject();
                    for (int k = 0; k < fields.size(); k++) {
                        String field = fields.get(k);

                        Answer answer = dbHandler.getAnswer(submissionID, field,
                                repeatId, repeatCount);

                        if (answer != null) {
                            jsonObject.addProperty(field,
                                    answer.getDisplayAnswer());
                        }
                    }
                    jsonArray.add(jsonObject);
                }


                ArrayList<LogMeasureForkItem> logs = new Gson().fromJson(jsonArray,
                        new TypeToken<List<LogMeasureForkItem>>() {
                        }.getType());
                if (logs != null) {
                    items.addAll(logs);
                }
            }
        }

        if (repeatCount != 0) {
            repeatCount++;
        }

        return items;
    }

    private void getNotices() {
        if (notices == null) {
            notices = new ArrayList<>();
        }
        notices.clear();
        notices.addAll(dbHandler.getNotices(submission.getJobID()));
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {

        switch (type) {
            case FormItem.TYPE_YES_NO:
                return new YesNoHolder(LayoutInflater.from(context).inflate(R.layout.item_yes_no, viewGroup, false));
            case FormItem.TYPE_ET_LONG_TEXT:
                return new LongTextHolder(LayoutInflater.from(context).inflate(R.layout.item_et_long_text, viewGroup, false));
            case FormItem.TYPE_ET_SHORT_TEXT:
                return new ShortTextHolder(LayoutInflater.from(context).inflate(R.layout.item_et_short_text, viewGroup, false));
            case FormItem.TYPE_TXT_BOLD_HEAD:
                return new BoldTextHolder(LayoutInflater.from(context).inflate(R.layout.item_txt_bold_head, viewGroup, false));
            case FormItem.TYPE_CHECKBOX:
                return new CheckBoxHolder(LayoutInflater.from(context).inflate(R.layout.item_checkbox, viewGroup, false));
            case FormItem.TYPE_DROPDOWN:
            case FormItem.TYPE_DIALOG_SCREEN:
            case FormItem.TYPE_TIME_SELECTOR_ITEM:

            case FormItem.TYPE_ADD_STORE_ITEM:
            case FormItem.TYPE_BAR_CODE:
                return new DropDownHolder(LayoutInflater.from(context).inflate(R.layout.item_dropdown, viewGroup, false));
            case FormItem.TYPE_SIGNATURE:
                return new SignatureHolder(LayoutInflater.from(context).inflate(R.layout.item_signature, viewGroup, false));
            case FormItem.TYPE_FORK:
                return new ForkHolder(LayoutInflater.from(context).inflate(R.layout.item_fork, viewGroup, false));
            case FormItem.TYPE_PHOTO:
                return new PhotoHolder(LayoutInflater.from(context).inflate(R.layout.item_form_photo, viewGroup, false));
            case FormItem.TYPE_REPEAT_ITEM:
                return new RepeatItemHolder(LayoutInflater.from(context).inflate(R.layout.item_repeat, viewGroup, false));
            case FormItem.TYPE_PASS_FAIL:
                return new PassFailHolder(LayoutInflater.from(context).inflate(R.layout.item_pass_fail, viewGroup, false));
            case FormItem.TYPE_SWITCH:
                return new SwitchHolder(LayoutInflater.from(context).inflate(R.layout.item_switch_layout, viewGroup, false));
            case FormItem.TYPE_NUMBER:
                return new NumberHolder(LayoutInflater.from(context).inflate(R.layout.item_et_number, viewGroup, false));
            case FormItem.TYPE_DROPDOWN_NUMBER:
                return new DropDownNumber(LayoutInflater.from(context).inflate(R.layout.item_dropdown, viewGroup, false));
            case FormItem.TYPE_LOCATION:
                return new LocationHolder(LayoutInflater.from(context).inflate(R.layout.item_location, viewGroup, false));
            case FormItem.TYPE_STORE_ITEM:
                return new StoreHolder(LayoutInflater.from(context).inflate(R.layout.item_store, viewGroup, false));
            case FormItem.TYPE_FORK_CARD:
            case FormItem.TYPE_ADD_NEG_DFE:
            case FormItem.TYPE_ADD_POS_DFE:
            case FormItem.TYPE_ADD_LOG_MEASURE:
            case FormItem.TYPE_ADD_DIG_MEASURE:
            case FormItem.TYPE_ADD_LOG_HOURS:
            case FormItem.TYPE_OPEN_LOG_HOURS:
            case FormItem.TYPE_ADD_SITE_CLEAR:
                return new ForkCardHolder(LayoutInflater.from(context).inflate(R.layout.item_fork_card, viewGroup, false));
            case FormItem.TYPE_CALENDER:
            case FormItem.TYPE_ITEM_WEEK_COMMENCING:
                return new CalenderHolder(LayoutInflater.from(context).inflate(R.layout.item_calender, viewGroup, false));
            case FormItem.TYPE_DFE_ITEM:
                return new DFEItemHolder(LayoutInflater.from(context).inflate(R.layout.item_dfe, viewGroup, false));
            case FormItem.TYPE_YES_NO_NA:
            case FormItem.TYPE_YES_NO_NA_OPTIONAL:
                return new YesNoNAHolder(LayoutInflater.from(context).inflate(R.layout.item_yes_no_na, viewGroup, false));
            case FormItem.TYPE_STOP_WATCH:
                return new StopWatchHolder(LayoutInflater.from(context).inflate(R.layout.item_stop_watch, viewGroup, false));
            case FormItem.TYPE_VISITOR_SIGN:
                return new VisitorSignHolder(LayoutInflater.from(context).inflate(R.layout.item_visitor_sign, viewGroup, false));
            case FormItem.TYPE_DATE_TIME:
                return new DateTimeHolder(LayoutInflater.from(context).inflate(R.layout.item_date_time, viewGroup, false));
            case FormItem.TYPE_RISK_ELEMENT:
                return new RiskElementHolder(LayoutInflater.from(context).inflate(R.layout.item_risk_element, viewGroup, false));
            case FormItem.TYPE_TAKE_PHOTO:
                return new TakePhotoHolder(LayoutInflater.from(context).inflate(R.layout.item_form_take_photo, viewGroup, false));
            case FormItem.TYPE_CURRENT_STORE:
                return new CurrentStoreHolder(LayoutInflater.from(context).inflate(R.layout.item_form_current_store, viewGroup, false));
            case FormItem.TYPE_STOCK_ITEM:
                return new StockItemHolder(LayoutInflater.from(context).inflate(R.layout.item_form_stock_item, viewGroup, false));
            case FormItem.TYPE_LOG_MEASURE:
                return new LogMeasureItemHolder(LayoutInflater.from(context).inflate(R.layout.item_log_measure_repeated, viewGroup, false));
            case FormItem.TYPE_LIST_DIG_MEASURE:
                return new DigMeasureItemHolder(LayoutInflater.from(context).inflate(R.layout.item_dig_measure_repeated, viewGroup, false));
            case FormItem.TYPE_TASK_LOG_BACKFILL_ITEM:
                return new LogDigBackFillItemHolder(LayoutInflater.from(context).inflate(R.layout.item_log_task, viewGroup, false));
            case FormItem.TYPE_LOG_REINSTATEMENT_ITEM:
                return new LogReinstatementItemHolder(LayoutInflater.from(context).inflate(R.layout.item_reinstatement_task, viewGroup, false));
            case FormItem.TYPE_TASK_LOG_MUCKAWAY_ITEM:
                return new LogMuckAwayHolder(LayoutInflater.from(context).inflate(R.layout.item_log_muckaway, viewGroup, false));
            case FormItem.TYPE_TASK_LOG_SERVICE_ITEM:
            case FormItem.TYPE_TASK_SITE_CLEAR_ITEM:
                return new LogServiceHolder(LayoutInflater.from(context).inflate(R.layout.item_service_material, viewGroup, false));
            case FormItem.TYPE_LIST_SITE_CLEAR_ITEM:
                return new SiteClearHolder(LayoutInflater.from(context).inflate(R.layout.item_list_site_clear, viewGroup, false));
            case FormItem.TYPE_SIGN_BRIEFING:
                return new BriefingSignHolder(LayoutInflater.from(context).inflate(R.layout.item_briefing_signs, viewGroup, false));
            case FormItem.TYPE_TV_BRIEFING_TEXT:
                return new Briefingtextholder(LayoutInflater.from(context).inflate(R.layout.briefings_read_item, viewGroup, false));
            case FormItem.TYPE_YES_NO_tooltip:
                return new YesNoToolTipHolder(LayoutInflater.from(context).inflate(R.layout.item_yes_no_tooltip, viewGroup, false));
            case FormItem.TYPE_YES_NO_NA_tooltip:
                return new YesNoNAToolTipHolder(LayoutInflater.from(context).inflate(R.layout.item_yes_no_na_tooltip, viewGroup, false));
            case FormItem.TYPE_ET_SEARCH_ESTIMATE:
                return new EstimateSearchHolder(LayoutInflater.from(context).inflate(R.layout.estimate_search_item, viewGroup, false));
            case FormItem.TYPE_RFNA_TOGGLE:
                return new RFNAToggleHolder(LayoutInflater.from(context).inflate(R.layout.item_rfna_toggle, viewGroup, false));
            case FormItem.TYPE_ITEM_LOG_HOURS:
            case FormItem.TYPE_TASK_TIMESHEET_ITEM:
                return new LogDayHoursViewHolder(LayoutInflater.from(context).inflate(R.layout.item_day_log_hours, viewGroup, false));
            case FormItem.TYPE_TOTAL_WORKED_HOURS:
                return new TimeTotalWorkedHoursHolder(LayoutInflater.from(context).inflate(R.layout.item_total_worked_hour, viewGroup, false));
            case FormItem.TYPE_ITEM_TIME_PICKER:
                return new TimePickerHolder(LayoutInflater.from(context).inflate(R.layout.item_time_picker, viewGroup, false));
            case FormItem.TYPE_ITEM_DAY:
                return new DayViewHolder(LayoutInflater.from(context).inflate(R.layout.item_day_view, viewGroup, false));
        }
        return new BoldTextHolder(LayoutInflater.from(context).inflate(R.layout.item_txt_bold_head, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        FormItem formItem = formItems.get(position);
        switch (formItem.getFormType()) {
            case FormItem.TYPE_YES_NO:
                bindYesNoItem((YesNoHolder) holder, position);
                break;
            case FormItem.TYPE_ET_LONG_TEXT:
                bindLongTextHolder((LongTextHolder) holder, position);
                break;
            case FormItem.TYPE_ET_SHORT_TEXT:
                bindShortTextHolder((ShortTextHolder) holder, position);
                break;
            case FormItem.TYPE_TXT_BOLD_HEAD:
                bindBoldTextHolder((BoldTextHolder) holder, position);
                break;
            case FormItem.TYPE_CHECKBOX:
                bindCheckBox((CheckBoxHolder) holder, position);
                break;
            case FormItem.TYPE_DROPDOWN:
                bindDropDownHolder((DropDownHolder) holder, position);
                break;
            case FormItem.TYPE_DIALOG_SCREEN:
                bindDialogScreen((DropDownHolder) holder, position);
                break;
            case FormItem.TYPE_TIME_SELECTOR_ITEM:
                bindTimeDropDownHolder((DropDownHolder) holder, position);
                break;
            case FormItem.TYPE_SIGNATURE:
                bindSignatureHolder((SignatureHolder) holder, position);
                break;
            case FormItem.TYPE_FORK:
                bindForkHolder((ForkHolder) holder, position);
                break;
            case FormItem.TYPE_PHOTO:
                bindPhotoHolder((PhotoHolder) holder, position);
                break;
            case FormItem.TYPE_REPEAT_ITEM:
                bindRepeatItemHolder((RepeatItemHolder) holder, position);
                break;
            case FormItem.TYPE_PASS_FAIL:
                bindPassFailHolder((PassFailHolder) holder, position);
                break;
            case FormItem.TYPE_SWITCH:
                bindSwitchHolder((SwitchHolder) holder, position);
                break;
            case FormItem.TYPE_LOG_AND_DIG_FORK:
                bindLogAndDigForkHolder((LogDigForkHolder) holder, position);
                break;
            case FormItem.TYPE_NUMBER:
                bindNumberHolder((NumberHolder) holder, position);
                break;
            case FormItem.TYPE_DROPDOWN_NUMBER:
            case FormItem.TYPE_TASK_SITE_CLEAR:
                bindDropDownNumber((DropDownNumber) holder, position);
                break;
            case FormItem.TYPE_LOCATION:
                bindLocation((LocationHolder) holder, position);
                break;
            case FormItem.TYPE_STORE_ITEM:
                bindStoreItem((StoreHolder) holder, position);
                break;
            case FormItem.TYPE_FORK_CARD:
            case FormItem.TYPE_ADD_NEG_DFE:
            case FormItem.TYPE_ADD_POS_DFE:
            case FormItem.TYPE_ADD_LOG_MEASURE:
            case FormItem.TYPE_ADD_DIG_MEASURE:
            case FormItem.TYPE_ADD_LOG_HOURS:
            case FormItem.TYPE_ADD_SITE_CLEAR:
                bindForkCard((ForkCardHolder) holder, position);
                break;
            case FormItem.TYPE_OPEN_LOG_HOURS:
                openLogHours((ForkCardHolder) holder, position);
                break;
            case FormItem.TYPE_CALENDER:
                bindCalender((CalenderHolder) holder, position);
                break;
            case FormItem.TYPE_ITEM_WEEK_COMMENCING:
                bindWeekCommencing((CalenderHolder) holder, position);
                break;
            case FormItem.TYPE_DFE_ITEM:
                bindDFEHolder((DFEItemHolder) holder, position);
                break;
            case FormItem.TYPE_YES_NO_NA:
                bindYesNoNAHolder((YesNoNAHolder) holder, position);
                break;
            case FormItem.TYPE_YES_NO_NA_OPTIONAL:
                bindYesNoNAOptionalHolder((YesNoNAHolder) holder, position);
                break;
            case FormItem.TYPE_STOP_WATCH:
                bindStopWatchHolder((StopWatchHolder) holder, position);
                break;
            case FormItem.TYPE_VISITOR_SIGN:
                bindVisitorSignHolder((VisitorSignHolder) holder, position);
                break;
            case FormItem.TYPE_DATE_TIME:
                bindDateTimeHolder((DateTimeHolder) holder, position);
                break;
            case FormItem.TYPE_RISK_ELEMENT:
                bindRiskElementHolder((RiskElementHolder) holder, position);
                break;
            case FormItem.TYPE_TAKE_PHOTO:
                bindTakePhotoHolder((TakePhotoHolder) holder, position);
                break;
            case FormItem.TYPE_CURRENT_STORE:
                bindCurrentStoreHolder((CurrentStoreHolder) holder, position);
                break;
            case FormItem.TYPE_ADD_STORE_ITEM:
                bindAddStoreItem((DropDownHolder) holder, position);
                break;
            case FormItem.TYPE_STOCK_ITEM:
                bindStockItem((StockItemHolder) holder, position);
                break;
            case FormItem.TYPE_BAR_CODE:
                bindBarCodeItem((DropDownHolder) holder, position);
                break;
            case FormItem.TYPE_LOG_MEASURE:
                bindLogMeasureItemHolder((LogMeasureItemHolder) holder, position);
                break;
            case FormItem.TYPE_LIST_DIG_MEASURE:
                bindDigMeasureItemHolder((DigMeasureItemHolder) holder, position);
                break;
            case FormItem.TYPE_TASK_LOG_BACKFILL_ITEM:
                bindLogFillItemHolder((LogDigBackFillItemHolder) holder, position);
                break;
            case FormItem.TYPE_LOG_REINSTATEMENT_ITEM:
                bindReinstatementItemHolder((LogReinstatementItemHolder) holder, position);
                break;
            case FormItem.TYPE_TASK_LOG_MUCKAWAY_ITEM:
                bindMuckAwayHolder((LogMuckAwayHolder) holder, position);
                break;
            case FormItem.TYPE_TASK_LOG_SERVICE_ITEM:
                bindServiceMaterialHolder((LogServiceHolder) holder, position);
                break;
            case FormItem.TYPE_TASK_SITE_CLEAR_ITEM:
                bindSiteClearScheduledHolder((LogServiceHolder) holder, position);
                break;
            case FormItem.TYPE_LIST_SITE_CLEAR_ITEM:
                bindSiteClearItemHolder((SiteClearHolder) holder, position);
                break;
            case FormItem.TYPE_SIGN_BRIEFING:
                bindBRIEFINGSignHolder((BriefingSignHolder) holder, position);
                break;
            case FormItem.TYPE_TV_BRIEFING_TEXT:
                bindBRIEFINGTEXTHolder((Briefingtextholder) holder, position);
                break;
            case FormItem.TYPE_YES_NO_tooltip:
                bindYesNoToolTipHolder((YesNoToolTipHolder) holder, position);
                break;
            case FormItem.TYPE_YES_NO_NA_tooltip:
                bindYesNoNAToolTipHolder((YesNoNAToolTipHolder) holder, position);
                break;
            case FormItem.TYPE_ET_SEARCH_ESTIMATE:
                bindEstimateSearchHolder((EstimateSearchHolder) holder, position);
                break;
            case FormItem.TYPE_RFNA_TOGGLE:
                bindRFNAToggleHolder((RFNAToggleHolder) holder, position);
                break;
            case FormItem.TYPE_ITEM_LOG_HOURS:
                bindLogHoursHolder((LogDayHoursViewHolder) holder, position);
                break;
            case FormItem.TYPE_TASK_TIMESHEET_ITEM:
                bindTimeSheetHolder((LogDayHoursViewHolder) holder, position);
                break;
            case FormItem.TYPE_TOTAL_WORKED_HOURS:
                bindTotalWorkedHourHolder((TimeTotalWorkedHoursHolder) holder, position);
                break;
            case FormItem.TYPE_ITEM_TIME_PICKER:
                bindTimePickerHolder((TimePickerHolder) holder, position);
                break;
            case FormItem.TYPE_ITEM_DAY:
                bindDayViewHolder((DayViewHolder) holder, position);
                break;
        }
    }

    private void bindDayViewHolder(final DayViewHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        LogHourItem logHourItem = formItem.getTimeSheetHour();
        holder.txtTitle.setText(formItem.getTitle());
        String status = dbHandler.getTimeSheetsStatus(logHourItem.getWeekCommencing());
        holder.setColor(status);
    }

    private void bindTimePickerHolder(final TimePickerHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtTime.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.txtTime.setOnClickListener(v -> {
            final Calendar myCalendar = Calendar.getInstance();

            TimePickerDialog.OnTimeSetListener date = (view, hourOfDay, minute) -> {
                Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);

                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);


                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
                String time = sdf.format(myCalendar.getTime());
                holder.txtTime.setText(time);


                if (answer12 == null) {
                    answer12 = new Answer(submissionID, formItem.getUploadId());
                }

                answer12.setAnswer(time);
                answer12.setDisplayAnswer(time);
                answer12.setRepeatID(formItem.getRepeatId());
                answer12.setRepeatCount(repeatCount);

                dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            };


            new TimePickerDialog(context, date, myCalendar
                    .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                    true).show();

        });
    }

    private void bindTotalWorkedHourHolder(TimeTotalWorkedHoursHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTime.setText(formItem.getTitle());
    }

    private void bindEstimateSearchHolder(EstimateSearchHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.editText.setHint(formItem.getHint());
        holder.editText.setText("");
        holder.editText.setFilters(new InputFilter[]{new InputFilter.AllCaps()});
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {

            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.editText.setText(value);
                if (estFlag) {
                    // position is imp to update items list particular  value.
                    listener.getEstimateOperative(value.trim(), 2 , false);
                    estFlag = false;
                }
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }
        holder.editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                EditText et = (EditText) view;
//                if(!TextUtils.isEmpty(et.getText().toString())) {
                    Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                    if (answer1 == null) {
                        answer1 = new Answer(submissionID, formItem.getUploadId());
                    }
                    if (answer1.getUploadID() != null &&
                            answer1.getUploadID().equalsIgnoreCase("visitorName")) {
                        if(submission.getJsonFileName().equalsIgnoreCase("visitor_attendance.json")) {
                            answer1.setIsMultiList(1);
                        }
                    }

                    answer1.setAnswer(et.getText().toString());
                    answer1.setRepeatID(formItem.getRepeatId());
                    answer1.setRepeatCount(repeatCount);
                    dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
//                }

                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });
        holder.editText.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                EditText et = (EditText) v;
                if (!TextUtils.isEmpty(et.getText().toString())) { // position is imp to update items list particular  value.
                    listener.getEstimateOperative(et.getText().toString().trim(), 2 , false);
                }
                return true;
            }
            return false;
        });
        holder.btn_estimate_search.setOnClickListener(v -> {
            String seatxt = holder.editText.getText().toString().trim();
            if (!seatxt.isEmpty()) {
                // position is imp to update items list particular  value.
                listener.getEstimateOperative(seatxt, 2 , false);
            }
        });

    }

    private void bindBRIEFINGSignHolder(BriefingSignHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answerItemId = dbHandler.getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answerItemId != null) {
            String value = answerItemId.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.txtTitle.setText(answerItemId.getDisplayAnswer());
            }
        }

        holder.imgButton.setOnClickListener(v -> {
            formItems.remove(position);
            Answer answerItemId1 = dbHandler.getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            // do not remove position 1 for multiple item doc name display
            Answer quantity1 = dbHandler.getAnswer(submissionID, fields.get(2),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (answerItemId1 != null) {
                dbHandler.removeAnswer(answerItemId1);
            }
            if (quantity1 != null) {
                dbHandler.removeAnswer(quantity1);
            }

            notifyDataSetChanged();
        });

        holder.view.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

    }

    private void bindBRIEFINGTEXTHolder(Briefingtextholder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.textView.setText(value);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.textView.setBackground(redBG);
        }
    }

    private void bindYesNoToolTipHolder(final YesNoToolTipHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtQuestion.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        holder.btnYes.setSelected(false);
        holder.btnNo.setSelected(false);

        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null) {
                if (value.equals("1")) {
                    holder.btnYes.setSelected(true);
                    holder.btnNo.setSelected(false);
                } else if (value.equals("2")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(true);
                } else if (value.equals("3")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(false);
                }
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnYes.setOnClickListener(view -> {

            holder.btnYes.setSelected(true);
            holder.btnNo.setSelected(false);
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }
            answer1.setAnswer("1");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);
            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.btnNo.setOnClickListener(view -> {

            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);
            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("2");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.imgBtnInfo.setOnClickListener(v -> {
            ToolTipDialog toolTipDialog = new ToolTipDialog(context, formItem.getToolTip());
            toolTipDialog.show();
        });
    }

    private void bindYesNoNAToolTipHolder(final YesNoNAToolTipHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtQuestion.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        holder.btnYes.setSelected(false);
        holder.btnNo.setSelected(false);
        holder.btnNA.setSelected(false);

        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null) {
                if (value.equals("1")) {
                    holder.btnYes.setSelected(true);
                    holder.btnNo.setSelected(false);
                    holder.btnNA.setSelected(false);
                } else if (value.equals("2")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(true);
                    holder.btnNA.setSelected(false);
                } else if (value.equals("3")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(false);
                    holder.btnNA.setSelected(true);
                }
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnYes.setOnClickListener(view -> {

            holder.btnYes.setSelected(true);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(false);
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }
            answer1.setAnswer("1");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);
            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.btnNo.setOnClickListener(view -> {

            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);
            holder.btnNA.setSelected(false);
            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("2");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.btnNA.setOnClickListener(view -> {
            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(true);
            Answer answer13 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);

            if (answer13 == null) {
                answer13 = new Answer(submissionID, formItem.getUploadId());
            }

            answer13.setAnswer("3");
            answer13.setRepeatID(formItem.getRepeatId());
            answer13.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer13.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
        });
        holder.imgBtnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToolTipDialog toolTipDialog = new ToolTipDialog(context, formItem.getToolTip());
                toolTipDialog.show();
            }
        });
    }


    private void bindServiceMaterialHolder(LogServiceHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        final BaseTask task = formItem.getTask();
        ArrayList<FormItem> fiLists = formItem.getDialogItems();
        if (fiLists == null || fiLists.isEmpty()) {
            return;
        }
        for (FormItem fi : fiLists) {
            String uploadId = fi.getUploadId();

            Answer answer = dbHandler.getAnswer(submissionID,
                    uploadId, fi.getRepeatId(),
                    formItem.getRepeatCount());
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                if (uploadId.equalsIgnoreCase("cones")) {
                    task.setCones(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("barriers")) {
                    task.setBarriers(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("chpt8")) {
                    task.setChpt8(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("fwBoards")) {
                    task.setFwBoards(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("bags")) {
                    task.setBags(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("sand")) {
                    task.setSand(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("stone")) {
                    task.setStone(parseFloat(answer.getAnswer()));
                }
            }
        }

        holder.txtCones.setText("Cones: " + task.getCones());
        holder.txtBarriers.setText("Barriers: " + task.getBarriers());
        holder.txtChpt8.setText("Chpt8: " + task.getChpt8());
        holder.txtFwBoards.setText("FwBoards: " + task.getFwBoards());
        holder.txtBags.setText("Bags: " + task.getBags());
        holder.txtSand.setText("Sand: " + task.getSand());
        holder.txtStone.setText("Stone: " + task.getStone());


        holder.txtComment.setText(task.getComments());

        holder.llBtnEdit.setOnClickListener(view -> listener.openTaskAmendment(formItem, submissionID, formItem.getRepeatCount()));

        if (task.isSelectable()) {
            holder.imgBtnCheck.setSelected(task.isSelected());
            holder.llBtnCheck.setVisibility(View.VISIBLE);
            holder.imgBtnCheck.setOnClickListener(view -> {
                view.setSelected(!view.isSelected());
                task.setSelected(view.isSelected());
                saveTaskAnswers();
            });
        }

    }

    private void bindSiteClearScheduledHolder(LogServiceHolder holder, int position) {
        holder.llBtnCheck.setVisibility(View.GONE);
        final FormItem formItem = formItems.get(position);
        final BaseTask task = formItem.getTask();
        ArrayList<FormItem> fiLists = formItem.getDialogItems();
        if (fiLists == null || fiLists.isEmpty()) {
            return;
        }
        for (FormItem fi : fiLists) {
            String uploadId = fi.getUploadId();

            Answer answer = dbHandler.getAnswer(submissionID,
                    uploadId, fi.getRepeatId(),
                    formItem.getRepeatCount());
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                if (uploadId.equalsIgnoreCase("cones")) {
                    task.setCones(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("barriers")) {
                    task.setBarriers(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("chpt8")) {
                    task.setChpt8(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("fwBoards")) {
                    task.setFwBoards(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("bags")) {
                    task.setBags(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("sand")) {
                    task.setSand(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("stone")) {
                    task.setStone(parseFloat(answer.getAnswer()));
                }
            }
        }

        holder.txtCones.setText("Cones: " + task.getCones());
        holder.txtBarriers.setText("Barriers: " + task.getBarriers());
        holder.txtChpt8.setText("Chpt8: " + task.getChpt8());
        holder.txtFwBoards.setText("FwBoards: " + task.getFwBoards());
        holder.txtBags.setText("Bags: " + task.getBags());
        holder.txtSand.setText("Sand: " + task.getSand());
        holder.txtStone.setText("Stone: " + task.getStone());


        holder.txtComment.setText(task.getComments());

        holder.llBtnEdit.setOnClickListener(view -> listener.openTaskAmendment(formItem, submissionID, formItem.getRepeatCount()));

//        if (task.isSelectable()) {
//            holder.imgBtnCheck.setSelected(task.isSelected());
//            holder.llBtnCheck.setVisibility(View.VISIBLE);
//            holder.imgBtnCheck.setOnClickListener(view -> {
//                view.setSelected(!view.isSelected());
//                task.setSelected(view.isSelected());
//                saveTaskAnswers();
//            });
//        }

    }

    private void bindSiteClearItemHolder(SiteClearHolder holder, int position) {

        final FormItem formItem = formItems.get(position);

        ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }
        for (String uploadId : fields) {


            Answer answer = dbHandler.getAnswer(submissionID,
                    uploadId, formItem.getRepeatId(),
                    formItem.getRepeatCount());
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                if (uploadId.equalsIgnoreCase("cones")) {
                    holder.txtCones.setText("Cones: " + answer.getAnswer());
                } else if (uploadId.equalsIgnoreCase("barriers")) {
                    holder.txtBarriers.setText("Barriers: " + answer.getAnswer());
                } else if (uploadId.equalsIgnoreCase("chpt8")) {
                    holder.txtChpt8.setText("Chpt8: " + answer.getAnswer());
                } else if (uploadId.equalsIgnoreCase("fwBoards")) {
                    holder.txtFwBoards.setText("FwBoards: " + answer.getAnswer());
                } else if (uploadId.equalsIgnoreCase("bags")) {
                    holder.txtBags.setText("Bags: " + answer.getAnswer());
                } else if (uploadId.equalsIgnoreCase("sand")) {
                    holder.txtSand.setText("Sand: " + answer.getAnswer());
                } else if (uploadId.equalsIgnoreCase("stone")) {
                    holder.txtStone.setText("Stone: " + answer.getAnswer());
                }
            }
        }

        holder.llBtnEdit.setOnClickListener(view -> listener.openTaskAmendment(formItem, submissionID, formItem.getRepeatCount()));
        holder.llBtnDelete.setOnClickListener(view -> {

            for (String field : fields) {
                Answer rateId1 = dbHandler.getAnswer(submissionID, field, formItem.getRepeatId(), formItem.getRepeatCount());
                if (rateId1 != null) {
                    dbHandler.removeAnswer(rateId1);
                }
            }

            reInflateItems(true);
        });

    }


    private void bindDigMeasureItemHolder(@NonNull final DigMeasureItemHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        ArrayList<String> fields = formItem.getFields();

        Answer SurfaceTypeID = dbHandler.getAnswer(submissionID, fields.get(0), formItem.getRepeatId(), formItem.getRepeatCount());
        if (SurfaceTypeID != null && !TextUtils.isEmpty(SurfaceTypeID.getAnswer())) {
            holder.txtSurfaceValue.setText(SurfaceTypeID.getDisplayAnswer());
        } else {
            holder.txtSurfaceValue.setText("");
        }

        Answer MaterialTypeID = dbHandler.getAnswer(submissionID, fields.get(1), formItem.getRepeatId(), formItem.getRepeatCount());
        if (MaterialTypeID != null && !TextUtils.isEmpty(MaterialTypeID.getAnswer())) {
            holder.txtMaterialValue.setText(MaterialTypeID.getDisplayAnswer());
        } else {
            holder.txtMaterialValue.setText("");
        }

        Answer Length = dbHandler.getAnswer(submissionID, fields.get(2), formItem.getRepeatId(), formItem.getRepeatCount());
        if (Length != null && !TextUtils.isEmpty(Length.getAnswer())) {
            holder.txtLength.setText(Length.getDisplayAnswer());
        } else {
            holder.txtLength.setText("");
        }

        Answer Width = dbHandler.getAnswer(submissionID, fields.get(3), formItem.getRepeatId(), formItem.getRepeatCount());
        if (Width != null && !TextUtils.isEmpty(Width.getAnswer())) {
            holder.txtWidth.setText(Width.getDisplayAnswer());
        } else {
            holder.txtWidth.setText("");
        }

        Answer Depth = dbHandler.getAnswer(submissionID, fields.get(4), formItem.getRepeatId(), formItem.getRepeatCount());
        if (Depth != null && !TextUtils.isEmpty(Depth.getAnswer())) {
            holder.txtDepth.setText(Depth.getDisplayAnswer());
        } else {
            holder.txtDepth.setText("");
        }


        holder.llBtnEdit.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

        holder.llBtnDelete.setOnClickListener(view -> {

            for (String field :
                    fields) {
                Answer rateId1 = dbHandler.getAnswer(submissionID, field, formItem.getRepeatId(), formItem.getRepeatCount());
                if (rateId1 != null) {
                    dbHandler.removeAnswer(rateId1);
                }
            }

            reInflateItems(true);
        });
    }

    private void bindMuckAwayHolder(LogMuckAwayHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        bindLogFillData(formItem);
        final BaseTask task = formItem.getTask();

        holder.txtMaterialName.setText("Material: " + task.getMaterialTypeName());
        holder.txtComment.setText("Comments: " + task.getComments());

        if (task.isSelectable()) {
            holder.checkBox.setSelected(task.isSelected());

            holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
                task.setSelected(isChecked);
                saveTaskAnswers();
            });
        }

    }


    private void bindLogFillItemHolder(LogDigBackFillItemHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        bindLogFillData(formItem);
        final BaseTask task = formItem.getTask();

        holder.txtSurfaceValue.setText(task.getSurfaceTypeName());
        holder.txtMaterialValue.setText(task.getMaterialTypeName());
        holder.txtLength.setText(String.valueOf(task.getLength()));
        holder.txtWidth.setText(String.valueOf(task.getWidth()));
        holder.txtDepth.setText(String.valueOf(task.getDepth()));
        holder.txtComments.setText(task.getComments());

        holder.llBtnEdit.setOnClickListener(view -> listener.openTaskAmendment(formItem, submissionID, formItem.getRepeatCount()));

        if (task.isSelectable()) {
            holder.imgBtnCheck.setSelected(task.isSelected());
            holder.llBtnCheck.setVisibility(View.VISIBLE);
            holder.imgBtnCheck.setOnClickListener(view -> {
                view.setSelected(!view.isSelected());
                task.setSelected(view.isSelected());
                saveTaskAnswers();
            });
        }

    }


    private void bindReinstatementItemHolder(LogReinstatementItemHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        bindLogFillData(formItem);
        final BaseTask task = formItem.getTask();

        if (notices != null && !notices.isEmpty()) {
            holder.txtNoticeReferenceValue.setText(notices.get(0).getworksReference());
            holder.txtNoticeAddressValue.setText(notices.get(0).getNoticeLocation());
        } else {
            holder.txtNoticeReferenceValue.setText("N/A");
            holder.txtNoticeAddressValue.setText("N/A");
        }


        holder.txtSurfaceValue.setText(task.getSurfaceTypeName());
        holder.txtMaterialValue.setText(task.getMaterialTypeName());
        holder.txtLength.setText(String.valueOf(task.getLength()));
        holder.txtWidth.setText(String.valueOf(task.getWidth()));
        holder.txtDepth.setText(String.valueOf(task.getDepth()));
        holder.txtComments.setText(task.getComments());

        holder.llBtnEdit.setOnClickListener(view -> listener.openTaskAmendment(formItem, submissionID, formItem.getRepeatCount()));

        if (task.isSelectable()) {
            holder.imgBtnCheck.setSelected(task.isSelected());
            holder.llBtnCheck.setVisibility(View.VISIBLE);
            holder.imgBtnCheck.setOnClickListener(view -> {
                view.setSelected(!view.isSelected());
                task.setSelected(view.isSelected());
                saveTaskAnswers();
            });
        }

    }

    private void saveTaskAnswers() {

        if (selectableTasks != null) {
            //remove old
            dbHandler.deleteMultiAnswer(submissionID, "completedSiteActivityTaskIds", repeatCount);
            //add new
            for (int i = 0; i < selectableTasks.size(); i++) {
                BaseTask task = selectableTasks.get(i);
                if (task.isSelected()) {
                    Answer answer = new Answer(submissionID, "completedSiteActivityTaskIds");
                    answer.setAnswer(String.valueOf(task.getSiteActivityTaskId()));
                    answer.setIsMultiList(1);
                    answer.setRepeatID(null);
                    answer.setRepeatCount(repeatCount);
                    dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
                }
            }
        }
    }

    private void bindLogFillData(FormItem formItem) {
        BaseTask task = formItem.getTask();
        ArrayList<FormItem> fiLists = formItem.getDialogItems();
        if (fiLists == null || fiLists.isEmpty()) {
            return;
        }
        for (FormItem fi : fiLists) {
            String uploadId = fi.getUploadId();

            Answer answer = dbHandler.getAnswer(submissionID,
                    uploadId, fi.getRepeatId(),
                    formItem.getRepeatCount());
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                if (uploadId.equalsIgnoreCase("surfaceTypeId")) {
                    task.setSurfaceTypeId(answer.getAnswer());
                    task.setSurfaceTypeName(answer.getDisplayAnswer());
                } else if (uploadId.equalsIgnoreCase("materialTypeId")) {
                    task.setMaterialTypeId(answer.getAnswer());
                    task.setMaterialTypeName(answer.getDisplayAnswer());
                } else if (uploadId.equalsIgnoreCase("length")) {
                    task.setLength(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("width")) {
                    task.setWidth(parseFloat(answer.getAnswer()));
                } else if (uploadId.equalsIgnoreCase("depth")) {
                    task.setDepth(parseFloat(answer.getAnswer()));
                }
            }
        }
    }

    private float parseFloat(String value){
        if(TextUtils.isEmpty(value)){
            return 0;
        }

        try{
            return Float.parseFloat(value);
        }catch (Exception e){
            return 0;
        }
    }

    private boolean isSubJob(){
        return job != null && job.isSubJob();
    };


    private void bindLogMeasureItemHolder(@NonNull final LogMeasureItemHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        ArrayList<String> fields = formItem.getFields();
        int counter = 0;
        if(isSubJob()){
            counter = 1;
        }

        Answer rateID = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
        if (rateID != null && !TextUtils.isEmpty(rateID.getAnswer())) {
            holder.txtWorkTitle.setText(rateID.getDisplayAnswer());
        } else {
            holder.txtWorkTitle.setText("");
        }

        counter++;
        Answer quantity = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
        if (quantity != null && !TextUtils.isEmpty(quantity.getAnswer())) {
            holder.txtQuantity.setText(quantity.getDisplayAnswer());
        } else {
            holder.txtQuantity.setText("");
        }

        counter++;
        Answer RaiseATask = dbHandler.getAnswer(submissionID, fields.get(counter),
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (RaiseATask != null && !TextUtils.isEmpty(RaiseATask.getAnswer()) && RaiseATask.getAnswer().equalsIgnoreCase("true")) {

            holder.llTaskData.setVisibility(View.VISIBLE);
            counter++;
            Answer SurfaceTypeID = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
            if (SurfaceTypeID != null && !TextUtils.isEmpty(SurfaceTypeID.getAnswer())) {
                holder.txtSurfaceValue.setText(SurfaceTypeID.getDisplayAnswer());
            } else {
                holder.txtSurfaceValue.setText("");
            }
            counter++;
            Answer MaterialTypeID = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
            if (MaterialTypeID != null && !TextUtils.isEmpty(MaterialTypeID.getAnswer())) {
                holder.txtMaterialValue.setText(MaterialTypeID.getDisplayAnswer());
            } else {
                holder.txtMaterialValue.setText("");
            }
            counter++;
            Answer Length = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
            if (Length != null && !TextUtils.isEmpty(Length.getAnswer())) {
                holder.txtLength.setText(Length.getDisplayAnswer());
            } else {
                holder.txtLength.setText("");
            }
            counter++;
            Answer Width = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
            if (Width != null && !TextUtils.isEmpty(Width.getAnswer())) {
                holder.txtWidth.setText(Width.getDisplayAnswer());
            } else {
                holder.txtWidth.setText("");
            }
            counter++;
            Answer DepthB = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
            if (DepthB != null && !TextUtils.isEmpty(DepthB.getAnswer())) {
                holder.txtDepth.setText(DepthB.getDisplayAnswer());
            } else {
                holder.txtDepth.setText("");
            }

        } else {
            holder.llTaskData.setVisibility(View.GONE);
        }


        holder.llBtnEdit.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

        holder.llBtnDelete.setOnClickListener(view -> {

            for (String field :
                    fields) {
                Answer rateId1 = dbHandler.getAnswer(submissionID, field, formItem.getRepeatId(), formItem.getRepeatCount());
                if (rateId1 != null) {
                    dbHandler.removeAnswer(rateId1);
                }
            }

            reInflateItems(true);
        });
    }

    private void bindLogHoursHolder(@NonNull final LogDayHoursViewHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        ArrayList<String> fields = formItem.getFields();

        Answer dateWorked = dbHandler.getAnswer(submissionID, fields.get(0), formItem.getRepeatId(), formItem.getRepeatCount());
        if (dateWorked != null && !TextUtils.isEmpty(dateWorked.getAnswer())) {
            holder.txtDate.setText(dateWorked.getDisplayAnswer());
        } else {
            holder.txtDate.setText("");
        }

        Answer taskType = dbHandler.getAnswer(submissionID, "timeTypeName", formItem.getRepeatId(), formItem.getRepeatCount());
        if (taskType != null && !TextUtils.isEmpty(taskType.getAnswer())) {
            holder.txtTaskType.setText(taskType.getDisplayAnswer());
        } else {
            holder.txtTaskType.setText("");
        }

        Answer timeType = dbHandler.getAnswer(submissionID, fields.get(1), formItem.getRepeatId(), formItem.getRepeatCount());
        if (timeType != null && !TextUtils.isEmpty(timeType.getAnswer())) {
            holder.llRow1.setVisibility(View.VISIBLE);
            holder.txtTimeTypeName1.setText(timeType.getDisplayAnswer());
        } else {
            holder.txtTimeTypeName1.setText("");
        }
        int counter = 2;
        if(submission.getJsonFileName().equalsIgnoreCase("timesheet_log_hours.json")){
            Answer operatives = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
            if (operatives != null && !TextUtils.isEmpty(operatives.getAnswer())) {
                holder.txtOperatives.setVisibility(View.VISIBLE);
                holder.txtOperatives.setText(operatives.getAnswer());
            } else {
                holder.txtOperatives.setVisibility(View.GONE);
            }
            counter++;
        }



        Answer jobReference = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
        if (jobReference != null && !TextUtils.isEmpty(jobReference.getAnswer())) {
            holder.txtJobReference.setText(jobReference.getDisplayAnswer());
        } else {
            holder.txtJobReference.setText("");
        }
        counter++;

        int totalTime = 0;
        Answer normalTimeMinutes = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
        counter++;
        Answer overtimeMinutes = dbHandler.getAnswer(submissionID, fields.get(counter), formItem.getRepeatId(), formItem.getRepeatCount());
        if (normalTimeMinutes != null && !TextUtils.isEmpty(normalTimeMinutes.getAnswer())) {
            holder.txtNormalTime1.setText(normalTimeMinutes.getDisplayAnswer());
            totalTime = Integer.parseInt(normalTimeMinutes.getAnswer());
        } else {
            holder.txtNormalTime1.setText("0h");
        }
        if (overtimeMinutes != null && !TextUtils.isEmpty(overtimeMinutes.getAnswer())) {
            holder.llOverTime1.setText(overtimeMinutes.getDisplayAnswer());
            totalTime += Integer.parseInt(overtimeMinutes.getAnswer());
        } else {
            holder.llOverTime1.setText("0h");
        }

        holder.llTotalHours1.setText(CommonUtils.getDisplayTime(totalTime));
        holder.llTotalHours5.setText(CommonUtils.getDisplayTime(totalTime));

        holder.itemView.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

        holder.imgBtnDelete.setOnClickListener(view -> {

            for (String field : fields) {
                if(field.equalsIgnoreCase("normalTimeMinutes") || field.equalsIgnoreCase("overtimeMinutes")) {
                    Answer answer = dbHandler.getAnswer(submissionID, field, formItem.getRepeatId(), formItem.getRepeatCount());
                    if (answer != null) {
                        answer.setDisplayAnswer("0h");
                        answer.setAnswer("0");
                        dbHandler.replaceData(Answer.DBTable.NAME , answer.toContentValues());
//                        dbHandler.removeAnswer(answer);
                    }
                }
            }
            reInflateItems(true);
        });
    }

    private void bindTimeSheetHolder(@NonNull final LogDayHoursViewHolder holder, int position) {
        FormItem item = formItems.get(position);
        LogHourItem timeSheetHour = item.getTimeSheetHour();
        String weekCommencing = timeSheetHour.getWeekCommencing();
        String status = DBHandler.getInstance().getTimeSheetsStatus(weekCommencing);
        holder.setColor(status);

        if (TextUtils.isEmpty(timeSheetHour.getTimeTypeName()) && TextUtils.isEmpty(timeSheetHour.getJobId())) {
            holder.llHoursContainer.setVisibility(View.GONE);
            holder.txtNoLoggedHours.setVisibility(View.VISIBLE);
        } else {
            holder.llHoursContainer.setVisibility(View.VISIBLE);
            holder.txtNoLoggedHours.setVisibility(View.GONE);
            holder.txtTaskType.setText(timeSheetHour.getTimeTypeName());
            holder.txtJobReference.setText(timeSheetHour.getEstimate());
            holder.txtOperatives.setVisibility(View.GONE);
            try {
                holder.txtDate.setText(Utils.getSimpleDateFormat(timeSheetHour.getDateWorked()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            addLayout(holder, timeSheetHour.getLogHourTimes());

            holder.llTotalHours5.setText(timeSheetHour.getTotalHoursText());
        }

        holder.itemView.setOnClickListener(v -> {
            if (timeSheetHour.isWaitingApproval() || timeSheetHour.isApproved()) {
                return;
            }
            Intent intent = new Intent(context, FormActivity.class);
            AppPreferences.setTheme(ThemeBaseActivity.THEME_TIME_SHEET);
            String jsonFileName = "timesheet_log_hours.json";
            Submission submission = new Submission(jsonFileName, "Log Hours", "");
            long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);
            timeSheetHour.saveAnswers(submission.getID(), item.getRepeatId());
            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
            listener.startActivityForResultFromAdapter(intent, 10001);
        });
        holder.imgBtnDelete.setVisibility(View.GONE);

//        holder.imgBtnDelete.setOnClickListener(view -> {
//            if (timeSheetHour.isWaitingApproval() || timeSheetHour.isApproved()) {
//                return;
//            }
//            DBHandler.getInstance().removeMultiAnswersByUploadID(submissionID , "timesheetHoursIds");
//            timeSheetHour.deleteLogHourItem();
//
//            reInflateItems(true);
//        });
    }

    private void openLogHours(ForkCardHolder holder , int position){
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, FormActivity.class);
            AppPreferences.setTheme(ThemeBaseActivity.THEME_TIME_SHEET);
            String jsonFileName = "timesheet_log_hours.json";
            Submission submission = new Submission(jsonFileName, "Log Hours", "");
            long submissionID = dbHandler.insertData(Submission.DBTable.NAME, submission.toContentValues());
            submission.setId(submissionID);
//            Answer answer = new Answer(submissionID , "selected_date" , null , 0);
//            answer.setAnswer(wee);
//            answer.setDisplayAnswer(timeSheetHour.getDisplayedWorkDate());
//            answer.setShouldUpload(false);
//            dbHandler.replaceData(Answer.DBTable.NAME , answer.toContentValues());

            intent.putExtra(FormActivity.ARG_SUBMISSION, submission);
            listener.startActivityForResultFromAdapter(intent, 10001);
        });

    }
    private void addLayout(LogDayHoursViewHolder viewHolder, ArrayList<LogHourTime> times) {
        if (times.isEmpty()) {
            return;
        }
        viewHolder.llRow1.setVisibility(View.GONE);
        viewHolder.llRow2.setVisibility(View.GONE);
        viewHolder.llRow3.setVisibility(View.GONE);
        viewHolder.llRow4.setVisibility(View.GONE);
        for (int i = 0; i < times.size(); i++) {
            if (i == 0) {
                viewHolder.llRow1.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName1.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime1.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime1.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours1.setText(times.get(i).getTotalHoursText());
            }
            if (i == 1) {
                viewHolder.llRow2.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName2.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime2.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime2.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours2.setText(times.get(i).getTotalHoursText());
            }

            if (i == 2) {
                viewHolder.llRow3.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName3.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime3.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime3.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours3.setText(times.get(i).getTotalHoursText());
            }

            if (i == 3) {
                viewHolder.llRow4.setVisibility(View.VISIBLE);
                viewHolder.txtTimeTypeName4.setText(times.get(i).getTimeTypeActivityName());
                viewHolder.txtNormalTime4.setText(times.get(i).getNormalTimeHours());
                viewHolder.llOverTime4.setText(times.get(i).getOvertimeHours());
                viewHolder.llTotalHours4.setText(times.get(i).getTotalHoursText());
            }
        }
    }

    private void bindBarCodeItem(DropDownHolder holder, int position) {
        holder.txtTitle.setText(formItems.get(position).getTitle());
        holder.view.setOnClickListener(v -> {
            Answer answer = dbHandler.getAnswer(submissionID, "StaId", "Items", 0);
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                Intent intent = new Intent(context, ScannedBarcodeActivity.class);
                intent.putExtra("StaId", answer.getAnswer());
                listener.startActivityForResultFromAdapter(intent, ScannedBarcodeActivity.REQUEST_CODE);
            }
        });

    }

    private void bindAddStoreItem(DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);

        holder.txtTitle.setText(formItem.getTitle());
        holder.view.setBackground(null);
        holder.txtValue.setVisibility(View.GONE);
        Answer answer = dbHandler.getAnswer(submissionID,
                formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtValue.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }


        holder.view.setOnClickListener(view -> {
            Intent intent;
            if (submission.getJsonFileName().equalsIgnoreCase("log_store.json")) {
                intent = new Intent(context, ListStoreItemActivity.class);
            } else {
                intent = new Intent(context, ListStockItemActivity.class);
            }
            intent.putExtra("submissionID", submissionID);
//                intent.putExtra("repeatId", formItem.getRepeatId());
//                intent.putExtra("isStoackLevelCheck", formItem.isStoackLevelCheck());
            intent.putExtra("formItem", formItem);
            context.startActivity(intent);
        });
    }

    private void bindStockItem(StockItemHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);

        final Answer answer = dbHandler.getAnswer(submissionID, "StaStockItemId",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
            return;
        }

        String description = "";
        if (submission.getJsonFileName().equalsIgnoreCase("log_store.json")) {
            MyStore item = dbHandler.getMyStores(answer.getAnswer());
            if (item != null) {
                description = item.getdescription();
            }
        } else {
            StockItems item = dbHandler.getStockItems(answer.getAnswer());
            if (item != null) {
                description = item.getdescription();
            }
        }


        holder.txtDescription.setText(description);


        final Answer Quantity = dbHandler.getAnswer(submissionID, "Quantity",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (Quantity == null || TextUtils.isEmpty(Quantity.getAnswer())) {
            return;
        }


        Answer StaId = dbHandler.getAnswer(submissionID,
                "StaId", formItem.getRepeatId(), formItem.getRepeatCount());
        if (StaId == null) {
            Answer staAnswer = dbHandler.getAnswer(submissionID, "StaId",
                    formItem.getRepeatId(), 0);
            if (staAnswer != null) {
                StaId = new Answer(submissionID, "StaId");
                StaId.setRepeatID(formItem.getRepeatId());
                StaId.setRepeatCount(formItem.getRepeatCount());
                StaId.setAnswer(staAnswer.getAnswer());
                StaId.setDisplayAnswer(staAnswer.getDisplayAnswer());
                dbHandler.replaceData(Answer.DBTable.NAME, StaId.toContentValues());
            }
        }

        holder.txtNumber.setText(Quantity.getAnswer());
        holder.btnDelete.setOnClickListener(v -> {
            dbHandler.removeAnswer(answer);
            dbHandler.removeAnswer(Quantity);
            final Answer StaId1 = dbHandler.getAnswer(submissionID, "StaId",
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (StaId1 != null) {
                dbHandler.removeAnswer(StaId1);
            }

            reInflateItems(false);

        });
    }

    private void bindCurrentStoreHolder(CurrentStoreHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        MyStore store = formItem.getMyStore();
        if (store == null) {
            return;
        }

        holder.txtDescription.setText(store.getdescription());
        holder.txtUnit.setText(store.getunitName());
        holder.txtQuantity.setText(String.valueOf(store.getquantity()));
        holder.txtNumber.setText(String.valueOf(formItem.getMyStoreQuantity()));

        Answer staStockItemId = dbHandler.getAnswer(submissionID, "StaStockItemId",
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (staStockItemId == null) {

            staStockItemId = new Answer(submissionID, "StaStockItemId");

        }

        staStockItemId.setAnswer(store.getStaStockItemId());
        staStockItemId.setDisplayAnswer("");
        staStockItemId.setRepeatID(formItem.getRepeatId());
        staStockItemId.setRepeatCount(formItem.getRepeatCount());

        dbHandler.replaceData(Answer.DBTable.NAME, staStockItemId.toContentValues());

        Answer Quantity = dbHandler.getAnswer(submissionID, "Quantity",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (Quantity == null) {
            Quantity = new Answer(submissionID, "Quantity");
        }

        Quantity.setAnswer(String.valueOf(formItem.getMyStoreQuantity()));
        Quantity.setDisplayAnswer("");
        Quantity.setRepeatID(formItem.getRepeatId());
        Quantity.setRepeatCount(formItem.getRepeatCount());

        dbHandler.replaceData(Answer.DBTable.NAME, Quantity.toContentValues());

        Answer StaId = dbHandler.getAnswer(submissionID, "StaId",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (StaId == null) {

            StaId = new Answer(submissionID, "StaId");

        }

        StaId.setAnswer(store.getstaId());
        StaId.setDisplayAnswer("");
        StaId.setRepeatID(formItem.getRepeatId());
        StaId.setRepeatCount(formItem.getRepeatCount());

        dbHandler.replaceData(Answer.DBTable.NAME, StaId.toContentValues());


    }

    private void bindRiskElementHolder(RiskElementHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText("");

        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answer = dbHandler.getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answer != null && !TextUtils.isEmpty(answer.getDisplayAnswer())) {
            holder.txtTitle.setText(answer.getDisplayAnswer());
        }

        holder.imgDelete.setOnClickListener(v -> {

            Answer answer1 = dbHandler.getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (answer1 != null) {
                dbHandler.removeAnswer(answer1);
            }

            Answer actionRequired = dbHandler.getAnswer(submissionID, "actionRequired",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (actionRequired != null) {
                dbHandler.removeAnswer(actionRequired);
            }

            Answer sPoleNo = dbHandler.getAnswer(submissionID, "sPoleNo",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (sPoleNo != null) {
                dbHandler.removeAnswer(sPoleNo);
            }

            Answer dPoleNo = dbHandler.getAnswer(submissionID, "dPoleNo",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (dPoleNo != null) {
                dbHandler.removeAnswer(dPoleNo);
            }

            Answer isPoleScheduleCompleted = dbHandler.getAnswer(submissionID, "isPoleScheduleCompleted",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (isPoleScheduleCompleted != null) {
                dbHandler.removeAnswer(isPoleScheduleCompleted);
            }

            Answer consideration = dbHandler.getAnswer(submissionID, "consideration",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (consideration != null) {
                dbHandler.removeAnswer(consideration);
            }
            formItems.remove(position);
            notifyDataSetChanged();
        });

        holder.view.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));
    }

    private void bindDateTimeHolder(final DateTimeHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtDate.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.txtDate.setOnClickListener(v -> {
            final Calendar myCalendar = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

                Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);


                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                String date1 = sdf.format(myCalendar.getTime());
                holder.txtDate.setText(date1.split("T")[0]);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }

                answer1.setAnswer(date1);
                answer1.setDisplayAnswer(date1);
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);

                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            };

            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 != null && !TextUtils.isEmpty(answer1.getAnswer())) {
                String value = answer1.getAnswer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

                try {
                    Date selectedDate = sdf.parse(value);
                    if (selectedDate != null) {
                        myCalendar.setTime(selectedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            new DatePickerDialog(context, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        });

        holder.txtTime.setOnClickListener(v -> {
            final Calendar myCalendar = Calendar.getInstance();

            TimePickerDialog.OnTimeSetListener date = (view, hourOfDay, minute) -> {
                Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);

                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);


                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                String date12 = sdf.format(myCalendar.getTime());
                holder.txtTime.setText(date12.split("T")[1]);


                if (answer12 == null) {
                    answer12 = new Answer(submissionID, formItem.getUploadId());
                }

                answer12.setAnswer(date12);
                answer12.setDisplayAnswer(date12);
                answer12.setRepeatID(formItem.getRepeatId());
                answer12.setRepeatCount(repeatCount);

                dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            };

            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 != null && !TextUtils.isEmpty(answer12.getAnswer())) {
                String value = answer12.getAnswer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

                try {
                    Date selectedDate = sdf.parse(value);
                    if (selectedDate != null) {
                        myCalendar.setTime(selectedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }


            new TimePickerDialog(context, date, myCalendar
                    .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                    true).show();

        });
    }

    private void bindVisitorSignHolder(VisitorSignHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answerVisitorName = dbHandler.getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer imgPath = dbHandler.getAnswer(submissionID, fields.get(1),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answerVisitorName != null) {
            String value = answerVisitorName.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.txtTitle.setText(value);
                holder.txtTitle.setText(answerVisitorName.getDisplayAnswer());
            }
        }

        if (imgPath != null) {
            String value = imgPath.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.imgSignature.setImageDrawable(null);
                Glide.with(context).load(value).into(holder.imgSignature);
            }
        }

        holder.imgDelete.setOnClickListener(v -> {

            Answer answerVisitorName1 = dbHandler.getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            Answer imgPath1 = dbHandler.getAnswer(submissionID, fields.get(1),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (answerVisitorName1 != null) {
                dbHandler.removeAnswer(answerVisitorName1);
            }

            if (imgPath1 != null) {
                dbHandler.removeAnswer(imgPath1);
            }

            formItems.remove(position);
            notifyDataSetChanged();
        });

        holder.view.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

    }

    private void bindStopWatchHolder(final StopWatchHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtTime.setText(answer.getDisplayAnswer());
                holder.setUpdateTime(Long.parseLong(value));
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnReset.setOnClickListener(v -> {
            holder.resetTime();
            isTimerRunning = false;
        });

        holder.btnStart.setOnClickListener(v -> {
            holder.btnStart.setSelected(!holder.btnStart.isSelected());
            if (holder.btnStart.isSelected()) {
                holder.startTimer();
                isTimerRunning = true;
            } else {
                isTimerRunning = false;
                holder.stopTimer();
                Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);
                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }

                answer1.setRepeatCount(repeatCount);
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setAnswer(String.valueOf(holder.getUpdateTime()));
                answer1.setDisplayAnswer(holder.txtTime.getText().toString());

                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            }

        });

    }


    private void bindDFEHolder(DFEItemHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answerItemId = dbHandler.getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer quantity = dbHandler.getAnswer(submissionID, fields.get(1),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answerItemId != null) {
            String value = answerItemId.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.txtValue.setText(answerItemId.getDisplayAnswer());
//                holder.txtTitle.setText(answerItemId.getDisplayAnswer());
            }
        }

        if (quantity != null) {
            String value = quantity.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.txtQuantity.setText(value);
            }
        }

        holder.llBtnDelete.setOnClickListener(v -> {
            formItems.remove(position);
            Answer answerItemId1 = dbHandler.getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            Answer quantity1 = dbHandler.getAnswer(submissionID, fields.get(1),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (answerItemId1 != null) {
                dbHandler.removeAnswer(answerItemId1);
            }

            if (quantity1 != null) {
                dbHandler.removeAnswer(quantity1);
            }

            notifyDataSetChanged();
        });

        holder.llBtnEdit.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

    }

    private void bindCalender(final CalenderHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtDate.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.view.setOnClickListener(v -> {
            final Calendar myCalendar = Calendar.getInstance();

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

                Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);
                String date1 = sdf.format(myCalendar.getTime());
                holder.txtDate.setText(date1);


                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }

                answer1.setAnswer(date1);
                answer1.setDisplayAnswer(date1);
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);

                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            };

            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 != null && !TextUtils.isEmpty(answer1.getAnswer())) {
                String value = answer1.getAnswer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

                try {
                    Date selectedDate = sdf.parse(value);
                    if (selectedDate != null) {
                        myCalendar.setTime(selectedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            new DatePickerDialog(context, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH)).show();

        });
    }

    private void bindWeekCommencing(final CalenderHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        final Calendar myCalendar = Calendar.getInstance();
        long timeInMiliSeconds = myCalendar.getTimeInMillis();
        holder.txtTitle.setText(formItem.getTitle());
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtDate.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.view.setOnClickListener(v -> {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
            SimpleDateFormat displaySdf = new SimpleDateFormat("dd/MM/yyyy", Locale.ENGLISH);
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 != null && !TextUtils.isEmpty(answer1.getAnswer())) {
                String value = answer1.getAnswer();
                try {
                    Date selectedDate = sdf.parse(value);
                    if (selectedDate != null) {
                        myCalendar.setTime(selectedDate);
                    }
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            listener.showDatePicker(new SimpleCalendarDialogFragment(context, date -> {

                myCalendar.setTime(date);
                if (myCalendar.get(Calendar.DAY_OF_WEEK) != CommonUtils.getWeekCommencingDayInt()) {
                    listener.showValidationDialog("Validation", "Please select week commencing date starting from "+CommonUtils.getWeekCommencingDay()+".");
                    return;
                }

                String date1 = sdf.format(myCalendar.getTime());
                String dispayDate = displaySdf.format(myCalendar.getTime());
                holder.txtDate.setText(dispayDate);


                Answer answer11 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);
                if (answer11 != null && !TextUtils.isEmpty(answer11.getAnswer())) {
                    ArrayList<TimeSheetHour> hours = dbHandler.getTimeHours(answer11.getAnswer());
                    for (int i = 0; i < hours.size(); i++) {
                        hours.get(i).deleteAnswers(submissionID, "timesheetHours", formItem.getRepeatCount());
                    }
                }

                if (answer11 == null) {
                    answer11 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
                }

                answer11.setAnswer(date1);
                answer11.setDisplayAnswer(dispayDate);
                dbHandler.replaceData(Answer.DBTable.NAME, answer11.toContentValues());
                getTimeSheetHours();
            }));
      });
    }


    private void bindStoreItem(final StoreHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.txtValue.setText("");
        holder.etNumber.setText("");

        int assetNumber = formItem.getAssetNumber();


        Answer assetId = dbHandler.getAnswer(submissionID, "itemId",
                formItem.getRepeatId(), assetNumber);
        Answer quantity = dbHandler.getAnswer(submissionID,
                "quantity", formItem.getRepeatId(), assetNumber);

        if (assetId != null) {
            String value = assetId.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.txtValue.setText(assetId.getDisplayAnswer());
            }
        }

        if (quantity != null) {
            String qtvalue = quantity.getAnswer();
            if (!TextUtils.isEmpty(qtvalue)) {
                holder.etNumber.setText(qtvalue);
            }

        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }


        holder.view.setOnClickListener(view -> {

            final ArrayList<DropDownItem> items = new ArrayList<>();
            String uploadId = formItem.getKey();
            items.addAll(dbHandler.getItemTypes(uploadId));
            if (items.isEmpty()) {
                return;
            }

            final DropdownMenu dropdownMenu = DropdownMenu.newInstance(items);
            dropdownMenu.setListener(position1 -> {
                holder.txtValue.setText(items.get(position1).getDisplayItem());
                int assetNumber1 = formItem.getAssetNumber();

                Answer assetType = dbHandler.getAnswer(submissionID, "type",
                        formItem.getRepeatId(), assetNumber1);

                if (assetType == null) {
                    assetType = new Answer(submissionID, "type");
                }

                assetType.setAnswer(formItem.getAssetType());
                assetType.setDisplayAnswer(formItem.getAssetType());
                assetType.setRepeatID(formItem.getRepeatId());
                assetType.setRepeatCount(assetNumber1);


                Answer assetId1 = dbHandler.getAnswer(submissionID, "itemId",
                        formItem.getRepeatId(), assetNumber1);

                if (assetId1 == null) {
                    assetId1 = new Answer(submissionID, "itemId");
                }

                assetId1.setAnswer(items.get(position1).getUploadValue());
                assetId1.setDisplayAnswer(items.get(position1).getDisplayItem());
                assetId1.setRepeatID(formItem.getRepeatId());
                assetId1.setRepeatCount(assetNumber1);


                dbHandler.replaceData(Answer.DBTable.NAME, assetType.toContentValues());
                dbHandler.replaceData(Answer.DBTable.NAME, assetId1.toContentValues());
            });

            listener.showBottomSheet(dropdownMenu);
        });

        holder.etNumber.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {

                int assetNumber12 = formItem.getAssetNumber();
                Answer assetType = dbHandler.getAnswer(submissionID, "type",
                        formItem.getRepeatId(), assetNumber12);

                if (assetType == null) {
                    assetType = new Answer(submissionID, "type");
                }

                assetType.setAnswer(formItem.getAssetType());
                assetType.setDisplayAnswer(formItem.getAssetType());
                assetType.setRepeatID(formItem.getRepeatId());
                assetType.setRepeatCount(assetNumber12);


                Answer quantity1 = dbHandler.getAnswer(submissionID,
                        "quantity", formItem.getRepeatId(), assetNumber12);

                if (quantity1 == null) {
                    quantity1 = new Answer(submissionID, "quantity");
                }

                EditText et = (EditText) view;
                quantity1.setAnswer(et.getText().toString());
                quantity1.setDisplayAnswer(et.getText().toString());
                quantity1.setRepeatID(formItem.getRepeatId());
                quantity1.setRepeatCount(assetNumber12);
                dbHandler.replaceData(Answer.DBTable.NAME,
                        quantity1.toContentValues());
                dbHandler.replaceData(Answer.DBTable.NAME,
                        assetType.toContentValues());
                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });

    }


    private void bindLocation(final LocationHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        if (themeColor != null && !themeColor.isEmpty()) {
            holder.txtBtnAddress.setBackgroundColor(Color.parseColor(themeColor));
        }
        Answer latitude = dbHandler.getAnswer(submissionID,
                "latitude",
                formItem.getRepeatId(), repeatCount);
        holder.view.setBackground(null);

        holder.txtLocation.setHint(formItem.getHint());
        if (latitude != null) {
            String value = latitude.getDisplayAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.txtLocation.setText(value);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }


        holder.view.setOnClickListener(v -> listener.getLocation(new LocationListener() {
            @Override
            public void onSuccess(Location location) {

                try {
                    Geocoder geocoder = new Geocoder(context, Locale.getDefault());
                    List<Address> addresses = null;
                    try {
                        addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    } catch (Exception e) {

                    }
                    String addressLine = "Latitude: " + location.getLatitude() + ", Longitude: " + location.getLongitude();
                    if (addresses != null && !addresses.isEmpty()) {
                        addressLine = addresses.get(0).getAddressLine(0);
                    }

                    holder.txtLocation.setText(addressLine);

                    Answer location1 = dbHandler.getAnswer(submissionID, "location",
                            formItem.getRepeatId(), repeatCount);
                    if (location1 == null) {
                        location1 = new Answer(submissionID, "location");
                    }

                    location1.setAnswer(String.valueOf(addressLine));
                    location1.setDisplayAnswer(addressLine);
                    location1.setRepeatID(formItem.getRepeatId());
                    location1.setRepeatCount(repeatCount);
                    dbHandler.replaceData(Answer.DBTable.NAME, location1.toContentValues());

                    Answer address = dbHandler.getAnswer(submissionID, "address",
                            formItem.getRepeatId(), repeatCount);
                    if (address == null) {
                        address = new Answer(submissionID, "address");
                    }

                    address.setAnswer(String.valueOf(addressLine));
                    address.setDisplayAnswer(addressLine);
                    address.setRepeatID(formItem.getRepeatId());
                    address.setRepeatCount(repeatCount);
                    dbHandler.replaceData(Answer.DBTable.NAME, address.toContentValues());

                    Answer latitude1 = dbHandler.getAnswer(submissionID, "latitude",
                            formItem.getRepeatId(), repeatCount);
                    if (latitude1 == null) {
                        latitude1 = new Answer(submissionID, "latitude");
                    }

                    latitude1.setAnswer(String.valueOf(location.getLatitude()));
                    latitude1.setDisplayAnswer(addressLine);
                    latitude1.setRepeatID(formItem.getRepeatId());
                    latitude1.setRepeatCount(repeatCount);

                    dbHandler.replaceData(Answer.DBTable.NAME, latitude1.toContentValues());

                    Answer longitude = dbHandler.getAnswer(submissionID, "longitude",
                            formItem.getRepeatId(), repeatCount);
                    if (longitude == null) {
                        longitude = new Answer(submissionID, "longitude");
                    }

                    longitude.setAnswer(String.valueOf(location.getLongitude()));
                    longitude.setDisplayAnswer(addressLine);
                    longitude.setRepeatID(formItem.getRepeatId());
                    longitude.setRepeatCount(repeatCount);

                    dbHandler.replaceData(Answer.DBTable.NAME, longitude.toContentValues());

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure() {
            }
        }));

        holder.txtLocation.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {

                Answer address = dbHandler.getAnswer(submissionID, "address",
                        formItem.getRepeatId(), repeatCount);
                if (address == null) {
                    address = new Answer(submissionID, "address");
                }

                address.setAnswer(String.valueOf(holder.txtLocation.getText()));
                address.setDisplayAnswer(holder.txtLocation.getText().toString());
                address.setRepeatID(formItem.getRepeatId());
                address.setRepeatCount(repeatCount);
                dbHandler.replaceData(Answer.DBTable.NAME, address.toContentValues());

                String lat = "0";
                String lon = "0";
                Answer latitude12 = dbHandler.getAnswer(submissionID, "latitude",
                        formItem.getRepeatId(), repeatCount);
                if (latitude12 == null) {
                    latitude12 = new Answer(submissionID, "latitude");
                } else if (!TextUtils.isEmpty(latitude12.getAnswer())) {
                    lat = latitude12.getAnswer();
                }

                latitude12.setAnswer(lat);
                latitude12.setDisplayAnswer(holder.txtLocation.getText().toString());
                latitude12.setRepeatID(formItem.getRepeatId());
                latitude12.setRepeatCount(repeatCount);

                dbHandler.replaceData(Answer.DBTable.NAME, latitude12.toContentValues());

                Answer longitude = dbHandler.getAnswer(submissionID, "longitude",
                        formItem.getRepeatId(), repeatCount);
                if (longitude == null) {
                    longitude = new Answer(submissionID, "longitude");
                } else if (!TextUtils.isEmpty(longitude.getAnswer())) {
                    lon = longitude.getAnswer();
                }

                longitude.setAnswer(lon);
                longitude.setDisplayAnswer(holder.txtLocation.getText().toString());
                longitude.setRepeatID(formItem.getRepeatId());
                longitude.setRepeatCount(repeatCount);

                dbHandler.replaceData(Answer.DBTable.NAME, longitude.toContentValues());

                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });

    }


    private void bindDropDownNumber(final DropDownNumber holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.txtValue.setText("");
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtValue.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        final DropdownNumberBottomSheet dropdownMenu = new DropdownNumberBottomSheet(context, number -> {
            StringBuilder sb = new StringBuilder();
            if (number.isEmpty()) {
                number = "00";
            }

            sb.append(number);

            holder.txtValue.setText(sb.toString());

            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer(sb.toString());
            answer1.setDisplayAnswer(sb.toString());
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());

        });

        holder.view.setOnClickListener(view -> dropdownMenu.show());
    }


    private void bindNumberHolder(NumberHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        if (answer != null) {
            holder.editText.setText(answer.getDisplayAnswer());
        } else {
            holder.editText.setText("");
        }

        holder.editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {

                Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }

                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);
                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                focusedEditText = null;

                if ((submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json") ||submission.getJsonFileName().equalsIgnoreCase("sub_job_risk_assessment.json")) &&
                        screen.getIndex() == 16) {
                    changeChamberListener.onChangeChamberCount(false);
                }
            } else {
                focusedEditText = (EditText) view;
            }
        });
    }

    private void bindPassFailHolder(final PassFailHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtQuestion.setText(formItem.getTitle());
        holder.rlContainer.setVisibility(View.GONE);

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        holder.btnYes.setSelected(false);
        holder.btnNo.setSelected(false);
        holder.btnNA.setSelected(false);

        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null) {
                if (value.equals("1")) {
                    holder.btnYes.setSelected(true);
                    holder.btnNo.setSelected(false);
                    holder.btnNA.setSelected(false);
                } else if (value.equals("2")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(true);
                    holder.btnNA.setSelected(false);
                    holder.rlContainer.setVisibility(View.VISIBLE);
                } else if (value.equals("3")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(false);
                    holder.btnNA.setSelected(true);
                }
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnYes.setOnClickListener(view -> {
            holder.btnYes.setSelected(true);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(false);
            holder.rlContainer.setVisibility(View.GONE);
            Answer answer13 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer13 == null) {
                answer13 = new Answer(submissionID, formItem.getUploadId());
            }
            answer13.setAnswer("1");
            answer13.setRepeatID(formItem.getRepeatId());
            answer13.setRepeatCount(repeatCount);
            dbHandler.replaceData(Answer.DBTable.NAME, answer13.toContentValues());
        });

        holder.btnNo.setOnClickListener(view -> {

            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);
            holder.btnNA.setSelected(false);
            holder.rlContainer.setVisibility(View.VISIBLE);
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer("2");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());

        });

        holder.btnNA.setOnClickListener(view -> {
            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(true);
            holder.rlContainer.setVisibility(View.GONE);
            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);

            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("3");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());

        });
    }


    private void bindYesNoNAHolder(final YesNoNAHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtQuestion.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        holder.btnYes.setSelected(false);
        holder.btnNo.setSelected(false);
        holder.btnNA.setSelected(false);
        if (formItem.isYesNotVisible()) {
            holder.btnYes.setVisibility(View.GONE);
        } else {
            holder.btnYes.setVisibility(View.VISIBLE);
        }

        if (formItem.isNoNotVisible()) {
            holder.btnNo.setVisibility(View.GONE);
        } else {
            holder.btnNo.setVisibility(View.VISIBLE);
        }

        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null) {
                if (value.equals("1")) {
                    holder.btnYes.setSelected(true);
                    holder.btnNo.setSelected(false);
                    holder.btnNA.setSelected(false);
                } else if (value.equals("2")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(true);
                    holder.btnNA.setSelected(false);
                } else if (value.equals("3")) {
                    holder.btnYes.setSelected(false);
                    holder.btnNo.setSelected(false);
                    holder.btnNA.setSelected(true);
                }
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnYes.setOnClickListener(view -> {

            holder.btnYes.setSelected(true);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(false);
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }
            answer1.setAnswer("1");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);
            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.btnNo.setOnClickListener(view -> {

            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);
            holder.btnNA.setSelected(false);
            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("2");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
        });

        holder.btnNA.setOnClickListener(view -> {
            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(true);
            Answer answer13 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);

            if (answer13 == null) {
                answer13 = new Answer(submissionID, formItem.getUploadId());
            }

            answer13.setAnswer("3");
            answer13.setRepeatID(formItem.getRepeatId());
            answer13.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer13.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
        });
    }

    private void bindYesNoNAOptionalHolder(final YesNoNAHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtQuestion.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        holder.btnYes.setSelected(false);
        holder.btnNo.setSelected(false);
        holder.btnNA.setSelected(false);

        holder.llYes.setVisibility(formItem.isYesNotVisible() ? View.GONE : View.VISIBLE);
        holder.llNo.setVisibility(formItem.isNoNotVisible() ? View.GONE : View.VISIBLE);
        holder.llNA.setVisibility(formItem.isNaNotVisible() ? View.GONE : View.VISIBLE);

        if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
            String value = answer.getAnswer();
            if (value.equals("1")) {
                holder.btnYes.setSelected(true);
                holder.btnNo.setSelected(false);
                holder.btnNA.setSelected(false);
            } else if (value.equals("2")) {
                holder.btnYes.setSelected(false);
                holder.btnNo.setSelected(true);
                holder.btnNA.setSelected(false);
            } else if (value.equals("3")) {
                holder.btnYes.setSelected(false);
                holder.btnNo.setSelected(false);
                holder.btnNA.setSelected(true);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnYes.setOnClickListener(view -> {

            holder.btnYes.setSelected(true);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(false);
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
            }
            answer1.setAnswer("1");
            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.btnNo.setOnClickListener(view -> {

            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);
            holder.btnNA.setSelected(false);
            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
            }

            answer12.setAnswer("2");
            dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
        });

        holder.btnNA.setOnClickListener(view -> {
            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(true);

            Answer answer13 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);

            if (answer13 == null) {
                answer13 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
                answer13.setAnswer("3");
                if (formItems.get(position + 1) != null) {
                    formItems.get(position + 1).setOptional(true);
                }
                dbHandler.replaceData(Answer.DBTable.NAME, answer13.toContentValues());
            } else {
                if (formItem.isYesNotVisible() && formItem.isNoNotVisible()) {
                    holder.btnNA.setSelected(false);
                    dbHandler.removeAnswer(answer13);
                    if (formItems.get(position + 1) != null) {
                        formItems.get(position + 1).setOptional(false);
                    }
                }
            }

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
        });
    }


    private void bindRepeatItemHolder(RepeatItemHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        holder.view.setOnClickListener(view -> {
            repeatCount = formItem.getRepeatCount();
            notifyDataSetChanged();
        });
    }


    private void bindPhotoHolder(PhotoHolder holder, int position) {
        FormItem formItem = formItems.get(position);

        if (formItem.getTitle().equalsIgnoreCase("Take Photo or Video")) {
            Answer answer = dbHandler.getAnswer(submissionID, "photoTypes", null, 0);
            if (answer != null) {
                formItem.setPhotoId(answer.getAnswer());
                ArrayList<Photo> photos = formItem.getPhotos();
                if (photos != null && !photos.isEmpty()) {
                    for (Photo photo :
                            photos) {
                        photo.setPhoto_id(formItem.getPhotoId());
                    }
                }
            }
        }

        if (holder.adapterPhoto == null) {
            holder.adapterPhoto = new AdapterPhoto(context, submissionID, formItem, repeatCount, this);
            holder.recyclerView.setAdapter(holder.adapterPhoto);
        }

        holder.adapterPhoto.update(formItem);
        holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload), formItem.getPhotos().size(), formItem.getPhotoRequired()));
        if (holder.adapterPhoto.getPhotosTaken() == 0) {
            holder.recyclerView.setVisibility(View.GONE);
            holder.imgBtnCamera.setVisibility(View.VISIBLE);
            holder.imgBtnCamera.setOnClickListener(view -> listener.openCamera(submissionID, formItem, repeatCount));
        } else {
            holder.imgBtnCamera.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.VISIBLE);
        }


        if (formItem.getTitle().equalsIgnoreCase("Damage Photo")) {
            holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload_damage), formItem.getPhotoSize()));
        } else {
            if (formItem.getPhotoSize() >= 100) {
                holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload_unlimited), formItem.getPhotoRequired()));
            } else {
                holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload), formItem.getPhotoSize(), formItem.getPhotoRequired()));
            }
        }
    }


    private void bindTakePhotoHolder(TakePhotoHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<Photo> photos = formItem.getPhotos();
        int size = photos.size();
        int required = 0;

        int photosTaken = 0;
        for (int i = 0; i < size; i++) {
            Photo photo = photos.get(i);
            if (!photo.isOptional()) {
                required++;
            }
            Answer answer = dbHandler.getAnswer(submissionID,
                    photo.getPhoto_id(),
                    repeatCount, photo.getTitle());

            if (answer != null) {
                photo.setIsVideo(answer.isPhoto() == 2);
                photo.setUrl(answer.getAnswer());
                photosTaken++;
            }
        }

        if (size >= 100) {
            holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload_unlimited), required));
        } else {
            holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload), size, required));
        }

        holder.imgBtnCamera.setOnClickListener(view -> {
            if (submission.getJsonFileName().equalsIgnoreCase("take_photo.json") || submission.getJsonFileName().equalsIgnoreCase("sub_job_take_photo.json")) {
                Answer photoId = dbHandler.getAnswer(submissionID,
                        "photoTypes",
                        formItem.getRepeatId(), 0);

                for (int i = 0; i < photos.size(); i++) {
                    Photo photo = photos.get(i);
                    Answer answer = dbHandler.getAnswer(submissionID,
                            photo.getPhoto_id(),
                            repeatCount, photo.getTitle());


                    if (answer == null && photoId != null) {
                        photo.setPhoto_id(photoId.getAnswer());
                    }
                }
            }
            listener.openCamera(submissionID, formItem, repeatCount);
        });


        if (photosTaken == 0) {
            holder.recyclerView.setVisibility(View.GONE);
            holder.imgBtnCamera.setVisibility(View.VISIBLE);
        } else {
            holder.imgBtnCamera.setVisibility(View.VISIBLE);
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.recyclerView.setAdapter(new AdapterTakePhoto(context, submissionID, formItem, repeatCount, this));
        }
    }


    private void bindLogAndDigForkHolder(LogDigForkHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        ArrayList<LogMeasureForkItem> items = getLogRepeatedItems(formItem);
        if (items.isEmpty()) {
            holder.llRecycleContainer.setVisibility(View.GONE);
            holder.llAddContainer.setVisibility(View.VISIBLE);
        } else {
            holder.llRecycleContainer.setVisibility(View.VISIBLE);
            holder.llAddContainer.setVisibility(View.GONE);
            holder.recyclerView.setAdapter(new AdapterLogMeasure(context, formItem, items, repeatCount, FormAdapter.this));
        }


        holder.btnAddItem.setOnClickListener(view -> {
            repeatCount++;
            listener.openForkFragment(formItem, submissionID, repeatCount);

        });

        holder.llAddContainer.setOnClickListener(view -> listener.openForkFragment(formItem, submissionID, repeatCount));
    }

    private void bindForkCard(ForkCardHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());


        holder.view.setOnClickListener(v -> {
            int rC = 0;
            ArrayList<String> fields = formItem.getFields();
            ArrayList<Answer> answers = new ArrayList<>();


            if (submission.getJsonFileName().equalsIgnoreCase("log_dfe.json") || submission.getJsonFileName().equalsIgnoreCase("sub_job_log_dfe.json")) {
                answers.addAll(dbHandler.getRepeatedAnswers(submissionID, fields.get(0), "items"));
                answers.addAll(dbHandler.getRepeatedAnswers(submissionID, fields.get(0), "negItems"));
            } else {
                answers.addAll(dbHandler.getRepeatedAnswers(submissionID, fields.get(0), formItem.getRepeatId()));
            }

            for (int i = 0; i < answers.size(); i++) {
                if (rC <= answers.get(i).getRepeatCount()) {
                    rC = answers.get(i).getRepeatCount();
                }
            }

            rC = rC + 1;

            if (submission.getJsonFileName().equalsIgnoreCase("briefing_sign.json")) {
                getBreifingRead(rC, fields.get(0), formItem.getRepeatId());
            }
            listener.openForkFragment(formItem, submissionID, rC);
        });
    }

    private void getBreifingRead(int repeatCount, String uploadId, String repeatId) {
        Answer answer = dbHandler.getAnswer(submissionID, uploadId, repeatId, repeatCount);
        if (answer == null) {
            answer = new Answer(submissionID, uploadId);
        }

        answer.setRepeatID(repeatId);
        answer.setRepeatCount(repeatCount);

        User user = dbHandler.getUser();
        if (user == null) {
            return;
        }
        ArrayList<OperativeTemplate> operativeTemplates = dbHandler.getOperativeHseq();
        for (OperativeTemplate ot : operativeTemplates) {
            if (ot.getOperativeId().equalsIgnoreCase(user.getuserId())) {

                answer.setDisplayAnswer(ot.getOperativeFullName());
                answer.setAnswer(ot.getOperativeId());
                dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
                return;
            }
        }
    }

    private void bindForkHolder(ForkHolder holder, final int position) {
        final FormItem item = formItems.get(position);
        holder.btnFork.setText(item.getTitle());

        holder.btnFork.setOnClickListener(view -> {
            if (focusedEditText != null) {
                focusedEditText.clearFocus();
            }

            if (!validate()) {
                return;
            }

            reInflateItems(true);
        });
    }

    private void bindSignatureHolder(final SignatureHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        if (!TextUtils.isEmpty(themeColor)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.btnClear.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
            }
        }
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

        holder.imgSignature.setImageDrawable(null);
        if (answer != null) {
            Glide.with(context).load(answer.getAnswer()).into(holder.imgSignature);
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.view.setOnClickListener(view -> listener.openSignature(formItem, submissionID, repeatCount));


        holder.btnClear.setOnClickListener(view -> {
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
            if (answer1 != null) {
                dbHandler.removeAnswer(answer1);
            }
            holder.imgSignature.setImageDrawable(null);
        });
    }


    private void bindTimeDropDownHolder(final DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.txtValue.setText("");
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtValue.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        final DropdownTimer dropdownMenu = new DropdownTimer(context, (hours, minutes) -> {
            StringBuilder sb = new StringBuilder();
            if (hours.isEmpty()) {
                hours = "00";
            }

            if (minutes.isEmpty()) {
                minutes = "00";
            }
            sb.append(hours);
            sb.append(":");
            sb.append(minutes);

            holder.txtValue.setText(sb.toString());

            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer(sb.toString());
            answer1.setDisplayAnswer(sb.toString());
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());

        });


        holder.view.setOnClickListener(view -> dropdownMenu.show());
    }

    private void bindDropDownHolder(final DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.view.setBackground(null);
        holder.txtValue.setText("");
        Answer answer = dbHandler.getAnswer(submissionID,
                formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtValue.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }


        holder.view.setOnClickListener(view -> {

            if (isScheduledInspection && formItem.getKey().equalsIgnoreCase(HseqDataset.DBTable.InspectionHseq)) {
                listener.showValidationDialog("Warning", "Already selected inspection type");
                return;
            }
            String uploadId = formItem.getUploadId();
            listener.showProgressBar();

            new DropDownCalls(formItem, submission, estimateGangId, new DropDownDataListner() {
                @Override
                public void success(ArrayList<DropDownItem> items , boolean isDependOnDatasetEndpoint) {
                    listener.hideProgressBar();
                    if (items.isEmpty()) {
                        String message = "Data can't be fetched due to slow internet connection.";
                        if(!isDependOnDatasetEndpoint){
                            message = "Data is not available.";
                        }
                        listener.showErrorDialog("Error", message, false);
                        return;
                    }

                    if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)
                            || formItem.getKey().equalsIgnoreCase(JobWorkItem.DBTable.NAME)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.bookOperatives)
                            || formItem.getKey().equalsIgnoreCase(TimesheetOperative.DBTable.NAME)

                    ) {
                        Intent intent = new Intent(context, ListActivity.class);
                        intent.putExtra("submissionID", submissionID);
                        intent.putExtra("repeatId", formItem.getRepeatId());
                        intent.putExtra("uploadId", uploadId);
                        intent.putExtra("keyItemType", formItem.getKey());
                        intent.putExtra("isMultiSelection", formItem.isMultiSelection());
                        intent.putExtra("isConcatDisplayText", formItem.isConcatDisplayText());
                        intent.putExtra("repeatCount", repeatCount);
                        intent.putExtra(ListActivity.ARGS_THEME_COLOR, themeColor);
                        context.startActivity(intent);
                        return;
                    } else if ((!TextUtils.isEmpty(submission.getJsonFileName()) && !submission.getJsonFileName().equalsIgnoreCase("request_task.json")) && (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.poleTypes)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.surfaceTypes)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.materialTypes)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.blockTerminalTypes)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.anchorTypes)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.jointClosureTypes)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.aerialCables)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.ugCableTypes)
                            || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dacTypes))) {
                        Intent intent = new Intent(context, ListEditActivity.class);
                        intent.putExtra("submissionID", submissionID);
                        intent.putExtra("repeatId", formItem.getRepeatId());
                        intent.putExtra("uploadId", uploadId);
                        intent.putExtra("keyItemType", formItem.getKey());
                        intent.putExtra("isMultiSelection", formItem.isMultiSelection());
                        intent.putExtra("repeatCount", repeatCount);
                        context.startActivity(intent);
                        return;
                    }

                    final DropdownMenu dropdownMenu = DropdownMenu.newInstance(items);

                    dropdownMenu.setListener(position1 -> {
                        holder.txtValue.setText(items.get(position1).getDisplayItem());

                        Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                                formItem.getRepeatId(), repeatCount);
                        if (answer1 == null) {
                            answer1 = new Answer(submissionID, formItem.getUploadId());
                        }
                        if (formItem.getKey().equalsIgnoreCase("bookOperatives")
                                && formItem.getUploadId().equalsIgnoreCase("userIds")) {
                            answer1.setIsMultiList(1);
                        }
                        answer1.setAnswer(items.get(position1).getUploadValue());
                        answer1.setDisplayAnswer(items.get(position1).getDisplayItem());
                        answer1.setRepeatID(formItem.getRepeatId());
                        answer1.setRepeatCount(repeatCount);
                        dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                        if (needToBeNotified(formItem)) {
                            reInflateItems(true);
                        }
                    });
                    listener.showBottomSheet(dropdownMenu);
                }

                @Override
                public void showValidationDialog(String title, String message) {
                    listener.showValidationDialog(title , message);
                }
            }).getDropDownItems();
        });
    }


    private void bindDialogScreen(final DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.txtValue.setText("");
        Answer answer = dbHandler.getAnswer(submissionID,
                formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtValue.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.view.setOnClickListener(view -> listener.openForkFragment(formItem, submissionID, repeatCount));
    }


    private void bindCheckBox(CheckBoxHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID,
                formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);


        if (answer != null) {
            String value = answer.getAnswer();
            if (value == null) {
                holder.checkBox.setChecked(formItem.isChecked());
            } else {
                holder.checkBox.setChecked(value.equals("true"));
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer(isChecked ? "true" : "false");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);


            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
        });
    }

    private void bindSwitchHolder(SwitchHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.btnSwitch.setChecked(formItem.isChecked());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        if (answer != null) {
            String value = answer.getAnswer();
            holder.btnSwitch.setChecked(!TextUtils.isEmpty(value) &&
                    (value.equals("true") || value.equals("1")));

        } else if (missingAnswerMode && !formItem.isOptional()) {

            holder.view.setBackground(redBG);
        }

        if (answer == null) {
            if (submission.getJsonFileName().equalsIgnoreCase("good_2_go.json") && (formItem.getUploadId().equalsIgnoreCase("trafficManagementMeetingRequired")
                    || formItem.getUploadId().equalsIgnoreCase("trafficManagementRequired"))) {
                answer = new Answer(submissionID, formItem.getUploadId());
                answer.setAnswer("2");
                answer.setRepeatID(formItem.getRepeatId());
                answer.setRepeatCount(repeatCount);

                dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
            }
        }


        holder.btnSwitch.setOnClickListener(v -> {
            Answer answer1 = dbHandler.getAnswer(submissionID,
                    formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            if (submission.getJsonFileName().equalsIgnoreCase("good_2_go.json") ||
                    submission.getJsonFileName().equalsIgnoreCase("sub_job_pre_site_survey.json")) {
                answer1.setAnswer(((SwitchCompat) v).isChecked() ? "1" : "2");
            } else {
                answer1.setAnswer(((SwitchCompat) v).isChecked() ? "true" : "false");
            }
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
        });
    }

    private void bindRFNAToggleHolder(RFNAToggleHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                if (value.equalsIgnoreCase("1")) {
                    holder.radioButtonRFNA1.setChecked(true);
                } else {
                    holder.radioButtonRFNA2.setChecked(true);
                }
            }

        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.radioGroup.setOnCheckedChangeListener((group, checkedId) -> {
            String value = null;
            switch (checkedId) {
                case R.id.radio_button_rfna1:
                    value = "true";
                    break;
                case R.id.radio_button_rfna2:
                    value = "false";
                    break;
            }
            Answer answer1 = dbHandler.getAnswer(submissionID,
                    formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer(value);
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            AppPreferences.putString("RadioButton_" + submission.getJobID(), value);
            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
        });
    }

    private void bindBoldTextHolder(BoldTextHolder holder, int position) {
        FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
    }

    private void bindYesNoItem(final YesNoHolder holder, int position) {
        final FormItem formItem = formItems.get(position);

        holder.txtQuestion.setText(formItem.getTitle());
        holder.view.setBackground(null);
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        holder.btnYes.setSelected(false);
        holder.btnNo.setSelected(false);

        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && (value.equals("true") || value.equals("1"))) {
                holder.btnYes.setSelected(true);
                holder.btnNo.setSelected(false);
            } else if (value != null && (value.equals("false") || value.equals("2"))) {
                holder.btnYes.setSelected(false);
                holder.btnNo.setSelected(true);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnYes.setOnClickListener(view -> {

            holder.btnYes.setSelected(true);
            holder.btnNo.setSelected(false);


            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }
            boolean isNumber = isSubJob() && (submission.getJsonFileName().equalsIgnoreCase("poling_job_data.json")
            || submission.getJsonFileName().equalsIgnoreCase("poling_fluidity_task.json")
            || submission.getJsonFileName().equalsIgnoreCase("sub_job_poling_solution.json")
            || submission.getJsonFileName().equalsIgnoreCase("poling_asset_data.json")
            || submission.getJsonFileName().equalsIgnoreCase("poling_planning_risk_assessment.json")
            || submission.getJsonFileName().equalsIgnoreCase("sub_job_asset_survey.json"));


            if (submission.getJsonFileName().equalsIgnoreCase("hoist_risk_assessment.json")
                    || submission.getJsonFileName().equalsIgnoreCase("good_2_go.json")
                    || submission.getJsonFileName().equalsIgnoreCase("poling_risk_assessment.json")
            || isNumber) {
                answer1.setAnswer("1");
            } else {
                answer1.setAnswer("true");
            }

            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);
            dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

            if (formItem.getStopWork() == 1) {
                FragmentStopWork fragmentStopWork =
                        FragmentStopWork.newInstance(submission);
                listener.openFragment(fragmentStopWork);
            }
        });

        holder.btnNo.setOnClickListener(view -> {
            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);

            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            boolean isNumber = isSubJob() && (submission.getJsonFileName().equalsIgnoreCase("poling_job_data.json")
                    || submission.getJsonFileName().equalsIgnoreCase("poling_fluidity_task.json")
                    || submission.getJsonFileName().equalsIgnoreCase("sub_job_poling_solution.json")
                    || submission.getJsonFileName().equalsIgnoreCase("poling_asset_data.json")
                    || submission.getJsonFileName().equalsIgnoreCase("poling_planning_risk_assessment.json")
                    || submission.getJsonFileName().equalsIgnoreCase("sub_job_asset_survey.json"));

            if (submission.getJsonFileName().equalsIgnoreCase("hoist_risk_assessment.json")
                    || submission.getJsonFileName().equalsIgnoreCase("good_2_go.json")
                    || submission.getJsonFileName().equalsIgnoreCase("poling_risk_assessment.json")
            || isNumber) {
                answer12.setAnswer("2");
            } else {
                answer12.setAnswer("false");
            }

            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

            if (formItem.getStopWork() == 2) {
                FragmentStopWork fragmentStopWork = FragmentStopWork.newInstance(submission);
                listener.openFragment(fragmentStopWork);
            }

        });

    }

    private boolean needToBeNotified(FormItem formItem) {

        return (formItem.getEnables() != null && !formItem.getEnables().isEmpty())
                || (formItem.getFalseEnables() != null && !formItem.getFalseEnables().isEmpty()
                || (formItem.getNaEnables() != null && !formItem.getNaEnables().isEmpty()));
    }

    public void reInflateItems(boolean isNotified) {

        this.formItems.clear();

        for (int i = 0; i < originalItems.size(); i++) {
            FormItem item = originalItems.get(i);
            addEnableItems(item);
        }

        if (isNotified) {
            notifyDataSetChanged();
        }
        addListItems(new ArrayList<>(formItems));
        addRepeatedVisitorSignature(new ArrayList<>(formItems));
        addStockItems(new ArrayList<>(formItems));
        addAnyTasks();
    }


    private void addEnableItems(FormItem item) {

        String value = getAnswerValue(item, repeatCount);
        if (TextUtils.isEmpty(value)) {
            if (!TextUtils.isEmpty(submission.getJsonFileName()) &&
                    submission.getJsonFileName().equalsIgnoreCase("slg_inspection.json") &&
                    !TextUtils.isEmpty(item.getUploadId()) &&
                    item.getUploadId().equalsIgnoreCase("actionWasRectifiedOnSite")) {
                Answer answer = dbHandler.getAnswer(submissionID, item.getUploadId(), item.getRepeatId(), item.getRepeatCount());
                if (answer == null) {
                    answer = new Answer(submissionID, "actionWasRectifiedOnSite", item.getRepeatId(), item.getRepeatCount());
                }
                answer.setAnswer("false");
                dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
                addEnableItems(item, "false");
                return;
            }

            this.formItems.add(item);

            return;
        }

        if (item.getFormType() == FormItem.TYPE_DROPDOWN) {
            addEnableItems(item, value);
        } else if (item.getFormType() == FormItem.TYPE_YES_NO ||
                item.getFormType() == FormItem.TYPE_YES_NO_NA ||
                item.getFormType() == FormItem.TYPE_YES_NO_NA_OPTIONAL ||
                item.getFormType() == FormItem.TYPE_YES_NO_tooltip ||
                item.getFormType() == FormItem.TYPE_YES_NO_NA_tooltip ||
                item.getFormType() == FormItem.TYPE_SWITCH) {
            addEnableItems(item, value);
        } else {
            this.formItems.add(item);
        }
    }

    private void addEnableItems(FormItem item, String value) {
        ArrayList<FormItem> toBeAdded = new ArrayList<>();
        ArrayList<FormItem> enableItems = item.getEnables();
        ArrayList<FormItem> falseEnableItems = item.getFalseEnables();
        ArrayList<FormItem> naEnableItems = item.getNaEnables();
        String jsonFileName = submission.getJsonFileName();
        if (item.getFormType() == FormItem.TYPE_DROPDOWN) {
            String uploadId = item.getUploadId();
            if (!TextUtils.isEmpty(jsonFileName) && jsonFileName.equalsIgnoreCase("request_task.json")
                    && !TextUtils.isEmpty(uploadId) && uploadId.equalsIgnoreCase("siteActivityTypeId")) {
                if (value != null) {
                    if (value.equalsIgnoreCase("1") || value.equalsIgnoreCase("6")) {
                        toBeAdded.addAll(enableItems);
                    } else {
                        if (!value.equalsIgnoreCase("3")) {
                            //not inflating item when muckaway selected
                            toBeAdded.addAll(falseEnableItems);
                        }
                    }
                }
            } else if (enableItems != null) {
                toBeAdded.addAll(enableItems);
            }
        } else if (value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true")) {
            if (enableItems != null && !enableItems.isEmpty()) {
                toBeAdded.addAll(enableItems);
            }
        } else if (value.equalsIgnoreCase("2") ||
                value.equalsIgnoreCase("0") || value.equalsIgnoreCase("false")) {
            if (falseEnableItems != null && !falseEnableItems.isEmpty()) {
                toBeAdded.addAll(falseEnableItems);
            }
        } else if (value.equalsIgnoreCase("3")) {
            if (naEnableItems != null && !naEnableItems.isEmpty()) {
                toBeAdded.addAll(naEnableItems);
            }
        }

        this.formItems.add(item);
        if (!toBeAdded.isEmpty()) {
            for (FormItem fi : toBeAdded) {
                addEnableItems(fi);
            }
        }
    }

    public Answer getAnswer(FormItem item, int repeatCount) {
        if (TextUtils.isEmpty(item.getUploadId())) {
            return null;
        }
        return dbHandler.getAnswer(submissionID,
                item.getUploadId(), item.getRepeatId(), repeatCount);
    }

    public Answer getAnswer(String uploadId, String repeatId, int repeatCount) {
        if (TextUtils.isEmpty(uploadId)) {
            return null;
        }
        return dbHandler.getAnswer(submissionID,
                uploadId, repeatId, repeatCount);
    }

    String getAnswerValue(FormItem item, int repeatCount) {
        Answer answer = getAnswer(item, repeatCount);
        if (answer == null) {
            return null;
        }

        return answer.getAnswer();
    }

    private void addAnyTasks() {
        for (int c = 0; c < formItems.size(); c++) {
            FormItem item = formItems.get(c);
            int formType = item.getFormType();
            String type = item.getTaskType(formType);
            if (!TextUtils.isEmpty(type)) {
                if (type.equalsIgnoreCase("task_list_timesheet_item")) {
                    addTimeSheetTasksAt(item, c, type);
                    return;
                }
                addTasksAt(item, c, type);
                return;
            }
        }
    }

    private void addTimeSheetTasksAt(final FormItem item, final int position, final String type) {

        int formType = item.getFormType();
        if (formType == FormItem.TYPE_TASK_LIST_TIMESHEET) {
            Answer answer = dbHandler.getAnswer(submissionID, "weekCommencing", null, 0);
            if (answer == null) {
                return;
            }

            String weekCommencing = answer.getAnswer();
            if (TextUtils.isEmpty(weekCommencing)) {
                return;
            }
            LinkedHashMap<String , ArrayList<LogHourItem>> timeSheetMap = dbHandler.getTimeSheetLogHoursByWeek(weekCommencing);
            ArrayList<FormItem> list = new ArrayList<>();
            long totalTime = 0;
            int repeatCount = 0;

            Set<String> keySet = timeSheetMap.keySet();
            Calendar calendar = Calendar.getInstance();
            for (String dayWorked : keySet){
                ArrayList<LogHourItem> timeSheetHours = timeSheetMap.get(dayWorked);
                Date date = Utils.parseDate(dayWorked , "yyyy-MM-dd'T'HH:mm:ss");
                if(date != null) {
                    calendar.setTime(date);
                    String dayName = calendar.getDisplayName(Calendar.DAY_OF_WEEK , Calendar.LONG , Locale.ENGLISH);
                    if(timeSheetHours != null && !timeSheetHours.isEmpty()) {
                        FormItem dayItem = new FormItem("day_view", dayName, dayWorked, null, true);
                        dayItem.setTimeSheetHour(timeSheetHours.get(0));
                        list.add(dayItem);
                    }

                }

                if(timeSheetHours!= null) {
                    for (int i = 0; i < timeSheetHours.size(); i++) {
                        LogHourItem timeSheetHour = timeSheetHours.get(i);
                        repeatCount = timeSheetHour.saveTimeHoursID(submissionID, null, repeatCount);
                        totalTime += timeSheetHour.getTotalTime();
                        FormItem formItem = new FormItem(type, timeSheetHour, true);
                        formItem.setRepeatId(item.getRepeatId());
                        formItem.setRepeatCount(repeatCount);
                        Amends amends = JsonReader.loadAmends(context, "ammend_task_timesheet.json");
                        formItem.setDialogItems(amends.getDialogItems());
                        list.add(formItem);
                    }
                }
            }


            String time = (totalTime / 60) + "h" + (totalTime % 60) + "m";
            String title = "Total Hours Worked : " + time;
            FormItem formItem = new FormItem("total_worked_hours", title, "total_worked_hours", null, true);
            Answer timeWorked = dbHandler.getAnswer(submissionID, "total_worked_hours", null, 0);
            if (timeWorked == null) {
                timeWorked = new Answer(submissionID, "total_worked_hours", null, 0);
            }
            timeWorked.setShouldUpload(false);
            timeWorked.setAnswer(time);
            dbHandler.replaceData(Answer.DBTable.NAME, timeWorked.toContentValues());
            list.add(formItem);

            formItems.addAll(position + 1, list);
            notifyDataSetChanged();
        }
    }


    private void addTasksAt(final FormItem item, final int position, final String type) {
        if (selectableTasks != null && !selectableTasks.isEmpty()) {
            if(isSubJob()){
                initSubTasks(new ArrayList<>(selectableTasks), position, type, item.getFormType());
            }else {
                initTasks(new ArrayList<>(selectableTasks), position, type, item.getFormType());
            }
            return;
        }

        int formType = item.getFormType();
        if (formType == FormItem.TYPE_TASK_LOG_REINSTATEMENT || formType == FormItem.TYPE_TASK_LOG_MUCKAWAY
                || formType == FormItem.TYPE_TASK_LOG_BACKFILL
                || formType == FormItem.TYPE_TASK_LOG_SERVICE || formType == FormItem.TYPE_TASK_SITE_CLEAR) {
            ArrayList<BaseTask> tasks = dbHandler.getTaskItems(submission.getJobID(), item.getTaskId(), isSubJob()? 1 : 0);
            if(isSubJob()) {
                initSubTasks(new ArrayList<>(tasks), position, type, item.getFormType());
            } else{
                initTasks(new ArrayList<>(tasks), position, type, item.getFormType());
            }
        }
    }

    private void initTasks(List<BaseTask> tasks, final int position, final String type, final int formType) {
        selectableTasks.clear();
        if (!submission.getJsonFileName().equalsIgnoreCase("job_site_clear.json") && (tasks == null || tasks.isEmpty())) {
            listener.showErrorDialog("Error", "Site Activity task not found.", true);
            return;
        }

        ArrayList<FormItem> list = new ArrayList<>();
        ArrayList<Answer> answers = dbHandler.getMultiAnswers(submissionID, "completedSiteActivityTaskIds", repeatCount);
        for (int i = 0; i < tasks.size(); i++) {
            BaseTask task = tasks.get(i);
            selectableTasks.add(task);

            FormItem formItem = new FormItem(type, task, true);
            formItem.setRepeatCount(i);
            list.add(formItem);
            if (answers != null && !answers.isEmpty()) {
                setIfTaskSelected(task, answers);
            }

            if (formType == FormItem.TYPE_TASK_LOG_BACKFILL ||
                    formType == FormItem.TYPE_TASK_LOG_REINSTATEMENT ||
                    formType == FormItem.TYPE_TASK_LOG_MUCKAWAY ||
                    formType == FormItem.TYPE_TASK_LOG_SERVICE ||
                    formType == FormItem.TYPE_TASK_SITE_CLEAR ||
                    formType == FormItem.TYPE_TASK_VIEW_DIG_MEASURES ||
                    formType == FormItem.TYPE_TASK_VIEW_BACKFILL_MEASURES ||
                    formType == FormItem.TYPE_TASK_VIEW_REINST_MEASURES) {

                String repeatId = null;

                if (formType == FormItem.TYPE_TASK_VIEW_DIG_MEASURES ||
                        formType == FormItem.TYPE_TASK_VIEW_BACKFILL_MEASURES ||
                        formType == FormItem.TYPE_TASK_VIEW_REINST_MEASURES) {
                    repeatId = "Items";
                }

                ArrayList<FormItem> formList;
                if (formType == FormItem.TYPE_TASK_LOG_SERVICE) {
                    Amends amends = JsonReader.loadAmends(context, "ammend_service_task_form.json");
                    formItem.setDialogItems(amends.getDialogItems());
                    formList = amends.getDialogItems();
                } else if (formType == FormItem.TYPE_TASK_SITE_CLEAR) {
                    Amends amends = JsonReader.loadAmends(context, "ammend_service_task_clear_form.json");
                    formItem.setDialogItems(amends.getDialogItems());
                    formList = amends.getDialogItems();
                } else {
                    Amends amends = JsonReader.loadAmends(context, "ammend_task_form.json");
                    formItem.setDialogItems(amends.getDialogItems());
                    formList = amends.getDialogItems();
                }

                Answer siteActivityTaskId = dbHandler.getAnswer(submissionID, "siteActivityTaskId", formList.get(0).getRepeatId(), i);
                if (siteActivityTaskId == null) {
                    siteActivityTaskId = new Answer(submissionID, "siteActivityTaskId", formList.get(0).getRepeatId(), i);
                    siteActivityTaskId.setAnswer(task.getSiteActivityTaskId());
                    dbHandler.replaceData(Answer.DBTable.NAME, siteActivityTaskId.toContentValues());
                }

                if (formType != FormItem.TYPE_TASK_LOG_MUCKAWAY) {
                    for (FormItem fi : formList) {
                        if (repeatId != null) {
                            fi.setRepeatId(repeatId);
                        }
                        Answer answer = dbHandler.getAnswer(submissionID, fi.getUploadId(), fi.getRepeatId(), i);
                        String uploadId = fi.getUploadId();
                        if (answer == null) {
                            answer = new Answer(submissionID, uploadId, fi.getRepeatId(), i);
                            task.setAnswer(answer);
                            dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
                        }
                    }
                }
            }
        }


        formItems.addAll(position + 1, list);
        notifyDataSetChanged();
    }


    private void initSubTasks(List<BaseTask> tasks, final int position, final String type, final int formType) {
        selectableTasks.clear();
        if (!submission.getJsonFileName().equalsIgnoreCase("job_site_clear.json") && (tasks == null || tasks.isEmpty())) {
            listener.showErrorDialog("Error", "Site Activity task not found.", true);
            return;
        }

        ArrayList<FormItem> list = new ArrayList<>();
        ArrayList<Answer> answers = dbHandler.getMultiAnswers(submissionID, "completedSiteActivityTaskIds", repeatCount);
        for (int i = 0; i < tasks.size(); i++) {
            BaseTask task = tasks.get(i);
            selectableTasks.add(task);

            FormItem formItem = new FormItem(type, task, true);
            formItem.setRepeatCount(i);
            list.add(formItem);
            if (answers != null && !answers.isEmpty()) {
                setIfTaskSelected(task, answers);
            }

            if (formType == FormItem.TYPE_TASK_LOG_BACKFILL ||
                    formType == FormItem.TYPE_TASK_LOG_REINSTATEMENT ||
                    formType == FormItem.TYPE_TASK_LOG_MUCKAWAY ||
                    formType == FormItem.TYPE_TASK_LOG_SERVICE ||
                    formType == FormItem.TYPE_TASK_SITE_CLEAR ||
                    formType == FormItem.TYPE_TASK_VIEW_DIG_MEASURES ||
                    formType == FormItem.TYPE_TASK_VIEW_BACKFILL_MEASURES ||
                    formType == FormItem.TYPE_TASK_VIEW_REINST_MEASURES) {

                String repeatId = null;

                if (formType == FormItem.TYPE_TASK_VIEW_DIG_MEASURES ||
                        formType == FormItem.TYPE_TASK_VIEW_BACKFILL_MEASURES ||
                        formType == FormItem.TYPE_TASK_VIEW_REINST_MEASURES) {
                    repeatId = "Items";
                }

                ArrayList<FormItem> formList;
                if (formType == FormItem.TYPE_TASK_LOG_SERVICE) {
                    Amends amends = JsonReader.loadAmends(context, "ammend_service_task_form.json");
                    formItem.setDialogItems(amends.getDialogItems());
                    formList = amends.getDialogItems();
                } else if (formType == FormItem.TYPE_TASK_SITE_CLEAR) {
                    Amends amends = JsonReader.loadAmends(context, "ammend_service_task_clear_form.json");
                    formItem.setDialogItems(amends.getDialogItems());
                    formList = amends.getDialogItems();
                } else {
                    Amends amends = JsonReader.loadAmends(context, "ammend_task_form.json");
                    formItem.setDialogItems(amends.getDialogItems());
                    formList = amends.getDialogItems();
                }

                for (FormItem fi : formList) {
                    fi.setRepeatId("tasks");
                    if (formType != FormItem.TYPE_TASK_LOG_MUCKAWAY) {
                        Answer answer = dbHandler.getAnswer(submissionID, fi.getUploadId(), fi.getRepeatId(), i);
                        String uploadId = fi.getUploadId();
                        if (answer == null) {
                            answer = new Answer(submissionID, uploadId, fi.getRepeatId(), i);
                            task.setAnswer(answer);
                            dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
                        }
                    }
                }

                Answer siteActivityTaskId = dbHandler.getAnswer(submissionID, "siteActivityTaskId", formList.get(0).getRepeatId(), i);
                if (siteActivityTaskId == null) {
                    siteActivityTaskId = new Answer(submissionID, "siteActivityTaskId", formList.get(0).getRepeatId(), i);
                    siteActivityTaskId.setAnswer(task.getSiteActivityTaskId());
                    dbHandler.replaceData(Answer.DBTable.NAME, siteActivityTaskId.toContentValues());
                }

                Answer siteActivityTypeId = dbHandler.getAnswer(submissionID, "siteActivityTypeId", formList.get(0).getRepeatId(), i);
                if (siteActivityTypeId == null) {
                    siteActivityTypeId = new Answer(submissionID, "siteActivityTypeId", formList.get(0).getRepeatId(), i);
                    siteActivityTypeId.setAnswer(String.valueOf(task.getSiteActivityTypeId()));
                    dbHandler.replaceData(Answer.DBTable.NAME, siteActivityTypeId.toContentValues());
                }

                if (formType != FormItem.TYPE_TASK_LOG_MUCKAWAY) {
                    for (FormItem fi : formList) {
                        if (repeatId != null) {
                            fi.setRepeatId(repeatId);
                        }
                        Answer answer = dbHandler.getAnswer(submissionID, fi.getUploadId(), fi.getRepeatId(), i);
                        String uploadId = fi.getUploadId();
                        if (answer == null) {
                            answer = new Answer(submissionID, uploadId, fi.getRepeatId(), i);
                            task.setAnswer(answer);
                            dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
                        }
                    }
                }
            }
        }


        formItems.addAll(position + 1, list);
        notifyDataSetChanged();
    }

    private void setIfTaskSelected(BaseTask task, ArrayList<Answer> answers) {
        for (int c = 0; c < answers.size(); c++) {
            Answer answer = answers.get(c);
            String value = answer.getAnswer();
            if (!TextUtils.isEmpty(value) &&
                    value.equalsIgnoreCase(String.valueOf(task.getSiteActivityTaskId()))) {
                task.setSelected(true);
            }
        }
    }


    private void bindLongTextHolder(LongTextHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.editText.setHint(formItem.getHint());
        holder.editText.setText("");

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.editText.setText(value);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                Answer answer1 = dbHandler.getAnswer(submissionID,
                        formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
                }

                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });

    }

    private void bindShortTextHolder(ShortTextHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.editText.setHint(formItem.getHint());
        holder.editText.setText("");

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {

            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.editText.setText(value);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
                }
                if (answer1.getUploadID() != null &&
                        answer1.getUploadID().equalsIgnoreCase("visitorName")) {
                    if(submission.getJsonFileName().equalsIgnoreCase("visitor_attendance.json")) {
                        answer1.setIsMultiList(1);
                    }
                }

                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });

    }

    @Override
    public int getItemCount() {
        return formItems.size();
    }

    @Override
    public int getItemViewType(int position) {
        return formItems.get(position).getFormType();
    }


    public boolean validate() {
        if (isTimerRunning) {
            return false;
        }

        if (focusedEditText != null) {
            focusedEditText.clearFocus();
        }

        missingAnswerMode = false;
        int missingCount = 0;
        boolean isPhotoMissing = false;
        boolean ifDFEAdded = false;
        boolean isBackFillSelected = false;
        boolean isSerMatbase = false;
        boolean isTotalHoursValid = false;
        for (int c = 0; c < formItems.size(); c++) {
            FormItem item = formItems.get(c);
            if (item.getFormType() == FormItem.TYPE_PHOTO) {
                final ArrayList<Photo> photos = item.getPhotos();
                int photosNeeded = photos.size();
                for (int i = 0; i < photosNeeded; i++) {
                    Photo photo = photos.get(i);
                    Answer answer = dbHandler.getAnswer(submissionID, String.valueOf(photo.getPhoto_id()), repeatCount, photo.getTitle());
                    if (!photo.isOptional() && answer == null) {
                        missingCount++;
                        isPhotoMissing = true;
                    }

                }
            } else {
                if ((item.getFormType() == FormItem.TYPE_FORK_CARD ||
                        item.getFormType() == FormItem.TYPE_ADD_STORE_ITEM) && !item.isOptional()) {
                    ArrayList<String> fields = item.getFields();
                    if (fields != null && !fields.isEmpty()) {
                        ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                        if (answers == null || answers.isEmpty()) {
                            missingCount++;
                        }
                    }


                } else if ((item.getFormType() == FormItem.TYPE_ADD_POS_DFE) ||
                        (item.getFormType() == FormItem.TYPE_ADD_NEG_DFE)) {
                    ArrayList<String> fields = item.getFields();
                    if (fields != null && !fields.isEmpty()) {
                        ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                        if (answers != null && !answers.isEmpty()) {
                            ifDFEAdded = true;
                        }
                    }
                } else if (item.getFormType() == FormItem.TYPE_LOCATION) {
                    Answer answer = dbHandler.getAnswer(submissionID, "latitude", item.getRepeatId(), repeatCount);
                    if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
                        missingCount++;
                    }
                } else if (item.getFormType() == FormItem.TYPE_TASK_LOG_BACKFILL_ITEM) {
                    Answer answer = dbHandler.getAnswer(submissionID, "completedSiteActivityTaskIds",
                            null, repeatCount);
                    if (answer != null) {
                        isBackFillSelected = true;
                    }
                } else if (item.getFormType() == FormItem.TYPE_TASK_LOG_SERVICE_ITEM) {
                    Answer answer = dbHandler.getAnswer(submissionID, "completedSiteActivityTaskIds",
                            null, repeatCount);
                    if (answer != null) {
                        isSerMatbase = true;
                    }
                } else if (item.getFormType() == FormItem.TYPE_LOG_AND_DIG_FORK) {
                    if (getLogRepeatedItems(item).isEmpty()) {
                        missingCount++;
                    }
                } else if (item.getFormType() == FormItem.TYPE_ADD_LOG_MEASURE) {
                    ArrayList<String> fields = item.getFields();
                    if (fields != null && !fields.isEmpty()) {
                        ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                        if (answers == null || answers.isEmpty()) {
                            missingCount++;
                        }
                    }
                } else if (item.getFormType() == FormItem.TYPE_ADD_LOG_HOURS) {
                    ArrayList<String> fields = item.getFields();
                    if (fields != null && !fields.isEmpty()) {
                        ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                        if (answers == null || answers.isEmpty()) {
                            missingCount++;
                        }
                    }
                }else if (item.getFormType() == FormItem.TYPE_ADD_SITE_CLEAR) {

                } else if (item.getFormType() == FormItem.TYPE_TOTAL_WORKED_HOURS) {
                    Answer answer = dbHandler.getAnswer(submissionID, item.getUploadId(), item.getRepeatId(), repeatCount);
                    if (answer == null || TextUtils.isEmpty(answer.getAnswer()) || answer.getAnswer().equalsIgnoreCase("0h0m")) {
                        missingCount++;
                        isTotalHoursValid = true;
                    }
                } else {
                    if (!item.isOptional() && item.getUploadId() != null) {
                        Answer answer = dbHandler.getAnswer(submissionID, item.getUploadId(), item.getRepeatId(), repeatCount);
                        if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
                            missingCount++;
                        } else if (submission.getJsonFileName().equalsIgnoreCase("eng_comp.json") && (answer.getAnswer().length() < 10)) {
                            listener.showValidationDialog("Validation Error", "Please provide minimum 10 character for comment field");
                            missingCount++;
                        }
                    }
                }
            }
        }
        if (submission.getJsonFileName().equalsIgnoreCase("log_dfe.json") && !ifDFEAdded) {
            missingCount++;
            listener.showValidationDialog("Validation Error", "Please add Positive or negative DFEs");
        } else if (submission.getJsonFileName().equalsIgnoreCase("log_back_fill.json") && !isBackFillSelected) {
            listener.showValidationDialog("Validation Error", "Please select Log BackFill amendment!");
        } else if (submission.getJsonFileName().equalsIgnoreCase("service_material.json") && !isSerMatbase) {
            listener.showValidationDialog("Validation Error", "Please select Service/Material amendment!");
        } else if (isTotalHoursValid) {
            listener.showValidationDialog("Validation Error", "No hours have been logged for this week. Please log hours.");
        } else if (isPhotoMissing) {
            listener.showValidationDialog("Validation Error", "Photos are missing");
        }
        if (missingCount > 0) {
            missingAnswerMode = true;
            notifyDataSetChanged();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public void openCamera(FormItem formItem, int repeatCounter) {
        if (submission.getJsonFileName().
                equalsIgnoreCase("take_photo.json") || submission.getJsonFileName().
                equalsIgnoreCase("sub_job_take_photo.json")) {

            Answer photoId = dbHandler.getAnswer(submissionID,
                    "photoTypes",
                    null, 0);

            if (photoId == null) {
                return;
            }
            ArrayList<Photo> photos = formItem.getPhotos();
            for (int i = 0; i < photos.size(); i++) {
                Photo photo = photos.get(i);
                Answer answer = dbHandler.getAnswer(submissionID,
                        photo.getPhoto_id(),
                        repeatCount, photo.getTitle());

                if (answer == null) {
                    photo.setPhoto_id(photoId.getAnswer());
                }
            }
        }

        listener.openCamera(submissionID, formItem, repeatCount);


    }

    @Override
    public void onAllPhotosRemoved() {
        notifyDataSetChanged();
    }

    @Override
    public void onEdit(FormItem item, int position) {
        listener.openForkFragment(item, submissionID, item.getRepeatCount());
    }

    @Override
    public void onRemove(FormItem item, int position) {

        ArrayList<FormItem> items = item.getDialogItems();
        if (items != null && !items.isEmpty()) {
            for (FormItem fi :
                    items) {
                Answer answer = dbHandler.getAnswer(submissionID,
                        fi.getUploadId(), fi.getRepeatId(), fi.getRepeatCount());
                if (answer != null) {
                    dbHandler.removeAnswer(answer);
                }
            }

        }
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public void removeUnnecessaryTasks() {
        if (selectableTasks == null || selectableTasks.isEmpty()) {
            return;
        }

        for (int i = 0; i < selectableTasks.size(); i++) {
            BaseTask task = selectableTasks.get(i);
            if (!task.isSelected()) {
                dbHandler.deleteAnswers(submissionID, "amendments", i);
            }
        }
    }

    public void showEstimateOperative(JobEstimate jobEstimate, int pos) {
        if (jobEstimate.getGangId() != null && !jobEstimate.getGangId().isEmpty()) {
            this.estimateGangId = jobEstimate.getGangId();
            //this.estimateGangId = "48c10128-fd64-48b8-fc56-08d771b0792e";
            Answer answer1 = dbHandler.getAnswer(submissionID,
                    formItems.get(pos).getUploadId(), formItems.get(pos).getRepeatId(), repeatCount);

            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItems.get(pos).getUploadId());
            }
            List<OperativeTemplate> estimateList = dbHandler.getOperativeTemplateByGangId(estimateGangId);
            if (estimateList.size() > 0) {
                answer1.setAnswer(estimateList.get(0).getOperativeId());
                answer1.setDisplayAnswer(estimateList.get(0).getOperativeFullName());
                answer1.setRepeatID(formItems.get(pos).getRepeatId());
                answer1.setRepeatCount(repeatCount);
                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                if (pos != RecyclerView.NO_POSITION) {
                    notifyItemChanged(pos);
                }
            }
        }
        if (!TextUtils.isEmpty(jobEstimate.getJobId())) {
            if (jobEstimate.getJobId() != null && !jobEstimate.getJobId().isEmpty()) {
                Answer answerjobID = dbHandler.getAnswer(submissionID,
                        "jobId", null, 0);

                if (answerjobID == null) {
                    answerjobID = new Answer(submissionID, "jobId");
                }
                answerjobID.setAnswer(jobEstimate.getJobId());
                answerjobID.setDisplayAnswer(jobEstimate.getJobRef());
                answerjobID.setRepeatCount(0);
                dbHandler.replaceData(Answer.DBTable.NAME, answerjobID.toContentValues());
            }
        }
    }

    private void getTimeSheetHours() {
        Answer answer = dbHandler.getAnswer(submissionID, "weekCommencing", null, 0);
        if (answer == null) {
            return;
        }

        String weekCommencing = answer.getAnswer();
        if (TextUtils.isEmpty(weekCommencing)) {
            return;
        }

        ((FromActivityListener) context).showProgressBar();
        User user = dbHandler.getUser();

        CallUtils.enqueueWithRetry(APICalls.getTimesheetHours(user.gettoken(), weekCommencing) , new Callback<TimeSheetHours>() {
            @Override
            public void onResponse(@NonNull Call<TimeSheetHours> call, @NonNull Response<TimeSheetHours> response) {
                DBHandler.getInstance().clearTable(TimeSheetHour.DBTable.NAME);
                DBHandler.getInstance().removeMultiAnswersByUploadID(submissionID , "timesheetHoursIds");
                if (response.isSuccessful()) {
                    TimeSheetHours timeSheetHours = response.body();
                    if (timeSheetHours != null && !timeSheetHours.isEmpty()) {
                        timeSheetHours.setWeekCommencing();
                        timeSheetHours.toContentValues();
                        reInflateItems(true);
                    }
                }
                ((FromActivityListener) context).hideProgressBar();
            }

            @Override
            public void onFailure(@NonNull Call<TimeSheetHours> call, @NonNull Throwable t) {
                ((FromActivityListener) context).hideProgressBar();
            }
        });
    }
}
