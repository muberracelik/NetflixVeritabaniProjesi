
import java.awt.Color;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

public class Register extends javax.swing.JPanel {

    int yapildimi = 0; // tarih seçme bileşeninin bugundan dolayı ilk girişte  bdayseciciPropertyChange direk çalışıyor bunun olumsuz sonuçlarını engellemek için
    int secilenTurSayisi = 0; // kullanıcının 3 ten fazla beğenilen tür seçememesi için
    ArrayList<String> secilenTurler = new ArrayList<>();    // kullanıcının seçtiği 3 türün tutulduğu liste

    public Register() {
        initComponents();
        this.setBackground(Color.BLACK);
        parola.setEchoChar((char) 0);
        parolaOnay.setEchoChar((char) 0);
        jPanel2.setVisible(false);
        jPanel3.setVisible(false);
        soz.setText("<html> <p style=\"text-align:center\">EN FAVORİ DİZİ VE FİLMLER İÇİN<p/> </html>");
        soz1.setText("<html> <p>SEVDİĞİN 3  TÜRÜ SEÇ<p/> </html>");
        tur3.setHorizontalAlignment(SwingConstants.CENTER);
        tur1.setHorizontalAlignment(SwingConstants.CENTER);
        tur2.setHorizontalAlignment(SwingConstants.CENTER);
    }

    // kullanıcının girdiği e-postanın kontrolü için kullanılan regex
    public final Pattern VALID_EMAIL_ADDRESS_REGEX = Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

    public boolean dogrulama(String mail) { // kullanıcının girdiği e-postanın kontrolü
        Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(mail);
        return matcher.find();
    }

    public void turKontrol(JCheckBox j) { // Kullanıcın seçtiği türleri listeye ekleyen ve 3 ten fazla tür secilmesini engellemek icin yazilan fonksiyon
        if (j.isSelected()) {
            if (secilenTurSayisi < 3) {
                secilenTurSayisi++;
                secilenTurler.add(j.getText());
            } else {
                j.setSelected(false);
            }
        } else {
            secilenTurler.remove(j.getText());
            j.setSelected(false);
            secilenTurSayisi--;
        }
    }

    public void kayitYap() {    // kullanıcının girdiği bilgileri kontrol edip veritabanına kullanıcıyı ekleme fonksiyonu
        
        boolean hata = false;   // kullanıcı herhangi bir isterde hata yaparsa bu değişken true olur ve kayıt engellenir.
        
        if (isim.getText().equals("") || isim.getText().equals("Ad-Soyad")) {   // kullanıcının isim girmediyse
            isim.setForeground(Color.red);
            isim.setText("*Ad-Soyad giriniz!");
            hata = true;
        } else {

        }

        if (bday.getText().equals("") || bday.getText().equals("Doğum Tarihi")) { // kullanıcı doğum tarihi girmediyse
            bday.setForeground(Color.red);
            bday.setText("*Doğum Tarihi giriniz!");
            hata = true;
        } else {

        }

        if (eposta.getText().equals("") || eposta.getText().equals("E-posta")) { // kullanıcının e posta girmediyse, yada regexe aykırı girdiyse
            eposta.setForeground(Color.red);
            eposta.setText("*E-Posta giriniz!");
            hata = true;
        } else {
            if (!dogrulama(eposta.getText())) { // kullanıcının girdiği e postanın regexe uygun olup olmadığını kontrol eder.
                eposta.setForeground(Color.red);
                hata = true;
            }
        }

        if (String.valueOf(parola.getPassword()).equals("") || String.valueOf(parola.getPassword()).equals("Parola")) { // kullanıcı parola girmediyse
            parola.setForeground(Color.red);
            parola.setEchoChar((char) 0);
            parola.setText("*Parola giriniz!");
            hata = true;
        } else {

        }

        if (String.valueOf(parolaOnay.getPassword()).equals("") || String.valueOf(parolaOnay.getPassword()).equals("Parola Onay")) { // kullanıcı parola onayını girmediyse
            parolaOnay.setForeground(Color.red);
            parolaOnay.setEchoChar((char) 0);
            parolaOnay.setText("*Parolanızı tekrar giriniz!");
            hata = true;
        } else {

        }
        if (!String.valueOf(parolaOnay.getPassword()).equals(String.valueOf(parola.getPassword()))) { // kullanıcının girdiği parolalar uyuşmuyorsa
            parola.setForeground(Color.red);
            parolaOnay.setForeground(Color.red);
            parola.setEchoChar((char) 0);
            parolaOnay.setEchoChar((char) 0);
            parola.setText("*Parolalar Uyuşmuyor!");
            parolaOnay.setText("*Parolalar Uyuşmuyor!");
            hata = true;
        }
        
        
        if (hata == false) {        // kullanıcının girdiği bilgilerde hata yoksa ve kullanıcı zaten kayıtlı değilse  kayıt işlemi gerçekleşir

            String mailSorgu = "select mail from kullanici where mail=\"" + eposta.getText() + "\"";
            try {   //kullanıcı veritabannda zaten mevcutsa
                ResultSet rs = Main.statement.executeQuery(mailSorgu);
                String mail = rs.getString("mail");
                System.out.println(mail);
                eposta.setForeground(Color.red);
                eposta.setText("*Zaten Aramızdasın");

            } catch (SQLException ex) { //kullanıcı veritabanında yoksa ekleme kısmı
                String kayit = "INSERT INTO kullanici(uid,uname, mail, pwd, bdate)"
                        + "VALUES('" + ++Main.kisiSayisi + "','" + isim.getText() + "', '" + eposta.getText() + "', '" + String.valueOf(parola.getPassword()) + "','" + bday.getText() + "')";

                System.out.println(kayit);

                try {
                    Main.statement.executeUpdate(kayit);
                    jPanel1.setVisible(false);
                    jPanel3.setVisible(false);
                    jPanel2.setVisible(true);

                } catch (SQLException ex1) {
                    Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }

        }

    }
    
    // kullanıcının tercih ettiği tür için 2şer tane en yüksek puanlı program atanır.
    public void tureGoreProgramAtama(String turIsmi,JLabel isimLabel1,JLabel bolumLabel1,JLabel puanLabel1,JLabel sureLabel1,JLabel isimLabel2,JLabel bolumLabel2,JLabel puanLabel2,JLabel sureLabel2){
            String sorgu = "select * from program where pid IN(select pid FROM progtur where tid=(SELECT tid from tur WHERE tname=\"" + turIsmi + "\")) ORDER BY puan DESC LIMIT 0,2";
            try {
                ResultSet rs = Main.statement.executeQuery(sorgu);
                rs.next();
                String isimLocal = rs.getString("pname");
                String sayi = rs.getString("bolumSayisi");
                String puan = rs.getString("puan");
                String sure = rs.getString("size") + "dk.";
                isimLabel1.setText(isimLocal);
                    isimLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24));
                bolumLabel1.setText("Bölüm sayısı: "+sayi);
                bolumLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24));
                puanLabel1.setText("Puan: "+puan);
                puanLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24));
                sureLabel1.setText("Süre: "+sure);
                sureLabel1.setFont(new java.awt.Font("Times New Roman", 1, 24));
                rs.next();
                isimLocal = rs.getString("pname");
                sayi = rs.getString("bolumSayisi");
                puan = rs.getString("puan");
                sure = rs.getString("size") + "dk.";
                isimLabel2.setText(isimLocal);
                isimLabel2.setFont(new java.awt.Font("Times New Roman", 1, 24));
                bolumLabel2.setText("Bölüm sayısı: "+sayi);
                bolumLabel2.setFont(new java.awt.Font("Times New Roman", 1, 24));
                puanLabel2.setText("Puan: "+puan);
                puanLabel2.setFont(new java.awt.Font("Times New Roman", 1, 24));
                sureLabel2.setText("Süre: "+sure);
                sureLabel2.setFont(new java.awt.Font("Times New Roman", 1, 24));
            } catch (SQLException ex) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    public void kullanıcıTureGoreProgramOneri(){ // kullanıcın seçtiği 3 türe göre 6 tane öneri program çıkaran fonksiyon
        if (secilenTurSayisi < 3) { // seçilen tür sayısı 3 olmadıkça işlem gerçekleşmez

        } else {                    // seçilen tür sayısı 3 olmadıkça işlem gerçekleşmez
            jPanel1.setVisible(false);
            jPanel2.setVisible(false);
            jPanel3.setVisible(true);
            tur1.setText(secilenTurler.get(0));
            tur2.setText(secilenTurler.get(1));
            tur3.setText(secilenTurler.get(2));
            // üç tür içinde 2şer tane en yüksek puanlı program atanır
            tureGoreProgramAtama(secilenTurler.get(0),filmIsmi1,bolumSayisi1,puan1,sure1,filmIsmi4,bolumSayisi4,puan4,sure4);
            tureGoreProgramAtama(secilenTurler.get(1),filmIsmi2,bolumSayisi2,puan2,sure2,filmIsmi5,bolumSayisi5,puan5,sure5);
            tureGoreProgramAtama(secilenTurler.get(2),filmIsmi3,bolumSayisi3,puan3,sure3,filmIsmi6,bolumSayisi6,puan6,sure6);
            

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

        jPanel3 = new javax.swing.JPanel();
        jSeparator2 = new javax.swing.JSeparator();
        jSeparator3 = new javax.swing.JSeparator();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator4 = new javax.swing.JSeparator();
        jSeparator5 = new javax.swing.JSeparator();
        girisYap = new javax.swing.JLabel();
        tur3 = new javax.swing.JLabel();
        tur1 = new javax.swing.JLabel();
        tur2 = new javax.swing.JLabel();
        filmIsmi1 = new javax.swing.JLabel();
        bolumSayisi1 = new javax.swing.JLabel();
        sure1 = new javax.swing.JLabel();
        puan1 = new javax.swing.JLabel();
        filmIsmi2 = new javax.swing.JLabel();
        bolumSayisi2 = new javax.swing.JLabel();
        puan2 = new javax.swing.JLabel();
        sure2 = new javax.swing.JLabel();
        filmIsmi3 = new javax.swing.JLabel();
        bolumSayisi3 = new javax.swing.JLabel();
        puan3 = new javax.swing.JLabel();
        sure3 = new javax.swing.JLabel();
        filmIsmi4 = new javax.swing.JLabel();
        bolumSayisi4 = new javax.swing.JLabel();
        puan4 = new javax.swing.JLabel();
        sure4 = new javax.swing.JLabel();
        filmIsmi5 = new javax.swing.JLabel();
        bolumSayisi5 = new javax.swing.JLabel();
        puan5 = new javax.swing.JLabel();
        sure5 = new javax.swing.JLabel();
        filmIsmi6 = new javax.swing.JLabel();
        bolumSayisi6 = new javax.swing.JLabel();
        puan6 = new javax.swing.JLabel();
        sure6 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        soz = new javax.swing.JLabel();
        soz1 = new javax.swing.JLabel();
        bilimVeDoga = new javax.swing.JCheckBox();
        aksiyonveMacera = new javax.swing.JCheckBox();
        romantik = new javax.swing.JCheckBox();
        cocukVeAile = new javax.swing.JCheckBox();
        realityProgram = new javax.swing.JCheckBox();
        belgesel = new javax.swing.JCheckBox();
        drama = new javax.swing.JCheckBox();
        gerilim = new javax.swing.JCheckBox();
        komedi = new javax.swing.JCheckBox();
        korku = new javax.swing.JCheckBox();
        bilimKurgu = new javax.swing.JCheckBox();
        anime = new javax.swing.JCheckBox();
        goster = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        eposta = new javax.swing.JTextField();
        mailIcon = new javax.swing.JLabel();
        pwdIcon = new javax.swing.JLabel();
        parolaOnay = new javax.swing.JPasswordField();
        okIcon = new javax.swing.JLabel();
        bday = new javax.swing.JTextField();
        bdayIcon = new javax.swing.JLabel();
        isim = new javax.swing.JTextField();
        kisilerIcon = new javax.swing.JLabel();
        parola = new javax.swing.JPasswordField();
        pwdIcon1 = new javax.swing.JLabel();
        bdaysecici = new com.toedter.calendar.JDateChooser();
        netflixLogosu = new javax.swing.JLabel();
        backIcon = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(800, 600));
        setLayout(null);

        jPanel3.setBackground(new java.awt.Color(0, 0, 0));
        jPanel3.setLayout(null);

        jSeparator2.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator2);
        jSeparator2.setBounds(267, 0, 3, 600);

        jSeparator3.setOrientation(javax.swing.SwingConstants.VERTICAL);
        jPanel3.add(jSeparator3);
        jSeparator3.setBounds(534, 0, 3, 600);
        jPanel3.add(jSeparator1);
        jSeparator1.setBounds(542, 300, 250, 10);
        jPanel3.add(jSeparator4);
        jSeparator4.setBounds(10, 300, 250, 10);
        jPanel3.add(jSeparator5);
        jSeparator5.setBounds(274, 300, 250, 10);

        girisYap.setBackground(new java.awt.Color(0, 0, 0));
        girisYap.setFont(new java.awt.Font("Ink Free", 1, 18)); // NOI18N
        girisYap.setForeground(new java.awt.Color(153, 153, 153));
        girisYap.setText("Giris Yap");
        girisYap.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                girisYapMouseClicked(evt);
            }
        });
        jPanel3.add(girisYap);
        girisYap.setBounds(690, 520, 100, 40);

        tur3.setBackground(new java.awt.Color(0, 0, 0));
        tur3.setFont(new java.awt.Font("Ink Free", 1, 16)); // NOI18N
        tur3.setForeground(new java.awt.Color(153, 153, 153));
        tur3.setText("TÜR3");
        tur3.setToolTipText("");
        tur3.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(tur3);
        tur3.setBounds(540, 10, 260, 50);

        tur1.setBackground(new java.awt.Color(0, 0, 0));
        tur1.setFont(new java.awt.Font("Ink Free", 1, 16)); // NOI18N
        tur1.setForeground(new java.awt.Color(153, 153, 153));
        tur1.setText("TÜR1");
        tur1.setToolTipText("");
        tur1.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(tur1);
        tur1.setBounds(0, 10, 270, 50);

        tur2.setBackground(new java.awt.Color(0, 0, 0));
        tur2.setFont(new java.awt.Font("Ink Free", 1, 16)); // NOI18N
        tur2.setForeground(new java.awt.Color(153, 153, 153));
        tur2.setText("TÜR2");
        tur2.setToolTipText("");
        tur2.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        jPanel3.add(tur2);
        tur2.setBounds(270, 10, 260, 50);

        filmIsmi1.setBackground(new java.awt.Color(0, 0, 0));
        filmIsmi1.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        filmIsmi1.setForeground(new java.awt.Color(255, 255, 255));
        filmIsmi1.setText("Kung Fu Panda Muhtesem Sırlar ");
        jPanel3.add(filmIsmi1);
        filmIsmi1.setBounds(10, 90, 250, 30);

        bolumSayisi1.setBackground(new java.awt.Color(0, 0, 0));
        bolumSayisi1.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        bolumSayisi1.setForeground(new java.awt.Color(255, 255, 255));
        bolumSayisi1.setText("Bölüm Sayisi:");
        jPanel3.add(bolumSayisi1);
        bolumSayisi1.setBounds(10, 140, 250, 40);

        sure1.setBackground(new java.awt.Color(0, 0, 0));
        sure1.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        sure1.setForeground(new java.awt.Color(255, 255, 255));
        sure1.setText("88 dk.");
        jPanel3.add(sure1);
        sure1.setBounds(10, 250, 250, 29);

        puan1.setBackground(new java.awt.Color(0, 0, 0));
        puan1.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        puan1.setForeground(new java.awt.Color(255, 255, 255));
        puan1.setText("Puan:");
        jPanel3.add(puan1);
        puan1.setBounds(10, 200, 100, 29);

        filmIsmi2.setBackground(new java.awt.Color(0, 0, 0));
        filmIsmi2.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        filmIsmi2.setForeground(new java.awt.Color(255, 255, 255));
        filmIsmi2.setText("Kung Fu Panda Muhtesem Sırlar ");
        jPanel3.add(filmIsmi2);
        filmIsmi2.setBounds(280, 90, 250, 30);

        bolumSayisi2.setBackground(new java.awt.Color(0, 0, 0));
        bolumSayisi2.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        bolumSayisi2.setForeground(new java.awt.Color(255, 255, 255));
        bolumSayisi2.setText("Bölüm Sayisi:");
        jPanel3.add(bolumSayisi2);
        bolumSayisi2.setBounds(280, 140, 250, 40);

        puan2.setBackground(new java.awt.Color(0, 0, 0));
        puan2.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        puan2.setForeground(new java.awt.Color(255, 255, 255));
        puan2.setText("Puan:");
        jPanel3.add(puan2);
        puan2.setBounds(280, 200, 100, 29);

        sure2.setBackground(new java.awt.Color(0, 0, 0));
        sure2.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        sure2.setForeground(new java.awt.Color(255, 255, 255));
        sure2.setText("88 dk.");
        jPanel3.add(sure2);
        sure2.setBounds(280, 250, 250, 29);

        filmIsmi3.setBackground(new java.awt.Color(0, 0, 0));
        filmIsmi3.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        filmIsmi3.setForeground(new java.awt.Color(255, 255, 255));
        filmIsmi3.setText("Kung Fu Panda Muhtesem Sırlar ");
        jPanel3.add(filmIsmi3);
        filmIsmi3.setBounds(540, 90, 250, 30);

        bolumSayisi3.setBackground(new java.awt.Color(0, 0, 0));
        bolumSayisi3.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        bolumSayisi3.setForeground(new java.awt.Color(255, 255, 255));
        bolumSayisi3.setText("Bölüm Sayisi:");
        jPanel3.add(bolumSayisi3);
        bolumSayisi3.setBounds(540, 140, 250, 40);

        puan3.setBackground(new java.awt.Color(0, 0, 0));
        puan3.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        puan3.setForeground(new java.awt.Color(255, 255, 255));
        puan3.setText("Puan:");
        jPanel3.add(puan3);
        puan3.setBounds(540, 200, 100, 29);

        sure3.setBackground(new java.awt.Color(0, 0, 0));
        sure3.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        sure3.setForeground(new java.awt.Color(255, 255, 255));
        sure3.setText("88 dk.");
        jPanel3.add(sure3);
        sure3.setBounds(540, 250, 250, 29);

        filmIsmi4.setBackground(new java.awt.Color(0, 0, 0));
        filmIsmi4.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        filmIsmi4.setForeground(new java.awt.Color(255, 255, 255));
        filmIsmi4.setText("Kung Fu Panda Muhtesem Sırlar ");
        jPanel3.add(filmIsmi4);
        filmIsmi4.setBounds(10, 350, 250, 30);

        bolumSayisi4.setBackground(new java.awt.Color(0, 0, 0));
        bolumSayisi4.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        bolumSayisi4.setForeground(new java.awt.Color(255, 255, 255));
        bolumSayisi4.setText("Bölüm Sayisi:");
        jPanel3.add(bolumSayisi4);
        bolumSayisi4.setBounds(10, 400, 250, 40);

        puan4.setBackground(new java.awt.Color(0, 0, 0));
        puan4.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        puan4.setForeground(new java.awt.Color(255, 255, 255));
        puan4.setText("Puan:");
        jPanel3.add(puan4);
        puan4.setBounds(10, 460, 100, 29);

        sure4.setBackground(new java.awt.Color(0, 0, 0));
        sure4.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        sure4.setForeground(new java.awt.Color(255, 255, 255));
        sure4.setText("88 dk.");
        jPanel3.add(sure4);
        sure4.setBounds(10, 510, 250, 29);

        filmIsmi5.setBackground(new java.awt.Color(0, 0, 0));
        filmIsmi5.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        filmIsmi5.setForeground(new java.awt.Color(255, 255, 255));
        filmIsmi5.setText("Kung Fu Panda Muhtesem Sırlar ");
        jPanel3.add(filmIsmi5);
        filmIsmi5.setBounds(280, 350, 250, 30);

        bolumSayisi5.setBackground(new java.awt.Color(0, 0, 0));
        bolumSayisi5.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        bolumSayisi5.setForeground(new java.awt.Color(255, 255, 255));
        bolumSayisi5.setText("Bölüm Sayisi:");
        jPanel3.add(bolumSayisi5);
        bolumSayisi5.setBounds(280, 400, 250, 40);

        puan5.setBackground(new java.awt.Color(0, 0, 0));
        puan5.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        puan5.setForeground(new java.awt.Color(255, 255, 255));
        puan5.setText("Puan:");
        jPanel3.add(puan5);
        puan5.setBounds(280, 460, 100, 29);

        sure5.setBackground(new java.awt.Color(0, 0, 0));
        sure5.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        sure5.setForeground(new java.awt.Color(255, 255, 255));
        sure5.setText("88 dk.");
        jPanel3.add(sure5);
        sure5.setBounds(280, 510, 250, 29);

        filmIsmi6.setBackground(new java.awt.Color(0, 0, 0));
        filmIsmi6.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        filmIsmi6.setForeground(new java.awt.Color(255, 255, 255));
        filmIsmi6.setText("Kung Fu Panda Muhtesem Sırlar ");
        jPanel3.add(filmIsmi6);
        filmIsmi6.setBounds(540, 350, 250, 30);

        bolumSayisi6.setBackground(new java.awt.Color(0, 0, 0));
        bolumSayisi6.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        bolumSayisi6.setForeground(new java.awt.Color(255, 255, 255));
        bolumSayisi6.setText("Bölüm Sayisi:");
        jPanel3.add(bolumSayisi6);
        bolumSayisi6.setBounds(540, 400, 250, 40);

        puan6.setBackground(new java.awt.Color(0, 0, 0));
        puan6.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        puan6.setForeground(new java.awt.Color(255, 255, 255));
        puan6.setText("Puan:");
        jPanel3.add(puan6);
        puan6.setBounds(540, 460, 100, 29);

        sure6.setBackground(new java.awt.Color(0, 0, 0));
        sure6.setFont(new java.awt.Font("Agency FB", 1, 24)); // NOI18N
        sure6.setForeground(new java.awt.Color(255, 255, 255));
        sure6.setText("88 dk.");
        jPanel3.add(sure6);
        sure6.setBounds(540, 510, 250, 29);

        add(jPanel3);
        jPanel3.setBounds(0, 0, 800, 600);

        jPanel2.setBackground(new java.awt.Color(0, 0, 0));
        jPanel2.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel2.setLayout(null);

        soz.setFont(new java.awt.Font("Ink Free", 1, 14)); // NOI18N
        soz.setForeground(new java.awt.Color(153, 153, 153));
        soz.setText("EN FAVORI DIZI VE FILMLER ICIN");
        jPanel2.add(soz);
        soz.setBounds(95, 0, 310, 60);

        soz1.setFont(new java.awt.Font("Ink Free", 1, 14)); // NOI18N
        soz1.setForeground(new java.awt.Color(153, 153, 153));
        soz1.setText("SEVDIGIN 3  TÜRÜ SEÇ");
        jPanel2.add(soz1);
        soz1.setBounds(130, 25, 220, 60);

        bilimVeDoga.setBackground(new java.awt.Color(0, 0, 0));
        bilimVeDoga.setForeground(new java.awt.Color(255, 255, 255));
        bilimVeDoga.setText("Bilim ve Doga");
        bilimVeDoga.setHideActionText(true);
        bilimVeDoga.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        bilimVeDoga.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bilimVeDogaActionPerformed(evt);
            }
        });
        jPanel2.add(bilimVeDoga);
        bilimVeDoga.setBounds(40, 170, 150, 25);

        aksiyonveMacera.setBackground(new java.awt.Color(0, 0, 0));
        aksiyonveMacera.setForeground(new java.awt.Color(255, 255, 255));
        aksiyonveMacera.setText("Aksiyon ve Macera");
        aksiyonveMacera.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                aksiyonveMaceraActionPerformed(evt);
            }
        });
        jPanel2.add(aksiyonveMacera);
        aksiyonveMacera.setBounds(40, 80, 150, 25);

        romantik.setBackground(new java.awt.Color(0, 0, 0));
        romantik.setForeground(new java.awt.Color(255, 255, 255));
        romantik.setText("Romantik");
        romantik.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                romantikActionPerformed(evt);
            }
        });
        jPanel2.add(romantik);
        romantik.setBounds(300, 80, 150, 25);

        cocukVeAile.setBackground(new java.awt.Color(0, 0, 0));
        cocukVeAile.setForeground(new java.awt.Color(255, 255, 255));
        cocukVeAile.setText("Cocuk ve Aile");
        cocukVeAile.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cocukVeAileActionPerformed(evt);
            }
        });
        jPanel2.add(cocukVeAile);
        cocukVeAile.setBounds(40, 140, 150, 25);

        realityProgram.setBackground(new java.awt.Color(0, 0, 0));
        realityProgram.setForeground(new java.awt.Color(255, 255, 255));
        realityProgram.setText("Reality Program");
        realityProgram.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                realityProgramActionPerformed(evt);
            }
        });
        jPanel2.add(realityProgram);
        realityProgram.setBounds(40, 110, 150, 25);

        belgesel.setBackground(new java.awt.Color(0, 0, 0));
        belgesel.setForeground(new java.awt.Color(255, 255, 255));
        belgesel.setText("Belgesel");
        belgesel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                belgeselActionPerformed(evt);
            }
        });
        jPanel2.add(belgesel);
        belgesel.setBounds(40, 200, 150, 25);

        drama.setBackground(new java.awt.Color(0, 0, 0));
        drama.setForeground(new java.awt.Color(255, 255, 255));
        drama.setText("Drama");
        drama.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                dramaActionPerformed(evt);
            }
        });
        jPanel2.add(drama);
        drama.setBounds(300, 140, 150, 25);

        gerilim.setBackground(new java.awt.Color(0, 0, 0));
        gerilim.setForeground(new java.awt.Color(255, 255, 255));
        gerilim.setText("Gerilim");
        gerilim.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                gerilimActionPerformed(evt);
            }
        });
        jPanel2.add(gerilim);
        gerilim.setBounds(300, 110, 150, 25);

        komedi.setBackground(new java.awt.Color(0, 0, 0));
        komedi.setForeground(new java.awt.Color(255, 255, 255));
        komedi.setText("Komedi");
        komedi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                komediActionPerformed(evt);
            }
        });
        jPanel2.add(komedi);
        komedi.setBounds(300, 170, 150, 25);

        korku.setBackground(new java.awt.Color(0, 0, 0));
        korku.setForeground(new java.awt.Color(255, 255, 255));
        korku.setText("Korku");
        korku.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                korkuActionPerformed(evt);
            }
        });
        jPanel2.add(korku);
        korku.setBounds(300, 200, 150, 25);

        bilimKurgu.setBackground(new java.awt.Color(0, 0, 0));
        bilimKurgu.setForeground(new java.awt.Color(255, 255, 255));
        bilimKurgu.setText("Bilim Kurgu ve Fantastik Yapimlar");
        bilimKurgu.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        bilimKurgu.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        bilimKurgu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                bilimKurguActionPerformed(evt);
            }
        });
        jPanel2.add(bilimKurgu);
        bilimKurgu.setBounds(40, 230, 230, 25);

        anime.setBackground(new java.awt.Color(0, 0, 0));
        anime.setForeground(new java.awt.Color(255, 255, 255));
        anime.setText("Anime");
        anime.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animeActionPerformed(evt);
            }
        });
        jPanel2.add(anime);
        anime.setBounds(300, 230, 150, 25);

        goster.setBackground(new java.awt.Color(0, 0, 0));
        goster.setFont(new java.awt.Font("Ink Free", 1, 15)); // NOI18N
        goster.setForeground(new java.awt.Color(153, 153, 153));
        goster.setText("GÖSTER");
        goster.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                gosterMouseClicked(evt);
            }
        });
        jPanel2.add(goster);
        goster.setBounds(180, 270, 70, 20);

        add(jPanel2);
        jPanel2.setBounds(170, 250, 440, 310);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));
        jPanel1.setLayout(null);

        eposta.setBackground(new java.awt.Color(0, 0, 0));
        eposta.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N
        eposta.setForeground(new java.awt.Color(255, 255, 255));
        eposta.setText("E-posta");
        eposta.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                epostaFocusGained(evt);
            }
        });
        jPanel1.add(eposta);
        eposta.setBounds(50, 130, 170, 30);

        mailIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/mail.jpg"))); // NOI18N
        jPanel1.add(mailIcon);
        mailIcon.setBounds(230, 130, 30, 30);

        pwdIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/parola.jpg"))); // NOI18N
        jPanel1.add(pwdIcon);
        pwdIcon.setBounds(230, 230, 30, 30);

        parolaOnay.setBackground(new java.awt.Color(0, 0, 0));
        parolaOnay.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N
        parolaOnay.setForeground(new java.awt.Color(255, 255, 255));
        parolaOnay.setText("Parola Onay");
        parolaOnay.setPreferredSize(new java.awt.Dimension(79, 23));
        parolaOnay.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                parolaOnayFocusGained(evt);
            }
        });
        parolaOnay.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                parolaOnayKeyPressed(evt);
            }
        });
        jPanel1.add(parolaOnay);
        parolaOnay.setBounds(50, 230, 170, 30);

        okIcon.setBackground(new java.awt.Color(0, 0, 0));
        okIcon.setForeground(new java.awt.Color(255, 255, 255));
        okIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/ok.jpg"))); // NOI18N
        okIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                okIconMouseClicked(evt);
            }
        });
        jPanel1.add(okIcon);
        okIcon.setBounds(230, 230, 20, 30);

        bday.setBackground(new java.awt.Color(0, 0, 0));
        bday.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N
        bday.setForeground(new java.awt.Color(255, 255, 255));
        bday.setText("Doğum Tarihi");
        bday.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                bdayFocusGained(evt);
            }
        });
        jPanel1.add(bday);
        bday.setBounds(50, 80, 150, 30);

        bdayIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/bday.jpg"))); // NOI18N
        jPanel1.add(bdayIcon);
        bdayIcon.setBounds(230, 80, 30, 30);

        isim.setBackground(new java.awt.Color(0, 0, 0));
        isim.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N
        isim.setForeground(new java.awt.Color(255, 255, 255));
        isim.setText("Ad-Soyad");
        isim.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                isimFocusGained(evt);
            }
        });
        jPanel1.add(isim);
        isim.setBounds(50, 30, 170, 30);

        kisilerIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/kisiler.jpg"))); // NOI18N
        jPanel1.add(kisilerIcon);
        kisilerIcon.setBounds(230, 30, 30, 30);

        parola.setBackground(new java.awt.Color(0, 0, 0));
        parola.setFont(new java.awt.Font("Times New Roman", 2, 14)); // NOI18N
        parola.setForeground(new java.awt.Color(255, 255, 255));
        parola.setText("Parola");
        parola.setPreferredSize(new java.awt.Dimension(79, 23));
        parola.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                parolaFocusGained(evt);
            }
        });
        jPanel1.add(parola);
        parola.setBounds(50, 180, 170, 30);

        pwdIcon1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/parola.jpg"))); // NOI18N
        jPanel1.add(pwdIcon1);
        pwdIcon1.setBounds(230, 180, 30, 30);

        bdaysecici.setBackground(new java.awt.Color(153, 255, 153));
        bdaysecici.setForeground(new java.awt.Color(153, 255, 102));
        bdaysecici.setToolTipText("");
        bdaysecici.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                bdayseciciPropertyChange(evt);
            }
        });
        jPanel1.add(bdaysecici);
        bdaysecici.setBounds(200, 80, 20, 30);

        add(jPanel1);
        jPanel1.setBounds(240, 250, 300, 290);

        netflixLogosu.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/logo.png"))); // NOI18N
        add(netflixLogosu);
        netflixLogosu.setBounds(270, 40, 220, 220);

        backIcon.setIcon(new javax.swing.ImageIcon(getClass().getResource("/images/back.jpg"))); // NOI18N
        backIcon.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backIconMouseClicked(evt);
            }
        });
        add(backIcon);
        backIcon.setBounds(0, 0, 50, 50);
    }// </editor-fold>//GEN-END:initComponents

    private void okIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_okIconMouseClicked
        kayitYap();
    }//GEN-LAST:event_okIconMouseClicked

    private void bdayseciciPropertyChange(java.beans.PropertyChangeEvent evt) {//GEN-FIRST:event_bdayseciciPropertyChange
        yapildimi++;    // tarih seçme bileşeninin bugundan dolayı ilk girişte  bdayseciciPropertyChange direk çalışıyor bunun olumsuz sonuçlarını engellemek için
        if (yapildimi > 1) {    // tarih seçme bileşeninin bugundan dolayı ilk girişte  bdayseciciPropertyChange direk çalışıyor bunun olumsuz sonuçlarını engellemek için
            String date = String.valueOf(bdaysecici.getDate());
            try {
                String splitt[] = date.split(" ");
                String lastdate = splitt[2] + "." + splitt[1] + "." + splitt[5];
                bday.setText(lastdate);
            } catch (Exception e) {

            }
        }
    }//GEN-LAST:event_bdayseciciPropertyChange

    private void backIconMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backIconMouseClicked
        this.setVisible(false);
        Main.ekran.login.setVisible(true);
        Main.ekran.login.setSize(800, 600);
    }//GEN-LAST:event_backIconMouseClicked

    private void aksiyonveMaceraActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_aksiyonveMaceraActionPerformed
        turKontrol(aksiyonveMacera);
    }//GEN-LAST:event_aksiyonveMaceraActionPerformed

    private void realityProgramActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_realityProgramActionPerformed
        turKontrol(realityProgram);
    }//GEN-LAST:event_realityProgramActionPerformed

    private void cocukVeAileActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cocukVeAileActionPerformed
        turKontrol(cocukVeAile);
    }//GEN-LAST:event_cocukVeAileActionPerformed

    private void bilimVeDogaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bilimVeDogaActionPerformed
        turKontrol(bilimVeDoga);
    }//GEN-LAST:event_bilimVeDogaActionPerformed

    private void belgeselActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_belgeselActionPerformed
        turKontrol(belgesel);
    }//GEN-LAST:event_belgeselActionPerformed

    private void bilimKurguActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_bilimKurguActionPerformed
        turKontrol(bilimKurgu);
    }//GEN-LAST:event_bilimKurguActionPerformed

    private void romantikActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_romantikActionPerformed
        turKontrol(romantik);
    }//GEN-LAST:event_romantikActionPerformed

    private void gerilimActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_gerilimActionPerformed
        turKontrol(gerilim);
    }//GEN-LAST:event_gerilimActionPerformed

    private void dramaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_dramaActionPerformed
        turKontrol(drama);
    }//GEN-LAST:event_dramaActionPerformed

    private void komediActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_komediActionPerformed
        turKontrol(komedi);
    }//GEN-LAST:event_komediActionPerformed

    private void korkuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_korkuActionPerformed
        turKontrol(korku);
    }//GEN-LAST:event_korkuActionPerformed

    private void animeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animeActionPerformed
        turKontrol(anime);
    }//GEN-LAST:event_animeActionPerformed

    private void girisYapMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_girisYapMouseClicked
        this.setVisible(false);
        Main.ekran.login.setVisible(true);
        Main.ekran.login.setSize(800, 600);
        Main.ekran.remove(this);
    }//GEN-LAST:event_girisYapMouseClicked

    private void gosterMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_gosterMouseClicked
        kullanıcıTureGoreProgramOneri();
    }//GEN-LAST:event_gosterMouseClicked

    private void epostaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_epostaFocusGained
        eposta.setForeground(Color.white);
        eposta.selectAll();
    }//GEN-LAST:event_epostaFocusGained

    private void parolaFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_parolaFocusGained
        parola.setForeground(Color.white);
        parola.selectAll();
        parola.setEchoChar('*'); //password = JPasswordField
    }//GEN-LAST:event_parolaFocusGained

    private void parolaOnayFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_parolaOnayFocusGained
        parolaOnay.setForeground(Color.white);
        parolaOnay.selectAll();
        pwdIcon.setVisible(false);
        parolaOnay.setEchoChar('*');
    }//GEN-LAST:event_parolaOnayFocusGained

    private void bdayFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_bdayFocusGained
        bday.setForeground(Color.white);
        bday.selectAll();
    }//GEN-LAST:event_bdayFocusGained

    private void isimFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_isimFocusGained
        isim.setForeground(Color.white);
        isim.selectAll();
    }//GEN-LAST:event_isimFocusGained

    private void parolaOnayKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_parolaOnayKeyPressed
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            kayitYap();
        }
    }//GEN-LAST:event_parolaOnayKeyPressed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox aksiyonveMacera;
    private javax.swing.JCheckBox anime;
    private javax.swing.JLabel backIcon;
    private javax.swing.JTextField bday;
    private javax.swing.JLabel bdayIcon;
    private com.toedter.calendar.JDateChooser bdaysecici;
    private javax.swing.JCheckBox belgesel;
    private javax.swing.JCheckBox bilimKurgu;
    private javax.swing.JCheckBox bilimVeDoga;
    private javax.swing.JLabel bolumSayisi1;
    private javax.swing.JLabel bolumSayisi2;
    private javax.swing.JLabel bolumSayisi3;
    private javax.swing.JLabel bolumSayisi4;
    private javax.swing.JLabel bolumSayisi5;
    private javax.swing.JLabel bolumSayisi6;
    private javax.swing.JCheckBox cocukVeAile;
    private javax.swing.JCheckBox drama;
    private javax.swing.JTextField eposta;
    private javax.swing.JLabel filmIsmi1;
    private javax.swing.JLabel filmIsmi2;
    private javax.swing.JLabel filmIsmi3;
    private javax.swing.JLabel filmIsmi4;
    private javax.swing.JLabel filmIsmi5;
    private javax.swing.JLabel filmIsmi6;
    private javax.swing.JCheckBox gerilim;
    private javax.swing.JLabel girisYap;
    private javax.swing.JLabel goster;
    private javax.swing.JTextField isim;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JSeparator jSeparator3;
    private javax.swing.JSeparator jSeparator4;
    private javax.swing.JSeparator jSeparator5;
    private javax.swing.JLabel kisilerIcon;
    private javax.swing.JCheckBox komedi;
    private javax.swing.JCheckBox korku;
    private javax.swing.JLabel mailIcon;
    private javax.swing.JLabel netflixLogosu;
    private javax.swing.JLabel okIcon;
    private javax.swing.JPasswordField parola;
    private javax.swing.JPasswordField parolaOnay;
    private javax.swing.JLabel puan1;
    private javax.swing.JLabel puan2;
    private javax.swing.JLabel puan3;
    private javax.swing.JLabel puan4;
    private javax.swing.JLabel puan5;
    private javax.swing.JLabel puan6;
    private javax.swing.JLabel pwdIcon;
    private javax.swing.JLabel pwdIcon1;
    private javax.swing.JCheckBox realityProgram;
    private javax.swing.JCheckBox romantik;
    private javax.swing.JLabel soz;
    private javax.swing.JLabel soz1;
    private javax.swing.JLabel sure1;
    private javax.swing.JLabel sure2;
    private javax.swing.JLabel sure3;
    private javax.swing.JLabel sure4;
    private javax.swing.JLabel sure5;
    private javax.swing.JLabel sure6;
    private javax.swing.JLabel tur1;
    private javax.swing.JLabel tur2;
    private javax.swing.JLabel tur3;
    // End of variables declaration//GEN-END:variables
}
