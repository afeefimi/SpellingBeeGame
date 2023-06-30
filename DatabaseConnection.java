import java.sql.*;
import java.io.*;

public class DatabaseConnection{ 
    
   public static void main(String args []){
      
    Statement stmt = null;
    ResultSet rs = null;
    BufferedReader reader;
    
    System.out.println("MySQL Connect.");
    Connection conn = null;
    String url = "jdbc:mysql://localhost:3306/";
    String dbName = "vocabulary";
    String driver = "com.mysql.cj.jdbc.Driver";
    String userName = "root"; 
    String password = "Barcelonalm10!";
    int i = 0;
    String sql;
    
    try {
      Class.forName(driver).newInstance();
      conn = DriverManager.getConnection(url + dbName , userName , password);
      System.out.println("Connected to the database");
      stmt = conn.createStatement();
      //rs = stmt.executeQuery("USE vocabulary");
      reader = new BufferedReader(new FileReader("english-words.txt"));
            String line = reader.readLine();
            while (line != null) {
                System.out.println(line);
                // read next line
                sql = "INSERT INTO word_list(word) VALUES ('" + line + "');";
                stmt.execute(sql);
                line = reader.readLine();}
      conn.close();
      System.out.println("Disconnected from database");
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    
  }
  
}