package co.uk.depotnet.onsa.modals.store;//TODO: package name

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class MyRequest implements Parcelable {
    public static final Creator<MyRequest> CREATOR = new Creator<MyRequest>() {
        @Override
        public MyRequest createFromParcel(Parcel in) {
            return new MyRequest(in);
        }

        @Override
        public MyRequest[] newArray(int size) {
            return new MyRequest[size];

        }

    };
    private String createdByUserId;
    private String rejectionComments;
    private String requestComments;
    private String approvalComments;
    private String userId;
    private String rejectedDate;
    private String approvedByUserId;
    private int itemCount;
    private String approvedDate;
    private String staId;
    private String createdDate;
    private String requestStatusName;
    private String requestId;
    private String requestStatusId;
    private String requestedByName;
    private String approvedByName;
    private String warehouseStaName;
    private String rejectedByName;
    private String rejectedByUserId;
    private int age;
    private ArrayList<RequestItem> items;

    protected MyRequest(Parcel in) {
        createdByUserId = in.readString();
        rejectionComments = in.readString();
        requestComments = in.readString();
        approvalComments = in.readString();
        userId = in.readString();
        rejectedDate = in.readString();
        approvedByUserId = in.readString();
        itemCount = in.readInt();
        approvedDate = in.readString();
        staId = in.readString();
        createdDate = in.readString();
        requestStatusName = in.readString();
        requestId = in.readString();
        requestStatusId = in.readString();
        requestedByName = in.readString();
        approvedByName = in.readString();
        warehouseStaName = in.readString();
        rejectedByName = in.readString();
        rejectedByUserId = in.readString();
        age = in.readInt();
        items = in.createTypedArrayList(RequestItem.CREATOR);


    }

    public MyRequest(Cursor cursor) {
        createdByUserId = cursor.getString(cursor.getColumnIndex(DBTable.createdByUserId));
        rejectionComments = cursor.getString(cursor.getColumnIndex(DBTable.rejectionComments));
        requestComments = cursor.getString(cursor.getColumnIndex(DBTable.requestComments));
        approvalComments = cursor.getString(cursor.getColumnIndex(DBTable.approvalComments));
        userId = cursor.getString(cursor.getColumnIndex(DBTable.userId));
        rejectedDate = cursor.getString(cursor.getColumnIndex(DBTable.rejectedDate));
        approvedByUserId = cursor.getString(cursor.getColumnIndex(DBTable.approvedByUserId));
        itemCount = cursor.getInt(cursor.getColumnIndex(DBTable.itemCount));
        approvedDate = cursor.getString(cursor.getColumnIndex(DBTable.approvedDate));
        staId = cursor.getString(cursor.getColumnIndex(DBTable.staId));
        createdDate = cursor.getString(cursor.getColumnIndex(DBTable.createdDate));
        requestStatusName = cursor.getString(cursor.getColumnIndex(DBTable.requestStatusName));
        requestId = cursor.getString(cursor.getColumnIndex(DBTable.requestId));
        requestStatusId = cursor.getString(cursor.getColumnIndex(DBTable.requestStatusId));
        requestedByName = cursor.getString(cursor.getColumnIndex(DBTable.requestedByName));
        approvedByName = cursor.getString(cursor.getColumnIndex(DBTable.approvedByName));
        warehouseStaName = cursor.getString(cursor.getColumnIndex(DBTable.warehouseStaName));
        rejectedByName = cursor.getString(cursor.getColumnIndex(DBTable.rejectedByName));
        rejectedByUserId = cursor.getString(cursor.getColumnIndex(DBTable.rejectedByUserId));
        age = cursor.getInt(cursor.getColumnIndex(DBTable.age));

        items = DBHandler.getInstance().getRequestItems(requestId);


    }

    public void setcreatedByUserId(String createdByUserId) {
        this.createdByUserId = createdByUserId;
    }

    public String getcreatedByUserId() {
        return this.createdByUserId;
    }

    public void setrejectionComments(String rejectionComments) {
        this.rejectionComments = rejectionComments;
    }

    public String getrejectionComments() {
        return this.rejectionComments;
    }

    public void setrequestComments(String requestComments) {
        this.requestComments = requestComments;
    }

    public String getrequestComments() {
        return this.requestComments;
    }

    public void setapprovalComments(String approvalComments) {
        this.approvalComments = approvalComments;
    }

    public String getapprovalComments() {
        return this.approvalComments;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getuserId() {
        return this.userId;
    }

    public void setrejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getrejectedDate() {
        return this.rejectedDate;
    }

    public void setapprovedByUserId(String approvedByUserId) {
        this.approvedByUserId = approvedByUserId;
    }

    public String getapprovedByUserId() {
        return this.approvedByUserId;
    }

    public void setitemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getitemCount() {
        return this.itemCount;
    }

    public void setapprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getapprovedDate() {
        return this.approvedDate;
    }

    public void setstaId(String staId) {
        this.staId = staId;
    }

    public String getstaId() {
        return this.staId;
    }

    public void setcreatedDate(String createdDate) {
        this.createdDate = createdDate;
    }

    public String getcreatedDate() {
        return this.createdDate;
    }

    public void setrequestStatusName(String requestStatusName) {
        this.requestStatusName = requestStatusName;
    }

    public String getrequestStatusName() {
        return this.requestStatusName;
    }

    public void setrequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getrequestId() {
        return this.requestId;
    }

    public void setrequestStatusId(String requestStatusId) {
        this.requestStatusId = requestStatusId;
    }

    public String getrequestStatusId() {
        return this.requestStatusId;
    }

    public void setrequestedByName(String requestedByName) {
        this.requestedByName = requestedByName;
    }

    public String getrequestedByName() {
        return this.requestedByName;
    }

    public void setapprovedByName(String approvedByName) {
        this.approvedByName = approvedByName;
    }

    public String getapprovedByName() {
        return this.approvedByName;
    }

    public void setwarehouseStaName(String warehouseStaName) {
        this.warehouseStaName = warehouseStaName;
    }

    public String getwarehouseStaName() {
        return this.warehouseStaName;
    }

    public void setrejectedByName(String rejectedByName) {
        this.rejectedByName = rejectedByName;
    }

    public String getrejectedByName() {
        return this.rejectedByName;
    }

    public void setrejectedByUserId(String rejectedByUserId) {
        this.rejectedByUserId = rejectedByUserId;
    }

    public String getrejectedByUserId() {
        return this.rejectedByUserId;
    }

    public void setage(int age) {
        this.age = age;
    }

    public int getage() {
        return this.age;
    }

    public ArrayList<RequestItem> getItems() {
        return items;
    }

    public void setItems(ArrayList<RequestItem> items) {
        this.items = items;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(createdByUserId);
        parcel.writeString(rejectionComments);
        parcel.writeString(requestComments);
        parcel.writeString(approvalComments);
        parcel.writeString(userId);
        parcel.writeString(rejectedDate);
        parcel.writeString(approvedByUserId);
        parcel.writeInt(itemCount);
        parcel.writeString(approvedDate);
        parcel.writeString(staId);
        parcel.writeString(createdDate);
        parcel.writeString(requestStatusName);
        parcel.writeString(requestId);
        parcel.writeString(requestStatusId);
        parcel.writeString(requestedByName);
        parcel.writeString(approvedByName);
        parcel.writeString(warehouseStaName);
        parcel.writeString(rejectedByName);
        parcel.writeString(rejectedByUserId);
        parcel.writeInt(age);
        parcel.writeTypedList(items);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.createdByUserId, this.createdByUserId);

        cv.put(DBTable.rejectionComments, this.rejectionComments);

        cv.put(DBTable.requestComments, this.requestComments);

        cv.put(DBTable.approvalComments, this.approvalComments);

        cv.put(DBTable.userId, this.userId);

        cv.put(DBTable.rejectedDate, this.rejectedDate);

        cv.put(DBTable.approvedByUserId, this.approvedByUserId);

        cv.put(DBTable.itemCount, this.itemCount);

        cv.put(DBTable.approvedDate, this.approvedDate);

        cv.put(DBTable.staId, this.staId);

        cv.put(DBTable.createdDate, this.createdDate);

        cv.put(DBTable.requestStatusName, this.requestStatusName);

        cv.put(DBTable.requestId, this.requestId);

        cv.put(DBTable.requestStatusId, this.requestStatusId);

        cv.put(DBTable.requestedByName, this.requestedByName);

        cv.put(DBTable.approvedByName, this.approvedByName);

        cv.put(DBTable.warehouseStaName, this.warehouseStaName);

        cv.put(DBTable.rejectedByName, this.rejectedByName);

        cv.put(DBTable.rejectedByUserId, this.rejectedByUserId);

        cv.put(DBTable.age, this.age);

        if (items != null && !items.isEmpty()) {
            for (RequestItem item : items) {
                DBHandler.getInstance().insertData(RequestItem.DBTable.NAME, item.toContentValues());

            }
        }

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "MyRequest";
        public static final String createdByUserId = "createdByUserId";
        public static final String rejectionComments = "rejectionComments";
        public static final String requestComments = "requestComments";
        public static final String approvalComments = "approvalComments";
        public static final String userId = "userId";
        public static final String rejectedDate = "rejectedDate";
        public static final String approvedByUserId = "approvedByUserId";
        public static final String itemCount = "itemCount";
        public static final String approvedDate = "approvedDate";
        public static final String staId = "staId";
        public static final String createdDate = "createdDate";
        public static final String requestStatusName = "requestStatusName";
        public static final String requestId = "requestId";
        public static final String requestStatusId = "requestStatusId";
        public static final String requestedByName = "requestedByName";
        public static final String approvedByName = "approvedByName";
        public static final String warehouseStaName = "warehouseStaName";
        public static final String rejectedByName = "rejectedByName";
        public static final String rejectedByUserId = "rejectedByUserId";
        public static final String age = "age";
        public static final String items = "RequestItem";

    }
}