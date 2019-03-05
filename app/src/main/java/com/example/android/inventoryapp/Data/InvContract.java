package com.example.android.inventoryapp.Data;

import android.provider.BaseColumns;

/**
 * Created by nalin on 16-Jun-17.
 */

public class InvContract {

    private InvContract(){}

    public static abstract class InvEntry implements BaseColumns{

        public static final String TABLE_NAME = "inventory";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_IMAGE = "image";
        public static final String COLUMN_PRODUCT_SUPPLIER = "supplier";
    }
}
