package co.uk.depotnet.onsa.modals;
import android.text.TextUtils;

import java.io.Serializable;

public class JobDetailItem implements Serializable {
    private String title;
    private String value;
    public JobDetailItem(){

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        if(TextUtils.isEmpty(value)){
            this.value = "N/A";
        }else {
            this.value = value;
        }
    }
}
