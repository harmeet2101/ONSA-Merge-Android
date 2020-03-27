package co.uk.depotnet.onsa.modals.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

import co.uk.depotnet.onsa.database.DBHandler;

public class Receipts implements Parcelable {
    public static final Creator<Receipts> CREATOR = new Creator<Receipts>() {
        @Override
        public Receipts createFromParcel(Parcel in) {
            return new Receipts(in);
        }

        @Override
        public Receipts[] newArray(int size) {
            return new Receipts[size];

        }

    };

    private String staName;
    private String goodsOutStatusName;
    private String sentByName;
    private String goodsOutStatusId;
    private String sentToName;
    private String batchRef;
    private String dateSent;
    private String userId;
    private ArrayList<ReceiptItems> items;
    private int age;
    private int itemCount;

    protected Receipts(Parcel in) {
        staName = in.readString();
        goodsOutStatusName = in.readString();
        sentByName = in.readString();
        goodsOutStatusId = in.readString();
        sentToName = in.readString();
        batchRef = in.readString();
        dateSent = in.readString();
        userId = in.readString();
        items = in.createTypedArrayList(ReceiptItems.CREATOR);
        age = in.readInt();
        itemCount = in.readInt();
    }

    public Receipts(Cursor cursor) {
        staName = cursor.getString(cursor.getColumnIndex(DBTable.staName));
        goodsOutStatusName = cursor.getString(cursor.getColumnIndex(DBTable.goodsOutStatusName));
        sentByName = cursor.getString(cursor.getColumnIndex(DBTable.sentByName));
        goodsOutStatusId = cursor.getString(cursor.getColumnIndex(DBTable.goodsOutStatusId));
        sentToName = cursor.getString(cursor.getColumnIndex(DBTable.sentToName));
        batchRef = cursor.getString(cursor.getColumnIndex(DBTable.batchRef));
        dateSent = cursor.getString(cursor.getColumnIndex(DBTable.dateSent));
        userId = cursor.getString(cursor.getColumnIndex(DBTable.userId));
        items = DBHandler.getInstance().getReceiptItems(batchRef);
        age = cursor.getInt(cursor.getColumnIndex(DBTable.age));
        itemCount = cursor.getInt(cursor.getColumnIndex(DBTable.itemCount));
    }

    public void setstaName(String staName) {
        this.staName = staName;
    }

    public String getstaName() {
        return this.staName;
    }

    public void setgoodsOutStatusName(String goodsOutStatusName) {
        this.goodsOutStatusName = goodsOutStatusName;
    }

    public String getgoodsOutStatusName() {
        return this.goodsOutStatusName;
    }

    public void setsentByName(String sentByName) {
        this.sentByName = sentByName;
    }

    public String getsentByName() {
        return this.sentByName;
    }

    public void setgoodsOutStatusId(String goodsOutStatusId) {
        this.goodsOutStatusId = goodsOutStatusId;
    }

    public String getgoodsOutStatusId() {
        return this.goodsOutStatusId;
    }

    public void setsentToName(String sentToName) {
        this.sentToName = sentToName;
    }

    public String getsentToName() {
        return this.sentToName;
    }

    public void setbatchRef(String batchRef) {
        this.batchRef = batchRef;
    }

    public String getbatchRef() {
        return this.batchRef;
    }

    public void setdateSent(String dateSent) {
        this.dateSent = dateSent;
    }

    public String getdateSent() {
        return this.dateSent;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getuserId() {
        return this.userId;
    }

    public ArrayList<ReceiptItems> getItems() {
        return items;
    }

    public void setItems(ArrayList<ReceiptItems> items) {
        this.items = items;
    }

    public void setage(int age) {
        this.age = age;
    }

    public int getage() {
        return this.age;
    }

    public void setitemCount(int itemCount) {
        this.itemCount = itemCount;
    }

    public int getitemCount() {
        return this.itemCount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(staName);
        parcel.writeString(goodsOutStatusName);
        parcel.writeString(sentByName);
        parcel.writeString(goodsOutStatusId);
        parcel.writeString(sentToName);
        parcel.writeString(batchRef);
        parcel.writeString(dateSent);
        parcel.writeString(userId);
        parcel.writeTypedList(items);
        parcel.writeInt(age);
        parcel.writeInt(itemCount);

    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.staName, this.staName);

        cv.put(DBTable.goodsOutStatusName, this.goodsOutStatusName);

        cv.put(DBTable.sentByName, this.sentByName);

        cv.put(DBTable.goodsOutStatusId, this.goodsOutStatusId);

        cv.put(DBTable.sentToName, this.sentToName);

        cv.put(DBTable.batchRef, this.batchRef);

        cv.put(DBTable.dateSent, this.dateSent);

        cv.put(DBTable.userId, this.userId);

        if (items != null && !items.isEmpty()) {
            for (ReceiptItems ri : items) {
                ri.setBatchRef(this.batchRef);
                DBHandler.getInstance().insertData(ReceiptItems.DBTable.NAME, ri.toContentValues());
            }
        }

        cv.put(DBTable.age, this.age);

        cv.put(DBTable.itemCount, this.itemCount);

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "Receipts";
        public static final String staName = "staName";
        public static final String goodsOutStatusName = "goodsOutStatusName";
        public static final String sentByName = "sentByName";
        public static final String goodsOutStatusId = "goodsOutStatusId";
        public static final String sentToName = "sentToName";
        public static final String batchRef = "batchRef";
        public static final String dateSent = "dateSent";
        public static final String userId = "userId";
        public static final String items = "items";
        public static final String age = "age";
        public static final String itemCount = "itemCount";
    }
}