package com.example.ettoregalli.shoppingmanager2.database.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ettoregalli.shoppingmanager2.database.model.ListItem;
import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ShoppingListDAO extends SQLiteOpenHelper {
    /* Attributi di classe */
    private static final String DATABASE_NAME = "shopping_list.db";
    private static final int DATABASE_VERSION = 1;

    /* Tipi statement SQL */
    private static final String STATEMENT_TYPE_SELECT = "SELECT";
    private static final String STATEMENT_TYPE_INSERT = "INSERT";
    private static final String STATEMENT_TYPE_UPDATE = "UPDATE";
    private static final String STATEMENT_TYPE_DELETE = "DELETE";

    /* Tipi SQL di destinazione */
    private static final String SQL_TYPE_TEXT = "TEXT";
    private static final String SQL_TYPE_NUMERIC = "NUMERIC";
    private static final String SQL_TYPE_DATE = "DATE";

    /* Destinazione finale indefinita */
    private static final String UNDEFINED_FINAL_DESTINATION = "altro";

    private static final String LIST_HEAD_TABLE_DROP = "DROP TABLE IF EXISTS list_headers";
    private static final String LIST_HEAD_TABLE_CREATE = "" +
            "CREATE TABLE list_headers( " +
            "  list_id            INTEGER, " +
            "  list_title         TEXT, " +
            "  list_date          TEXT" +
            ")";

    private static final String LIST_ITEMS_TABLE_DROP = "DROP TABLE IF EXISTS list_items";
    private static final String LIST_ITEMS_TABLE_CREATE = "" +
            "CREATE TABLE list_items ( " +
            "  list_id                  INTEGER, " +
            "  item_id                  INTEGER, " +
            "  item_description         TEXT, " +
            "  item_unit                TEXT, " +
            "  item_quantity            DECIMAL(15, 5), " +
            "  item_unit_price          DECIMAL(15, 5), " +
            "  item_amount_added        DECIMAL(15, 5), " +
            "  item_final_destination   TEXT,  " +
            "  PRIMARY KEY(list_id, item_id) " +
            ")";
    private static final String TOTAL_PRICE_FORMULA = "(item_quantity*item_unit_price + item_amount_added)";

    /* Utilities di input/output */
    InputOutputUtils iou;

    public ShoppingListDAO(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.iou = new InputOutputUtils();
    }

    public void scratchDelete() {
        SQLiteDatabase db = getWritableDatabase();
        destroyDatabase(db);
        onCreate(db);
    }

    public void destroyDatabase(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LIST_HEAD_TABLE_DROP);
        sqLiteDatabase.execSQL(LIST_ITEMS_TABLE_DROP);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(LIST_HEAD_TABLE_CREATE);
        sqLiteDatabase.execSQL(LIST_ITEMS_TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        /*
        this.destroyDatabase(sqLiteDatabase);
        this.onCreate(sqLiteDatabase);
        */
    }

    public List<ListItem> getListItemList(int listId, int itemId) {
        return getListItemList(getReadableDatabase(), listId, itemId);
    }

    public List<ListItem> getListItemList(SQLiteDatabase sqLiteDatabase, int listId, int itemId) {
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
                "list_items AS li",         // The table to query
                LIST_ITEMS_TABLE_COLUMNS,   // The columns to return
                selection,                  // The columns for the WHERE clause
                selectionArgs,              // The values for the WHERE clause
                null,                       // don't group the rows
                null,                       // don't filter by row groups
                orderBy                     // The sort order
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

    public int getNextListId() {
        return getNextListId(getReadableDatabase());
    }

    public int getNextListId(SQLiteDatabase sqLiteDatabase) {
        int nextItemId = 0;
        String q = "SELECT 1 + IFNULL(MAX(list_id), 0) FROM list_items";
        Cursor cGetListId = sqLiteDatabase.rawQuery(q, null);
        while (cGetListId.moveToNext()) {
            nextItemId = cGetListId.getInt(0);
        }
        return nextItemId;
    }

    public int getNextItemId(int listId) {
        return getNextItemId(getReadableDatabase(), listId);
    }

    public int getNextItemId(SQLiteDatabase sqLiteDatabase, int listId) {
        int nextItemId = 0;
        String q = "SELECT 1 + IFNULL(MAX(item_id), 0) FROM list_items WHERE (list_id=?)";
        String[] qArgs = {(new Integer(listId).toString())};
        Cursor cGetListId = sqLiteDatabase.rawQuery(q, qArgs);
        while (cGetListId.moveToNext()) {
            nextItemId = cGetListId.getInt(0);
        }
        return nextItemId;
    }

    public boolean listHeaderExists(int listId) {
        return listHeaderExists(getReadableDatabase(), listId);
    }

    public boolean listHeaderExists(SQLiteDatabase sqLiteDatabase, int listId) {
        boolean headerExists = false;
        String q = "SELECT COUNT(1) FROM list_headers WHERE (list_id=?)";
        String[] qArgs = {(new Integer(listId).toString())};
        Cursor cEsi = sqLiteDatabase.rawQuery(q, qArgs);
        while (cEsi.moveToNext()) {
            headerExists = (cEsi.getInt(0) > 0);
        }
        return headerExists;
    }

    public boolean listItemExists(int listId, int itemId) {
        return listItemExists(getReadableDatabase(), listId, itemId);
    }

    public boolean listItemExists(SQLiteDatabase sqLiteDatabase, int listId, int itemId) {
        boolean itemExists = false;
        String q = "SELECT COUNT(1) FROM list_items WHERE (list_id=?) AND (item_id=?)";
        String[] qArgs = {(new Integer(listId).toString()), (new Integer(itemId).toString())};
        Cursor cEsi = sqLiteDatabase.rawQuery(q, qArgs);
        while (cEsi.moveToNext()) {
            itemExists = (cEsi.getInt(0) > 0);
        }
        return itemExists;
    }

    public ContentValues getListItemNonKeyContentValues(ListItem li) {
        ContentValues values = new ContentValues();
        values.put("item_description", li.getDescription());
        values.put("item_unit", li.getUnit());
        values.put("item_quantity", li.getQuantity().doubleValue());
        values.put("item_unit_price", li.getUnitPrice().doubleValue());
        values.put("item_amount_added", li.getAmountAdded().doubleValue());
        values.put("item_final_destination", li.getFinalDestination());
        return values;
    }



    public ContentValues getListItemNonKeyContentValues(int listId, int itemId) {
        ContentValues values = new ContentValues();
        return addListItemKeyToContentValues(listId, itemId, values);
    }

    public ContentValues addListItemKeyToContentValues(int listId, int itemId, ContentValues nonKeyCv) {
        ContentValues values = nonKeyCv;
        values.put("list_id", listId);
        values.put("item_id", itemId);
        return values;
    }

    public int addListItem(int listId, ListItem li) {
        return this.addListItem(getWritableDatabase(), listId, li);
    }

    public int addListItem(SQLiteDatabase sqLiteDatabase, int listId, ListItem li) {
        int newItemId;
        sqLiteDatabase.beginTransaction();
        newItemId = this.getNextItemId(listId);
        ContentValues values = getListItemNonKeyContentValues(li);
        values = addListItemKeyToContentValues(listId, newItemId, values);
        sqLiteDatabase.insert("list_items", null, values);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
        return newItemId;
    }

    public void updListItem(int listId, int itemId, ListItem li) {
        updListItem(getWritableDatabase(), listId, itemId, li);
    }

    public void updListItem(SQLiteDatabase sqLiteDatabase, int listId, int itemId, ListItem li) {
        sqLiteDatabase.beginTransaction();
        ContentValues nonKeyContentValues = getListItemNonKeyContentValues(li);
        String updateWhere = "(list_id=" + listId + ") AND (item_id=" + itemId + ")";
        sqLiteDatabase.update("list_items", nonKeyContentValues, updateWhere, null);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }

    public void dltListItem(int listId, int itemId) {
        dltListItem(getWritableDatabase(), listId, itemId);
    }

    public void dltListItem(SQLiteDatabase sqLiteDatabase, int listId, int itemId) {
        sqLiteDatabase.beginTransaction();
        String updateWhere = "(list_id=" + listId + ") AND (item_id=" + itemId + ")";
        sqLiteDatabase.delete("list_items", updateWhere, null);
        sqLiteDatabase.setTransactionSuccessful();
        sqLiteDatabase.endTransaction();
    }



} // ShoppingListDAO
