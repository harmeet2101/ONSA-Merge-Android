package co.uk.depotnet.onsa.modals.hseq;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class PhotoResponse implements Parcelable
{
    private String fileName;
    private String fileBytes;
    private List<String> tags = null;
    private List<String> comments = null;
    private double latitude;
    private double longitude;
    private String dateTimeTaken;
    private Integer photoTypeId;

    public PhotoResponse() {
    }

    protected PhotoResponse(Parcel in) {
        fileName = in.readString();
        fileBytes = in.readString();
        comments = in.createStringArrayList();
        tags = in.createStringArrayList();
        latitude = in.readDouble();
        longitude = in.readDouble();
        dateTimeTaken = in.readString();
        if (in.readByte() == 0) {
            photoTypeId = null;
        } else {
            photoTypeId = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(fileName);
        dest.writeString(fileBytes);
        dest.writeStringList(comments);
        dest.writeStringList(tags);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(dateTimeTaken);
        if (photoTypeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(photoTypeId);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PhotoResponse> CREATOR = new Creator<PhotoResponse>() {
        @Override
        public PhotoResponse createFromParcel(Parcel in) {
            return new PhotoResponse(in);
        }

        @Override
        public PhotoResponse[] newArray(int size) {
            return new PhotoResponse[size];
        }
    };

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileBytes() {
        return fileBytes;
    }

    public void setFileBytes(String fileBytes) {
        this.fileBytes = fileBytes;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getDateTimeTaken() {
        return dateTimeTaken;
    }

    public void setDateTimeTaken(String dateTimeTaken) {
        this.dateTimeTaken = dateTimeTaken;
    }

    public Integer getPhotoTypeId() {
        return photoTypeId;
    }

    public void setPhotoTypeId(Integer photoTypeId) {
        this.photoTypeId = photoTypeId;
    }
}
