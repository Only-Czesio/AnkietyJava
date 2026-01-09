package klient.widoki;

import common.*;
import klient.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OknoAnkiety extends JDialog {
    private Map<Integer, List<Integer>> wybraneOdpowiedzi = new HashMap<>();
    private final Ankieta aktualnaAnkieta;

    public OknoAnkiety(SzablonAnkiety szablon, Klient klient) {
        setTitle("Wypełnianie: " + szablon.getTytul());
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));

        Komunikat reqSzkic = new Komunikat(TypKomunikatu.POBIERZ_MOJE_ANKIETY); // Serwer odfiltruje po loginie i ID szablonu
        reqSzkic.setWiadomosc(szablon.getId());
        Komunikat respSzkic = klient.wyslij(reqSzkic);

        if (respSzkic != null && respSzkic.getAnkieta() != null) {
            this.aktualnaAnkieta = respSzkic.getAnkieta();
            this.wybraneOdpowiedzi = aktualnaAnkieta.getOdpowiedzi();
        } else {
            this.aktualnaAnkieta = new Ankieta(szablon.getId(), klient.getZalogowanyUser().getLogin(), wybraneOdpowiedzi);
        }

        for (int i = 0; i < szablon.getPytania().size(); i++) {
            Pytanie p = szablon.getPytania().get(i);
            add(new JLabel(p.getTrescPytania()));
            int nrPytania = i;

            List<Integer> juzZaznaczone = wybraneOdpowiedzi.getOrDefault(nrPytania, new java.util.ArrayList<>());

            if (p.czyWielokrotnyWybor()) {
                for (int j = 0; j < p.getOpcjeOdpowiedzi().size(); j++) {
                    int nrOpcji = j;
                    JCheckBox cb = new JCheckBox(p.getOpcjeOdpowiedzi().get(j));

                    if (juzZaznaczone.contains(nrOpcji)) cb.setSelected(true);

                    cb.addActionListener(e -> {
                        List<Integer> lista = wybraneOdpowiedzi.computeIfAbsent(nrPytania, k -> new java.util.ArrayList<>());
                        if (cb.isSelected()) {
                            if (!lista.contains(nrOpcji)) lista.add(nrOpcji);
                        } else {
                            lista.remove(Integer.valueOf(nrOpcji));
                        }
                        wyslijSzkic(klient);
                    });
                    add(cb);
                }
            } else {
                ButtonGroup grupa = new ButtonGroup();
                for (int j = 0; j < p.getOpcjeOdpowiedzi().size(); j++) {
                    int nrOpcji = j;
                    JRadioButton rb = new JRadioButton(p.getOpcjeOdpowiedzi().get(j));

                    if (juzZaznaczone.contains(nrOpcji)) rb.setSelected(true);

                    rb.addActionListener(e -> {
                        wybraneOdpowiedzi.put(nrPytania, java.util.Collections.singletonList(nrOpcji));
                        wyslijSzkic(klient);
                    });
                    grupa.add(rb);
                    add(rb);
                }
            }
        }

        JButton btnZakoncz = new JButton("Zakończ i wyślij");
        btnZakoncz.addActionListener(e -> {
            aktualnaAnkieta.zakonczAnkiete();
            Komunikat req = new Komunikat(TypKomunikatu.ZAPISZ_ANKIETE, "Zakończono");
            req.setAnkieta(aktualnaAnkieta);
            klient.wyslij(req);
            dispose();
        });
        add(btnZakoncz);
        pack();
    }

    private void wyslijSzkic(Klient klient) {
        Komunikat req = new Komunikat(TypKomunikatu.ZAPISZ_ANKIETE, "Zapisano");
        req.setAnkieta(aktualnaAnkieta);
        klient.wyslij(req);
    }
}
