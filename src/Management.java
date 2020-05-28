
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

public class Management extends javax.swing.JPanel {

    DefaultListModel model = new DefaultListModel();
    DefaultListModel sonucModel = new DefaultListModel();
    public int turProgramSayisi = 0;

    public void dbTurCek() {
        String turSorgu = "SELECT tname FROM tur";
        try {
            ResultSet rs = Main.statement.executeQuery(turSorgu);
            while (rs.next()) {
                String tname = rs.getString("tname");
                model.addElement(tname);
            }

        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void dbProgAra(String progname) {
        if (!progname.equals("")) {//kullanıcı hiçbirşey girmezse boşuna sorgu yapılmaz.
            String progSorgu = "SELECT pname FROM program where pname like " + "\'%" + progname + "%\'";
            try {
                ResultSet rs = Main.statement.executeQuery(progSorgu);
                while (rs.next()) {
                    String pname = rs.getString("pname");
                    sonucModel.addElement(pname);
                }

            } catch (SQLException ex) {
                Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            aramaSonuc.setVisible(false);
        }

    }

    public void programAlanOlustur(String turName) {
        programlarPanel.removeAll();
        programlarPanel.revalidate();
        programlarPanel.repaint();
        String programSorgu = "select COUNT(pid) FROM progtur where tid=(SELECT tid from tur WHERE tname=\"" + turName + "\")";
        try {
            ResultSet rs = Main.statement.executeQuery(programSorgu);
            rs.next();
            int psayi = rs.getInt(1);
            turProgramSayisi = psayi;
            programlarPanel.setPreferredSize(new Dimension(Main.ekranX - (225 + (Main.ekranX / 8)), ((Main.ekranY / 4) * turProgramSayisi / 3) + (Main.ekranY / 4)));
        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<JPanel> programlar = new ArrayList();
        for (int i = 0; i < turProgramSayisi; i++) {
            JPanel programAlan = new JPanel();
            programlar.add(programAlan);
            programAlan.setPreferredSize(new Dimension((Main.ekranX - (225 + (Main.ekranX / 8))) / 3, (Main.ekranY / 4)));
            programlarPanel.add(programAlan);
            programAlan.setVisible(true);
            programAlan.setBackground(Color.black);
            programAlan.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));

        }
        programSorgu = "select * from program where pid IN(select pid FROM progtur where tid=(SELECT tid from tur WHERE tname=\"" + turName + "\"))";
        try {
            ResultSet rs = Main.statement.executeQuery(programSorgu);
            int sayac = 0;
            while (rs.next()) {
                String isim = rs.getString("pname");
                String sayi = rs.getString("bolumSayisi");
                String puan = rs.getString("puan");
                String sure = rs.getString("size") + "dk.";
                String tip = rs.getString("tip");
                ozellikAta(programlar.get(sayac), isim);
                ozellikAta(programlar.get(sayac), "Tip: " + tip);
                ozellikAta(programlar.get(sayac), "Bölüm Sayısı: " + sayi);
                ozellikAta(programlar.get(sayac), "Puan: " + puan);
                ozellikAta(programlar.get(sayac), sure);
                JLabel izle = new JLabel("                         İZLE");
                izle.setBackground(new java.awt.Color(0, 0, 0));
                izle.setForeground(new java.awt.Color(255, 255, 255));
                izle.setFont(new java.awt.Font("Ink Free FB", 1, 30)); // NOI18N
                izle.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        

                    }
                });
                programlar.get(sayac).add(izle);
                sayac++;
                

            }

        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public void ozellikAta(JPanel j, String deger) {
        j.setLayout(new BoxLayout(j, BoxLayout.Y_AXIS));
        JLabel bir = new javax.swing.JLabel();
        bir.setBackground(new java.awt.Color(0, 0, 0));
        bir.setFont(new java.awt.Font("Times New Roman FB", 1, 24)); // NOI18N
        bir.setForeground(new java.awt.Color(255, 255, 255));
        bir.setText(deger);
        j.add(bir);
        j.add(Box.createVerticalStrut(10));
    }

    public Management(int id) {
        initComponents();
        this.setBackground(Color.black);
        aramaSonuc.setVisible(false);
        turler.setSize((Main.ekranX) / 8, (Main.ekranY));
        aramaIcon.setLocation(Main.ekranX - 225, 0);
        aramaText.setLocation(Main.ekranX - 180, 0);
        aramaSonuc.setLocation(Main.ekranX - 180, 40);
        list.setModel(model);
        list.setFixedCellHeight(50);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        sonuc.setModel(sonucModel);
        sonuc.setFixedCellHeight(30);
        DefaultListCellRenderer renderer1 = (DefaultListCellRenderer) list.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        dbTurCek();
        aramaSonuc.getVerticalScrollBar().setForeground(Color.black);
        aramaSonuc.getVerticalScrollBar().setBackground(Color.black);
        aramaSonuc.getHorizontalScrollBar().setForeground(Color.black);
        aramaSonuc.getHorizontalScrollBar().setBackground(Color.black);
        programlar.setLocation((Main.ekranX) / 8, 0);
        programlar.setSize(Main.ekranX - (225 + (Main.ekranX / 8)), Main.ekranY - 40);
        programlarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        programlarPanel.setPreferredSize(new Dimension(Main.ekranX - (225 + (Main.ekranX / 8)), Main.ekranY));
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        turler = new javax.swing.JScrollPane();
        list = new javax.swing.JList<>();
        aramaText = new javax.swing.JTextField();
        aramaIcon = new javax.swing.JLabel();
        aramaSonuc = new javax.swing.JScrollPane();
        sonuc = new javax.swing.JList<>();
        programlar = new javax.swing.JScrollPane();
        programlarPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1870, 900));
        setLayout(null);

        list.setBackground(new java.awt.Color(0, 0, 0));
        list.setForeground(new java.awt.Color(153, 153, 153));
        list.setModel(model);
        list.setVisibleRowCount(15);
        list.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        turler.setViewportView(list);

        add(turler);
        turler.setBounds(0, 0, 70, 250);

        aramaText.setBackground(new java.awt.Color(0, 0, 0));
        aramaText.setForeground(new java.awt.Color(255, 255, 255));
        aramaText.setMargin(new java.awt.Insets(6, 2, 2, 2));
        aramaText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                aramaTextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                aramaTextFocusLost(evt);
            }
        });
        aramaText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                aramaTextKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aramaTextKeyReleased(evt);
            }
        });
        add(aramaText);
        aramaText.setBounds(730, 0, 180, 40);

        aramaIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/arama.jpg"))); // NOI18N
        add(aramaIcon);
        aramaIcon.setBounds(680, 0, 40, 40);

        aramaSonuc.setBackground(new java.awt.Color(0, 0, 0));
        aramaSonuc.setForeground(new java.awt.Color(153, 153, 153));

        sonuc.setBackground(new java.awt.Color(0, 0, 0));
        sonuc.setForeground(new java.awt.Color(255, 255, 255));
        sonuc.setModel(sonucModel);
        aramaSonuc.setViewportView(sonuc);

        add(aramaSonuc);
        aramaSonuc.setBounds(730, 40, 180, 330);

        programlar.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        programlar.setPreferredSize(new java.awt.Dimension(50, 50));

        programlarPanel.setBackground(new java.awt.Color(0, 0, 0));
        programlarPanel.setLayout(null);
        programlar.setViewportView(programlarPanel);

        add(programlar);
        programlar.setBounds(70, 0, 380, 290);
    }// </editor-fold>//GEN-END:initComponents

    private void aramaTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_aramaTextKeyReleased
        sonucModel.clear();
        dbProgAra(aramaText.getText());
    }//GEN-LAST:event_aramaTextKeyReleased

    private void aramaTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_aramaTextFocusGained
        aramaSonuc.setVisible(true);
    }//GEN-LAST:event_aramaTextFocusGained

    private void aramaTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_aramaTextFocusLost
        aramaSonuc.setVisible(false);
    }//GEN-LAST:event_aramaTextFocusLost

    private void aramaTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_aramaTextKeyPressed

        aramaSonuc.setVisible(true);
    }//GEN-LAST:event_aramaTextKeyPressed

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        programAlanOlustur(list.getSelectedValue());
    }//GEN-LAST:event_listValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel aramaIcon;
    private javax.swing.JScrollPane aramaSonuc;
    private javax.swing.JTextField aramaText;
    private javax.swing.JList<String> list;
    private javax.swing.JScrollPane programlar;
    private javax.swing.JPanel programlarPanel;
    private javax.swing.JList<String> sonuc;
    private javax.swing.JScrollPane turler;
    // End of variables declaration//GEN-END:variables
}
