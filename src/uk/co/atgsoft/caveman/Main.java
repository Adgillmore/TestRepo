/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.atgsoft.caveman;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableStringValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Pair;
import uk.co.atgsoft.caveman.database.dao.DepletionDao;
import uk.co.atgsoft.caveman.database.dao.DepletionDaoImpl;
import uk.co.atgsoft.caveman.database.dao.PurchaseDao;
import uk.co.atgsoft.caveman.database.dao.PurchaseDaoImpl;
import uk.co.atgsoft.caveman.database.dao.StockDao;
import uk.co.atgsoft.caveman.database.dao.StockDaoImpl;
import uk.co.atgsoft.caveman.database.dao.WineDao;
import uk.co.atgsoft.caveman.database.dao.WineDaoImpl;
import uk.co.atgsoft.caveman.ui.CellarTableController;
import uk.co.atgsoft.caveman.ui.WineDetailController;
import uk.co.atgsoft.caveman.wine.BottleSize;
import uk.co.atgsoft.caveman.wine.Wine;
import uk.co.atgsoft.caveman.wine.record.purchase.PurchaseEntryImpl;
import uk.co.atgsoft.caveman.wine.record.stock.StockRecord;

/**
 *
 * @author adam
 */
public class Main extends Application {
    
    private static final String DATABASE_NAME="user1";
    
    private TableView stockTable;
    
    private WineDao wineDao;
    
    private WineDetailController wineDetailController;
    
    private PurchaseDao purchaseDao;
    
    private StockDao stockDao;
    
    private DepletionDao depletionDao;
    
    private ObservableList<StockRecord> stockList;
    
    private StockRecord editedRecord;
    
    private Dialog<Pair<String, String>> wineDetailDialog;
    
    private BooleanProperty saveDisabled = new SimpleBooleanProperty();
    
    private ObservableStringValue vintageLimit = new SimpleStringProperty("1850");
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        initDaos();
        initStockTable();
        
        wineDetailDialog = initDialog(stockList, wineDao);
        
        
        final BorderPane root = new BorderPane(stockTable, initToolbar(stockList, stockDao), 
                null, null, getFilterPane());
        root.setFocusTraversable(false);
        Scene scene = new Scene(root, 1280, 800);
        
        primaryStage.setTitle("CaveMan - The wine cave manager");
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }
    
    private void initDaos() {
        wineDao = new WineDaoImpl(DATABASE_NAME);
        purchaseDao = new PurchaseDaoImpl(DATABASE_NAME);
        depletionDao = new DepletionDaoImpl(DATABASE_NAME);
        stockDao = new StockDaoImpl(DATABASE_NAME);
    }
    
    private void initStockTable() {
        stockList = FXCollections.observableArrayList();
        stockList.addAll(getStock());
        stockTable = initTableView(stockList);
    }
    
    private ObservableList<StockRecord> getStock() {
        final List<Wine> wines = wineDao.getAllWines();
        final ObservableList<StockRecord> stock = FXCollections.observableArrayList();
        for (Wine wine : wines) {
            stock.add(stockDao.getStockRecord(wine));
        }
        return stock;
    }
    
    private Node getFilterPane() {
        final VBox filterPane = new VBox(10, new Text("All wines"), new Text("Favourites"));
        filterPane.setAlignment(Pos.TOP_LEFT);
        filterPane.setPrefWidth(125);
        filterPane.setPadding(new Insets(10, 10, 10, 15));
        return filterPane;
    }
    
    private Node getPreviewPane() {
        final VBox previewPane = new VBox();
        previewPane.setPrefWidth(300);
        previewPane.setPadding(new Insets(10, 10, 10, 10));
        return previewPane;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    private Node initToolbar(final ObservableList<StockRecord> wines, final StockDao dao) {
        final Button addButton = new Button("Add");
        addButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(final ActionEvent event) {
                wineDetailController.setWine(null);
//                wineDetailController.setPurchaseRecords(new ArrayList<>());
                wineDetailDialog.show();
            }
        });
        
        final Button removeButton = new Button("Remove");
        removeButton.disableProperty().bind(stockTable.getSelectionModel().selectedItemProperty().isNull());
//        removeButton.setOnAction((ActionEvent event) -> {
//            wines.remove(table.getSelectionModel().getFocusedIndex());
//            dao.removeStock((StockRecord) table.getSelectionModel().getSelectedItem());
//        });
        final HBox toolbar = new HBox(10, addButton, removeButton);
        toolbar.setPadding(new Insets(10, 10, 10, 10));
        toolbar.setBackground(new Background(new BackgroundFill(Color.LIGHTBLUE, CornerRadii.EMPTY, Insets.EMPTY)));
        return toolbar;
    }
    
    private Dialog initDialog(final ObservableList<StockRecord> stock, final WineDao dao) {
        final FXMLLoader loader = new FXMLLoader();
        try {
            loader.load(WineDetailController.class.getResourceAsStream("wine_detail.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        final AnchorPane wineDetailPane = (AnchorPane) loader.getRoot();
        wineDetailController = loader.getController();
        final Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New Wine");
        ButtonType saveButtonType = new ButtonType("Save", ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(saveButtonType, ButtonType.CANCEL);
        dialog.getDialogPane().setContent(wineDetailPane);
        final Button saveButton = (Button) dialog.getDialogPane().lookupButton(saveButtonType);
        saveButton.setOnAction(getClickListener());
        return dialog;
    }
    
    private EventHandler<ActionEvent> getClickListener() {
        return (ActionEvent event) -> {
            
            final Wine wine = wineDetailController.getWine();
            if (wineDao.exists(wine)) {
                wineDao.updateWine(wine);
                //update purchases;
                System.out.println("updating purchases");
            } else {
                wineDao.insertWine(wine);
                // insert 1 wine
                final LocalDate date = LocalDate.now();
                purchaseDao.addPurchase(new PurchaseEntryImpl(wine.getId() + ":" + date, wine, 
                        new BigDecimal(0), 1, BottleSize.STANDARD, "", date));
            }
            
//            final List<PurchaseEntry> records = wineDetailController.getPurchaseRecords();
//            for (PurchaseEntry r : records) {
//                if (r.getId() == null) {
//                    r.setId(r.getWine().getId() + ":" + r.getDate() + ":" + r.getBottleSize() + ":" + r.getQuantity());
//                    purchaseDao.addPurchase(r);
//                } else {
//                    // update?
//                }
//                
//            }
//            if (editedRecord != null) {
//                stock.remove(editedRecord);
//            }
            
            final StockRecord record = stockDao.getStockRecord(wine);
            stockList.add(record);
            editedRecord = null;
        };
    }
    
    private TableView initTableView(final ObservableList<StockRecord> wines) {
        final FXMLLoader loader = new FXMLLoader();
        try {
            stockTable = loader.load(CellarTableController.class.getResourceAsStream("cellar_table.fxml"));
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        stockTable.setItems(wines);
        stockTable.setRowFactory(callback -> {
            final TableRow<StockRecord> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (row.getItem() == null) return;
                final Wine wine = row.getItem().getWine();
                if (event.getClickCount() == 2 && !row.isEmpty()) {
                    wineDetailController.setWine(wine);
                    wineDetailController.setPurchaseRecords(purchaseDao.getPurchases(wine));
                    editedRecord = row.getItem();
                    wineDetailDialog.show();
                }
            });
            return row;
        });
        
        
        return stockTable;
    }
    
}
