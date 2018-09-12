package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
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
                                                    // implements LoaderManager.LoaderCallbacks<Cursor>
//                                                  will create error so just clcik on lightbuld and implement methods
    //                                              and select all overwrite methods keyboard shortcut Ctrl + I
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    //first make and integer loader part of background tread process for db
    private static  final int PET_CURSUR = 0;

    // to reference and instance repeatedly to the class file PetCursorAdapter in this class we make it
    // a variable object  part of background tread process for db
    PetCursorAdapter mCusorAdapter ;


    // These are the Contacts rows that we will retrieve
    static final String[] PROJECTION = new String[] {ContactsContract.Data._ID,
            ContactsContract.Data.DISPLAY_NAME};

    // This is the select criteria
    static final String SELECTION = "((" +
            ContactsContract.Data.DISPLAY_NAME + " NOTNULL) AND (" +
            ContactsContract.Data.DISPLAY_NAME + " != '' ))";

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

        // Find the ListView which will be populated with the pet data just to avid adding to
        // many things to the main activity_catalog.xml file
        ListView petListView = (ListView) findViewById(R.id.list_view_recycler);

        //Using the already declared object of listView make it the items click-able
        // with setOnItemClickListener and not!!!!OnClickListener!!!! different ok.
        petListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // create the overwrite method to open the edit_update intent
            // adapterview is just the listview
            //view is the particular view for the item
            // id is the position of the item in listview
            //long is
            public void onItemClick(AdapterView<?> adapterView, View view, int id, long uriId) {
                // create your intent object
                //                      say that the object is going to be and intent from the this class file
                //                                                  and you want to open the EditorActivity class
                Intent edite_update = new Intent(CatalogActivity.this, EditorActivity.class);
                // Ask for the current URI so it would be asking for the database entry you click on
                Uri currentPetUri = ContentUris.withAppendedId(PetEntry.CONTENT_URI,id);
                //pass on parameter of uri database into the object of  edite_update
                edite_update.setData(currentPetUri);

                // rember to specify to start the intent and pass on this object so the activity can
                // what database row was click on which will be received by protected void onCreate(Bundle savedInstanceState)
                startActivity(edite_update);
            }
        });



        // Find and set empty view on the ListView, so that it only shows when the list has 0
        // items. Later in the querry if new pets are found in database it will overlap this messages
        View emptyView = findViewById(R.id.empty_view);
        //set the listview to show this message
        petListView.setEmptyView(emptyView);

        //Setup the adpater to create a list item for each row of the pett data in the Cursor.
        //There is no pet data yet (untill the loader finishes) so pass in null for the cursor.
        mCusorAdapter = new PetCursorAdapter(this, null);
        petListView.setAdapter(mCusorAdapter);
        //Kick off the loader
        getLoaderManager().initLoader(PET_CURSUR, null, this);

        //Used only for manual db access reads
        //displayDatabaseInfo();
        //PetDbHelper mDbHelper = new PetDbHelper(this);

        //SQLiteDatabase db = mDbHelper.getReadableDatabase();


    }

    /**
     * This will be cement out as we dont want this to run in the main thread of activities
    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }
     */

    /**
     * Temporary helper method to display information in the onscreen TextView about the state of
     * the pets database.
     * This will be cement out as we dont want this to run in the main thread of activities
    private void displayDatabaseInfo() {

        PetDbHelper dbHelperForDisplay  = new PetDbHelper(this);
        SQLiteDatabase db = dbHelperForDisplay.getWritableDatabase();
        Cursor mCursor = db.rawQuery("SELECT * FROM pets", null);
        ListView PetListView = (ListView) findViewById(R.id.list_view_recycler);

        PetCursorAdapter adapter = new PetCursorAdapter(this, mCursor);
        PetListView.setAdapter(adapter);


    }
     */
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
        Values.put(PetEntry.COLUMN_PET_GENDER,1);
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
    // the 3 overwrite methods ware part of the implements LoaderManager.LoaderCallbacks<Cursor>
    // that is used for background tread
    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Now create and return a CursorLoader that will take care of
        // creating a Cursor for the data being displayed.
        // so copy some of the display method code.
        //Define a projection that species the columns from the table we care about
        String [] projection = {
                PetEntry._ID,
                PetEntry.COLUMN_PET_NAME,
                PetEntry.COLUMN_PET_BREED
        };
        // This laoder will excute the contentProvider's query method on a background thread
        return new CursorLoader(this, //Parent activity context
                PetEntry.CONTENT_URI,   //Provider content URI to query
                projection,             // Column to include in the resulting Cursor
                null,            //no selection Clause/ look in column
                null,         //No selection arguments / where string or int is
                                        // found in selection-clause column
                null);          //Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Swap the new cursor in. (The framework will take care of closing the
        // old cursor once we return.)
        //mCusorAdapter is just PetCusorAdapter class
        mCusorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // This is called when the last Cursor provided to onLoadFinished()
        // above is about to be closed. We need to make sure we are no
        // longer using it.
        //mCusorAdapter is just PetCusorAdapter class
        mCusorAdapter.swapCursor(null);

    }
}