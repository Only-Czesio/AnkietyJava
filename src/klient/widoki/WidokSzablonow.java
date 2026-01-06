package klient.widoki;

import common.*;
import klient.*;
import javax.swing.*;

public class WidokSzablonow extends WidokBazowy {
    private Klient klient;

    public WidokSzablonow(Klient klient) {
        super(new String[]{"ID", "Tytuł", "Liczba pytań"});
        this.klient = klient;
        odswiezDane();
    }

    @Override
    protected void odswiezDane() {
        Komunikat resp = klient.wyslij(new Komunikat(TypKomunikatu.POBIERZ_SZABLONY));
        modelTabeli.setRowCount(0);
        if (resp != null && resp.getListaSzablonow() != null) {
            for (SzablonAnkiety s : resp.getListaSzablonow()) {
                modelTabeli.addRow(new Object[]{
                        s.getId(),
                        s.getTytul(),
                        s.getPytania().size()
                });
            }
        }
    }

    @Override
    protected void akcjaDodaj() {
        KreatorAnkietyDialog dialog = new KreatorAnkietyDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.isZatwierdzono()) {
            SzablonAnkiety nowy = dialog.getSzablon();
            Komunikat req = new Komunikat(TypKomunikatu.DODAJ_SZABLON);
            req.setSzablon(nowy); // Używamy pola 'szablon'

            klient.wyslij(req);
            odswiezDane();
        }
    }

    @Override
    protected void akcjaUsun() {
        String id = pobierzZaznaczoneId();
        if (id == null) return;

        int confirm = JOptionPane.showConfirmDialog(this, "Usunąć ankietę " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            Komunikat req = new Komunikat(TypKomunikatu.USUN_SZABLON);
            req.setWiadomosc(id); // Przesyłamy ID do usunięcia
            klient.wyslij(req);
            odswiezDane();
        }
    }

    @Override
    protected void akcjaEdytuj() {
        JOptionPane.showMessageDialog(this, "Edycja szablonów w przygotowaniu.");
    }
}