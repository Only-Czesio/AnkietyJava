package klient.widoki;

import common.*;
import klient.*;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class WidokAnkiet extends WidokBazowy {
    private final Klient klient;
    private final boolean czyAdmin;

    public WidokAnkiet(Klient klient, boolean czyAdmin) {
        super(new String[]{"ID", "Tytuł", "Status", "Rozpoczęcie", "Zakończenie"});
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
                Ankieta a = pobierzAnkieteUzytkownika(s.getId());

                modelTabeli.addRow(new Object[]{
                        s.getId(),
                        s.getTytul(),
                        status,
                        a != null ? format(a.getStartDate()) : "—",
                        a != null && a.getEndDate() != null ? format(a.getEndDate()) : "—"
                });

            }
        }
    }

    private Ankieta pobierzAnkieteUzytkownika(String idSzablonu) {
        Komunikat req = new Komunikat(TypKomunikatu.POBIERZ_ANKIETE_UZYTKOWNIKA);
        req.setWiadomosc(idSzablonu);
        req.setUzytkownik(klient.getZalogowanyUser());

        Komunikat resp = klient.wyslij(req);
        return resp.getAnkieta();
    }


    private String format(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }


    private String sprawdzStatusAnkiety(String idSzablonu) {
        Komunikat req = new Komunikat(TypKomunikatu.SPRAWDZ_STATUS_ANKIETY);
        req.setWiadomosc(idSzablonu);
        req.setUzytkownik(klient.getZalogowanyUser());
        Komunikat resp = klient.wyslij(req);
        return resp.getWiadomosc();
    }

    @Override
    protected void akcjaDodaj() {
        if (czyAdmin) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            KreatorAnkietyDialog dialog = new KreatorAnkietyDialog((Frame) parentWindow);
            dialog.setVisible(true);

            if (dialog.czyZatwierdzono()) {
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

            String status = (String) modelTabeli.getValueAt(tabela.getSelectedRow(), 2);

            if ("ZAKOŃCZONA".equals(status)) {
                JOptionPane.showMessageDialog(this, "Tę ankietę już wypełniłeś.");
                return;
            }

            Komunikat reqA = new Komunikat(TypKomunikatu.POBIERZ_AKTYWNA_ANKIETE);
            reqA.setWiadomosc(id);
            reqA.setUzytkownik(klient.getZalogowanyUser());
            Komunikat respA = klient.wyslij(reqA);

            Ankieta ankieta = respA.getAnkieta();
            OknoAnkiety okno = new OknoAnkiety(resp.getSzablon(), klient, ankieta);
            okno.setVisible(true);
        }
    }

    @Override
    protected void akcjaEdytuj() {
        if (!czyAdmin) return;

        String id = pobierzZaznaczoneId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Wybierz ankietę z tabeli!");
            return;
        }

        Komunikat req = new Komunikat(TypKomunikatu.POBIERZ_SZABLON);
        req.setWiadomosc(id);
        Komunikat resp = klient.wyslij(req);

        if (resp != null && resp.getSzablon() != null) {
            Window parentWindow = SwingUtilities.getWindowAncestor(this);
            KreatorAnkietyDialog dialog = new KreatorAnkietyDialog((Frame) parentWindow, resp.getSzablon());
            dialog.setVisible(true);

            if (dialog.czyZatwierdzono()) {
                Komunikat updateReq = new Komunikat(TypKomunikatu.EDYTUJ_SZABLON);
                updateReq.setSzablon(dialog.getSzablon());
                klient.wyslij(updateReq);
                odswiezDane();
            }
        }
    }

    @Override
    protected void akcjaUsun() {
        if (!czyAdmin) return;

        String id = pobierzZaznaczoneId();
        if (id == null) {
            JOptionPane.showMessageDialog(this, "Zaznacz coś do usunięcia!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Czy na pewno usunąć ankietę " + id + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            Komunikat req = new Komunikat(TypKomunikatu.USUN_SZABLON);
            req.setWiadomosc(id);
            klient.wyslij(req);
            odswiezDane();
        }
    }
}
