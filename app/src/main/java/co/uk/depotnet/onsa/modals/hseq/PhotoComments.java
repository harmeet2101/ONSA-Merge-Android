package co.uk.depotnet.onsa.modals.hseq;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoComments implements Parcelable {
    private int id;
    private int answerId;
    private String comments;
    private String date;

    public PhotoComments() {
    }

    public PhotoComments(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            id = cursor.getInt(cursor.getColumnIndex(PhotoComments.DBTable.id));
            answerId = cursor.getInt(cursor.getColumnIndex(PhotoComments.DBTable.answerId));
            comments = cursor.getString(cursor.getColumnIndex(PhotoComments.DBTable.comments));
            date = cursor.getString(cursor.getColumnIndex(PhotoComments.DBTable.date));
        }
    }
    protected PhotoComments(Parcel in) {
        id = in.readInt();
        answerId = in.readInt();
        comments = in.readString();
        date = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeInt(answerId);
        dest.writeString(comments);
        dest.writeString(date);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoComments> CREATOR = new Creator<PhotoComments>() {
        @Override
        public PhotoComments createFromParcel(Parcel in) {
            return new PhotoComments(in);
        }

        @Override
        public PhotoComments[] newArray(int size) {
            return new PhotoComments[size];
        }
    };

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    public static class DBTable {
        public static final String NAME = "PhotoComments";
        public static final String id = "id";
        public static final String answerId = "answerId";
        public static final String comments = "comments";
        public static final String date = "date";
    }
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        if (id > 0) {
            cv.put(PhotoComments.DBTable.id, id);
        }

        cv.put(PhotoComments.DBTable.answerId, answerId);
        cv.put(PhotoComments.DBTable.comments, comments);
        cv.put(PhotoComments.DBTable.date, date);

        return cv;
    }

    @Override
    public String toString() {
        return "PhotoComments{" +
                "id=" + id +
                ", answerId=" + answerId +
                ", comments='" + comments + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
