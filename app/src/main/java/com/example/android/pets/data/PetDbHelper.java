package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PetDbHelper extends SQLiteOpenHelper {

    private static final String  LOG_TAG = PetDbHelper.class.getSimpleName();

    /**
     * NAme of the DB file
     */

    private static final String DATABASE_NAME ="shelter.db";
    /**
     * DATABASE version, If you change the DB schema, you must increment the databse version.
     */
    private static final int DATABASE_VERSION = 1;
    /**
     * create a new instance of {@link PetDbHelper}.
     *
     * @para context of the app
     */
    public PetDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * This is called when the databse is created for the first time
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        //Create a string that contains the SQL stament to create the epts table
        String SQL_CREATE_PETS_TABLE = "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME+" ("
                + PetContract.PetEntry._ID+" INTEGER PRIMARY KEY AUTOINCREMENT, "
                + PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, "
                + PetContract.PetEntry.COLUMN_PET_BREED + " TEXT, "
                + PetContract.PetEntry.COLUMN_PET_GENDER + " INTEGER NOT NULL, "
                + PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

        //Excute the SQL stament
        db.execSQL(SQL_CREATE_PETS_TABLE);

    }
    /**
     * This is called when the databse needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1 so there's nothing to do be done here.

    }
}
