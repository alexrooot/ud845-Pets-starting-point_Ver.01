/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetProvider;

/**
 * The next import is looking for files in package we created called data
 * we also told it to look at the class file PetContract,
 * so if we want to use something form that class file we can use the
 * mGender = PetContract.PetEntry.GENDER_MALE;
 * Or under import we can use import com.example.android.pets.data.PetContract.PetEntry;
 * and then just use mGender = PetEntry.GENDER_MALE;
 */
import com.example.android.pets.data.PetDbHelper;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    /** Identifier for the pet data loader */
    private static final int EXISTING_PET_LOADER = 0;

    /** Content URI for the existing pet (null if it's a new pet) */
    private Uri mCurrentPetUri;

    /** EditText field to enter the pet's name */
    private EditText mNameEditText;

    /** EditText field to enter the pet's breed */
    private EditText mBreedEditText;

    /** EditText field to enter the pet's weight */
    private EditText mWeightEditText;

    /** EditText field to enter the pet's gender */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = PetContract.PetEntry.GENDER_UNKNOWN;



    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);
        // after click on on listView ask to build and object to have the database ID indetify
        Intent intent_edite_update = getIntent();
        //put the URI into an object that was return from from onItemClick
        Uri currentPetUri = intent_edite_update.getData();

        // this variable gets used on the overwrite methods and we are just getting the data from passed in onitemclick
        mCurrentPetUri = intent_edite_update.getData();

        if (currentPetUri == null){
            setTitle("Add a Pet");
        }else{
            Log.v("Uri in use","using "+currentPetUri);
            setTitle(R.string.editor_activity_title_edit_pet);

            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PET_LOADER,null,this);

        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        setupSpinner();

    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    /**
     *
     *
     * Making a new method to save the user entered data from the Edittext
     */
    private void savePet(){
        Intent intent_edite_update = getIntent();
        //put the URI into an object that was return from from onItemClick
        Uri currentPetUri = intent_edite_update.getData();




        String nameString =  mNameEditText.getText().toString().trim();
        String breedString = mBreedEditText.getText().toString().trim();
        int genderString = mGender;
        String weightString = mWeightEditText.getText().toString().trim();
        // convert the string to int value if possible
        int weight = Integer.parseInt(weightString);

        //instiate the class petHelepr for  use in this method to pass over elements
        PetDbHelper mDbHelper = new PetDbHelper(this);

        // instiate a write to databse write mode
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        //Starte to build the content values again just like for the dummy
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, nameString);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breedString);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight);
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER,genderString);

        //Send data to helper instance
        // and save the return value of db.insert(PetContract.PetEntry.TABLE_NAME,null, values);
        //Not used onle manual method now use contentProvider long newPet = db.insert(PetContract.PetEntry.TABLE_NAME,null, values);

        // ask if we have a clickItem Uri already by using if null will be a new pet
        if (mCurrentPetUri == null){
            //You dont need the Uri save object its just to use if you need to remmer or reuse that provider later
            //          to save the values start with getContentResolver to get the content provider to work
            //                  Then imply to the contentprovder that you will be inserting data
            //                              then say were that data is at
            Uri newId = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);
            Log.v("database Entry Info", "The Manual edite entered "+newId );
            Toast dbToast = Toast.makeText(getApplicationContext(),R.string.action_save, Toast.LENGTH_SHORT);
            dbToast.setMargin(500,500);
            dbToast.show();
        }else{
            //again this is just to write to the database bia contentprovider, so start with getContentResolver
            //                          Specify that you are going to perfrom and update
            //                                  tell it the path by using the parameter of mCurrentPetUri = intent_edite_update.getData();
            int updateId = getContentResolver().update(mCurrentPetUri, values, null, null);
            if (updateId == 0){
                Toast.makeText(getApplicationContext(),R.string.editor_update_pet_failed, Toast.LENGTH_SHORT).show();
            }else{
                Toast.makeText(getApplicationContext(),R.string.editor_update_pet_successful, Toast.LENGTH_SHORT).show();
            }
        }
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Just call on the method above
                savePet();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                PetContract.PetEntry._ID,
                PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED,
                PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,   // Parent activity context
                mCurrentPetUri,         // Query the content URI for the current pet
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // get out early if the cursor is null or there is less than 1 row in the cursor

        if (cursor == null  ) {
            return;
        }
        if (cursor.moveToFirst()){
            // if the cursor has more than 1 row then we can check for what row we are in and try
            // to update the user interface information displayed
            // We have been passed down the currentURI activity with this information from onitemclcick
            // create int address for where those elemts are in the databse so the content resolver can use
            int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
            int weightCulumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);

            //use the address to pull its data values
            //using a cursor object to get the string
            String name = cursor.getString(nameColumnIndex);
            String breed = cursor.getString(breedColumnIndex);
            int gender = cursor.getInt(genderColumnIndex);
            int weight = cursor.getInt(weightCulumnIndex);

            //now send the raw values to the UI to update the edit field's
            mNameEditText.setText(name);
            mBreedEditText.setText(breed);
            mWeightEditText.setText(Integer.toString(weight));
            // Gender is a dropdown spinner, so map the constant value from the database
            // into one of the dropdown options (0 is Unknown, 1 is Male, 2 is Female).
            // Then call setSelection() so that option is displayed on screen as the current selection.
            switch (gender){
                case PetContract.PetEntry.GENDER_MALE:
                    mGenderSpinner.setSelection(1);
                    break;
                case PetContract.PetEntry.GENDER_FEMALE:
                    mGenderSpinner.setSelection(2);
                    break;
                default:
                    mGenderSpinner.setSelection(PetContract.PetEntry.GENDER_UNKNOWN);
                    break;
                }
            }
        }



    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.setText(null);
        mBreedEditText.setText(null);
        mGenderSpinner.setSelection(0);
        mWeightEditText.setText(0);
    }
}