package common;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;

public class Ankieta implements Serializable {
    private static final long serialVersionUID = 1L;

    private String idSzablonu;
    private String loginUzytkownika;

    private Map<Integer, List<Integer>> odpowiedzi;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private boolean czyZakonczona;

    public Ankieta(String idSzablonu, String loginUzytkownika, Map<Integer, List<Integer>> odpowiedzi) {
        this.idSzablonu = idSzablonu;
        this.loginUzytkownika = loginUzytkownika;
        this.odpowiedzi = odpowiedzi;
        this.startDate = LocalDateTime.now();
        this.czyZakonczona = false;
    }

    public String getIdSzablonu() { return idSzablonu; }
    public String getLoginUzytkownika() { return loginUzytkownika; }
    public Map<Integer, List<Integer>> getOdpowiedzi() { return odpowiedzi; }

    public LocalDateTime getStartDate() { return startDate; }
    public LocalDateTime getEndDate() { return endDate; }

    public void aktualizujOdpowiedzi(Map<Integer, List<Integer>> noweOdpowiedzi) {
        this.odpowiedzi.putAll(noweOdpowiedzi);
    }

    public void zakonczAnkiete() {
        this.czyZakonczona = true;
        this.endDate = LocalDateTime.now();
    }

    public boolean czyZakonczona() {
        return  czyZakonczona;
    }
}