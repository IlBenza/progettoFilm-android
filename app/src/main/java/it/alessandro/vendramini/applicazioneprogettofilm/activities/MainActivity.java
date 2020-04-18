package it.alessandro.vendramini.applicazioneprogettofilm.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

import java.util.List;

import it.alessandro.vendramini.applicazioneprogettofilm.R;
import it.alessandro.vendramini.applicazioneprogettofilm.adapters.FilmAdapter;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;
import it.alessandro.vendramini.applicazioneprogettofilm.data.services.IWebService;
import it.alessandro.vendramini.applicazioneprogettofilm.data.services.WebService;


public class MainActivity extends AppCompatActivity{

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
}
