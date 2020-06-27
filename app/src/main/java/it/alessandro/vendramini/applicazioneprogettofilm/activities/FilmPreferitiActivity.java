package it.alessandro.vendramini.applicazioneprogettofilm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import it.alessandro.vendramini.applicazioneprogettofilm.R;
import it.alessandro.vendramini.applicazioneprogettofilm.adapters.FilmAdapter;
import it.alessandro.vendramini.applicazioneprogettofilm.adapters.FilmPreferitoAdapter;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;
import it.alessandro.vendramini.applicazioneprogettofilm.fragments.AggiungiPreferitoDialogFragment;
import it.alessandro.vendramini.applicazioneprogettofilm.fragments.IAggiungiPreferitoDialogFragmentListener;
import it.alessandro.vendramini.applicazioneprogettofilm.fragments.IRimuoviTuttiPreferitiDialogFragmentListener;
import it.alessandro.vendramini.applicazioneprogettofilm.fragments.RimuoviTuttiPreferitiDialogFragment;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmDB;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmTableHelper;
import it.alessandro.vendramini.applicazioneprogettofilm.util.Singleton;

public class FilmPreferitiActivity extends AppCompatActivity implements IAggiungiPreferitoDialogFragmentListener, IRimuoviTuttiPreferitiDialogFragmentListener {

    final String tableName = FilmTableHelper.TABLE_NAME;
    final String sortOrder = FilmTableHelper.ID_FILM + " ASC ";

    private RecyclerView recyclerView_listaFilmPreferiti;
    private FilmPreferitoAdapter filmPreferitoAdapter;
    private GridLayoutManager gridLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_film_preferiti);

        //Nome pagina
        getSupportActionBar().setTitle(R.string.app_json_preferito);

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

    //Menu in alto a destra
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_preferiti, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if(item.getItemId() == R.id.cancella_icon){
            FragmentManager fragmentManager = getSupportFragmentManager();
            RimuoviTuttiPreferitiDialogFragment dialogFragment = new RimuoviTuttiPreferitiDialogFragment("ATTENZIONE", "Vuoi davvero rimuovere tutti i film preferiti?️");
            dialogFragment.show(fragmentManager, RimuoviTuttiPreferitiDialogFragment.class.getName());
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositivePressed(long filmId) {
        //Rimuovi dai preferiti
        Toast.makeText(this, "Film Rimosso", Toast.LENGTH_SHORT).show();

        //Aggiorno i dati
        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getReadableDatabase();
        String nameId = Long.toString(filmId);
        ContentValues contentValues = new ContentValues();
        contentValues.put(FilmTableHelper.PREFERITO, false);

        sqLiteDatabase.update(tableName, contentValues, FilmTableHelper.ID_FILM + " = ?", new String[] { nameId });
        finish();
        startActivity(getIntent());
    }

    @Override
    public void onPositivePressedRimuovi() {
        //Rimuovi tutto dai preferiti
        Toast.makeText(this, "Rimosso tutti i film", Toast.LENGTH_SHORT).show();

        //Aggiorno i dati
        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(FilmTableHelper.PREFERITO, false);

        sqLiteDatabase.update(tableName, contentValues, null, null);
        finish();
    }

    @Override
    public void onNegativePressed() {
        //Nulla
    }
}
