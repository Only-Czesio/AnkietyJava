package common;
import java.io.Serializable;

public class Uzytkownik implements Serializable {
    private static final long serialVersionUID = 1L;

    public String login;
    public String haslo;
    public boolean czyAdmin;

    public Uzytkownik(String login, String haslo, boolean czyAdmin) {
        this.login = login;
        this.haslo = haslo;
        this.czyAdmin = czyAdmin;
    }

    @Override
    public String toString() {
        return login + " (" + (czyAdmin ? "ADMIN" : "USER") + ")";
    }
}
