package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

import com.example.android.pets.data.PetContract.PetEntry;


/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {


    /** URI matcher code for the content URI for the pets table */
    private static final int PETS = 100;

    /** URI matcher code for the content URI for a single pet in the pets table */
    private static final int PET_ID = 101;


    /**
     * The MIME type of the {@link #CONTENT_URI} for a list of pets.
     */
    public static final String CONTENT_LIST_TYPE =  //    Need to tell it where this varialbe is so you need to import the package class
            ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + PetContract.CONTENT_AUTHORITY + "/" + PetContract.PATH_PETS;
    /**
     * The MIME type of the {@link #CONTENT_URI} for a single pet.
     */
    public static final String CONTENT_ITEM_TYPE =  //    Need to tell it where this varialbe is so you need to import the package class
            ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + PetContract.CONTENT_AUTHORITY + "/" + PetContract.PATH_PETS;


    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.

        // TO DO: Add 2 content URIs to URI matcher
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY,PetContract.PATH_PETS+"/#",PET_ID);
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = mDbhelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = sUriMatcher.match(uri);
        // test each case to the variable that was passed down as parameter
        switch (match) {
            case PETS:
                // For the PETS code, query the pets table directly with the given
                // projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows of the pets table.
                // TO DO: Perform database query on pets table
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection,selection,selectionArgs,
                        null,null,sortOrder);
                break;
            case PET_ID:
                // For the PET_ID code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.android.pets/pets/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.
                //
                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };

                // This will perform a query on the pets table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = database.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        return cursor;
    }


    @Override
    //Uri is the unique resource identifier,
    // ContetValues is the data with other data inside of it.
    public Uri insert(Uri uri, ContentValues contentValues) {
        //ask android to find uri code with one inside android device
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a pet into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    // Uri is the data type that indicates to use unique resource identifier
    //                          ContentValues are the data variables passes as parameters
    private Uri insertPet(Uri uri, ContentValues values) {

        // TO DO: Insert a new pet into the pets database table with the given ContentValues
        // Get writeable database
        SQLiteDatabase database = mDbhelper.getReadableDatabase();

        //Sanity check values that were pass into insertPet method that things are written corretly.
        //            values is the big variable with the data content inside
        //                to get a specific element first ask for a specific data type
        //                      Ask for class file PetEntry then ask for specific BaseColumns
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME);
        String breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        int gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER);
        int weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (name == null || gender < 1 ||gender < 0 || weight < 0) {
            throw new IllegalArgumentException("Pet requires a name");
        }

        // Insert the new pet with the given values
        //I also imported the Petentry of that specific package into this class so you can shorten out resource destination
        long id = database.insert(PetEntry.TABLE_NAME, null, values);
        if (id == -1){
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }
        // Once we know the ID of the new row in the table,
        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    // you may need too inherite class files.
    //Uri is the unique resource identifier,
    // ContetValues is the data with other data inside of it.
    //selection is the BaseColumns in the database
    //selectionArgs is where you find males, or Toto names
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {




        //ask android to find uri code with one inside android device
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS://updates all or many columns
                return updatePet(uri, contentValues, selection, selectionArgs);
            case PET_ID://updates selected rows based on selectionArgs
                // For the PET_ID code, extract out the ID from the URI,
                // so we know which row to update. Selection will be "_id=?" and selection
                // arguments will be a String array containing the actual ID.
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updatePet(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    /**
     * Update pets in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more pets).
     * Return the number of rows that were successfully updated.
     */
    private int updatePet(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        //Sanity check values that were pass into insertPet method that things are written corretly.
        //            values is the big variable with the data content inside
        //                to get a specific element first ask for a specific data type
        //                      Ask for class file PetEntry then ask for specific BaseColumns
        String name = values.getAsString(PetEntry.COLUMN_PET_NAME),
                breed = values.getAsString(PetEntry.COLUMN_PET_BREED);
        Integer  gender = values.getAsInteger(PetEntry.COLUMN_PET_GENDER),
                weight = values.getAsInteger(PetEntry.COLUMN_PET_WEIGHT);
        if (name == null || gender < 1 ||gender < 0 ||gender == null || weight < 0) {
            throw new IllegalArgumentException("Pet requires a name, gender, weight, breed");
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        // get writeable database to update the data
        SQLiteDatabase database = mDbhelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement & excutes the update instruction
        return database.update(PetEntry.TABLE_NAME, values, selection, selectionArgs);
    }

    /** Tag for the log messages
     * ince we’ll be logging multiple times throughout this file, it would be ideal to create a log tag as a global
     * constant variable, so all log messages from the PetProvider will have the same log tag identifier when you
     * are reading the system logs*/
    public static final String LOG_TAG = PetProvider.class.getSimpleName();



    /**
     * Initialize the provider and the database helper object.
     */
    private PetDbHelper mDbhelper;// ADDed this as TO DO said so

    @Override
    public boolean onCreate() {
        //  Create and initialize a PetDbHelper object to gain access to the pets database.
        // Make sure the variable is a global variable, so it can be referenced from other
        // ContentProvider methods.
        mDbhelper = new PetDbHelper(getContext());// Added this as TO DO said so
        return true;
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     * This got replace with newwer version of content providers that use switch cases
     */
    //@Override
    //public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
     //                   String sortOrder) {
     //   return null;
    //}

    /**
     * Insert new data into the provider with the given ContentValues. This method is old
     * we now use theContentProvider methods

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        return null;
    }
     */

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     * Dont use this is the old manual way now use the contentProviders
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }
     */

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database just to see what we can use
        SQLiteDatabase database = mDbhelper.getWritableDatabase();
        // ask android to find a uri codes in the device to comapre to ourse using switch
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                // Delete all rows that match the selection and selection args
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            case PET_ID:
                // Delete a single row given by the ID in the URI
                selection = PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return database.delete(PetEntry.TABLE_NAME, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PETS:
                return PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

}