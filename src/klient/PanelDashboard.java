package klient;

import klient.widoki.*;
import javax.swing.*;
import java.awt.*;

public class PanelDashboard extends JPanel {

    private final JPanel panelTresci;
    private final CardLayout cardLayoutTresci;

    public PanelDashboard(Klient klient, boolean czyAdmin) {
        setLayout(new BorderLayout());

        // ===== MENU BOCZNE =====
        JPanel menuPanel = new JPanel(new GridLayout(8, 1, 5, 5));
        menuPanel.setPreferredSize(new Dimension(200, 0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        menuPanel.setBackground(Color.LIGHT_GRAY);

        JButton btnAnkiety = new JButton("Ankiety");
        JButton btnRaporty = new JButton("Raporty");
        JButton btnUzytkownicy = new JButton("Użytkownicy");
        JButton btnWyloguj = new JButton("Wyloguj");

        menuPanel.add(btnAnkiety);
        menuPanel.add(btnRaporty);

        if (czyAdmin) {
            menuPanel.add(btnUzytkownicy);
        }

        menuPanel.add(new JLabel(""));
        menuPanel.add(btnWyloguj);

        add(menuPanel, BorderLayout.WEST);

        // ===== TREŚĆ =====
        cardLayoutTresci = new CardLayout();
        panelTresci = new JPanel(cardLayoutTresci);
        if (czyAdmin) {
            WidokSzablonow widokSzablonow = new WidokSzablonow(klient);
            panelTresci.add(widokSzablonow, "ANKIETY");
        } else {
            WidokAnkiet widokAnkiet = new WidokAnkiet(klient, false);
            panelTresci.add(widokAnkiet, "ANKIETY");
        }

        if (czyAdmin) {
            WidokUzytkownikow widokUserow = new WidokUzytkownikow(klient);
            panelTresci.add(widokUserow, "UZYTKOWNICY");
        }

        WidokRaportow raporty = new WidokRaportow(klient);
        panelTresci.add(raporty, "RAPORTY");

        add(panelTresci, BorderLayout.CENTER);

        // ===== AKCJE =====
        btnAnkiety.addActionListener(e -> cardLayoutTresci.show(panelTresci, "ANKIETY"));
        btnRaporty.addActionListener(e -> cardLayoutTresci.show(panelTresci, "RAPORTY"));

        if (czyAdmin) {
            btnUzytkownicy.addActionListener(e -> cardLayoutTresci.show(panelTresci, "UZYTKOWNICY"));
        }

        btnWyloguj.addActionListener(e -> klient.wyloguj());

        cardLayoutTresci.show(panelTresci, "ANKIETY");
    }
}
