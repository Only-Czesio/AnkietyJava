package klient;

import common.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.net.Socket;

public class Klient extends JFrame {
    private static final String HOST = "localhost";
    private static final int PORT = 6000;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JTextField loginField;
    private JPasswordField passField;
    private Uzytkownik zalogowanyUser;

    public Klient() {
        super("Ankiety ONLINE");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);

        if (!polacz()) {
            JOptionPane.showMessageDialog(this, "Brak serwera!", "Błąd", JOptionPane.ERROR_MESSAGE);
            System.exit(0);
        }

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(budujPanelLogowania(), "LOGIN");

        JButton btnWylogujUser = new JButton("Wyloguj");
        btnWylogujUser.addActionListener(e -> wyloguj());
        add(mainPanel);
        setVisible(true);
    }

    private boolean polacz() {
        try {
            socket = new Socket(HOST, PORT);
            out = new ObjectOutputStream(socket.getOutputStream());
            out.flush();
            in = new ObjectInputStream(socket.getInputStream());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
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

    public void wyloguj() {
        loginField.setText("");
        passField.setText("");
        cardLayout.show(mainPanel, "LOGIN");
    }

    private void akcjaLogowanie() {
        String l = loginField.getText();
        String h = new String(passField.getPassword());

        Komunikat req = new Komunikat(TypKomunikatu.LOGIN);
        req.uzytkownik = new Uzytkownik(l, h, RodzajKonta.UZYTKOWNIK);

        Komunikat resp = wyslij(req);

        if (resp.typ == TypKomunikatu.ODPOWIEDZ_OK) {
            this.zalogowanyUser = resp.uzytkownik;

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