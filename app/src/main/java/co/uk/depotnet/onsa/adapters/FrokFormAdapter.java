package co.uk.depotnet.onsa.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import co.uk.depotnet.onsa.R;
import co.uk.depotnet.onsa.activities.ListActivity;
import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.formholders.BoldTextHolder;
import co.uk.depotnet.onsa.formholders.DFEItemHolder;
import co.uk.depotnet.onsa.formholders.DescTextHolder;
import co.uk.depotnet.onsa.formholders.DropDownHolder;
import co.uk.depotnet.onsa.formholders.ForkCardHolder;
import co.uk.depotnet.onsa.formholders.LongTextHolder;
import co.uk.depotnet.onsa.formholders.NumberHolder;
import co.uk.depotnet.onsa.formholders.PhotoHolder;
import co.uk.depotnet.onsa.formholders.ShortTextHolder;
import co.uk.depotnet.onsa.formholders.YesNoHolder;
import co.uk.depotnet.onsa.fragments.FragmentStopWork;
import co.uk.depotnet.onsa.listeners.DropDownItem;
import co.uk.depotnet.onsa.listeners.FormAdapterListener;
import co.uk.depotnet.onsa.listeners.PhotoAdapterListener;
import co.uk.depotnet.onsa.modals.RiskElementType;
import co.uk.depotnet.onsa.modals.forms.Answer;
import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;
import co.uk.depotnet.onsa.modals.forms.Submission;
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

    public FrokFormAdapter(Context context, Submission submission,
                           FormItem parentFormItem, int repeatCount,
                           FormAdapterListener listener) {
        this.context = context;
        this.parentFormItem = parentFormItem;
        this.submission = submission;
        this.submissionID = submission.getID();
        this.repeatCount = repeatCount;
        this.listener = listener;
        this.formItems = new ArrayList<>();
        this.formItems.addAll(parentFormItem.getDialogItems());

        redBG = new GradientDrawable();
        redBG.setColor(Color.parseColor("#e24444"));

        addListItems();
    }


    public void addListItems() {

        ArrayList<FormItem> dialogItems = parentFormItem.getDialogItems();
        formItems.clear();
        int forkPosition = 0;
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

            }
        }

        formItems.addAll(forkPosition, listItems);
        notifyDataSetChanged();

        if (!listItems.isEmpty()) {
            repeatCount++;
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
                return new BoldTextHolder(LayoutInflater.from(context).inflate(R.layout.item_txt_bold_head, viewGroup, false));
            case FormItem.TYPE_TXT_DESC:
                return new DescTextHolder(LayoutInflater.from(context).inflate(R.layout.item_txt_bold_head, viewGroup, false));
            case FormItem.TYPE_ET_SHORT_TEXT:
                return new ShortTextHolder(LayoutInflater.from(context).inflate(R.layout.item_et_short_text, viewGroup, false));
            case FormItem.TYPE_DROPDOWN:
                return new DropDownHolder(LayoutInflater.from(context).inflate(R.layout.item_dropdown, viewGroup, false));
            case FormItem.TYPE_NUMBER:
                return new NumberHolder(LayoutInflater.from(context).inflate(R.layout.item_et_number, viewGroup, false));
            case FormItem.TYPE_FORK_CARD:
                return new ForkCardHolder(LayoutInflater.from(context).inflate(R.layout.item_fork_card, viewGroup, false));
            case FormItem.TYPE_PHOTO:
                return new PhotoHolder(LayoutInflater.from(context).inflate(R.layout.item_form_photo, viewGroup, false));
            case FormItem.TYPE_DFE_ITEM:
                return new DFEItemHolder(LayoutInflater.from(context).inflate(R.layout.item_dfe, viewGroup, false));

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
            case FormItem.TYPE_FORK_CARD:
                bindForkCard((ForkCardHolder) holder, position);
                break;
            case FormItem.TYPE_PHOTO:
                bindPhotoHolder((PhotoHolder) holder, position);
                break;
            case FormItem.TYPE_DFE_ITEM:
                bindDFEHolder((DFEItemHolder) holder, position);
                break;
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


        holder.view.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {

                String repeatId = formItem.getRepeatId();
                String uploadId = formItem.getUploadId();

                if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.dfeWorkItems)
                ) {
                    Intent intent = new Intent(context, ListActivity.class);
                    intent.putExtra("submissionID", submissionID);
                    intent.putExtra("repeatId", repeatId);
                    intent.putExtra("uploadId", uploadId);
                    intent.putExtra("keyItemType", formItem.getKey());
                    intent.putExtra("isMultiSelection", formItem.isMultiSelection());
                    intent.putExtra("isConcatDisplayText", formItem.isConcatDisplayText());
                    intent.putExtra("repeatCount", repeatCount);
                    context.startActivity(intent);
                } else {
                    final ArrayList<DropDownItem> items = new ArrayList<>();

                    if (formItem.getKey().equalsIgnoreCase(DatasetResponse.DBTable.riskAssessmentRiskElementTypes)) {
                        items.addAll(DBHandler.getInstance().getRiskElementType(formItem.getKey()));
                    } else {
                        items.addAll(DBHandler.getInstance().getItemTypes(formItem.getKey()));
                    }

                    final DropdownMenu dropdownMenu = DropdownMenu.newInstance(items);
                    dropdownMenu.setListener(new DropDownAdapter.OnItemSelectedListener() {
                        @Override
                        public void onItemSelected(int position) {
                            holder.txtValue.setText(items.get(position).getDisplayItem());

                            Answer answer = DBHandler.getInstance().getAnswer(submissionID, formItem.getUploadId(),
                                    formItem.getRepeatId(), repeatCount);
                            if (answer == null) {
                                answer = new Answer(submissionID, formItem.getUploadId());
                            }

                            answer.setAnswer(items.get(position).getUploadValue());
                            answer.setDisplayAnswer(items.get(position).getDisplayItem());
                            answer.setRepeatID(formItem.getRepeatId());
                            answer.setRepeatCount(repeatCount);

                            DBHandler.getInstance().replaceData(Answer.DBTable.NAME, answer.toContentValues());

                            if (formItem.getUploadId().equalsIgnoreCase("type") && formItem.getRepeatId().equalsIgnoreCase("riskElements")) {
                                if (items.get(position).getUploadValue().equalsIgnoreCase("dorSPoles")) {
                                    ArrayList<FormItem> items = JsonReader.loadFormJSON(context, FormItem.class, "pra_DOS.json");
                                    formItem.setDialogItems(items);
                                } else if (items.get(position).getUploadValue().equalsIgnoreCase("workAtHeight")) {
                                    ArrayList<FormItem> items = JsonReader.loadFormJSON(context, FormItem.class, "pra_work_n_height.json");
                                    formItem.setDialogItems(items);
                                } else {
                                    ArrayList<FormItem> items = JsonReader.loadFormJSON(context, FormItem.class, "pra_access.json");
                                    formItem.setDialogItems(items);
                                }

                                Answer consideration = DBHandler.getInstance().getAnswer(submissionID, "",
                                        formItem.getRepeatId(), repeatCount);
                                if (consideration == null) {
                                    consideration = new Answer(submissionID, "consideration");

                                }

                                consideration.setAnswer(((RiskElementType) items.get(position)).getOnScreenText());
                                consideration.setDisplayAnswer(((RiskElementType) items.get(position)).getOnScreenText());
                                consideration.setRepeatID(formItem.getRepeatId());
                                consideration.setRepeatCount(repeatCount);

                                DBHandler.getInstance().replaceData(Answer.DBTable.NAME, consideration.toContentValues());

                                listener.openForkFragment(formItem, submissionID, repeatCount);
                            }
                        }
                    });

                    listener.showBottomSheet(dropdownMenu);
                }
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
        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.openForkFragment(formItem, submissionID, repeatCount);
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
