import java.sql.Connection;
import connection.DatabaseConnection;
import java.sql.SQLException;

public class TesteConexao {
    public static void main(String[] args) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            System.out.println("✅ Conexão com banco estabelecida com sucesso!");
            System.out.println("Database: " + conn.getCatalog());
        } catch (SQLException e) {
            System.err.println("❌ Erro de conexão: " + e.getMessage());
            e.printStackTrace();
        }
    }
}