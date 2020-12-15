package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ListActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.formholders.BoldTextHolder;
import co.uk.depotnet.onsa.formholders.BriefingSignHolder;
import co.uk.depotnet.onsa.formholders.Briefingtextholder;
import co.uk.depotnet.onsa.formholders.DFEItemHolder;
import co.uk.depotnet.onsa.formholders.DescTextHolder;
import co.uk.depotnet.onsa.formholders.DropDownHolder;
import co.uk.depotnet.onsa.formholders.ForkCardHolder;
import co.uk.depotnet.onsa.formholders.LongTextHolder;
import co.uk.depotnet.onsa.formholders.NumberHolder;
import co.uk.depotnet.onsa.formholders.PhotoHolder;
import co.uk.depotnet.onsa.formholders.ShortTextHolder;
import co.uk.depotnet.onsa.formholders.SignatureHolder;
import co.uk.depotnet.onsa.formholders.YesNoHolder;
import co.uk.depotnet.onsa.fragments.FragmentStopWork;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.PhotoAdapterListener;
import co.uk.depotnet.onsa.modals.JobWorkItem;
import co.uk.depotnet.onsa.modals.MeasureItems;
import co.uk.depotnet.onsa.modals.RiskElementType;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Submission;
import co.uk.depotnet.onsa.modals.hseq.HseqDataset;
import co.uk.depotnet.onsa.modals.responses.DatasetResponse;
import co.uk.depotnet.onsa.utils.JsonReader;
import co.uk.depotnet.onsa.views.DropdownMenu;

public class FrokFormAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
        implements PhotoAdapterListener {


    private Context context;
    private FormItem parentFormItem;
    private List<FormItem> formItems;

    private long submissionID;
    private int repeatCount;
    private Submission submission;
    private EditText focusedEditText;
    private boolean missingAnswerMode;
    private FormAdapterListener listener;
    private GradientDrawable redBG;
    private String themeColor;
    private ArrayList<FormItem> originalItems;
    private ArrayList<String> recipients;

    public FrokFormAdapter(Context context, Submission submission,
                           FormItem parentFormItem, int repeatCount,
                           FormAdapterListener listener ,String themeColor , ArrayList<String> recipients) {
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

        addListItems();
    }


    public void addListItems() {

        ArrayList<FormItem> dialogItems = parentFormItem.getDialogItems();
        formItems.clear();
        int forkPosition = 0;
        boolean ifItemAdded = false;
        boolean ifPosDFEAdded = false;
        boolean ifNegDFEAdded = false;
        DBHandler dbHandler = DBHandler.getInstance();
        ArrayList<FormItem> listItems = new ArrayList<>();
        for (int c = 0; c < dialogItems.size(); c++) {
            FormItem item = dialogItems.get(c);
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

        addBriefingsDocs();

    }

    public void addBriefingsDocs() {
        ArrayList<FormItem> dialogItems = parentFormItem.getDialogItems();
        int forkPosition = 0;

        DBHandler dbHandler = DBHandler.getInstance();
        ArrayList<FormItem> listItems = new ArrayList<>();
        for (int c = 0; c < dialogItems.size(); c++) {
            FormItem item = dialogItems.get(c);
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
                formItems.addAll(forkPosition+1, listItems);
                break;
            }
        }

        notifyDataSetChanged();
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

        }
        return null;
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
        }
    }

    private void bindBRIEFINGSignHolder(BriefingSignHolder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        final ArrayList<String> fields = formItem.getFields();
        if (fields == null || fields.isEmpty()) {
            return;
        }

        Answer answerItemId = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer docname = DBHandler.getInstance().getAnswer(submissionID, fields.get(1),
                formItem.getRepeatId(), formItem.getRepeatCount());
        Answer quantity = DBHandler.getInstance().getAnswer(submissionID, fields.get(2),
                formItem.getRepeatId(), formItem.getRepeatCount());

        if (answerItemId != null) {
            String value = answerItemId.getAnswer();
            if (!TextUtils.isEmpty(value)) {
                //holder.txtValue.setText(value);
                holder.txtTitle.setText(answerItemId.getDisplayAnswer());
            }
        }
        if (docname!=null)
        {
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
            Answer answerItemId1 = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                    formItem.getRepeatId(), formItem.getRepeatCount());

            Answer quantity1 = DBHandler.getInstance().getAnswer(submissionID, fields.get(2),
                    formItem.getRepeatId(), formItem.getRepeatCount());
            if (answerItemId1 != null) {
                DBHandler.getInstance().removeAnswer(answerItemId1);
            }
            // do not remove doc name of position 1 - for multiple item use to display...
            if (quantity1 != null) {
                DBHandler.getInstance().removeAnswer(quantity1);
            }

            notifyDataSetChanged();
        });

        holder.view.setOnClickListener(v -> listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount()));

    }
    private void bindBRIEFINGTEXTHolder(Briefingtextholder holder, final int position) {
        final FormItem formItem = formItems.get(position);
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
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

        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Answer answer = DBHandler.getInstance().getAnswer(submissionID,
                            formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                    if (answer == null) {
                        answer = new Answer(submissionID, formItem.getUploadId());
                    }


                    EditText et = (EditText) view;
                    answer.setAnswer(et.getText().toString());
                    answer.setRepeatID(formItem.getRepeatId());
                    answer.setRepeatCount(repeatCount);
                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
                    focusedEditText = null;
                } else {
                    focusedEditText = (EditText) view;
                }
            }
        });

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

        holder.btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.btnYes.setSelected(true);
                holder.btnNo.setSelected(false);

                if (formItem.getStopWork() == 1) {
                    FragmentStopWork fragmentStopWork = FragmentStopWork.newInstance(submission);
                    listener.openFragment(fragmentStopWork);
                    return;
                }

                Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);
                if (answer == null) {
                    answer = new Answer(submissionID, formItem.getUploadId());
                }
                answer.setAnswer("true");
                answer.setRepeatID(formItem.getRepeatId());
                answer.setRepeatCount(repeatCount);
                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());

                if (needToBeNotified(formItem)) {
                    reInflateItems(true);
                }

            }
        });

        holder.btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                holder.btnYes.setSelected(false);
                holder.btnNo.setSelected(true);
                if (formItem.getStopWork() == 2) {
                    FragmentStopWork fragmentStopWork = FragmentStopWork.newInstance(submission);
                    listener.openFragment(fragmentStopWork);
                    return;
                }

                Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                        formItem.getRepeatId(), repeatCount);
                if (answer == null) {
                    answer = new Answer(submissionID, formItem.getUploadId());
                }

                answer.setAnswer("false");
                answer.setRepeatID(formItem.getRepeatId());
                answer.setRepeatCount(repeatCount);

                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());

                if (needToBeNotified(formItem)) {
                    reInflateItems(true);
                }
            }
        });

    }

    private boolean needToBeNotified(FormItem formItem) {
        return (formItem.getEnables() != null && !formItem.getEnables().isEmpty())
                || (formItem.getFalseEnables() != null && !formItem.getFalseEnables().isEmpty())
                || (formItem.getNaEnables() != null && !formItem.getNaEnables().isEmpty());
    }

    private void reInflateItems(boolean isNotified){
        this.formItems.clear();
        for (int i = 0; i < originalItems.size(); i++) {
            FormItem item = originalItems.get(i);
            this.formItems.add(new FormItem(item));
            if(!TextUtils.isEmpty(item.getUploadId())){
                Answer answer = DBHandler.getInstance().getAnswer(submissionID,
                        item.getUploadId(), item.getRepeatId(), repeatCount);
                if(answer != null){
                    String value = answer.getAnswer();
                    if (!TextUtils.isEmpty(value)) {
                        if(item.getFormType() == FormItem.TYPE_YES_NO || item.getFormType() == FormItem.TYPE_PASS_FAIL){
                            addEnableItems(item , value);
                        }
                    }
                }
            }
        }

        if(isNotified){
            notifyDataSetChanged();
        }
    }

    private void addEnableItems(FormItem formItem, String value) {
        ArrayList<FormItem> toBeAdded = new ArrayList<>();
        ArrayList<FormItem> toBeRemoved = new ArrayList<>();
        ArrayList<FormItem> enableItems = formItem.getEnables();
        ArrayList<FormItem> falseEnableItems = formItem.getFalseEnables();
        ArrayList<FormItem> naEnableItems = formItem.getNaEnables();


        if(value.equalsIgnoreCase("1") || value.equalsIgnoreCase("true")){
            if(enableItems!= null && !enableItems.isEmpty()){
                toBeAdded.addAll(enableItems);
            }
            if(falseEnableItems!= null && !falseEnableItems.isEmpty()){
                toBeRemoved.addAll(falseEnableItems);
            }
            if(naEnableItems!= null && !naEnableItems.isEmpty()){
                toBeRemoved.addAll(naEnableItems);
            }
        }else if(value.equalsIgnoreCase("2") || value.equalsIgnoreCase("false")){
            if(falseEnableItems!= null && !falseEnableItems.isEmpty()){
                toBeAdded.addAll(falseEnableItems);
            }
            if(enableItems!= null && !enableItems.isEmpty()){
                toBeRemoved.addAll(enableItems);
            }
            if(naEnableItems!= null && !naEnableItems.isEmpty()){
                toBeRemoved.addAll(naEnableItems);
            }
        }else if(value.equalsIgnoreCase("3")){
            if(naEnableItems!= null && !naEnableItems.isEmpty()){
                toBeAdded.addAll(naEnableItems);
            }
            if(falseEnableItems!= null && !falseEnableItems.isEmpty()){
                toBeRemoved.addAll(falseEnableItems);
            }
            if(enableItems!= null && !enableItems.isEmpty()){
                toBeRemoved.addAll(enableItems);
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

        if (!toBeAdded.isEmpty()) {
            formItems.addAll(toBeAdded);
        }
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

        holder.llBtnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formItems.remove(position);
                Answer answerItemId = DBHandler.getInstance().getAnswer(submissionID, fields.get(0),
                        formItem.getRepeatId(), formItem.getRepeatCount());
                Answer quantity = DBHandler.getInstance().getAnswer(submissionID, fields.get(1),
                        formItem.getRepeatId(), formItem.getRepeatCount());
                if (answerItemId != null) {
                    DBHandler.getInstance().removeAnswer(answerItemId);
                }

                if (quantity != null) {
                    DBHandler.getInstance().removeAnswer(quantity);
                }

                notifyDataSetChanged();
            }
        });

        holder.llBtnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.openForkFragment(formItem, submissionID, formItem.getRepeatCount());
            }
        });

    }

    private void bindShortTextHolder(ShortTextHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
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

        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                    if (answer == null) {
                        answer = new Answer(submissionID, formItem.getUploadId());
                    }
                    if (answer.getUploadID() != null &&
                            answer.getUploadID().equalsIgnoreCase("Visitors")) {
                        answer.setIsMultiList(1);
                    }

                    EditText et = (EditText) view;
                    answer.setAnswer(et.getText().toString());
                    answer.setRepeatID(formItem.getRepeatId());
                    answer.setRepeatCount(repeatCount);
                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
                    focusedEditText = null;
                } else {
                    focusedEditText = (EditText) view;
                }
            }
        });

    }

    private void bindNumberHolder(NumberHolder holder, int position) {
        final FormItem formItem = formItems.get(position);
        holder.txtTitle.setText(formItem.getTitle());

        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                formItem.getRepeatId(), repeatCount);
        if (answer != null) {
            holder.editText.setText(answer.getDisplayAnswer());
        }

        holder.editText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

                    if (answer == null) {
                        answer = new Answer(submissionID, formItem.getUploadId());
                    }

                    EditText et = (EditText) view;
                    String text = et.getText().toString();
                    if (!TextUtils.isEmpty(formItem.getRepeatId()) &&
                            (formItem.getRepeatId().equalsIgnoreCase("negItems") || formItem.getRepeatId().equalsIgnoreCase("negDfeItems"))) {
                        text = "-" + text;
                    }
                    answer.setAnswer(text);
                    answer.setRepeatID(formItem.getRepeatId());
                    answer.setRepeatCount(repeatCount);
                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());
                    focusedEditText = null;
                } else {
                    focusedEditText = (EditText) view;
                }
            }
        });
    }


    private void bindDropDownHolder(final DropDownHolder holder, int position) {
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


        holder.view.setOnClickListener(view -> {

            String repeatId = formItem.getRepeatId();
            String uploadId = formItem.getUploadId();

            if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)
                    || formItem.getKey().equalsIgnoreCase(JobWorkItem.DBTable.NAME)
                    || formItem.getKey().equalsIgnoreCase(HseqDataset.DBTable.OperativesHseq)
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
                context.startActivity(intent);
            } else {
                final ArrayList<DropDownItem> items = new ArrayList<>();

                if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.riskAssessmentRiskElementTypes)) {
                    items.addAll(DBHandler.getInstance().getRiskElementType(formItem.getKey()));
                }if (formItem.getKey().equalsIgnoreCase(MeasureItems.DBTable.NAME)) {
                    items.addAll(DBHandler.getInstance().getMeasures());
                } else {
                    items.addAll(DBHandler.getInstance().getItemTypes(formItem.getKey()));
                }

                final DropdownMenu dropdownMenu = DropdownMenu.newInstance(items);
                dropdownMenu.setListener(position1 -> {
                    holder.txtValue.setText(items.get(position1).getDisplayItem());

                    Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                            formItem.getRepeatId(), repeatCount);
                    if (answer1 == null) {
                        answer1 = new Answer(submissionID, formItem.getUploadId());
                    }

                    answer1.setAnswer(items.get(position1).getUploadValue());
                    answer1.setDisplayAnswer(items.get(position1).getDisplayItem());
                    answer1.setRepeatID(formItem.getRepeatId());
                    answer1.setRepeatCount(repeatCount);

                    DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer1.toContentValues());

                    if (formItem.getUploadId().equalsIgnoreCase("type") && formItem.getRepeatId().equalsIgnoreCase("riskElements")) {
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

                        Answer consideration = DBHandler.getInstance().getAnswer(submissionID, "",
                                formItem.getRepeatId(), repeatCount);
                        if (consideration == null) {
                            consideration = new Answer(submissionID, "consideration");

                        }

                        consideration.setAnswer(((RiskElementType) items.get(position1)).getOnScreenText());
                        consideration.setDisplayAnswer(((RiskElementType) items.get(position1)).getOnScreenText());
                        consideration.setRepeatID(formItem.getRepeatId());
                        consideration.setRepeatCount(repeatCount);

                        DBHandler.getInstance().replaceData(Answer.DBTable.NAME, consideration.toContentValues());

                        listener.openForkFragment(formItem, submissionID, repeatCount);
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
        Answer answer = DBHandler.getInstance().getAnswer(submissionID,
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
            if (formItem.getPhotoSize() >= 100) {
                holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload_unlimited), formItem.getPhotoRequired()));
            } else {
                holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload), formItem.getPhotoSize(), formItem.getPhotoRequired()));
            }
        }
    }

    /*private void bindPhotoHolder(PhotoHolder holder, int position) {
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
                    String.valueOf(photo.getPhoto_id()),
                    repeatCount, photo.getTitle());
            if (answer != null) {
                photo.setUrl(answer.getAnswer());
                answers.add(answer);
                photosTaken++;
            }
        }

        holder.txtTitle.setText(String.format(context.getString(R.string.photo_upload), size, required));
        if (photosTaken == 0) {
            holder.recyclerView.setVisibility(View.GONE);
            holder.imgBtnCamera.setVisibility(View.VISIBLE);
            holder.imgBtnCamera.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.openCamera(submissionID, formItem, repeatCount);
                }
            });
        } else {
            holder.imgBtnCamera.setVisibility(View.GONE);
            holder.recyclerView.setVisibility(View.VISIBLE);
            holder.recyclerView.setAdapter(new AdapterPhoto(context, answers, formItem.getTitle(),
                    photos, repeatCount, this));
        }
    }*/

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
                    answers.addAll(DBHandler.getInstance().getRepeatedAnswers(submissionID, fields.get(0), "dfeItems"));
                    answers.addAll(DBHandler.getInstance().getRepeatedAnswers(submissionID, fields.get(0), "negDfeItems"));
                } else {
                    answers.addAll(DBHandler.getInstance().getRepeatedAnswers(submissionID, fields.get(0), repeatId));
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
        Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);

        holder.imgSignature.setImageDrawable(null);
        if (answer != null) {
            Glide.with(context).load(answer.getAnswer()).into(holder.imgSignature);
        } else if (missingAnswerMode && !formItem.isOptional()) {
            holder.view.setBackground(redBG);
        }

        holder.view.setOnClickListener(view -> listener.openSignature(formItem, submissionID, repeatCount));
        if (themeColor!=null && !themeColor.isEmpty())
        {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                holder.btnClear.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(themeColor)));
            }
        }

        holder.btnClear.setOnClickListener(view -> {
            Answer answer1 = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(), formItem.getRepeatId(), repeatCount);
            if (answer1 != null) {
                DBHandler.getInstance().removeAnswer(answer1);
            }
            holder.imgSignature.setImageDrawable(null);
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
            if (!item.isOptional() && item.getUploadId() != null) {
                Answer answer = DBHandler.getInstance().getAnswer(submissionID,
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

                            Answer code = DBHandler.getInstance().getAnswer(submissionID,
                                    "synthCode", "measures", repeatCount);
                            if (code != null && !TextUtils.isEmpty(code.getAnswer())) {
                                ArrayList<Answer> answers = DBHandler.getInstance().getRepeatedAnswers(submissionID,
                                        "synthCode", "measures");
                                JobWorkItem workItem = DBHandler.getInstance().getJobWorkItem(submission.getJobID(), code.getAnswer());
                                if (workItem != null) {

                                    if (qty == 0 || qty > workItem.getAvailableToMeasureQuantity()) {
                                        missingCount++;
                                        listener.showValidationDialog("Validation Error", "Please enter correct quantity");
                                    }
                                    //validation check for same item
                                    else if (answers!=null && answers.size()>1) {
                                            for (Answer A1 : answers)
                                            {
                                                List<Answer> subanswers = answers.subList(answers.indexOf(A1) + 1, answers.size());
                                                for (Answer A2 : subanswers) {
                                                    if (A1.getAnswer().equalsIgnoreCase(A2.getAnswer()))
                                                    {
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
                }else if (!TextUtils.isEmpty(item.getRepeatId()) && item.getRepeatId().equalsIgnoreCase("negItems")) {
                    if (!TextUtils.isEmpty(item.getUploadId()) && item.getUploadId().equalsIgnoreCase("quantity")) {
                        if (!TextUtils.isEmpty(answer.getAnswer())) {
                            try {
                                int qty = Integer.parseInt(answer.getAnswer());
                                if (qty < 0) {
                                    qty = -1 * qty;
                                }
                                Answer code = DBHandler.getInstance().getAnswer(submissionID,
                                        "itemCode", "negItems", repeatCount);
                                if (code != null && !TextUtils.isEmpty(code.getAnswer())) {
                                    JobWorkItem workItem = DBHandler.getInstance().getJobWorkItem(submission.getJobID(), code.getAnswer());
                                    if (workItem != null) {
                                        if (qty > workItem.getquantity()) {
                                            missingCount++;
                                            listener.showValidationDialog("Validation Error", "Please enter correct quantity");
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
}
