package be.vdab;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.InputMismatchException;
import java.util.Scanner;

public class Main3 {
private static final String URL = "jdbc:mysql://localhost/tuincentrum?useSSL=false";
    private static final String USER = "cursist";
    private static final String PASSWORD = "cursist";
    private static final String UPDATE_PRIJS = 
        "update planten set verkoopprijs = ? where id = ? and ? <= verkoopprijs*1.1";
    private static final String SELECT_ID =
        "select id from planten where id = ? ";
    public static void main(String[] args) {
        try(Scanner scanner = new Scanner(System.in)){
            System.out.println("Id: ");
            int id = scanner.nextInt();
            System.out.println("Verkoopprijs: ");
            BigDecimal nieuwePrijs = scanner.nextBigDecimal();
            try(Connection connection = DriverManager.getConnection(URL, USER, PASSWORD);
                PreparedStatement statementUpdate =
                    connection.prepareStatement(UPDATE_PRIJS)){
                statementUpdate.setBigDecimal(1, nieuwePrijs);
                statementUpdate.setInt(2, id);
                statementUpdate.setBigDecimal(3, nieuwePrijs);                
                connection.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
                connection.setAutoCommit(false);
                if(statementUpdate.executeUpdate() == 0){
                    try (PreparedStatement statementSelect =
                        connection.prepareStatement(SELECT_ID)){
                        statementSelect.setInt(1, id);
                        try(ResultSet resultSet = statementSelect.executeQuery()){
                            System.out.println(resultSet.next()?
                                "Nieuwe verkoopprijs te hoog" : "Plant niet gevonden");
                        }
                    }
                }
                connection.commit();
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
