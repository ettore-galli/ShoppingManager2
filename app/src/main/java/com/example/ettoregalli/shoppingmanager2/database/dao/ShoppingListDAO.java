package com.example.ettoregalli.shoppingmanager2.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ettoregalli.shoppingmanager2.database.model.ListHeader;
import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.model.ListSubtotal;
import com.example.ettoregalli.shoppingmanager2.database.sqliteutilities.SQLCudQueryBuilder;
import com.example.ettoregalli.shoppingmanager2.ui.ShoppingListDriverConstants;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingListDAO extends ShoppingListManagerBaseDAO {

    public ShoppingListDAO(Context context) {
        super(context);
    }

    public List<ListItem> getListItemList(int listId, int itemId) {
        String orderBy = "list_id, item_id";
        return getListItemList(getReadableDatabase(), listId, itemId, null, orderBy);
    }

    public List<ListItem> getListItemList(int listId, int itemId, String finalDestination, String orderBy) {
        return getListItemList(getReadableDatabase(), listId, itemId, finalDestination, orderBy);
    }

    private List<ListItem> getListItemList(SQLiteDatabase sqLiteDatabase, int listId, int itemId, String finalDestination, String orderBy) {
        List<ListItem> listItems = new ArrayList<ListItem>();
        String[] LIST_ITEMS_TABLE_COLUMNS = {
                "list_id",
                "item_id",
                "item_description",
                "item_unit",
                "item_quantity",
                "item_unit_price",
                "item_amount_added",
                TOTAL_PRICE_FORMULA + " AS item_total_price ",
                FINAL_DESTINATION_FORMULA + " AS item_final_destination ",
                FINAL_DESTINATION_VISUAL_INDEX_FORMULA + " AS item_final_destination_visual_index "
        };

        SQLCudQueryBuilder sqb = new SQLCudQueryBuilder(LIST_ITEMS_TABLE_NAME);
        try {
            if (listId != 0) {
                sqb.put("list_id", listId, true);
            }
            if (itemId != 0) {
                sqb.put("item_id", itemId, true);
            }
            if (finalDestination != null) {
                sqb.put("item_final_destination", finalDestination, true);
            }
        } catch (SQLCudQueryBuilder.ClassNotSupportedException ce) {
        }

        String selection = sqb.getWhereClauseNoWhere();
        String[] selectionArgs = null;

        Cursor slcur = sqLiteDatabase.query(
                LIST_ITEMS_TABLE_NAME + " AS li",         // The table to query
                LIST_ITEMS_TABLE_COLUMNS,                 // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                orderBy                                   // The sort order
        );

        while (slcur.moveToNext()) {
            ListItem li = new ListItem();

            li.setListId(slcur.getInt(slcur.getColumnIndex("list_id")));
            li.setItemId(slcur.getInt(slcur.getColumnIndex("item_id")));
            li.setDescription(slcur.getString(slcur.getColumnIndex("item_description")));
            li.setQuantity(new BigDecimal(slcur.getDouble(slcur.getColumnIndex("item_quantity"))));
            li.setUnit(slcur.getString(slcur.getColumnIndex("item_unit")));
            li.setUnitPrice(new BigDecimal(slcur.getDouble(slcur.getColumnIndex("item_unit_price"))));
            li.setTotalPrice(new BigDecimal(slcur.getDouble(slcur.getColumnIndex("item_total_price"))));
            li.setAmountAdded(new BigDecimal(slcur.getDouble(slcur.getColumnIndex("item_amount_added"))));
            li.setFinalDestination(slcur.getString(slcur.getColumnIndex("item_final_destination")));
            li.setFinalDestinationVisualIndex(slcur.getInt(slcur.getColumnIndex("item_final_destination_visual_index")));
            listItems.add(li);

        }

        return listItems;
    }

    private SQLCudQueryBuilder getListItemQueryBuilder(int listId, int itemId, ListItem li) throws SQLCudQueryBuilder.ClassNotSupportedException {
        SQLCudQueryBuilder sqb = new SQLCudQueryBuilder(super.LIST_ITEMS_TABLE_NAME);

        /* Campi chiave */
        sqb.put("list_id", listId, true);
        sqb.put("item_id", itemId, true);

        /* Campi non di chiave */
        if (li != null) {
            sqb.put("item_description", li.getDescription());
            sqb.put("item_unit", li.getUnit());
            sqb.put("item_quantity", li.getQuantity());
            sqb.put("item_unit_price", li.getUnitPrice());
            sqb.put("item_amount_added", li.getAmountAdded());
            sqb.put("item_final_destination", li.getFinalDestination());
        }
        return sqb;
    }

    public int addListItem(int listId, ListItem li) throws SQLCudQueryBuilder.ClassNotSupportedException {
        return this.addListItem(getWritableDatabase(), listId, li);
    }

    public int addListItem(SQLiteDatabase sqLiteDatabase, int listId, ListItem li) throws SQLCudQueryBuilder.ClassNotSupportedException {
        int newItemId;
        sqLiteDatabase.beginTransaction();
        newItemId = this.getNextItemId(listId);
        String q = getListItemQueryBuilder(listId, newItemId, li).getInsertQuery();
        sqLiteDatabase.execSQL(q);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        return newItemId;
    }

    public void updListItem(int listId, int itemId, ListItem li) throws SQLCudQueryBuilder.ClassNotSupportedException {
        updListItem(getWritableDatabase(), listId, itemId, li);
    }

    public void updListItem(SQLiteDatabase sqLiteDatabase, int listId, int itemId, ListItem li) throws SQLCudQueryBuilder.ClassNotSupportedException {
        sqLiteDatabase.beginTransaction();
        String q = getListItemQueryBuilder(listId, itemId, li).getUpdateQuery();
        sqLiteDatabase.execSQL(q);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public void dltListItem(int listId, int itemId) throws SQLCudQueryBuilder.ClassNotSupportedException {
        dltListItem(getWritableDatabase(), listId, itemId);
    }

    private void dltListItem(SQLiteDatabase sqLiteDatabase, int listId, int itemId) throws SQLCudQueryBuilder.ClassNotSupportedException {
        sqLiteDatabase.beginTransaction();
        String q = getListItemQueryBuilder(listId, itemId, null).getDeleteQuery();
        sqLiteDatabase.execSQL(q);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public List<String> getFinalDestinationsList(int listId, int itemId) {
        return getFinalDestinationsList(getReadableDatabase(), listId, itemId);
    }

    private List<String> getFinalDestinationsList(SQLiteDatabase sqLiteDatabase, int listId, int itemId) {
        List<String> finalDestinations = new ArrayList<String>();
        String[] LIST_ITEMS_TABLE_COLUMNS = {FINAL_DESTINATION_FORMULA};

        SQLCudQueryBuilder sqb = new SQLCudQueryBuilder(LIST_ITEMS_TABLE_NAME);
        try {
            if (listId != 0) {
                sqb.put("list_id", listId, true);
            }
            if (itemId != 0) {
                sqb.put("item_id", itemId, true);
            }
        } catch (SQLCudQueryBuilder.ClassNotSupportedException ce) {
        }

        String selection = sqb.getWhereClauseNoWhere();
        String[] selectionArgs = null;

        Cursor slcur = sqLiteDatabase.query(
                true,
                LIST_ITEMS_TABLE_NAME + " AS li",         // The table to query
                LIST_ITEMS_TABLE_COLUMNS,                 // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null,                                     // The sort order
                null
        );

        while (slcur.moveToNext()) {
            finalDestinations.add(slcur.getString(0));
        }

        return finalDestinations;
    } //

    public List<ListSubtotal> getListSubtotalList(int listId, int itemId) {
        return getListSubtotalList(getReadableDatabase(), listId, itemId);
    }

    private List<ListSubtotal> getListSubtotalList(SQLiteDatabase sqLiteDatabase, int listId, int itemId) {
        List<ListSubtotal> listSubtotals = new ArrayList<ListSubtotal>();
        BigDecimal grandTotal = new BigDecimal(0);
        String[] LIST_ITEMS_TABLE_COLUMNS = {
                FINAL_DESTINATION_FORMULA + " AS item_final_destination ",
                "SUM(" + TOTAL_PRICE_FORMULA + ") AS item_total_price ",
                FINAL_DESTINATION_VISUAL_INDEX_FORMULA + " AS item_final_destination_visual_index "
        };

        SQLCudQueryBuilder sqb = new SQLCudQueryBuilder(LIST_ITEMS_TABLE_NAME);
        try {
            if (listId != 0) {
                sqb.put("list_id", listId, true);
            }
            if (itemId != 0) {
                sqb.put("item_id", itemId, true);
            }
        } catch (SQLCudQueryBuilder.ClassNotSupportedException ce) {
        }

        String selection = sqb.getWhereClauseNoWhere();
        String[] selectionArgs = null;

        Cursor slcur = sqLiteDatabase.query(
                LIST_ITEMS_TABLE_NAME + " AS li",         // The table to query
                LIST_ITEMS_TABLE_COLUMNS,                 // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                "item_final_destination",                 // don't group the rows
                null,                                     // don't filter by row groups
                "item_final_destination"                  // The sort order
        );

        /* Subtotali */
        while (slcur.moveToNext()) {
            ListSubtotal li = new ListSubtotal();
            BigDecimal totalPrice = new BigDecimal(slcur.getDouble(slcur.getColumnIndex("item_total_price")));
            li.setTotalPrice(totalPrice);
            li.setFinalDestination(slcur.getString(slcur.getColumnIndex("item_final_destination")));
            li.setFinalDestinationVisualIndex(slcur.getInt(slcur.getColumnIndex("item_final_destination_visual_index")));
            grandTotal = grandTotal.add(totalPrice);
            listSubtotals.add(li);
        }

        /* Totale generale */
        ListSubtotal li = new ListSubtotal();
        li.setTotalPrice(grandTotal);
        li.setFinalDestination(ShoppingListDriverConstants.SUBTOTALS_GRAND_TOTAL_CAPTION);
        li.setFinalDestinationVisualIndex(ShoppingListDriverConstants.SUBTOTALS_GRAND_TOTAL_VISUAL_INDEX); // Valore convenzionale per il totale

        listSubtotals.add(li);

        return listSubtotals;
    }

    private SQLCudQueryBuilder getListHeaderQueryBuilder(int listId) throws SQLCudQueryBuilder.ClassNotSupportedException {
        SQLCudQueryBuilder sqb = new SQLCudQueryBuilder(super.LIST_HEAD_TABLE_NAME);

        /* Campi chiave */
        sqb.put("list_id", listId, true);

        /* Campi non di chiave */
        sqb.put("list_title", "Lista N° " + listId);

        return sqb;
    }

    /**
     * Reperisce id lista "corrente"
     * @return
     */
    public int getCurrentListId() {
        return getLastListId(getReadableDatabase());
    }

    /**
     * Creazione di una nuova lista
     * @return
     * @throws SQLCudQueryBuilder.ClassNotSupportedException
     */
    public int createNewList() throws SQLCudQueryBuilder.ClassNotSupportedException {
        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        int newListId = getNextListId(db);
        String q = getListHeaderQueryBuilder(newListId).getInsertQuery();
        db.execSQL(q);
        db.setTransactionSuccessful();
        db.endTransaction();
        return getCurrentListId();
    }

    /**
     * Lista di testate
     * @param listId
     * @return
     */
    public List<ListHeader> getListHeaderList(int listId) {
        return getListHeaderList(getReadableDatabase(), listId);
    }

    /**
     * Lista di testate
     * @param sqLiteDatabase
     * @param listId
     * @return
     */
    private List<ListHeader> getListHeaderList(SQLiteDatabase sqLiteDatabase, int listId) {
        List<ListHeader> hlist = new ArrayList<ListHeader>();

        String[] LIST_ITEMS_TABLE_COLUMNS = {
                "list_id",
                "list_title",
                "list_date"
        };
        SQLCudQueryBuilder sqb = new SQLCudQueryBuilder(LIST_HEAD_TABLE_NAME);
        try {
            if (listId != 0) {
                sqb.put("list_id", listId, true);
            }
        } catch (SQLCudQueryBuilder.ClassNotSupportedException ce) {
        }

        String selection = sqb.getWhereClauseNoWhere();
        String[] selectionArgs = null;

        Cursor slcur = sqLiteDatabase.query(
                LIST_HEAD_TABLE_NAME + " AS li",         // The table to query
                LIST_ITEMS_TABLE_COLUMNS,                 // The columns to return
                selection,                                // The columns for the WHERE clause
                selectionArgs,                            // The values for the WHERE clause
                null,                                     // don't group the rows
                null,                                     // don't filter by row groups
                null                                      // The sort order
        );

        while (slcur.moveToNext()) {

            String r = slcur.getInt(0) + " " + slcur.getString(1);
            System.out.println(r);
            ListHeader lh = new ListHeader();
            lh.setListId(slcur.getInt(slcur.getColumnIndex("list_id")));
            lh.setListTitle(slcur.getString(slcur.getColumnIndex("list_title")));
            try {
                Date d = (DateFormat.getDateInstance()).parse(slcur.getString(slcur.getColumnIndex("list_date")));
                lh.setListDate(d);
            } catch (Exception e) {
            }
            hlist.add(lh);
        }
        return hlist;
    }

    /**
     * Descrizione della lista corrente
     * @return
     */
    public String getCurrentListDescription() {
        String ldes = "";
        int curList = getCurrentListId();
        ListHeader lh = null;
        try {
            lh = getListHeaderList(getReadableDatabase(), curList).get(0);
            ldes = lh.getListTitle();
        } catch (Exception e) {
        }
        return ldes;
    }

} // ShoppingListDAO
