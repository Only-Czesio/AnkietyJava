package klient.widoki;

import common.*;
import klient.*;

import javax.swing.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class OknoAnkiety extends JDialog {
    private final Map<Integer, List<Integer>> wybraneOdpowiedzi = new HashMap<>();
    private final Ankieta aktualnaAnkieta;

    public OknoAnkiety(SzablonAnkiety szablon, Klient klient) {
        setTitle("Wypełnianie: " + szablon.getTytul());
        setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
        aktualnaAnkieta = new Ankieta(szablon.getId(), klient.getZalogowanyUser().getLogin(), wybraneOdpowiedzi);
        for (int i = 0; i < szablon.getPytania().size(); i++) {
            Pytanie p = szablon.getPytania().get(i);
            add(new JLabel(p.getTrescPytania()));
            final int nrPytania = i;
            if (p.isWielokrotnyWybor()) {
                for (int j = 0; j < p.getOpcjeOdpowiedzi().size(); j++) {
                    final int nrOpcji = j;
                    JCheckBox cb = new JCheckBox(p.getOpcjeOdpowiedzi().get(j));

                    cb.addActionListener(e -> {
                        List<Integer> wybrane = wybraneOdpowiedzi.computeIfAbsent(nrPytania, k -> new java.util.ArrayList<>());

                        if (cb.isSelected()) {
                            if (!wybrane.contains(nrOpcji)) wybrane.add(nrOpcji);
                        } else {
                            wybrane.remove(Integer.valueOf(nrOpcji));
                        }
                        wyslijSzkic(klient);
                    });
                    add(cb);
                }
            } else {
                ButtonGroup grupa = new ButtonGroup();
                for (int j = 0; j < p.getOpcjeOdpowiedzi().size(); j++) {
                    final int nrOpcji = j;
                    JRadioButton rb = new JRadioButton(p.getOpcjeOdpowiedzi().get(j));

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
            Komunikat req = new Komunikat(TypKomunikatu.ZAPISZ_ANKIETE, "FINAL");
            req.setAnkieta(aktualnaAnkieta);
            klient.wyslij(req);
            dispose();
        });
        add(btnZakoncz);
        pack();
    }

    private void wyslijSzkic(Klient klient) {
        Komunikat req = new Komunikat(TypKomunikatu.ZAPISZ_ANKIETE, "DRAFT");
        req.setAnkieta(aktualnaAnkieta);
        klient.wyslij(req);
    }
}
