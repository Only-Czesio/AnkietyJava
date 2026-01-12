package klient.widoki;

import common.*;
import klient.Klient;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class WidokRaportow extends JPanel {

    private final Klient klient;

    private JComboBox<SzablonAnkiety> cbAnkiety;
    private JComboBox<Uzytkownik> cbUzytkownicy;

    private JPanel center;
    private WykresPanel wykres;
    private JLabel info;
    private JPanel panelOdpowiedzi;

    public WidokRaportow(Klient klient) {
        this.klient = klient;
        setLayout(new BorderLayout());

        add(budujFiltry(), BorderLayout.NORTH);
        add(budujCenter(), BorderLayout.CENTER);

        odswiez();
        pokazPlaceholder();
    }

    // ===== FILTRY =====
    private JPanel budujFiltry() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
        p.setPreferredSize(new Dimension(1000, 45));
        cbAnkiety = new JComboBox<>();
        cbAnkiety.addItem(null);
        cbAnkiety.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "Wybierz ankietę" : value.getTytul())
        );

        cbUzytkownicy = new JComboBox<>();
        cbUzytkownicy.addItem(null);
        cbUzytkownicy.setRenderer((list, value, index, isSelected, cellHasFocus) ->
                new JLabel(value == null ? "Wszyscy użytkownicy" : value.getLogin())
        );

        JButton btnGeneruj = new JButton("Generuj raport");
        btnGeneruj.addActionListener(e -> generuj());

        p.add(new JLabel("Ankieta:"));
        p.add(cbAnkiety);
        p.add(new JLabel("Użytkownik:"));
        p.add(cbUzytkownicy);
        p.add(btnGeneruj);

        return p;
    }

    private JPanel budujCenter() {
        center = new JPanel(new CardLayout());

        JLabel placeholder = new JLabel(
                "Wybierz ankietę i (opcjonalnie) użytkownika, aby zobaczyć raport",
                SwingConstants.CENTER
        );

        JPanel top = new JPanel(new BorderLayout());
        info = new JLabel("", SwingConstants.CENTER);
        wykres = new WykresPanel();

        top.add(info, BorderLayout.NORTH);
        top.add(wykres, BorderLayout.CENTER);

        panelOdpowiedzi = new JPanel();
        panelOdpowiedzi.setLayout(new BoxLayout(panelOdpowiedzi, BoxLayout.Y_AXIS));
        JScrollPane scroll = new JScrollPane(panelOdpowiedzi);

        JSplitPane split = new JSplitPane(
                JSplitPane.VERTICAL_SPLIT,
                top,
                scroll
        );
        split.setResizeWeight(0.7);
        split.setBorder(null);

        center.add(placeholder, "EMPTY");
        center.add(split, "CHART");

        return center;
    }

    private void pokazOdpowiedziUzytkownika(
            SzablonAnkiety szablon,
            Ankieta ankieta
    ) {
        panelOdpowiedzi.removeAll();

        panelOdpowiedzi.add(new JLabel(
                "Zakończono: " + format(ankieta.getEndDate())
        ));
        panelOdpowiedzi.add(Box.createVerticalStrut(10));


        Map<Integer, List<Integer>> odp = ankieta.getOdpowiedzi();

        int i = 0;
        for (Pytanie p : szablon.getPytania()) {
            JLabel pytanie = new JLabel(
                    (i + 1) + ". " + p.getTrescPytania()
            );
            pytanie.setFont(pytanie.getFont().deriveFont(Font.BOLD));

            panelOdpowiedzi.add(pytanie);

            List<Integer> zaznaczone = odp.get(i);
            if (zaznaczone == null || zaznaczone.isEmpty()) {
                panelOdpowiedzi.add(new JLabel("— brak odpowiedzi —"));
            } else {
                for (int idx : zaznaczone) {
                    panelOdpowiedzi.add(
                            new JLabel("✔ " + p.getOpcjeOdpowiedzi().get(idx))
                    );
                }
            }

            panelOdpowiedzi.add(Box.createVerticalStrut(8));
            i++;
        }

        panelOdpowiedzi.revalidate();
        panelOdpowiedzi.repaint();
    }

    private String format(LocalDateTime dt) {
        return dt.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
    }

    private void pokazPlaceholder() {
        ((CardLayout) center.getLayout()).show(center, "EMPTY");
    }

    private void pokazWykres() {
        ((CardLayout) center.getLayout()).show(center, "CHART");
    }

    // ===== DANE =====
    private void odswiez() {
        Komunikat szablony = klient.wyslij(new Komunikat(TypKomunikatu.POBIERZ_SZABLONY));
        Komunikat users = klient.wyslij(new Komunikat(TypKomunikatu.POBIERZ_UZYTKOWNIKOW));

        cbAnkiety.removeAllItems();
        cbAnkiety.addItem(null);
        for (SzablonAnkiety s : szablony.getListaSzablonow()) {
            cbAnkiety.addItem(s);
        }
        cbUzytkownicy.removeAllItems();
        cbUzytkownicy.addItem(null);

        for (Uzytkownik u : users.getListaUzytkownikow()) {
            if (u.getRodzajKonta() != RodzajKonta.ADMIN) {
                cbUzytkownicy.addItem(u);
            }
        }
    }

    // ===== LOGIKA RAPORTU =====
    private void generuj() {
        SzablonAnkiety ankieta = (SzablonAnkiety) cbAnkiety.getSelectedItem();
        Uzytkownik user = (Uzytkownik) cbUzytkownicy.getSelectedItem();

        if (ankieta == null) {
            pokazPlaceholder();
            return;
        }

        List<Ankieta> wszystkieAnkiety = klient
                .wyslij(new Komunikat(TypKomunikatu.POBIERZ_ANKIETY))
                .getListaOdpowiedzi();

        List<Ankieta> zakonczone = wszystkieAnkiety.stream()
                .filter(a -> a.getIdSzablonu().equals(ankieta.getId()))
                .filter(Ankieta::czyZakonczona)
                .collect(Collectors.toList());

        List<Ankieta> wypelnione = zakonczone;

        if (user != null) {
            wypelnione = zakonczone.stream()
                    .filter(a -> a.getIDUzytkownika().equals(user.getLogin()))
                    .collect(Collectors.toList());
        }


        Set<String> wypelniliLoginy = zakonczone.stream()
                .map(Ankieta::getIDUzytkownika)
                .collect(Collectors.toSet());

        List<Uzytkownik> ankietowani = klient
                .wyslij(new Komunikat(TypKomunikatu.POBIERZ_UZYTKOWNIKOW))
                .getListaUzytkownikow()
                .stream()
                .filter(u -> u.getRodzajKonta() != RodzajKonta.ADMIN)
                .toList();

        int wypelnili = wypelniliLoginy.size();
        int wszyscy = ankietowani.size();
        int niewypelnili = Math.max(0, wszyscy - wypelnili);


        info.setText(
                "Liczba pytań: " + ankieta.getPytania().size() +
                        " | Wypełnili: " + wypelnili +
                        " | Nie wypełnili: " + niewypelnili
        );

        wykres.ustaw(wypelnili, niewypelnili);

        if (user != null && !wypelnione.isEmpty()) {
            Ankieta ankietaUsera = wypelnione.get(0);
            pokazOdpowiedziUzytkownika(ankieta, ankietaUsera);
            panelOdpowiedzi.setVisible(true);
        } else {
            panelOdpowiedzi.setVisible(false);
        }

        pokazWykres();
    }

    // ===== WYKRES =====
    static class WykresPanel extends JPanel {
        private int tak = 0;
        private int nie = 0;

        void ustaw(int tak, int nie) {
            this.tak = tak;
            this.nie = nie;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            int h = getHeight() - 60;
            int w = getWidth();
            int max = Math.max(1, Math.max(tak, nie));

            int hTak = h * tak / max;
            int hNie = h * nie / max;

            g.setColor(Color.GREEN);
            g.fillRect(w / 4 - 30, h - hTak + 40, 60, hTak);

            g.setColor(Color.RED);
            g.fillRect(3 * w / 4 - 30, h - hNie + 40, 60, hNie);

            g.setColor(Color.BLACK);
            g.drawString("Wypełnili", w / 4 - 25, h + 55);
            g.drawString("Nie wypełnili", 3 * w / 4 - 35, h + 55);
        }
    }
}
