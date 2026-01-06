package serwer;

import common.*;
import java.io.*;
import java.util.*;

public class BazaDanych {
    private static final String PLIK_BAZY = "baza_danych.dat";
    private List<Uzytkownik> listaUzytkownikow = new ArrayList<>();
    private List<SzablonAnkiety> listaSzablonow = new ArrayList<>();
    private List<Ankieta> listaWynikow = new ArrayList<>();

    public BazaDanych() {
        wczytajBazeZPliku();
    }

    private synchronized void wczytajBazeZPliku() {
        File plik = new File(PLIK_BAZY);
        if (!plik.exists()) {
            String zahashowaneHasloAdmina = Bezpieczenstwo.hashujHaslo("password");
            listaUzytkownikow.add(new Uzytkownik("admin", zahashowaneHasloAdmina, RodzajKonta.ADMIN));
            zapiszBazeDoPliku();
            return;
        }
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(plik))) {
            listaUzytkownikow = (List<Uzytkownik>) ois.readObject();
            listaWynikow = (List<Ankieta>) ois.readObject();
            listaSzablonow = (List<SzablonAnkiety>) ois.readObject();
        } catch (Exception e) {
            System.err.println("Błąd wczytywania: " + e.getMessage());
        }
    }

    public synchronized void zapiszBazeDoPliku() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(PLIK_BAZY))) {
            oos.writeObject(new ArrayList<>(listaUzytkownikow));
            oos.writeObject(new ArrayList<>(listaWynikow));
            oos.writeObject(new ArrayList<>(listaSzablonow));
        } catch (IOException e) {
            System.err.println("Błąd zapisu: " + e.getMessage());
        }
    }

    public synchronized Uzytkownik autentykuj(String login, String hasloNiezaszyfrowane) {
        Uzytkownik uzytkownik = znajdzUzytkownika(login);

        if (uzytkownik != null) {
            if (Bezpieczenstwo.sprawdzHaslo(hasloNiezaszyfrowane, uzytkownik.getHaslo())) {
                return uzytkownik;
            }
        }
        return null;
    }

    public synchronized List<Uzytkownik> getListaUzytkownikow() {
        return new ArrayList<>(listaUzytkownikow);
    }


    public synchronized void dodajUzytkownika(Uzytkownik u) {
        String zahashowane = Bezpieczenstwo.hashujHaslo(u.getHaslo());
        u.setHaslo(zahashowane);
        listaUzytkownikow.add(u);
        zapiszBazeDoPliku();
    }

    public synchronized boolean edytujUzytkownika(Uzytkownik dane) {
        for (int i = 0; i < listaUzytkownikow.size(); i++) {
            Uzytkownik u = listaUzytkownikow.get(i);
            if (u.getLogin().equals(dane.getLogin())) {
                u.setRodzajKonta(dane.getRodzajKonta());

                if (dane.getHaslo() != null && !dane.getHaslo().isEmpty()) {
                    u.setHaslo(Bezpieczenstwo.hashujHaslo(dane.getHaslo()));
                }

                zapiszBazeDoPliku();
                return true;
            }
        }
        return false;
    }

    public synchronized boolean usunUzytkownika(String login) {
        boolean usunieto = listaUzytkownikow.removeIf(u -> u.login.equals(login));
        if (usunieto) zapiszBazeDoPliku();
        return usunieto;
    }

    public synchronized Uzytkownik znajdzUzytkownika(String login) {
        return listaUzytkownikow.stream()
                .filter(u -> u.login.equals(login))
                .findFirst()
                .orElse(null);
    }

    public synchronized void dodajSzablon(SzablonAnkiety s) {
        listaSzablonow.add(s);
        zapiszBazeDoPliku();
    }

    public synchronized boolean usunSzablon(String id) {
        boolean usunieto = listaSzablonow.removeIf(s -> s.getId().equals(id));

        if (usunieto) {
            zapiszBazeDoPliku();
        }
        return usunieto;
    }

    public synchronized List<SzablonAnkiety> getListaSzablonow() {
        return new ArrayList<>(listaSzablonow);
    }

    public synchronized void dodajWynik(Ankieta w) {
        listaWynikow.add(w);
        zapiszBazeDoPliku();
    }

    public synchronized SzablonAnkiety znajdzSzablon(String id) {
        return listaSzablonow.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst()
                .orElse(null);
    }
}

