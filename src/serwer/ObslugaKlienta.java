package serwer;

import common.*;
import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class ObslugaKlienta implements Runnable {
    private final Socket socket;
    private final BazaDanych baza;

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
        } catch (EOFException | SocketException e) {
        System.out.println("Klient rozłączony.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Komunikat procesujZapytanie(Komunikat req) {
        Komunikat resp = new Komunikat(TypKomunikatu.ODPOWIEDZ_OK);

        switch (req.typ) {
            case LOGIN:
                Uzytkownik u = baza.autentykuj(req.uzytkownik.getLogin(), req.uzytkownik.getHaslo());

                if (u != null) {
                    resp.setWiadomosc("Zalogowano pomyślnie");
                    resp.setUzytkownik(u);
                    u.setZalogowany(true);
                } else {
                    resp.setTyp(TypKomunikatu.ODPOWIEDZ_BLAD);
                    resp.setWiadomosc("Błędny login lub hasło");
                }
                break;

            case REJESTRACJA:
                if (baza.znajdzUzytkownika(req.uzytkownik.getLogin()) == null) {
                    req.uzytkownik.setRodzajKonta(RodzajKonta.UZYTKOWNIK);
                    baza.dodajUzytkownika(req.uzytkownik);
                    resp.setTyp(TypKomunikatu.REJESTRACJA);
                    resp.setWiadomosc("Konto zostało utworzone");
                } else {
                    resp.setTyp(TypKomunikatu.ODPOWIEDZ_BLAD);
                    resp.setWiadomosc("Ten login jest już zajęty");
                }
                break;

            case POBIERZ_UZYTKOWNIKOW:
                resp.setListaUzytkownikow(baza.getListaUzytkownikow());
                resp.setWiadomosc("Pobrano listę użytkowników");
                resp.setTyp(TypKomunikatu.ODPOWIEDZ_OK);
                break;

            case DODAJ_UZYTKOWNIKA:
                if (baza.znajdzUzytkownika(req.uzytkownik.getLogin()) == null) {
                    baza.dodajUzytkownika(req.uzytkownik);
                    resp.setWiadomosc("Dodano użytkownika");
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
                baza.dodajSzablon(req.getSzablon());
                resp.wiadomosc = "Dodano nowy szablon ankiety!";
                break;

            case EDYTUJ_SZABLON:
                req.getSzablon().oznaczModyfikacje();
                baza.edytujSzablon(req.getSzablon());
                resp.setWiadomosc("Edytowano szablon ankiety!");
                resp.setTyp(TypKomunikatu.EDYTUJ_SZABLON);
                break;

            case POBIERZ_SZABLON:
                String idSzukanego = req.getWiadomosc();
                SzablonAnkiety znaleziony = baza.getListaSzablonow().stream()
                        .filter(s -> s.getId().equals(idSzukanego))
                        .findFirst()
                        .orElse(null);

                resp.wiadomosc = "Pobrano szablon z serwera";
                resp.setSzablon(znaleziony);
                resp.setTyp(TypKomunikatu.ODPOWIEDZ_OK);
                break;

            case POBIERZ_SZABLONY:
                resp.setListaSzablonow(baza.getListaSzablonow());
                resp.setTyp(TypKomunikatu.ODPOWIEDZ_OK);
                resp.wiadomosc = "Pobrano szablony z serwera";
                break;


            case USUN_SZABLON:
                String idDoUsuniecia = req.getWiadomosc();
                if (baza.usunSzablon(idDoUsuniecia)) {
                    resp.setWiadomosc("Ankieta została usunięta.");
                } else {
                    resp.setTyp(TypKomunikatu.ODPOWIEDZ_BLAD);
                    resp.setWiadomosc("Nie znaleziono ankiety o podanym ID.");
                }
                break;

            case ZAPISZ_ANKIETE:
                Ankieta a = req.getAnkieta();
                String status = req.getWiadomosc();

                if (a != null) {
                    baza.zapiszLubAktualizujAnkiete(a);
                    resp.setWiadomosc("Zapisano status: " + status);
                } else {
                    resp.setTyp(TypKomunikatu.ODPOWIEDZ_BLAD);
                }
                break;

            case POBIERZ_ANKIETY:
                resp.setListaOdpowiedzi(baza.getAnkiety());
                resp.setTyp(TypKomunikatu.ODPOWIEDZ_OK);
                break;

            case SPRAWDZ_STATUS_ANKIETY:
                String idSzablonu = req.getWiadomosc();
                String user = req.getUzytkownik().getLogin();
                String statusAnkiety = baza.pobierzStatus(idSzablonu, user);
                resp.setWiadomosc(statusAnkiety);
                resp.setTyp(TypKomunikatu.ODPOWIEDZ_OK);
                break;

            case POBIERZ_AKTYWNA_ANKIETE:
                Ankieta aktywna = baza.getAktywnaAnkieta(
                        req.getWiadomosc(),
                        req.getUzytkownik().getLogin()
                );
                resp.setAnkieta(aktywna);
                break;

            case POBIERZ_ANKIETE_UZYTKOWNIKA:
                Ankieta ostatnia = baza.getOstatniaAnkieta(
                        req.getWiadomosc(),
                        req.getUzytkownik().getLogin()
                );
                resp.setAnkieta(ostatnia);
                resp.setTyp(TypKomunikatu.ODPOWIEDZ_OK);
                break;

            default:
                resp.setTyp(TypKomunikatu.ODPOWIEDZ_BLAD);
                resp.setWiadomosc("Nieobsługiwany request");
                break;

        }
        return resp;
    }
}