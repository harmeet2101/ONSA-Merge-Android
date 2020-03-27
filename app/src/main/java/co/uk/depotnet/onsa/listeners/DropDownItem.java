package co.uk.depotnet.onsa.listeners;

import android.os.Parcelable;

public interface DropDownItem extends Parcelable {

    String getDisplayItem();
    String getUploadValue();

}
