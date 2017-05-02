package com.example.ettoregalli.shoppingmanager2.database.dao;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.ettoregalli.shoppingmanager2.database.utils.InputOutputUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ShoppingListManagerBaseDAO extends SQLiteOpenHelper {
    public static final String ORDER_ITEMS_BY_ID = "list_id, item_id";
    public static final String ORDER_ITEMS_BY_FINAL_DESTINATION = "list_id, item_final_destination, item_id";

    /* Attributi di classe */
    protected static final String DATABASE_NAME = "shopping_list.db";
    protected static final int DATABASE_VERSION = 1;

    /* Tipi statement SQL */
    protected static final String STATEMENT_TYPE_SELECT = "SELECT";
    protected static final String STATEMENT_TYPE_INSERT = "INSERT";
    protected static final String STATEMENT_TYPE_UPDATE = "UPDATE";
    protected static final String STATEMENT_TYPE_DELETE = "DELETE";

    /* Tipi SQL di destinazione */
    protected static final String SQL_TYPE_TEXT = "TEXT";
    protected static final String SQL_TYPE_NUMERIC = "NUMERIC";
    protected static final String SQL_TYPE_DATE = "DATE";

    /* Destinazione finale indefinita */
    protected static final String UNDEFINED_FINAL_DESTINATION = "altro";

    protected static final String LIST_HEAD_TABLE_NAME = "list_headers";
    protected static final String LIST_HEAD_TABLE_DROP = "DROP TABLE IF EXISTS " + LIST_HEAD_TABLE_NAME;
    protected static final String LIST_HEAD_TABLE_CREATE = "" +
            "CREATE TABLE " + LIST_HEAD_TABLE_NAME + "( " +
            "  list_id            INTEGER, " +
            "  list_title         TEXT, " +
            "  list_date          TEXT" +
            ")";

    protected static final String LIST_ITEMS_TABLE_NAME = "list_items";
    protected static final String LIST_ITEMS_TABLE_DROP = "DROP TABLE IF EXISTS " + LIST_ITEMS_TABLE_NAME;
    protected static final String LIST_ITEMS_TABLE_CREATE = "" +
            "CREATE TABLE " + LIST_ITEMS_TABLE_NAME + "( " +
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
    protected static final String TOTAL_PRICE_FORMULA = "(item_quantity*item_unit_price + item_amount_added)";
    protected static final String FINAL_DESTINATION_FORMULA = "IFNULL(item_final_destination, '" + UNDEFINED_FINAL_DESTINATION + "')";
    protected static final String FINAL_DESTINATION_VISUAL_INDEX_FORMULA = "(SELECT 1 + COUNT(DISTINCT item_final_destination) FROM list_items AS fd WHERE fd.list_id=li.list_id AND fd.item_final_destination < li.item_final_destination)";

    /* Unità di misura */
    private static final String UNIT_NR = "NR";
    private static final String UNIT_KG = "KG";
    private static final String UNIT_LT = "LT";
    private static final String[] UNITS = {UNIT_NR, UNIT_KG, UNIT_LT};

    /* Utilities di input/output */
    InputOutputUtils iou;

    public ShoppingListManagerBaseDAO(Context context) {
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

    public boolean listHeaderExists(int listId) {
        return listHeaderExists(getReadableDatabase(), listId);
    }

    private boolean listHeaderExists(SQLiteDatabase sqLiteDatabase, int listId) {
        boolean headerExists = false;
        String q = "SELECT COUNT(1) FROM " + LIST_HEAD_TABLE_NAME + " WHERE (list_id=?)";
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

    private boolean listItemExists(SQLiteDatabase sqLiteDatabase, int listId, int itemId) {
        boolean itemExists = false;
        String q = "SELECT COUNT(1) FROM " + LIST_ITEMS_TABLE_NAME + " WHERE (list_id=?) AND (item_id=?)";
        String[] qArgs = {(new Integer(listId).toString()), (new Integer(itemId).toString())};
        Cursor cEsi = sqLiteDatabase.rawQuery(q, qArgs);
        while (cEsi.moveToNext()) {
            itemExists = (cEsi.getInt(0) > 0);
        }
        return itemExists;
    }

    public int getNextListId() {
        return getNextListId(getReadableDatabase());
    }

    private int getNextListId(SQLiteDatabase sqLiteDatabase) {
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

    private int getNextItemId(SQLiteDatabase sqLiteDatabase, int listId) {
        int nextItemId = 0;
        String q = "SELECT 1 + IFNULL(MAX(item_id), 0) FROM list_items WHERE (list_id=?)";
        String[] qArgs = {(new Integer(listId).toString())};
        Cursor cGetListId = sqLiteDatabase.rawQuery(q, qArgs);
        while (cGetListId.moveToNext()) {
            nextItemId = cGetListId.getInt(0);
        }
        return nextItemId;
    }

    public List<String> getUnitsList() {
        return new ArrayList<String>(Arrays.asList(UNITS));
    }

} // ShoppingListDAO
