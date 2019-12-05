package com.example.android.inventoryapp;

import android.app.Activity;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
import com.example.android.inventoryapp.data.InventoryContract.InventoryItems;
import com.example.android.inventoryapp.data.InventoryHelper;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor>{
    private static final int CAMERA_R = 1888;
    public static final int EXISTING_LOAD = 0;
    private Uri cItemUri;
    private ImageView imageView;
    private EditText nameText;
    private CustomEditText priceText;
    private TextView idView;
    private TextView quantityView;
    private EditText supNameText;
    private EditText supPhoneText;
    private EditText supEmailText;
    private boolean edited = false;

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            edited = true;
            return false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // when an item was clicked, currentItemUri has content
        cItemUri = getIntent().getData();



        
        if (cItemUri == null) {
            Button orderButton = (Button) findViewById(R.id.order_button_edit);
            orderButton.setVisibility(View.GONE);


        }


        getSupportActionBar().setElevation(4);

        imageView = (ImageView) findViewById(R.id.image_editor_big);

        nameText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_name);
        priceText = (CustomEditText) findViewById(R.id.include).findViewById(R.id.edittext_price);

        idView = (TextView) findViewById(R.id.include).findViewById(R.id.id_textview);
        quantityView = (TextView) findViewById(R.id.include).findViewById(R.id.quantity_textview);

        supNameText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_sup_name);
        supPhoneText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_sup_phone);
        supEmailText = (EditText) findViewById(R.id.include).findViewById(R.id.edittext_sup_email);

        nameText.setOnTouchListener(touchListener);
        priceText.setOnTouchListener(touchListener);
        priceText.addTextChangedListener(new Price(priceText));
        supNameText.setOnTouchListener(touchListener);
        supPhoneText.setOnTouchListener(touchListener);
        supEmailText.setOnTouchListener(touchListener);


        if (cItemUri == null) {
            setTitle("Add an Item");
            idView.setText("#");
            invalidateOptionsMenu();
        } else {
            setTitle("Edit Item");
            getSupportLoaderManager().initLoader(EXISTING_LOAD, null, this);
        }
    }
    @Override
    protected void onStop() {
        edited = false;
        super.onStop();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (cItemUri == null) {
            MenuItem menuItem = menu.findItem(R.id.delete_single_entry_edit);
            menuItem.setVisible(false);
        }
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.delete_single_entry_edit:
                showDeleteConfirmationDialog();
                return true;
            case R.id.saveChanges_edit:
                boolean savingHasWorked = saveChanges();
                if (savingHasWorked) finish();
                return true;
            case R.id.make_image_edit:
                dispatchTakePictureIntent();
                edited = true;
                return true;
            case android.R.id.home:
                if (!edited) {
                    NavUtils.navigateUpFromSameTask(this);
                } else {
                    DialogInterface.OnClickListener discardButtonClickListener =
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                                }
                            };
                    showUnsavedChangesDialog(discardButtonClickListener);
                    return true;
                }
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onBackPressed() {
        if (!edited) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    private boolean saveChanges(){
        // get name
        String nameString = nameText.getText().toString().trim();

        if (nameString.equals("")){
            Toast.makeText(this, "Please enter the inventory name", Toast.LENGTH_SHORT).show();
            return false;
        }

        // get price and validate
        String priceString = priceText.getText().toString().replaceAll("\\D", "");
        int price;
        try{
            price = Integer.valueOf(priceString);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Price must contain numbers", Toast.LENGTH_SHORT).show();
            return false;
        }

        String currentQuantityString = quantityView.getText().toString();
        int quantityInt;
        try{
            quantityInt = Integer.parseInt(currentQuantityString);
        } catch (Exception e) {
            Toast.makeText(this, "Quantity must contain numbers", Toast.LENGTH_LONG).show();
            return false;
        }

        // get supplier name
        String supNameString = supNameText.getText().toString().trim();
        if (supNameString.equals("")){
            Toast.makeText(this, "Please enter the supplier name", Toast.LENGTH_SHORT).show();
            return false;
        }
        String supPhoneString = supPhoneText.getText().toString().trim();
        if (supPhoneString.equals("")){
            Toast.makeText(this, "Please enter the supplier phone number", Toast.LENGTH_SHORT).show();
            return false;
        }

        String supEmailString = supEmailText.getText().toString().trim();
        if (supEmailString.equals("")){
            Toast.makeText(this, "please enter the supplier email", Toast.LENGTH_SHORT).show();
            return false;
        }

        Bitmap imageBit = null;
        try {
            imageBit = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } catch (Exception e){
            Log.e("EditActivity", "Error getting Drawable in saveChanges()");
        }

        if (imageBit == null) {
            Toast.makeText(this, "Don't forget to take a picture", Toast.LENGTH_SHORT).show();
            return false;
        }
        byte[] imageAsByte = InventoryHelper.getAsByteArray(imageBit);

        if (cItemUri == null && TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) && TextUtils.isEmpty(supNameString)){
            return false;
        }

        ContentValues values = new ContentValues();
        values.put(InventoryItems.COLUMN_NAME, nameString);
        values.put(InventoryItems.COLUMN_PRICE, price);
        values.put(InventoryItems.COLUMN_QUANTITY, quantityInt);
        values.put(InventoryItems.COLUMN_IMAGE, imageAsByte);
        values.put(InventoryItems.COLUMN_SUP_NAME, supNameString);
        values.put(InventoryItems.COLUMN_SUP_PHONE,supPhoneString);
        values.put(InventoryItems.COLUMN_SUP_EMAIL,supEmailString);

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
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonListener){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Some changes were detected. Do you want to save them?");
        builder.setPositiveButton("Don't save them", discardButtonListener);
        builder.setNegativeButton("Save them", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    boolean savingHasWorked = saveChanges();
                    if (savingHasWorked) finish();
                    dialog.dismiss();
                }
            }
        });

        builder.create().show();
    }

    private void dispatchTakePictureIntent(){
        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, CAMERA_R);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_R && resultCode == Activity.RESULT_OK) {
            Bitmap photo = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(photo);
        }
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
}
