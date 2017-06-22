package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.DbHelper;
import com.example.android.inventoryapp.Data.InvContract.InvEntry;

/**
 * Created by nalin on 16-Jun-17.
 */

public class AddActivity extends AppCompatActivity {

    private EditText name;
    private EditText price;
    private EditText quantity;
    InvCursorAdapter mCursorAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

         name = (EditText)findViewById(R.id.name_button);
         price = (EditText)findViewById(R.id.price_button);
         quantity = (EditText)findViewById(R.id.quantity_button);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    public void saveProduct(){
        String nameString = name.getText().toString().trim();
        String priceString = price.getText().toString().trim();
        int price = Integer.parseInt(priceString);
        String quantityString = quantity.getText().toString().trim();
        int quantity = Integer.parseInt(quantityString);

        ContentValues values = new ContentValues();
        values.put(InvEntry.COLUMN_PRODUCT_NAME,nameString);
        values.put(InvEntry.COLUMN_PRODUCT_PRICE,price);
        values.put(InvEntry.COLUMN_PRODUCT_QUANTITY,quantity);

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long newRowId = db.insert(InvEntry.TABLE_NAME,null,values);

        if( newRowId == -1) {
            Toast.makeText(this, "Product Not Saved", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(this, "Product Save", Toast.LENGTH_SHORT).show();

        }

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
               saveProduct();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
               // showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // which is the {@link MainActivity}.
                    NavUtils.navigateUpFromSameTask(AddActivity.this);
                    return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
