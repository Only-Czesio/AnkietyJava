package common;

import common.*;

import java.util.List;
import java.io.Serializable;

public class Komunikat implements Serializable {
    private static final long serialVersionUID = 1L;

    public TypKomunikatu typ;
    public String wiadomosc;
    public Uzytkownik uzytkownik;
    public List<Uzytkownik> listaUzytkownikow;

    public Komunikat(TypKomunikatu typ, String login, String haslo) {
        this.typ = typ;
        uzytkownik.login = login;
        uzytkownik.haslo = haslo;
    }

    public Komunikat(TypKomunikatu typ, String wiadomosc) {
        this.typ = typ;
        this.wiadomosc = wiadomosc;
    }

    public Komunikat(TypKomunikatu typKomunikatu) {
        //Do zaimplementowania
    }
}