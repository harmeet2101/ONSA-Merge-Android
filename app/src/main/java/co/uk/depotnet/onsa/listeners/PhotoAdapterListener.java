package co.uk.depotnet.onsa.listeners;

import java.util.ArrayList;

import co.uk.depotnet.onsa.modals.forms.FormItem;
import co.uk.depotnet.onsa.modals.forms.Photo;

public interface PhotoAdapterListener{
    void openCamera(FormItem item, int repeatCounter);
        void onAllPhotosRemoved();
    }