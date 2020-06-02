
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/*  Değişken Tanımlamaları                    
        JLabel aramaIcon;               // program arama barının(JTextField ProgramProgramAramaText) solundaki görsel buton
        JScrollPane aramaSonucScroll;   // program arayınca çıkan aramaSonucList jList'ini sarmalar ve scroll yeteneği kazandırır.
        JList<String> aramaSonucList;   // ProgramProgramAramaText(JTextField) ile program arayınca databasede eşleşen programların listelendiği JList
        JTextField ProgramAramaText;    // sağ üstte program aramak için  JTextField
        JScrollPane turlerScroll;       // turlerList'i sarmalar ve scroll özelliği kazandırır.
	JList<String> turlerList;       // ekranın solunda databasedeki türleri listeleyen Jlist
        JScrollPane programlar;         // programlarPanel(JPanel)'i sarmalayan JScrollPane, scroll yeteneği kazandırır.
        JPanel programlarPanel;         // ekranın ortasındaki, databasedeki programların listelendiği Jpanel       
    Değişken Tanımlamalarının Sonu   */
public class Management extends javax.swing.JPanel {

    int turBarBoyut = 220;              // ekranın sulunda bulunan turlerin listelendiği alananın sabit boyutu
    int uid;                            // oturum açan kullanıcının id sinin atandığı değişken
    int aramaIconBoyut = 40;              // sağ üstteki arama ıconunun x i ve y si
    int aramaTextBoyut = 180;             // sağ üstteki ProgramAramaText inin x'i
    DefaultListModel turListModel = new DefaultListModel();   // turList Jlist'inin modeli
    DefaultListModel aramaSonucModel = new DefaultListModel();  // aramaSonucList Jlist'inin modeli
    public int turProgramSayisi = 0;    //  kullanıcının seçtiği aktif türe ait program sayısı "turProgramAlanOlustur" çağırıldıkça güncellenir.

    //  RESPONSİVE DESİGN  //
    public Management(int id) { // Oturum açan kullanıcının uid'sini aldık
        uid = id;               // deger olarak alınan idyi atadık. Oturum açan kullanıcının uid'si.
        initComponents();
        this.setBackground(Color.black);
        aramaSonucScroll.setVisible(false);
        turlerScroll.setSize(turBarBoyut, (Main.ekranY));
        aramaIcon.setLocation(Main.ekranX - (aramaIconBoyut + aramaTextBoyut + 5), 0);
        ProgramAramaText.setLocation(Main.ekranX - aramaTextBoyut, 0);
        ProgramAramaText.setText("Dizi veya Film Ara");
        aramaSonucScroll.setLocation(Main.ekranX - aramaTextBoyut, aramaIconBoyut);
        turlerList.setModel(turListModel);
        turlerList.setFixedCellHeight(50);
        DefaultListCellRenderer renderer = (DefaultListCellRenderer) turlerList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        aramaSonucList.setModel(aramaSonucModel);
        aramaSonucList.setFixedCellHeight(30);
        DefaultListCellRenderer renderer1 = (DefaultListCellRenderer) turlerList.getCellRenderer();
        renderer.setHorizontalAlignment(SwingConstants.CENTER);
        dbTurCek();
        aramaSonucScroll.getVerticalScrollBar().setForeground(Color.black);
        aramaSonucScroll.getVerticalScrollBar().setBackground(Color.black);
        aramaSonucScroll.getHorizontalScrollBar().setForeground(Color.black);
        aramaSonucScroll.getHorizontalScrollBar().setBackground(Color.black);
        programlar.setLocation(turBarBoyut, 0);
        programlar.setSize(Main.ekranX - ((aramaIconBoyut + aramaTextBoyut + 5) + turBarBoyut), Main.ekranY - aramaIconBoyut);
        programlarPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 0, 0));
        programlarPanel.setPreferredSize(new Dimension(Main.ekranX - ((aramaIconBoyut + aramaTextBoyut + 5) + turBarBoyut), Main.ekranY));
        aramaSonucList.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                programAlanOlustur(aramaSonucList.getSelectedValue());
            }
        });
    }

    public void dbTurCek() { // veritabanından turlerin isimlerini çeker ve bunları turListModel'e ekler
        String turSorgu = "SELECT tname FROM tur";
        try {
            ResultSet rs = Main.statement.executeQuery(turSorgu);
            while (rs.next()) {
                String tname = rs.getString("tname");
                turListModel.addElement(tname);
            }

        } catch (SQLException ex) {
            System.out.println(ex.getMessage());
        }
    }

    public void dbProgAra(String progname) {    //  kullanıcının ProgramAramaText'e yazdığı stringi veritabınında program ismi olarak sorgular
        // ve arama sonuçlarını aramaSonucModel'ekler.

        if (!progname.equals("")) {//kullanıcı hiçbirşey girmezse boşuna sorgu yapılmaz.
            String progSorgu = "SELECT pname FROM program where pname like " + "\'%" + progname + "%\'";
            try {
                ResultSet rs = Main.statement.executeQuery(progSorgu);
                while (rs.next()) {
                    String pname = rs.getString("pname");
                    aramaSonucModel.addElement(pname);
                }

            } catch (SQLException ex) {
                Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            aramaSonucScroll.setVisible(false);
        }

    }

    public void turProgramAlanOlustur(String turName) {     //  kullanıcının turlerList de seçtiği ture gore programPanele programları yerlertirir ve günceller

        programlarPanel.removeAll();
        programlarPanel.revalidate();                       //kullanıcının farklı tür seçmesine karşılık programlarPanel öncelikle sıfırlanır
        programlarPanel.repaint();

        String programSorgu = "select COUNT(pid) FROM progtur where tid=(SELECT tid from tur WHERE tname=\"" + turName + "\")";
        try {
            ResultSet rs = Main.statement.executeQuery(programSorgu);
            rs.next();
            int psayi = rs.getInt(1);
            turProgramSayisi = psayi;
            programlarPanel.setPreferredSize(new Dimension(Main.ekranX - ((aramaIconBoyut + aramaTextBoyut + 5) + turBarBoyut), ((Main.ekranY / 4) * turProgramSayisi / 3) + (Main.ekranY / 4)));
        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }

        ArrayList<JPanel> programlarlist = new ArrayList();         // program sayısı kade panel üretilir(daha sonra bunlar programlarPanel'e eklenir.
        for (int i = 0; i < turProgramSayisi; i++) {
            JPanel programAlan = new JPanel();
            programlarlist.add(programAlan);
            programAlan.setPreferredSize(new Dimension((Main.ekranX - ((aramaIconBoyut + aramaTextBoyut + 5) + turBarBoyut)) / 3, (Main.ekranY / 4)));
            programlarPanel.add(programAlan);
            programAlan.setVisible(true);
            programAlan.setBackground(Color.black);
            programAlan.setBorder(javax.swing.BorderFactory.createMatteBorder(1, 1, 1, 1, new java.awt.Color(255, 255, 255)));

        }

        programSorgu = "select * from program where pid IN(select pid FROM progtur where tid=(SELECT tid from tur WHERE tname=\"" + turName + "\"))";
        try {
            ResultSet rs = Main.statement.executeQuery(programSorgu);
            int sayac = 0;  //turProgramSayisi kadar donecek olusturulan programlar arraylistindeki panelleri özelleştirir.
            while (rs.next()) {
                String pid = rs.getString("pid");
                String isim = rs.getString("pname");
                String sayi = rs.getString("bolumSayisi");
                String puan = rs.getString("puan");
                String sure = rs.getString("size") + "dk.";
                String tip = rs.getString("tip");
                ozellikAta(programlarlist.get(sayac), isim);
                ozellikAta(programlarlist.get(sayac), "Tip: " + tip);
                ozellikAta(programlarlist.get(sayac), "Bölüm Sayısı: " + sayi);
                ozellikAta(programlarlist.get(sayac), "Puan: " + puan);
                ozellikAta(programlarlist.get(sayac), sure);
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

                programlarlist.get(sayac).add(izle);
                sayac++;
                izle.addMouseListener(new MouseAdapter() {      //izle butonlarına tıklanınca yapılacaklar
                    public String isim1 = isim;
                    public String progid = pid;
                    public String puanım = puan;
                    public String sure = "00:00:00";
                    public boolean izlenmismi;
                    public Time baslangic = Time.valueOf("11:11:11");
                    public String watchdate;
                    public Thread tired;
                    public String toplam = "00:00:00";

                    @Override
                    public void mouseClicked(MouseEvent e) {        //jDialog olşturulur ve seçilen programın kullanıcı ile ilişkisi jDialogda gösterilir.
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

                        tired = new Thread() {  //kullanıcı diziyi izlediği sürece bu thread çalışır.

                            @Override
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
                        tired.start();  //threadi başlattık
                        tired.suspend();    //thread dürdü çünkü kullanıcının başlatması bekleniyor.

                        JLabel pauseIcon = new JLabel();
                        JLabel playIcon = new JLabel();
                        pauseIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/pauseIcon.jpg")));
                        playIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/playIcon.jpg")));
                        pauseIcon.setVisible(false);
                        playIcon.setVisible(true);
                        panel.add(pauseIcon);
                        panel.add(playIcon);

                        pauseIcon.addMouseListener(new MouseAdapter() { //kullanıcı pause ikonuna tıklarsa thread durur

                            public void mouseClicked(MouseEvent e) {
                                playIcon.setVisible(true);
                                pauseIcon.setVisible(false);
                                tired.suspend();
                            }
                        });
                        playIcon.addMouseListener(new MouseAdapter() {  //kullanıcı play ikonuna tıklarsa thread devam eder.

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

                            @Override
                            public void windowClosed(WindowEvent e) {   // dalog penceresi kapanırsa yapılacaklar
                                tired.stop();
                                String dbguncelle;
                                if (izlenmis == true) { // kullanıcı daha önce programı izlemişse  bilgiler veritabanında update edilir.
                                    dbguncelle = "UPDATE kullaniciprog SET watchdate='" + watchdate + "' , watchtime= '" + toplam + "' , aktifbolum='"
                                            + 1 + "' , puan='" + puanlama.getSelectedIndex() + "' WHERE uid='" + uid + "' AND pid='" + progid + "'";
                                } else {    //kullanıcı daha önce programı izlememişse bilgiler veritabanına eklenir
                                    dbguncelle = "INSERT INTO kullaniciprog('watchdate','watchtime','aktifbolum','puan','pid','uid')"
                                            + "VALUES('" + watchdate + "','" + toplam + "','" + 1 + "','" + puanlama.getSelectedIndex() + "','" + progid + "','" + uid + "')";

                                }
                                //System.out.println(dbguncelle); //konsol çıktısı
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

    public void ozellikAta(JPanel j, String deger) {        // kullanıcının bilgisayarının ekran değerlerine gore özellik atanır.
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

    // programAlanOlustur fonksiyonu için tanımlanan değişkenler//
    public String sum = "00:00:00";
    public boolean izlendimi = false;
    public String wd;
    public String surec = "00:00:00";
    // programAlanOlustur fonksiyonu için tanımlanan değişkenlerin sonu //

    public void programAlanOlustur(String progName) {   // kullanıcı ProgramAramaText ile program arayınca listeden program seçerse gerçekleşecekler.
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

                @Override
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

                @Override
                public void mouseClicked(MouseEvent e) {
                    playIcon.setVisible(true);
                    pauseIcon.setVisible(false);
                    tired.suspend();
                }
            });
            playIcon.addMouseListener(new MouseAdapter() {

                @Override
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

                @Override
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
                    //System.out.println(dbguncelle); // konsol çıktısı
                    try {
                        Main.statement.executeUpdate(dbguncelle);

                    } catch (SQLException ex1) {
                        Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
                    }

                    sum = "00:00:00";
                    izlendimi = false;
                    surec = "00:00:00";
                }

            });

        } catch (SQLException ex) {
            Logger.getLogger(Management.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        turlerScroll = new javax.swing.JScrollPane();
        turlerList = new javax.swing.JList<>();
        ProgramAramaText = new javax.swing.JTextField();
        aramaIcon = new javax.swing.JLabel();
        aramaSonucScroll = new javax.swing.JScrollPane();
        aramaSonucList = new javax.swing.JList<>();
        programlar = new javax.swing.JScrollPane();
        programlarPanel = new javax.swing.JPanel();

        setPreferredSize(new java.awt.Dimension(1870, 900));
        setLayout(null);

        turlerList.setBackground(new java.awt.Color(0, 0, 0));
        turlerList.setForeground(new java.awt.Color(153, 153, 153));
        turlerList.setModel(turListModel);
        turlerList.setVisibleRowCount(15);
        turlerList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChanged(evt);
            }
        });
        turlerScroll.setViewportView(turlerList);

        add(turlerScroll);
        turlerScroll.setBounds(0, 0, 70, 250);

        ProgramAramaText.setBackground(new java.awt.Color(0, 0, 0));
        ProgramAramaText.setForeground(new java.awt.Color(255, 255, 255));
        ProgramAramaText.setMargin(new java.awt.Insets(6, 2, 2, 2));
        ProgramAramaText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                aramaTextFocusGained(evt);
            }
            public void focusLost(java.awt.event.FocusEvent evt) {
                aramaTextFocusLost(evt);
            }
        });
        ProgramAramaText.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                aramaTextKeyPressed(evt);
            }
            public void keyReleased(java.awt.event.KeyEvent evt) {
                aramaTextKeyReleased(evt);
            }
        });
        add(ProgramAramaText);
        ProgramAramaText.setBounds(730, 0, 180, 40);

        aramaIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/arama.jpg"))); // NOI18N
        add(aramaIcon);
        aramaIcon.setBounds(680, 0, 40, 40);

        aramaSonucScroll.setBackground(new java.awt.Color(0, 0, 0));
        aramaSonucScroll.setForeground(new java.awt.Color(153, 153, 153));

        aramaSonucList.setBackground(new java.awt.Color(0, 0, 0));
        aramaSonucList.setForeground(new java.awt.Color(255, 255, 255));
        aramaSonucList.setModel(aramaSonucModel);
        aramaSonucScroll.setViewportView(aramaSonucList);

        add(aramaSonucScroll);
        aramaSonucScroll.setBounds(730, 40, 180, 330);

        programlar.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
        programlar.setPreferredSize(new java.awt.Dimension(50, 50));

        programlarPanel.setBackground(new java.awt.Color(0, 0, 0));
        programlarPanel.setLayout(null);
        programlar.setViewportView(programlarPanel);

        add(programlar);
        programlar.setBounds(70, 0, 380, 290);
    }// </editor-fold>//GEN-END:initComponents

    private void aramaTextKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_aramaTextKeyReleased
        aramaSonucModel.clear();    // aramaText'e yazılıp silindikçe aramaSonucModel'i güncellemek için
        dbProgAra(ProgramAramaText.getText());
    }//GEN-LAST:event_aramaTextKeyReleased

    private void aramaTextFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_aramaTextFocusGained
        aramaSonucScroll.setVisible(true);
        ProgramAramaText.selectAll();
    }//GEN-LAST:event_aramaTextFocusGained

    private void aramaTextFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_aramaTextFocusLost
        aramaSonucScroll.setVisible(false);
        ProgramAramaText.setText("Dizi veya Film Ara");
    }//GEN-LAST:event_aramaTextFocusLost

    private void aramaTextKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_aramaTextKeyPressed

        aramaSonucScroll.setVisible(true);
    }//GEN-LAST:event_aramaTextKeyPressed

    private void listValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChanged
        turProgramAlanOlustur(turlerList.getSelectedValue());
    }//GEN-LAST:event_listValueChanged


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel aramaIcon;
    private javax.swing.JScrollPane aramaSonucScroll;
    private javax.swing.JTextField ProgramAramaText;
    private javax.swing.JList<String> turlerList;
    private javax.swing.JScrollPane programlar;
    private javax.swing.JPanel programlarPanel;
    private javax.swing.JList<String> aramaSonucList;
    private javax.swing.JScrollPane turlerScroll;
    // End of variables declaration//GEN-END:variables
}
