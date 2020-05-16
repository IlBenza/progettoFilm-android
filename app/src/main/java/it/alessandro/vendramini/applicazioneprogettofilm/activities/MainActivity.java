package it.alessandro.vendramini.applicazioneprogettofilm.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import it.alessandro.vendramini.applicazioneprogettofilm.R;
import it.alessandro.vendramini.applicazioneprogettofilm.adapters.FilmAdapter;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;
import it.alessandro.vendramini.applicazioneprogettofilm.data.services.IWebService;
import it.alessandro.vendramini.applicazioneprogettofilm.data.services.WebService;
import it.alessandro.vendramini.applicazioneprogettofilm.fragments.AggiungiPreferitoDialogFragment;
import it.alessandro.vendramini.applicazioneprogettofilm.fragments.IAggiungiPreferitoDialogFragmentListener;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmDB;
import it.alessandro.vendramini.applicazioneprogettofilm.local.FilmTableHelper;


public class MainActivity extends AppCompatActivity implements IAggiungiPreferitoDialogFragmentListener {

    final String tableName = FilmTableHelper.TABLE_NAME;
    final String sortOrder = FilmTableHelper.ID_FILM + " ASC ";

    private RecyclerView recyclerView_listaFilm;
    private FilmAdapter filmAdapter;
    private GridLayoutManager gridLayoutManager;
    private WebService webService;
    private ProgressBar progressBar_caricamento;
    private ProgressBar getProgressBar_caricamentoLista;
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

                //Nome pagina
                getSupportActionBar().setTitle(R.string.app_json_type_name);

                //Salvo i dati nel db
                salvaNelDB(listaFilm);

            } else {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getProgressBar_caricamentoLista.setVisibility(View.GONE);
                        progressBar_caricamento.setVisibility(View.GONE);
                        imageView_logo.setVisibility(View.GONE);
                        recyclerView_listaFilm.setVisibility(View.GONE);
                        Toast.makeText(getApplicationContext(), "Connessione di rete non riuscita", Toast.LENGTH_SHORT ).show();

                        //Leggo nel db
                        filmAdapter.setListaFilm(leggiDatiNelDB());
                        filmAdapter.notifyDataSetChanged();
                        recyclerView_listaFilm.setVisibility(View.VISIBLE);
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

        recyclerView_listaFilm = findViewById(R.id.recyclerView_listaFilmPreferiti);
        progressBar_caricamento = findViewById(R.id.progressBar_caricamento);
        imageView_logo = findViewById(R.id.imageView_logo);
        getProgressBar_caricamentoLista = findViewById(R.id.progressBar_caricamentoLista);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2);
            recyclerView_listaFilm.setLayoutManager(gridLayoutManager);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 3);
            recyclerView_listaFilm.setLayoutManager(gridLayoutManager);
        }

        filmAdapter = new FilmAdapter(this);
        recyclerView_listaFilm.setAdapter(filmAdapter);

        getProgressBar_caricamentoLista.setVisibility(View.GONE);
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
        getProgressBar_caricamentoLista.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                pageNumber ++;
                webService.getFilm(webServerListener, Locale.getDefault().getLanguage(), pageNumber);
                getProgressBar_caricamentoLista.setVisibility(View.GONE);
            }
        }, 2000);
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
                contentValues.put(FilmTableHelper.STRINGA_IMMAGINE_UNO, singoloFilm.getImmaginePrimoPoster());
                contentValues.put(FilmTableHelper.STRINGA_IMMAGINE_DUE, singoloFilm.getImmagineSecondoPoster());
                contentValues.put(FilmTableHelper.VALUTAZIONE, singoloFilm.getValutazione());
                contentValues.put(FilmTableHelper.PREFERITO, singoloFilm.isPreferito());
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
            String stringaDescrizioneUno = cursor.getString(cursor.getColumnIndex(FilmTableHelper.STRINGA_IMMAGINE_UNO));
            String stringaDescrizioneDue = cursor.getString(cursor.getColumnIndex(FilmTableHelper.STRINGA_IMMAGINE_DUE));
            boolean isPreferito = (cursor.getInt(cursor.getColumnIndex(FilmTableHelper.PREFERITO)) == 1);
            listaTemporanea.add(new Film(idFilm, titolo, valutazione, dataRilascio, descrizione, stringaDescrizioneUno, stringaDescrizioneDue, isPreferito));
        }

        return listaTemporanea;
    }

    //Menu in alto a destra
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_layout, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        //Click sul Preferiti
        if(item.getItemId() == R.id.favorite_icon){

            Intent intent = new Intent(this, FilmPreferitiActivity.class);
            startActivity(intent);

        }

        //Click sul cerca
        if(item.getItemId() == R.id.search_icon){

            androidx.appcompat.widget.SearchView searchView = (androidx.appcompat.widget.SearchView) item.getActionView();
            searchView.setQueryHint("Cerca film");

            searchView.setOnQueryTextListener(new androidx.appcompat.widget.SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filmAdapter.getFilter().filter(newText);
                    return true;
                }
            });
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onPositivePressed(long filmId) {
        //Salva nei preferiti
        Toast.makeText(this, "Salvato", Toast.LENGTH_SHORT).show();

        //Aggiorno o dati
        SQLiteDatabase sqLiteDatabase = new FilmDB(this).getReadableDatabase();
        String nameId = Long.toString(filmId);
        ContentValues contentValues = new ContentValues();
        contentValues.put(FilmTableHelper.PREFERITO, true);

        sqLiteDatabase.update(tableName, contentValues, FilmTableHelper.ID_FILM + " = ?", new String[] { nameId });
    }

    @Override
    public void onNegativePressed() {
        //Nulla
    }
}
