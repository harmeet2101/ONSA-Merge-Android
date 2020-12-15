package co.uk.depotnet.onsa.listeners;

import co.uk.depotnet.onsa.modals.actions.ActionsClose;

public interface ActionsListner {
    void StartCorrectiveMeasure(ActionsClose action);
    void StartCannotRectify(ActionsClose action);
}
