package com.example.pharmaswift.navigation_bar;

import static android.app.DownloadManager.COLUMN_ID;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "PharmaSwift.db";
    private static final String DB_PATH = "/data/data/com.PharmaSwift/databases/";
    private static final int DATABASE_VERSION = 2;

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_PRODUCT_NAME = "product_name";
    private static final String COLUMN_PRICE = "price";
    private static final String COLUMN_TIMESTAMP = "timestamp";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DATABASE_VERSION);
    }



    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create userBalance table
        String userBalance = "CREATE TABLE IF NOT EXISTS userBalance (" +
                "balance DOUBLE, " +
                "points DOUBLE)";
        db.execSQL(userBalance);

        // Create transaction table with backticks around 'transaction'
        String CREATE_TRANSACTION_TABLE = "CREATE TABLE IF NOT EXISTS `transaction` ("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COLUMN_PRODUCT_NAME + " TEXT, "
                + COLUMN_PRICE + " DOUBLE, "
                + COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP"
                + ")";
        db.execSQL(CREATE_TRANSACTION_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }


    public void insertBalance(double balance, double points) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", balance);
        values.put("points", points);
        db.insert("userBalance", null, values);
        db.close();
    }

    public double[] getBalance() {
        try (SQLiteDatabase db = this.getReadableDatabase();
             Cursor cursor = db.rawQuery("SELECT balance, points FROM userBalance LIMIT 1", null)) {

            if (cursor.moveToFirst()) {
                return new double[]{cursor.getDouble(0), cursor.getDouble(1)};
            } else {
                return new double[]{0.0, 0.0}; // Default if nothing is found
            }
        }

    }public boolean updateBalance(double newBalance, double newPoints) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("balance", newBalance);
        values.put("points", newPoints);

        // Assuming you have only one user row (id = 1), or no WHERE clause if it's just one row
        int rowsAffected = db.update("userBalance", values, null, null);

        return rowsAffected > 0;
    }


    public void insertTransaction(String productName, double price) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_PRODUCT_NAME, productName);
        values.put(COLUMN_PRICE, price);

        // Insert row
        long result = db.insert("`transaction`", null, values);
        if (result != -1) {
            Log.d("DatabaseHelper", "Transaction added successfully");
        } else {
            Log.e("DatabaseHelper", "Failed to add transaction");
        }
        db.close();
    }

    // Fetch all transactions (if needed for debugging)
    public ArrayList<Model> getAllTransactions() {
        ArrayList<Model> transactions = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String query = "SELECT * FROM `transaction`";  // Ensure backticks are used here for 'transaction'

        Cursor cursor = db.rawQuery(query, null);
        if (cursor.moveToFirst()) {
            do {
                String productName = cursor.getString(cursor.getColumnIndex("product_name"));
                double price = cursor.getDouble(cursor.getColumnIndex("price"));
                String timestamp = cursor.getString(cursor.getColumnIndex("timestamp"));
                transactions.add(new Model(productName, price, timestamp));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transactions;
    }
    public List<Product> getProductData() {
        List<Product> products = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name, price, imageData FROM Medicines", null);

        if (cursor.moveToFirst()) {
            do {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") double price = cursor.getDouble(cursor.getColumnIndex("price"));
                @SuppressLint("Range") byte[] imageData = cursor.getBlob(cursor.getColumnIndex("imageData"));

                // Convert BLOB to Bitmap
                Bitmap bitmap = BitmapFactory.decodeByteArray(imageData, 0, imageData.length);

//                imageView.setImageBitmap(bitmap);  // Assuming imageView is a reference to your ImageView

                // You can also use the name and price as needed
                Log.d("ProductInfo", "Name: " + name + ", Price: " + price);

                Product product = new Product(name, price, bitmap);
                products.add(product);

            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return products;
    }

    @SuppressLint("ResourceType")
    public void updateDrugsImages(Context context, int drawableResId, String medicineNameKeyword) {
        SQLiteDatabase db = this.getWritableDatabase();

        try {
            Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), drawableResId);

            if (bitmap == null) {
                Log.e("DatabaseHelper", "Failed to decode resource: " + drawableResId);
                return;
            }

            byte[] imageData = bitmapToByteArray(bitmap);

            ContentValues values = new ContentValues();
            values.put("imageData", imageData);

            // Use the medicineNameKeyword instead of hardcoding "Biogesic"
            String keywordPattern = "%" + medicineNameKeyword + "%";

            // Check how many matching items exist
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM Medicines WHERE name LIKE ?", new String[]{keywordPattern});
            if (cursor.moveToFirst()) {
                int count = cursor.getInt(0);
                Log.d("DatabaseHelper", "Found " + count + " product(s) matching: " + medicineNameKeyword);
            }
            cursor.close();

            // Update matching items
            int rowsUpdated = db.update(
                    "Medicines",
                    values,
                    "name LIKE ?",
                    new String[]{keywordPattern}
            );

            Log.d("DatabaseHelper", "Updated " + rowsUpdated + " product(s) image for: " + medicineNameKeyword);
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error updating images: " + e.getMessage());
        } finally {
            db.close();
        }
    }
    // Helper method to convert Bitmap to byte[]
    private byte[] bitmapToByteArray(Bitmap bitmap) {
        java.io.ByteArrayOutputStream stream = new java.io.ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }


}

