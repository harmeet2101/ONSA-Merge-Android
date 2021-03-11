package co.uk.depotnet.onsa.listeners;

import co.uk.depotnet.onsa.modals.actions.Action;

public interface ActionsListener {
    void startCorrectiveMeasure(Action action);
    void startCannotRectify(Action action);
}
