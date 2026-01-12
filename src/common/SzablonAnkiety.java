package common;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SzablonAnkiety implements Serializable {
    @Serial
    private static final long serialVersionUID = 2L;
    private String id;
    private String tytul;
    private List<Pytanie> pytania;
    private LocalDateTime dataUtworzenia;
    private LocalDateTime dataModyfikacji;

    public SzablonAnkiety(String id, String tytul) {
        this.id = id;
        this.tytul = tytul;
        this.pytania = new ArrayList<>();
        this.dataUtworzenia = LocalDateTime.now();
        this.dataModyfikacji = this.dataUtworzenia;
    }

    public SzablonAnkiety(String id, String tytul,
                          LocalDateTime dataUtworzenia,
                          LocalDateTime dataModyfikacji) {
        this.id = id;
        this.tytul = tytul;
        this.pytania = new ArrayList<>();
        this.dataUtworzenia = dataUtworzenia;
        this.dataModyfikacji = dataModyfikacji;
    }


    public void dodajPytanie(Pytanie p) {
        this.pytania.add(p);
    }

    public String getId() { return id; }
    public String getTytul() { return tytul; }
    public List<Pytanie> getPytania() { return pytania; }

    public LocalDateTime getDataUtworzenia() { return dataUtworzenia; }
    public LocalDateTime getDataModyfikacji() { return dataModyfikacji; }
    public void oznaczModyfikacje() {
        this.dataModyfikacji = LocalDateTime.now();
    }
}