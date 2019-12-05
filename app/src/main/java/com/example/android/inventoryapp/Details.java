package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract;
import com.example.android.inventoryapp.data.InventoryHelper;
import com.example.android.inventoryapp.data.InventoryContract.InventoryItems;

public class Details extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{

    Uri cItemUri ;
    private ImageView imageView;
    private EditText nameText;
    private CustomEditText priceText;
    private TextView idView;
    private TextView quantityView;
    private EditText supNameText;
    private EditText supPhoneText;
    private EditText supEmailText;
    private Button button;
    private boolean edited = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);


        imageView = (ImageView) findViewById(R.id.image_editor_big);

        nameText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_name);
        priceText = (CustomEditText) findViewById(R.id.include).findViewById(R.id.edittext_price);

        idView = (TextView) findViewById(R.id.include).findViewById(R.id.id_textview);
        quantityView = (TextView) findViewById(R.id.include).findViewById(R.id.quantity_textview);

        supNameText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_sup_name);
        supPhoneText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_sup_phone);
        supEmailText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_sup_email);

        cItemUri = getIntent().getData();
        getSupportLoaderManager().initLoader(0, null, this);

        button = findViewById(R.id.details);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Details.this, EditorActivity.class);
                intent.setData(cItemUri);
                startActivity(intent);
            }
        });

        nameText.setEnabled(false);
        priceText.setEnabled(false);
        supNameText.setEnabled(false);
        supPhoneText.setEnabled(false);
        supEmailText.setEnabled(false);



        TextView textView6 = (TextView) findViewById(R.id.m1);
        textView6.setVisibility(View.INVISIBLE);
        TextView textView7 = (TextView) findViewById(R.id.m2);
        textView7.setVisibility(View.INVISIBLE);
        TextView textView8 = (TextView) findViewById(R.id.m3);
        textView8.setVisibility(View.INVISIBLE);
        TextView textView9 = (TextView) findViewById(R.id.m4);
        textView9.setVisibility(View.INVISIBLE);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {

                showDeleteConfirmationDialog();
                return true;

    }



    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryItems._ID,
                InventoryContract.InventoryItems.COLUMN_NAME,
                InventoryContract.InventoryItems.COLUMN_PRICE,
                InventoryContract.InventoryItems.COLUMN_QUANTITY,
                InventoryContract.InventoryItems.COLUMN_IMAGE,
                InventoryContract.InventoryItems.COLUMN_SUP_NAME,
                InventoryContract.InventoryItems.COLUMN_SUP_PHONE,
                InventoryContract.InventoryItems.COLUMN_SUP_EMAIL
        };
        return new CursorLoader(this,
                cItemUri,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data == null || data.getCount() < 1) return;
        data.moveToFirst();
        int indexID = data.getColumnIndex(InventoryItems._ID);
        int indexName = data.getColumnIndex(InventoryItems.COLUMN_NAME);
        int indexPrice = data.getColumnIndex(InventoryItems.COLUMN_PRICE);
        int indexQt = data.getColumnIndex(InventoryItems.COLUMN_QUANTITY);
        int indexIm = data.getColumnIndex(InventoryItems.COLUMN_IMAGE);
        int id = data.getInt(indexID);
        int indexSupName = data.getColumnIndex(InventoryItems.COLUMN_SUP_NAME);
        int indexSupPhone = data.getColumnIndex(InventoryItems.COLUMN_SUP_PHONE);
        int indexSupEmail = data.getColumnIndex(InventoryItems.COLUMN_SUP_EMAIL);

        String name = data.getString(indexName);
        int price = data.getInt(indexPrice);
        int quant = data.getInt(indexQt);
        byte[] imageAsByte = data.getBlob(indexIm);
        Bitmap image = InventoryHelper.getBitmap(imageAsByte);
        String supName = data.getString(indexSupName);
        String supPhone = data.getString(indexSupPhone);
        String supEmail = data.getString(indexSupEmail);

        nameText.setText("" + name);
        priceText.setText("" + price);
        idView.setText("" + id);
        quantityView.setText("" + quant);
        imageView.setImageBitmap(image);
        supNameText.setText("" + supName);
        supPhoneText.setText("" + supPhone);
        supEmailText.setText("" + supEmail);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        nameText.setText("");
        priceText.setText("" + 0);
        idView.setText("");
        quantityView.setText("");
        supNameText.setText("");
        supPhoneText.setText("");
        supEmailText.setText("");
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do you wish to delete this item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteEntry();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
    private void deleteEntry(){
        if (cItemUri != null){
            String selection = InventoryItems._ID + " = ?";
            String[] selectionArgs = {String.valueOf(ContentUris.parseId(cItemUri))};
            int rowsDeleted = getContentResolver().delete(cItemUri, selection, selectionArgs);
            if (rowsDeleted == 0) {
                Toast.makeText(this,"Error deleting item", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


    public void decrementQuantity(View v){
        if (quantityView.getText().toString().isEmpty()){
            quantityView.setText("0");
            return;
        }
        String currentValueString = quantityView.getText().toString();
        int valueInt = Integer.parseInt(currentValueString);
        if (valueInt > 0) {
            valueInt = valueInt - 1;
        }
        quantityView.setText(String.valueOf(valueInt));
        edited = true;
    }
    public void incrementQuantity(View v){
        if (quantityView.getText().toString().isEmpty()){
            quantityView.setText("1");
            return;
        }
        String currentValueString = quantityView.getText().toString();
        int valueInt = Integer.parseInt(currentValueString);
        int newValue = valueInt + 1;
        System.out.println("New Value is " + valueInt);
        quantityView.setText(String.valueOf(newValue));
        edited = true;
    }
    public void order(View v){
        if (edited){
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Some changes were detected. How do you want to proceed, before ordering a new item?");
            builder.setPositiveButton("Save changes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialog != null) {
                        if (!saveChanges()) dialog.dismiss();
                        fireOffMailIntent();
                        dialog.dismiss();
                    }
                }
            });
            builder.setNegativeButton("Don't save changes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });

            builder.create().show();
        } else {
            fireOffMailIntent();
        }

    }
    public void fireOffMailIntent(){

        String[] mail = { supEmailText.getText().toString().trim() };
        String itemName = nameText.getText().toString();
        String text = "Hello,\n I'd like to order the following:\n\n" +
                "\tname:\t" + itemName + "\n" +
                "\tquantity:\t" +quantityView.getText().toString() + "\n" +
                "\n\nSincerely," + "\n" ;
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order of " + itemName );
        emailIntent.putExtra(Intent.EXTRA_TEXT, text);
        emailIntent.putExtra(Intent.EXTRA_EMAIL, mail);
        if (emailIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(Intent.createChooser(emailIntent, "Send email"));
        } else {
            Toast.makeText(this, "No mail-app available!", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean saveChanges(){

        String currentQuantityString = quantityView.getText().toString();
        int quantityInt;
        try{
            quantityInt = Integer.parseInt(currentQuantityString);
        } catch (Exception e) {
            Toast.makeText(this, "Quantity must contain numbers", Toast.LENGTH_LONG).show();
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryItems.COLUMN_QUANTITY, quantityInt);

        if (cItemUri == null){
            Uri newUri = getContentResolver().insert(InventoryItems.CONTENT_URI, values);
            if (newUri == null){
                Toast.makeText(this, "Error saving Item", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this, "Succesfully saved", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsUpdated = getContentResolver().update(cItemUri, values, null, null);
            if (rowsUpdated == 0) {
                Toast.makeText(this, "Error updating Item", Toast.LENGTH_SHORT).show();
                return false;
            } else {
                Toast.makeText(this, "Item successfully updated", Toast.LENGTH_SHORT).show();
            }
        }

        return true;
    }


}
