package co.uk.depotnet.onsa.modals.forms;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Amends implements Parcelable {

    ArrayList<FormItem> dialogItems;

    protected Amends(Parcel in) {
        dialogItems = in.createTypedArrayList(FormItem.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(dialogItems);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Amends> CREATOR = new Creator<Amends>() {
        @Override
        public Amends createFromParcel(Parcel in) {
            return new Amends(in);
        }

        @Override
        public Amends[] newArray(int size) {
            return new Amends[size];
        }
    };

    public ArrayList<FormItem> getDialogItems() {
        return dialogItems;
    }
}
