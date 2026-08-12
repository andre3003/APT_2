package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Pruefung;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class PruefungTransferable implements Transferable {

    public static final DataFlavor PRUEFUNG_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Pruefung.class.getName(), "Prüfung");

    private final Pruefung pruefung;

    public PruefungTransferable(Pruefung pruefung) {
        this.pruefung = pruefung;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{PRUEFUNG_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return PRUEFUNG_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
        if (!isDataFlavorSupported(flavor)) {
            throw new UnsupportedFlavorException(flavor);
        }

        return pruefung;
    }
}