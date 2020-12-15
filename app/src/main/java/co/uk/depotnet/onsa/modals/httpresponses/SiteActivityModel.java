package co.uk.depotnet.onsa.modals.httpresponses;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SiteActivityModel {

    @SerializedName("result")
    @Expose
    private List<BaseTask> result = null;

    public List<BaseTask> getResult() {
        return result;
    }

    public void setResult(List<BaseTask> result) {
        this.result = result;
    }

}