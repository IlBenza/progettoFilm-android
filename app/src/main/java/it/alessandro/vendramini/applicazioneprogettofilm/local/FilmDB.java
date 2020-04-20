package it.alessandro.vendramini.applicazioneprogettofilm.local;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;

public class FilmDB extends SQLiteOpenHelper {

    public static final String DB_NAME = "film.db";
    public static final int VERSION = 1;

    public FilmDB(@Nullable Context context) {
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(FilmTableHelper.CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public List<Film> leggiDatiNelDB(SQLiteDatabase sqLiteDatabase, String tableName, String sortOrder){

        Cursor cursor = sqLiteDatabase.query(tableName, null, null, null,
                null, null, sortOrder);

        /*
        toDoAdapter.changeCursor(toDoItems);
        toDoAdapter.notifyDataSetChanged();*/
        return null;
    }
}
