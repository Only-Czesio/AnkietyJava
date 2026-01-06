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
            listaUzytkownikow.add(new Uzytkownik("admin", "password", RodzajKonta.ADMIN));
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

    public synchronized List<Uzytkownik> getListaUzytkownikow() {
        return new ArrayList<>(listaUzytkownikow);
    }

    public synchronized void dodajUzytkownika(Uzytkownik u) {
        listaUzytkownikow.add(u);
        zapiszBazeDoPliku();
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