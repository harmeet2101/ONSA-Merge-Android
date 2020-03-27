package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import co.uk.depotnet.onsa.listeners.DropDownItem;

public class RiskElementType implements Parcelable , DropDownItem {
    public static final Creator<RiskElementType> CREATOR = new Creator<RiskElementType>() {
        @Override
        public RiskElementType createFromParcel(Parcel in) {
            return new RiskElementType(in);
        }

        @Override
        public RiskElementType[] newArray(int size) {
            return new RiskElementType[size];

        }

    };
    private String onScreenText;
    private String text;
    private String value;
    private String type;

    protected RiskElementType(Parcel in) {
        onScreenText = in.readString();
        text = in.readString();
        value = in.readString();
        type = in.readString();

    }

    public RiskElementType(Cursor cursor) {
        onScreenText = cursor.getString(cursor.getColumnIndex(DBTable.onScreenText));
        text = cursor.getString(cursor.getColumnIndex(DBTable.text));
        value = cursor.getString(cursor.getColumnIndex(DBTable.value));
        type = cursor.getString(cursor.getColumnIndex(DBTable.type));
    }

    public String getOnScreenText() {
        return onScreenText;
    }

    public void setOnScreenText(String onScreenText) {
        this.onScreenText = onScreenText;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(onScreenText);
        parcel.writeString(text);
        parcel.writeString(value);
        parcel.writeString(type);

    }

    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.onScreenText, this.onScreenText);
        cv.put(DBTable.text, this.text);
        cv.put(DBTable.value, this.value);
        cv.put(DBTable.type, this.type);

        return cv;
    }

    public static class DBTable {
        public static final String NAME = "riskAssessmentRiskElementTypes";
        public static final String onScreenText = "onScreenText";
        public static final String text = "text";
        public static final String value = "value";
        public static final String type = "type";
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