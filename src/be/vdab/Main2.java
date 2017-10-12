package be.vdab;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main2 {
    private static final String URL = "jdbc:mysql://localhost/tuincentrum?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String SELECT_PRIJS =
        "select verkoopprijs from planten where id = ?";
    private static final String UPDATE_PRIJS = 
        "update planten set verkoopprijs = ? where id = ?";
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            System.out.println("Id: ");
            int id = scanner.nextInt();
            System.out.println("Verkoopprijs: ");
            BigDecimal nieuwePrijs = scanner.nextBigDecimal();
            try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement statementSelect = 
                    connection.prepareStatement(SELECT_PRIJS);
                PreparedStatement statementUpdate =
                    connection.prepareStatement(UPDATE_PRIJS)){
                statementSelect.setInt(1, id);
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                try(ResultSet resultSet = statementSelect.executeQuery()){
                    if (resultSet.next()){
                        BigDecimal oudePrijs = resultSet.getBigDecimal("verkoopprijs");
                        if (nieuwePrijs.compareTo(
                            oudePrijs.multiply(BigDecimal.valueOf(1.1))) <= 0){
                            statementUpdate.setBigDecimal(1, nieuwePrijs);
                            statementUpdate.setInt(2, id);
                            statementUpdate.executeUpdate();
                            connection.commit();
                        }
                        else {
                            System.out.println("Nieuwe verkoopprijs te hoog");
                        }
                    }
                    else {
                        System.out.println("Plant niet gevonden");
                    }
                }
            }
            catch(SQLException ex){
                ex.printStackTrace();
            }
        }
        catch (InputMismatchException ex){
            System.out.println("verkeerde invoer");
        }
    }    
}
