package common;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SzablonAnkiety implements Serializable {
    private static final long serialVersionUID = 2L;

    private String id; // UUID
    private String tytul;
    private List<Pytanie> pytania;

    public SzablonAnkiety(String id, String tytul) {
        this.id = id;
        this.tytul = tytul;
        this.pytania = new ArrayList<>();
    }

    public void dodajPytanie(Pytanie p) {
        this.pytania.add(p);
    }

    // Gettery i Settery
    public String getId() { return id; }
    public String getTytul() { return tytul; }
    public List<Pytanie> getPytania() { return pytania; }
}