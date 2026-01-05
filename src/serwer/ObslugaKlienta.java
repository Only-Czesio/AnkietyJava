package serwer;

import common.*;
import java.io.*;
import java.net.Socket;

public class ObslugaKlienta implements Runnable {
    private Socket socket;
    private BazaDanych baza;

    public ObslugaKlienta(Socket socket, BazaDanych baza) {
        this.socket = socket;
        this.baza = baza;
    }

    @Override
    public void run() {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                Komunikat req = (Komunikat) in.readObject();
                Komunikat resp = procesujZapytanie(req);
                out.writeObject(resp);
                out.flush();
            }
        } catch (EOFException e) {
            System.out.println("Klient rozłączony.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Komunikat procesujZapytanie(Komunikat req) {
        Komunikat resp = new Komunikat(TypKomunikatu.ODPOWIEDZ_OK);

        switch (req.typ) {
            case LOGIN:
                Uzytkownik u = baza.znajdzUzytkownika(req.uzytkownik.login);
                if (u != null && u.haslo.equals(req.uzytkownik.haslo)) {
                    resp.wiadomosc = "Zalogowano";
                    resp.uzytkownik = u;
                } else {
                    resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                    resp.wiadomosc = "Błąd logowania";
                }
                break;

            case POBIERZ_UZYTKOWNIKOW:
                resp.listaUzytkownikow = baza.getListaUzytkownikow();
                break;

            case REJESTRACJA:
                if (baza.znajdzUzytkownika(req.uzytkownik.login) == null) {
                    req.uzytkownik.rodzajKonta = RodzajKonta.UZYTKOWNIK;
                    baza.dodajUzytkownika(req.uzytkownik);
                    resp.wiadomosc = "Zarejestrowano";
                } else {
                    resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                    resp.wiadomosc = "Login zajęty";
                }
                break;

            // ... reszta case'ów (USUN, EDYTUJ) analogicznie korzystając z metod klasy BazaDanych
        }
        return resp;
    }
}