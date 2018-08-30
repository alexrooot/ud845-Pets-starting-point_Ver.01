package com.example.android.pets.data;


import android.net.Uri;
import android.provider.BaseColumns;

public final class PetContract {
    //Create a string constant whose value is the same as that from the AndroidManifest
    public static final String CONTENT_AUTHORITY = "com.example.android.pets";


    // To make this a usable URI, we use the parse method which takes in a URI string and returns a Uri.
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    //This constants stores the path for each of the tables which will be appended to the base content URI.
    public static final String PATH_PETS = "pets";



    public static final class PetEntry implements BaseColumns{
        public static String TABLE_NAME = "pets";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PET_NAME = "name";
        public final static String COLUMN_PET_BREED = "breed";
        public final static String COLUMN_PET_GENDER = "gender";
        public final static String COLUMN_PET_WEIGHT = "weight";

        public final static int GENDER_UNKNOWN =0;
        public final static int GENDER_MALE = 1;
        public final static int GENDER_FEMALE = 2;


        //Lastly, inside each of the Entry classes in the contract, we create a full URI for the class as a
        // constant called CONTENT_URI. The Uri.withAppendedPath() method appends the BASE_CONTENT_URI
        // (which contains the scheme and the content authority) to the path segment.
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PETS);

        }

    }

