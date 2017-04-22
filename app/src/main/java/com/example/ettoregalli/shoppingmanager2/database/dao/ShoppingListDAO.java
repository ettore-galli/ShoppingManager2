package com.example.ettoregalli.shoppingmanager2.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.sqliteutilities.SQLCudQueryBuilder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListDAO extends ShoppingListManagerBaseDAO {

    public ShoppingListDAO(Context context) {
        super(context);
    }

    public List<ListItem> getListItemList(int listId, int itemId) {
        return getListItemList(getReadableDatabase(), listId, itemId);
    }

    private List<ListItem> getListItemList(SQLiteDatabase sqLiteDatabase, int listId, int itemId) {
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
                "IFNULL(item_final_destination, '" + UNDEFINED_FINAL_DESTINATION + "') AS item_final_destination ",
                "(SELECT 1 + COUNT(DISTINCT item_final_destination) FROM list_items AS fd WHERE fd.list_id=li.list_id AND fd.item_final_destination < li.item_final_destination) AS item_final_destination_visual_index "
        };
        String selection;
        String[] selectionArgs;
        if (itemId == 0) {
            selection = "(list_id=?)";
            selectionArgs = new String[1];
            selectionArgs[0] = String.valueOf(listId);
        } else {
            selection = "(list_id=?) AND (item_id=?)";
            selectionArgs = new String[2];
            selectionArgs[0] = String.valueOf(listId);
            selectionArgs[1] = String.valueOf(itemId);
        }

        String orderBy = "list_id, item_id";

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

    public void dltListItem(SQLiteDatabase sqLiteDatabase, int listId, int itemId) throws SQLCudQueryBuilder.ClassNotSupportedException {
        sqLiteDatabase.beginTransaction();
        String q = getListItemQueryBuilder(listId, itemId, null).getUpdateQuery();
        sqLiteDatabase.execSQL(q);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

} // ShoppingListDAO
