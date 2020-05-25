
import java.awt.Toolkit;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {

    
    public static Toolkit kit = Toolkit.getDefaultToolkit();
    static AnaEkran ekran = new AnaEkran();
    public static int ekranX;
    public static int ekranY;
    public static String path = "jdbc:sqlite:database/netflix.db";
    public static Connection baglanti=null;
    public static Statement statement=null;
                

    public static void main(String[] args) {        
        try {
            baglanti=DriverManager.getConnection(path);
            System.out.println("Database'a baglanildi");
            statement = baglanti.createStatement();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        
        while (ekran.isActive()) {

        }
        
    }

}
