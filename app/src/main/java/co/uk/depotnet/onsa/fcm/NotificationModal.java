package co.uk.depotnet.onsa.fcm;

import android.content.ContentValues;
import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ABHIKR on 08/08/2020.
 */
public class NotificationModal implements Parcelable {
    private String title;
    private String message;
    private String iconUrl;
    private String action;
    private String actionDestination;

    public NotificationModal() {
    }
    public NotificationModal(Cursor cursor) {
        if (cursor != null && !cursor.isBeforeFirst() && !cursor.isAfterLast()) {
            title = cursor.getString(cursor.getColumnIndex(DBTable.title));
            message = cursor.getString(cursor.getColumnIndex(DBTable.message));
        }
    }
    protected NotificationModal(Parcel in) {
        title = in.readString();
        message = in.readString();
        iconUrl = in.readString();
        action = in.readString();
        actionDestination = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(message);
        dest.writeString(iconUrl);
        dest.writeString(action);
        dest.writeString(actionDestination);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<NotificationModal> CREATOR = new Creator<NotificationModal>() {
        @Override
        public NotificationModal createFromParcel(Parcel in) {
            return new NotificationModal(in);
        }

        @Override
        public NotificationModal[] newArray(int size) {
            return new NotificationModal[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getActionDestination() {
        return actionDestination;
    }

    public void setActionDestination(String actionDestination) {
        this.actionDestination = actionDestination;
    }
    public ContentValues toContentValues() {
        ContentValues cv = new ContentValues();
        //DBHandler dbHandler = DBHandler.getInstance();
        cv.put(DBTable.title, title);
        cv.put(DBTable.message, message);
        //if (title != null && message != null)
          //  dbHandler.replaceData(NotificationModal.DBTable.NAME, cv.toContentValues());

        return cv;
    }
    public static class DBTable {
        public static final String NAME = "Notification";
        public static final String Id = "id";
        public static final String title = "title";
        public static final String message = "message";
    }
}
