package co.uk.depotnet.onsa.modals.forms;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Screen implements Parcelable {

    private String title;
    private String url;
    private String photoUrl;

    private boolean upload;
    private int index;
    private ArrayList<FormItem> items;

    protected Screen(Parcel in) {
        title = in.readString();
        url = in.readString();
        photoUrl = in.readString();
        upload = in.readByte() != 0;
        index = in.readInt();
        items = in.createTypedArrayList(FormItem.CREATOR);
    }

    public static final Creator<Screen> CREATOR = new Creator<Screen>() {
        @Override
        public Screen createFromParcel(Parcel in) {
            return new Screen(in);
        }

        @Override
        public Screen[] newArray(int size) {
            return new Screen[size];
        }
    };

    public ArrayList<FormItem> getItems() {
        return items;
    }

    public String getTitle() {
        return title;
    }

    public String getUrl() {
        return url;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public boolean isUpload() {
        return upload;
    }

    public void setUpload(boolean upload) {
        this.upload = upload;
    }

    public void setItems(ArrayList<FormItem> items) {
        this.items = items;
    }



    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeString(url);
        parcel.writeString(photoUrl);
        parcel.writeByte((byte) (upload ? 1 : 0));
        parcel.writeInt(index);
        parcel.writeTypedList(items);
    }
}