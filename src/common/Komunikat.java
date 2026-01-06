package common;

import java.io.Serial;
import java.util.List;
import java.io.Serializable;

public class Komunikat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public TypKomunikatu typ;
    public String wiadomosc;

    public List<Uzytkownik> listaUzytkownikow;
    public List<SzablonAnkiety> listaSzablonow;
    public List<Ankieta> listaOdpowiedzi;

    public Uzytkownik uzytkownik;
    public SzablonAnkiety szablon;
    public Ankieta ankieta;

    public Komunikat(TypKomunikatu typ, String wiadomosc) {
        this.typ = typ;
        this.wiadomosc = wiadomosc;
    }

    public Komunikat(TypKomunikatu typKomunikatu) {
        this.typ = typKomunikatu;
    }

    public List<Uzytkownik> getListaUzytkownikow() {
        return listaUzytkownikow;
    }

    public void setListaUzytkownikow(List<Uzytkownik> listaUzytkownikow) {
        this.listaUzytkownikow = listaUzytkownikow;
    }

    public Uzytkownik getUzytkownik() {
        return uzytkownik;
    }

    public void setUzytkownik(Uzytkownik uzytkownik) {
        this.uzytkownik = uzytkownik;
    }

    public TypKomunikatu getTyp() {
        return typ;
    }

    public void setTyp(TypKomunikatu typ) {
        this.typ = typ;
    }

    public String getWiadomosc() {
        return wiadomosc;
    }

    public void setWiadomosc(String wiadomosc) {
        this.wiadomosc = wiadomosc;
    }

    public List<SzablonAnkiety> getListaSzablonow() {
        return listaSzablonow;
    }

    public void setListaSzablonow(List<SzablonAnkiety> listaSzablonow) {
        this.listaSzablonow = listaSzablonow;
    }

    public List<Ankieta> getListaOdpowiedzi() {
        return listaOdpowiedzi;
    }

    public void setListaOdpowiedzi(List<Ankieta> listaOdpowiedzi) {
        this.listaOdpowiedzi = listaOdpowiedzi;
    }
    public void setSzablon(SzablonAnkiety szablon) {
        this.szablon = szablon;
    }

    public SzablonAnkiety getSzablon() {
        return szablon;
    }

    public void setAnkieta(Ankieta ankieta) {
        this.ankieta = ankieta;
    }

    public Ankieta getAnkieta() {
        return ankieta;
    }
}