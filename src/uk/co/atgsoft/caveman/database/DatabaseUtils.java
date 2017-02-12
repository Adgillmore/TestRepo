/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.atgsoft.caveman.database;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import uk.co.atgsoft.caveman.wine.BottleSize;
import uk.co.atgsoft.caveman.wine.Wine;
import uk.co.atgsoft.caveman.wine.WineColour;
import uk.co.atgsoft.caveman.wine.WineImpl;
import uk.co.atgsoft.caveman.wine.WineOriginImpl;
import uk.co.atgsoft.caveman.wine.WineStyle;
import uk.co.atgsoft.caveman.wine.record.WineCompositionImpl;
import uk.co.atgsoft.caveman.wine.record.depletion.DepletionRecord;
import uk.co.atgsoft.caveman.wine.record.depletion.DepletionRecordImpl;
import uk.co.atgsoft.caveman.wine.record.purchase.PurchaseRecord;
import uk.co.atgsoft.caveman.wine.record.purchase.PurchaseRecordImpl;

/**
 *
 * @author adam
 */
public final class DatabaseUtils {
    
    public static void createTable(final String databaseName, final String statement) {
        Connection c = null;
        Statement stmt = null;
        try {
            c = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
            System.out.println("Opened database successfully");
            stmt = c.createStatement();
            stmt.executeUpdate(statement);
            stmt.close();
            c.close();
        }   catch ( Exception e ) {
            System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Table created successfully");
    }
    
    public static void executeStatement(final String databaseName, final String statement) {
        Connection c = null;
        Statement stmt = null;
        try {
          c = DriverManager.getConnection("jdbc:sqlite:" + databaseName + ".db");
          stmt = c.createStatement();
          stmt.executeUpdate(statement);
          stmt.close();
          c.commit();
          c.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
    }
    
    public static Wine createWine(final ResultSet rs) throws SQLException {
        final Wine wine = new WineImpl(
                rs.getString("wine_id"), 
                rs.getString("name"), 
                new WineOriginImpl(rs.getString("producer"), rs.getString("region"), rs.getString("country")),
                new WineCompositionImpl(WineColour.valueOf(rs.getString("colour").toUpperCase()), 
                        WineStyle.valueOf(rs.getString("style").toUpperCase()), rs.getString("grape")),
                rs.getInt("vintage"),
                rs.getFloat("alcohol"),
                rs.getBigDecimal("price")
             );
        return wine;
    }
    
    public static PurchaseRecord createPurchaseRecord(final Wine wine, final ResultSet rs) throws SQLException {
        return new PurchaseRecordImpl(rs.getString("id"),
                wine, 
                new BigDecimal(rs.getFloat("price")), 
                rs.getInt("quantity"), 
                BottleSize.valueOf(rs.getString("size")), 
                rs.getString("vendor"), 
                LocalDate.parse(rs.getString("date"))
                );
    }
    
    public static DepletionRecord createDepletionRecord(final Wine wine, final ResultSet rs) throws SQLException {
        return new DepletionRecordImpl(rs.getString("id"), 
                wine, 
                rs.getInt("quantity"), 
                BottleSize.valueOf(rs.getString("size")), 
                LocalDate.parse(rs.getString("date")), 
                rs.getFloat("rating"), 
                rs.getString("review"));
    }
        
}
