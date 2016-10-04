package com.example.zz3430gs.hellosqlite;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

/**
 * Created by zz3430gs on 10/4/16.
 */
public class ProductListAdapter extends CursorAdapter{

    Context context;

    private static int NAME_COL = 1;
    private static int QUANTITY_COL = 2;

    public ProductListAdapter(Context context, Cursor c, boolean autoRequery) {
        super(context, c, autoRequery);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item, parent, false);
        return v;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        //TODO Put data from this cursor (represents one row of the database) into this view (the corresponding row in the list)

        TextView productListName = (TextView) view.findViewById(R.id.product_list_name);
        TextView productListQuantity = (TextView) view.findViewById(R.id.product_list_quantity);
        productListName.setText(cursor.getString(NAME_COL));
        productListQuantity.setText(cursor.getString(QUANTITY_COL));
    }
}
