package common;
import java.io.Serializable;

public class Uzytkownik implements Serializable {
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

    @Override
    public String toString() {
        return login + " (" + rodzajKonta + ")";
    }
}
