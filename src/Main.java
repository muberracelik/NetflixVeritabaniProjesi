
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class Main {
    public static Toolkit kit = Toolkit.getDefaultToolkit();    // Kullanıcının kullandığı bilgisayarın ekran boyutlarını alıp responsive tasarım yapmak için;
    static AnaEkran ekran = new AnaEkran();     // Tüm işlemleri tek bir jframe'de gerçekleştiriyoruz.(o yüzden static belirledik)
    public static int ekranX;   // Kullanıcının kullandığı bilgisayarın ekran genişliği
    public static int ekranY;   // Kullanıcının kullandığı bilgisayarın ekran uzunluğu
    public  static String db = "jdbc:sqlite:database/netflix.db";    // sqlite VTYS dosyasının bulunduğu relative path;
    public static Connection baglanti = null;   // database'i jdbc ye bağlamak için kullanılan connection sınıfı objesi;
    public static Statement statement = null;   // sorguları databasede gerçekleştirmek için kullanılan statement sınıfı objesi;
    static int kisiSayisi = 0;                    // database'de kullanıcı uid sini güncellemek için tuttuğumuz değişken;
    public static String dbVeri=String.valueOf(Main.class.getResource("/database/netflix.db"));
    
    public static void main(String[] args) throws IOException, InterruptedException {
        veritabaniBaglama();     // Veri tabanına bağlanır ve kişilerin uidsi güncellenir.

        tabloSifirlama("program");  // Netflix DB.xlsx dosyasındaan veri okunurken çakışma olmaması için program tablosunun verilerini siliyoruz.
        programTablosuOlustur();    // program tablosu oluşturmak için Netflix DB.xlsx'dosyasını her açılışta okuyup yeni verileri güncelliyoruz.

        tabloSifirlama("progtur");  // Netflix DB.xlsx dosyasındaan veri okunurken çakışma olmaması için progtur tablosunun verilerini siliyoruz.
        programTurTablosuOlustur(); // progtur tablosu oluşturmak için güncel program tablosundaki veriler kullanılır.
    }

    public static void programTurTablosuOlustur() { //programlar ile turler arasındaki ilişkiyi oluşturur ve progtur tablosuna kaydeder.
        String turSorgu = "SELECT tur,pid FROM program";
        try {
            ResultSet rs = statement.executeQuery(turSorgu);
            while (rs.next()) {     //sırasıyla program tablosundaki tüm programların, tür ve pid leri alınır.
                Statement statement1;
                statement1 = baglanti.createStatement();
                String tur = rs.getString("tur");
                String pid = rs.getString("pid");

                String splitt[] = tur.split(",");   //Bir program birden fazla türe sahip olabileceği için virgül ile split ediyoruz.
                String tidSorgu;
                for (String splitt1 : splitt) {     //tür isimine göre türid bulunur ve bunlar progtür tablosuna pid ile birlikte eklenir.
                    tidSorgu = "SELECT tid FROM tur where tname=" + "\"" + splitt1 + "\"";      //tür isimine göre tid bulunur.
                    ResultSet rs2 = statement1.executeQuery(tidSorgu);
                    rs2.next();
                    String tid = rs2.getString("tid");
                    String dbkayit = "INSERT INTO progtur(pid,tid)"
                            + "VALUES('" + pid + "','" + tid + "')";
                    try {
                        Statement statement2;
                        statement2 = baglanti.createStatement();
                        statement2.executeUpdate(dbkayit);
                        statement2.close();

                    } catch (SQLException ex1) {
                        Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
                    }
                }
                statement1.close();
            }

        } catch (SQLException ex) {
            System.out.println("Program bulunamadı" + ex.getMessage());//konsol çıktısı
        }
    }

    public static void programTablosuOlustur() throws FileNotFoundException, IOException {
         
        File dosya = new File("database/Netflix DB.xlsx");  // Program verilerinin tutulduğu excel dosyası;
        try (FileInputStream f = new FileInputStream(dosya); XSSFWorkbook workbook = new XSSFWorkbook(f)) {
            XSSFSheet sheet = workbook.getSheetAt(0);   // ilk sayfadaki veriler kullanılır.
            Iterator<Row> row = sheet.iterator();       // dosyadaki satırları alan yineleyici;
            int programIndex = 1;  // satır sayısına göre program pid'sini vermek için kullandığımız değişken;
            while (row.hasNext()) { // dosyada dizi veya film buldukça bu işlem tekrarlanır
                Row row1 = row.next();

                Iterator<Cell> satir = row1.cellIterator(); // dosyadaki sütünları alan yineleyici

                Cell sutun = satir.next();
                String isim = String.valueOf(sutun);    // isim sütunu
                sutun = satir.next();
                String tur = String.valueOf(sutun);     // tur sütunu
                sutun = satir.next();
                String tip = String.valueOf(sutun);     // tip sütunu
                sutun = satir.next();
                String bolumSayisi = String.valueOf((int) Math.round(Double.valueOf(String.valueOf(sutun))));   // bölum sayısı sütunu
                sutun = satir.next();
                String puan = String.valueOf((int) Math.round(Double.valueOf(String.valueOf(sutun))));          // puan sütunu
                sutun = satir.next();
                String sure = String.valueOf(sutun);    // süre sütunu

                String dbkayit = "INSERT INTO program(pid,pname, tip, tur, bolumSayisi,size,puan)"
                        + "VALUES('" + programIndex + "','" + isim + "', '" + tip + "', '" + tur + "','" + bolumSayisi + "','" + sure + "','" + puan + "')";

                try {
                    statement.executeUpdate(dbkayit);   // VTYS'ye sorguyu gönderir.
                    programIndex++;    // eğer program verisi eklenirse program indexi artar;

                } catch (SQLException ex1) {
                    Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
                }

            }
           f.close();
           workbook.close();           
        }
        

    }

    public static void tabloSifirlama(String tabloIsmi) {   //aldığı değeri içeren tabloyu sıfırlar
        String tabloSil = "DELETE FROM " + tabloIsmi;
        try {
            statement.executeUpdate(tabloSil);

        } catch (SQLException ex1) {
            System.out.println("tablo sıfırlanamadı" + ex1.getMessage());//konsol çıktısı
        }
    }

    public static void veritabaniBaglama() {// Veri tabanına bağlanır ve kişilerin uidsi güncellenir.

        try {
            baglanti = DriverManager.getConnection(db);
            System.out.println("Database'a baglanildi");//konsol çıktısı
            statement = baglanti.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        String kisiSorgu = "select COUNT(uid) FROM kullanici"; //toplam kisi sayisini alma kısmı
        try {
            ResultSet rs = Main.statement.executeQuery(kisiSorgu);
            rs.next();
            kisiSayisi = rs.getInt(1);

        } catch (SQLException ex1) {//eğer veritabanında kullanıcı bulunamazsa;
            kisiSayisi = 0;
        }

    }

}
