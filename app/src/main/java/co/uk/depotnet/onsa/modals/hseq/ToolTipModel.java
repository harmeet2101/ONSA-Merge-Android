package co.uk.depotnet.onsa.modals.hseq;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ToolTipModel implements Parcelable {
    @SerializedName("questionId")
    @Expose
    private String questionId;
    @SerializedName("questionText")
    @Expose
    private String questionText;
    @SerializedName("tooltip")
    @Expose
    private String tooltip;
    @SerializedName("questionIsMandatory")
    @Expose
    private Boolean questionIsMandatory;
    @SerializedName("commentIsMandatory")
    @Expose
    private Boolean commentIsMandatory;
    @SerializedName("minimumPhotoCount")
    @Expose
    private Integer minimumPhotoCount;

    public ToolTipModel() {
    }


    protected ToolTipModel(Parcel in) {
        questionId = in.readString();
        questionText = in.readString();
        tooltip = in.readString();
        byte tmpQuestionIsMandatory = in.readByte();
        questionIsMandatory = tmpQuestionIsMandatory == 0 ? null : tmpQuestionIsMandatory == 1;
        byte tmpCommentIsMandatory = in.readByte();
        commentIsMandatory = tmpCommentIsMandatory == 0 ? null : tmpCommentIsMandatory == 1;
        if (in.readByte() == 0) {
            minimumPhotoCount = null;
        } else {
            minimumPhotoCount = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(questionId);
        dest.writeString(questionText);
        dest.writeString(tooltip);
        dest.writeByte((byte) (questionIsMandatory == null ? 0 : questionIsMandatory ? 1 : 2));
        dest.writeByte((byte) (commentIsMandatory == null ? 0 : commentIsMandatory ? 1 : 2));
        if (minimumPhotoCount == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(minimumPhotoCount);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ToolTipModel> CREATOR = new Creator<ToolTipModel>() {
        @Override
        public ToolTipModel createFromParcel(Parcel in) {
            return new ToolTipModel(in);
        }

        @Override
        public ToolTipModel[] newArray(int size) {
            return new ToolTipModel[size];
        }
    };

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getTooltip() {
        return tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

    public Boolean getQuestionIsMandatory() {
        return questionIsMandatory;
    }

    public Boolean getCommentIsMandatory() {
        return commentIsMandatory;
    }

    public void setQuestionIsMandatory(Boolean questionIsMandatory) {
        this.questionIsMandatory = questionIsMandatory;
    }

    public Integer getMinimumPhotoCount() {
        return minimumPhotoCount;
    }

    public void setMinimumPhotoCount(Integer minimumPhotoCount) {
        this.minimumPhotoCount = minimumPhotoCount;
    }
}
