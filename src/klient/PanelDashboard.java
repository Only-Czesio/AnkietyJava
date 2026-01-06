package klient;

import klient.widoki.*;
import common.RodzajKonta;
import javax.swing.*;
import java.awt.*;

public class PanelDashboard extends JPanel {

    private final JPanel panelTresci;
    private final CardLayout cardLayoutTresci;

    public PanelDashboard(Klient klient, boolean czyAdmin) {
        setLayout(new BorderLayout());

        // --- 1. PASEK BOCZNY (MENU) ---
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new GridLayout(10, 1, 5, 5)); // Lista przycisków w pionie
        menuPanel.setPreferredSize(new Dimension(200, 0)); // Szerokość paska
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setBackground(Color.LIGHT_GRAY);

        JButton btnStart = new JButton("Strona Główna");
        JButton btnAnkiety = new JButton("Ankiety");
        JButton btnRaporty = new JButton("Raporty");
        JButton btnUzytkownicy = new JButton("Użytkownicy");
        JButton btnWyloguj = new JButton("Wyloguj");

        menuPanel.add(btnStart);
        menuPanel.add(btnAnkiety);
        menuPanel.add(btnRaporty);

        if (czyAdmin) {
            menuPanel.add(btnUzytkownicy);
        }

        menuPanel.add(new JLabel(""));
        menuPanel.add(btnWyloguj);

        add(menuPanel, BorderLayout.WEST);

        cardLayoutTresci = new CardLayout();
        panelTresci = new JPanel(cardLayoutTresci);

        JPanel startPanel = new JPanel();
        startPanel.add(new JLabel("Witaj! Wybierz opcję z menu po lewej."));
        panelTresci.add(startPanel, "START");

        if (czyAdmin) {
            WidokUzytkownikow widokUserow = new WidokUzytkownikow(klient);
            panelTresci.add(widokUserow, "UZYTKOWNICY");
        }

        if (czyAdmin) {
            WidokSzablonow widokSzablonow = new WidokSzablonow(klient);
            panelTresci.add(widokSzablonow, "ANKIETY");
        } else {
            JPanel widokDlaUsera = new JPanel();
            widokDlaUsera.add(new JLabel("Lista ankiet do wypełnienia - wkrótce!"));
            panelTresci.add(widokDlaUsera, "ANKIETY");
        }

        WidokAnkiet widokAnkiet = new WidokAnkiet(klient, czyAdmin);
        panelTresci.add(widokAnkiet, "ANKIETY");

        JPanel raportyPanel = new JPanel();
        raportyPanel.add(new JLabel("Tu będą wykresy"));
        panelTresci.add(raportyPanel, "RAPORTY");

        add(panelTresci, BorderLayout.CENTER);

        btnStart.addActionListener(e -> cardLayoutTresci.show(panelTresci, "START"));
        btnAnkiety.addActionListener(e -> cardLayoutTresci.show(panelTresci, "ANKIETY"));
        btnRaporty.addActionListener(e -> cardLayoutTresci.show(panelTresci, "RAPORTY"));

        if (czyAdmin) {
            btnUzytkownicy.addActionListener(e -> cardLayoutTresci.show(panelTresci, "UZYTKOWNICY"));
        }

        btnWyloguj.addActionListener(e -> klient.wyloguj());
    }
}