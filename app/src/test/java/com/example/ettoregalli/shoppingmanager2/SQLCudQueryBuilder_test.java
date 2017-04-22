package com.example.ettoregalli.shoppingmanager2;

/**
 * Created by ettoregalli on 11/04/17.
 */

import com.example.ettoregalli.shoppingmanager2.database.sqliteutilities.SQLCudQueryBuilder;

import org.junit.Test;

public class SQLCudQueryBuilder_test {
    @Test
    public void SQLCudQueryBuilder_base_test() throws Exception {
        //SQLCudQueryBuilder scb = new SQLCudQueryBuilder("tabella");

        SQLCudQueryBuilder scb = new SQLCudQueryBuilder("tabella");
        scb.put("chiave1", "aaa", true);
        scb.put("chiave2", "bbb", true);
        scb.put("campo_alfa", "stringa");
        scb.put("campo_int", (int) 3);
        scb.put("campo_double", (new Double("3.1415")));
        scb.put("campo_null", null);

        System.out.println(scb.getInsertQuery());
        System.out.println(scb.getUpdateQuery());
        System.out.println(scb.getDeleteQuery());
        System.out.println(scb.getDeleteQuery("WHERE exists(SELECT lalla from qqq WHERE xyz=4)"));
        System.out.println(scb.getWhereClause());


        SQLCudQueryBuilder scb2 = new SQLCudQueryBuilder("tabella2");
        scb2.put("chiave1", "aaa2", true);
        scb2.put("chiave2", "bbb2", true);
        System.out.println(scb2.getDeleteQuery());
    }

}
