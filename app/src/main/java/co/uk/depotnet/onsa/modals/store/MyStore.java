package co.uk.depotnet.onsa.modals.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class MyStore implements Parcelable {
    public static final Creator<MyStore> CREATOR = new Creator<MyStore>() {
        @Override
        public MyStore createFromParcel(Parcel in) {
            return new MyStore(in);
        }

        @Override
        public MyStore[] newArray(int size) {
            return new MyStore[size];

        }

    };

    private String staStockItemId;
    private String staId;
    private String warehouseStaName;
    private String stockItemId;
    private String altName;
    private String barcode;
    private int mapID;
    private String stockTypeId;
    private String stockTypeName;
    private int unit;
    private String unitName;
    private String description;
    private double quantity;
    private String photoPath;
    private boolean serialised;
    private String tmpid;
    private String userId;
    private String packagingName;
    private String packagingId;
    private String unitId;
    private String batchRef;
    private boolean isFavorite;
    private String reference;

    public String getBatchRef() {
        return batchRef;
    }

    public void setBatchRef(String batchRef) {
        this.batchRef = batchRef;
    }

    protected MyStore(Parcel in) {
        quantity = in.readDouble();
        unitName = in.readString();
        photoPath = in.readString();
        stockItemId = in.readString();
        staStockItemId = in.readString();
        serialised = in.readByte() != 0;
        tmpid = in.readString();
        description = in.readString();
        userId = in.readString();
        packagingName = in.readString();
        packagingId = in.readString();
        staId = in.readString();
        unit = in.readInt();
        stockTypeName = in.readString();
        stockTypeId = in.readString();
        unitId = in.readString();
        batchRef = in.readString();
        mapID = in.readInt();
        warehouseStaName = in.readString();
        altName = in.readString();
        barcode = in.readString();
        isFavorite = in.readByte() != 0;
        reference = in.readString();

    }

    public MyStore(Cursor cursor) {
        quantity = cursor.getDouble(cursor.getColumnIndex(DBTable.quantity));
        unitName = cursor.getString(cursor.getColumnIndex(DBTable.unitName));
        photoPath = cursor.getString(cursor.getColumnIndex(DBTable.photoPath));
        stockItemId = cursor.getString(cursor.getColumnIndex(DBTable.stockItemId));
        staStockItemId = cursor.getString(cursor.getColumnIndex(DBTable.staStockItemId));
        serialised = cursor.getInt(cursor.getColumnIndex(DBTable.serialised)) != 0;
        tmpid = cursor.getString(cursor.getColumnIndex(DBTable.tmpid));
        description = cursor.getString(cursor.getColumnIndex(DBTable.description));
        userId = cursor.getString(cursor.getColumnIndex(DBTable.userId));
        packagingName = cursor.getString(cursor.getColumnIndex(DBTable.packagingName));
        packagingId = cursor.getString(cursor.getColumnIndex(DBTable.packagingId));
        staId = cursor.getString(cursor.getColumnIndex(DBTable.staId));
        unit = cursor.getInt(cursor.getColumnIndex(DBTable.unit));
        stockTypeName = cursor.getString(cursor.getColumnIndex(DBTable.stockTypeName));
        stockTypeId = cursor.getString(cursor.getColumnIndex(DBTable.stockTypeId));
        unitId = cursor.getString(cursor.getColumnIndex(DBTable.unitId));
        batchRef = cursor.getString(cursor.getColumnIndex(DBTable.batchRef));
        mapID = cursor.getInt(cursor.getColumnIndex(DBTable.mapID));
        warehouseStaName = cursor.getString(cursor.getColumnIndex(DBTable.warehouseStaName));
        altName = cursor.getString(cursor.getColumnIndex(DBTable.altName));
        barcode = cursor.getString(cursor.getColumnIndex(DBTable.barcode));
        isFavorite = cursor.getInt(cursor.getColumnIndex(DBTable.isFavorite)) != 0;
        reference = cursor.getString(cursor.getColumnIndex(DBTable.reference));
    }

    public void setquantity(int quantity) {
        this.quantity = quantity;
    }

    public double getquantity() {
        return this.quantity;
    }

    public void setunitName(String unitName) {
        this.unitName = unitName;
    }

    public String getunitName() {
        return this.unitName;
    }

    public void setphotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getphotoPath() {
        return this.photoPath;
    }

    public void setstockItemId(String stockItemId) {
        this.stockItemId = stockItemId;
    }

    public String getstockItemId() {
        return this.stockItemId;
    }

    public void setserialised(boolean serialised) {
        this.serialised = serialised;
    }

    public boolean isserialised() {
        return this.serialised;
    }

    public void settmpid(String tmpid) {
        this.tmpid = tmpid;
    }

    public String gettmpid() {
        return this.tmpid;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public String getdescription() {
        return this.description;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getuserId() {
        return this.userId;
    }

    public void setpackagingName(String packagingName) {
        this.packagingName = packagingName;
    }

    public String getpackagingName() {
        return this.packagingName;
    }

    public void setpackagingId(String packagingId) {
        this.packagingId = packagingId;
    }

    public String getpackagingId() {
        return this.packagingId;
    }

    public void setstaId(String staId) {
        this.staId = staId;
    }

    public String getstaId() {
        return this.staId;
    }

    public void setunit(int unit) {
        this.unit = unit;
    }

    public int getunit() {
        return this.unit;
    }

    public void setstockTypeName(String stockTypeName) {
        this.stockTypeName = stockTypeName;
    }

    public String getstockTypeName() {
        return this.stockTypeName;
    }

    public void setstockTypeId(String stockTypeId) {
        this.stockTypeId = stockTypeId;
    }

    public String getstockTypeId() {
        return this.stockTypeId;
    }

    public void setunitId(String unitId) {
        this.unitId = unitId;
    }

    public String getunitId() {
        return this.unitId;
    }

    public void setmapID(int mapID) {
        this.mapID = mapID;
    }

    public int getmapID() {
        return this.mapID;
    }

    public void setwarehouseStaName(String warehouseStaName) {
        this.warehouseStaName = warehouseStaName;
    }

    public String getwarehouseStaName() {
        return this.warehouseStaName;
    }

    public void setaltName(String altName) {
        this.altName = altName;
    }

    public String getaltName() {
        return this.altName;
    }

    public void setbarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getbarcode() {
        return this.barcode;
    }


    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public String getStaStockItemId() {
        return staStockItemId;
    }

    public void setStaStockItemId(String staStockItemId) {
        this.staStockItemId = staStockItemId;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeDouble(quantity);
        parcel.writeString(unitName);
        parcel.writeString(photoPath);
        parcel.writeString(stockItemId);
        parcel.writeString(staStockItemId);
        parcel.writeByte((byte) (serialised ? 1 : 0));
        parcel.writeString(tmpid);
        parcel.writeString(description);
        parcel.writeString(userId);
        parcel.writeString(packagingName);
        parcel.writeString(packagingId);
        parcel.writeString(staId);
        parcel.writeInt(unit);
        parcel.writeString(stockTypeName);
        parcel.writeString(stockTypeId);
        parcel.writeString(unitId);
        parcel.writeString(batchRef);
        parcel.writeInt(mapID);
        parcel.writeString(warehouseStaName);
        parcel.writeString(altName);
        parcel.writeString(barcode);
        parcel.writeByte((byte) (isFavorite ? 1 : 0));
        parcel.writeString(reference);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.quantity, this.quantity);
        cv.put(DBTable.unitName, this.unitName);
        cv.put(DBTable.photoPath, this.photoPath);
        cv.put(DBTable.stockItemId, this.stockItemId);
        cv.put(DBTable.staStockItemId, this.staStockItemId);
        cv.put(DBTable.serialised, this.serialised);
        cv.put(DBTable.tmpid, this.tmpid);
        cv.put(DBTable.description, this.description);
        cv.put(DBTable.userId, this.userId);
        cv.put(DBTable.packagingName, this.packagingName);
        cv.put(DBTable.packagingId, this.packagingId);
        cv.put(DBTable.staId, this.staId);
        cv.put(DBTable.unit, this.unit);
        cv.put(DBTable.stockTypeName, this.stockTypeName);
        cv.put(DBTable.stockTypeId, this.stockTypeId);
        cv.put(DBTable.unitId, this.unitId);
        cv.put(DBTable.batchRef, this.batchRef);
        cv.put(DBTable.mapID, this.mapID);
        cv.put(DBTable.warehouseStaName, this.warehouseStaName);
        cv.put(DBTable.altName, this.altName);
        cv.put(DBTable.barcode, this.barcode);
        cv.put(DBTable.isFavorite, this.isFavorite);
        cv.put(DBTable.reference, this.reference);
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "MyStore";
        public static final String quantity = "quantity";
        public static final String unitName = "unitName";
        public static final String photoPath = "photoPath";
        public static final String stockItemId = "stockItemId";
        public static final String staStockItemId = "staStockItemId";
        public static final String serialised = "serialised";
        public static final String tmpid = "tmpid";
        public static final String description = "description";
        public static final String userId = "userId";
        public static final String packagingName = "packagingName";
        public static final String packagingId = "packagingId";
        public static final String staId = "staId";
        public static final String unit = "unit";
        public static final String stockTypeName = "stockTypeName";
        public static final String stockTypeId = "stockTypeId";
        public static final String unitId = "unitId";
        public static final String batchRef = "batchRef";
        public static final String mapID = "mapID";
        public static final String warehouseStaName = "warehouseStaName";
        public static final String altName = "altName";
        public static final String barcode = "barcode";
        public static final String isFavorite = "isFavorite";
        public static final String reference = "reference";
    }
}