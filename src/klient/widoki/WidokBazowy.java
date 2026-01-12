package klient.widoki;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;


public abstract class WidokBazowy extends JPanel {

    protected DefaultTableModel modelTabeli;
    protected JTable tabela;
    protected JButton btnDodaj, btnEdytuj, btnUsun, btnOdswiez;

    public WidokBazowy(String[] kolumny) {
        setLayout(new BorderLayout());

        modelTabeli = new DefaultTableModel(kolumny, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        tabela = new JTable(modelTabeli);
        tabela.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        add(new JScrollPane(tabela), BorderLayout.CENTER);

        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setPreferredSize(new Dimension(1000, 45));

        JPanel left = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnOdswiez = new JButton("Odśwież");
        left.add(btnOdswiez);

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btnDodaj = new JButton("Dodaj");
        btnEdytuj = new JButton("Edytuj");
        btnUsun = new JButton("Usuń");

        right.add(btnDodaj);
        right.add(btnEdytuj);
        right.add(btnUsun);

        topBar.add(left, BorderLayout.WEST);
        topBar.add(right, BorderLayout.EAST);

        add(topBar, BorderLayout.NORTH);

        btnOdswiez.addActionListener(e -> odswiezDane());
        btnDodaj.addActionListener(e -> akcjaDodaj());
        btnEdytuj.addActionListener(e -> akcjaEdytuj());
        btnUsun.addActionListener(e -> akcjaUsun());
    }


    protected abstract void odswiezDane();
    protected abstract void akcjaDodaj();
    protected abstract void akcjaEdytuj();
    protected abstract void akcjaUsun();
    public JButton getBtnDodaj() { return btnDodaj; }
    public JButton getBtnEdytuj() { return btnEdytuj; }
    public JButton getBtnUsun() { return btnUsun; }

    protected String pobierzZaznaczoneId() {
        int row = tabela.getSelectedRow();
        if (row == -1) return null;
        return modelTabeli.getValueAt(row, 0).toString();
    }
}
