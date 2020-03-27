package co.uk.depotnet.onsa.modals;

import android.os.Parcel;
import android.os.Parcelable;

public class Disclaimer implements Parcelable {

    private String disclaimerText;


    protected Disclaimer(Parcel in) {
        disclaimerText = in.readString();
    }

    public static final Creator<Disclaimer> CREATOR = new Creator<Disclaimer>() {
        @Override
        public Disclaimer createFromParcel(Parcel in) {
            return new Disclaimer(in);
        }

        @Override
        public Disclaimer[] newArray(int size) {
            return new Disclaimer[size];
        }
    };

    public String getDisclaimerText() {
        return disclaimerText;
    }

    public void setDisclaimerText(String disclaimerText) {
        this.disclaimerText = disclaimerText;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(disclaimerText);
    }
}
