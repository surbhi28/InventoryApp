package com.example.android.inventoryapp;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.android.inventoryapp.Data.DbHelper;
import com.example.android.inventoryapp.Data.InvContract.InvEntry;

public class MainActivity extends AppCompatActivity {

    InvCursorAdapter mCursorAdapter;

    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton addInventory = (ImageButton)findViewById(R.id.add_button);
        addInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AddActivity.class);
                startActivity(intent);
            }
        });

        displayData();

    }

    protected void onResume(){
        super.onResume();
        displayData();

    }

    private void displayData(){
        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_QUANTITY,
        };

         cursor = db.query(InvEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        ListView invListView = (ListView) findViewById(R.id.list);
        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);

        invListView.setEmptyView(emptyView);
        mCursorAdapter = new InvCursorAdapter(this, cursor);

        invListView.setAdapter(mCursorAdapter);

    }


}
