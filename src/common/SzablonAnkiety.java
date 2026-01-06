package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SzablonAnkiety implements Serializable {
    private String id;
    private String tytul;
    private List<Pytanie> pytania = new ArrayList<>();

    public SzablonAnkiety(String id, String tytul) {
        this.id = id;
        this.tytul = tytul;
    }

    public void dodajPytanie(Pytanie p) { pytania.add(p); }
    public String getId() { return id; }
    public List<Pytanie> getPytania() { return pytania; }
}