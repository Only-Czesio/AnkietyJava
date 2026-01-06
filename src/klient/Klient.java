package klient;

import common.*;
import klient.widoki.WidokUzytkownikow;
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

    private JTextField loginField;
    private JPasswordField passField;
    private Uzytkownik zalogowanyUser;

    private DefaultTableModel tableModel;
    private JTable usersTable;



    public Klient() {
        super("Ankiety ONLINE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        if (!polacz()) {
            JOptionPane.showMessageDialog(this, "Brak serwera!", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(budujPanelLogowania(), "LOGIN");

        JPanel adminContainer = new JPanel(new BorderLayout());

        WidokUzytkownikow widokUsers = new WidokUzytkownikow(this);

        JPanel adminTop = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnWylogujAdmin = new JButton("Wyloguj");
        btnWylogujAdmin.addActionListener(e -> wyloguj());
        adminTop.add(new JLabel("Jesteś w panelu Administratora  "));
        adminTop.add(btnWylogujAdmin);

        adminContainer.add(adminTop, BorderLayout.NORTH);
        adminContainer.add(widokUsers, BorderLayout.CENTER);

        mainPanel.add(adminContainer, "ADMIN_PANEL");

        JPanel userPanel = new JPanel();
        userPanel.add(new JLabel("Witaj Użytkowniku! Tutaj wkrótce pojawią się ankiety."));
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

    public boolean zaloguj(String login, String haslo) {
        Komunikat req = new Komunikat(TypKomunikatu.LOGIN);
        req.setUzytkownik(new Uzytkownik(login, haslo, null));

        Komunikat resp = wyslij(req);

        if (resp != null && resp.getTyp() == TypKomunikatu.ODPOWIEDZ_OK) {
            // TUTAJ przypisujemy użytkownika zwróconego przez serwer
            this.zalogowanyUser = resp.getUzytkownik();
            return true;
        }
        return false;
    }

    public void wyloguj() {
        loginField.setText("");
        passField.setText("");
        // Wracamy do ekranu logowania
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void akcjaLogowanie() {
        String l = loginField.getText();
        String h = new String(passField.getPassword());

        Komunikat req = new Komunikat(TypKomunikatu.LOGIN);
        req.uzytkownik = new Uzytkownik(l, h, RodzajKonta.UZYTKOWNIK);

        Komunikat resp = wyslij(req);

        if (resp.typ == TypKomunikatu.ODPOWIEDZ_OK) {

            JOptionPane.showMessageDialog(this, "Zalogowano!");

            boolean czyAdmin = resp.uzytkownik.rodzajKonta.equals(RodzajKonta.ADMIN);

            PanelDashboard dashboard = new PanelDashboard(this, czyAdmin);

            mainPanel.add(dashboard, "APP");
            cardLayout.show(mainPanel, "APP");

        } else {
            JOptionPane.showMessageDialog(this, resp.wiadomosc);
        }
    }

    private void akcjaRejestracja() {
        String l = loginField.getText();
        String h = new String(passField.getPassword());
        if(l.isEmpty() || h.isEmpty()) return;

        Komunikat req = new Komunikat(TypKomunikatu.REJESTRACJA);
        req.uzytkownik = new Uzytkownik(l, h, RodzajKonta.UZYTKOWNIK);

        Komunikat resp = wyslij(req);
        JOptionPane.showMessageDialog(this, resp.wiadomosc);
        loginField.setText("");
        passField.setText("");
    }

    private void akcjaEdycja() {
        int row = usersTable.getSelectedRow();
        if(row == -1) {
            JOptionPane.showMessageDialog(this, "Zaznacz użytkownika!");
            return;
        }

        String login = (String) tableModel.getValueAt(row, 0);
        String stareHaslo = (String) tableModel.getValueAt(row, 1);
        boolean czyAdmin = tableModel.getValueAt(row, 2).equals(RodzajKonta.ADMIN);

        JTextField passEdit = new JTextField(stareHaslo);
        JCheckBox adminCheck = new JCheckBox("Administrator", czyAdmin);
        Object[] msg = {"Nowe hasło:", passEdit, adminCheck};

        int result = JOptionPane.showConfirmDialog(this, msg, "Edycja " + login, JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Komunikat req = new Komunikat(TypKomunikatu.EDYTUJ_UZYTKOWNIKA);
            req.uzytkownik = new Uzytkownik(login, passEdit.getText(), czyAdmin ? RodzajKonta.ADMIN : RodzajKonta.UZYTKOWNIK);
            wyslij(req);
            odswiezListeUzytkownikow();
        }
    }

    private void odswiezListeUzytkownikow() {
        Komunikat resp = wyslij(new Komunikat(TypKomunikatu.POBIERZ_UZYTKOWNIKOW));
        if (resp.listaUzytkownikow != null) {
            tableModel.setRowCount(0);
            for (Uzytkownik u : resp.listaUzytkownikow) {
                tableModel.addRow(new Object[]{u.login, u.haslo, u.rodzajKonta});
            }
        }
    }

    public Komunikat wyslij(Komunikat k) {
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

    public Uzytkownik getZalogowanyUser() {
        return zalogowanyUser;
    }
}