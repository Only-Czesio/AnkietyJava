package klient.widoki;

import common.*;
import klient.*;
import javax.swing.*;

public class WidokUzytkownikow extends WidokBazowy {

    private Klient klient;

    public WidokUzytkownikow(Klient klient) {
        super(new String[]{"Login", "Hasło", "Rodzaj konta"});
        this.klient = klient;
        odswiezDane();
    }

    @Override
    protected void odswiezDane() {
        Komunikat resp = klient.wyslij(new Komunikat(TypKomunikatu.POBIERZ_UZYTKOWNIKOW));

        modelTabeli.setRowCount(0);

        if (resp != null && resp.getListaUzytkownikow() != null) {
            for (Uzytkownik u : resp.getListaUzytkownikow()) {
                modelTabeli.addRow(new Object[]{
                        u.getLogin(),
                        u.getHaslo(),
                        u.getRodzajKonta()
                });
            }
        }
        System.out.println("Odświeżono listę użytkowników.");
    }

    @Override
    protected void akcjaDodaj() {
        JTextField loginField = new JTextField();
        JPasswordField passField = new JPasswordField();
        JComboBox<RodzajKonta> roleCombo = new JComboBox<>(RodzajKonta.values());

        Object[] message = {
                "Login:", loginField,
                "Hasło:", passField,
                "Rola:", roleCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Dodaj użytkownika", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String login = loginField.getText();
            String password = new String(passField.getPassword());
            RodzajKonta rola = (RodzajKonta) roleCombo.getSelectedItem();

            if(!login.isEmpty() && !password.isEmpty()) {
                Komunikat req = new Komunikat(TypKomunikatu.DODAJ_UZYTKOWNIKA);
                req.setUzytkownik(new Uzytkownik(login, password, rola));

                klient.wyslij(req);
                odswiezDane();
            } else {
                JOptionPane.showMessageDialog(this, "Pola nie mogą być puste!");
            }
        }
    }

    @Override
    protected void akcjaEdytuj() {
        String login = pobierzZaznaczoneId();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "Zaznacz użytkownika!");
            return;
        }

        int row = tabela.getSelectedRow();
        RodzajKonta obecnaRola = (RodzajKonta) modelTabeli.getValueAt(row, 2);

        JTextField loginField = new JTextField(login);
        loginField.setEditable(false); // Loginu zazwyczaj nie zmieniamy
        JPasswordField passField = new JPasswordField(); // Puste = bez zmiany hasła
        JComboBox<RodzajKonta> roleCombo = new JComboBox<>(RodzajKonta.values());
        roleCombo.setSelectedItem(obecnaRola);

        Object[] message = {
                "Użytkownik:", loginField,
                "Nowe hasło (zostaw puste, aby nie zmieniać):", passField,
                "Rola:", roleCombo
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Edytuj użytkownika", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            Komunikat req = new Komunikat(TypKomunikatu.EDYTUJ_UZYTKOWNIKA);
            String noweHaslo = new String(passField.getPassword());
            RodzajKonta nowaRola = (RodzajKonta) roleCombo.getSelectedItem();

            req.setUzytkownik(new Uzytkownik(login, noweHaslo, nowaRola));
            klient.wyslij(req);
            odswiezDane();
        }
    }

    @Override
    protected void akcjaUsun() {
        String login = pobierzZaznaczoneId();
        if (login == null) {
            JOptionPane.showMessageDialog(this, "Zaznacz użytkownika do usunięcia!");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Czy na pewno usunąć użytkownika " + login + "?");
        if (confirm == JOptionPane.YES_OPTION) {
            Komunikat req = new Komunikat(TypKomunikatu.USUN_UZYTKOWNIKA);
            req.setUzytkownik(new Uzytkownik(login, "", null)); // Identyfikujemy po loginie

            klient.wyslij(req);
            odswiezDane();
        }
    }
}
