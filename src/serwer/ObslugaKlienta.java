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
                out.reset();
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
                // Używamy nowej metody autentykuj, która wewnątrz sprawdza Hash
                Uzytkownik u = baza.autentykuj(req.uzytkownik.getLogin(), req.uzytkownik.getHaslo());

                if (u != null) {
                    resp.wiadomosc = "Zalogowano pomyślnie";
                    resp.uzytkownik = u;
                } else {
                    resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                    resp.wiadomosc = "Błędny login lub hasło";
                }
                break;

            case REJESTRACJA:
                if (baza.znajdzUzytkownika(req.uzytkownik.getLogin()) == null) {
                    // Rola ustawiana automatycznie dla rejestrujących się
                    req.uzytkownik.setRodzajKonta(RodzajKonta.UZYTKOWNIK);

                    // Metoda dodajUzytkownika w klasie BazaDanych powinna
                    // teraz sama wywołać Bezpieczenstwo.hashujHaslo()
                    baza.dodajUzytkownika(req.uzytkownik);

                    resp.wiadomosc = "Konto zostało utworzone";
                } else {
                    resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                    resp.wiadomosc = "Ten login jest już zajęty";
                }
                break;

            case POBIERZ_UZYTKOWNIKOW:
                resp.setListaUzytkownikow(baza.getListaUzytkownikow());
                break;

            case DODAJ_UZYTKOWNIKA:
                if (baza.znajdzUzytkownika(req.uzytkownik.getLogin()) == null) {
                    baza.dodajUzytkownika(req.uzytkownik);
                    resp.wiadomosc = "Dodano użytkownika";
                } else {
                    resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                    resp.wiadomosc = "Użytkownik o takim loginie już istnieje!";
                }
                break;

            case EDYTUJ_UZYTKOWNIKA:
                if (baza.edytujUzytkownika(req.uzytkownik)) {
                    resp.wiadomosc = "Zaktualizowano dane";
                } else {
                    resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                    resp.wiadomosc = "Nie udało się edytować użytkownika";
                }
                break;

            case USUN_UZYTKOWNIKA:
                if (baza.usunUzytkownika(req.uzytkownik.getLogin())) {
                    resp.wiadomosc = "Usunięto pomyślnie";
                } else {
                    resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                    resp.wiadomosc = "Błąd usuwania";
                }
                break;

            case DODAJ_SZABLON:
                // Pobieramy szablon z pola szablon w komunikacie
                baza.dodajSzablon(req.getSzablon());
                resp.wiadomosc = "Dodano nowy szablon ankiety!";
                break;

            case POBIERZ_SZABLONY:
                // Serwer pakuje listę wszystkich szablonów do komunikatu zwrotnego
                resp.setListaSzablonow(baza.getListaSzablonow());
                break;

            case USUN_SZABLON:
                // Pobieramy ID, które klient wpisał w pole wiadomosc
                String idDoUsuniecia = req.getWiadomosc();
                if (baza.usunSzablon(idDoUsuniecia)) {
                    resp.setWiadomosc("Ankieta została usunięta.");
                } else {
                    resp.setTyp(TypKomunikatu.ODPOWIEDZ_BLAD);
                    resp.setWiadomosc("Nie znaleziono ankiety o podanym ID.");
                }
                break;
        }
        return resp;
    }
}