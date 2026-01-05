package klient;

import common.*;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Klient extends JFrame {
    private static final String HOST = "localhost";
    private static final int PORT = 5000;

    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    private JPanel mainPanel;
    private CardLayout cardLayout;

    // Pola logowania
    private JTextField loginField;
    private JPasswordField passField;

    // Elementy panelu admina
    private DefaultTableModel tableModel;
    private JTable usersTable;

    public Klient() {
        super("System Ankiet");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(600, 400);
        setLocationRelativeTo(null);

        if (!polacz()) {
            JOptionPane.showMessageDialog(this, "Brak serwera!", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(budujPanelLogowania(), "LOGIN");
        mainPanel.add(budujPanelAdmina(), "ADMIN_PANEL");

        // Panel usera na razie pusty (placeholder)
        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Witaj Użytkowniku! (Tu będą ankiety)"));
        JButton btnWylogujUser = new JButton("Wyloguj");
        btnWylogujUser.addActionListener(e -> wyloguj());
        userPanel.add(btnWylogujUser);
        mainPanel.add(userPanel, "USER_PANEL");

        add(mainPanel);
        setVisible(true);
    }

    private boolean polacz() {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            in = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (Exception e) { return false; }
    }

    // --- BUDOWANIE WIDOKÓW ---

    private JPanel budujPanelLogowania() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,5,5,5);

        loginField = new JTextField(15);
        passField = new JPasswordField(15);
        JButton bLogin = new JButton("Zaloguj");
        JButton bRejestracja = new JButton("Zarejestruj");

        c.gridx=0; c.gridy=0; p.add(new JLabel("Login:"), c);
        c.gridx=1; p.add(loginField, c);
        c.gridx=0; c.gridy=1; p.add(new JLabel("Hasło:"), c);
        c.gridx=1; p.add(passField, c);
        c.gridx=1; c.gridy=2;
        JPanel btns = new JPanel();
        btns.add(bLogin); btns.add(bRejestracja);
        p.add(btns, c);

        bLogin.addActionListener(e -> akcjaLogowanie());
        bRejestracja.addActionListener(e -> akcjaRejestracja());

        return p;
    }

    private JPanel budujPanelAdmina() {
        JPanel p = new JPanel(new BorderLayout());

        // Tabela użytkowników
        String[] kolumny = {"Login", "Hasło", "Czy Admin"};
        tableModel = new DefaultTableModel(kolumny, 0);
        usersTable = new JTable(tableModel);

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("PANEL ADMINISTRATORA"));
        JButton refresh = new JButton("Odśwież listę");
        top.add(refresh);

        JPanel bottom = new JPanel();
        JButton btnEdit = new JButton("Edytuj zaznaczonego");
        JButton btnDelete = new JButton("Usuń zaznaczonego");
        JButton btnLogout = new JButton("Wyloguj");
        bottom.add(btnEdit);
        bottom.add(btnDelete);
        bottom.add(btnLogout);

        p.add(top, BorderLayout.NORTH);
        p.add(new JScrollPane(usersTable), BorderLayout.CENTER);
        p.add(bottom, BorderLayout.SOUTH);

        // Akcje przycisków
        refresh.addActionListener(e -> odswiezListeUzytkownikow());
        btnLogout.addActionListener(e -> wyloguj());

        btnDelete.addActionListener(e -> {
            int row = usersTable.getSelectedRow();
            if(row == -1) return;
            String login = (String) tableModel.getValueAt(row, 0);

            // Nie pozwól usunąć samego siebie (opcjonalne, ale mądre)
            if(login.equals(loginField.getText())) {
                JOptionPane.showMessageDialog(this, "Nie możesz usunąć siebie!");
                return;
            }

            Komunikat req = new Komunikat(TypKomunikatu.USUN_UZYTKOWNIKA);
            req.uzytkownik = new Uzytkownik(login, "", false);
            wyslij(req);
            odswiezListeUzytkownikow();
        });

        btnEdit.addActionListener(e -> akcjaEdycja());

        return p;
    }

    // --- LOGIKA BIZNESOWA ---

    private void akcjaLogowanie() {
        String l = loginField.getText();
        String h = new String(passField.getPassword());

        Komunikat req = new Komunikat(TypKomunikatu.LOGIN);
        req.uzytkownik = new Uzytkownik(l, h, false);

        Komunikat resp = wyslij(req);
        if (resp.typ == TypKomunikatu.ODPOWIEDZ_OK) {
            JOptionPane.showMessageDialog(this, "Zalogowano!");
            if (resp.uzytkownik.czyAdmin) {
                cardLayout.show(mainPanel, "ADMIN_PANEL");
                odswiezListeUzytkownikow();
            } else {
                cardLayout.show(mainPanel, "USER_PANEL");
            }
        } else {
            JOptionPane.showMessageDialog(this, resp.wiadomosc);
        }
    }

    private void akcjaRejestracja() {
        String l = loginField.getText();
        String h = new String(passField.getPassword());
        if(l.isEmpty() || h.isEmpty()) return;

        Komunikat req = new Komunikat(TypKomunikatu.REJESTRACJA);
        req.uzytkownik = new Uzytkownik(l, h, false);

        Komunikat resp = wyslij(req);
        JOptionPane.showMessageDialog(this, resp.wiadomosc);
    }

    private void akcjaEdycja() {
        int row = usersTable.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Zaznacz użytkownika!");
            return;
        }

        String login = (String) tableModel.getValueAt(row, 0);
        String stareHaslo = (String) tableModel.getValueAt(row, 1);
        boolean czyAdmin = tableModel.getValueAt(row, 2).toString().equals("true");

        // Proste okienko edycji
        JTextField passEdit = new JTextField(stareHaslo);
        JCheckBox adminCheck = new JCheckBox("Administrator", czyAdmin);
        Object[] msg = {"Nowe hasło:", passEdit, adminCheck};

        int result = JOptionPane.showConfirmDialog(this, msg, "Edycja " + login, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Komunikat req = new Komunikat(TypKomunikatu.EDYTUJ_UZYTKOWNIKA);
            req.uzytkownik = new Uzytkownik(login, passEdit.getText(), adminCheck.isSelected());
            wyslij(req);
            odswiezListeUzytkownikow();
        }
    }

    private void odswiezListeUzytkownikow() {
        Komunikat resp = wyslij(new Komunikat(TypKomunikatu.POBIERZ_UZYTKOWNIKOW));
        if (resp.listaUzytkownikow != null) {
            tableModel.setRowCount(0); // Wyczyść tabelę
            for (Uzytkownik u : resp.listaUzytkownikow) {
                tableModel.addRow(new Object[]{u.login, u.haslo, u.czyAdmin});
            }
        }
    }

    private void wyloguj() {
        loginField.setText("");
        passField.setText("");
        cardLayout.show(mainPanel, "LOGIN");
    }

    private Komunikat wyslij(Komunikat k) {
        try {
            out.writeObject(k);
            out.flush();
            return (Komunikat) in.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            return new Komunikat(TypKomunikatu.ODPOWIEDZ_BLAD);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Klient::new);
    }
}