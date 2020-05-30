
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.text.DateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class Management extends javax.swing.JPanel {

    int turBarBoyut = 220;
    int uid;
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

    public void turProgramAlanOlustur(String turName) {
        programlarPanel.removeAll();
        programlarPanel.revalidate();
        programlarPanel.repaint();
        String programSorgu = "select COUNT(pid) FROM progtur where tid=(SELECT tid from tur WHERE tname=\"" + turName + "\")";
        try {
            ResultSet rs = Main.statement.executeQuery(programSorgu);
            rs.next();
            int psayi = rs.getInt(1);
            turProgramSayisi = psayi;
            programlarPanel.setPreferredSize(new Dimension(Main.ekranX - (225 + turBarBoyut), ((Main.ekranY / 4) * turProgramSayisi / 3) + (Main.ekranY / 4)));
        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<JPanel> programlar = new ArrayList();
        for (int i = 0; i < turProgramSayisi; i++) {
            JPanel programAlan = new JPanel();
            programlar.add(programAlan);
            programAlan.setPreferredSize(new Dimension((Main.ekranX - (225 + turBarBoyut)) / 3, (Main.ekranY / 4)));
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
                String pid = rs.getString("pid");
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
                int fontSize = 16;
                String izleYazisi = "                  İZLE";
                if (Main.ekranX >= 1920) {
                    fontSize = 30;
                    izleYazisi = "                         İZLE";
                } else if (Main.ekranX >= 1280) {
                    fontSize = 20;
                    izleYazisi = "                   İZLE";
                }
                JLabel izle = new JLabel(izleYazisi);
                izle.setBackground(new java.awt.Color(0, 0, 0));
                izle.setForeground(new java.awt.Color(255, 255, 255));
                izle.setFont(new java.awt.Font("Ink Free FB", 1, fontSize)); // NOI18N
                izle.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {

                    }
                });
                programlar.get(sayac).add(izle);
                sayac++;
                izle.addMouseListener(new MouseAdapter() {
                    public String isim1 = isim;
                    public String progid = pid;
                    public String puanım = puan;
                    public String sure = "00:00:00";
                    public boolean izlenmismi;
                    public Time baslangic = Time.valueOf("11:11:11");
                    public String watchdate;
                    public Thread tired;
                    public String toplam = "00:00:00";

                    public void mouseClicked(MouseEvent e) {
                        String dateSorgu = "SELECT datetime('now','localtime')";
                        try {
                            ResultSet rs = Main.statement.executeQuery(dateSorgu);
                            rs.next();
                            watchdate = rs.getString(1);
                        } catch (SQLException ex) {

                        }
                        String bolum = "1";
                        String puan = "0";

                        String kontrolSorgu = "select * FROM kullaniciprog where pid=\"" + progid + "\" AND uid=\"" + uid + "\"";
                        try {
                            ResultSet rs = Main.statement.executeQuery(kontrolSorgu);
                            rs.next();
                            izlenmismi = true;
                            bolum = rs.getString("aktifbolum");
                            puan = rs.getString("puan");
                            sure = rs.getString("watchtime");
                        } catch (SQLException ex) {
                            izlenmismi = false;

                        }
                        JDialog dialog = new JDialog();      
                        dialog.setTitle("DİZİ&FİLM");
                        JPanel panel = new JPanel();
                        panel.setBackground(Color.black);
                        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
                        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

                        JLabel isimLabel = new JLabel(isim1);
                        isimLabel.setBounds(10, 10, 40, 20);
                        isimLabel.setFont(new java.awt.Font("Times New Roman FB", 1, 20));
                        isimLabel.setAlignmentX(CENTER_ALIGNMENT);
                        isimLabel.setForeground(Color.white);
                        panel.add(isimLabel);

                        panel.add(Box.createVerticalStrut(20));

                        JLabel bolumLabel = new JLabel("Kaldığın Bölüm: " + bolum);
                        bolumLabel.setBounds(10, 10, 40, 20);
                        bolumLabel.setFont(new java.awt.Font("Times New Roman FB", 1, 20));
                        bolumLabel.setForeground(Color.white);

                        bolumLabel.setAlignmentX(CENTER_ALIGNMENT);
                        panel.add(bolumLabel);
                        panel.add(Box.createVerticalStrut(20));

                        String puanlar[] = {"Puan Ver", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
                        JComboBox puanlama = new JComboBox(puanlar);
                        puanlama.setBackground(Color.black);
                        puanlama.setForeground(Color.white);

                        puanlama.setAlignmentX(CENTER_ALIGNMENT);
                        puanlama.setSelectedIndex(Integer.valueOf(puan));
                        panel.add(puanlama);
                        panel.add(Box.createVerticalStrut(20));

                        String sureSorgu = "SELECT time('now','localtime')";
                        try {
                            ResultSet rs = Main.statement.executeQuery(sureSorgu);
                            rs.next();
                            baslangic = Time.valueOf(rs.getString(1));
                        } catch (SQLException ex) {
                        }
                        JLabel sureLabel = new JLabel("Geçen Süre: " + sure);
                        sureLabel.setFont(new java.awt.Font("Times New Roman FB", 1, 20));

                        sureLabel.setAlignmentX(CENTER_ALIGNMENT);
                        sureLabel.setForeground(Color.white);
                        sureLabel.setBounds(10, 10, 40, 20);
                        panel.add(sureLabel);

                        tired = new Thread() {

                            public void run() {
                                String sureSorgu = "SELECT time('now','localtime')";
                                LocalTime izlenen = LocalTime.of(0, 0, 0);
                                if (izlenmismi == true) {
                                    String splitt[] = sure.split(":");
                                    izlenen = LocalTime.of(Integer.valueOf(splitt[0]), Integer.valueOf(splitt[1]), Integer.valueOf(splitt[2]));

                                } else {

                                }
                                long gecenvakit = (izlenen.getSecond() * 1000) + (izlenen.getMinute() * 1000 * 60) + (izlenen.getHour() * 1000 * 60 * 60);
                                while (true) {
                                    try {
                                        this.sleep(999);
                                    } catch (InterruptedException ex) {
                                        Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
                                    }
                                    gecenvakit += 1000;
                                    long diffSeconds = gecenvakit / 1000 % 60;
                                    long diffMinutes = gecenvakit / (60 * 1000) % 60;
                                    long diffHours = gecenvakit / (60 * 60 * 1000) % 24;
                                    toplam = String.valueOf(diffHours) + ":" + String.valueOf(diffMinutes) + ":" + String.valueOf(diffSeconds);
                                    sureLabel.setText("Geçen Süre: " + toplam);
                                }
                            }
                        };
                        tired.start();
                        tired.suspend();
                        JLabel pauseIcon = new JLabel();
                        JLabel playIcon = new JLabel();
                        pauseIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pauseIcon.jpg")));
                        playIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/playIcon.jpg")));
                        pauseIcon.setVisible(false);
                        playIcon.setVisible(true);
                        panel.add(pauseIcon);
                        panel.add(playIcon);
                        pauseIcon.addMouseListener(new MouseAdapter() {

                            public void mouseClicked(MouseEvent e) {
                                playIcon.setVisible(true);
                                pauseIcon.setVisible(false);
                                tired.suspend();
                            }
                        });
                        playIcon.addMouseListener(new MouseAdapter() {

                            public void mouseClicked(MouseEvent e) {
                                playIcon.setVisible(false);
                                pauseIcon.setVisible(true);
                                synchronized (tired) {
                                    tired.resume();
                                }
                            }
                        });

                        dialog.add(panel);
                        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                        dialog.setSize(400, 300);
                        dialog.setLocationRelativeTo(null);
                        dialog.setVisible(true);
                        dialog.addWindowListener(new WindowAdapter() {
                            public boolean izlenmis = izlenmismi;

                            public void windowClosed(WindowEvent e) {
                                tired.stop();
                                String dbguncelle;
                                if (izlenmis == true) {
                                    dbguncelle = "UPDATE kullaniciprog SET watchdate='" + watchdate + "' , watchtime= '" + toplam + "' , aktifbolum='"
                                            + 1 + "' , puan='" + puanlama.getSelectedIndex() + "' WHERE uid='" + uid + "' AND pid='" + progid + "'";
                                } else {
                                    dbguncelle = "INSERT INTO kullaniciprog('watchdate','watchtime','aktifbolum','puan','pid','uid')"
                                            + "VALUES('" + watchdate + "','" + toplam + "','" + 1 + "','" + puanlama.getSelectedIndex() + "','" + progid + "','" + uid + "')";

                                }
                                System.out.println(dbguncelle);
                                try {
                                    Main.statement.executeUpdate(dbguncelle);

                                } catch (SQLException ex1) {
                                    Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
                                }
                            }

                        });

                    }
                });

            }

        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void ozellikAta(JPanel j, String deger) {
        int fontSize = 12;
        if (Main.ekranX >= 1920) {
            fontSize = 24;
        } else if (Main.ekranX >= 1280) {
            fontSize = 16;
        }

        j.setLayout(new BoxLayout(j, BoxLayout.Y_AXIS));
        JLabel bir = new javax.swing.JLabel();
        bir.setBackground(new java.awt.Color(0, 0, 0));
        bir.setFont(new java.awt.Font("Times New Roman FB", 1, fontSize)); // NOI18N
        bir.setForeground(new java.awt.Color(255, 255, 255));
        bir.setText(deger);
        j.add(bir);
        j.add(Box.createVerticalStrut(10));
    }

    public String sum = "00:00:00";
    public boolean izlendimi = false;
    public String wd;
    public String surec = "00:00:00";

    public void programAlanOlustur(String progName) {
        String programSorgu = "select * FROM program where pname= '" + progName + "'";
        try {
            ResultSet rs = Main.statement.executeQuery(programSorgu);
            rs.next();
            String pid = rs.getString("pid");
            String isim = rs.getString("pname");

            Time baslangic = Time.valueOf("11:11:11");
            Thread tired;

            String dateSorgu = "SELECT datetime('now','localtime')";
            try {
                ResultSet rs1 = Main.statement.executeQuery(dateSorgu);
                rs1.next();
                wd = rs1.getString(1);
            } catch (SQLException ex) {

            }
            String bolum = "1";
            String puan = "0";

            String kontrolSorgu = "select * FROM kullaniciprog where pid=\"" + pid + "\" AND uid=\"" + uid + "\"";
            try {
                ResultSet rs1 = Main.statement.executeQuery(kontrolSorgu);
                rs1.next();
                izlendimi = true;
                bolum = rs1.getString("aktifbolum");
                puan = rs1.getString("puan");
                surec = rs1.getString("watchtime");
            } catch (SQLException ex) {
                izlendimi = false;

            }
            JDialog dialog = new JDialog();
            JPanel panel = new JPanel();
            panel.setBackground(Color.black);
            panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
            panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 0));

            JLabel isimLabel = new JLabel(isim);
            isimLabel.setBounds(10, 10, 40, 20);
            isimLabel.setFont(new java.awt.Font("Times New Roman FB", 1, 20));
            isimLabel.setAlignmentX(CENTER_ALIGNMENT);
            isimLabel.setForeground(Color.white);
            panel.add(isimLabel);

            panel.add(Box.createVerticalStrut(20));

            JLabel bolumLabel = new JLabel("Kaldığın Bölüm: " + bolum);
            bolumLabel.setBounds(10, 10, 40, 20);
            bolumLabel.setFont(new java.awt.Font("Times New Roman FB", 1, 20));
            bolumLabel.setForeground(Color.white);

            bolumLabel.setAlignmentX(CENTER_ALIGNMENT);
            panel.add(bolumLabel);
            panel.add(Box.createVerticalStrut(20));

            String puanlar[] = {"Puan Ver", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10"};
            JComboBox puanlama = new JComboBox(puanlar);
            puanlama.setBackground(Color.black);
            puanlama.setForeground(Color.white);

            puanlama.setAlignmentX(CENTER_ALIGNMENT);
            puanlama.setSelectedIndex(Integer.valueOf(puan));
            panel.add(puanlama);
            panel.add(Box.createVerticalStrut(20));

            String sureSorgu = "SELECT time('now','localtime')";
            try {
                ResultSet rs1 = Main.statement.executeQuery(sureSorgu);
                rs.next();
                baslangic = Time.valueOf(rs.getString(1));
            } catch (SQLException ex) {
            }
            JLabel sureLabel = new JLabel("Geçen Süre: " + surec);
            sureLabel.setFont(new java.awt.Font("Times New Roman FB", 1, 20));

            sureLabel.setAlignmentX(CENTER_ALIGNMENT);
            sureLabel.setForeground(Color.white);
            sureLabel.setBounds(10, 10, 40, 20);
            panel.add(sureLabel);

            tired = new Thread() {

                public void run() {
                    String sureSorgu = "SELECT time('now','localtime')";
                    LocalTime izlenen = LocalTime.of(0, 0, 0);
                    if (izlendimi == true) {
                        String splitt[] = surec.split(":");
                        izlenen = LocalTime.of(Integer.valueOf(splitt[0]), Integer.valueOf(splitt[1]), Integer.valueOf(splitt[2]));

                    } else {

                    }
                    long gecenvakit = (izlenen.getSecond() * 1000) + (izlenen.getMinute() * 1000 * 60) + (izlenen.getHour() * 1000 * 60 * 60);
                    while (true) {
                        try {
                            this.sleep(980);
                        } catch (InterruptedException ex) {
                            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        gecenvakit += 1000;
                        long diffSeconds = gecenvakit / 1000 % 60;
                        long diffMinutes = gecenvakit / (60 * 1000) % 60;
                        long diffHours = gecenvakit / (60 * 60 * 1000) % 24;
                        sum = String.valueOf(diffHours) + ":" + String.valueOf(diffMinutes) + ":" + String.valueOf(diffSeconds);
                        sureLabel.setText("Geçen Süre: " + sum);
                    }
                }
            };
            tired.start();
            try {
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
            }
            tired.suspend();
            JLabel pauseIcon = new JLabel();
            JLabel playIcon = new JLabel();
            pauseIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pauseIcon.jpg")));
            playIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/playIcon.jpg")));
            pauseIcon.setVisible(false);
            playIcon.setVisible(true);
            panel.add(pauseIcon);
            panel.add(playIcon);
            pauseIcon.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    playIcon.setVisible(true);
                    pauseIcon.setVisible(false);
                    tired.suspend();
                }
            });
            playIcon.addMouseListener(new MouseAdapter() {

                public void mouseClicked(MouseEvent e) {
                    playIcon.setVisible(false);
                    pauseIcon.setVisible(true);
                    synchronized (tired) {
                        tired.resume();
                    }
                }
            });

            dialog.add(panel);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setSize(400, 300);
            dialog.setLocationRelativeTo(null);
            dialog.setVisible(true);
            dialog.addWindowListener(new WindowAdapter() {
                public boolean izlenmis = izlendimi;

                public void windowClosed(WindowEvent e) {
                    tired.stop();
                    String dbguncelle;
                    if (izlenmis == true) {
                        dbguncelle = "UPDATE kullaniciprog SET watchdate='" + wd + "' , watchtime= '" + sum + "' , aktifbolum='"
                                + 1 + "' , puan='" + puanlama.getSelectedIndex() + "' WHERE uid='" + uid + "' AND pid='" + pid + "'";
                    } else {
                        dbguncelle = "INSERT INTO kullaniciprog('watchdate','watchtime','aktifbolum','puan','pid','uid')"
                                + "VALUES('" + wd + "','" + sum + "','" + 1 + "','" + puanlama.getSelectedIndex() + "','" + pid + "','" + uid + "')";

                    }
                    System.out.println(dbguncelle);
                    try {
                        Main.statement.executeUpdate(dbguncelle);

                    } catch (SQLException ex1) {
                        Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }

            });

        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }
        sum = "00:00:00";
        izlendimi = false;
        surec = "00:00:00";
    }

    public Management(int id) {
        uid = id;
        initComponents();
        this.setBackground(Color.black);
        aramaSonuc.setVisible(false);
        turler.setSize(turBarBoyut, (Main.ekranY));
        aramaIcon.setLocation(Main.ekranX - 225, 0);
        aramaText.setLocation(Main.ekranX - 180, 0);
        aramaText.setText("Dizi veya Film Ara");
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
        programlar.setLocation(turBarBoyut, 0);
        programlar.setSize(Main.ekranX - (225 + turBarBoyut), Main.ekranY - 40);
        programlarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        programlarPanel.setPreferredSize(new Dimension(Main.ekranX - (225 + turBarBoyut), Main.ekranY));
        sonuc.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                programAlanOlustur(sonuc.getSelectedValue());
            }
        });
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
        aramaText.selectAll();
    }//GEN-LAST:event_aramaTextFocusGained

    private void aramaTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_aramaTextFocusLost
        aramaSonuc.setVisible(false);
        aramaText.setText("Dizi veya Film Ara");
    }//GEN-LAST:event_aramaTextFocusLost

    private void aramaTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_aramaTextKeyPressed

        aramaSonuc.setVisible(true);
    }//GEN-LAST:event_aramaTextKeyPressed

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        turProgramAlanOlustur(list.getSelectedValue());
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
