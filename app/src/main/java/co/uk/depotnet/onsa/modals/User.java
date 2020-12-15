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
    private String tokenId;
    private String foreName;
    private boolean isDisclaimerAccepted;
    private boolean canSeeCommercial;
    private boolean twoFactorEnabled;
    private boolean twoFactorMandatory;
    private boolean captureMfaChallenge;
    private boolean isLoggedIn;
    private boolean backfill;
    private boolean reinstatement;
    private boolean muckaway;
    private boolean serviceMaterialDrop;
    private boolean siteClear;
    private String qrCodeBase64;

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
        tokenId = in.readString();
        foreName = in.readString();
        isDisclaimerAccepted = in.readInt() == 1;
        canSeeCommercial = in.readInt() == 1;
        twoFactorEnabled = in.readInt() == 1;
        twoFactorMandatory = in.readInt() == 1;
        captureMfaChallenge = in.readInt() == 1;
        isLoggedIn = in.readInt() == 1;
        backfill = in.readInt() == 1;
        reinstatement = in.readInt() == 1;
        muckaway = in.readInt() == 1;
        serviceMaterialDrop = in.readInt() == 1;
        siteClear = in.readInt() == 1;
        qrCodeBase64 = in.readString();
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
        tokenId = cursor.getString(cursor.getColumnIndex(DBTable.tokenId));
        foreName = cursor.getString(cursor.getColumnIndex(DBTable.foreName));
        isDisclaimerAccepted = cursor.getInt(cursor.getColumnIndex(DBTable.isDisclaimerAccepted)) == 1;
        canSeeCommercial = cursor.getInt(cursor.getColumnIndex(DBTable.canSeeCommercial)) == 1;
        twoFactorEnabled = cursor.getInt(cursor.getColumnIndex(DBTable.twoFactorEnabled)) == 1;
        twoFactorMandatory = cursor.getInt(cursor.getColumnIndex(DBTable.twoFactorMandatory)) == 1;
        captureMfaChallenge = cursor.getInt(cursor.getColumnIndex(DBTable.captureMfaChallenge)) == 1;
        isLoggedIn = cursor.getInt(cursor.getColumnIndex(DBTable.isLoggedIn)) == 1;
        backfill = cursor.getInt(cursor.getColumnIndex(DBTable.backfill)) == 1;
        reinstatement = cursor.getInt(cursor.getColumnIndex(DBTable.reinstatement)) == 1;
        muckaway = cursor.getInt(cursor.getColumnIndex(DBTable.muckaway)) == 1;
        serviceMaterialDrop = cursor.getInt(cursor.getColumnIndex(DBTable.serviceMaterialDrop)) == 1;
        siteClear = cursor.getInt(cursor.getColumnIndex(DBTable.siteClear)) == 1;
        qrCodeBase64 = cursor.getString(cursor.getColumnIndex(DBTable.qrCodeBase64));

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

    public boolean isCaptureMfaChallenge() {
        return captureMfaChallenge;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setLoggedIn(boolean loggedIn) {
        isLoggedIn = loggedIn;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
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

    public boolean isBackfill() {
        return backfill;
    }

    public boolean isReinstatement() {
        return reinstatement;
    }

    public boolean isSiteClear() {
        return siteClear;
    }

    public boolean isMuckaway() {
        return muckaway;
    }

    public boolean isServiceMaterialDrop() {
        return serviceMaterialDrop;
    }

    public String getQrCodeBase64() {
        return qrCodeBase64;
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
        dest.writeString(tokenId);
        dest.writeString(foreName);
        dest.writeInt(isDisclaimerAccepted ? 1 : 0);
        dest.writeInt(canSeeCommercial ? 1 : 0);
        dest.writeInt(twoFactorEnabled ? 1 : 0);
        dest.writeInt(twoFactorMandatory ? 1 : 0);
        dest.writeInt(captureMfaChallenge ? 1 : 0);
        dest.writeInt(isLoggedIn ? 1 : 0);
        dest.writeInt(backfill ? 1 : 0);
        dest.writeInt(reinstatement ? 1 : 0);
        dest.writeInt(muckaway ? 1 : 0);
        dest.writeInt(serviceMaterialDrop ? 1 : 0);
        dest.writeInt(siteClear ? 1 : 0);
        dest.writeString(qrCodeBase64);
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
        cv.put(DBTable.captureMfaChallenge, this.captureMfaChallenge);
        cv.put(DBTable.isLoggedIn, this.isLoggedIn);
        cv.put(DBTable.backfill, this.backfill);
        cv.put(DBTable.reinstatement, this.reinstatement);
        cv.put(DBTable.muckaway, this.muckaway);
        cv.put(DBTable.serviceMaterialDrop, this.serviceMaterialDrop);
        cv.put(DBTable.siteClear, this.siteClear);
        cv.put(DBTable.siteClear, this.siteClear);
        cv.put(DBTable.qrCodeBase64, this.qrCodeBase64);

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
        public static final String tokenId = "tokenId";
        public static final String foreName = "foreName";
        public static final String isDisclaimerAccepted = "isDisclaimerAccepted";
        public static final String canSeeCommercial = "canSeeCommercial";
        public static final String twoFactorEnabled = "twoFactorEnabled";
        public static final String twoFactorMandatory = "twoFactorMandatory";
        public static final String captureMfaChallenge = "captureMfaChallenge";
        public static final String isLoggedIn = "isLoggedIn";
        public static final String backfill = "backfill";
        public static final String reinstatement = "reinstatement";
        public static final String muckaway = "muckaway";
        public static final String serviceMaterialDrop = "serviceMaterialDrop";
        public static final String siteClear = "siteClear";
        public static final String qrCodeBase64 = "qrCodeBase64";

    }
}