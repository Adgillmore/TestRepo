/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package uk.co.atgsoft.caveman.wine.record.purchase;

import java.math.BigDecimal;
import uk.co.atgsoft.caveman.wine.record.DatedRecordEntry;

/**
 * An abstraction of a wine purchase.
 * @author adam
 */
public interface PurchaseEntry extends DatedRecordEntry {
    
    void setPrice(BigDecimal price);
    
    BigDecimal getPrice();
    
    void setVendor(String vendor);
    
    String getVendor();
}
