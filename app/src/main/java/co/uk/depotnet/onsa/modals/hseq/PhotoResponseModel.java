package co.uk.depotnet.onsa.modals.hseq;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PhotoResponseModel implements Parcelable {
    @SerializedName("photos")
    @Expose
    private List<PhotoResponse> photos = null;

    public PhotoResponseModel() {
    }

    protected PhotoResponseModel(Parcel in) {
        photos = in.createTypedArrayList(PhotoResponse.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(photos);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoResponseModel> CREATOR = new Creator<PhotoResponseModel>() {
        @Override
        public PhotoResponseModel createFromParcel(Parcel in) {
            return new PhotoResponseModel(in);
        }

        @Override
        public PhotoResponseModel[] newArray(int size) {
            return new PhotoResponseModel[size];
        }
    };

    public List<PhotoResponse> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotoResponse> photos) {
        this.photos = photos;
    }
}
