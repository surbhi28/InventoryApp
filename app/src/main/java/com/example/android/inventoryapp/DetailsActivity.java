package com.example.android.inventoryapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.android.inventoryapp.Data.DbHelper;
import com.example.android.inventoryapp.Data.InvContract.InvEntry;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class DetailsActivity extends AppCompatActivity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final int PICK_IMAGE_REQUEST = 0;
    Cursor cursor;
    EditText productName;
    EditText productPrice;
    EditText productQuantity;
    ImageView productPhoto;
    Button changePhoto;
    Button decreaseQuantity;
    Button increaseQuantity;
    Button orderSupplier;
    Button updateProduct;
    Button deleteProduct;
    DbHelper dbHelper;
    private boolean mInventoryHasChanged = false;
    private String orderName;
    private Uri mUri;
    private String currentPhotoUri = "no images";

    /**
     * OnTouchListener that listens for any user touches on a View, implying that they are modifying
     * the view, and we change the mInventoryHasChanged boolean to true.
     */
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Uri uri = getIntent().getData();
        String stringId = uri.toString();
        final Long id = Long.parseLong(stringId);

        dbHelper = new DbHelper(this);
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String[] projection = {
                InvEntry._ID,
                InvEntry.COLUMN_PRODUCT_NAME,
                InvEntry.COLUMN_PRODUCT_PRICE,
                InvEntry.COLUMN_PRODUCT_QUANTITY,
                InvEntry.COLUMN_PRODUCT_IMAGE
        };
        String selection = InvEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(id)};

        cursor = database.query(InvEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null);

        productName = (EditText) findViewById(R.id.name);
        productPrice = (EditText) findViewById(R.id.price);
        productQuantity = (EditText) findViewById(R.id.quantity);
        productPhoto = (ImageView) findViewById(R.id.image_product_photo);
        changePhoto = (Button) findViewById(R.id.change_photo_button);
        decreaseQuantity = (Button) findViewById(R.id.dec);
        increaseQuantity = (Button) findViewById(R.id.inc);
        orderSupplier = (Button) findViewById(R.id.order_supplier_button);
        updateProduct = (Button) findViewById(R.id.save_product_button);
        deleteProduct = (Button) findViewById(R.id.delete_product_button);


        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_QUANTITY);
            int imageColumnIndex = cursor.getColumnIndex(InvEntry.COLUMN_PRODUCT_IMAGE);


            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);

            String photo = cursor.getString(imageColumnIndex);

            // Update the views on the screen with the values from the database
            productName.setText(name);
            productPrice.setText(String.valueOf(price));
            productQuantity.setText(String.valueOf(quantity));
            orderName = name;

            if (photo == null) {
                productPhoto.setImageResource(R.mipmap.ic_launcher);
            } else {
                mUri = Uri.parse(photo);

                Log.i(LOG_TAG, "Uri: " + mUri);

                ViewTreeObserver viewTreeObserver = productPhoto.getViewTreeObserver();
                viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                    @Override
                    public void onGlobalLayout() {
                        productPhoto.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        productPhoto.setImageBitmap(getBitmapFromUri(mUri));
                    }
                });
            }
            //monitor activity so we can protect user
            productName.setOnTouchListener(mTouchListener);
            productPrice.setOnTouchListener(mTouchListener);
            productQuantity.setOnTouchListener(mTouchListener);
            productPhoto.setOnTouchListener(mTouchListener);

        }

        changePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openImageSelector();
            }
        });

        decreaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(productQuantity.getText().toString().trim());
                if (quantity == 0) {
                    Toast.makeText(getApplicationContext(), "Can't Decrease the quantity", Toast.LENGTH_SHORT).show();
                } else {
                    quantity = quantity - 1;
                    productQuantity.setText(String.valueOf(quantity));
                }
            }
        });

        increaseQuantity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.parseInt(productQuantity.getText().toString().trim());
                quantity = quantity + 1;
                productQuantity.setText(String.valueOf(quantity));

            }
        });

        orderSupplier.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "Order for " + orderName);

                try {
                    startActivity(Intent.createChooser(intent, "Send mail..."));
                } catch (android.content.ActivityNotFoundException ex) {
                    ex.printStackTrace();
                }

            }
        });

        updateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveProduct(id);
                finish();

            }
        });

        deleteProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(id);


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

                currentPhotoUri = mUri.toString();

                productPhoto.setImageBitmap(getBitmapFromUri(mUri));
            }
        }
    }

    public Bitmap getBitmapFromUri(Uri uri) {

        if (uri == null || uri.toString().isEmpty())
            return null;

        // Get the dimensions of the View
        int targetW = productPhoto.getWidth();
        int targetH = productPhoto.getHeight();

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
    public void onBackPressed() {
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
                // and continue editing the inventory.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private int saveProduct(long productNo) {
        //Read values from text fields
        String name = productName.getText().toString().trim();
        String priceString = productPrice.getText().toString().trim();
        String quantityString = productQuantity.getText().toString().trim();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(quantityString) && currentPhotoUri.equals("no images")) {
            return 0;
        }
        Integer price = Integer.parseInt(priceString);
        Integer quantity = Integer.parseInt(quantityString);

        ContentValues values = new ContentValues();
        values.put(InvEntry.COLUMN_PRODUCT_NAME, name);
        values.put(InvEntry.COLUMN_PRODUCT_PRICE, price);
        values.put(InvEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        values.put(InvEntry.COLUMN_PRODUCT_IMAGE, currentPhotoUri);

        if (name == null) {
            throw new IllegalArgumentException("Product requires a name");
        }
        if (price != null && price < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }
        if (quantity != null && quantity < 0) {
            throw new IllegalArgumentException("Product requires a valid quantity");
        }
        if (currentPhotoUri == null) {
            throw new IllegalArgumentException("Product requires a photo");
        }
        if (values.size() == 0) {
            return 0;
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();
        String selection = InvEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(productNo)};
        int rowsAffected = database.update(InvEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsAffected != 0) {
            Toast.makeText(this, "Product Updated", Toast.LENGTH_SHORT).show();
        } else
            Toast.makeText(this, "Error in updating Product", Toast.LENGTH_SHORT).show();

        return rowsAffected;

    }

    /**
     * Prompt the user to confirm that they want to delete this product.
     */
    private void showDeleteConfirmationDialog(final long productToDelete) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(
                R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User clicked the "Delete" button, so delete the product.
                        deleteThisProduct(productToDelete);
                        finish();
                    }
                });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                // and continue editing the pet.
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteThisProduct(long productNo) {

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        String selection = InvEntry._ID + "=?";
        String[] selectionArgs = new String[]{String.valueOf(productNo)};
        int rowsDeleted = database.delete(InvEntry.TABLE_NAME, selection, selectionArgs);

        if (rowsDeleted == 0) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                    Toast.LENGTH_SHORT).show();
        }

    }

}