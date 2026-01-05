package serwer;

import common.*;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.*;

public class Serwer {
    private static final int PORT = 5000;
    private static final String PLIK_BAZY = "baza_danych.dat";

    private static List<Uzytkownik> listaUzytkownikow = new ArrayList<>();

    public static void main(String[] args) {
        wczytajBazeZPliku();
        System.out.println("SERWER: Start na porcie " + PORT);

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                new Thread(() -> obslugaKlienta(clientSocket)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static synchronized void wczytajBazeZPliku() {
        File plik = new File(PLIK_BAZY);
        if (!plik.exists()) {
            System.out.println("SERWER: Brak pliku bazy. Tworzę domyślnego admina.");
            listaUzytkownikow.add(new Uzytkownik("admin", "admin", RodzajKonta.ADMIN));
            zapiszBazeDoPliku();
            return;
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(plik))) {
            listaUzytkownikow = (List<Uzytkownik>) ois.readObject();
            System.out.println("SERWER: Wczytano " + listaUzytkownikow.size() + " użytkowników.");
        } catch (Exception e) {
            System.err.println("SERWER: Błąd wczytywania bazy: " + e.getMessage());
        }
    }

    private static synchronized void zapiszBazeDoPliku() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PLIK_BAZY))) {
            oos.writeObject(listaUzytkownikow);
            System.out.println("SERWER: Zapisano zmiany w bazie.");
        } catch (IOException e) {
            System.err.println("SERWER: Błąd zapisu bazy: " + e.getMessage());
        }
    }

    private static void obslugaKlienta(Socket socket) {
        try (ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

            while (true) {
                Komunikat req = (Komunikat) in.readObject();
                Komunikat resp = new Komunikat(TypKomunikatu.ODPOWIEDZ_OK);

                switch (req.typ) {
                    case LOGIN:
                        Uzytkownik znaleziony = znajdzUzytkownika(req.uzytkownik.login);
                        if (znaleziony != null && znaleziony.haslo.equals(req.uzytkownik.haslo)) {
                            resp.wiadomosc = "Zalogowano";
                            znaleziony.zalogowany = true;
                        } else {
                            resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                            resp.wiadomosc = "Błędny login lub hasło";
                        }
                        break;

                    case REJESTRACJA:
                        if (znajdzUzytkownika(req.uzytkownik.login) != null) {
                            resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                            resp.wiadomosc = "Taki login jest już zajęty!";
                        } else {
                            req.uzytkownik.rodzajKonta = RodzajKonta.UZYTKOWNIK;
                            listaUzytkownikow.add(req.uzytkownik);
                            zapiszBazeDoPliku();
                            resp.wiadomosc = "Konto utworzone.";
                        }
                        break;

                    case POBIERZ_UZYTKOWNIKOW:
                        resp.listaUzytkownikow = new ArrayList<>(listaUzytkownikow);
                        break;

                    case USUN_UZYTKOWNIKA:
                        boolean usunieto = listaUzytkownikow.removeIf(u -> u.login.equals(req.uzytkownik.login));
                        if (usunieto) {
                            zapiszBazeDoPliku();
                            resp.wiadomosc = "Usunięto użytkownika.";
                        } else {
                            resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                            resp.wiadomosc = "Nie znaleziono użytkownika.";
                        }
                        break;

                    case EDYTUJ_UZYTKOWNIKA:
                        Uzytkownik doEdycji = znajdzUzytkownika(req.uzytkownik.login);
                        if (doEdycji != null) {
                            doEdycji.haslo = req.uzytkownik.haslo;
                            doEdycji.rodzajKonta = req.uzytkownik.rodzajKonta;
                            zapiszBazeDoPliku();
                            resp.wiadomosc = "Zaktualizowano dane.";
                        } else {
                            resp.typ = TypKomunikatu.ODPOWIEDZ_BLAD;
                            resp.wiadomosc = "Błąd edycji.";
                        }
                        break;
                }

                out.writeObject(resp);
                out.flush();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Uzytkownik znajdzUzytkownika(String login) {
        return listaUzytkownikow.stream()
                .filter(u -> u.login.equals(login))
                .findFirst()
                .orElse(null);
    }
}