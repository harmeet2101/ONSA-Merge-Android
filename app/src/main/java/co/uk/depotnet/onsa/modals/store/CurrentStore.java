package co.uk.depotnet.onsa.modals.store;

import android.os.Parcel;
import android.os.Parcelable;

public class CurrentStore implements Parcelable {
    public static final Creator<CurrentStore> CREATOR = new Creator<CurrentStore>() {
        @Override
        public CurrentStore createFromParcel(Parcel in) {
            return new CurrentStore(in);
        }

        @Override
        public CurrentStore[] newArray(int size) {
            return new CurrentStore[size];
        }
    };

    private String ItemID = "ItemID";
    private String Barcode = "Barcode";
    private String MapID = "MapID";
    private String StockItemType = "StockItemType";
    private String Description = "altDescription";
    private String AltName = "AltName";
    private String Packaging = "Packaging";
    private String Unit = "Unit";
    private String UnitType = "UnitType";
    private String Quantity = "Quantity";
    private String Supplier = "Supplier";
    private String Department = "Department";
    private String Warehouse = "Warehouse";
    private String Serialised = "Serialised";
    private String StoreImage = "StoreImage";
    private boolean isFavorite;


    public CurrentStore() {

    }

    protected CurrentStore(Parcel in) {
        ItemID = in.readString();
        Barcode = in.readString();
        MapID = in.readString();
        StockItemType = in.readString();
        Description = in.readString();
        AltName = in.readString();
        Packaging = in.readString();
        Unit = in.readString();
        UnitType = in.readString();
        Quantity = in.readString();
        Supplier = in.readString();
        Department = in.readString();
        Warehouse = in.readString();
        Serialised = in.readString();
        StoreImage = in.readString();
        isFavorite = in.readInt() == 1;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ItemID);
        dest.writeString(Barcode);
        dest.writeString(MapID);
        dest.writeString(StockItemType);
        dest.writeString(Description);
        dest.writeString(AltName);
        dest.writeString(Packaging);
        dest.writeString(Unit);
        dest.writeString(UnitType);
        dest.writeString(Quantity);
        dest.writeString(Supplier);
        dest.writeString(Department);
        dest.writeString(Warehouse);
        dest.writeString(Serialised);
        dest.writeString(StoreImage);
        dest.writeInt(isFavorite ? 1 : 0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    public String getBarcode() {
        return Barcode;
    }

    public void setBarcode(String barcode) {
        Barcode = barcode;
    }

    public String getMapID() {
        return MapID;
    }

    public void setMapID(String mapID) {
        MapID = mapID;
    }

    public String getStockItemType() {
        return StockItemType;
    }

    public void setStockItemType(String stockItemType) {
        StockItemType = stockItemType;
    }

    public String getDescription() {
        return Description;
    }

    public void setDescription(String description) {
        Description = description;
    }

    public String getAltName() {
        return AltName;
    }

    public void setAltName(String altName) {
        AltName = altName;
    }

    public String getPackaging() {
        return Packaging;
    }

    public void setPackaging(String packaging) {
        Packaging = packaging;
    }

    public String getUnit() {
        return Unit;
    }

    public void setUnit(String unit) {
        Unit = unit;
    }

    public String getUnitType() {
        return UnitType;
    }

    public void setUnitType(String unitType) {
        UnitType = unitType;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public String getSupplier() {
        return Supplier;
    }

    public void setSupplier(String supplier) {
        Supplier = supplier;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getWarehouse() {
        return Warehouse;
    }

    public void setWarehouse(String warehouse) {
        Warehouse = warehouse;
    }

    public String getSerialised() {
        return Serialised;
    }

    public void setSerialised(String serialised) {
        Serialised = serialised;
    }

    public String getStoreImage() {
        return StoreImage;
    }

    public void setStoreImage(String storeImage) {
        StoreImage = storeImage;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }
}
