package klient.widoki;

import common.*;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.*;
import java.util.List;

public class KreatorAnkietyDialog extends JDialog {
    private JTextField tytulField = new JTextField(20);
    private DefaultListModel<String> pytaniaModel = new DefaultListModel<>();
    private JList<String> pytaniaList = new JList<>(pytaniaModel);
    private List<Pytanie> listaPytan = new ArrayList<>();
    private boolean zatwierdzono = false;
    private String idEdytowanego = null;
    private SzablonAnkiety szablonDoEdycji;

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
        JButton btnUsunPytanie = new JButton("Usuń zaznaczone pytanie");
        btnDodajPytanie.addActionListener(e -> dodajPytanieDialog());
        btnUsunPytanie.addActionListener(e -> {
            int idx = pytaniaList.getSelectedIndex();
            if (idx == -1) {
                JOptionPane.showMessageDialog(this, "Zaznacz pytanie do usunięcia");
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Czy na pewno chcesz usunąć to pytanie?",
                    "Potwierdzenie usunięcia",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                pytaniaModel.remove(idx);
                listaPytan.remove(idx);
            }
        });

        JPanel panelPrzyciski = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panelPrzyciski.add(btnDodajPytanie);
        panelPrzyciski.add(btnUsunPytanie);

        center.add(panelPrzyciski, BorderLayout.SOUTH);

        JPanel bottom = getJPanel();
        add(top, BorderLayout.NORTH);
        add(center, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
        setSize(400, 500);
        setLocationRelativeTo(owner);
    }



    public KreatorAnkietyDialog(Frame owner, SzablonAnkiety szablonDoEdycji) {
        this(owner);
        setTitle("Edycja Szablonu: " + szablonDoEdycji.getTytul());

        this.szablonDoEdycji = szablonDoEdycji;

        tytulField.setText(szablonDoEdycji.getTytul());
        listaPytan.clear();
        listaPytan.addAll(szablonDoEdycji.getPytania());

        pytaniaModel.clear();
        for (Pytanie p : listaPytan) {
            String typ = p.czyWielokrotnyWybor() ? "[Wielo]" : "[Jedno]";
            pytaniaModel.addElement(
                    typ + " " + p.getTrescPytania() + " (" + p.getOpcjeOdpowiedzi().size() + " odp)"
            );
        }

        this.idEdytowanego = szablonDoEdycji.getId();
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
        JTextField trescField = new JTextField();
        JTextField opcjeField = new JTextField("Tak,Nie,Nie wiem");
        JCheckBox wielokrotnyCheck = new JCheckBox("Wielokrotny wybór?");

        Object[] inputFields = {
                "Treść pytania:", trescField,
                "Opcje (rozdzielone przecinkiem):", opcjeField,
                "", wielokrotnyCheck
        };

        int result = JOptionPane.showConfirmDialog(this, inputFields, "Dodaj pytanie", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            String tresc = trescField.getText();
            String odpStr = opcjeField.getText();
            boolean isMulti = wielokrotnyCheck.isSelected();

            if (tresc.isEmpty() || odpStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Wszystkie pola muszą być wypełnione!");
                return;
            }

            List<String> opcje = Arrays.asList(odpStr.split(","));
            Pytanie p = new Pytanie(tresc, opcje, isMulti);

            listaPytan.add(p);
            String typStr = isMulti ? "[Wielo]" : "[Jedno]";
            pytaniaModel.addElement(typStr + " " + tresc + " (" + opcje.size() + " odp)");
        }
    }

    public boolean czyZatwierdzono() { return zatwierdzono; }

    public SzablonAnkiety getSzablon() {
        String id = (idEdytowanego != null) ? idEdytowanego : UUID.randomUUID().toString();
        SzablonAnkiety s;

        if (idEdytowanego != null) {
            s = new SzablonAnkiety(
                    idEdytowanego,
                    tytulField.getText(),
                    szablonDoEdycji.getDataUtworzenia(),
                    LocalDateTime.now()
            );
        } else {
            s = new SzablonAnkiety(
                    UUID.randomUUID().toString(),
                    tytulField.getText()
            );
        }

        for (Pytanie p : listaPytan) s.dodajPytanie(p);
        return s;
    }
}