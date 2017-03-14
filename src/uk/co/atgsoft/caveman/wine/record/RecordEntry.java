/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.atgsoft.caveman.wine.record;

import uk.co.atgsoft.caveman.wine.BottleSize;
import uk.co.atgsoft.caveman.wine.Wine;

/**
 * Abstraction of common metadata for a wine record.
 * @author adam
 */
public interface RecordEntry {
    
    void setId(String id);
    
    String getId();
    
    void setWine(Wine wine);
    
    Wine getWine();
    
    void setQuantity(int quantity);
    
    int getQuantity();
    
    void setBottleSize(BottleSize size);
    
    BottleSize getBottleSize();
}
