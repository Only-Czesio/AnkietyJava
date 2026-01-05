package klient;

import klient.widoki.*;
import common.RodzajKonta;
import javax.swing.*;
import java.awt.*;

public class PanelDashboard extends JPanel {

    private Klient klient;
    private JPanel panelTresci;
    private CardLayout cardLayoutTresci;

    public PanelDashboard(Klient klient, boolean czyAdmin) {
        this.klient = klient;
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

        // Przycisk widoczny tylko dla ADMINA
        if (czyAdmin) {
            menuPanel.add(btnUzytkownicy);
        }

        menuPanel.add(new JLabel(""));
        menuPanel.add(btnWyloguj);

        add(menuPanel, BorderLayout.WEST);

        // --- 2. ŚRODEK (ZMIENNA TREŚĆ) ---
        cardLayoutTresci = new CardLayout();
        panelTresci = new JPanel(cardLayoutTresci);

        // -- A. Widok Startowy --
        JPanel startPanel = new JPanel();
        startPanel.add(new JLabel("Witaj! Wybierz opcję z menu po lewej."));
        panelTresci.add(startPanel, "START");

        // -- B. Widok Użytkowników (Tylko dla admina) --
        if (czyAdmin) {
            // Tutaj wstawiamy Twój gotowy WidokUzytkownikow!
            WidokUzytkownikow widokUserow = new WidokUzytkownikow(klient);
            panelTresci.add(widokUserow, "UZYTKOWNICY");
        }

        // -- C. Widok Ankiet (Placeholder na razie) --
        JPanel ankietyPanel = new JPanel();
        ankietyPanel.add(new JLabel("Tu będzie lista ankiet (WidokAnkiet)"));
        panelTresci.add(ankietyPanel, "ANKIETY");

        // -- D. Widok Raportów (Placeholder) --
        JPanel raportyPanel = new JPanel();
        raportyPanel.add(new JLabel("Tu będą wykresy"));
        panelTresci.add(raportyPanel, "RAPORTY");

        add(panelTresci, BorderLayout.CENTER);

        // --- 3. OBSŁUGA KLIKNIĘĆ (NAWIGACJA) ---

        btnStart.addActionListener(e -> cardLayoutTresci.show(panelTresci, "START"));
        btnAnkiety.addActionListener(e -> cardLayoutTresci.show(panelTresci, "ANKIETY"));
        btnRaporty.addActionListener(e -> cardLayoutTresci.show(panelTresci, "RAPORTY"));

        if (czyAdmin) {
            btnUzytkownicy.addActionListener(e -> cardLayoutTresci.show(panelTresci, "UZYTKOWNICY"));
        }

        // Wylogowanie wywołuje metodę w głównym Kliencie
        btnWyloguj.addActionListener(e -> klient.wyloguj());
    }
}