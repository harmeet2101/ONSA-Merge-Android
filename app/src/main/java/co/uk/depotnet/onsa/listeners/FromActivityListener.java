package co.uk.depotnet.onsa.listeners;

import androidx.fragment.app.Fragment;

import co.uk.depotnet.onsa.modals.forms.Screen;

public interface FromActivityListener {
    void addFragment(Fragment fragment);
    void goToNextScreen(int currentScreen);
    void onChangeChamberCount(int chamberCount);
    void setTitle(String title);
    void popFragmentImmediate();
    void onScreenChange(Screen screen);
    void showProgressBar();
    void hideProgressBar();
    void showBtnContainer(boolean isVisible);
}
