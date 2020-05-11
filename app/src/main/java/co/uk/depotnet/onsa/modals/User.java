package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;

public class User implements Parcelable {
    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
    private String surName;
    private ArrayList<String> permissions;
    private String roleName;
    private String expirationUtc;
    private String userName;
    private String userId;
    private String token;
    private String foreName;
    private boolean isDisclaimerAccepted;
    private boolean canSeeCommercial;
    private boolean twoFactorEnabled;
    private boolean twoFactorMandatory;

    public void setTwoFactorMandatory(boolean twoFactorMandatory) {
        this.twoFactorMandatory = twoFactorMandatory;
    }

    protected User(Parcel in) {
        surName = in.readString();
        permissions = in.createStringArrayList();
        roleName = in.readString();
        expirationUtc = in.readString();
        userName = in.readString();
        userId = in.readString();
        token = in.readString();
        foreName = in.readString();
        isDisclaimerAccepted = in.readInt() == 1;
        canSeeCommercial = in.readInt() == 1;
        twoFactorEnabled = in.readInt() == 1;
        twoFactorMandatory = in.readInt() == 1;
    }

    public User(Cursor cursor) {
        surName = cursor.getString(cursor.getColumnIndex(DBTable.surName));
        String permissions = cursor.getString(cursor.getColumnIndex(DBTable.permissions));
        this.permissions = new ArrayList<>();
        if (permissions != null) {
            Collections.addAll(this.permissions, permissions.split(","));
        }
        roleName = cursor.getString(cursor.getColumnIndex(DBTable.roleName));
        expirationUtc = cursor.getString(cursor.getColumnIndex(DBTable.expirationUtc));
        userName = cursor.getString(cursor.getColumnIndex(DBTable.userName));
        userId = cursor.getString(cursor.getColumnIndex(DBTable.userId));
        token = cursor.getString(cursor.getColumnIndex(DBTable.token));
        foreName = cursor.getString(cursor.getColumnIndex(DBTable.foreName));
        isDisclaimerAccepted = cursor.getInt(cursor.getColumnIndex(DBTable.isDisclaimerAccepted)) == 1;
        canSeeCommercial = cursor.getInt(cursor.getColumnIndex(DBTable.canSeeCommercial)) == 1;
        twoFactorEnabled = cursor.getInt(cursor.getColumnIndex(DBTable.twoFactorEnabled)) == 1;
        twoFactorMandatory = cursor.getInt(cursor.getColumnIndex(DBTable.twoFactorMandatory)) == 1;

    }

    public void setsurName(String surName) {
        this.surName = surName;
    }

    public String getsurName() {
        return this.surName;
    }

    public void setpermissions(ArrayList<String> permissions) {
        this.permissions = permissions;
    }

    public ArrayList<String> getpermissions() {
        return this.permissions;
    }

    public void setroleName(String roleName) {
        this.roleName = roleName;
    }

    public String getroleName() {
        return this.roleName;
    }

    public void setexpirationUtc(String expirationUtc) {
        this.expirationUtc = expirationUtc;
    }

    public String getexpirationUtc() {
        return this.expirationUtc;
    }

    public void setuserName(String userName) {
        this.userName = userName;
    }

    public String getuserName() {
        return this.userName;
    }

    public void setuserId(String userId) {
        this.userId = userId;
    }

    public String getuserId() {
        return this.userId;
    }

    public void settoken(String token) {
        this.token = token;
    }

    public String gettoken() {
        return this.token;
    }

    public void setforeName(String foreName) {
        this.foreName = foreName;
    }

    public String getforeName() {
        return this.foreName;
    }

    public boolean isDisclaimerAccepted() {
        return isDisclaimerAccepted;
    }

    public boolean isCanSeeCommercial() {
        return canSeeCommercial;
    }

    public boolean isTwoFactorEnabled() {
        return twoFactorEnabled;
    }

    public boolean isTwoFactorMandatory() {
        return twoFactorMandatory;
    }

    public void setDisclaimerAccepted(boolean disclaimerAccepted) {
        isDisclaimerAccepted = disclaimerAccepted;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(surName);
        dest.writeStringList(permissions);
        dest.writeString(roleName);
        dest.writeString(expirationUtc);
        dest.writeString(userName);
        dest.writeString(userId);
        dest.writeString(token);
        dest.writeString(foreName);
        dest.writeInt(isDisclaimerAccepted ? 1 : 0);
        dest.writeInt(canSeeCommercial ? 1 : 0);
        dest.writeInt(twoFactorEnabled ? 1 : 0);
        dest.writeInt(twoFactorMandatory ? 1 : 0);
    }

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.surName, this.surName);
        String permissions = "";
        if (this.permissions != null && !this.permissions.isEmpty()) {
            permissions = TextUtils.join(",", this.permissions);
        }


        cv.put(DBTable.permissions, permissions);

        cv.put(DBTable.roleName, this.roleName);

        cv.put(DBTable.expirationUtc, this.expirationUtc);

        cv.put(DBTable.userName, this.userName);

        cv.put(DBTable.userId, this.userId);

        cv.put(DBTable.token, this.token);

        cv.put(DBTable.foreName, this.foreName);
        cv.put(DBTable.isDisclaimerAccepted, this.isDisclaimerAccepted);
        cv.put(DBTable.canSeeCommercial, this.canSeeCommercial);
        cv.put(DBTable.twoFactorEnabled, this.twoFactorEnabled);
        cv.put(DBTable.twoFactorMandatory, this.twoFactorMandatory);

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "User";
        public static final String surName = "surName";
        public static final String permissions = "permissions";
        public static final String roleName = "roleName";
        public static final String expirationUtc = "expirationUtc";
        public static final String userName = "userName";
        public static final String userId = "userId";
        public static final String token = "token";
        public static final String foreName = "foreName";
        public static final String isDisclaimerAccepted = "isDisclaimerAccepted";
        public static final String canSeeCommercial = "canSeeCommercial";
        public static final String twoFactorEnabled = "twoFactorEnabled";
        public static final String twoFactorMandatory = "twoFactorMandatory";

    }
}