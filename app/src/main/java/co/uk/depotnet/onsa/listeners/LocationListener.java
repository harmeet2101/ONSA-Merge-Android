package co.uk.depotnet.onsa.listeners;

import android.location.Location;

public interface LocationListener {

    void onSuccess(Location location);
    void onFailure();
}
