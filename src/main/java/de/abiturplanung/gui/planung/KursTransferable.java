package de.abiturplanung.gui.planung;

import de.abiturplanung.model.Kurs;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;

public class KursTransferable implements Transferable {
    private final Kurs kurs;

    public static final DataFlavor KURS_FLAVOR;
    //Hier definieren wir den Datentyp, der transferiert werden soll; und zwar ausschließlich innerhalb der aktuellen JVM.
    static {
        try {
            KURS_FLAVOR = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class=" + Kurs.class.getName());
        } catch (ClassNotFoundException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    public KursTransferable(Kurs kurs) {
        this.kurs = kurs;
    }

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{KURS_FLAVOR};
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return KURS_FLAVOR.equals(flavor);
    }

    @Override
    public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (!isDataFlavorSupported(flavor)) throw new UnsupportedFlavorException(flavor);
        return kurs;
    }
}
