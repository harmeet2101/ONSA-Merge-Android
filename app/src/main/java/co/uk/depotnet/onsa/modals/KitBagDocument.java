package co.uk.depotnet.onsa.modals;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class KitBagDocument implements Parcelable {
    private String text;
    private String id;
    private String updatedDate;


    public KitBagDocument(Cursor cursor) {
        text = cursor.getString(cursor.getColumnIndex(DBTable.text));
        id = cursor.getString(cursor.getColumnIndex(DBTable.id));
        updatedDate = cursor.getString(cursor.getColumnIndex(DBTable.updatedDate));

    }

    protected KitBagDocument(Parcel in) {
        text = in.readString();
        id = in.readString();
        updatedDate = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeString(id);
        dest.writeString(updatedDate);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<KitBagDocument> CREATOR = new Creator<KitBagDocument>() {
        @Override
        public KitBagDocument createFromParcel(Parcel in) {
            return new KitBagDocument(in);
        }

        @Override
        public KitBagDocument[] newArray(int size) {
            return new KitBagDocument[size];
        }
    };

    public ContentValues toContentValues() {

        ContentValues cv = new ContentValues();

        cv.put(DBTable.text, this.text);

        cv.put(DBTable.id, this.id);

        cv.put(DBTable.updatedDate, this.updatedDate);
        return cv;
    }

    public static class DBTable {
        public static final String NAME = "KitBagDocuments";
        public static final String text = "text";
        public static final String id = "id";
        public static final String updatedDate = "updatedDate";
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(String updatedDate) {
        this.updatedDate = updatedDate;
    }
}