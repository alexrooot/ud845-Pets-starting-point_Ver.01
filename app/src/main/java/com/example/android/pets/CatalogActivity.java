package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract.PetEntry;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    //Making a global instance to the class PetHelper
    PetDbHelper mDbHelper = new PetDbHelper(this);

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

        displayDatabaseInfo();
        PetDbHelper mDbHelper = new PetDbHelper(this);

        SQLiteDatabase db = mDbHelper.getReadableDatabase();
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
        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        PetDbHelper mDbHelper = new PetDbHelper(this);

        // Create and/or open a database to read from it
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

        //Define a projection that specifies the colunms fromthe database
        String[] projection = {BaseColumns._ID,
        PetEntry.COLUMN_PET_NAME,
        PetEntry.COLUMN_PET_BREED,
        PetEntry.COLUMN_PET_GENDER,
        PetEntry.COLUMN_PET_WEIGHT};

        //Filter out by colunm where it say's = ****...
       // String selection = PetEntry.COLUMN_PET_NAME + " = ?";
       // String [] selectionArgs = {"Toto"};

        //How you want to store cursor results
        //String sortOrder =
        //        PetEntry._ID + " DESC";
        Cursor cursor = db.query(
                PetEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );




        TextView displayView = (TextView) findViewById(R.id.text_view_pet);

        // Perform this raw SQL query "SELECT * FROM pets"
        // to get a Cursor that contains all rows from the pets table.

        /**
         *
         * This version of reading the database is consider solpy for user display
         */
        //Cursor cursor = db.rawQuery("SELECT * FROM " + PetEntry.TABLE_NAME, null);
        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // pets table in the database).

            displayView.setText("Number of rows in pets database table: " + cursor.getCount()+"\n\n");
            displayView.append(PetEntry._ID + "_" +
                    PetEntry.COLUMN_PET_NAME + " "+
                    PetEntry.COLUMN_PET_BREED + " " +
                    PetEntry.COLUMN_PET_GENDER+ " " +
                    PetEntry.COLUMN_PET_WEIGHT+"\n");
            // You need to create these varibles so that Java know you are refering to a
            // specific column in the wile loop
            int idColumnIndex = cursor.getColumnIndex(PetEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_GENDER);
            int weightColumnIndex = cursor.getColumnIndex(PetEntry.COLUMN_PET_WEIGHT);
            // the moveTONext will return true if there truly is a next row
            while (cursor.moveToNext()){
                int currentID = cursor.getInt(idColumnIndex);
                String currentName = cursor.getString(nameColumnIndex);
                String currentBreed = cursor.getString(breedColumnIndex);
                String currentGender = cursor.getString(genderColumnIndex);
                String currentWeight = cursor.getString(weightColumnIndex);
                // to save or display the current row of index data you do it now
                displayView.append("\n"+
                        currentID +
                        ":"+
                        currentName +
                        "-"+
                        currentBreed +
                        "-" +
                        currentGender +
                        "-" +
                        currentWeight+
                        "\n");
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }


    }
    private void insertPet(){
        //Make and instance of the class PetDbHelper to use the as to
        // where in databse columns you are pointing too
        //PetDbHelper mDbHelper = new PetDbHelper(this);

        // Again make and instance to the class Pethelper but more specificly
        //to the method getReadableDatabase
        //SQLiteDatabase db = mDbHelper.getReadableDatabase();

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        ContentValues Values = new ContentValues();
        //
        Values.put(PetEntry.COLUMN_PET_NAME, "Toto");
        Values.put(PetEntry.COLUMN_PET_BREED,"Terrier");
        Values.put(PetEntry.COLUMN_PET_GENDER,"Male");
        Values.put(PetEntry.COLUMN_PET_WEIGHT,7);

        //Inser data into a new row
        //You dont need to add the long newROWID =
        //That is just to make use for the Log.v(tag...,msg... + string)
        long newRowId = db.insert(PetEntry.TABLE_NAME,null, Values);

        Log.v("New row", "New row ID"+newRowId);
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