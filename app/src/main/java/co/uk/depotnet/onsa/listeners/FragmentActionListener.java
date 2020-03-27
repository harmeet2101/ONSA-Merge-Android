package co.uk.depotnet.onsa.listeners;

import androidx.fragment.app.Fragment;

public interface FragmentActionListener {
    void addFragment(Fragment fragment, boolean isHorizontalAnim);
    void setTitle(String title);
    void onFragmentHomeVisible(boolean isVisible);
    void showProgressBar();
    void hideProgressBar();
    void setReceiptsBadge(String number);
}
