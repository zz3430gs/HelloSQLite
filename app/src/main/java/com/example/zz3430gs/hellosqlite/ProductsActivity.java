package com.example.zz3430gs.hellosqlite;

import android.content.DialogInterface;
import android.database.Cursor;
import android.os.DropBoxManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ProductsActivity extends AppCompatActivity {

    EditText productNameET;
    EditText productQuantityET;
    EditText searchNameET;
    EditText updateProductQuantityET;

    ListView allProductsListView;

    ProductListAdapter allProductListAdapter;
    Cursor allProductsCursor;

    Button addProductButton;
    Button searchProductsButton;
    Button updateQuantityButton;

    private DatabaseManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);


        dbManager = new DatabaseManager(this);

        productNameET = (EditText)findViewById(R.id.add_new_product_name_et);
        productQuantityET = (EditText)findViewById(R.id.add_new_product_quantity_et);
        searchNameET = (EditText)findViewById(R.id.search_et);
        updateProductQuantityET = (EditText)findViewById(R.id.update_quantity_et);

        addProductButton = (Button)findViewById(R.id.add_product_button);
        searchProductsButton = (Button)findViewById(R.id.search_products_button);
        updateQuantityButton = (Button)findViewById(R.id.update_quantity_button);

        allProductsListView = (ListView)findViewById(R.id.all_products_listview);
        allProductsCursor = dbManager.getCursorAll();
        allProductListAdapter = new ProductListAdapter(this, allProductsCursor, false);
        allProductsListView.setAdapter(allProductListAdapter);


        //Adds a product to the database
        addProductButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                String newName = productNameET.getText().toString();
                String newQuantity = productQuantityET.getText().toString();

                //If there is nothing typed, a message shows and asks for a product and amount
                if ( newName.length() == 0  || !newQuantity.matches("^\\d+$")) {   //regex validation
                    Toast.makeText(ProductsActivity.this, "Please enter a product name and numerical quantity",
                            Toast.LENGTH_LONG).show();
                    return;
                }


                //Add the amount to the database and name
                int quantity = Integer.parseInt(newQuantity);

                if (dbManager.addProduct(newName, quantity)){
                    Toast.makeText(ProductsActivity.this, "Product added to the database", Toast.LENGTH_LONG).show();

                    productNameET.getText().clear();
                    productQuantityET.getText().clear();
                    allProductListAdapter.changeCursor(dbManager.getCursorAll());
                }
                //If the product is there already then message will show that there is already a product in the database
                else {
                    Toast.makeText(ProductsActivity.this, newName + " is already in the database", Toast.LENGTH_LONG).show();
                }


            }
        });


        //Searches the database for a product that the user want to search for
        searchProductsButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //Get the name
                String searchName = searchNameET.getText().toString();
                //If nothing typed, ask to enter a product name
                if ( searchName.equals("")) {
                    Toast.makeText(ProductsActivity.this, "Please enter a product to search for",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                int quantity = dbManager.getQuantityForProduct(searchName);

                //If the product is not there, message shows that there is no such product
                if (quantity == -1){
                    Toast.makeText(ProductsActivity.this, "Product " + searchName + " not found", Toast.LENGTH_LONG).show();
                }
                //If product is there, then you can update the amount of product that is there
                else {
                    updateProductQuantityET.setText(Integer.toString(quantity));
                }
            }
        });

        //Update the amount of product that is there
        updateQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Ensure a product is selected and new quantity provided

                int newQuantity = Integer.parseInt(updateProductQuantityET.getText().toString());
                String productName = searchNameET.getText().toString();

                //If the update is successful, message will say amount was updated
                if (dbManager.updateQuantity(productName, newQuantity)){
                    Toast.makeText(ProductsActivity.this, "Quantity updated", Toast.LENGTH_LONG).show();
                    allProductListAdapter.changeCursor(dbManager.getCursorAll());
                }else {
                    Toast.makeText(ProductsActivity.this, "Product not found in database", Toast.LENGTH_LONG).show();
                }
            }
        });


        //ListView's OnItemLongClickListener to delete product.
        //TODO remember to configure the list view! This template app will crash on this line since allProductsListView is null.
        allProductsListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            //The last argument is the value from the database _id column, provided by the ProductListAdapter
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, final long id) {

                //TODO Show confirmation dialog. If user clicks OK, then delete item

                Cursor cursor = (Cursor)allProductListAdapter.getItem(position);
                String name = cursor.getString(1);

                new AlertDialog.Builder(ProductsActivity.this)
                        .setTitle("Delete")
                        .setMessage("Delete " + name + "?")
                        .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                dbManager.deleteProduct(id);
                                Toast.makeText(ProductsActivity.this, "Product deleted", Toast.LENGTH_LONG).show();
                                allProductListAdapter.changeCursor(dbManager.getCursorAll());
                            }
                        }).setNegativeButton(android.R.string.cancel, null)
                        .create().show();

                // We can delete by id, no problem, so could simply call dbManager.deleteProduct(id)
                // In this case, we'd like to show a confirmation dialog
                // with the name of the product, so need to get some data about this list item
                // Want the data? Need to call getItem to get the Cursor for this row

                return false;
            }
        });
    }

    @Override
    protected void onPause(){
        super.onPause();
        dbManager.close();
    }

    @Override
    protected void onResume(){
        super.onResume();
        dbManager = new DatabaseManager(this);
    }


}
