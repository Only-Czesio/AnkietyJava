package klient.widoki;

import common.*;
import klient.Klient;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;

public class OknoAnkiety extends JDialog {

    private final Ankieta ankietaWTrakcie;
    private final SzablonAnkiety szablon;
    private final Klient klient;

    private final Map<Integer, List<AbstractButton>> mapaKontrolek = new HashMap<>();

    public OknoAnkiety(SzablonAnkiety szablon, Klient klient, Ankieta ankieta) {
        super((Frame) null, "Ankieta: " + szablon.getTytul(), true);
        this.szablon = szablon;
        this.klient = klient;
        this.ankietaWTrakcie = ankieta;

        JLabel info = new JLabel(
                "Rozpoczęcie: " + format(ankietaWTrakcie != null
                        ? ankietaWTrakcie.getStartDate()
                        : LocalDateTime.now())
        );
        info.setBorder(BorderFactory.createEmptyBorder(5,10,5,10));
        add(info, BorderLayout.NORTH);


        setLayout(new BorderLayout());
        add(budujPanelPytan(), BorderLayout.CENTER);
        add(budujPanelPrzyciskow(), BorderLayout.SOUTH);

        setSize(600, 500);
        setLocationRelativeTo(null);

        if (ankietaWTrakcie != null) {
            wypelnijZapisaneOdpowiedzi(ankietaWTrakcie.getOdpowiedzi());
        }
    }

    private String format(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void wypelnijZapisaneOdpowiedzi(Map<Integer, List<Integer>> odp) {
        for (var e : odp.entrySet()) {
            List<AbstractButton> btns = mapaKontrolek.get(e.getKey());
            for (int i : e.getValue()) {
                btns.get(i).setSelected(true);
            }
        }
    }


    private JScrollPane budujPanelPytan() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        int index = 0;
        for (Pytanie p : szablon.getPytania()) {
            JPanel pytaniePanel = new JPanel();
            pytaniePanel.setLayout(new BoxLayout(pytaniePanel, BoxLayout.Y_AXIS));
            pytaniePanel.setBorder(BorderFactory.createTitledBorder(p.getTrescPytania()));

            pytaniePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            pytaniePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

            List<AbstractButton> kontrolki = new ArrayList<>();

            if (p.czyWielokrotnyWybor()) {
                for (String opcja : p.getOpcjeOdpowiedzi()) {
                    JCheckBox cb = new JCheckBox(opcja);
                    kontrolki.add(cb);
                    pytaniePanel.add(cb);
                }
            } else {
                ButtonGroup bg = new ButtonGroup();
                for (String opcja : p.getOpcjeOdpowiedzi()) {
                    JRadioButton rb = new JRadioButton(opcja);
                    bg.add(rb);
                    kontrolki.add(rb);
                    pytaniePanel.add(rb);
                }
            }

            mapaKontrolek.put(index++, kontrolki);
            panel.add(pytaniePanel);
        }

        return new JScrollPane(panel);
    }

    private JPanel budujPanelPrzyciskow() {
        JPanel panel = new JPanel();

        JButton btnZapisz = new JButton("Zapisz");
        JButton btnZakoncz = new JButton("Zakończ");

        btnZapisz.addActionListener(e -> zapisz(false));
        btnZakoncz.addActionListener(e -> zapisz(true));

        panel.add(btnZapisz);
        panel.add(btnZakoncz);
        return panel;
    }

    private void zapisz(boolean zakoncz) {
        Map<Integer, List<Integer>> odpowiedzi = new HashMap<>();

        for (Map.Entry<Integer, List<AbstractButton>> entry : mapaKontrolek.entrySet()) {
            List<Integer> zaznaczone = new ArrayList<>();
            List<AbstractButton> btns = entry.getValue();

            for (int i = 0; i < btns.size(); i++) {
                if (btns.get(i).isSelected()) {
                    zaznaczone.add(i);
                }
            }

            if (!zaznaczone.isEmpty()) {
                odpowiedzi.put(entry.getKey(), zaznaczone);
            }
        }

        // ✅ UŻYWAMY ISTNIEJĄCEJ ANKIETY
        Ankieta ankieta = ankietaWTrakcie != null
                ? ankietaWTrakcie
                : new Ankieta(
                szablon.getId(),
                klient.getZalogowanyUser().getLogin(),
                new HashMap<>()
        );

        ankieta.setOdpowiedzi(odpowiedzi);

        if (zakoncz) {
            int liczbaPytan = szablon.getPytania().size();

            if (odpowiedzi.size() < liczbaPytan) {
                JOptionPane.showMessageDialog(
                        this,
                        "Aby zakończyć ankietę, musisz odpowiedzieć na wszystkie pytania.",
                        "Niekompletna ankieta",
                        JOptionPane.WARNING_MESSAGE
                );
                return;
            }

            ankieta.zakonczAnkiete();
        }

        Komunikat req = new Komunikat(TypKomunikatu.ZAPISZ_ANKIETE);
        req.setAnkieta(ankieta);
        req.setWiadomosc(zakoncz ? "ZAKOŃCZONA" : "W TRAKCIE");

        klient.wyslij(req);

        JOptionPane.showMessageDialog(
                this,
                zakoncz ? "Ankieta zakończona" : "Postęp zapisany"
        );

        if (zakoncz) dispose();
    }

}
