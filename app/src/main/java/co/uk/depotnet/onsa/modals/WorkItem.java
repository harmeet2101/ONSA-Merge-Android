package co.uk.depotnet.onsa.modals;//TODO: package name
import android.content.ContentValues;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class WorkItem implements Parcelable , DropDownItem {

	private int id;
	private String type;
	private String itemCode;
	private String description;
	private int revisionNo;



	public void setitemCode(String itemCode){
		this.itemCode = itemCode;
	}

	public String getitemCode(){
		return this.itemCode;
	}

	public void setdescription(String description){
		this.description = description;
	}

	public String getdescription(){
		return this.description;
	}

	public void setrevisionNo(int revisionNo){
		this.revisionNo = revisionNo;
	}

	public int getrevisionNo(){
		return this.revisionNo;
	}

	public void setid(int id){
		this.id = id;
	}

	public int getid(){
		return this.id;
	}

	public void settype(String type){
		this.type = type;
	}

	public String gettype(){
		return this.type;
	}


	protected WorkItem(Parcel in) {
		itemCode = in.readString();
		description = in.readString();
		revisionNo = in.readInt();
		id = in.readInt();
		type = in.readString();

	}
	public static final Creator<WorkItem> CREATOR = new Creator<WorkItem>() {
		@Override
		public WorkItem createFromParcel(Parcel in) {
			return new WorkItem(in); 
		}

		@Override
		public WorkItem[] newArray(int size) {
			return new WorkItem[size]; 

		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String getDisplayItem() {
		return description;
	}

	@Override
	public String getUploadValue() {
		return itemCode;
	}

	public static class DBTable{
		public static final String NAME = "WorkItems";
		public static final String itemCode = "itemCode";
		public static final String description = "description";
		public static final String revisionNo = "revisionNo";
		public static final String id = "id";
		public static final String type = "type";

	}
	@Override
	public void writeToParcel(Parcel parcel, int i) {
	parcel.writeString(itemCode);
	parcel.writeString(description);
	parcel.writeInt(revisionNo);
	parcel.writeInt(id);
	parcel.writeString(type);

	}
	public WorkItem(Cursor cursor) {
		itemCode = cursor.getString(cursor.getColumnIndex(DBTable.itemCode));
		description = cursor.getString(cursor.getColumnIndex(DBTable.description));
		revisionNo = cursor.getInt(cursor.getColumnIndex(DBTable.revisionNo));
		id = cursor.getInt(cursor.getColumnIndex(DBTable.id));
		type = cursor.getString(cursor.getColumnIndex(DBTable.type));

	}
	public ContentValues toContentValues(){

		ContentValues cv = new ContentValues();

		cv.put(DBTable.itemCode , this.itemCode);

		cv.put(DBTable.description , this.description);

		cv.put(DBTable.revisionNo , this.revisionNo);

		cv.put(DBTable.type , this.type);

		 return cv;	}
}
