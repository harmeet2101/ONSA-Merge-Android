package co.uk.depotnet.onsa.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import java.util.List;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ListActivity;
import co.uk.depotnet.onsa.activities.ListEditActivity;
import co.uk.depotnet.onsa.activities.ListStockItemActivity;
import co.uk.depotnet.onsa.activities.ListStoreItemActivity;
import co.uk.depotnet.onsa.barcode.ScannedBarcodeActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.formholders.BoldTextHolder;
import co.uk.depotnet.onsa.formholders.CalenderHolder;
import co.uk.depotnet.onsa.formholders.CheckBoxHolder;
import co.uk.depotnet.onsa.formholders.CurrentStoreHolder;
import co.uk.depotnet.onsa.formholders.DFEItemHolder;
import co.uk.depotnet.onsa.formholders.DateTimeHolder;
import co.uk.depotnet.onsa.formholders.DropDownHolder;
import co.uk.depotnet.onsa.formholders.DropDownNumber;
import co.uk.depotnet.onsa.formholders.ForkCardHolder;
import co.uk.depotnet.onsa.formholders.ForkHolder;
import co.uk.depotnet.onsa.formholders.LocationHolder;
import co.uk.depotnet.onsa.formholders.LogDigForkHolder;
import co.uk.depotnet.onsa.formholders.LongTextHolder;
import co.uk.depotnet.onsa.formholders.NumberHolder;
import co.uk.depotnet.onsa.formholders.PassFailHolder;
import co.uk.depotnet.onsa.formholders.PhotoHolder;
import co.uk.depotnet.onsa.formholders.RepeatItemHolder;
import co.uk.depotnet.onsa.formholders.RiskElementHolder;
import co.uk.depotnet.onsa.formholders.ShortTextHolder;
import co.uk.depotnet.onsa.formholders.SignatureHolder;
import co.uk.depotnet.onsa.formholders.StockItemHolder;
import co.uk.depotnet.onsa.formholders.StopWatchHolder;
import co.uk.depotnet.onsa.formholders.StoreHolder;
import co.uk.depotnet.onsa.formholders.SwitchHolder;
import co.uk.depotnet.onsa.formholders.TakePhotoHolder;
import co.uk.depotnet.onsa.formholders.VisitorSignHolder;
import co.uk.depotnet.onsa.formholders.YesNoHolder;
import co.uk.depotnet.onsa.formholders.YesNoNAHolder;
import co.uk.depotnet.onsa.fragments.FragmentStopWork;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.LocationListener;
import co.uk.depotnet.onsa.listeners.OnChangeChamberCount;
import co.uk.depotnet.onsa.listeners.PhotoAdapterListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.LogMeasureForkItem;
import co.uk.depotnet.onsa.modals.WorkItem;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.modals.forms.Screen;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.store.StockItems;
import co.uk.depotnet.onsa.views.DropdownMenu;
import co.uk.depotnet.onsa.views.DropdownNumberBottomSheet;
import co.uk.depotnet.onsa.views.DropdownTimer;

public class FormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements PhotoAdapterListener, AdapterLogMeasure.AdapterListener {

    private Context context;
    private Screen screen;
    private List<FormItem> formItems;
    private List<FormItem> originalItems;
    private FormAdapterListener listener;
    private OnChangeChamberCount changeChamberListener;
    private long submissionID;
    private int repeatCount;
    private Submission submission;
    private EditText focusedEditText;
    private boolean missingAnswerMode;
    private GradientDrawable redBG;
    private boolean isTimerRunning;


    public FormAdapter(Context context, Submission submission,
                       Screen screen, FormAdapterListener listener, OnChangeChamberCount changeChamberListener) {
        this.context = context;
        this.screen = screen;
        this.listener = listener;
        this.changeChamberListener = changeChamberListener;
        this.originalItems = screen.getItems();
        this.submission = submission;
        this.submissionID = submission.getID();
        this.formItems = new ArrayList<>();

        redBG = new GradientDrawable();
        redBG.setColor(Color.parseColor("#e24444"));

        reInflateItems(false);
    }


    private void addRepeatedVisitorSignature(ArrayList<FormItem> formItems) {
        this.formItems.clear();

        DBHandler dbHandler = DBHandler.getInstance();
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
        DBHandler dbHandler = DBHandler.getInstance();

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
        int forkPosition = 0;
        boolean ifItemAdded = false;
        boolean ifPosDFEAdded = false;
        boolean ifNegDFEAdded = false;
        DBHandler dbHandler = DBHandler.getInstance();
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
            }else if (!ifPosDFEAdded && item.getFormType() == FormItem.TYPE_ADD_POS_DFE) {
                forkPosition = formItems.size()-1;
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
            }else if (!ifNegDFEAdded && item.getFormType() == FormItem.TYPE_ADD_NEG_DFE) {
                forkPosition = formItems.size()-1;;
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
            }
        }
        notifyDataSetChanged();

        if (!listItems.isEmpty()) {
            repeatCount++;
        }
    }


    private ArrayList<LogMeasureForkItem> getLogRepeatedItems(FormItem item) {

        ArrayList<LogMeasureForkItem> items = new ArrayList<>();
        DBHandler dbHandler = DBHandler.getInstance();
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
                return new ForkCardHolder(LayoutInflater.from(context).inflate(R.layout.item_fork_card, viewGroup, false));
            case FormItem.TYPE_CALENDER:
                return new CalenderHolder(LayoutInflater.from(context).inflate(R.layout.item_calender, viewGroup, false));
            case FormItem.TYPE_DFE_ITEM:
                return new DFEItemHolder(LayoutInflater.from(context).inflate(R.layout.item_dfe, viewGroup, false));
            case FormItem.TYPE_YES_NO_NA:
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
                bindForkCard((ForkCardHolder) holder, position);
                break;
            case FormItem.TYPE_CALENDER:
                bindCalender((CalenderHolder) holder, position);
                break;
            case FormItem.TYPE_DFE_ITEM:
                bindDFEHolder((DFEItemHolder) holder, position);
                break;
            case FormItem.TYPE_YES_NO_NA:
                bindYesNoNAHolder((YesNoNAHolder) holder, position);
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
        }
    }

    private void bindBarCodeItem(DropDownHolder holder, int position) {
        holder.txtTitle.setText(formItems.get(position).getTitle());
        holder.view.setOnClickListener(v -> {
            Answer answer = DBHandler.getInstance().getAnswer(submissionID , "StaId" , "Items" , 0);
            if(answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                Intent intent = new Intent(context , ScannedBarcodeActivity.class);
                intent.putExtra("StaId" , answer.getAnswer());
                listener.startActivityForResultFromAdapter(intent, ScannedBarcodeActivity.REQUEST_CODE);
            }
        });

    }

    private void bindAddStoreItem(DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);

        holder.txtTitle.setText(formItem.getTitle());
        holder.view.setBackground(null);
        holder.txtValue.setVisibility(View.GONE);
        Answer answer = DBHandler.getInstance().getAnswer(submissionID,
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
            if(submission.getJsonFileName().equalsIgnoreCase("log_store.json")){
                intent = new Intent(context, ListStoreItemActivity.class);
            }else {
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

        final Answer answer = DBHandler.getInstance().getAnswer(submissionID, "StaStockItemId",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
            return;
        }

        String description = "";
        if(submission.getJsonFileName().equalsIgnoreCase("log_store.json")){
            MyStore item = DBHandler.getInstance().getMyStores(answer.getAnswer());
            if(item != null){
                description = item.getdescription();
            }
        }else{
            StockItems item = DBHandler.getInstance().getStockItems(answer.getAnswer());
            if(item != null){
                description = item.getdescription();
            }
        }


        holder.txtDescription.setText(description);



        final Answer Quantity = DBHandler.getInstance().getAnswer(submissionID, "Quantity",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (Quantity == null || TextUtils.isEmpty(Quantity.getAnswer())) {
            return;
        }


        Answer StaId = DBHandler.getInstance().getAnswer(submissionID,
                "StaId", formItem.getRepeatId(), formItem.getRepeatCount());
        if (StaId == null) {
            Answer staAnswer = DBHandler.getInstance().getAnswer(submissionID, "StaId",
                    formItem.getRepeatId(), 0);
            if (staAnswer != null) {
                StaId = new Answer(submissionID, "StaId");
                StaId.setRepeatID(formItem.getRepeatId());
                StaId.setRepeatCount(formItem.getRepeatCount());
                StaId.setAnswer(staAnswer.getAnswer());
                StaId.setDisplayAnswer(staAnswer.getDisplayAnswer());
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, StaId.toContentValues());
            }
        }

        holder.txtNumber.setText(Quantity.getAnswer());
        holder.btnDelete.setOnClickListener(v -> {
            DBHandler.getInstance().removeAnswer(answer);
            DBHandler.getInstance().removeAnswer(Quantity);
            final Answer StaId1 = DBHandler.getInstance().getAnswer(submissionID, "StaId",
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (StaId1 != null) {
                DBHandler.getInstance().removeAnswer(StaId1);
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

        Answer staStockItemId = DBHandler.getInstance().getAnswer(submissionID, "StaStockItemId",
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (staStockItemId == null) {

            staStockItemId = new Answer(submissionID, "StaStockItemId");

        }

        staStockItemId.setAnswer(store.getStaStockItemId());
        staStockItemId.setDisplayAnswer("");
        staStockItemId.setRepeatID(formItem.getRepeatId());
        staStockItemId.setRepeatCount(formItem.getRepeatCount());

        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, staStockItemId.toContentValues());

        Answer Quantity = DBHandler.getInstance().getAnswer(submissionID, "Quantity",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (Quantity == null) {
            Quantity = new Answer(submissionID, "Quantity");
        }

        Quantity.setAnswer(String.valueOf(formItem.getMyStoreQuantity()));
        Quantity.setDisplayAnswer("");
        Quantity.setRepeatID(formItem.getRepeatId());
        Quantity.setRepeatCount(formItem.getRepeatCount());

        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, Quantity.toContentValues());

        Answer StaId = DBHandler.getInstance().getAnswer(submissionID, "StaId",
                formItem.getRepeatId(), formItem.getRepeatCount());
        if (StaId == null) {

            StaId = new Answer(submissionID, "StaId");

        }

        StaId.setAnswer(store.getstaId());
        StaId.setDisplayAnswer("");
        StaId.setRepeatID(formItem.getRepeatId());
        StaId.setRepeatCount(formItem.getRepeatCount());

        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, StaId.toContentValues());


    }

    private void bindRiskElementHolder(RiskElementHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText("");

        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answer != null && !TextUtils.isEmpty(answer.getDisplayAnswer())) {
            holder.txtTitle.setText(answer.getDisplayAnswer());
        }

        holder.imgDelete.setOnClickListener(v -> {

            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (answer1 != null) {
                DBHandler.getInstance().removeAnswer(answer1);
            }

            Answer actionRequired = DBHandler.getInstance().getAnswer(submissionID, "actionRequired",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (actionRequired != null) {
                DBHandler.getInstance().removeAnswer(actionRequired);
            }

            Answer sPoleNo = DBHandler.getInstance().getAnswer(submissionID, "sPoleNo",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (sPoleNo != null) {
                DBHandler.getInstance().removeAnswer(sPoleNo);
            }

            Answer dPoleNo = DBHandler.getInstance().getAnswer(submissionID, "dPoleNo",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (dPoleNo != null) {
                DBHandler.getInstance().removeAnswer(dPoleNo);
            }

            Answer isPoleScheduleCompleted = DBHandler.getInstance().getAnswer(submissionID, "isPoleScheduleCompleted",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (isPoleScheduleCompleted != null) {
                DBHandler.getInstance().removeAnswer(isPoleScheduleCompleted);
            }

            Answer consideration = DBHandler.getInstance().getAnswer(submissionID, "consideration",
                    formItem.getRepeatId(), formItem.getRepeatCount());

            if (consideration != null) {
                DBHandler.getInstance().removeAnswer(consideration);
            }
            formItems.remove(position);
            notifyDataSetChanged();
        });

        holder.view.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));
    }

    private void bindDateTimeHolder(final DateTimeHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            };

            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 != null && !TextUtils.isEmpty(answer1.getAnswer())) {
                String value = answer1.getAnswer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

                try {
                    Date selectedDate = sdf.parse(value);
                    if(selectedDate != null) {
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
                Answer answer12 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            };

            Answer answer12 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 != null && !TextUtils.isEmpty(answer12.getAnswer())) {
                String value = answer12.getAnswer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

                try {
                    Date selectedDate = sdf.parse(value);
                    if(selectedDate != null) {
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

        Answer answerVisitorName = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer imgPath = DBHandler.getInstance().getAnswer(submissionID, fields.get(1),
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

            Answer answerVisitorName1 = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            Answer imgPath1 = DBHandler.getInstance().getAnswer(submissionID, fields.get(1),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (answerVisitorName1 != null) {
                DBHandler.getInstance().removeAnswer(answerVisitorName1);
            }

            if (imgPath1 != null) {
                DBHandler.getInstance().removeAnswer(imgPath1);
            }

            formItems.remove(position);
            notifyDataSetChanged();
        });

        holder.view.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

    }

    private void bindStopWatchHolder(final StopWatchHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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
                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);
                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }

                answer1.setRepeatCount(repeatCount);
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setAnswer(String.valueOf(holder.getUpdateTime()));
                answer1.setDisplayAnswer(holder.txtTime.getText().toString());

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            }

        });

    }

    private void bindDFEHolder(DFEItemHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answerItemId = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer quantity = DBHandler.getInstance().getAnswer(submissionID, fields.get(1),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answerItemId != null) {
            String value = answerItemId.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                holder.txtValue.setText(value);
                holder.txtTitle.setText(answerItemId.getDisplayAnswer());
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
            Answer answerItemId1 = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            Answer quantity1 = DBHandler.getInstance().getAnswer(submissionID, fields.get(1),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (answerItemId1 != null) {
                DBHandler.getInstance().removeAnswer(answerItemId1);
            }

            if (quantity1 != null) {
                DBHandler.getInstance().removeAnswer(quantity1);
            }

            notifyDataSetChanged();
        });

        holder.llBtnEdit.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

    }

    private void bindCalender(final CalenderHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            };

            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 != null && !TextUtils.isEmpty(answer1.getAnswer())) {
                String value = answer1.getAnswer();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss", Locale.ENGLISH);

                try {
                    Date selectedDate = sdf.parse(value);
                    if(selectedDate != null) {
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


    private void bindStoreItem(final StoreHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.txtValue.setText("");
        holder.etNumber.setText("");

        int assetNumber = formItem.getAssetNumber();


        Answer assetId = DBHandler.getInstance().getAnswer(submissionID, "itemId",
                formItem.getRepeatId(), assetNumber);
        Answer quantity = DBHandler.getInstance().getAnswer(submissionID,
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
            items.addAll(DBHandler.getInstance().getItemTypes(uploadId));
            if (items.isEmpty()) {
                return;
            }

            final DropdownMenu dropdownMenu = DropdownMenu.newInstance(items);
            dropdownMenu.setListener(position1 -> {
                holder.txtValue.setText(items.get(position1).getDisplayItem());
                int assetNumber1 = formItem.getAssetNumber();

                Answer assetType = DBHandler.getInstance().getAnswer(submissionID, "type",
                        formItem.getRepeatId(), assetNumber1);

                if (assetType == null) {
                    assetType = new Answer(submissionID, "type");
                }

                assetType.setAnswer(formItem.getAssetType());
                assetType.setDisplayAnswer(formItem.getAssetType());
                assetType.setRepeatID(formItem.getRepeatId());
                assetType.setRepeatCount(assetNumber1);


                Answer assetId1 = DBHandler.getInstance().getAnswer(submissionID, "itemId",
                        formItem.getRepeatId(), assetNumber1);

                if (assetId1 == null) {
                    assetId1 = new Answer(submissionID, "itemId");
                }

                assetId1.setAnswer(items.get(position1).getUploadValue());
                assetId1.setDisplayAnswer(items.get(position1).getDisplayItem());
                assetId1.setRepeatID(formItem.getRepeatId());
                assetId1.setRepeatCount(assetNumber1);


                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, assetType.toContentValues());
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, assetId1.toContentValues());
            });

            listener.showBottomSheet(dropdownMenu);
        });

        holder.etNumber.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {

                int assetNumber12 = formItem.getAssetNumber();
                Answer assetType = DBHandler.getInstance().getAnswer(submissionID, "type",
                        formItem.getRepeatId(), assetNumber12);

                if (assetType == null) {
                    assetType = new Answer(submissionID, "type");
                }

                assetType.setAnswer(formItem.getAssetType());
                assetType.setDisplayAnswer(formItem.getAssetType());
                assetType.setRepeatID(formItem.getRepeatId());
                assetType.setRepeatCount(assetNumber12);


                Answer quantity1 = DBHandler.getInstance().getAnswer(submissionID,
                        "quantity", formItem.getRepeatId(), assetNumber12);

                if (quantity1 == null) {
                    quantity1 = new Answer(submissionID, "quantity");
                }

                EditText et = (EditText) view;
                quantity1.setAnswer(et.getText().toString());
                quantity1.setDisplayAnswer(et.getText().toString());
                quantity1.setRepeatID(formItem.getRepeatId());
                quantity1.setRepeatCount(assetNumber12);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME,
                        quantity1.toContentValues());
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME,
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
        Answer latitude = DBHandler.getInstance().getAnswer(submissionID,
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
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        String addressLine = addresses.get(0).getAddressLine(0);
                        holder.txtLocation.setText(addressLine);

                        Answer address = DBHandler.getInstance().getAnswer(submissionID, "address",
                                formItem.getRepeatId(), repeatCount);
                        if (address == null) {
                            address = new Answer(submissionID, "address");
                        }

                        address.setAnswer(String.valueOf(addressLine));
                        address.setDisplayAnswer(addressLine);
                        address.setRepeatID(formItem.getRepeatId());
                        address.setRepeatCount(repeatCount);
                        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, address.toContentValues());

                        Answer latitude1 = DBHandler.getInstance().getAnswer(submissionID, "latitude",
                                formItem.getRepeatId(), repeatCount);
                        if (latitude1 == null) {
                            latitude1 = new Answer(submissionID, "latitude");
                        }

                        latitude1.setAnswer(String.valueOf(location.getLatitude()));
                        latitude1.setDisplayAnswer(addressLine);
                        latitude1.setRepeatID(formItem.getRepeatId());
                        latitude1.setRepeatCount(repeatCount);

                        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, latitude1.toContentValues());

                        Answer longitude = DBHandler.getInstance().getAnswer(submissionID, "longitude",
                                formItem.getRepeatId(), repeatCount);
                        if (longitude == null) {
                            longitude = new Answer(submissionID, "longitude");
                        }

                        longitude.setAnswer(String.valueOf(location.getLongitude()));
                        longitude.setDisplayAnswer(addressLine);
                        longitude.setRepeatID(formItem.getRepeatId());
                        longitude.setRepeatCount(repeatCount);

                        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, longitude.toContentValues());
                    }
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

                Answer address = DBHandler.getInstance().getAnswer(submissionID, "address",
                        formItem.getRepeatId(), repeatCount);
                if (address == null) {
                    address = new Answer(submissionID, "address");
                }

                address.setAnswer(String.valueOf(holder.txtLocation.getText()));
                address.setDisplayAnswer(holder.txtLocation.getText().toString());
                address.setRepeatID(formItem.getRepeatId());
                address.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, address.toContentValues());

                String lat = "0";
                String lon = "0";
                Answer latitude12 = DBHandler.getInstance().getAnswer(submissionID, "latitude",
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

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, latitude12.toContentValues());

                Answer longitude = DBHandler.getInstance().getAnswer(submissionID, "longitude",
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

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, longitude.toContentValues());

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
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer(sb.toString());
            answer1.setDisplayAnswer(sb.toString());
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

        });

        holder.view.setOnClickListener(view -> dropdownMenu.show());
    }

    private void bindNumberHolder(NumberHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        if (answer != null) {
            holder.editText.setText(answer.getDisplayAnswer());
        }

        holder.editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {

                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }

                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                focusedEditText = null;

                if (submission.getJsonFileName().equalsIgnoreCase("risk_assessment.json") &&
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

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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
            Answer answer13 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer13 == null) {
                answer13 = new Answer(submissionID, formItem.getUploadId());
            }
            answer13.setAnswer("1");
            answer13.setRepeatID(formItem.getRepeatId());
            answer13.setRepeatCount(repeatCount);
            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer13.toContentValues());
        });

        holder.btnNo.setOnClickListener(view -> {

            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);
            holder.btnNA.setSelected(false);
            holder.rlContainer.setVisibility(View.VISIBLE);
            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer("2");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

        });

        holder.btnNA.setOnClickListener(view -> {
            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(true);
            holder.rlContainer.setVisibility(View.GONE);
            Answer answer12 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);

            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("3");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer12.toContentValues());

        });
    }


    private void bindYesNoNAHolder(final YesNoNAHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtQuestion.setText(formItem.getTitle());

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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
            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }
            answer1.setAnswer("1");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);
            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.btnNo.setOnClickListener(view -> {

            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(true);
            holder.btnNA.setSelected(false);
            Answer answer12 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("2");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }

        });

        holder.btnNA.setOnClickListener(view -> {
            holder.btnYes.setSelected(false);
            holder.btnNo.setSelected(false);
            holder.btnNA.setSelected(true);
            Answer answer13 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);

            if (answer13 == null) {
                answer13 = new Answer(submissionID, formItem.getUploadId());
            }

            answer13.setAnswer("3");
            answer13.setRepeatID(formItem.getRepeatId());
            answer13.setRepeatCount(repeatCount);

            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer13.toContentValues());

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

        if(formItem.getTitle().equalsIgnoreCase("Take Photo/Video")){
            Answer answer = DBHandler.getInstance().getAnswer(submissionID , "photoTypes" , null , 0);
            if(answer != null){
                formItem.setPhotoId(answer.getAnswer());
                ArrayList<Photo> photos = formItem.getPhotos();
                if(photos != null && !photos.isEmpty()){
                    for (Photo photo :
                            photos) {
                        photo.setPhoto_id(formItem.getPhotoId());
                    }
                }
            }
        }

        if(holder.adapterPhoto == null){
            holder.adapterPhoto = new AdapterPhoto(context, submissionID, formItem, repeatCount, this);
            holder.recyclerView.setAdapter(holder.adapterPhoto);
        }

        holder.adapterPhoto.update();
        holder.adapterPhoto.notifyDataSetChanged();
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
            if(formItem.getPhotoSize() >= 100){
                holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload_unlimited), formItem.getPhotoRequired()));
            }else {
                holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload), formItem.getPhotoSize(), formItem.getPhotoRequired()));
            }
        }
    }


    private void bindTakePhotoHolder(TakePhotoHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<Photo> photos = formItem.getPhotos();
        int size = photos.size();
        int required = 0;

        ArrayList<Answer> answers = new ArrayList<>();

        int photosTaken = 0;
        for (int i = 0; i < size; i++) {
            Photo photo = photos.get(i);
            if (!photo.isOptional()) {
                required++;
            }
            Answer answer = DBHandler.getInstance().getAnswer(submissionID,
                    photo.getPhoto_id(),
                    repeatCount, photo.getTitle());

            if (answer != null) {
                photo.setIsVideo(answer.isPhoto() == 2);
                photo.setUrl(answer.getAnswer());
                answers.add(answer);
                photosTaken++;
            }
        }

        if(size >= 100) {
            holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload_unlimited), required));
        }else{
            holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload), size, required));
        }

        holder.imgBtnCamera.setOnClickListener(view -> {

            if (submission.getJsonFileName().equalsIgnoreCase("take_photo.json")) {
                Answer photoId = DBHandler.getInstance().getAnswer(submissionID,
                        "photoTypes",
                        formItem.getRepeatId(), 0);

                for (int i = 0; i < photos.size(); i++) {
                    Photo photo = photos.get(i);
                    Answer answer = DBHandler.getInstance().getAnswer(submissionID,
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
        int rC = 0;
        ArrayList<String> fields = formItem.getFields();
        ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionID, fields.get(0), "items");
        answers.addAll(DBHandler.getInstance().getRepeatedAnswers(submissionID, fields.get(0), "negItems"));

        if (answers != null) {
            for (int i = 0; i < answers.size() ; i++){
                if(rC <= answers.get(i).getRepeatCount()){
                    rC = answers.get(i).getRepeatCount();
                }
            }
        }

        final int repeatCount = rC+1;
        holder.view.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, repeatCount));

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
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

        holder.imgSignature.setImageDrawable(null);
        if (answer != null) {
            Glide.with(context).load(answer.getAnswer()).into(holder.imgSignature);
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.view.setOnClickListener(view -> listener.openSignature(formItem, submissionID, repeatCount));


        holder.btnClear.setOnClickListener(view -> {
            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
            if (answer1 != null) {
                DBHandler.getInstance().removeAnswer(answer1);
            }
            holder.imgSignature.setImageDrawable(null);
        });
    }


    private void bindTimeDropDownHolder(final DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.txtValue.setText("");
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && !value.isEmpty()) {
                holder.txtValue.setText(answer.getDisplayAnswer());
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        final DropdownTimer dropdownMenu = new DropdownTimer(context, new DropdownTimer.OnTimeSelected() {
            @Override
            public void timeSelected(String hours, String minutes) {
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

                Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);
                if (answer == null) {
                    answer = new Answer(submissionID, formItem.getUploadId());
                }

                answer.setAnswer(sb.toString());
                answer.setDisplayAnswer(sb.toString());
                answer.setRepeatID(formItem.getRepeatId());
                answer.setRepeatCount(repeatCount);

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());

            }
        });


        holder.view.setOnClickListener(view -> dropdownMenu.show());
    }

    private void bindDropDownHolder(final DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.view.setBackground(null);
        holder.txtValue.setText("");
        Answer answer = DBHandler.getInstance().getAnswer(submissionID,
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

            final ArrayList<DropDownItem> items = new ArrayList<>();

            String uploadId = formItem.getUploadId();

            if (uploadId.equalsIgnoreCase("noticeId")) {
                items.addAll(DBHandler.getInstance().getNotices(submission.getJobID()));
            }

            if(formItem.getKey().equalsIgnoreCase(
                    Job.DBTable.NAME)) {
                items.addAll(DBHandler.getInstance().getJobs());
            }else if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)) {
                items.addAll(DBHandler.getInstance().getWorkItem(DatasetResponse.DBTable.dfeWorkItems,
                        WorkItem.DBTable.itemCode));
            } else if (formItem.getKey().equalsIgnoreCase(JobWorkItem.DBTable.NAME)) {
                items.addAll(DBHandler.getInstance().getJobWorkItem(submission.getJobID()));
            } else {
                items.addAll(DBHandler.getInstance().getItemTypes(formItem.getKey()));
            }

            if (items.isEmpty()) {
                return;
            }

            if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)
                    || formItem.getKey().equalsIgnoreCase(JobWorkItem.DBTable.NAME)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.bookOperatives)

            ) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("submissionID", submissionID);
                intent.putExtra("repeatId", formItem.getRepeatId());
                intent.putExtra("uploadId", uploadId);
                intent.putExtra("keyItemType", formItem.getKey());
                intent.putExtra("isMultiSelection", formItem.isMultiSelection());
                intent.putExtra("isConcatDisplayText", formItem.isConcatDisplayText());
                intent.putExtra("repeatCount", repeatCount);
                context.startActivity(intent);
                return;
            } else if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.poleTypes)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.surfaceTypes)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.materialTypes)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.blockTerminalTypes)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.anchorTypes)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.jointClosureTypes)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.aerialCables)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.ugCableTypes)
                    || formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dacTypes)) {
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
            dropdownMenu.setListener(new DropDownAdapter.OnItemSelectedListener() {
                @Override
                public void onItemSelected(int position1) {


                    holder.txtValue.setText(items.get(position1).getDisplayItem());
                    Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

                    if(needToBeNotified(formItem)){
                        reInflateItems(true);
                    }
                }
            });

            listener.showBottomSheet(dropdownMenu);
        });
    }

    private void bindDialogScreen(final DropDownHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.txtValue.setText("");
        Answer answer = DBHandler.getInstance().getAnswer(submissionID,
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

        Answer answer = DBHandler.getInstance().getAnswer(submissionID,
                formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);


        if (answer != null) {
            String value = answer.getAnswer();
            if (value == null) {
                holder.checkBox.setChecked(formItem.isChecked());
            } else if (value.equals("true")) {
                holder.checkBox.setChecked(true);
            } else {
                holder.checkBox.setChecked(false);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.checkBox.setOnCheckedChangeListener((compoundButton, isChecked) -> {
            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer(isChecked ? "true" : "false");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);


            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
        });
    }

    private void bindSwitchHolder(SwitchHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.btnSwitch.setChecked(formItem.isChecked());

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            String value = answer.getAnswer();
            holder.btnSwitch.setChecked(!TextUtils.isEmpty(value) &&
                    value.equals("true"));

        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnSwitch.setOnClickListener(v -> {
            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID,
                    formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }

            answer1.setAnswer(((SwitchCompat) v).isChecked() ? "true" : "false");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);

            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
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
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        holder.btnYes.setSelected(false);
        holder.btnNo.setSelected(false);

        if (answer != null) {
            String value = answer.getAnswer();
            if (value != null && value.equals("true")) {
                holder.btnYes.setSelected(true);
                holder.btnNo.setSelected(false);
            } else if (value != null && value.equals("false")) {
                holder.btnYes.setSelected(false);
                holder.btnNo.setSelected(true);
            }
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.btnYes.setOnClickListener(view -> {

            holder.btnYes.setSelected(true);
            holder.btnNo.setSelected(false);


            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }
            answer1.setAnswer("true");
            answer1.setRepeatID(formItem.getRepeatId());
            answer1.setRepeatCount(repeatCount);
            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
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


            Answer answer12 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("false");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer12.toContentValues());
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
                || (formItem.getFalseEnables() != null && !formItem.getFalseEnables().isEmpty());
    }

    public void reInflateItems(boolean isNotified) {

        this.formItems.clear();
        for (int i = 0; i < originalItems.size(); i++) {
            FormItem item = originalItems.get(i);
            this.formItems.add(new FormItem(item));
            if (!TextUtils.isEmpty(item.getUploadId())) {
                Answer answer = DBHandler.getInstance().getAnswer(submissionID,
                        item.getUploadId(), item.getRepeatId(), repeatCount);
                if (answer != null) {
                    String value = answer.getAnswer();
                    if (!TextUtils.isEmpty(value)) {
                       if (item.getFormType() == FormItem.TYPE_YES_NO_NA) {
                            int v = 0;
                            try {
                                v = Integer.parseInt(value);
                            } catch (Exception e) {
                            }
                            addEnableItems(item, v);
                        }else if(item.getFormType() == FormItem.TYPE_DROPDOWN){
                           addEnableItems(item, true);
                        } else {
                            addEnableItems(item, value.equalsIgnoreCase("true"));
                        }
                    }
                }
            }
        }

        if (isNotified) {
            notifyDataSetChanged();
        }
        addListItems(new ArrayList<>(formItems));
        addRepeatedVisitorSignature(new ArrayList<>(formItems));
        addStockItems(new ArrayList<>(formItems));

    }


    private void addEnableItems(FormItem formItem, int value) {
        if (value == 0) {
            return;
        }

        ArrayList<FormItem> toBeAdded = null;
        ArrayList<FormItem> toBeRemoved = new ArrayList<>();
        if (value == 1) {
            toBeAdded = formItem.getEnables();
            if (formItem.getFalseEnables() != null) {
                toBeRemoved.addAll(formItem.getFalseEnables());
            }
            if (formItem.getNaEnables() != null) {
                toBeRemoved.addAll(formItem.getNaEnables());
            }
        } else if (value == 2) {
            toBeAdded = formItem.getFalseEnables();
            if (formItem.getEnables() != null) {
                toBeRemoved.addAll(formItem.getEnables());
            }
            if (formItem.getNaEnables() != null) {
                toBeRemoved.addAll(formItem.getNaEnables());
            }
        } else {
            toBeAdded = formItem.getNaEnables();
            if (formItem.getEnables() != null) {
                toBeRemoved.addAll(formItem.getEnables());
            }
            if (formItem.getFalseEnables() != null) {
                toBeRemoved.addAll(formItem.getFalseEnables());
            }
        }

        if (!toBeRemoved.isEmpty()) {

            for (FormItem item : toBeRemoved) {
                String field = item.getUploadId();
                for (int i = 0; i < formItems.size(); i++) {
                    FormItem fi = formItems.get(i);
                    String uploadId = fi.getUploadId();
                    if (uploadId != null) {
                        if (fi.getUploadId().equalsIgnoreCase(field)) {
                            formItems.remove(i);
                            break;
                        }
                    }
                }
            }
        }

        if (toBeAdded != null && !toBeAdded.isEmpty()) {
            formItems.addAll(toBeAdded);
        }
    }


    private void addEnableItems(FormItem formItem, boolean isEnableItems) {

        ArrayList<FormItem> toBeAdded = isEnableItems ? formItem.getEnables() : formItem.getFalseEnables();
        ArrayList<FormItem> toBeRemoved = !isEnableItems ? formItem.getEnables() : formItem.getFalseEnables();


        if (toBeRemoved != null && !toBeRemoved.isEmpty()) {

            for (FormItem item : toBeRemoved) {
                String field = item.getUploadId();
                for (int i = 0; i < formItems.size(); i++) {
                    FormItem fi = formItems.get(i);
                    String uploadId = fi.getUploadId();
                    if (uploadId != null) {
                        if (fi.getUploadId().equalsIgnoreCase(field)) {
                            formItems.remove(i);
                            break;
                        }
                    }
                }
            }
        }

        if (toBeAdded != null && !toBeAdded.isEmpty()) {
            formItems.addAll(toBeAdded);
        }
    }


    private void bindLongTextHolder(LongTextHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        holder.editText.setHint(formItem.getHint());
        holder.editText.setText("");

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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
                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID,
                        formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }


                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
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

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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
                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }
                if (answer1.getUploadID() != null &&
                        answer1.getUploadID().equalsIgnoreCase("visitorName")) {
                    answer1.setIsMultiList(1);
                }


                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());
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

//        if (getPoleCount() != 0) {
//            Intent intent = new Intent(context, PollingSurveyActivity.class);
//            intent.putExtra(PollingSurveyActivity.ARG_SUBMISSION_ID, submissionID);
//            ((Activity) context).startActivityForResult(intent, 1234);
//            return true;
//        }

        missingAnswerMode = false;
        int missingCount = 0;
        boolean isPhotoMissing = false;
        boolean ifDFEAdded = false;
        for (int c = 0; c < formItems.size(); c++) {
            FormItem item = formItems.get(c);
            if (item.getFormType() == FormItem.TYPE_PHOTO) {
                final ArrayList<Photo> photos = item.getPhotos();
                int photosNeeded = photos.size();
                for (int i = 0; i < photosNeeded; i++) {
                    Photo photo = photos.get(i);
                    Answer answer = DBHandler.getInstance().getAnswer(submissionID, String.valueOf(photo.getPhoto_id()), repeatCount, photo.getTitle());
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
                        ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                        if (answers == null || answers.isEmpty()) {
                            missingCount++;
                        }
                    }


                }else if ((item.getFormType() == FormItem.TYPE_ADD_POS_DFE) ||
                        (item.getFormType() == FormItem.TYPE_ADD_NEG_DFE)) {
                    ArrayList<String> fields = item.getFields();
                    if (fields != null && !fields.isEmpty()) {
                        ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                        if (answers!=null && !answers.isEmpty()) {
                            ifDFEAdded = true;
                        }
                    }
                }else if (item.getFormType() == FormItem.TYPE_LOCATION) {
                    Answer answer = DBHandler.getInstance().getAnswer(submissionID, "latitude", item.getRepeatId(), repeatCount);
                    if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
                        missingCount++;
                    }
                } else if (item.getFormType() == FormItem.TYPE_LOG_AND_DIG_FORK) {
                    if (getLogRepeatedItems(item).isEmpty()) {
                        missingCount++;
                    }
                } else {
                    if (!item.isOptional() && item.getUploadId() != null) {
                        Answer answer = DBHandler.getInstance().getAnswer(submissionID, item.getUploadId(), item.getRepeatId(), repeatCount);
                        if (answer == null) {
                            missingCount++;
                        }
                    }
                }
            }
        }
        if(submission.getJsonFileName().equalsIgnoreCase("log_dfe.json") && !ifDFEAdded){
            missingCount++;
            listener.showValidationDialog("Validation Error" , "Please add Positive or negative DFEs");
        }else if(isPhotoMissing){
            listener.showValidationDialog("Validation Error" , "Photos are missing");
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
                equalsIgnoreCase("take_photo.json")) {

            Answer photoId = DBHandler.getInstance().getAnswer(submissionID,
                    "photoTypes",
                    null, 0);

            if (photoId == null) {

                return;
            }
            ArrayList<Photo> photos = formItem.getPhotos();
            for (int i = 0; i < photos.size(); i++) {
                Photo photo = photos.get(i);
                Answer answer = DBHandler.getInstance().getAnswer(submissionID,
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
                Answer answer = DBHandler.getInstance().getAnswer(submissionID,
                        fi.getUploadId(), fi.getRepeatId(), fi.getRepeatCount());
                if (answer != null) {
                    DBHandler.getInstance().removeAnswer(answer);
                }
            }

        }
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }
}
