package connection;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final Logger logger = Logger.getLogger(DatabaseConnection.class.getName());
    
    private static final String URL = System.getProperty("db.url", "jdbc:mysql://localhost:3306/projetoitamar?allowPublicKeyRetrieval=true&useSSL=false");
    private static final String USER = System.getProperty("db.user", "admin");
    private static final String PASSWORD = System.getProperty("db.password", "123");

    public static Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
            logger.info("Conexão com o banco de dados estabelecida com sucesso!");
            return conn;
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Driver MySQL não encontrado", e);
            throw new SQLException("Driver MySQL não encontrado", e);
        } catch (SQLException e) {
            logger.log(Level.SEVERE, "Falha ao conectar ao banco de dados", e);
            throw e;
        }
    }
}