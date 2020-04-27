package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;
import co.uk.depotnet.onsa.listeners.DropDownItem;

public class Job implements Parcelable, DropDownItem {
    public static final Creator<Job> CREATOR = new Creator<Job>() {
        @Override
        public Job createFromParcel(Parcel in) {
            return new Job(in);
        }

        @Override
        public Job[] newArray(int size) {
            return new Job[size];
        }
    };
    private ArrayList<Note> notes;
    private ArrayList<Document> documents;
    private String estimateNumber;
    private ArrayList<JobWorkItem> workItems;
    private String workTitle;
    private String shortAddress;
    private String latitude;
    private String contract;
    private String requiredByDate;
    private String locationAddress;
    private String priority;
    private ArrayList<Notice> notices;
    private String jobId;
    private String jobCatagory;
    private String specialInstructions;
    private String exchange;
    private String postCode;
    private String activityType;
    private String jobNumber;
    private String status;
    private String longitude;
    private String scheduledStartDate;
    private String scheduledEndDate;
    private String icon;
    private boolean isHotJob;
    private boolean isSiteClear;
    private ArrayList<A75Groups> a75Groups;
    private boolean hasRFNA;

    protected Job(Parcel in) {
        notes = in.createTypedArrayList(Note.CREATOR);
        documents = in.createTypedArrayList(Document.CREATOR);
        estimateNumber = in.readString();
        workItems = in.createTypedArrayList(JobWorkItem.CREATOR);
        workTitle = in.readString();
        shortAddress = in.readString();
        latitude = in.readString();
        contract = in.readString();
        requiredByDate = in.readString();
        locationAddress = in.readString();
        priority = in.readString();
        notices = in.createTypedArrayList(Notice.CREATOR);
        jobId = in.readString();
        jobCatagory = in.readString();
        specialInstructions = in.readString();
        exchange = in.readString();
        postCode = in.readString();
        activityType = in.readString();
        jobNumber = in.readString();
        status = in.readString();
        longitude = in.readString();
        scheduledStartDate = in.readString();
        scheduledEndDate = in.readString();
        icon = in.readString();
        isHotJob = in.readInt()==1;
        isSiteClear = in.readInt()==1;
        a75Groups = in.createTypedArrayList(A75Groups.CREATOR);
        hasRFNA = in.readInt()==1;
    }

    public Job() {

    }

    public Job(Cursor cursor) {
        jobId = cursor.getString(cursor.getColumnIndex(DBTable.jobId));
        notes = DBHandler.getInstance().getNotes(jobId);
        documents = DBHandler.getInstance().getDocument(jobId);
        estimateNumber = cursor.getString(cursor.getColumnIndex(DBTable.estimateNumber));
        workItems = DBHandler.getInstance().getJobWorkItem(jobId);
        workTitle = cursor.getString(cursor.getColumnIndex(DBTable.workTitle));
        shortAddress = cursor.getString(cursor.getColumnIndex(DBTable.shortAddress));
        latitude = cursor.getString(cursor.getColumnIndex(DBTable.latitude));
        contract = cursor.getString(cursor.getColumnIndex(DBTable.contract));
        requiredByDate = cursor.getString(cursor.getColumnIndex(DBTable.requiredByDate));
        locationAddress = cursor.getString(cursor.getColumnIndex(DBTable.locationAddress));
        priority = cursor.getString(cursor.getColumnIndex(DBTable.priority));
        notices = DBHandler.getInstance().getNotices(jobId);

        jobCatagory = cursor.getString(cursor.getColumnIndex(DBTable.jobCatagory));
        specialInstructions = cursor.getString(cursor.getColumnIndex(DBTable.specialInstructions));
        exchange = cursor.getString(cursor.getColumnIndex(DBTable.exchange));
        postCode = cursor.getString(cursor.getColumnIndex(DBTable.postCode));
        activityType = cursor.getString(cursor.getColumnIndex(DBTable.activityType));
        jobNumber = cursor.getString(cursor.getColumnIndex(DBTable.jobNumber));
        status = cursor.getString(cursor.getColumnIndex(DBTable.status));
        longitude = cursor.getString(cursor.getColumnIndex(DBTable.longitude));
        scheduledStartDate = cursor.getString(cursor.getColumnIndex(DBTable.scheduledStartDate));
        scheduledEndDate = cursor.getString(cursor.getColumnIndex(DBTable.scheduledEndDate));
        icon = cursor.getString(cursor.getColumnIndex(DBTable.icon));
        isHotJob = cursor.getInt(cursor.getColumnIndex(DBTable.isHotJob)) == 1;
        isSiteClear = cursor.getInt(cursor.getColumnIndex(DBTable.isSiteClear)) == 1;
        a75Groups = DBHandler.getInstance().getA75Groups(jobId);
        hasRFNA = cursor.getInt(cursor.getColumnIndex(DBTable.hasRFNA)) == 1;
    }

    public void setnotes(ArrayList<Note> notes) {
        this.notes = notes;
    }

    public ArrayList<Note> getnotes() {
        return this.notes;
    }

    public void setdocuments(ArrayList<Document> documents) {
        this.documents = documents;
    }

    public ArrayList<Document> getdocuments() {
        return this.documents;
    }

    public void setestimateNumber(String estimateNumber) {
        this.estimateNumber = estimateNumber;
    }

    public String getestimateNumber() {
        return this.estimateNumber;
    }

    public void setworkItems(ArrayList<JobWorkItem> workItems) {
        this.workItems = workItems;
    }

    public ArrayList<JobWorkItem> getworkItems() {
        return this.workItems;
    }

    public void setworkTitle(String workTitle) {
        this.workTitle = workTitle;
    }

    public String getworkTitle() {
        return this.workTitle;
    }

    public void setshortAddress(String shortAddress) {
        this.shortAddress = shortAddress;
    }

    public String getshortAddress() {
        return this.shortAddress;
    }

    public void setlatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getlatitude() {
        return this.latitude;
    }

    public void setcontract(String contract) {
        this.contract = contract;
    }

    public String getcontract() {
        return this.contract;
    }

    public void setrequiredByDate(String requiredByDate) {
        this.requiredByDate = requiredByDate;
    }

    public String getrequiredByDate() {
        return this.requiredByDate;
    }

    public void setlocationAddress(String locationAddress) {
        this.locationAddress = locationAddress;
    }

    public String getlocationAddress() {
        return this.locationAddress;
    }

    public void setpriority(String priority) {
        this.priority = priority;
    }

    public String getpriority() {
        return this.priority;
    }

    public void setnotices(ArrayList<Notice> notices) {
        this.notices = notices;
    }

    public ArrayList<Notice> getnotices() {
        return this.notices;
    }

    public void setjobId(String jobId) {
        this.jobId = jobId;
    }

    public String getjobId() {
        return this.jobId;
    }

    public void setjobCatagory(String jobCatagory) {
        this.jobCatagory = jobCatagory;
    }

    public String getjobCatagory() {
        return this.jobCatagory;
    }

    public void setspecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public String getspecialInstructions() {
        return this.specialInstructions;
    }

    public void setexchange(String exchange) {
        this.exchange = exchange;
    }

    public String getexchange() {
        return this.exchange;
    }

    public void setpostCode(String postCode) {
        this.postCode = postCode;
    }

    public String getpostCode() {
        return this.postCode;
    }

    public void setactivityType(String activityType) {
        this.activityType = activityType;
    }

    public String getactivityType() {
        return this.activityType;
    }

    public void setjobNumber(String jobNumber) {
        this.jobNumber = jobNumber;
    }

    public String getjobNumber() {
        return this.jobNumber;
    }

    public void setstatus(String status) {
        this.status = status;
    }

    public String getstatus() {
        return this.status;
    }

    public void setlongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getlongitude() {
        return this.longitude;
    }

    public String getScheduledStartDate() {
        return scheduledStartDate;
    }

    public void setScheduledStartDate(String scheduledStartDate) {
        this.scheduledStartDate = scheduledStartDate;
    }

    public String getScheduledEndDate() {
        return scheduledEndDate;
    }

    public void setScheduledEndDate(String scheduledEndDate) {
        this.scheduledEndDate = scheduledEndDate;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public boolean isHotJob() {
        return isHotJob;
    }

    public boolean isSiteClear() {
        return isSiteClear;
    }

    public ArrayList<A75Groups> getA75Groups() {
        return a75Groups;
    }

    public boolean hasRFNA() {
        return hasRFNA;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(notes);
        dest.writeTypedList(documents);
        dest.writeString(estimateNumber);
        dest.writeTypedList(workItems);
        dest.writeString(workTitle);
        dest.writeString(shortAddress);
        dest.writeString(latitude);
        dest.writeString(contract);
        dest.writeString(requiredByDate);
        dest.writeString(locationAddress);
        dest.writeString(priority);
        dest.writeTypedList(notices);
        dest.writeString(jobId);
        dest.writeString(jobCatagory);
        dest.writeString(specialInstructions);
        dest.writeString(exchange);
        dest.writeString(postCode);
        dest.writeString(activityType);
        dest.writeString(jobNumber);
        dest.writeString(status);
        dest.writeString(longitude);
        dest.writeString(scheduledStartDate);
        dest.writeString(scheduledEndDate);
        dest.writeString(icon);
        dest.writeInt(isHotJob ? 1 : 0);
        dest.writeInt(isSiteClear ? 1 : 0);
        dest.writeTypedList(a75Groups);
        dest.writeInt(hasRFNA ? 1: 0);
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();
        DBHandler dbHandler = DBHandler.getInstance();
        if (notes != null && !notes.isEmpty()) {
            for (Note n :
                    notes) {
                n.setJobId(jobId);
                dbHandler.replaceData(Note.DBTable.NAME, n.toContentValues());
            }
        }

        if (documents != null && !documents.isEmpty()) {
            for (Document d :
                    documents) {
                d.setJobId(jobId);
                dbHandler.replaceData(Document.DBTable.NAME, d.toContentValues());
            }
        }


        if (workItems != null && !workItems.isEmpty()) {
            for (JobWorkItem w :
                    workItems) {
                w.setJobId(jobId);
                dbHandler.replaceData(JobWorkItem.DBTable.NAME, w.toContentValues());
            }
        }

        if (notices != null && !notices.isEmpty()) {
            for (Notice n : notices) {
                n.setJobId(jobId);
                dbHandler.replaceData(Notice.DBTable.NAME, n.toContentValues());
            }
        }


        cv.put(DBTable.estimateNumber, this.estimateNumber);


        cv.put(DBTable.workTitle, this.workTitle);

        cv.put(DBTable.shortAddress, this.shortAddress);

        cv.put(DBTable.latitude, this.latitude);

        cv.put(DBTable.contract, this.contract);

        cv.put(DBTable.requiredByDate, this.requiredByDate);

        cv.put(DBTable.locationAddress, this.locationAddress);

        cv.put(DBTable.priority, this.priority);

        cv.put(DBTable.jobId, this.jobId);

        cv.put(DBTable.jobCatagory, this.jobCatagory);

        cv.put(DBTable.specialInstructions, this.specialInstructions);

        cv.put(DBTable.exchange, this.exchange);

        cv.put(DBTable.postCode, this.postCode);

        cv.put(DBTable.activityType, this.activityType);

        cv.put(DBTable.jobNumber, this.jobNumber);

        cv.put(DBTable.status, this.status);

        cv.put(DBTable.longitude, this.longitude);
        cv.put(DBTable.scheduledStartDate, this.scheduledStartDate);
        cv.put(DBTable.scheduledEndDate, this.scheduledEndDate);
        cv.put(DBTable.icon, this.icon);
        cv.put(DBTable.isHotJob, this.isHotJob);
        cv.put(DBTable.isSiteClear, this.isSiteClear);
        cv.put(DBTable.hasRFNA, this.hasRFNA);

        if (a75Groups != null && !a75Groups.isEmpty()) {
            for (A75Groups a : a75Groups) {
                a.setJobId(jobId);
                dbHandler.replaceData(A75Groups.DBTable.NAME, a.toContentValues());
            }
        }
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "Job";
        public static final String notes = "notes";
        public static final String documents = "documents";
        public static final String estimateNumber = "estimateNumber";
        public static final String workItems = "workItems";
        public static final String workTitle = "workTitle";
        public static final String shortAddress = "shortAddress";
        public static final String latitude = "latitude";
        public static final String contract = "contract";
        public static final String requiredByDate = "requiredByDate";
        public static final String locationAddress = "locationAddress";
        public static final String priority = "priority";
        public static final String notices = "notices";
        public static final String jobId = "jobId";
        public static final String jobCatagory = "jobCatagory";
        public static final String specialInstructions = "specialInstructions";
        public static final String exchange = "exchange";
        public static final String postCode = "postCode";
        public static final String activityType = "activityType";
        public static final String jobNumber = "jobNumber";
        public static final String status = "status";
        public static final String longitude = "longitude";
        public static final String scheduledStartDate = "scheduledStartDate";
        public static final String scheduledEndDate = "scheduledEndDate";
        public static final String icon = "icon";
        public static final String isHotJob = "isHotJob";
        public static final String isSiteClear = "isSiteClear";
        public static final String a75Groups = "a75Groups";
        public static final String hasRFNA = "hasRFNA";

    }

    @Override
    public String getUploadValue() {
        return jobId;
    }

    @Override
    public String getDisplayItem() {
        return estimateNumber;
    }


}