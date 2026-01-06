package klient.widoki;

import common.*;
import klient.*;

import javax.swing.*;
import java.awt.*;

public class WidokAnkiet extends WidokBazowy {
    private final Klient klient;
    private final boolean czyAdmin;

    public WidokAnkiet(Klient klient, boolean czyAdmin) {
        super(new String[]{"ID", "Tytuł", "Status"});
        this.klient = klient;
        this.czyAdmin = czyAdmin;
        odswiezDane();

        if (!czyAdmin) {
            getBtnUsun().setVisible(false);
            getBtnEdytuj().setVisible(false);
        }
        getBtnDodaj().setText(czyAdmin ? "Stwórz nową" : "Wypełnij wybraną");
    }

    @Override
    protected void odswiezDane() {
        Komunikat resp = klient.wyslij(new Komunikat(TypKomunikatu.POBIERZ_SZABLONY));
        modelTabeli.setRowCount(0);

        if (resp != null && resp.getListaSzablonow() != null) {
            for (SzablonAnkiety s : resp.getListaSzablonow()) {
                String status = sprawdzStatusAnkiety(s.getId());
                modelTabeli.addRow(new Object[]{s.getId(), s.getTytul(), status});
            }
        }
    }

    private String sprawdzStatusAnkiety(String idSzablonu) {
        Komunikat req = new Komunikat(TypKomunikatu.SPRAWDZ_STATUS_ANKIETY);
        req.setWiadomosc(idSzablonu);
        Komunikat resp = klient.wyslij(req);
        return resp.getWiadomosc();
    }

    @Override
    protected void akcjaDodaj() {
        if (czyAdmin) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            KreatorAnkietyDialog dialog = new KreatorAnkietyDialog((Frame) parentWindow);
            dialog.setVisible(true);

            if (dialog.isZatwierdzono()) {
                SzablonAnkiety nowySzablon = dialog.getSzablon();
                Komunikat req = new Komunikat(TypKomunikatu.DODAJ_SZABLON);
                req.setSzablon(nowySzablon);

                Komunikat resp = klient.wyslij(req);
                if (resp != null && resp.getTyp() == TypKomunikatu.ODPOWIEDZ_OK) {
                    JOptionPane.showMessageDialog(this, "Dodano pomyślnie!");
                }
                odswiezDane();
            }
        } else {
            String id = pobierzZaznaczoneId();
            if (id == null) {
                JOptionPane.showMessageDialog(this, "Najpierw zaznacz ankietę w tabeli!");
                return;
            }

            Komunikat req = new Komunikat(TypKomunikatu.POBIERZ_SZABLON);
            req.setWiadomosc(id);
            Komunikat resp = klient.wyslij(req);

            if (resp != null && resp.getSzablon() != null) {
                OknoAnkiety okno = new OknoAnkiety(resp.getSzablon(), klient);
                okno.setVisible(true);
                odswiezDane();
            }
        }
    }

    @Override
    protected void akcjaEdytuj() {

    }

    @Override
    protected void akcjaUsun() {

    }
}
