package klient.widoki;

import common.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class KreatorAnkietyDialog extends JDialog {
    private JTextField tytulField = new JTextField(20);
    private DefaultListModel<String> pytaniaModel = new DefaultListModel<>();
    private JList<String> pytaniaList = new JList<>(pytaniaModel);
    private List<Pytanie> listaPytan = new ArrayList<>();
    private boolean zatwierdzono = false;

    public KreatorAnkietyDialog(Frame owner) {
        super(owner, "Nowy Szablon Ankiety", true);
        setLayout(new BorderLayout());

        JPanel top = new JPanel(new FlowLayout());
        top.add(new JLabel("Tytuł ankiety:"));
        top.add(tytulField);

        JPanel center = new JPanel(new BorderLayout());
        center.setBorder(BorderFactory.createTitledBorder("Pytania"));
        center.add(new JScrollPane(pytaniaList), BorderLayout.CENTER);

        JButton btnDodajPytanie = new JButton("Dodaj pytanie");
        btnDodajPytanie.addActionListener(e -> dodajPytanieDialog());
        center.add(btnDodajPytanie, BorderLayout.SOUTH);

        JPanel bottom = getJPanel();

        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setSize(400, 500);
        setLocationRelativeTo(owner);
    }

    private JPanel getJPanel() {
        JPanel bottom = new JPanel();
        JButton btnZapisz = new JButton("Zapisz całą ankietę");
        JButton btnAnuluj = new JButton("Anuluj");

        btnZapisz.addActionListener(e -> {
            if (tytulField.getText().isEmpty() || listaPytan.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Podaj tytuł i dodaj przynajmniej jedno pytanie!");
                return;
            }
            zatwierdzono = true;
            dispose();
        });

        btnAnuluj.addActionListener(e -> dispose());
        bottom.add(btnZapisz);
        bottom.add(btnAnuluj);
        return bottom;
    }

    private void dodajPytanieDialog() {
        String tresc = JOptionPane.showInputDialog(this, "Treść pytania:");
        if (tresc == null || tresc.isEmpty()) return;

        String odpStr = JOptionPane.showInputDialog(this, "Opcje odpowiedzi (rozdzielone przecinkiem):", "Tak,Nie,Nie wiem");
        if (odpStr == null) return;

        List<String> opcje = Arrays.asList(odpStr.split(","));
        Pytanie p = new Pytanie(tresc, opcje);
        listaPytan.add(p);
        pytaniaModel.addElement(tresc + " (" + opcje.size() + " odp)");
    }

    public boolean isZatwierdzono() { return zatwierdzono; }

    public SzablonAnkiety getSzablon() {
        SzablonAnkiety s = new SzablonAnkiety(UUID.randomUUID().toString(), tytulField.getText());
        for (Pytanie p : listaPytan) s.dodajPytanie(p);
        return s;
    }
}