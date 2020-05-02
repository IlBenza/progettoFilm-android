package it.alessandro.vendramini.applicazioneprogettofilm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
    private GridLayoutManager gridLayoutManager;
    private WebService webService;
    private ProgressBar progressBar_caricamento;
    private ImageView imageView_logo;

    int pageNumber = 1;
    int currentItems, totalItems, scrollOutItems;
    boolean isScrolling = false;

    private IWebService webServerListener = new IWebService() {

        @Override
        public void onTodosFetched(boolean success, List<Film> listaFilm, int errorCode, String errorMessage) {
            if (success) {

                filmAdapter.addListaFilm(listaFilm);
                filmAdapter.notifyDataSetChanged();
                progressBar_caricamento.setVisibility(View.GONE);
                imageView_logo.setVisibility(View.GONE);
                recyclerView_listaFilm.setVisibility(View.VISIBLE);

                //Salvo i dati nel db
                salvaNelDB(listaFilm);

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        progressBar_caricamento.setVisibility(View.GONE);
                        imageView_logo.setVisibility(View.GONE);
                        recyclerView_listaFilm.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Connessione di rete non riuscita", Toast.LENGTH_SHORT ).show();

                        //Leggo nel db
                        filmAdapter.setListaFilm(leggiDatiNelDB());
                        filmAdapter.notifyDataSetChanged();
                    }
                }, 5000);
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
            gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView_listaFilm.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView_listaFilm.setLayoutManager(gridLayoutManager);
        }

        filmAdapter = new FilmAdapter(this);
        recyclerView_listaFilm.setAdapter(filmAdapter);

        progressBar_caricamento.setVisibility(View.VISIBLE);
        imageView_logo.setVisibility(View.VISIBLE);
        recyclerView_listaFilm.setVisibility(View.GONE);

        webService.getFilm(webServerListener, Locale.getDefault().getLanguage(), pageNumber);

        //Infinite scroll
        recyclerView_listaFilm.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                    isScrolling = true;
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if(dy > 0){
                    currentItems = gridLayoutManager.getChildCount();
                    totalItems = gridLayoutManager.getItemCount();
                    scrollOutItems = gridLayoutManager.findFirstVisibleItemPosition();

                    if(isScrolling && ((currentItems + scrollOutItems) >= totalItems)){
                        isScrolling = false;
                        caricoAltriDati();
                    }
                }
            }
        });
    }

    private void caricoAltriDati(){
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNumber ++;
                webService.getFilm(webServerListener, Locale.getDefault().getLanguage(), pageNumber);
            }
        }, 500);
    }

    private void salvaNelDB(List<Film> listaFilm){

        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getWritableDatabase();

        for(Film singoloFilm : listaFilm){

            if (!didIdExist(singoloFilm.getIdFilm())) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(FilmTableHelper.ID_FILM, singoloFilm.getIdFilm());
                contentValues.put(FilmTableHelper.TITOLO, singoloFilm.getTitolo());
                contentValues.put(FilmTableHelper.DATA_RILASCIO, singoloFilm.getDataRilascio());
                contentValues.put(FilmTableHelper.DESCRIZIONE, singoloFilm.getDescrizione());
                contentValues.put(FilmTableHelper.VALUTAZIONE, singoloFilm.getValutazione());
                sqLiteDatabase.insert(tableName, null, contentValues);
            }
        }
    }

    public boolean didIdExist(long id) {

        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getReadableDatabase();
        String[] allColumns = { FilmTableHelper.ID_FILM };
        Cursor cursor = sqLiteDatabase.query(tableName, allColumns,
                FilmTableHelper.ID_FILM + " = " + id, null, null, null,
                null);
        cursor.moveToFirst();
        boolean result = false;
        if (!cursor.isAfterLast()) {
            result = true;
        }
        return result;
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
            listaTemporanea.add(new Film(idFilm, titolo, valutazione, dataRilascio, descrizione, null, null));
        }

        return listaTemporanea;
    }
}
