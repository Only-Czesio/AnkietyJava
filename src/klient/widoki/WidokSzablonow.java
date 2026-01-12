package klient.widoki;

import common.*;
import klient.*;
import javax.swing.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WidokSzablonow extends WidokBazowy {
    private Klient klient;

    public WidokSzablonow(Klient klient) {
        super(new String[]{
                "ID", "Tytuł", "Liczba pytań", "Utworzono", "Zmodyfikowano"
        });
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
                        s.getPytania().size(),
                        s.getDataUtworzenia() != null
                                ? format(s.getDataUtworzenia())
                                : "—",
                        s.getDataModyfikacji() != null
                                ? format(s.getDataModyfikacji())
                                : "—"
                });

            }
        }
    }

    private String format(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    @Override
    protected void akcjaDodaj() {
        KreatorAnkietyDialog dialog = new KreatorAnkietyDialog((JFrame) SwingUtilities.getWindowAncestor(this));
        dialog.setVisible(true);

        if (dialog.czyZatwierdzono()) {
            SzablonAnkiety nowy = dialog.getSzablon();
            Komunikat req = new Komunikat(TypKomunikatu.DODAJ_SZABLON);
            req.setSzablon(nowy);

            klient.wyslij(req);
            odswiezDane();
        }
    }

    @Override
    protected void akcjaUsun() {
        String id = pobierzZaznaczoneId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Zaznacz ankietę do usunięcia!");
            return;
        }

        String tytul = (String) modelTabeli.getValueAt(
                tabela.getSelectedRow(), 1);

        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Czy na pewno chcesz usunąć ankietę: " + tytul + "?",
                "Potwierdzenie",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            Komunikat req = new Komunikat(TypKomunikatu.USUN_SZABLON);
            req.setWiadomosc(id);

            klient.wyslij(req);
            odswiezDane();
        }
    }

    @Override
    protected void akcjaEdytuj() {
        String id = pobierzZaznaczoneId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Zaznacz ankietę do edycji!");
            return;
        }

        Komunikat req = new Komunikat(TypKomunikatu.POBIERZ_SZABLON);
        req.setWiadomosc(id);
        Komunikat resp = klient.wyslij(req);

        if (resp != null && resp.getSzablon() != null) {
            JFrame parent = (JFrame) SwingUtilities.getWindowAncestor(this);
            KreatorAnkietyDialog dialog =
                    new KreatorAnkietyDialog(parent, resp.getSzablon());
            dialog.setVisible(true);

            if (dialog.czyZatwierdzono()) {
                Komunikat update = new Komunikat(TypKomunikatu.EDYTUJ_SZABLON);
                update.setSzablon(dialog.getSzablon());
                klient.wyslij(update);
                odswiezDane();
            }
        }
    }

}