package co.uk.depotnet.onsa.modals.notify;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotifyModel implements Parcelable {
    @SerializedName("notificationId")
    @Expose
    private String notificationId;
    @SerializedName("hasBeenRead")
    @Expose
    private Boolean hasBeenRead;
    @SerializedName("hasBeenPushed")
    @Expose
    private Boolean hasBeenPushed;
    @SerializedName("notificationText")
    @Expose
    private String notificationText;
    @SerializedName("notificationType")
    @Expose
    private Integer notificationType;

    public NotifyModel() {
    }

    protected NotifyModel(Parcel in) {
        notificationId = in.readString();
        byte tmpHasBeenRead = in.readByte();
        hasBeenRead = tmpHasBeenRead == 0 ? null : tmpHasBeenRead == 1;
        byte tmpHasBeenPushed = in.readByte();
        hasBeenPushed = tmpHasBeenPushed == 0 ? null : tmpHasBeenPushed == 1;
        notificationText = in.readString();
        if (in.readByte() == 0) {
            notificationType = null;
        } else {
            notificationType = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(notificationId);
        dest.writeByte((byte) (hasBeenRead == null ? 0 : hasBeenRead ? 1 : 2));
        dest.writeByte((byte) (hasBeenPushed == null ? 0 : hasBeenPushed ? 1 : 2));
        dest.writeString(notificationText);
        if (notificationType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(notificationType);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotifyModel> CREATOR = new Creator<NotifyModel>() {
        @Override
        public NotifyModel createFromParcel(Parcel in) {
            return new NotifyModel(in);
        }

        @Override
        public NotifyModel[] newArray(int size) {
            return new NotifyModel[size];
        }
    };

    public String getNotificationId() {
        return notificationId;
    }

    public void setNotificationId(String notificationId) {
        this.notificationId = notificationId;
    }

    public Boolean getHasBeenRead() {
        return hasBeenRead;
    }

    public void setHasBeenRead(Boolean hasBeenRead) {
        this.hasBeenRead = hasBeenRead;
    }

    public Boolean getHasBeenPushed() {
        return hasBeenPushed;
    }

    public void setHasBeenPushed(Boolean hasBeenPushed) {
        this.hasBeenPushed = hasBeenPushed;
    }

    public String getNotificationText() {
        return notificationText;
    }

    public void setNotificationText(String notificationText) {
        this.notificationText = notificationText;
    }

    public Integer getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(Integer notificationType) {
        this.notificationType = notificationType;
    }

    public NotifyModel(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            notificationId = cursor.getString(cursor.getColumnIndex(DBTable.notificationId));
            hasBeenRead = cursor.getInt(cursor.getColumnIndex(DBTable.hasBeenRead)) != 0;
            hasBeenPushed = cursor.getInt(cursor.getColumnIndex(DBTable.hasBeenPushed)) != 0;
            notificationText = cursor.getString(cursor.getColumnIndex(DBTable.notificationText));
            notificationType = cursor.getInt(cursor.getColumnIndex(DBTable.notificationType));
        }
    }
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        cv.put(DBTable.notificationId, notificationId);
        cv.put(DBTable.hasBeenRead, hasBeenRead);
        cv.put(DBTable.hasBeenPushed, hasBeenPushed);
        cv.put(DBTable.notificationText, notificationText);
        cv.put(DBTable.notificationText, notificationText);
        cv.put(DBTable.notificationType, notificationType);

        return cv;
    }
    public static class DBTable {
        public static final String NAME = "Notification";
        public static final String notificationId = "notificationId";
        public static final String hasBeenRead = "hasBeenRead";
        public static final String hasBeenPushed = "hasBeenPushed";
        public static final String notificationText = "notificationText";
        public static final String notificationType = "notificationType";
    }

    @Override
    public String toString() {
        return "NotifyModel{" +
                "notificationId='" + notificationId + '\'' +
                ", hasBeenRead=" + hasBeenRead +
                ", hasBeenPushed=" + hasBeenPushed +
                ", notificationText='" + notificationText + '\'' +
                ", notificationType=" + notificationType +
                '}';
    }
}
