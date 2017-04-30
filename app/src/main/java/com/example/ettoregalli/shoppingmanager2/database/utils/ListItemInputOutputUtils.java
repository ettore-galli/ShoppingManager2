package com.example.ettoregalli.shoppingmanager2.database.utils;

import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;

import java.math.BigDecimal;

public class ListItemInputOutputUtils {

    /**
     * Costruisce un nuovo elemento di lista a partire da input testuale
     *
     * @param listIdInput
     * @param itemIdInput
     * @param descriptionInput
     * @param unitInput
     * @param quantityInput
     * @param unitPriceInput
     * @param amountAddedInput
     * @param finalDestinationInput
     * @return
     */
    public ListItem getListItemFromInputFields(
            String listIdInput,
            String itemIdInput,
            String descriptionInput,
            String unitInput,
            String quantityInput,
            String unitPriceInput,
            String amountAddedInput,
            String finalDestinationInput
    ) throws InputOutputException {
        /* Istanziazione */
        ListItem li = new ListItem();
        /* Utility input */
        InputOutputUtils iou = new InputOutputUtils();
        /* Valori e default*/
        li.setListId(new Integer(listIdInput).intValue());
        li.setItemId(new Integer(itemIdInput).intValue());
        li.setDescription(descriptionInput);
        li.setUnit(unitInput);
        li.setQuantity(iou.getBigDecimalFromInput(quantityInput));
        li.setUnitPrice(iou.getBigDecimalFromInput(unitPriceInput));
        li.setAmountAdded(iou.getBigDecimalFromInput(amountAddedInput));
        if (li.getQuantity() == null) {
            li.setQuantity(BigDecimal.ONE);
        }
        if (li.getUnitPrice() == null) {
            li.setUnitPrice(BigDecimal.ZERO);
        }
        if (li.getAmountAdded() == null) {
            li.setAmountAdded(BigDecimal.ZERO);
        }
        li.setFinalDestination(finalDestinationInput.trim().toUpperCase());
        return li;

    }

}
