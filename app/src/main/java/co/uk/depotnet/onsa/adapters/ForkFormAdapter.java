package co.uk.depotnet.onsa.adapters;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.util.Pair;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.DatePicker;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.builders.DatePickerBuilder;
import com.applandeo.materialcalendarview.listeners.OnSelectDateListener;
import com.bumptech.glide.Glide;
import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.MaterialDatePicker;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ListActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.dialogs.HourPickerDialog;
import co.uk.depotnet.onsa.dialogs.SimpleCalendarDialogFragment;
import co.uk.depotnet.onsa.formholders.BoldTextHolder;
import co.uk.depotnet.onsa.formholders.BriefingSignHolder;
import co.uk.depotnet.onsa.formholders.Briefingtextholder;
import co.uk.depotnet.onsa.formholders.CalenderHolder;
import co.uk.depotnet.onsa.formholders.DFEItemHolder;
import co.uk.depotnet.onsa.formholders.DateTimeHolder;
import co.uk.depotnet.onsa.formholders.DescTextHolder;
import co.uk.depotnet.onsa.formholders.DropDownHolder;
import co.uk.depotnet.onsa.formholders.ForkCardHolder;
import co.uk.depotnet.onsa.formholders.LongTextHolder;
import co.uk.depotnet.onsa.formholders.NumberHolder;
import co.uk.depotnet.onsa.formholders.PhotoHolder;
import co.uk.depotnet.onsa.formholders.ShortTextHolder;
import co.uk.depotnet.onsa.formholders.SignatureHolder;
import co.uk.depotnet.onsa.formholders.SwitchHolder;
import co.uk.depotnet.onsa.formholders.TimePickerHolder;
import co.uk.depotnet.onsa.formholders.TimeSheetHourHolder;
import co.uk.depotnet.onsa.formholders.YesNoHolder;
import co.uk.depotnet.onsa.fragments.FragmentStopWork;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.PhotoAdapterListener;
import co.uk.depotnet.onsa.modals.Job;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.MeasureItems;
import co.uk.depotnet.onsa.modals.MenSplit;
import co.uk.depotnet.onsa.modals.RiskElementType;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.modals.timesheet.TimeTypeActivity;
import co.uk.depotnet.onsa.modals.timesheet.TimesheetOperative;
import co.uk.depotnet.onsa.networking.CommonUtils;
import co.uk.depotnet.onsa.utils.JsonReader;
import co.uk.depotnet.onsa.utils.Utils;
import co.uk.depotnet.onsa.views.DropdownMenu;

public class ForkFormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements PhotoAdapterListener {


    private final Context context;
    private final FormItem parentFormItem;
    private List<FormItem> formItems;

    private final long submissionID;
    private int repeatCount;
    private final Submission submission;
    private EditText focusedEditText;
    private boolean missingAnswerMode;
    private final FormAdapterListener listener;
    private final GradientDrawable redBG;
    private final String themeColor;
    private final ArrayList<FormItem> originalItems;
    private final ArrayList<String> recipients;
    private final DBHandler dbHandler;

    public ForkFormAdapter(Context context, Submission submission,
                           FormItem parentFormItem, int repeatCount,
                           FormAdapterListener listener, String themeColor, ArrayList<String> recipients) {
        this.context = context;
        this.parentFormItem = parentFormItem;
        this.submission = submission;
        this.submissionID = submission.getID();
        this.repeatCount = repeatCount;
        this.themeColor = themeColor;
        this.listener = listener;
        this.formItems = new ArrayList<>();
        this.originalItems = parentFormItem.getDialogItems();
        this.formItems = new ArrayList<>(originalItems);
        this.recipients = recipients;
        redBG = new GradientDrawable();
        redBG.setColor(Color.parseColor("#e24444"));
        this.dbHandler = DBHandler.getInstance();
        reInflateItems(false);
    }


    private void addListItems(ArrayList<FormItem> inflatedItems) {
        formItems.clear();
        int forkPosition;
        boolean ifPosDFEAdded = false;
        boolean ifNegDFEAdded = false;

        ArrayList<FormItem> listItems = new ArrayList<>();
        for (int c = 0; c < inflatedItems.size(); c++) {
            FormItem item = inflatedItems.get(c);
            formItems.add(item);

            if (item.getFormType() == FormItem.TYPE_FORK_CARD) {
                forkPosition = c;
                ArrayList<String> fields = item.getFields();
                if (fields != null && !fields.isEmpty()) {
                    ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, fields.get(0), item.getRepeatId());
                    if (answers != null) {
                        for (int i = 0; i < answers.size(); i++) {
                            repeatCount = answers.get(i).getRepeatCount();
                            FormItem qItem = new FormItem(item.getListItemType(), "", "", "", true);
                            qItem.setFields(item.getFields());
                            qItem.setDialogItems(item.getDialogItems());
                            qItem.setRepeatCount(repeatCount);
                            listItems.add(qItem);

                        }
                    }
                }
                formItems.addAll(forkPosition, listItems);

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
            }
        }


        if (!listItems.isEmpty()) {
            repeatCount++;
        }

        addBriefingsDocs(new ArrayList<>(formItems));

    }

    private void addBriefingsDocs(ArrayList<FormItem> inflatedItems) {

        int forkPosition;
        ArrayList<FormItem> listItems = new ArrayList<>();
        for (int c = 0; c < inflatedItems.size(); c++) {
            FormItem item = inflatedItems.get(c);
            if (item.getFormType() == FormItem.TYPE_LIST_BREIFDOC) {
                forkPosition = c;
                ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID, item.getUploadId(), item.getRepeatId());
                if (answers != null) {
                    for (int i = 0; i < answers.size(); i++) {
                        int repeatCount = answers.get(i).getRepeatCount();
                        FormItem qItem = new FormItem(item.getListItemType(), "", item.getUploadId(), item.getRepeatId(), true);
                        qItem.setRepeatCount(repeatCount);
                        listItems.add(qItem);
                    }
                }
                formItems.addAll(forkPosition + 1, listItems);
                break;
            }
        }

    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int type) {
        switch (type) {
            case FormItem.TYPE_YES_NO:
                return new YesNoHolder(LayoutInflater.from(context).inflate(R.layout.item_yes_no, viewGroup, false));
            case FormItem.TYPE_ET_LONG_TEXT:
                return new LongTextHolder(LayoutInflater.from(context).inflate(R.layout.item_et_long_text, viewGroup, false));
            case FormItem.TYPE_TXT_BOLD_HEAD:
            case FormItem.TYPE_LIST_BREIFDOC:
                return new BoldTextHolder(LayoutInflater.from(context).inflate(R.layout.item_txt_bold_head, viewGroup, false));
            case FormItem.TYPE_TXT_DESC:
                return new DescTextHolder(LayoutInflater.from(context).inflate(R.layout.item_txt_bold_head, viewGroup, false));
            case FormItem.TYPE_ET_SHORT_TEXT:
                return new ShortTextHolder(LayoutInflater.from(context).inflate(R.layout.item_et_short_text, viewGroup, false));
            case FormItem.TYPE_DROPDOWN:
                return new DropDownHolder(LayoutInflater.from(context).inflate(R.layout.item_dropdown, viewGroup, false));
            case FormItem.TYPE_NUMBER:
                return new NumberHolder(LayoutInflater.from(context).inflate(R.layout.item_et_number, viewGroup, false));
            case FormItem.TYPE_SIGNATURE:
                return new SignatureHolder(LayoutInflater.from(context).inflate(R.layout.item_signature, viewGroup, false));
            case FormItem.TYPE_FORK_CARD:
            case FormItem.TYPE_ADD_NEG_DFE:
            case FormItem.TYPE_ADD_POS_DFE:
                return new ForkCardHolder(LayoutInflater.from(context).inflate(R.layout.item_fork_card, viewGroup, false));
            case FormItem.TYPE_PHOTO:
                return new PhotoHolder(LayoutInflater.from(context).inflate(R.layout.item_form_photo, viewGroup, false));
            case FormItem.TYPE_DFE_ITEM:
                return new DFEItemHolder(LayoutInflater.from(context).inflate(R.layout.item_dfe, viewGroup, false));
            case FormItem.TYPE_SIGN_BRIEFING:
                return new BriefingSignHolder(LayoutInflater.from(context).inflate(R.layout.item_briefing_signs, viewGroup, false));
            case FormItem.TYPE_TV_BRIEFING_TEXT:
                return new Briefingtextholder(LayoutInflater.from(context).inflate(R.layout.briefings_read_item, viewGroup, false));
            case FormItem.TYPE_DATE_TIME:
                return new DateTimeHolder(LayoutInflater.from(context).inflate(R.layout.item_date_time, viewGroup, false));
            case FormItem.TYPE_ITEM_TIME_PICKER:
                return new TimePickerHolder(LayoutInflater.from(context).inflate(R.layout.item_time_picker, viewGroup, false));
            case FormItem.TYPE_CALENDER:
                return new CalenderHolder(LayoutInflater.from(context).inflate(R.layout.item_calender, viewGroup, false));
            case FormItem.TYPE_ET_TIME_SHEET_HOURS:
                return new TimeSheetHourHolder(LayoutInflater.from(context).inflate(R.layout.item_et_timesheet_hours, viewGroup, false));
            case FormItem.TYPE_SWITCH:
                return new SwitchHolder(LayoutInflater.from(context).inflate(R.layout.item_switch_layout, viewGroup, false));
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
            case FormItem.TYPE_TXT_BOLD_HEAD:
            case FormItem.TYPE_LIST_BREIFDOC:
                bindBoldTextHolder((BoldTextHolder) holder, position);
                break;
            case FormItem.TYPE_TXT_DESC:
                bindDescTextHolder((DescTextHolder) holder, position);
                break;
            case FormItem.TYPE_DROPDOWN:
                bindDropDownHolder((DropDownHolder) holder, position);
                break;
            case FormItem.TYPE_ET_SHORT_TEXT:
                bindShortTextHolder((ShortTextHolder) holder, position);
                break;
            case FormItem.TYPE_NUMBER:
                bindNumberHolder((NumberHolder) holder, position);
                break;
            case FormItem.TYPE_SIGNATURE:
                bindSignatureHolder((SignatureHolder) holder, position);
                break;

            case FormItem.TYPE_FORK_CARD:
            case FormItem.TYPE_ADD_NEG_DFE:
            case FormItem.TYPE_ADD_POS_DFE:
                bindForkCard((ForkCardHolder) holder, position);
                break;
            case FormItem.TYPE_PHOTO:
                bindPhotoHolder((PhotoHolder) holder, position);
                break;
            case FormItem.TYPE_DFE_ITEM:
                bindDFEHolder((DFEItemHolder) holder, position);
                break;
            case FormItem.TYPE_SIGN_BRIEFING:
                bindBRIEFINGSignHolder((BriefingSignHolder) holder, position);
                break;
            case FormItem.TYPE_TV_BRIEFING_TEXT:
                bindBRIEFINGTEXTHolder((Briefingtextholder) holder, position);
                break;
            case FormItem.TYPE_DATE_TIME:
                bindDateTimeHolder((DateTimeHolder) holder, position);
                break;
            case FormItem.TYPE_ITEM_TIME_PICKER:
                bindTimePickerHolder((TimePickerHolder) holder, position);
                break;
            case FormItem.TYPE_CALENDER:
                bindCalender((CalenderHolder) holder, position);
                break;
            case FormItem.TYPE_ET_TIME_SHEET_HOURS:
                bindTimeSheetHoursHolder((TimeSheetHourHolder) holder, position);
                break;
            case FormItem.TYPE_SWITCH:
                bindSwitchHolder((SwitchHolder) holder, position);
                break;
        }
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

            if (submission.getJsonFileName().equalsIgnoreCase("good_2_go.json")) {
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

    private void bindCalender(final CalenderHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer == null) {
            if (submission.getJsonFileName().equalsIgnoreCase("timesheet_log_hours.json")) {
                Answer selectedDate = dbHandler.getAnswer(submissionID, "selected_date", null, 0);
                if (selectedDate != null) {
                    answer = new Answer(submissionID, formItem.getUploadId(),
                            formItem.getRepeatId(), repeatCount);
                    answer.setAnswer(selectedDate.getAnswer());
                    answer.setDisplayAnswer(selectedDate.getDisplayAnswer());
                    dbHandler.replaceData(Answer.DBTable.NAME, answer.toContentValues());
                }
            }
        }
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
            long timeInMil = myCalendar.getTimeInMillis();

            DatePickerDialog.OnDateSetListener date = (view, year, monthOfYear, dayOfMonth) -> {

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                String date1 = Utils.formatDate(myCalendar.getTime(), "yyyy-MM-dd'T'HH:mm:ss");
                String displayDate = Utils.formatDate(myCalendar.getTime(), "dd/MM/yyyy");
                holder.txtDate.setText(displayDate);

                Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount, date1, displayDate);
                }

                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
            };

            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 != null && !TextUtils.isEmpty(answer1.getAnswer())) {
                String value = answer1.getAnswer();
                Date selectedDate = Utils.parseDate(value, "yyyy-MM-dd'T'HH:mm:ss");
                if (selectedDate != null) {
                    myCalendar.setTime(selectedDate);
                }
            }

            DatePickerDialog datePickerDialog = new DatePickerDialog(context, date, myCalendar
                    .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                    myCalendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.getDatePicker().setMaxDate(timeInMil);
            datePickerDialog.show();

        });
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
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.ENGLISH);
            if (answer != null && !TextUtils.isEmpty(answer.getAnswer())) {
                try {
                    Date date = sdf.parse(answer.getAnswer());
                    myCalendar.setTime(date);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            TimePickerDialog.OnTimeSetListener date = (view, hourOfDay, minute) -> {
                Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);

                myCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);

                String time = sdf.format(myCalendar.getTime());
                holder.txtTime.setText(time);


                if (answer12 == null) {
                    answer12 = new Answer(submissionID, formItem.getUploadId());
                }

                answer12.setAnswer(time);
                answer12.setDisplayAnswer(time);
                answer12.setRepeatID(formItem.getRepeatId());
                answer12.setRepeatCount(repeatCount);

                if (submission.getJsonFileName().equalsIgnoreCase("timesheet_submit_timesheet.json")) {
//                    TimeSheetHour timeSheetHour = parentFormItem.getTimeSheetHour();
//                    if(timeSheetHour != null){
//                        if(formItem.getUploadId().equalsIgnoreCase("timeFrom")){
////                            timeSheetHour.setTimeFrom(time);
//                        }else if(formItem.getUploadId().equalsIgnoreCase("timeTo")){
////                            timeSheetHour.setTimeTo(time);
//                        }
//
//                        timeSheetHour.setEdited(true);
//                        dbHandler.replaceData(TimeSheetHour.DBTable.NAME , timeSheetHour.toContentValues());
//                    }
                }

                dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());
            };


            new TimePickerDialog(context, date, myCalendar
                    .get(Calendar.HOUR_OF_DAY), myCalendar.get(Calendar.MINUTE),
                    true).show();

        });
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

    private void bindBRIEFINGSignHolder(BriefingSignHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answerItemId = dbHandler.getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer docname = dbHandler.getAnswer(submissionID, fields.get(1),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer quantity = dbHandler.getAnswer(submissionID, fields.get(2),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answerItemId != null) {
            String value = answerItemId.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                //holder.txtValue.setText(value);
                holder.txtTitle.setText(answerItemId.getDisplayAnswer());
            }
        }
        if (docname != null) {
            String value = docname.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                //Toast.makeText(context, ""+value, Toast.LENGTH_SHORT).show();
            }
        }
        if (quantity != null) {
            String value = quantity.getAnswer();
           /* if (!TextUtils.isEmpty(value)) {
                holder.txtQuantity.setText(value);
            }*/
        }

        holder.imgButton.setOnClickListener(v -> {
            formItems.remove(position);
            Answer answerItemId1 = dbHandler.getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());

            Answer quantity1 = dbHandler.getAnswer(submissionID, fields.get(2),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (answerItemId1 != null) {
                dbHandler.removeAnswer(answerItemId1);
            }
            // do not remove doc name of position 1 - for multiple item use to display...
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
                formItem.getRepeatId(), formItem.getRepeatCount());// repeat count for multiple doc display...
        if (answer != null) {
            String value = answer.getDisplayAnswer();
            if (value != null && !value.isEmpty()) {
                holder.textView.setText(value);
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
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }


                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);
                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });

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

            if (formItem.getStopWork() == 1) {
                FragmentStopWork fragmentStopWork = FragmentStopWork.newInstance(submission);
                listener.openFragment(fragmentStopWork);
                return;
            }

            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer1 == null) {
                answer1 = new Answer(submissionID, formItem.getUploadId());
            }
            answer1.setAnswer("true");
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
            if (formItem.getStopWork() == 2) {
                FragmentStopWork fragmentStopWork = FragmentStopWork.newInstance(submission);
                listener.openFragment(fragmentStopWork);
                return;
            }

            Answer answer12 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);
            if (answer12 == null) {
                answer12 = new Answer(submissionID, formItem.getUploadId());
            }

            answer12.setAnswer("false");
            answer12.setRepeatID(formItem.getRepeatId());
            answer12.setRepeatCount(repeatCount);

            dbHandler.replaceData(Answer.DBTable.NAME, answer12.toContentValues());

            if (needToBeNotified(formItem)) {
                reInflateItems(true);
            }
        });

    }

    private boolean needToBeNotified(FormItem formItem) {
        return (formItem.getEnables() != null && !formItem.getEnables().isEmpty())
                || (formItem.getFalseEnables() != null && !formItem.getFalseEnables().isEmpty())
                || (formItem.getNaEnables() != null && !formItem.getNaEnables().isEmpty());
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

    }

    private void addEnableItems(FormItem item) {

        String value = getAnswerValue(item, repeatCount);
        if (TextUtils.isEmpty(value)) {
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

    private void addEnableItems(FormItem formItem, String value) {
        ArrayList<FormItem> toBeAdded = new ArrayList<>();
        ArrayList<FormItem> enableItems = formItem.getEnables();
        ArrayList<FormItem> falseEnableItems = formItem.getFalseEnables();
        ArrayList<FormItem> naEnableItems = formItem.getNaEnables();


        if (formItem.getFormType() == FormItem.TYPE_DROPDOWN) {
            String uploadId = formItem.getUploadId();
            if (!TextUtils.isEmpty(submission.getJsonFileName()) && (submission.getJsonFileName().equalsIgnoreCase("timesheet_log_hours.json") || submission.getJsonFileName().equalsIgnoreCase("timesheet_edit_log_hours.json"))
                    && !TextUtils.isEmpty(uploadId) && uploadId.equalsIgnoreCase("timeTypeActivityId")) {
                TimeTypeActivity timeTypeActivity = dbHandler.getTimeTypeActivity(value);
                if (timeTypeActivity != null) {
                    String text = timeTypeActivity.getDisplayItem();
                    if (text != null) {
                        if (text.equalsIgnoreCase("Working")) {
                            toBeAdded.addAll(enableItems);
                        } else if (text.equalsIgnoreCase("Briefing") || text.equalsIgnoreCase("Travel")) {
                            toBeAdded.addAll(falseEnableItems);
                        } else /*if (text.equalsIgnoreCase("Lunch") || text.equalsIgnoreCase("Admin")
                                || text.equalsIgnoreCase("Vehicle check")|| text.equalsIgnoreCase("Annual Leave"))*/ {
                            toBeAdded.addAll(naEnableItems);
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

        } else if (value.equalsIgnoreCase("2") || value.equalsIgnoreCase("false")) {
            if (falseEnableItems != null && !falseEnableItems.isEmpty()) {
                toBeAdded.addAll(falseEnableItems);
            }

        } else if (value.equalsIgnoreCase("3")) {
            if (naEnableItems != null && !naEnableItems.isEmpty()) {
                toBeAdded.addAll(naEnableItems);
            }
        }

        this.formItems.add(formItem);
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
//                holder.txtValue.setText(value);
                holder.txtValue.setText(answerItemId.getDisplayAnswer());
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

    private void bindShortTextHolder(ShortTextHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
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
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }
                if (answer1.getUploadID() != null &&
                        answer1.getUploadID().equalsIgnoreCase("Visitors")) {
                    answer1.setIsMultiList(1);
                }

                EditText et = (EditText) view;
                answer1.setAnswer(et.getText().toString());
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);
                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });

    }

    private void bindNumberHolder(NumberHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            holder.editText.setText(answer.getDisplayAnswer());
        }

        holder.editText.setOnFocusChangeListener((view, hasFocus) -> {
            if (!hasFocus) {
                Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId());
                }

                EditText et = (EditText) view;
                String text = et.getText().toString();
                if (!TextUtils.isEmpty(formItem.getRepeatId()) &&
                        (formItem.getRepeatId().equalsIgnoreCase("negItems") || formItem.getRepeatId().equalsIgnoreCase("negDfeItems"))) {
                    text = "-" + text;
                }
                answer1.setAnswer(text);
                answer1.setRepeatID(formItem.getRepeatId());
                answer1.setRepeatCount(repeatCount);
                dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                focusedEditText = null;
            } else {
                focusedEditText = (EditText) view;
            }
        });
    }


    private void bindDropDownHolder(final DropDownHolder holder, int position) {
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

        holder.view.setOnClickListener(view -> {
            Answer timeSheetHourIds = dbHandler.getAnswer(submissionID, "timeSheetHoursId", "timesheetHours", repeatCount);
            if (timeSheetHourIds != null) {
                listener.showErrorDialog("Error", "Operatives can not be selected for recorded hours.", false);
            }
            String repeatId = formItem.getRepeatId();
            String uploadId = formItem.getUploadId();

            if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)
                    || formItem.getKey().equalsIgnoreCase(JobWorkItem.DBTable.NAME)
                    || formItem.getKey().equalsIgnoreCase(HseqDataset.DBTable.OperativesHseq)
                    || formItem.getKey().equalsIgnoreCase(TimesheetOperative.DBTable.NAME)
            ) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("submissionID", submissionID);
                intent.putExtra("repeatId", repeatId);
                intent.putExtra("uploadId", uploadId);
                intent.putExtra("keyItemType", formItem.getKey());
                intent.putExtra("isMultiSelection", formItem.isMultiSelection());
                intent.putExtra("isConcatDisplayText", formItem.isConcatDisplayText());
                intent.putExtra("recipients", recipients);
                intent.putExtra("repeatCount", repeatCount);
                intent.putExtra(ListActivity.ARGS_THEME_COLOR, themeColor);
                context.startActivity(intent);
            } else {
                final ArrayList<DropDownItem> items = new ArrayList<>();

                if (formItem.getKey().equalsIgnoreCase(Job.DBTable.NAME)) {
                    items.addAll(dbHandler.getJobs());
                } else if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.riskAssessmentRiskElementTypes)) {
                    items.addAll(dbHandler.getRiskElementType(formItem.getKey()));
                } else if (formItem.getKey().equalsIgnoreCase(MeasureItems.DBTable.NAME)) {
                    items.addAll(dbHandler.getMeasures());
                } else if (formItem.getKey().equalsIgnoreCase(TimeTypeActivity.DBTable.NAME)) {
                    items.addAll(dbHandler.getTimeTypeActivities());
                } else if (formItem.getKey().equalsIgnoreCase(MenSplit.DBTable.NAME)) {
                    items.addAll(dbHandler.getMenSplit());
                } else {
                    items.addAll(dbHandler.getItemTypes(formItem.getKey()));
                }

                final DropdownMenu dropdownMenu = DropdownMenu.newInstance(items);
                dropdownMenu.setListener(position1 -> {
                    holder.txtValue.setText(items.get(position1).getDisplayItem());
                    if (formItem.getUploadId().equalsIgnoreCase("timeTypeActivityId")) {
                        Answer timeTypeName = dbHandler.getAnswer(submissionID, "timeTypeName",
                                formItem.getRepeatId(), repeatCount);
                        if (timeTypeName == null) {
                            timeTypeName = new Answer(submissionID, "timeTypeName", formItem.getRepeatId(), repeatCount);

                        }
                        timeTypeName.setShouldUpload(false);
                        String displayAnswer = items.get(position1).getDisplayItem();
                        String type = "SHRINKAGE";
                        if (displayAnswer.equalsIgnoreCase("Working") || displayAnswer.equalsIgnoreCase("Travel")) {
                            type = "ON TASK";
                        } else if (displayAnswer.equalsIgnoreCase("Briefing") || displayAnswer.equalsIgnoreCase("Admin") ||
                                displayAnswer.equalsIgnoreCase("Vehicle check") || displayAnswer.equalsIgnoreCase("Lunch")) {
                            type = "OFF TASK";
                        }
                        timeTypeName.setAnswer(type);
                        timeTypeName.setDisplayAnswer(type);
                        dbHandler.replaceData(Answer.DBTable.NAME, timeTypeName.toContentValues());
                    }

                    Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(),
                            formItem.getRepeatId(), repeatCount);
                    if (answer1 == null) {
                        answer1 = new Answer(submissionID, formItem.getUploadId());
                    }

                    answer1.setAnswer(items.get(position1).getUploadValue());
                    answer1.setDisplayAnswer(items.get(position1).getDisplayItem());
                    answer1.setRepeatID(formItem.getRepeatId());
                    answer1.setRepeatCount(repeatCount);

                    dbHandler.replaceData(Answer.DBTable.NAME, answer1.toContentValues());
                    if (formItem.getUploadId().equalsIgnoreCase("type") &&
                            formItem.getRepeatId().equalsIgnoreCase("riskElements")) {
                        if (items.get(position1).getUploadValue().equalsIgnoreCase("dorSPoles")) {
                            ArrayList<FormItem> items1 = JsonReader.loadFormJSON(context, FormItem.class, "pra_DOS.json");
                            formItem.setDialogItems(items1);
                        } else if (items.get(position1).getUploadValue().equalsIgnoreCase("workAtHeight")) {
                            ArrayList<FormItem> items1 = JsonReader.loadFormJSON(context, FormItem.class, "pra_work_n_height.json");
                            formItem.setDialogItems(items1);
                        } else {
                            ArrayList<FormItem> items1 = JsonReader.loadFormJSON(context, FormItem.class, "pra_access.json");
                            formItem.setDialogItems(items1);
                        }

                        Answer consideration = dbHandler.getAnswer(submissionID, "",
                                formItem.getRepeatId(), repeatCount);
                        if (consideration == null) {
                            consideration = new Answer(submissionID, "consideration");

                        }

                        consideration.setAnswer(((RiskElementType) items.get(position1)).getOnScreenText());
                        consideration.setDisplayAnswer(((RiskElementType) items.get(position1)).getOnScreenText());
                        consideration.setRepeatID(formItem.getRepeatId());
                        consideration.setRepeatCount(repeatCount);

                        dbHandler.replaceData(Answer.DBTable.NAME, consideration.toContentValues());

                        listener.openForkFragment(formItem, submissionID, repeatCount);
                    }

                    if (needToBeNotified(formItem)) {
                        reInflateItems(true);
                    }
                });

                listener.showBottomSheet(dropdownMenu);
            }
        });
    }


    private void bindBoldTextHolder(BoldTextHolder holder, int position) {
        FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
    }

    private void bindDescTextHolder(DescTextHolder holder, int position) {
        FormItem formItem = formItems.get(position);
        Answer answer = dbHandler.getAnswer(submissionID,
                "consideration",
                formItem.getRepeatId(), repeatCount);

        if (answer != null && !TextUtils.isEmpty(answer.getDisplayAnswer())) {
            holder.txtTitle.setText(answer.getDisplayAnswer());
        }
    }


    private void bindPhotoHolder(PhotoHolder holder, int position) {
        FormItem formItem = formItems.get(position);
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


    private void bindForkCard(ForkCardHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        holder.view.setOnClickListener(v -> {
            int rC = 0;
            ArrayList<String> fields = formItem.getFields();
            ArrayList<Answer> answers = new ArrayList<>();
            String repeatId = formItem.getRepeatId();
            if (fields != null && !fields.isEmpty()) {
                if (repeatId.equalsIgnoreCase("dfeItems") ||
                        repeatId.equalsIgnoreCase("negDfeItems")) {
                    answers.addAll(dbHandler.getRepeatedAnswers(submissionID, fields.get(0), "dfeItems"));
                    answers.addAll(dbHandler.getRepeatedAnswers(submissionID, fields.get(0), "negDfeItems"));
                } else {
                    answers.addAll(dbHandler.getRepeatedAnswers(submissionID, fields.get(0), repeatId));
                }

                for (int i = 0; i < answers.size(); i++) {
                    if (rC <= answers.get(i).getRepeatCount()) {
                        rC = answers.get(i).getRepeatCount();
                    }
                }
            }
            rC = rC + 1;
            listener.openForkFragment(formItem, submissionID, rC);
        });

    }

    private void bindSignatureHolder(final SignatureHolder holder, int position) {

        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());
        Answer answer = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

        holder.imgSignature.setImageDrawable(null);
        if (answer != null) {
            Glide.with(context).load(answer.getAnswer()).into(holder.imgSignature);
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.view.setOnClickListener(view -> listener.openSignature(formItem, submissionID, repeatCount));
        if (themeColor != null && !themeColor.isEmpty()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.btnClear.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
            }
        }

        holder.btnClear.setOnClickListener(view -> {
            Answer answer1 = dbHandler.getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
            if (answer1 != null) {
                dbHandler.removeAnswer(answer1);
            }
            holder.imgSignature.setImageDrawable(null);
        });
    }

    private void bindTimeSheetHoursHolder(TimeSheetHourHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.llOverTime.setVisibility(formItem.isOverTimeVisible() ? View.VISIBLE : View.GONE);
        int time = 0;
        int normalTimeInMinutes = 0;
        int overTimeInMinutes = 0;

        Answer normalTime = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);

        if (normalTime != null && !TextUtils.isEmpty(normalTime.getAnswer())) {
            holder.txtNormalTime.setText(normalTime.getDisplayAnswer());
            normalTimeInMinutes = CommonUtils.parseInt(normalTime.getAnswer());
        }

        Answer overTime = DBHandler.getInstance().getAnswer(submissionID, "overtimeMinutes", formItem.getRepeatId(), repeatCount);
        if (overTime != null && !TextUtils.isEmpty(overTime.getAnswer())) {
            holder.txtOverTime.setText(overTime.getDisplayAnswer());
            overTimeInMinutes = CommonUtils.parseInt(overTime.getAnswer());

        }
        time = normalTimeInMinutes + overTimeInMinutes;

        holder.txtTotalHours.setText(CommonUtils.getDisplayTime(time));

        holder.txtNormalTime.setOnClickListener(v -> {
            int normalTimeMins = 0;

            Answer normalTime2 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                    formItem.getRepeatId(), repeatCount);

            if (normalTime2 != null && !TextUtils.isEmpty(normalTime2.getAnswer())) {
                normalTimeMins = CommonUtils.parseInt(normalTime2.getAnswer());
            }

            new HourPickerDialog.Builder(context).setListener((timeInMinutes, displayValue) -> {
                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
                }

                answer1.setAnswer(String.valueOf(timeInMinutes));
                answer1.setDisplayAnswer(displayValue);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

                int totalTime = CommonUtils.parseInt(answer1.getAnswer());

                Answer overTime1 = DBHandler.getInstance().getAnswer(submissionID, "overtimeMinutes", formItem.getRepeatId(), repeatCount);
                if (overTime1 != null && !TextUtils.isEmpty(overTime1.getAnswer())) {
                    totalTime += CommonUtils.parseInt(overTime1.getAnswer());
                }
                holder.txtNormalTime.setText(displayValue);
                holder.txtTotalHours.setText(CommonUtils.getDisplayTime(totalTime));
            }).setTimeInMinutes(normalTimeMins).build().show();

        });

        holder.txtOverTime.setOnClickListener(v -> {
            int overTimeMins = 0;
            Answer overTime2 = DBHandler.getInstance().getAnswer(submissionID, "overtimeMinutes", formItem.getRepeatId(), repeatCount);
            if (overTime2 != null && !TextUtils.isEmpty(overTime2.getAnswer())) {
                overTimeMins = CommonUtils.parseInt(overTime2.getAnswer());
            }
            new HourPickerDialog.Builder(context).setListener((timeInMinutes, displayValue) -> {
                Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, "overtimeMinutes", formItem.getRepeatId(), repeatCount);

                if (answer1 == null) {
                    answer1 = new Answer(submissionID, "overtimeMinutes", formItem.getRepeatId(), repeatCount);
                }

                answer1.setAnswer(String.valueOf(timeInMinutes));
                answer1.setDisplayAnswer(displayValue);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

                int totalTime = CommonUtils.parseInt(answer1.getAnswer());

                Answer normalTime1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
                if (normalTime1 != null && !TextUtils.isEmpty(normalTime1.getAnswer())) {
                    totalTime += CommonUtils.parseInt(normalTime1.getAnswer());
                }
                holder.txtOverTime.setText(displayValue);
                holder.txtTotalHours.setText(CommonUtils.getDisplayTime(totalTime));
            }).setTimeInMinutes(overTimeMins).build().show();
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
        if (focusedEditText != null) {
            focusedEditText.clearFocus();
        }

        missingAnswerMode = false;
        int missingCount = 0;
        for (int c = 0; c < formItems.size(); c++) {
            FormItem item = formItems.get(c);

            if (submission.getJsonFileName().contains("log_measure.json")
                    && !TextUtils.isEmpty(item.getUploadId())
                    && item.getUploadId().equalsIgnoreCase("comments")) {
                Answer answer = dbHandler.getAnswer(submissionID,
                        item.getUploadId(), item.getRepeatId(), repeatCount);
                if (!TextUtils.isEmpty(answer.getAnswer())) {
                    try {
                        int commentSize = answer.getAnswer().length();
                        if (commentSize > 150) {
                            missingCount++;
                            listener.showValidationDialog("Validation Error", "Please provide maximum 150 character for comment field");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            if (!item.isOptional() && item.getUploadId() != null) {
                Answer answer = dbHandler.getAnswer(submissionID,
                        item.getUploadId(), item.getRepeatId(), repeatCount);
                if (answer == null || TextUtils.isEmpty(answer.getAnswer())) {
                    missingCount++;
                }
                if (submission.getJsonFileName().equalsIgnoreCase("log_measure.json")
                        && !TextUtils.isEmpty(item.getUploadId())
                        && item.getUploadId().equalsIgnoreCase("measureQuantity")) {
                    if (!TextUtils.isEmpty(answer.getAnswer())) {
                        try {
                            int qty = Integer.parseInt(answer.getAnswer());

                            Answer code = dbHandler.getAnswer(submissionID,
                                    "synthCode", "measures", repeatCount);
                            if (code != null && !TextUtils.isEmpty(code.getAnswer())) {
                                ArrayList<Answer> answers = dbHandler.getRepeatedAnswers(submissionID,
                                        "synthCode", "measures");
                                JobWorkItem workItem = dbHandler.getJobWorkItem(submission.getJobID(), code.getAnswer());
                                if (workItem != null) {

                                    if (qty == 0 || qty > workItem.getAvailableToMeasureQuantity()) {
                                        missingCount++;
                                        listener.showValidationDialog("Validation Error", "Please enter correct quantity");
                                    }
                                    //validation check for same item
                                    else if (answers != null && answers.size() > 1) {
                                        for (Answer A1 : answers) {
                                            List<Answer> subanswers = answers.subList(answers.indexOf(A1) + 1, answers.size());
                                            for (Answer A2 : subanswers) {
                                                if (A1.getAnswer().equalsIgnoreCase(A2.getAnswer())) {
                                                    missingCount++;
                                                    listener.showValidationDialog("Validation Error", "Please select different workitem!");
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else if (!TextUtils.isEmpty(item.getRepeatId()) && item.getRepeatId().equalsIgnoreCase("negItems")) {
                    if (!TextUtils.isEmpty(item.getUploadId()) && item.getUploadId().equalsIgnoreCase("quantity")) {
                        if (!TextUtils.isEmpty(answer.getAnswer())) {
                            try {
                                int qty = Integer.parseInt(answer.getAnswer());
                                if (qty < 0) {
                                    qty = -1 * qty;
                                }

                                if (qty == 0) {
                                    missingCount++;
                                    listener.showValidationDialog("Validation Error", "Please enter quantity other than 0");
                                } else {
                                    String uploadId = "itemCode";
                                    Answer code = dbHandler.getAnswer(submissionID,
                                            uploadId, "negItems", repeatCount);
                                    if (code == null) {
                                        uploadId = "itemId";
                                        code = dbHandler.getAnswer(submissionID,
                                                uploadId, "negItems", repeatCount);
                                    }
                                    if (code != null && !TextUtils.isEmpty(code.getAnswer())) {
                                        JobWorkItem workItem;
                                        if (uploadId.equalsIgnoreCase("itemCode")) {
                                            workItem = dbHandler.getJobWorkItemByItemCode(submission.getJobID(), code.getAnswer());
                                        } else {
                                            workItem = dbHandler.getJobWorkItem(submission.getJobID(), code.getAnswer());
                                        }

                                        if (workItem != null) {
                                            if (qty > workItem.getAvailableToMeasureQuantity()) {
                                                missingCount++;
                                                listener.showValidationDialog("Validation Error", "Please enter correct quantity");
                                            }
                                        }
                                    }
                                }
                            } catch (Exception e) {
                            }
                        }
                    }
                }

            }
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
        listener.openCamera(submissionID, formItem, repeatCount);
    }

    @Override
    public void onAllPhotosRemoved() {
        notifyDataSetChanged();
    }


    private void openDatePicker() {

//        ArrayList<EventDay> eventDays = new ArrayList<>();
//        Calendar calendar = Calendar.getInstance();
//        for(int i = 0 ; i < 1 ; i++){
//            calendar.add(Calendar.DAY_OF_MONTH , 1);
//            EventDay day = new EventDay(calendar , R.drawable.pending_day_circle);
//            eventDays.add(day);
//        }
//        DatePickerBuilder builder = new DatePickerBuilder(context, new OnSelectDateListener() {
//            @Override
//            public void onSelect(List<Calendar> calendar) {
//
//            }
//        }).setPickerType(CalendarView.ONE_DAY_PICKER)
//                .setEvents(eventDays);
//
//        bu
//
//        DatePicker datePicker = builder.build();

//        datePicker.show();
    }
}
