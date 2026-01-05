package common;

import java.io.Serial;
import java.util.List;
import java.io.Serializable;

public class Komunikat implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    public TypKomunikatu typ;
    public String wiadomosc;
    public Uzytkownik uzytkownik;
    public List<Uzytkownik> listaUzytkownikow;

    public Komunikat(TypKomunikatu typ, String wiadomosc) {
        this.typ = typ;
        this.wiadomosc = wiadomosc;
    }

    public Komunikat(TypKomunikatu typKomunikatu) {
        this.typ = typKomunikatu;
    }
}