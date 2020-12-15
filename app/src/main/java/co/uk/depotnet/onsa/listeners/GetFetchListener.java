package co.uk.depotnet.onsa.listeners;

import com.tonyodev.fetch2.Fetch;

public interface GetFetchListener {
    Fetch getFetch();
    void openKitbagFolder(int parentId);
}
