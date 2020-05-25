
import java.awt.Toolkit;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

    public static void main(String[] args) throws FileNotFoundException, IOException {
        try {
            baglanti = DriverManager.getConnection(db);
            System.out.println("Database'a baglanildi");
            statement = baglanti.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        File dosya = new File("database/veri.xlsx");
        FileInputStream f = new FileInputStream(dosya);
        XSSFWorkbook workbook = new XSSFWorkbook(f);
        XSSFSheet sheet = workbook.getSheetAt(0);
        Iterator<Row> row = sheet.iterator();

        while (row.hasNext()) {
            Row row1 = row.next();
            Iterator<Cell> satir = row1.cellIterator();
            while (satir.hasNext()) {
                Cell sutun = satir.next();
                System.out.print(String.valueOf(sutun)+"-");
            }
            System.out.println("");
        }
        
        workbook.close();
        f.close();

    }

}
