package common;

import java.io.Serializable;
import java.util.List;

public class Pytanie implements Serializable {
    private String trescPytania;
    private List<String> opcjeOdpowiedzi;

    public Pytanie(String trescPytania, List<String> opcjeOdpowiedzi) {
        this.trescPytania = trescPytania;
        this.opcjeOdpowiedzi = opcjeOdpowiedzi;
    }

    public String getTrescPytania() { return trescPytania; }
    public List<String> getOpcjeOdpowiedzi() { return opcjeOdpowiedzi; }
}