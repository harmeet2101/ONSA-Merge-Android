package co.uk.depotnet.onsa.modals.hseq;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

public class PhotoTags implements Parcelable {
    private int tagId;
    private int answerId;
    private String tagName;

    public PhotoTags() {
    }

    public PhotoTags(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            tagId = cursor.getInt(cursor.getColumnIndex(PhotoTags.DBTable.tagId));
            answerId = cursor.getInt(cursor.getColumnIndex(PhotoTags.DBTable.answerId));
            tagName = cursor.getString(cursor.getColumnIndex(PhotoTags.DBTable.tagName));
        }
    }
    protected PhotoTags(Parcel in) {
        tagId = in.readInt();
        answerId = in.readInt();
        tagName = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(tagId);
        dest.writeInt(answerId);
        dest.writeString(tagName);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoTags> CREATOR = new Creator<PhotoTags>() {
        @Override
        public PhotoTags createFromParcel(Parcel in) {
            return new PhotoTags(in);
        }

        @Override
        public PhotoTags[] newArray(int size) {
            return new PhotoTags[size];
        }
    };

    public int getTagId() {
        return tagId;
    }

    public void setTagId(int tagId) {
        this.tagId = tagId;
    }

    public int getAnswerId() {
        return answerId;
    }

    public void setAnswerId(int answerId) {
        this.answerId = answerId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }
    public static class DBTable {
        public static final String NAME = "PhotoTags";
        public static final String tagId = "tagId";
        public static final String answerId = "answerId";
        public static final String tagName = "tagName";
    }
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();

        if (tagId > 0) {
            cv.put(PhotoTags.DBTable.tagId, tagId);
        }

        cv.put(PhotoTags.DBTable.answerId, answerId);
        cv.put(PhotoTags.DBTable.tagName, tagName);

        return cv;
    }

    @Override
    public String toString() {
        return "PhotoTags{" +
                "tagId=" + tagId +
                ", answerId=" + answerId +
                ", tagName='" + tagName + '\'' +
                '}';
    }
}
