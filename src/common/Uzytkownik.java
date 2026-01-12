package common;
import java.io.Serial;
import java.io.Serializable;

public class Uzytkownik implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    public String login;
    public String haslo;
    public RodzajKonta rodzajKonta;
    public boolean zalogowany = false;

    public Uzytkownik(String login, String haslo, RodzajKonta rodzajKonta) {
        this.login = login;
        this.haslo = haslo;
        this.rodzajKonta = rodzajKonta;
    }

    public String getLogin() {
        return login;
    }

    public void setZalogowany(boolean zalogowany) {
        this.zalogowany = zalogowany;
    }

    public String getHaslo() {
        return haslo;
    }

    public void setHaslo(String haslo) {
        this.haslo = haslo;
    }

    public RodzajKonta getRodzajKonta() {
        return rodzajKonta;
    }

    public void setRodzajKonta(RodzajKonta rodzajKonta) {
        this.rodzajKonta = rodzajKonta;
    }

    @Override
    public String toString() {
        return login + " (" + rodzajKonta + ")";
    }
}
