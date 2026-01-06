package common;

import java.io.Serializable;
import java.util.List;

public class Pytanie implements Serializable {
    private String tresc;
    private List<String> mozliweOdpowiedzi;

    public Pytanie(String tresc, List<String> mozliweOdpowiedzi) {
        this.tresc = tresc;
        this.mozliweOdpowiedzi = mozliweOdpowiedzi;
    }

    public String getTresc() { return tresc; }
    public List<String> getMozliweOdpowiedzi() { return mozliweOdpowiedzi; }
}