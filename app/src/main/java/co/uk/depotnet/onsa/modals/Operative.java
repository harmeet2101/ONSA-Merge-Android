package co.uk.depotnet.onsa.modals;//TODO: package name
import android.content.ContentValues;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class Operative implements Parcelable ,
		DropDownItem {
	private String MobileNo;
	private String Email;
	private boolean SuspendedYN;
	private int EngineerID;
	private String GangName;
	private int RoleID;
	private boolean TMController;
	private String RoleName;
	private int SignaturePhotoID;
	private String Username;
	private int UserID;
	private String Forename;
	private String FullName;
	private int ProfilePhotoID;
	private String text;
	private int id;
	private String Surname;
	private boolean ActiveYN;
	private String ReferenceNo;
	private String Notes;

	public void setMobileNo(String MobileNo){
		this.MobileNo = MobileNo;
	}

	public String getMobileNo(){
		return this.MobileNo;
	}

	public void setEmail(String Email){
		this.Email = Email;
	}

	public String getEmail(){
		return this.Email;
	}

	public void setSuspendedYN(boolean SuspendedYN){
		this.SuspendedYN = SuspendedYN;
	}

	public boolean isSuspendedYN(){
		return this.SuspendedYN;
	}

	public void setEngineerID(int EngineerID){
		this.EngineerID = EngineerID;
	}

	public int getEngineerID(){
		return this.EngineerID;
	}

	public void setGangName(String GangName){
		this.GangName = GangName;
	}

	public String getGangName(){
		return this.GangName;
	}

	public void setRoleID(int RoleID){
		this.RoleID = RoleID;
	}

	public int getRoleID(){
		return this.RoleID;
	}

	public void setTMController(boolean TMController){
		this.TMController = TMController;
	}

	public boolean isTMController(){
		return this.TMController;
	}

	public void setRoleName(String RoleName){
		this.RoleName = RoleName;
	}

	public String getRoleName(){
		return this.RoleName;
	}

	public void setSignaturePhotoID(int SignaturePhotoID){
		this.SignaturePhotoID = SignaturePhotoID;
	}

	public int getSignaturePhotoID(){
		return this.SignaturePhotoID;
	}

	public void setUsername(String Username){
		this.Username = Username;
	}

	public String getUsername(){
		return this.Username;
	}

	public void setUserID(int UserID){
		this.UserID = UserID;
	}

	public int getUserID(){
		return this.UserID;
	}

	public void setForename(String Forename){
		this.Forename = Forename;
	}

	public String getForename(){
		return this.Forename;
	}

	public void setFullName(String FullName){
		this.FullName = FullName;
	}

	public String getFullName(){
		return this.FullName;
	}

	public void setProfilePhotoID(int ProfilePhotoID){
		this.ProfilePhotoID = ProfilePhotoID;
	}

	public int getProfilePhotoID(){
		return this.ProfilePhotoID;
	}

	public void settext(String text){
		this.text = text;
	}

	public String gettext(){
		return this.text;
	}

	public void setid(int id){
		this.id = id;
	}

	public int getid(){
		return this.id;
	}

	public void setSurname(String Surname){
		this.Surname = Surname;
	}

	public String getSurname(){
		return this.Surname;
	}

	public void setActiveYN(boolean ActiveYN){
		this.ActiveYN = ActiveYN;
	}

	public boolean isActiveYN(){
		return this.ActiveYN;
	}

	public void setReferenceNo(String ReferenceNo){
		this.ReferenceNo = ReferenceNo;
	}

	public String getReferenceNo(){
		return this.ReferenceNo;
	}

	public void setNotes(String Notes){
		this.Notes = Notes;
	}

	public String getNotes(){
		return this.Notes;
	}


	protected Operative(Parcel in) {
		MobileNo = in.readString();
		Email = in.readString();
		SuspendedYN = in.readByte() != 0;
		EngineerID = in.readInt();
		GangName = in.readString();
		RoleID = in.readInt();
		TMController = in.readByte() != 0;
		RoleName = in.readString();
		SignaturePhotoID = in.readInt();
		Username = in.readString();
		UserID = in.readInt();
		Forename = in.readString();
		FullName = in.readString();
		ProfilePhotoID = in.readInt();
		text = in.readString();
		id = in.readInt();
		Surname = in.readString();
		ActiveYN = in.readByte() != 0;
		ReferenceNo = in.readString();
		Notes = in.readString();

	}
	public static final Creator<Operative> CREATOR = new Creator<Operative>() {
		@Override
		public Operative createFromParcel(Parcel in) {
			return new Operative(in); 
		}

		@Override
		public Operative[] newArray(int size) {
			return new Operative[size]; 

		}

	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public String getDisplayItem() {
		return this.getFullName();
	}

	@Override
	public String getUploadValue() {
		return String.valueOf(this.getUserID());
	}

	public static class DBTable{
		public static final String NAME = "Operatives";
		public static final String MobileNo = "MobileNo";
		public static final String Email = "Email";
		public static final String SuspendedYN = "SuspendedYN";
		public static final String EngineerID = "EngineerID";
		public static final String GangName = "GangName";
		public static final String RoleID = "RoleID";
		public static final String TMController = "TMController";
		public static final String RoleName = "RoleName";
		public static final String SignaturePhotoID = "SignaturePhotoID";
		public static final String Username = "Username";
		public static final String UserID = "UserID";
		public static final String Forename = "Forename";
		public static final String FullName = "FullName";
		public static final String ProfilePhotoID = "ProfilePhotoID";
		public static final String text = "text";
		public static final String id = "id";
		public static final String Surname = "Surname";
		public static final String ActiveYN = "ActiveYN";
		public static final String ReferenceNo = "ReferenceNo";
		public static final String Notes = "Notes";

	}
	@Override
	public void writeToParcel(Parcel parcel, int i) {
	parcel.writeString(MobileNo);
	parcel.writeString(Email);
	parcel.writeByte((byte) (SuspendedYN ? 1 : 0));
	parcel.writeInt(EngineerID);
	parcel.writeString(GangName);
	parcel.writeInt(RoleID);
	parcel.writeByte((byte) (TMController ? 1 : 0));
	parcel.writeString(RoleName);
	parcel.writeInt(SignaturePhotoID);
	parcel.writeString(Username);
	parcel.writeInt(UserID);
	parcel.writeString(Forename);
	parcel.writeString(FullName);
	parcel.writeInt(ProfilePhotoID);
	parcel.writeString(text);
	parcel.writeInt(id);
	parcel.writeString(Surname);
	parcel.writeByte((byte) (ActiveYN ? 1 : 0));
	parcel.writeString(ReferenceNo);
	parcel.writeString(Notes);

	}
	public Operative(Cursor cursor) {
		MobileNo = cursor.getString(cursor.getColumnIndex(DBTable.MobileNo));
		Email = cursor.getString(cursor.getColumnIndex(DBTable.Email));
		SuspendedYN = cursor.getInt(cursor.getColumnIndex(DBTable.SuspendedYN))!=0;
		EngineerID = cursor.getInt(cursor.getColumnIndex(DBTable.EngineerID));
		GangName = cursor.getString(cursor.getColumnIndex(DBTable.GangName));
		RoleID = cursor.getInt(cursor.getColumnIndex(DBTable.RoleID));
		TMController = cursor.getInt(cursor.getColumnIndex(DBTable.TMController))!=0;
		RoleName = cursor.getString(cursor.getColumnIndex(DBTable.RoleName));
		SignaturePhotoID = cursor.getInt(cursor.getColumnIndex(DBTable.SignaturePhotoID));
		Username = cursor.getString(cursor.getColumnIndex(DBTable.Username));
		UserID = cursor.getInt(cursor.getColumnIndex(DBTable.UserID));
		Forename = cursor.getString(cursor.getColumnIndex(DBTable.Forename));
		FullName = cursor.getString(cursor.getColumnIndex(DBTable.FullName));
		ProfilePhotoID = cursor.getInt(cursor.getColumnIndex(DBTable.ProfilePhotoID));
		text = cursor.getString(cursor.getColumnIndex(DBTable.text));
		id = cursor.getInt(cursor.getColumnIndex(DBTable.id));
		Surname = cursor.getString(cursor.getColumnIndex(DBTable.Surname));
		ActiveYN = cursor.getInt(cursor.getColumnIndex(DBTable.ActiveYN))!=0;
		ReferenceNo = cursor.getString(cursor.getColumnIndex(DBTable.ReferenceNo));
		Notes = cursor.getString(cursor.getColumnIndex(DBTable.Notes));

	}
	public ContentValues toContentValues(){

		ContentValues cv = new ContentValues();

		cv.put(DBTable.MobileNo , this.MobileNo);

		cv.put(DBTable.Email , this.Email);

		cv.put(DBTable.SuspendedYN , this.SuspendedYN);

		cv.put(DBTable.EngineerID , this.EngineerID);

		cv.put(DBTable.GangName , this.GangName);

		cv.put(DBTable.RoleID , this.RoleID);

		cv.put(DBTable.TMController , this.TMController);

		cv.put(DBTable.RoleName , this.RoleName);

		cv.put(DBTable.SignaturePhotoID , this.SignaturePhotoID);

		cv.put(DBTable.Username , this.Username);

		cv.put(DBTable.UserID , this.UserID);

		cv.put(DBTable.Forename , this.Forename);

		cv.put(DBTable.FullName , this.FullName);

		cv.put(DBTable.ProfilePhotoID , this.ProfilePhotoID);

		cv.put(DBTable.text , this.text);

		cv.put(DBTable.id , this.id);

		cv.put(DBTable.Surname , this.Surname);

		cv.put(DBTable.ActiveYN , this.ActiveYN);

		cv.put(DBTable.ReferenceNo , this.ReferenceNo);

		cv.put(DBTable.Notes , this.Notes);

		 return cv;	}
}