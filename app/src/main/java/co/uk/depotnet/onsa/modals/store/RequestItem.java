package co.uk.depotnet.onsa.modals.store;//TODO: package name
import android.content.ContentValues;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class RequestItem implements Parcelable{
	private String supplierName;
	private int quantity;
	private String unitName;
	private String stockItemId;
	private String description;
	private String requestItemId;
	private double stockLevel;
	private String staId;
	private int unit;
	private boolean hasPhoto;
	private String stockTypeName;
	private String requestId;
	private int mapID;
	private String altName;
	private String warehouseStaName;
	private String barcode;

	public void setsupplierName(String supplierName){
		this.supplierName = supplierName;
	}

	public String getsupplierName(){
		return this.supplierName;
	}

	public void setquantity(int quantity){
		this.quantity = quantity;
	}

	public int getquantity(){
		return this.quantity;
	}

	public void setunitName(String unitName){
		this.unitName = unitName;
	}

	public String getunitName(){
		return this.unitName;
	}

	public void setstockItemId(String stockItemId){
		this.stockItemId = stockItemId;
	}

	public String getstockItemId(){
		return this.stockItemId;
	}

	public void setdescription(String description){
		this.description = description;
	}

	public String getdescription(){
		return this.description;
	}

	public void setrequestItemId(String requestItemId){
		this.requestItemId = requestItemId;
	}

	public String getrequestItemId(){
		return this.requestItemId;
	}

	public void setstockLevel(double stockLevel){
		this.stockLevel = stockLevel;
	}

	public double getstockLevel(){
		return this.stockLevel;
	}

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

	public void sethasPhoto(boolean hasPhoto){
		this.hasPhoto = hasPhoto;
	}

	public boolean ishasPhoto(){
		return this.hasPhoto;
	}

	public void setstockTypeName(String stockTypeName){
		this.stockTypeName = stockTypeName;
	}

	public String getstockTypeName(){
		return this.stockTypeName;
	}

	public void setrequestId(String requestId){
		this.requestId = requestId;
	}

	public String getrequestId(){
		return this.requestId;
	}

	public void setmapID(int mapID){
		this.mapID = mapID;
	}

	public int getmapID(){
		return this.mapID;
	}

	public void setaltName(String altName){
		this.altName = altName;
	}

	public String getaltName(){
		return this.altName;
	}

	public void setwarehouseStaName(String warehouseStaName){
		this.warehouseStaName = warehouseStaName;
	}

	public String getwarehouseStaName(){
		return this.warehouseStaName;
	}

	public void setbarcode(String barcode){
		this.barcode = barcode;
	}

	public String getbarcode(){
		return this.barcode;
	}


	protected RequestItem(Parcel in) {
		supplierName = in.readString();
		quantity = in.readInt();
		unitName = in.readString();
		stockItemId = in.readString();
		description = in.readString();
		requestItemId = in.readString();
		stockLevel = in.readDouble();
		staId = in.readString();
		unit = in.readInt();
		hasPhoto = in.readByte() != 0;
		stockTypeName = in.readString();
		requestId = in.readString();
		mapID = in.readInt();
		altName = in.readString();
		warehouseStaName = in.readString();
		barcode = in.readString();

	}
	public static final Creator<RequestItem> CREATOR = new Creator<RequestItem>() {
		@Override
		public RequestItem createFromParcel(Parcel in) {
			return new RequestItem(in); 
		}

		@Override
		public RequestItem[] newArray(int size) {
			return new RequestItem[size]; 

		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	public static class DBTable{
		public static final String NAME = "RequestItem";
		public static final String supplierName = "supplierName";
		public static final String quantity = "quantity";
		public static final String unitName = "unitName";
		public static final String stockItemId = "stockItemId";
		public static final String description = "description";
		public static final String requestItemId = "requestItemId";
		public static final String stockLevel = "stockLevel";
		public static final String staId = "staId";
		public static final String unit = "unit";
		public static final String hasPhoto = "hasPhoto";
		public static final String stockTypeName = "stockTypeName";
		public static final String requestId = "requestId";
		public static final String mapID = "mapID";
		public static final String altName = "altName";
		public static final String warehouseStaName = "warehouseStaName";
		public static final String barcode = "barcode";

	}
	@Override
	public void writeToParcel(Parcel parcel, int i) {
	parcel.writeString(supplierName);
	parcel.writeInt(quantity);
	parcel.writeString(unitName);
	parcel.writeString(stockItemId);
	parcel.writeString(description);
	parcel.writeString(requestItemId);
	parcel.writeDouble(stockLevel);
	parcel.writeString(staId);
	parcel.writeInt(unit);
	parcel.writeByte((byte) (hasPhoto ? 1 : 0));
	parcel.writeString(stockTypeName);
	parcel.writeString(requestId);
	parcel.writeInt(mapID);
	parcel.writeString(altName);
	parcel.writeString(warehouseStaName);
	parcel.writeString(barcode);

	}
	public RequestItem(Cursor cursor) {
		supplierName = cursor.getString(cursor.getColumnIndex(DBTable.supplierName));
		quantity = cursor.getInt(cursor.getColumnIndex(DBTable.quantity));
		unitName = cursor.getString(cursor.getColumnIndex(DBTable.unitName));
		stockItemId = cursor.getString(cursor.getColumnIndex(DBTable.stockItemId));
		description = cursor.getString(cursor.getColumnIndex(DBTable.description));
		requestItemId = cursor.getString(cursor.getColumnIndex(DBTable.requestItemId));
		stockLevel = cursor.getDouble(cursor.getColumnIndex(DBTable.stockLevel));
		staId = cursor.getString(cursor.getColumnIndex(DBTable.staId));
		unit = cursor.getInt(cursor.getColumnIndex(DBTable.unit));
		hasPhoto = cursor.getInt(cursor.getColumnIndex(DBTable.hasPhoto))!=0;
		stockTypeName = cursor.getString(cursor.getColumnIndex(DBTable.stockTypeName));
		requestId = cursor.getString(cursor.getColumnIndex(DBTable.requestId));
		mapID = cursor.getInt(cursor.getColumnIndex(DBTable.mapID));
		altName = cursor.getString(cursor.getColumnIndex(DBTable.altName));
		warehouseStaName = cursor.getString(cursor.getColumnIndex(DBTable.warehouseStaName));
		barcode = cursor.getString(cursor.getColumnIndex(DBTable.barcode));

	}
	public ContentValues toContentValues(){

		ContentValues cv = new ContentValues();

		cv.put(DBTable.supplierName , this.supplierName);

		cv.put(DBTable.quantity , this.quantity);

		cv.put(DBTable.unitName , this.unitName);

		cv.put(DBTable.stockItemId , this.stockItemId);

		cv.put(DBTable.description , this.description);

		cv.put(DBTable.requestItemId , this.requestItemId);

		cv.put(DBTable.stockLevel , this.stockLevel);

		cv.put(DBTable.staId , this.staId);

		cv.put(DBTable.unit , this.unit);

		cv.put(DBTable.hasPhoto , this.hasPhoto);

		cv.put(DBTable.stockTypeName , this.stockTypeName);

		cv.put(DBTable.requestId , this.requestId);

		cv.put(DBTable.mapID , this.mapID);

		cv.put(DBTable.altName , this.altName);

		cv.put(DBTable.warehouseStaName , this.warehouseStaName);

		cv.put(DBTable.barcode , this.barcode);

		 return cv;	}
}