package com.example.ettoregalli.shoppingmanager2.database.sqliteutilities;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * <p><b>Purpose and scope
 * </p></b>
 * <p>The SQLCudQueryBuilder is a utility class that makes writing of SQL data modification statements easy;
 * with data modification meaning the usual CRUD operations less the "R"
 * </p>
 * <p><b>Definition of terms:
 * </p></b>
 * <ul>
 * <li><b>CUD:</b> <i>Create, Update, Delete database operations (i.e. INSERT INTO, UPDATE, DELETE SQL statements)</i>
 * </li>
 * <li><b>table:</b> <i>A single database table</i>
 * </li>
 * <li><b>field:</b> <i>A table column together with its own value and the indication whether it is a "key" field or not.
 * Fields can be divided into two categories according to their "key" nature:
 * </i>
 * <ul>
 * <li><b>key fields:</b> <i>Fields that are used to select records in update and delete operations and that appear in the WHERE clause of SQL statements</i></li>
 * <li><b>key fields:</b> <i>Fields whose values will be changed in UPDATE statements and that do not appear in the WHERE clause</i></li>
 * </ul>
 * <i>Please note that the "key" nature of fields is not related to the table primary key; when fields are added (see example below) the key nature simply represents the
 * fields that will be used to select the records to update or delete</i>
 * </li>
 * </ul>
 * <p>
 * <p><b>How the class works and should be used:
 * </b></p>
 * <p>The class basically consists of a collection of fields and a table name, from which SQL statements can be constructed.
 * The class is intended to be used as follows.
 * </p>
 * <p>First, create a new SQLCudQueryBuilder object:
 * </p>
 * {@code
 * SQLCudQueryBuilder myBuilder = new SQLCudQueryBuilder("myTable");
 * }
 * <p>Then, add the fields:
 * </p>
 * <pre>{@code
 * myBuilder.put("a_key_column", 123456, true);
 * myBuilder.put("a_string_column", "value");
 * myBuilder.put("a_numeric_column", 3.141);
 * }
 * </pre>
 * <p>At this point, SQL statements are generated automatically:
 * </p>
 * <pre>{@code
 * System.out.println(myBuilder.getInsertQuery());
 * System.out.println(myBuilder.getUpdateQuery());
 * System.out.println(myBuilder.getDeleteQuery());
 * }
 * </pre>
 * <p> The result is the following:
 * </p>
 * <pre>{@code
 * INSERT INTO myTable(a_numeric_column, a_string_column, a_key_column) VALUES(3.141, "value", 123456);
 * UPDATE myTable SET a_numeric_column=3.141, a_string_column="value" WHERE (a_key_column=123456);
 * DELETE FROM myTable WHERE (a_key_column=123456);
 * }
 * </pre>
 *
 * @author Ettore Galli
 * @version Pre Alpha 1
 * @todo How the class works
 * @todo Why "CUD"
 */

public class SQLCudQueryBuilder {

    /* Filter values for selecting key and/or non key columns */
    private static final String KEY_VALUES_NON_KEY = "N";
    private static final String KEY_VALUES_KEYS = "K";
    private static final String KEY_VALUES_BOTH = "B";

    /* Types of expression generated internally */
    private static final String EXPRESSION_TYPE_FIELDS = "F";
    private static final String EXPRESSION_TYPE_VALUES = "V";
    private static final String EXPRESSION_TYPE_FIELDS_EQUAL_VALUES = "E";

    /* Various components */
    private static final String OPEN_PARENTHESES = "(";
    private static final String CLOSED_PARENTHESES = ")";
    private static final String EQUALS_SIGN = "=";
    private static final String EXPRESSION_SEPARATOR = ", ";
    private static final String WHERE_CLAUSE_AND_SEPARATOR = " AND ";
    private static final String SQL_QUOTE = "\"";
    private static final String SQL_NULL = "NULL";


    /* The table addressed by the SQL statements */
    private String tableName;
    /* List of field names toghether with their own values */
    private HashMap<String, Object> fieldValues;
    /* Key field status */
    private HashMap<String, Boolean> keyFieldFlags;
    /* List of supported classes for field values */
    private List<Class> supportedClassList;

    /**
     * Instantiates a new SQLCudQueryBuilder
     *
     * @param tableName The name of the table
     */
    public SQLCudQueryBuilder(String tableName) {
        this.tableName = tableName;
        this.fieldValues = new HashMap<String, Object>();
        this.keyFieldFlags = new HashMap<String, Boolean>();

        supportedClassList = new ArrayList<Class>();
        supportedClassList.add(String.class);
        supportedClassList.add(Integer.class);
        supportedClassList.add(Short.class);
        supportedClassList.add(Long.class);
        supportedClassList.add(Byte.class);
        supportedClassList.add(Float.class);
        supportedClassList.add(Double.class);
        supportedClassList.add(BigInteger.class);
        supportedClassList.add(BigDecimal.class);
        supportedClassList.add(Boolean.class);
        supportedClassList.add(Character.class); // Aggiunta 19/04/2017

    }

    /**
     * Adds a table field toghether with its own value
     *
     * @param fieldName      The name of the field being added
     * @param fieldValue     The value of the field; it must be the instance of a class or null, not a primitive type.
     * @param isCrudKeyField Tells the Query Builder that the field is a key field for this specific operation; key fields are in the where clause of update and delete statements and their values are used only in insert statements.
     * @throws ClassNotSupportedException
     */
    public void put(String fieldName, Object fieldValue, boolean isCrudKeyField) throws ClassNotSupportedException {
        if (fieldValue != null) {
            Class cx = fieldValue.getClass();
            if (!supportedClassList.contains(cx)) {
                throw new ClassNotSupportedException("Class " + cx.getCanonicalName() + " not supported");
            }
            this.fieldValues.put(fieldName, cx.cast(fieldValue));
        } else {
            this.fieldValues.put(fieldName, fieldValue); // c è nullo
        }
        this.keyFieldFlags.put(fieldName, isCrudKeyField);
    }

    /**
     * Adds a table non-key field toghether with its own value
     *
     * @param fieldName  The name of the field being added
     * @param fieldValue The value of the field; it must be a class (one that is supported) or null
     * @throws ClassNotSupportedException
     */
    public void put(String fieldName, Object fieldValue) throws ClassNotSupportedException {
        put(fieldName, fieldValue, false);
    }

    /**
     * Retrieves the current value for the field
     *
     * @param fieldName The name of the field whose value is being retrieved
     * @return The value of the field
     */
    public Object get(String fieldName) {
        Object r = fieldValues.get(fieldName);
        Class cx = r.getClass();
        return cx.cast(r);
    }

    /**
     * Internal function used to contruct values for query construction
     *
     * @param fieldObject
     * @return The value formatted for query construction
     */
    private String getFieldValue(Object fieldObject) {
        String fieldVal;
        if (fieldObject != null) {
            fieldVal = fieldObject.toString();
            if ((fieldObject instanceof String) || (fieldObject instanceof Boolean)) {
                fieldVal = SQL_QUOTE + fieldVal.trim() + SQL_QUOTE;
            }
        } else {
            fieldVal = SQL_NULL;
        }
        return fieldVal;
    }

    /**
     * Gets the insert query statement for the current table and field/value list
     *
     * @return The query statement
     */
    public String getInsertQuery() {
        String q;
        String fields = "(" + getFieldExpressionList(KEY_VALUES_BOTH, EXPRESSION_TYPE_FIELDS, EXPRESSION_SEPARATOR, false) + ")";
        String values = "(" + getFieldExpressionList(KEY_VALUES_BOTH, EXPRESSION_TYPE_VALUES, EXPRESSION_SEPARATOR, false) + ")";
        q = "INSERT INTO " + tableName.trim() + fields.trim() + " VALUES" + values.trim() + ";";
        return q;
    }

    /**
     * Same as getUpdateQuery(String whereClause) but using the where clause built automatically
     *
     * @return
     */
    public String getUpdateQuery() {
        return getUpdateQuery(null);
    }

    /**
     * Gets the update query statement for the current table and field/value list; the non-key fields are the ones being modified
     * while key fields are used in the where clause
     *
     * @param whereClause The where clause to use instead of the one that would be otherwise built automatically
     * @return The query statement
     */
    public String getUpdateQuery(String whereClause) {
        String q;
        String set = "SET " + getFieldExpressionList(KEY_VALUES_NON_KEY, EXPRESSION_TYPE_FIELDS_EQUAL_VALUES, EXPRESSION_SEPARATOR, false);
        String where;
        if (whereClause != null) {
            where = whereClause;
        } else {
            where = getWhereClause();
        }
        q = "UPDATE " + tableName.trim() + " " + set + " " + where + ";";
        return q;
    }

    /**
     * Same as getDeleteQuery(String whereClause) but using the where clause built automatically
     *
     * @return
     */
    public String getDeleteQuery() {
        return getDeleteQuery(null);
    }

    /**
     * Gets the delete query statement for the current table and field/value list; the key fields are used in the where clause
     *
     * @param whereClause The where clause to use instead of the one that would be otherwise built automatically
     * @return The query statement
     */
    public String getDeleteQuery(String whereClause) {
        String q;
        String where;
        if (whereClause != null) {
            where = whereClause;
        } else {
            where = getWhereClause();
        }
        q = "DELETE FROM " + tableName.trim() + " " + where + ";";
        return q;
    }

    /**
     * Gets the where clause in update and delete statements
     *
     * @return
     */
    public String getWhereClause() {
        String where = "WHERE " + getFieldExpressionList(KEY_VALUES_KEYS, EXPRESSION_TYPE_FIELDS_EQUAL_VALUES, WHERE_CLAUSE_AND_SEPARATOR, true);
        return where;
    }

    /**
     * Gets the field expression list used
     *
     * @param keyValues               Allows to select key fields only, non-key fields only or both
     * @param expressionType          Allows selection of the expression type (values list, field name list, field=value)
     * @param expressionSeparator     The separator being used (e.g. you can specify " AND " for where clauses)
     *                                Please note that is the caller's responsibility to insert proper spaces before and/or after the separator
     * @param expressionInParentheses If true puts the resulting expression in parentheses
     * @return The expression list required
     */
    private String getFieldExpressionList(String keyValues, String expressionType, String expressionSeparator, boolean expressionInParentheses) {

        String expressionList = "";

        String fieldName = null;
        String fieldValue = null;
        String expr = null;
        boolean isKeyField = false;

        for (String f : this.fieldValues.keySet()) {
            fieldName = f;
            fieldValue = getFieldValue(this.fieldValues.get(f));
            isKeyField = this.keyFieldFlags.get(f);
            if ((keyValues.equals(KEY_VALUES_BOTH))
                    ||
                    (keyValues.equals(KEY_VALUES_KEYS) && (isKeyField))
                    ||
                    (keyValues.equals(KEY_VALUES_NON_KEY) && (!isKeyField))
                    ) {
                switch (expressionType) {
                    case EXPRESSION_TYPE_FIELDS:
                        expr = fieldName;
                        break;
                    case EXPRESSION_TYPE_VALUES:
                        expr = fieldValue;
                        break;
                    case EXPRESSION_TYPE_FIELDS_EQUAL_VALUES:
                        expr = fieldName + EQUALS_SIGN + fieldValue;
                        break;
                }
                if (expressionList != "") {
                    expressionList += expressionSeparator;
                }
                if (expressionInParentheses) {
                    expr = OPEN_PARENTHESES + expr + CLOSED_PARENTHESES;
                }
                expressionList += expr;
            }
        }
        return expressionList;
    }

    /**
     * Provides a readable representation of inner data
     *
     * @return The String representation
     */
    @Override
    public String toString() {
        final String key_fmt = "%-25s";
        final String val_fmt = "%-60s";
        final String type_fmt = "%-25s";
        final String sep = "|";
        String keyv;
        String valv;
        String typev;
        String s = "";
        keyv = String.format(key_fmt, "Field");
        valv = String.format(val_fmt, "Value");
        typev = String.format(type_fmt, "Class");
        s += keyv + sep + valv + sep + typev + "\n";
        for (String k : fieldValues.keySet()) {
            keyv = k.trim();
            Object val = fieldValues.get(k);
            if (val != null) {
                valv = val.toString();
                typev = val.getClass().toString();
            } else {
                valv = SQL_NULL;
                typev = "null";
            }
            keyv = String.format(key_fmt, keyv);
            valv = String.format(val_fmt, valv);
            typev = String.format(type_fmt, typev);
            s += keyv + sep + valv + sep + typev + "\n";
        }
        return s;
    }

    /**
     * Gets the name of the table
     *
     * @return The table name
     */
    public String getTableName() {
        return tableName;
    }

    /**
     * Sets the name of the table
     *
     * @param tableName The table name
     */
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /* Exception thrown when an unsupported class is used for a field value */
    public class ClassNotSupportedException extends Exception {
        public ClassNotSupportedException(String message) {
            super(message);
        }
    }


}