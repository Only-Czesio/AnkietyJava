package klient.widoki;

import klient.*;
import javax.swing.*;

public class WidokUzytkownikow extends WidokBazowy {

    private Klient klient;

    public WidokUzytkownikow(Klient klient) {
        super(new String[]{"Login", "Hasło", "Czy Admin"});
        this.klient = klient;
        odswiezDane();
    }

    @Override
    protected void odswiezDane() {
        // Tu logika wysłania zapytania do serwera i wypełnienia modelTabeli
        // np. Komunikat resp = klient.wyslij(new Komunikat(TypKomunikatu.POBIERZ_UZYTKOWNIKOW));
        // modelTabeli.setRowCount(0);
        // pętla po resp.listaUzytkownikow -> modelTabeli.addRow(...)
        System.out.println("Odświeżam listę użytkowników...");
    }

    @Override
    protected void akcjaDodaj() {
        // Tu JDialog z polami login/hasło
        System.out.println("Dodawanie użytkownika...");
    }

    @Override
    protected void akcjaEdytuj() {
        String login = pobierzZaznaczoneId();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "Zaznacz wiersz!");
            return;
        }
        System.out.println("Edycja użytkownika: " + login);
    }

    @Override
    protected void akcjaUsun() {
        String login = pobierzZaznaczoneId();
        // Logika usuwania...
    }
}
