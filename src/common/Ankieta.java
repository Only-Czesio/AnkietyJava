package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;

public class Ankieta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idSzablonu;
    private String loginUzytkownika;

    private Map<Integer, Integer> odpowiedzi;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean czyZakonczona;

    public Ankieta(String idSzablonu, String loginUzytkownika, Map<Integer, Integer> odpowiedzi) {
        this.idSzablonu = idSzablonu;
        this.loginUzytkownika = loginUzytkownika;
        this.odpowiedzi = odpowiedzi;
        this.startDate = LocalDateTime.now();
        this.czyZakonczona = false;
    }

    public String getIdSzablonu() { return idSzablonu; }
    public String getLoginUzytkownika() { return loginUzytkownika; }
    public Map<Integer, Integer> getOdpowiedzi() { return odpowiedzi; }

    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }

    public boolean czyZakonczona() { return czyZakonczona; }

    public void aktualizujOdpowiedzi(Map<Integer, Integer> noweOdpowiedzi) {
        this.odpowiedzi.putAll(noweOdpowiedzi);
    }

    public void zakonczAnkiete() {
        this.czyZakonczona = true;
        this.endDate = LocalDateTime.now();
    }
}