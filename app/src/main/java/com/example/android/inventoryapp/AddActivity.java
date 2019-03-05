package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.DbHelper;
import com.example.android.inventoryapp.Data.InvContract.InvEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by nalin on 16-Jun-17.
 */

public class AddActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 0;
    private EditText name;
    private EditText price;
    private EditText quantity;
    private ImageView photo;
    private EditText supplier;
    private Uri mUri;
    private String currentPhotoPath = "no images";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add);

        name = (EditText) findViewById(R.id.name_button);
        price = (EditText) findViewById(R.id.price_button);
        quantity = (EditText) findViewById(R.id.quantity_button);
        photo = (ImageView) findViewById(R.id.image_add_product_photo);
        supplier = (EditText) findViewById(R.id.supplier_button);

        photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageSelector();

            }
        });
    }

    public void openImageSelector() {
        Intent intent;

        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
        } else {
            intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        }

        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData) {
        // The ACTION_OPEN_DOCUMENT intent was sent with the request code READ_REQUEST_CODE.
        // If the request code seen here doesn't match, it's the response to some other intent,
        // and the below code shouldn't run at all.

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.  Pull that uri using "resultData.getData()"

            if (resultData != null) {
                mUri = resultData.getData();
                Log.i(LOG_TAG, "Uri: " + mUri.toString());

                currentPhotoPath = mUri.toString();

                photo.setImageBitmap(getBitmapFromUri(mUri));

            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = photo.getWidth();
        int targetH = photo.getHeight();

        InputStream input = null;
        try {
            input = this.getContentResolver().openInputStream(uri);

            // Get the dimensions of the bitmap
            BitmapFactory.Options bmOptions = new BitmapFactory.Options();
            bmOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();

            int photoW = bmOptions.outWidth;
            int photoH = bmOptions.outHeight;

            // Determine how much to scale down the image
            int scaleFactor = Math.min(photoW / targetW, photoH / targetH);

            // Decode the image file into a Bitmap sized to fill the View
            bmOptions.inJustDecodeBounds = false;
            bmOptions.inSampleSize = scaleFactor;
            bmOptions.inPurgeable = true;

            input = this.getContentResolver().openInputStream(uri);
            Bitmap bitmap = BitmapFactory.decodeStream(input, null, bmOptions);
            input.close();
            return bitmap;

        } catch (FileNotFoundException fne) {
            Log.e(LOG_TAG, "Failed to load image.", fne);
            return null;
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to load image.", e);
            return null;
        } finally {
            try {
                input.close();
            } catch (IOException ioe) {

            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_add.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_add, menu);
        return true;
    }

    public void saveProduct() {
        String nameString = name.getText().toString().trim();
        String priceString = price.getText().toString().trim();
        String quantityString = quantity.getText().toString().trim();
        String supplierString = supplier.getText().toString().trim();

        if (TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString)
                && TextUtils.isEmpty(quantityString) && currentPhotoPath.equals("no images")
                && TextUtils.isEmpty(supplierString)) {
            return;
        }

        if (TextUtils.isEmpty(nameString)) {
            Toast.makeText(this, "Product requires a name", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(priceString)) {
            Toast.makeText(this, "Product requires a price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(quantityString)) {
            Toast.makeText(this, "Product requires a quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        if ((currentPhotoPath == "no images") || TextUtils.isEmpty(currentPhotoPath)) {
            Toast.makeText(this, "Product requires a photo", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(supplierString)) {
            Toast.makeText(this, "Product requires a supplier", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer price = Integer.parseInt(priceString);
        Integer quantity = Integer.parseInt(quantityString);

        ContentValues values = new ContentValues();
        values.put(InvEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InvEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(InvEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InvEntry.COLUMN_PRODUCT_IMAGE, currentPhotoPath);
        values.put(InvEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);

        if (price != null && price < 0) {
            Toast.makeText(this, "Product requires a valid price", Toast.LENGTH_SHORT).show();
            return;
        }
        if (quantity != null && quantity < 0) {
            Toast.makeText(this, "Product requires a valid quantity", Toast.LENGTH_SHORT).show();
            return;
        }
        if (values.size() == 0) {
            return;
        }

        DbHelper dbHelper = new DbHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long newRowId = db.insert(InvEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Product Not Saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product Save", Toast.LENGTH_SHORT).show();

        }
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                saveProduct();
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
