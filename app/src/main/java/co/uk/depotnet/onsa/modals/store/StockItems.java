package co.uk.depotnet.onsa.modals.store;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class StockItems implements Parcelable, DropDownItem {

    public static final Creator<StockItems> CREATOR = new Creator<StockItems>() {
        @Override
        public StockItems createFromParcel(Parcel in) {
            return new StockItems(in);
        }

        @Override
        public StockItems[] newArray(int size) {
            return new StockItems[size];

        }
    };

    private String departmentName;
    private String supplierName;
    private String supplierId;
    private String unitName;
    private String stockItemId;
    private String staStockItemId;
    private String staId;
    private String warehouseStaName;
    private String departmentId;
    private String description;
    private int unit;
    private String stockTypeName;
    private String stockTypeId;
    private int mapID;
    private String altName;
    private String barcode;
    private String type;
    private String reference;
    private int stockLevelUnit;
    private int stockLevel;

    protected StockItems(Parcel in) {
        departmentName = in.readString();
        supplierName = in.readString();
        supplierId = in.readString();
        unitName = in.readString();
        stockItemId = in.readString();
        staStockItemId = in.readString();
        staId = in.readString();
        warehouseStaName = in.readString();
        departmentId = in.readString();
        description = in.readString();
        unit = in.readInt();
        stockTypeName = in.readString();
        stockTypeId = in.readString();
        mapID = in.readInt();
        altName = in.readString();
        barcode = in.readString();
        type = in.readString();
        reference = in.readString();
        stockLevelUnit = in.readInt();
        stockLevel = in.readInt();
    }

    public StockItems(Cursor cursor) {
        departmentName = cursor.getString(cursor.getColumnIndex(DBTable.departmentName));
        supplierName = cursor.getString(cursor.getColumnIndex(DBTable.supplierName));
        supplierId = cursor.getString(cursor.getColumnIndex(DBTable.supplierId));
        unitName = cursor.getString(cursor.getColumnIndex(DBTable.unitName));
        stockItemId = cursor.getString(cursor.getColumnIndex(DBTable.stockItemId));
        staStockItemId = cursor.getString(cursor.getColumnIndex(DBTable.staStockItemId));
        staId = cursor.getString(cursor.getColumnIndex(DBTable.staId));
        warehouseStaName = cursor.getString(cursor.getColumnIndex(DBTable.warehouseStaName));
        departmentId = cursor.getString(cursor.getColumnIndex(DBTable.departmentId));
        description = cursor.getString(cursor.getColumnIndex(DBTable.description));
        unit = cursor.getInt(cursor.getColumnIndex(DBTable.unit));
        stockTypeName = cursor.getString(cursor.getColumnIndex(DBTable.stockTypeName));
        stockTypeId = cursor.getString(cursor.getColumnIndex(DBTable.stockTypeId));
        mapID = cursor.getInt(cursor.getColumnIndex(DBTable.mapID));
        altName = cursor.getString(cursor.getColumnIndex(DBTable.altName));
        barcode = cursor.getString(cursor.getColumnIndex(DBTable.barcode));
        type = cursor.getString(cursor.getColumnIndex(DBTable.type));
        reference = cursor.getString(cursor.getColumnIndex(DBTable.reference));
        stockLevelUnit = cursor.getInt(cursor.getColumnIndex(DBTable.stockLevelUnit));
        stockLevel = cursor.getInt(cursor.getColumnIndex(DBTable.stockLevel));
    }

    public void setdepartmentName(String departmentName) {
        this.departmentName = departmentName;
    }

    public String getdepartmentName() {
        return this.departmentName;
    }

    public void setsupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getsupplierName() {
        return this.supplierName;
    }

    public void setsupplierId(String supplierId) {
        this.supplierId = supplierId;
    }

    public String getsupplierId() {
        return this.supplierId;
    }

    public void setunitName(String unitName) {
        this.unitName = unitName;
    }

    public String getunitName() {
        return this.unitName;
    }

    public void setstockItemId(String stockItemId) {
        this.stockItemId = stockItemId;
    }

    public String getstockItemId() {
        return this.stockItemId;
    }

    public void setdepartmentId(String departmentId) {
        this.departmentId = departmentId;
    }

    public String getdepartmentId() {
        return this.departmentId;
    }

    public void setdescription(String description) {
        this.description = description;
    }

    public String getdescription() {
        return this.description;
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

    public void setmapID(int mapID) {
        this.mapID = mapID;
    }

    public int getmapID() {
        return this.mapID;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStaId() {
        return staId;
    }

    public void setStaId(String staId) {
        this.staId = staId;
    }

    public String getStaStockItemId() {
        return staStockItemId;
    }

    public void setStaStockItemId(String staStockItemId) {
        this.staStockItemId = staStockItemId;
    }

    public String getWarehouseStaName() {
        return warehouseStaName;
    }


    public void setWarehouseStaName(String warehouseStaName) {
        this.warehouseStaName = warehouseStaName;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }

    public int getStockLevel() {
        return stockLevel;
    }

    public void setStockLevel(int stockLevel) {
        this.stockLevel = stockLevel;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(departmentName);
        parcel.writeString(supplierName);
        parcel.writeString(supplierId);
        parcel.writeString(unitName);
        parcel.writeString(stockItemId);
        parcel.writeString(staStockItemId);
        parcel.writeString(staId);
        parcel.writeString(warehouseStaName);
        parcel.writeString(departmentId);
        parcel.writeString(description);
        parcel.writeInt(unit);
        parcel.writeString(stockTypeName);
        parcel.writeString(stockTypeId);
        parcel.writeInt(mapID);
        parcel.writeString(altName);
        parcel.writeString(barcode);
        parcel.writeString(type);
        parcel.writeString(reference);
        parcel.writeInt(stockLevelUnit);
        parcel.writeInt(stockLevel);
    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.departmentName, this.departmentName);
        cv.put(DBTable.supplierName, this.supplierName);
        cv.put(DBTable.supplierId, this.supplierId);
        cv.put(DBTable.unitName, this.unitName);
        cv.put(DBTable.stockItemId, this.stockItemId);
        cv.put(DBTable.staStockItemId, this.staStockItemId);
        cv.put(DBTable.staId, this.staId);
        cv.put(DBTable.warehouseStaName, this.warehouseStaName);
        cv.put(DBTable.departmentId, this.departmentId);
        cv.put(DBTable.description, this.description);
        cv.put(DBTable.unit, this.unit);
        cv.put(DBTable.stockTypeName, this.stockTypeName);
        cv.put(DBTable.stockTypeId, this.stockTypeId);
        cv.put(DBTable.mapID, this.mapID);
        cv.put(DBTable.altName, this.altName);
        cv.put(DBTable.barcode, this.barcode);
        cv.put(DBTable.type, this.type);
        cv.put(DBTable.reference, this.reference);
        cv.put(DBTable.stockLevelUnit, this.stockLevelUnit);
        cv.put(DBTable.stockLevel, this.stockLevel);
        return cv;
    }

    public int getStockLevelUnit() {
        return stockLevelUnit;
    }

    public void setStockLevelUnit(int stockLevelUnit) {
        this.stockLevelUnit = stockLevelUnit;
    }

    @Override
    public String getDisplayItem() {
        return this.altName;
    }

    @Override
    public String getUploadValue() {
        return this.getstockItemId();
    }



    public static class DBTable {
        public static final String NAME = "StockItems";
        public static final String departmentName = "departmentName";
        public static final String supplierName = "supplierName";
        public static final String supplierId = "supplierId";
        public static final String unitName = "unitName";
        public static final String stockItemId = "stockItemId";
        public static final String staId = "staId";
        public static final String warehouseStaName = "warehouseStaName";
        public static final String departmentId = "departmentId";
        public static final String description = "description";
        public static final String unit = "unit";
        public static final String stockTypeName = "stockTypeName";
        public static final String stockTypeId = "stockTypeId";
        public static final String staStockItemId = "staStockItemId";
        public static final String mapID = "mapID";
        public static final String altName = "altName";
        public static final String barcode = "barcode";
        public static final String type = "type";
        public static final String reference = "reference";
        public static final String stockLevelUnit = "stockLevelUnit";
        public static final String stockLevel = "stockLevel";
    }
}