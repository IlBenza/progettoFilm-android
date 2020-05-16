package it.alessandro.vendramini.applicazioneprogettofilm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ProgressBar;

import java.util.ArrayList;
import java.util.List;

import it.alessandro.vendramini.applicazioneprogettofilm.R;
import it.alessandro.vendramini.applicazioneprogettofilm.adapters.FilmAdapter;
import it.alessandro.vendramini.applicazioneprogettofilm.adapters.FilmPreferitoAdapter;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmDB;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmTableHelper;
import it.alessandro.vendramini.applicazioneprogettofilm.util.Singleton;

public class FilmPreferitiActivity extends AppCompatActivity {

    final String tableName = FilmTableHelper.TABLE_NAME;
    final String sortOrder = FilmTableHelper.ID_FILM + " ASC ";

    private RecyclerView recyclerView_listaFilmPreferiti;
    private FilmPreferitoAdapter filmPreferitoAdapter;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_preferiti);

        recyclerView_listaFilmPreferiti = findViewById(R.id.recyclerView_listaFilmPreferiti);

        filmPreferitoAdapter = new FilmPreferitoAdapter(this);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView_listaFilmPreferiti.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView_listaFilmPreferiti.setLayoutManager(gridLayoutManager);
        }

        //Leggo nel db
        filmPreferitoAdapter.setListaFilm(leggiDatiNelDB());
        filmPreferitoAdapter.notifyDataSetChanged();

        recyclerView_listaFilmPreferiti.setAdapter(filmPreferitoAdapter);
    }

    private List<Film> leggiDatiNelDB(){

        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(sqLiteDatabase, tableName);
        Cursor cursor = sqLiteDatabase.query(tableName, null, null, null,
                null, null, sortOrder);

        List<Film> listaTemporanea = new ArrayList<>();

        for (int i = 0; i < count; i++){
            cursor.moveToNext();
            long idFilm = cursor.getInt(cursor.getColumnIndex(FilmTableHelper.ID_FILM));
            String titolo = cursor.getString(cursor.getColumnIndex(FilmTableHelper.TITOLO));
            Double valutazione = cursor.getDouble(cursor.getColumnIndex(FilmTableHelper.VALUTAZIONE));
            String dataRilascio = cursor.getString(cursor.getColumnIndex(FilmTableHelper.DATA_RILASCIO));
            String descrizione = cursor.getString(cursor.getColumnIndex(FilmTableHelper.DESCRIZIONE));
            String stringaDescrizioneUno = cursor.getString(cursor.getColumnIndex(FilmTableHelper.STRINGA_IMMAGINE_UNO));
            String stringaDescrizioneDue = cursor.getString(cursor.getColumnIndex(FilmTableHelper.STRINGA_IMMAGINE_DUE));
            boolean isPreferito = (cursor.getInt(cursor.getColumnIndex(FilmTableHelper.PREFERITO)) == 1);

            //Solo se è true allora vuol dire che è preferito
            if (isPreferito) {
                listaTemporanea.add(new Film(idFilm, titolo, valutazione, dataRilascio, descrizione, stringaDescrizioneUno, stringaDescrizioneDue, isPreferito));
            }
        }
        return listaTemporanea;
    }
}
