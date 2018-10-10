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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
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
import android.view.MotionEvent;
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

import static com.example.android.pets.data.PetContract.PetEntry._ID;

/**
 * Allows user to create a new pet or edit an existing one.
 */
// to get a something started from an intent lisview use LoaderManager.LoaderCallbacks<Cursor> and add all the missing methods and build your to do things there
    // but call on that
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    // make a boolean variable to flag if some value change in the edit filed
    private boolean mPetHasChanged = false;

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

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

        if (currentPetUri == null){
            setTitle(R.string.edite_adding_pet);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }else{
            Log.v("Uri in use","using "+currentPetUri);
            setTitle(R.string.editor_activity_title_edit_pet);
            // Give all Views an a activeListener
            mNameEditText.setOnTouchListener(mTouchListener);
            mBreedEditText.setOnTouchListener(mTouchListener);
            mWeightEditText.setOnTouchListener(mTouchListener);
            mGenderSpinner.setOnTouchListener(mTouchListener);
            // Initialize a loader to read the pet data from the database
            // and display the current values in the editor
            getLoaderManager().initLoader(EXISTING_PET_LOADER,null,this);

        }


        setupSpinner();

    }

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mPetHasChanged boolean to true.

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mPetHasChanged = true;
            return false;
        }
    };

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
        if (mCurrentPetUri == null ){
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
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If this is a new pet, hide the "Delete" menu item.
        if (mCurrentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }
    // having a way to see if any edittextview is empty and we are slos pasing the paramter of
    // MenuItem item to see if we have a touch event on the menu
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        boolean compltedFields = true;
        if ( TextUtils.isEmpty(mNameEditText.getText().toString())){
            mNameEditText.setError("Required");
            compltedFields = false;
        }

        if (mGender == PetContract.PetEntry.GENDER_UNKNOWN){
            Toast.makeText(getApplicationContext(),R.string.edit_error_required_field,Toast.LENGTH_SHORT);
        }
        if (TextUtils.isEmpty(mWeightEditText.getText().toString())){
            mWeightEditText.setError("Require");
            compltedFields = false;
        }
        if (compltedFields == false){
            return false;
        }



        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Just call on the method above
                savePet();

                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
            //call on the method that makes the diaolog box to cancle action or to delete pet
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mPetHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all pet attributes, define a projection that contains
        // all columns from the pet table
        String[] projection = {
                _ID,
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
    String id_colum_to_delete;
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
            int id_ColumnIndex = cursor.getColumnIndex(_ID);
            int nameColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
            int breedColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
            int genderColumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
            int weightCulumnIndex = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);

            //use the address to pull its data values
            //using a cursor object to get the string
            id_colum_to_delete =cursor.getString(id_ColumnIndex);
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
        // clear all to null otherwise you get error when going back to the main Activity
        mNameEditText.setText(null);
        mBreedEditText.setText(null);
        mGenderSpinner.setSelection(0);
        mWeightEditText.setText(null);
    }

    // This type of method will create a box dialog to display a message.
    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        //AlertDialog needs and import of class hover over AlertDialog
        // and click on Ctrl+Enter
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Keep editing" button, so dismiss the dialog
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

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mPetHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }
    /** This next 2 methods are needed to make a selected pet be deleted from database */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the pet.
                deletePet();
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

    /**
     * Perform the deletion of the pet in the database.
     */
    private void deletePet() {
        // Pull the data again from the parameter when the item got click on the listview
        Intent intent_edite_update = getIntent();
        Uri contenteproviderUri = intent_edite_update.getData();
        String projection ="_ID";
        String [] argumentSaythat = new String[] {PetContract.PetEntry._ID};
        int rowDeletedReturn = 0;
        if (contenteproviderUri != null) {
            // pROJECTION/where & arguments/selectionArgu can be null since contenteproviderUri
            //is only going to point to a single data set of values in that row, otherwise use
            //String [] argumentSaythat = new String[] {PetContract.PetEntry._ID}; &string where= "_ID";
            rowDeletedReturn = getContentResolver().delete(contenteproviderUri, null, null);
        }
        if (0 == rowDeletedReturn) {
            // If no rows were deleted, then there was an error with the delete.
            Toast.makeText(this, getString(R.string.editor_delete_pet_failed),
                    Toast.LENGTH_SHORT).show();
        }else {
            // Otherwise, the delete was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_delete_pet_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }

}