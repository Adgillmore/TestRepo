/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.atgsoft.caveman.database.dao;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import uk.co.atgsoft.caveman.database.DatabaseUtils;
import uk.co.atgsoft.caveman.wine.Wine;
import uk.co.atgsoft.caveman.wine.WineColour;
import uk.co.atgsoft.caveman.wine.WineImpl;

/**
 *
 * @author adam.gillmore
 */
public class WineDaoImpl implements WineDao {

    public WineDaoImpl() {
        DatabaseUtils.createTable("CREATE TABLE IF NOT EXISTS WINE " +
           "(ID TEXT PRIMARY KEY," +
           " NAME           TEXT    NOT NULL, " + 
           " PRODUCER       TEXT    NOT NULL, " + 
           " REGION       TEXT    NOT NULL, " + 
           " COUNTRY       TEXT    NOT NULL, " + 
           " VINTAGE        INT, " + 
           " ALCOHOL        REAL, " + 
           " COLOUR         TEXT, " + 
           " PRICE          REAL, " + 
           " GRAPE         TEXT)");
    }
    
    
    
    @Override
    public void insertWine(final Wine wine) {
    DatabaseUtils.executeStatement(
          "INSERT INTO WINE (ID,NAME,PRODUCER,REGION,COUNTRY,VINTAGE,ALCOHOL,COLOUR,PRICE,GRAPE) " +
               "VALUES (" 
          + "'" + wine.getId() + "', "
          + "'" + wine.getName() + "', " 
          + "'" + wine.getProducer() + "', "
          + "'" + wine.getRegion() + "', "
          + "'" + wine.getCountry() + "', "
          + wine.getVintage() + ", "
          + wine.getAlcohol() + ", "
          + "'" + wine.getWineColour() + "', "
          + wine.getPrice().floatValue() + ", "
          + "'" + wine.getGrape() + "');");
    System.out.println("Wine added successfully");
    }
    
    public int getId(final Wine wine) {
        Connection c = null;
        Statement stmt = null;
        int id = 0;
        try {
          c = DriverManager.getConnection("jdbc:sqlite:test.db");
          stmt = c.createStatement();
          ResultSet rs = stmt.executeQuery(
                  "SELECT ID FROM WINE WHERE NAME='" 
                  + wine.getName() + "' AND PRODUCER='"
                  + wine.getProducer() + "' AND VINTAGE="
                  + wine.getVintage() + ";");
          rs.next();
          id = rs.getInt("ID");
          rs.close();
          stmt.close();
          c.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Operation done successfully");
        return id;
    }
    
    @Override
    public List<Wine> getAllWines() {
        final List<Wine> wines = new ArrayList<>();
        
        Connection c = null;
        Statement stmt = null;
        try {
//          Class.forName("org.sqlite.JDBC");
          c = DriverManager.getConnection("jdbc:sqlite:test.db");
          System.out.println("Opened database successfully");

          stmt = c.createStatement();
          ResultSet rs = stmt.executeQuery( "SELECT * FROM WINE;" );
          while ( rs.next() ) {
             final Wine wine = new WineImpl(
                rs.getString("id"),
                rs.getString("name"),
                rs.getString("producer"),
                rs.getString("region"),
                rs.getString("country"),
                rs.getInt("vintage"),
                rs.getFloat("alcohol"),
                new BigDecimal(rs.getFloat("price")),
                WineColour.valueOf(rs.getString("colour").toUpperCase()),
                rs.getString("grape")
             );
             wines.add(wine);
             
          }
          rs.close();
          stmt.close();
          c.close();
        } catch ( Exception e ) {
          System.err.println( e.getClass().getName() + ": " + e.getMessage() );
        }
        System.out.println("Operation done successfully");
  
        
        return wines;
    }

    @Override
    public void removeWine(final Wine wine) {
        DatabaseUtils.executeStatement(
           "DELETE FROM WINE WHERE NAME = '" + wine.getName() + "';");
        System.out.println("Wine deleted successfully");
    }

    @Override
    public void updateWine(Wine wine) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
