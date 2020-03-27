package co.uk.depotnet.onsa.listeners;

import java.io.Serializable;

public interface ColorListener extends Serializable {
    void colorSelected(String colorCode);
}
