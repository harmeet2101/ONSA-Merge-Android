package co.uk.depotnet.onsa.modals;
import android.content.ContentValues;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class ItemType implements Parcelable , DropDownItem {
	private int id;
	private String text;
	private String type;
	private String value;

	public void setid(int id){
		this.id = id;
	}

	public int getid(){
		return this.id;
	}

	public void settext(String text){
		this.text = text;
	}

	public String gettext(){
		return this.text;
	}

	public void settype(String type){
		this.type = type;
	}

	public String gettype(){
		return this.type;
	}

	public void setvalue(String value){
		this.value = value;
	}

	public String getvalue(){
		return this.value;
	}


	protected ItemType(Parcel in) {
		id = in.readInt();
		text = in.readString();
		type = in.readString();
		value = in.readString();

	}
	public static final Creator<ItemType> CREATOR = new Creator<ItemType>() {
		@Override
		public ItemType createFromParcel(Parcel in) {
			return new ItemType(in); 
		}

		@Override
		public ItemType[] newArray(int size) {
			return new ItemType[size]; 

		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	public static class DBTable{
		public static final String NAME = "ItemTypes";
		public static final String id = "id";
		public static final String text = "text";
		public static final String type = "type";
		public static final String value = "value";

	}
	@Override
	public void writeToParcel(Parcel parcel, int i) {
	parcel.writeInt(id);
	parcel.writeString(text);
	parcel.writeString(type);
	parcel.writeString(value);

	}

	public ItemType(String text, String type, String value) {
		this.text = text;
		this.type = type;
		this.value = value;
	}

	public ItemType(Cursor cursor) {
		id = cursor.getInt(cursor.getColumnIndex(DBTable.id));
		text = cursor.getString(cursor.getColumnIndex(DBTable.text));
		type = cursor.getString(cursor.getColumnIndex(DBTable.type));
		value = cursor.getString(cursor.getColumnIndex(DBTable.value));

	}
	
	public ContentValues toContentValues(){

		ContentValues cv = new ContentValues();

		cv.put(DBTable.text , this.text);

		cv.put(DBTable.type , this.type);

		cv.put(DBTable.value , this.value);

		 return cv;	
	}

	@Override
	public String getDisplayItem() {
		return text;
	}

	@Override
	public String getUploadValue() {
		return value;
	}
}
