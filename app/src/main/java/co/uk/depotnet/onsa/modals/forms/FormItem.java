package co.uk.depotnet.onsa.modals.forms;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;

import co.uk.depotnet.onsa.modals.httpresponses.BaseTask;
import co.uk.depotnet.onsa.modals.store.MyStore;
import co.uk.depotnet.onsa.modals.timesheet.LogHourItem;

public class FormItem implements Parcelable {

    public static final int TYPE_YES_NO = 1;
    public static final int TYPE_ET_LONG_TEXT = 2;
    public static final int TYPE_TXT_BOLD_HEAD = 3;
    public static final int TYPE_CHECKBOX = 4;
    public static final int TYPE_DROPDOWN = 5;
    public static final int TYPE_SIGNATURE = 6;
    public static final int TYPE_FORK = 7;
    public static final int TYPE_PHOTO = 8;
    public static final int TYPE_REPEAT_ITEM = 9;
    public static final int TYPE_TIME_SELECTOR_ITEM = 10;
    public static final int TYPE_REMOVE = 11;
    public static final int TYPE_PASS_FAIL = 12;
    public static final int TYPE_SWITCH = 13;
    public static final int TYPE_LOG_AND_DIG_FORK = 14;
    public static final int TYPE_NUMBER = 15;
    public static final int TYPE_ET_SHORT_TEXT = 16;
    public static final int TYPE_DROPDOWN_NUMBER = 17;
    public static final int TYPE_LOCATION = 19;
    public static final int TYPE_STORE_ITEM = 20;
    public static final int TYPE_FORK_CARD = 21;
    public static final int TYPE_DIALOG_SCREEN = 22;
    public static final int TYPE_DFE_ITEM = 23;
    public static final int TYPE_CALENDER = 24;
    public static final int TYPE_YES_NO_NA = 25;
    public static final int TYPE_STOP_WATCH = 26;
    public static final int TYPE_VISITOR_SIGN = 27;
    public static final int TYPE_DATE_TIME = 28;
    public static final int TYPE_TXT_DESC = 29;
    public static final int TYPE_RISK_ELEMENT = 30;
    public static final int TYPE_TAKE_PHOTO = 31;
    public static final int TYPE_CURRENT_STORE = 32;
    public static final int TYPE_ADD_STORE_ITEM = 33;
    public static final int TYPE_STOCK_ITEM = 34;
    public static final int TYPE_BAR_CODE = 35;
    public static final int TYPE_ADD_POS_DFE = 36;
    public static final int TYPE_ADD_NEG_DFE = 37;
    public static final int TYPE_ADD_LOG_MEASURE = 38;
    public static final int TYPE_LOG_MEASURE = 39;
    public static final int TYPE_TASK_LOG_BACKFILL = 40;
    public static final int TYPE_TASK_LOG_BACKFILL_ITEM = 41;
    public static final int TYPE_TASK_LOG_REINSTATEMENT = 42;
    public static final int TYPE_LOG_REINSTATEMENT_ITEM = 43;
    public static final int TYPE_TASK_LOG_MUCKAWAY = 44;
    public static final int TYPE_TASK_LOG_MUCKAWAY_ITEM = 45;
    public static final int TYPE_TASK_LOG_SERVICE = 46;
    public static final int TYPE_TASK_LOG_SERVICE_ITEM = 47;
    public static final int TYPE_TASK_VIEW_DIG_MEASURES = 48;
    public static final int TYPE_TASK_VIEW_DIG_MEASUERS_ITEM = 49;
    public static final int TYPE_TASK_VIEW_BACKFILL_MEASURES = 50;
    public static final int TYPE_TASK_VIEW_BACKFILL_MEASUERS_ITEM = 51;
    public static final int TYPE_TASK_VIEW_REINST_MEASURES = 52;
    public static final int TYPE_TASK_VIEW_REINST_MEASUERS_ITEM = 53;
    public static final int TYPE_ADD_DIG_MEASURE = 57;
    public static final int TYPE_LIST_DIG_MEASURE = 58;
    public static final int TYPE_TASK_SITE_CLEAR = 59;
    public static final int TYPE_TASK_SITE_CLEAR_ITEM = 60;
    public static final int TYPE_SIGN_BRIEFING = 61;
    public static final int TYPE_LIST_BREIFDOC = 62;
    public static final int TYPE_TV_BRIEFING_TEXT = 63;
    public static final int TYPE_ET_SEARCH_ESTIMATE = 64;
    public static final int TYPE_YES_NO_NA_tooltip = 65;
    public static final int TYPE_YES_NO_tooltip = 66;
    public static final int TYPE_RFNA_TOGGLE = 67;
    public static final int TYPE_YES_NO_NA_OPTIONAL = 68;
    public static final int TYPE_ADD_LOG_HOURS = 69;
    public static final int TYPE_ITEM_LOG_HOURS = 70;
    public static final int TYPE_ITEM_TIME_PICKER = 71;
    public static final int TYPE_ITEM_WEEK_COMMENCING = 72;
    public static final int TYPE_TASK_LIST_TIMESHEET = 73;
    public static final int TYPE_TASK_TIMESHEET_ITEM = 74;
    public static final int TYPE_TOTAL_WORKED_HOURS = 75;
    public static final int TYPE_ET_TIME_SHEET_HOURS = 76;
    public static final int TYPE_OPEN_LOG_HOURS = 77;
    public static final int TYPE_ITEM_DAY = 78;


    private String type;
    private String uploadId;
    private String title;
    private String hint;
    private boolean optional;
    private boolean isYesNotVisible;
    private boolean isNoNotVisible;
    private boolean isNaNotVisible;
    private boolean isChecked;
    private String imagePath;
    private String assetType;
    private ArrayList<String> fields;
    private String repeatId;
    private ArrayList<Photo> photos;
    private int repeatCount;
    private ArrayList<FormItem> falseEnables;
    private ArrayList<FormItem> enables;
    private ArrayList<FormItem> naEnables;
    private ArrayList<FormItem> dialogItems;
    private String getURL;
    private String signatureUrl;
    private int photoSize;
    private int photoRequired;
    private String photoId;
    private String key;
    private boolean isMultiSelection;
    private boolean isConcatDisplayText;
    private int stopWork;
    private String listItemType;
    private boolean isStoackLevelCheck;

    private MyStore myStore;
    private int myStoreQuantity;
    private int taskId;
    private BaseTask task;
    private String toolTip;
    private LogHourItem timeSheetHour;
    private String signatureFormat;
    private boolean isOverTimeVisible;


    public FormItem(FormItem formItem) {
        this.type = formItem.type;
        this.uploadId = formItem.uploadId;
        this.title = formItem.title;
        this.hint = formItem.hint;
        this.optional = formItem.optional;
        this.isYesNotVisible = formItem.isYesNotVisible;
        this.isNoNotVisible = formItem.isNoNotVisible;
        this.isNaNotVisible = formItem.isNaNotVisible;
        this.isChecked = formItem.isChecked;
        this.imagePath = formItem.imagePath;
        this.assetType = formItem.assetType;
        this.fields = formItem.fields;
        this.repeatId = formItem.repeatId;
        this.photos = formItem.photos;
        this.repeatCount = formItem.repeatCount;
        this.falseEnables = formItem.falseEnables;
        this.enables = formItem.enables;
        this.dialogItems = formItem.dialogItems;
        this.getURL = formItem.getURL;
        this.signatureUrl = formItem.signatureUrl;
        this.photoSize = formItem.photoSize;
        this.photoRequired = formItem.photoRequired;
        this.photoId = formItem.photoId;
        this.key = formItem.key;
        this.isMultiSelection = formItem.isMultiSelection;
        this.isConcatDisplayText = formItem.isConcatDisplayText;
        this.stopWork = formItem.stopWork;
        this.listItemType = formItem.listItemType;
        this.myStore = formItem.myStore;
        this.myStoreQuantity = formItem.myStoreQuantity;
        this.isStoackLevelCheck = formItem.isStoackLevelCheck;
        this.taskId = formItem.taskId;
        this.task = formItem.task;
        this.toolTip = formItem.toolTip;
        this.timeSheetHour = formItem.timeSheetHour;

    }

    protected FormItem(Parcel in) {
        type = in.readString();
        uploadId = in.readString();
        title = in.readString();
        hint = in.readString();
        optional = in.readByte() != 0;
        isYesNotVisible = in.readByte() != 0;
        isNoNotVisible = in.readByte() != 0;
        isNaNotVisible = in.readByte() != 0;
        isChecked = in.readByte() != 0;
        imagePath = in.readString();
        assetType = in.readString();
        fields = in.createStringArrayList();
        repeatId = in.readString();
        photos = in.createTypedArrayList(Photo.CREATOR);
        repeatCount = in.readInt();
        falseEnables = in.createTypedArrayList(FormItem.CREATOR);
        enables = in.createTypedArrayList(FormItem.CREATOR);
        naEnables = in.createTypedArrayList(FormItem.CREATOR);
        dialogItems = in.createTypedArrayList(FormItem.CREATOR);
        getURL = in.readString();
        signatureUrl = in.readString();
        photoSize = in.readInt();
        photoRequired = in.readInt();
        photoId = in.readString();
        key = in.readString();
        isMultiSelection = in.readByte() != 0;
        isConcatDisplayText = in.readByte() != 0;
        stopWork = in.readInt();
        listItemType = in.readString();
        isStoackLevelCheck = in.readByte() != 0;
        myStore = in.readParcelable(MyStore.class.getClassLoader());
        myStoreQuantity = in.readInt();
        taskId = in.readInt();
        task = in.readParcelable(BaseTask.class.getClassLoader());
        toolTip = in.readString();
        timeSheetHour = in.readParcelable(LogHourItem.class.getClassLoader());
        signatureFormat = in.readString();
        isOverTimeVisible = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(type);
        dest.writeString(uploadId);
        dest.writeString(title);
        dest.writeString(hint);
        dest.writeByte((byte) (optional ? 1 : 0));
        dest.writeByte((byte) (isYesNotVisible ? 1 : 0));
        dest.writeByte((byte) (isNoNotVisible ? 1 : 0));
        dest.writeByte((byte) (isNaNotVisible ? 1 : 0));
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeString(imagePath);
        dest.writeString(assetType);
        dest.writeStringList(fields);
        dest.writeString(repeatId);
        dest.writeTypedList(photos);
        dest.writeInt(repeatCount);
        dest.writeTypedList(falseEnables);
        dest.writeTypedList(enables);
        dest.writeTypedList(naEnables);
        dest.writeTypedList(dialogItems);
        dest.writeString(getURL);
        dest.writeString(signatureUrl);
        dest.writeInt(photoSize);
        dest.writeInt(photoRequired);
        dest.writeString(photoId);
        dest.writeString(key);
        dest.writeByte((byte) (isMultiSelection ? 1 : 0));
        dest.writeByte((byte) (isConcatDisplayText ? 1 : 0));
        dest.writeInt(stopWork);
        dest.writeString(listItemType);
        dest.writeByte((byte) (isStoackLevelCheck ? 1 : 0));
        dest.writeParcelable(myStore, flags);
        dest.writeInt(myStoreQuantity);
        dest.writeInt(taskId);
        dest.writeParcelable(task, flags);
        dest.writeString(toolTip);
        dest.writeParcelable(timeSheetHour, flags);
        dest.writeString(signatureFormat);
        dest.writeByte((byte) (isOverTimeVisible ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<FormItem> CREATOR = new Creator<FormItem>() {
        @Override
        public FormItem createFromParcel(Parcel in) {
            return new FormItem(in);
        }

        @Override
        public FormItem[] newArray(int size) {
            return new FormItem[size];
        }
    };

    public void setMyStore(MyStore myStore) {
        this.myStore = myStore;
    }

    public MyStore getMyStore() {
        return myStore;
    }

    public FormItem(String type, String title, String uploadID, String repeatID, boolean optional) {
        this.type = type;
        this.title = title;
        this.repeatId = repeatID;
        this.uploadId = uploadID;
        this.optional = optional;
    }

    public FormItem(String type) {
        this.type = type;
        this.optional = true;
    }

    public FormItem(String type, BaseTask task, boolean selectable) {
        this.type = type;
        this.task = task;
        this.task.setSelectable(selectable);
        this.optional = true;
    }

    public FormItem(String type, LogHourItem timeSheetHour, boolean selectable) {
        this.type = type;
        this.timeSheetHour = timeSheetHour;
        this.optional = true;
    }


    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUploadId() {
        return uploadId;
    }

    public void setUploadId(String uploadId) {
        this.uploadId = uploadId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }

    public boolean isOptional() {
        return optional;
    }

    public boolean isYesNotVisible() {
        return isYesNotVisible;
    }

    public boolean isNoNotVisible() {
        return isNoNotVisible;
    }

    public boolean isNaNotVisible() {
        return isNaNotVisible;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public void setisYesNotVisible(boolean isYesNotVisible) {
        this.isYesNotVisible = isYesNotVisible;
    }

    public void setisNoNotVisible(boolean isNoNotVisible) {
        this.isNoNotVisible = isNoNotVisible;
    }

    public void setisNaNotVisible(boolean isNaNotVisible) {
        this.isNaNotVisible = isNaNotVisible;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getRepeatId() {
        return repeatId;
    }

    public void setRepeatId(String repeatId) {
        this.repeatId = repeatId;
    }

    public ArrayList<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(ArrayList<Photo> photos) {
        this.photos = photos;
    }

    public ArrayList<String> getFields() {
        return fields;
    }

    public void setFields(ArrayList<String> fields) {
        this.fields = fields;
    }

    public int getRepeatCount() {
        return repeatCount;
    }

    public void setRepeatCount(int repeatCount) {
        this.repeatCount = repeatCount;
    }

    public ArrayList<FormItem> getEnables() {
        return enables;
    }

    public void setEnables(ArrayList<FormItem> enables) {
        this.enables = enables;
    }

    public ArrayList<FormItem> getDialogItems() {
        return dialogItems;
    }

    public void setDialogItems(ArrayList<FormItem> dialogItems) {
        this.dialogItems = dialogItems;
    }

    public String getURL() {
        return getURL;
    }

    public void setGetURL(String getURL) {
        this.getURL = getURL;
    }

    public String getListItemType() {
        return listItemType;
    }

    public void setListItemType(String listItemType) {
        this.listItemType = listItemType;
    }

    public boolean isConcatDisplayText() {
        return isConcatDisplayText;
    }

    public boolean isStoackLevelCheck() {
        return isStoackLevelCheck;
    }

    public int getTaskId() {
        return taskId;
    }

    public BaseTask getTask() {
        return task;
    }

    public String getToolTip() {
        return toolTip;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public LogHourItem getTimeSheetHour() {
        return timeSheetHour;
    }

    public void setTimeSheetHour(LogHourItem timeSheetHour) {
        this.timeSheetHour = timeSheetHour;
    }

    public String getSignatureFormat() {
        return signatureFormat;
    }

    public void setSignatureFormat(String signatureFormat) {
        this.signatureFormat = signatureFormat;
    }

    public boolean isOverTimeVisible() {
        return isOverTimeVisible;
    }

    public int getFormType() {
        if (type.equalsIgnoreCase("yes_no")) {
            return TYPE_YES_NO;
        } else if (type.equalsIgnoreCase("et_long_text")) {
            return TYPE_ET_LONG_TEXT;
        } else if (type.equalsIgnoreCase("txt_bold_head")) {
            return TYPE_TXT_BOLD_HEAD;
        } else if (type.equalsIgnoreCase("checkbox")) {
            return TYPE_CHECKBOX;
        } else if (type.equalsIgnoreCase("dropdown")) {
            return TYPE_DROPDOWN;
        } else if (type.equalsIgnoreCase("signature")) {
            return TYPE_SIGNATURE;
        } else if (type.equalsIgnoreCase("fork")) {
            return TYPE_FORK;
        } else if (type.equalsIgnoreCase("photo")) {
            return TYPE_PHOTO;
        } else if (type.equalsIgnoreCase("repeat_item")) {
            return TYPE_REPEAT_ITEM;
        } else if (type.equalsIgnoreCase("time_selector")) {
            return TYPE_TIME_SELECTOR_ITEM;
        } else if (type.equalsIgnoreCase("remove")) {
            return TYPE_REMOVE;
        } else if (type.equalsIgnoreCase("pass_fail")) {
            return TYPE_PASS_FAIL;
        } else if (type.equalsIgnoreCase("switch_layout")) {
            return TYPE_SWITCH;
        } else if (type.equalsIgnoreCase("number")) {
            return TYPE_NUMBER;
        } else if (type.equalsIgnoreCase("et_short_text")) {
            return TYPE_ET_SHORT_TEXT;
        } else if (type.equalsIgnoreCase("dropdown_number")) {
            return TYPE_DROPDOWN_NUMBER;
        } else if (type.equalsIgnoreCase("task_site_clear")) {
            return TYPE_TASK_SITE_CLEAR;
        } else if (type.equalsIgnoreCase("location")) {
            return TYPE_LOCATION;
        } else if (type.equalsIgnoreCase("store-item")) {
            return TYPE_STORE_ITEM;
        } else if (type.equalsIgnoreCase("card_fork")) {
            return TYPE_FORK_CARD;
        } else if (type.equalsIgnoreCase("dialog_screen")) {
            return TYPE_DIALOG_SCREEN;
        } else if (type.equalsIgnoreCase("log_dfe_item")) {
            return TYPE_DFE_ITEM;
        } else if (type.equalsIgnoreCase("calender")) {
            return TYPE_CALENDER;
        } else if (type.equalsIgnoreCase("yes_no_na")) {
            return TYPE_YES_NO_NA;
        } else if (type.equalsIgnoreCase("yes_no_na_optional")) {
            return TYPE_YES_NO_NA_OPTIONAL;
        } else if (type.equalsIgnoreCase("stopWatch")) {
            return TYPE_STOP_WATCH;
        } else if (type.equalsIgnoreCase("visitor_signature")) {
            return TYPE_VISITOR_SIGN;
        } else if (type.equalsIgnoreCase("date_time")) {
            return TYPE_DATE_TIME;
        } else if (type.equalsIgnoreCase("txt_description")) {
            return TYPE_TXT_DESC;
        } else if (type.equalsIgnoreCase("risk_element")) {
            return TYPE_RISK_ELEMENT;
        } else if (type.equalsIgnoreCase("take_photo")) {
            return TYPE_TAKE_PHOTO;
        } else if (type.equalsIgnoreCase("current_store")) {
            return TYPE_CURRENT_STORE;
        } else if (type.equalsIgnoreCase("add_store_item")) {
            return TYPE_ADD_STORE_ITEM;
        } else if (type.equalsIgnoreCase("stock_item")) {
            return TYPE_STOCK_ITEM;
        } else if (type.equalsIgnoreCase("bar_code")) {
            return TYPE_BAR_CODE;
        } else if (type.equalsIgnoreCase("add_pos_dfe")) {
            return TYPE_ADD_POS_DFE;
        } else if (type.equalsIgnoreCase("add_neg_dfe")) {
            return TYPE_ADD_NEG_DFE;
        } else if (type.equalsIgnoreCase("add_log_measure")) {
            return TYPE_ADD_LOG_MEASURE;
        } else if (type.equalsIgnoreCase("log_measure_item")) {
            return TYPE_LOG_MEASURE;
        } else if (type.equalsIgnoreCase("item_log_measure")) {
            return TYPE_LOG_MEASURE;
        } else if (type.equalsIgnoreCase("add_dig_measure")) {
            return TYPE_ADD_DIG_MEASURE;
        } else if (type.equalsIgnoreCase("item_dig_measure")) {
            return TYPE_LIST_DIG_MEASURE;
        } else if (type.equalsIgnoreCase("task_list_backfill")) {
            return TYPE_TASK_LOG_BACKFILL;
        } else if (type.equalsIgnoreCase("task_list_backfill_item")) {
            return TYPE_TASK_LOG_BACKFILL_ITEM;
        } else if (type.equalsIgnoreCase("task_list_reinstatement")) {
            return TYPE_TASK_LOG_REINSTATEMENT;
        } else if (type.equalsIgnoreCase("task_list_reinstatement_item")) {
            return TYPE_LOG_REINSTATEMENT_ITEM;
        } else if (type.equalsIgnoreCase("task_view_dig_measures")) {
            return TYPE_TASK_VIEW_DIG_MEASURES;
        } else if (type.equalsIgnoreCase("task_view_dig_measures_item")) {
            return TYPE_TASK_VIEW_DIG_MEASUERS_ITEM;
        } else if (type.equalsIgnoreCase("task_view_backfill_measures")) {
            return TYPE_TASK_VIEW_BACKFILL_MEASURES;
        } else if (type.equalsIgnoreCase("task_view_backfill_measures_item")) {
            return TYPE_TASK_VIEW_BACKFILL_MEASUERS_ITEM;
        } else if (type.equalsIgnoreCase("task_view_reinstatement_measures")) {
            return TYPE_TASK_VIEW_REINST_MEASURES;
        } else if (type.equalsIgnoreCase("task_view_reinstatement_measures_item")) {
            return TYPE_TASK_VIEW_REINST_MEASUERS_ITEM;
        } else if (type.equalsIgnoreCase("task_list_muckaway")) {
            return TYPE_TASK_LOG_MUCKAWAY;
        } else if (type.equalsIgnoreCase("task_list_muckaway_item")) {
            return TYPE_TASK_LOG_MUCKAWAY_ITEM;
        } else if (type.equalsIgnoreCase("task_list_service_material")) {
            return TYPE_TASK_LOG_SERVICE;
        } else if (type.equalsIgnoreCase("task_list_service_material_item")) {
            return TYPE_TASK_LOG_SERVICE_ITEM;
        } else if (type.equalsIgnoreCase("task_list_site_clear")) {
            return TYPE_TASK_LOG_SERVICE;
        } else if (type.equalsIgnoreCase("task_list_site_clear_item")) {
            return TYPE_TASK_LOG_SERVICE_ITEM;
        } else if (type.equalsIgnoreCase("yes_no_tooltip")) {
            return TYPE_YES_NO_tooltip;
        } else if (type.equalsIgnoreCase("yes_no_na_tooltip")) {
            return TYPE_YES_NO_NA_tooltip;
        } else if (type.equalsIgnoreCase("log_measure_item")) {
            return TYPE_LOG_MEASURE;
        } else if (type.equalsIgnoreCase("log_briefing_sign")) {
            return TYPE_SIGN_BRIEFING;
        } else if (type.equalsIgnoreCase("log_list_briefing")) {
            return TYPE_LIST_BREIFDOC;
        } else if (type.equalsIgnoreCase("tv_briefing_text")) {
            return TYPE_TV_BRIEFING_TEXT;
        } else if (type.equalsIgnoreCase("et_search_text")) {
            return TYPE_ET_SEARCH_ESTIMATE;
        } else if (type.equalsIgnoreCase("rfna_toggle")) {
            return TYPE_RFNA_TOGGLE;
        } else if (type.equalsIgnoreCase("add_log_hours")) {
            return TYPE_ADD_LOG_HOURS;
        }else if (type.equalsIgnoreCase("log_hours_item")) {
            return TYPE_ITEM_LOG_HOURS;
        }else if (type.equalsIgnoreCase("time_picker")) {
            return TYPE_ITEM_TIME_PICKER;
        }else if (type.equalsIgnoreCase("week_commencing")) {
            return TYPE_ITEM_WEEK_COMMENCING;
        }else if (type.equalsIgnoreCase("task_list_timesheet")) {
            return TYPE_TASK_LIST_TIMESHEET;
        }else if (type.equalsIgnoreCase("task_list_timesheet_item")) {
            return TYPE_TASK_TIMESHEET_ITEM;
        }else if (type.equalsIgnoreCase("total_worked_hours")) {
            return TYPE_TOTAL_WORKED_HOURS;
        }else if (type.equalsIgnoreCase("et_time_sheet_hours")) {
            return TYPE_ET_TIME_SHEET_HOURS;
        }else if (type.equalsIgnoreCase("open_log_hours")) {
            return TYPE_OPEN_LOG_HOURS;
        }else if (type.equalsIgnoreCase("day_view")) {
            return TYPE_ITEM_DAY;
        }
//        else if (type.equalsIgnoreCase("day_log_hours_item")) {
//            return TYPE_ITEM_DAY_LOG_HOURS;
//        }


        return TYPE_TXT_BOLD_HEAD;
    }

    public ArrayList<FormItem> getFalseEnables() {
        return falseEnables;
    }


    public ArrayList<FormItem> getNaEnables() {
        return naEnables;
    }

    public String getSignatureUrl() {
        return signatureUrl;
    }

    public int getPhotoRequired() {
        return photoRequired;
    }

    public void setPhotoRequired(int photoRequired) {
        this.photoRequired = photoRequired;
        if (photos != null) {
            for (int i = 0; i < photoRequired && i < photos.size(); i++) {
                photos.get(i).setOptional(false);
            }
        }
    }

    public int getPhotoSize() {
        return photoSize;
    }

    public void setPhotoSize(int photoSize) {
        this.photoSize = photoSize;
    }

    public String getPhotoId() {
        return photoId;
    }

    public void setPhotoId(String photoId) {
        this.photoId = photoId;
        if (photos != null) {
            for (Photo photo : photos) {
                photo.setPhoto_id(photoId);
            }
        }
    }

    public void setSignatureUrl(String signatureUrl) {
        this.signatureUrl = signatureUrl;
    }

    public String getAssetType() {
        return assetType;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getStopWork() {
        return stopWork;
    }

    public int getMyStoreQuantity() {
        return myStoreQuantity;
    }

    public void setMyStoreQuantity(int myStoreQuantity) {
        this.myStoreQuantity = myStoreQuantity;
    }

    public int getAssetNumber() {
        if (TextUtils.isEmpty(assetType)) {
            return -1;
        }

        if (assetType.equalsIgnoreCase("pole")) {
            return 0;
        } else if (assetType.equalsIgnoreCase("blockTerminal")) {
            return 1;
        } else if (assetType.equalsIgnoreCase("anchor")) {
            return 2;
        } else if (assetType.equalsIgnoreCase("jointClosure")) {
            return 3;
        } else if (assetType.equalsIgnoreCase("aerialCable")) {
            return 4;
        } else if (assetType.equalsIgnoreCase("ugCable")) {
            return 5;
        } else if (assetType.equalsIgnoreCase("dropWire")) {
            return 6;
        } else if (assetType.equalsIgnoreCase("surfaceTypes")) {
            return 7;
        } else if (assetType.equalsIgnoreCase("materialTypes")) {
            return 8;
        } else if (assetType.equalsIgnoreCase("dacs")) {
            return 9;
        }
        return -1;

    }

    public boolean isMultiSelection() {
        return isMultiSelection;
    }

    public String getTaskType(int formType) {
        switch (formType) {
            case TYPE_TASK_LOG_BACKFILL:
            case TYPE_TASK_LOG_REINSTATEMENT:
            case TYPE_TASK_LOG_MUCKAWAY:
            case TYPE_TASK_LOG_SERVICE:
            case TYPE_TASK_SITE_CLEAR:
            case TYPE_TASK_LIST_TIMESHEET:
                return type + "_item";
            default:
                return null;
        }
    }


}