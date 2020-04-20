package it.alessandro.vendramini.applicazioneprogettofilm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import it.alessandro.vendramini.applicazioneprogettofilm.R;
import it.alessandro.vendramini.applicazioneprogettofilm.adapters.FilmAdapter;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;
import it.alessandro.vendramini.applicazioneprogettofilm.data.services.IWebService;
import it.alessandro.vendramini.applicazioneprogettofilm.data.services.WebService;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmDB;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmTableHelper;
import it.alessandro.vendramini.applicazioneprogettofilm.util.Singleton;


public class MainActivity extends AppCompatActivity{

    final String tableName = FilmTableHelper.TABLE_NAME;
    final String sortOrder = FilmTableHelper.ID_FILM + " ASC ";

    private RecyclerView recyclerView_listaFilm;
    private FilmAdapter filmAdapter;
    private WebService webService;
    private ProgressBar progressBar_caricamento;
    private ImageView imageView_logo;

    private IWebService webServerListener = new IWebService() {

        @Override
        public void onTodosFetched(boolean success, List<Film> listaFilm, int errorCode, String errorMessage) {
            if (success) {

                filmAdapter.setListaFilm(listaFilm);
                filmAdapter.notifyDataSetChanged();
                progressBar_caricamento.setVisibility(View.GONE);
                imageView_logo.setVisibility(View.GONE);
                recyclerView_listaFilm.setVisibility(View.VISIBLE);

                salvaNelDB(listaFilm);
                leggiDatiNelDB(listaFilm.size());

                //Se la connessiona va giu
                if (!isNetworkConnected()){
                    filmAdapter.setListaFilm(leggiDatiNelDB(listaFilm.size()));
                    filmAdapter.notifyDataSetChanged();
                }

            } else {
                progressBar_caricamento.setVisibility(View.GONE);
                imageView_logo.setVisibility(View.GONE);
                recyclerView_listaFilm.setVisibility(View.GONE);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        webService = WebService.getInstance();

        recyclerView_listaFilm = findViewById(R.id.recyclerView_listaFilm);
        progressBar_caricamento = findViewById(R.id.progressBar_caricamento);
        imageView_logo = findViewById(R.id.imageView_logo);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            recyclerView_listaFilm.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            recyclerView_listaFilm.setLayoutManager(new GridLayoutManager(this, 3));
        }

        filmAdapter = new FilmAdapter(this);
        recyclerView_listaFilm.setAdapter(filmAdapter);

        progressBar_caricamento.setVisibility(View.VISIBLE);
        imageView_logo.setVisibility(View.VISIBLE);
        recyclerView_listaFilm.setVisibility(View.GONE);
        webService.getFilm(webServerListener);
    }

    private void salvaNelDB(List<Film> listaFilm){

        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getWritableDatabase();

        for(Film singoloFilm : listaFilm){
            ContentValues contentValues = new ContentValues();
            contentValues.put(FilmTableHelper.ID_FILM, singoloFilm.getIdFilm());
            contentValues.put(FilmTableHelper.TITOLO, singoloFilm.getTitolo());
            contentValues.put(FilmTableHelper.DATA_RILASCIO, singoloFilm.getDataRilascio());
            contentValues.put(FilmTableHelper.DESCRIZIONE, singoloFilm.getDescrizione());
            contentValues.put(FilmTableHelper.VALUTAZIONE, singoloFilm.getValutazione());
            sqLiteDatabase.insert(tableName, null, contentValues);


        }
    }

    private List<Film> leggiDatiNelDB(int size){

        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getReadableDatabase();
        Cursor cursor = sqLiteDatabase.query(tableName, null, null, null,
                null, null, sortOrder);

        List<Film> listaTemporanea = new ArrayList<>();

        for (int i = 0; i < size; i++){
            cursor.moveToNext();
            long idFilm = cursor.getInt(cursor.getColumnIndex(FilmTableHelper.ID_FILM));
            String titolo = cursor.getString(cursor.getColumnIndex(FilmTableHelper.TITOLO));
            Double valutazione = cursor.getDouble(cursor.getColumnIndex(FilmTableHelper.VALUTAZIONE));
            String dataRilascio = cursor.getString(cursor.getColumnIndex(FilmTableHelper.DATA_RILASCIO));
            String descrizione = cursor.getString(cursor.getColumnIndex(FilmTableHelper.DESCRIZIONE));
            listaTemporanea.add(new Film(idFilm, titolo, valutazione, dataRilascio, descrizione));
        }


        for (Film film : listaTemporanea){
            Log.d(Singleton.LOG_TAG, "ID: " + film.getIdFilm());
            Log.d(Singleton.LOG_TAG, "Titolo: " + film.getTitolo());
            Log.d(Singleton.LOG_TAG, "Valutaz: " + film.getValutazione());
            Log.d(Singleton.LOG_TAG, "Data: " + film.getDataRilascio());
            Log.d(Singleton.LOG_TAG, "Descrizione: " + film.getDescrizione());
        }

        return listaTemporanea;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }
}
