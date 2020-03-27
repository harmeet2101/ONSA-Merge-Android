package co.uk.depotnet.onsa.modals.forms;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    private String title;
    private String photo_id;
    private boolean optional;
    private boolean isVideo;
    private String url;

    protected Photo(Parcel in) {
        title = in.readString();
        photo_id = in.readString();
        optional = in.readByte() != 0;
        url = in.readString();
        isVideo = in.readByte() == 1;
    }

    public Photo(){

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(photo_id);
        dest.writeByte((byte) (optional ? 1 : 0));
        dest.writeString(url);
        dest.writeByte((byte) (isVideo ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Photo> CREATOR = new Creator<Photo>() {
        @Override
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        @Override
        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPhoto_id() {
        return photo_id;
    }

    public void setPhoto_id(String photo_id) {
        this.photo_id = photo_id;
    }

    public boolean isOptional() {
        return optional;
    }

    public void setOptional(boolean optional) {
        this.optional = optional;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isVideo() {
        return isVideo;
    }

    public void setIsVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }

}
