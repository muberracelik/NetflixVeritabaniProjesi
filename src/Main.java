
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

    public static Toolkit kit = Toolkit.getDefaultToolkit();
    static AnaEkran ekran = new AnaEkran();
    public static int ekranX;
    public static int ekranY;
    public static String db = "jdbc:sqlite:database/netflix.db";
    public static Connection baglanti = null;
    public static Statement statement = null;
    static int kisiSayisi=0;
    public static void programTablosuOlustur() throws FileNotFoundException, IOException {
        File dosya = new File("database/veri.xlsx");
        FileInputStream f = new FileInputStream(dosya);
        XSSFWorkbook workbook = new XSSFWorkbook(f);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> row = sheet.iterator();
        int i = 1;
        while (row.hasNext()) {
            Row row1 = row.next();
            Iterator<Cell> satir = row1.cellIterator();
            Cell sutun = satir.next();
            String isim = String.valueOf(sutun);
            sutun = satir.next();
            String tur = String.valueOf(sutun);
            sutun = satir.next();
            String tip = String.valueOf(sutun);
            sutun = satir.next();
            String bolumSayisi = String.valueOf((int) Math.round(Double.valueOf(String.valueOf(sutun))));
            sutun = satir.next();
            String puan = String.valueOf((int) Math.round(Double.valueOf(String.valueOf(sutun))));
            sutun = satir.next();
            String sure = String.valueOf(sutun);

            String dbkayit = "INSERT INTO program(pid,pname, tip, tur, bolumSayisi,size,puan)"
                    + "VALUES('" + i + "','" + isim + "', '" + tip + "', '" + tur + "','" + bolumSayisi + "','" + sure + "','" + puan + "')";

            try {
                statement.executeUpdate(dbkayit);
                i++;

            } catch (SQLException ex1) {
                Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
            }

        }

        workbook.close();
        f.close();

    }

    public static void tabloSifirlama(String tabloIsmi) {
        String tabloSil = "DELETE FROM " + tabloIsmi;
        try {
            statement.executeUpdate(tabloSil);

        } catch (SQLException ex1) {
            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
        }
    }

    public static void veritabaniBaglama() {

        try {
            baglanti = DriverManager.getConnection(db);
            System.out.println("Database'a baglanildi");
            statement = baglanti.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
         String kisiSorgu = "select COUNT(uid) FROM kullanici";//kisi sayisini alma kısmı
        try {
             ResultSet rs = Main.statement.executeQuery(kisiSorgu);
             rs.next();
             kisiSayisi = rs.getInt(1);;
            
        } catch (SQLException ex1) {
            kisiSayisi=0;
        }

    }

    public static void programTurTablosuOlustur() {
        String turSorgu = "SELECT tur,pid FROM program";
        try {
            
            ResultSet rs = statement.executeQuery(turSorgu);
            while (rs.next()) {
                Statement statement1 = null;
                statement1 = baglanti.createStatement();
                String tur = rs.getString("tur");
                String pid = rs.getString("pid");
                //System.out.println(tur);
                String splitt[] = tur.split(",");
                String tidSorgu;
                for (int i = 0; i < splitt.length; i++) {
                    tidSorgu = "SELECT tid FROM tur where tname=" + "\"" + splitt[i] + "\"";                    
                        ResultSet rs2 = statement1.executeQuery(tidSorgu);
                        rs2.next();
                        String tid = rs2.getString("tid");
                        String dbkayit = "INSERT INTO progtur(pid,tid)"
                                + "VALUES('" + pid + "','" + tid + "')";                       

                        try {
                            Statement statement2 = null;
                            statement2 = baglanti.createStatement();
                            statement2.executeUpdate(dbkayit);
                            statement2.close();

                        } catch (SQLException ex1) {
                            Logger.getLogger(Register.class.getName()).log(Level.SEVERE, null, ex1);
                        }                 
                }
                statement1.close();
            }

        } catch (SQLException ex) {//kullanıcı yoksa ekleme kısmı}

        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        veritabaniBaglama();
        tabloSifirlama("program");
        programTablosuOlustur();
        tabloSifirlama("progtur");
        programTurTablosuOlustur();
    }

}
