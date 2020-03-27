package co.uk.depotnet.onsa.modals.store;
import android.content.ContentValues;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class ReceiptItems implements Parcelable{
    private String staId;
    private int unit;
    private int quantity;
    private String goodsOutId;
    private String unitName;
    private String referenceNumber;
    private boolean serialised;
    private String name;
    private String staStockItemId;
    private String altName;
    private String packagingName;
    private String packagingId;
    private String batchRef;

    public void setstaId(String staId){
        this.staId = staId;
    }

    public String getstaId(){
        return this.staId;
    }

    public void setunit(int unit){
        this.unit = unit;
    }

    public int getunit(){
        return this.unit;
    }

    public void setquantity(int quantity){
        this.quantity = quantity;
    }

    public int getquantity(){
        return this.quantity;
    }

    public void setgoodsOutId(String goodsOutId){
        this.goodsOutId = goodsOutId;
    }

    public String getgoodsOutId(){
        return this.goodsOutId;
    }

    public void setunitName(String unitName){
        this.unitName = unitName;
    }

    public String getunitName(){
        return this.unitName;
    }

    public void setreferenceNumber(String referenceNumber){
        this.referenceNumber = referenceNumber;
    }

    public String getreferenceNumber(){
        return this.referenceNumber;
    }

    public void setserialised(boolean serialised){
        this.serialised = serialised;
    }

    public boolean isserialised(){
        return this.serialised;
    }

    public void setname(String name){
        this.name = name;
    }

    public String getname(){
        return this.name;
    }

    public void setstaStockItemId(String staStockItemId){
        this.staStockItemId = staStockItemId;
    }

    public String getstaStockItemId(){
        return this.staStockItemId;
    }

    public void setaltName(String altName){
        this.altName = altName;
    }

    public String getaltName(){
        return this.altName;
    }

    public void setpackagingName(String packagingName){
        this.packagingName = packagingName;
    }

    public String getpackagingName(){
        return this.packagingName;
    }

    public void setpackagingId(String packagingId){
        this.packagingId = packagingId;
    }

    public String getpackagingId(){
        return this.packagingId;
    }

    public String getBatchRef() {
        return batchRef;
    }

    public void setBatchRef(String batchRef) {
        this.batchRef = batchRef;
    }

    protected ReceiptItems(Parcel in) {
        staId = in.readString();
        unit = in.readInt();
        quantity = in.readInt();
        goodsOutId = in.readString();
        unitName = in.readString();
        referenceNumber = in.readString();
        serialised = in.readByte() != 0;
        name = in.readString();
        staStockItemId = in.readString();
        altName = in.readString();
        packagingName = in.readString();
        packagingId = in.readString();
        batchRef = in.readString();

    }
    public static final Creator<ReceiptItems> CREATOR = new Creator<ReceiptItems>() {
        @Override
        public ReceiptItems createFromParcel(Parcel in) {
            return new ReceiptItems(in);
        }

        @Override
        public ReceiptItems[] newArray(int size) {
            return new ReceiptItems[size];

        }

    };

    @Override
    public int describeContents() {
        return 0;
    }

    public static class DBTable{
        public static final String NAME = "ReceiptItems";
        public static final String staId = "staId";
        public static final String unit = "unit";
        public static final String quantity = "quantity";
        public static final String goodsOutId = "goodsOutId";
        public static final String unitName = "unitName";
        public static final String referenceNumber = "referenceNumber";
        public static final String serialised = "serialised";
        public static final String name = "name";
        public static final String staStockItemId = "staStockItemId";
        public static final String altName = "altName";
        public static final String packagingName = "packagingName";
        public static final String packagingId = "packagingId";
        public static final String batchRef = "batchRef";

    }
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(staId);
        parcel.writeInt(unit);
        parcel.writeInt(quantity);
        parcel.writeString(goodsOutId);
        parcel.writeString(unitName);
        parcel.writeString(referenceNumber);
        parcel.writeByte((byte) (serialised ? 1 : 0));
        parcel.writeString(name);
        parcel.writeString(staStockItemId);
        parcel.writeString(altName);
        parcel.writeString(packagingName);
        parcel.writeString(packagingId);
        parcel.writeString(batchRef);

    }
    public ReceiptItems(Cursor cursor) {
        staId = cursor.getString(cursor.getColumnIndex(DBTable.staId));
        unit = cursor.getInt(cursor.getColumnIndex(DBTable.unit));
        quantity = cursor.getInt(cursor.getColumnIndex(DBTable.quantity));
        goodsOutId = cursor.getString(cursor.getColumnIndex(DBTable.goodsOutId));
        unitName = cursor.getString(cursor.getColumnIndex(DBTable.unitName));
        referenceNumber = cursor.getString(cursor.getColumnIndex(DBTable.referenceNumber));
        serialised = cursor.getInt(cursor.getColumnIndex(DBTable.serialised))!=0;
        name = cursor.getString(cursor.getColumnIndex(DBTable.name));
        staStockItemId = cursor.getString(cursor.getColumnIndex(DBTable.staStockItemId));
        altName = cursor.getString(cursor.getColumnIndex(DBTable.altName));
        packagingName = cursor.getString(cursor.getColumnIndex(DBTable.packagingName));
        packagingId = cursor.getString(cursor.getColumnIndex(DBTable.packagingId));
        batchRef = cursor.getString(cursor.getColumnIndex(DBTable.batchRef));

    }
    public ContentValues toContentValues(){

        ContentValues cv = new ContentValues();

        cv.put(DBTable.staId , this.staId);

        cv.put(DBTable.unit , this.unit);

        cv.put(DBTable.quantity , this.quantity);

        cv.put(DBTable.goodsOutId , this.goodsOutId);

        cv.put(DBTable.unitName , this.unitName);

        cv.put(DBTable.referenceNumber , this.referenceNumber);

        cv.put(DBTable.serialised , this.serialised);

        cv.put(DBTable.name , this.name);

        cv.put(DBTable.staStockItemId , this.staStockItemId);

        cv.put(DBTable.altName , this.altName);

        cv.put(DBTable.packagingName , this.packagingName);

        cv.put(DBTable.packagingId , this.packagingId);
        cv.put(DBTable.batchRef , this.batchRef);



        return cv;	}
}