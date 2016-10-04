package com.example.zz3430gs.hellosqlite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseManager {

    private Context context;
    private SQLHelper helper;
    private SQLiteDatabase db;
    protected static final String DB_NAME = "products.db";

    protected static final int DB_VERSION = 1;
    protected static final String DB_TABLE = "inventory";

    private static final String INT_COL = "_id";
    protected static final String NAME_COL = "product_name";
    protected static final String QUANTITY_COL = "quantity";

    private static final String DB_TAG = "DatabaseManager" ;
    private static final String SQL_TAG = "SQLHelper" ;

    public DatabaseManager(Context c) {
        this.context = c;
        helper = new SQLHelper(c);
        this.db = helper.getWritableDatabase();
    }

    public void close() {
        helper.close(); //Closes the database - very important!
    }


    //TODO add method to fetch all data and return a Cursor
    //TODO add method to select (search) for a product by name
    //TODO add method to insert (add) a product and quantity
    //TODO add method to delete a product
    //TODO add method to update (change) the quantity of a product

    public boolean updateQuantity(String name, int newQuantity){
        ContentValues updateProduct = new ContentValues();
        updateProduct.put(QUANTITY_COL, newQuantity);
        String[] whereArgs = { name };
        String where = NAME_COL + " = ?";

        int rowsChanged = db.update(DB_TABLE, updateProduct, where, whereArgs);

        Log.i(DB_TAG, "Update " + name + " new quantity " + newQuantity);

        if ( rowsChanged > 0){
            return true;
        }
        return false;
    }

    public int getQuantityForProduct(String productName){
        String[] cols = { QUANTITY_COL };

        String selection = NAME_COL + " =? ";
        String[] selectionArgs = { productName };

        Cursor cursor = db.query(DB_TABLE, cols, selection, selectionArgs, null, null, null);

        if (cursor.getCount() == 1){
            cursor.moveToFirst();
            int quantity = cursor.getInt(0);
            cursor.close();
            return quantity;
        }
        else {
            return -1;  //todo -- better way to indicate not found?
        }
    }


    public class SQLHelper extends SQLiteOpenHelper {
        public SQLHelper(Context c){
            super(c, DB_NAME, null, DB_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            //Table contains a primary key column, _id which autoincrements - saves you setting the value
            //Having a primary key column is almost always a good idea. In this app, the _id column is used by
            //the list CursorAdapter data source to figure out what to put in the list, and to uniquely identify each element
            //Name column, String
            //Quantity column, int

            String createTable = "CREATE TABLE " + DB_TABLE + " (" + INT_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, " +  NAME_COL +" TEXT UNIQUE, " + QUANTITY_COL +" INTEGER);"  ;
            Log.d(SQL_TAG, createTable);
            db.execSQL(createTable);
        }


        //If the table already exists, delete it and recreate it with possible new data
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE);
            onCreate(db);
            Log.w(SQL_TAG, "Upgrade table - drop and recreate it");
        }
    }

    public Cursor getCursorAll(){
        Cursor cursor = db.query(DB_TABLE, null, null, null, null, null, NAME_COL);
        return cursor;
    }


    //Adds a new product to the database
    public boolean addProduct(String name, int quantity){
        ContentValues newProduct = new ContentValues();
        newProduct.put(NAME_COL, name);
        newProduct.put(QUANTITY_COL, quantity);
        try{
            db.insertOrThrow(DB_TABLE, null, newProduct);
            return true;
        }catch (SQLiteConstraintException sqlce){
            Log.e(DB_TAG, "error inserting data into table. " + "Name:" + name + " quantity:" + quantity, sqlce);
            return false;
        }

    }


    //When long press a message will say do you want to delete and if click ok the product gets deleted
    public boolean deleteProduct(long productId){
        String[] whereArgs = {Long.toString(productId)};
        String where = "_id = ?";
        int rowsDeleted = db.delete(DB_TABLE, where, whereArgs);

        Log.i(DB_TAG, "Delete " + productId + " rows deleted:" + rowsDeleted);

        if (rowsDeleted == 1){
            return true;
        }
        return false;
    }

}
