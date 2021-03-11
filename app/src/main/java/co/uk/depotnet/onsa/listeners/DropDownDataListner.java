package co.uk.depotnet.onsa.listeners;

import java.util.ArrayList;

public interface DropDownDataListner {

    void success(ArrayList<DropDownItem> items , boolean isDependOnDatasetEndpoint);
    void showValidationDialog(String title , String message);
}
