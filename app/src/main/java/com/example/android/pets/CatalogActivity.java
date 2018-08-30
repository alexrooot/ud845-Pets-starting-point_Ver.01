package com.example.android.pets;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
// you can import the the classes varaibles b using this two type of imports
//You can be more specific about what file if you give the specifc class name
// and it will avoid you from typing in the class name and then file.
import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;
import com.example.android.pets.data.PetProvider;
//import com.example.android.pets.data.PetDbHelper;
//import com.example.android.pets.data.PetProvider;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    //Making a global instance to the class PetHelper
    //PetDbHelper mDbHelper = new PetDbHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });
        //Used only for manual db access reads
        //displayDatabaseInfo();
        //PetDbHelper mDbHelper = new PetDbHelper(this);

        //SQLiteDatabase db = mDbHelper.getReadableDatabase();
    }
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }


    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     */
    private void displayDatabaseInfo() {

        PetDbHelper dbHelperForDisplay  = new PetDbHelper(this);
        SQLiteDatabase db = dbHelperForDisplay.getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM pets", null);
        ListView PetListView = (ListView) findViewById(R.id.list_view_recycler);

        PetCursorAdapter adapter = new PetCursorAdapter(this, mCursor);
        PetListView.setAdapter(adapter);


    }
    private void insertPet(){

        //Make and instance of the class PetDbHelper to use the as to
        // where in databse columns you are pointing too
        //PetDbHelper mDbHelper = new PetDbHelper(this);

        // Again make and instance to the class Pethelper but more specificly
        //to the method getReadableDatabase
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Not in use only for manual db access SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues Values = new ContentValues();
        //
        Values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        Values.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        Values.put(PetEntry.COLUMN_PET_GENDER,"Male");
        Values.put(PetEntry.COLUMN_PET_WEIGHT,7);

        // Insert a new row for Toto into the provider using the ContentResolver.
        // Use the {@link PetEntry#CONTENT_URI} to indicate that we want to insert
        // into the pets database table.
        // Receive the new content URI that will allow us to access Toto's data in the future.
        Uri newUri = getContentResolver().insert(PetEntry.CONTENT_URI, Values);
        Log.v("New row", "New row ID"+newUri);

        //Inser data into a new row
        //You dont need to add the long newROWID =
        //That is just to make use for the Log.v(tag...,msg... + string)
        // But his is a bad way instead use the contentProvider
        //long newRowId = db.insert(PetEntry.TABLE_NAME,null, Values);
        //Log.v("New row", "New row ID"+newRowId);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}