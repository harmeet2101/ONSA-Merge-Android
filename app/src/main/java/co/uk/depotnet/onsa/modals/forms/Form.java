package co.uk.depotnet.onsa.modals.forms;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Form implements Parcelable {

    private String title;
    private ArrayList<Screen> screens;
    private boolean isProgressVisible;

    protected Form(Parcel in) {
        title = in.readString();
        screens = in.createTypedArrayList(Screen.CREATOR);
        isProgressVisible = in.readByte() != 0;
    }

    public static final Creator<Form> CREATOR = new Creator<Form>() {
        @Override
        public Form createFromParcel(Parcel in) {
            return new Form(in);
        }

        @Override
        public Form[] newArray(int size) {
            return new Form[size];
        }
    };

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<Screen> getScreens() {
        return screens;
    }

    public void setScreens(ArrayList<Screen> screens) {
        this.screens = screens;
    }

    public boolean isProgressVisible() {
        return isProgressVisible;
    }

    public void setProgressVisible(boolean progressVisible) {
        isProgressVisible = progressVisible;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(title);
        parcel.writeTypedList(screens);
        parcel.writeByte((byte) (isProgressVisible ? 1 : 0));
    }
}
