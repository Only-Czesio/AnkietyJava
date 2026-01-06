package common;

import java.io.Serializable;
import java.util.List;

public class Pytanie implements Serializable {
    private String trescPytania;
    private List<String> opcjeOdpowiedzi;
    private boolean wielokrotnyWybor; // true = JCheckBox, false = JRadioButton



    public Pytanie(String tresc, List<String> opcje, boolean wielokrotnyWybor) {
        this.trescPytania = tresc;
        this.opcjeOdpowiedzi = opcje;
        this.wielokrotnyWybor = wielokrotnyWybor;
    }

    public String getTrescPytania() { return trescPytania; }
    public List<String> getOpcjeOdpowiedzi() { return opcjeOdpowiedzi; }
    public boolean czyWielokrotnyWybor() { return wielokrotnyWybor; }
}