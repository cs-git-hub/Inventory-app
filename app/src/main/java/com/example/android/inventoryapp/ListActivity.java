package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.example.android.inventoryapp.data.InventoryContract;

public class ListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int ITEM_LOADER = 0;
    InventoryCursorAdapter mItemCursorAdapter;
    public static FloatingActionButton fab;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        ListView itemsListView = (ListView) findViewById(R.id.list);
        TextView emptyListText = (TextView) findViewById(R.id.noContent);
        itemsListView.setEmptyView(emptyListText);
        mItemCursorAdapter = new InventoryCursorAdapter(this, null);
        itemsListView.setAdapter(mItemCursorAdapter);
        itemsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        Intent intent = new Intent(ListActivity.this, Details.class);
                        intent.setData(ContentUris.withAppendedId(InventoryContract.InventoryItems.CONTENT_URI, id));
                        startActivity(intent);
                    }


        });
        getSupportLoaderManager().initLoader(ITEM_LOADER, null, this);
    }
    @Override
    public void onBackPressed() {
        int count = getFragmentManager().getBackStackEntryCount();
        if (count == 0) {
            super.onBackPressed();
        } else {
            getFragmentManager().popBackStack();
            fab.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_list, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.delete_all_entries_list) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Do you wish to delete all items?");
            builder.setPositiveButton("Delete All", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    int rowsDeleted = getContentResolver().delete(InventoryContract.InventoryItems.CONTENT_URI, null, null);
                    Log.v("ListActivity", rowsDeleted + " rows deleted from database");
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                InventoryContract.InventoryItems._ID,
                InventoryContract.InventoryItems.COLUMN_NAME,
                InventoryContract.InventoryItems.COLUMN_PRICE,
                InventoryContract.InventoryItems.COLUMN_QUANTITY,
                InventoryContract.InventoryItems.COLUMN_IMAGE

        };
        return new CursorLoader(this,
                InventoryContract.InventoryItems.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mItemCursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mItemCursorAdapter.swapCursor(null);
    }
}
