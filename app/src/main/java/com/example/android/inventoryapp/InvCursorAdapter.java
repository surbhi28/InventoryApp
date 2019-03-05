package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.DbHelper;
import com.example.android.inventoryapp.Data.InvContract.InvEntry;

/**
 * Created by nalin on 17-Jun-17.
 */

public class InvCursorAdapter extends CursorAdapter {


    public InvCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // this method and return the list item view (instead of null)
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);

    }

    /**
     * This method binds the pet data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current pet can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        // Find fields to populate in inflated template
        TextView nameView = (TextView) view.findViewById(R.id.product_name);
        TextView priceView = (TextView) view.findViewById(R.id.product_price);
        final TextView quantityView = (TextView) view.findViewById(R.id.product_quantity);

        // Extract properties from cursor
        // Find the columns of inventory attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PRICE);
        int quantityIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_QUANTITY);
        int rowIndex = cursor.getColumnIndex(InvEntry._ID);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        int productPrice = cursor.getInt(priceColumnIndex);
        int productQuantity = cursor.getInt(quantityIndex);
        final int rowId = cursor.getInt(rowIndex);

        // Populate fields with extracted properties
        nameView.setText(productName);
        priceView.setText(Integer.toString(productPrice));
        quantityView.setText(Integer.toString(productQuantity));

        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                DbHelper dbHelper = new DbHelper(context);
                SQLiteDatabase database = dbHelper.getWritableDatabase();

                int items = Integer.parseInt(quantityView.getText().toString());
                if (items > 0) {
                    int mQuantitySold = items - 1;
                    ContentValues values = new ContentValues();
                    values.put(InvEntry.COLUMN_PRODUCT_QUANTITY, mQuantitySold);
                    String selection = InvEntry._ID + "=?";
                    String[] selectionArgs = new String[]{String.valueOf(rowId)};
                    int rowsAffected = database.update(InvEntry.TABLE_NAME, values, selection, selectionArgs);
                    if (rowsAffected != -1) {
                        quantityView.setText(Integer.toString(mQuantitySold));
                    }
                } else
                    Toast.makeText(context, "No Stock Left ", Toast.LENGTH_SHORT).show();
            }
        });

    }

}


