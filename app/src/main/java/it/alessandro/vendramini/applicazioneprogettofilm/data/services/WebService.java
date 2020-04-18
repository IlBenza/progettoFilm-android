package it.alessandro.vendramini.applicazioneprogettofilm.data.services;

import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import it.alessandro.vendramini.applicazioneprogettofilm.BuildConfig;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;
import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Results;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebService {

    private static final String BASE_URL = "https://api.themoviedb.org/3/";
    private static WebService instance;
    private IService iService;
    private ArrayList<Film> arrayFilm;

    public WebService(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        iService = retrofit.create(IService.class);
    }

    public static WebService getInstance() {
        if (instance == null)
            instance = new WebService();
        return instance;
    }

    public void getFilm(final IWebService callback) {

        // TODO cambiare BuildConfig.THE_MOVIE_DB_API_KEY perchè non funziona se killo l'app
        Call<Results> call = iService.getMovieUpcoming(BuildConfig.THE_MOVIE_DB_API_KEY, "it", 1);
        call.enqueue(new Callback<Results>() {
            @Override
            public void onResponse(Call<Results> call, Response<Results> response) {
                if (response.code() == 200) {
                    Results results = response.body();
                    int page = response.body().getPage();
                    int totalPage = response.body().getTotal_page();
                    arrayFilm = new ArrayList<>(Arrays.asList(results.getResults()));
                    callback.onTodosFetched(true, arrayFilm, -1, null);
                } else {
                    try {
                        callback.onTodosFetched(true, null, response.code(), response.errorBody().string());
                    } catch (IOException ex) {
                        Log.e("WebService", ex.toString());
                        callback.onTodosFetched(true, null, response.code(), "Generic error message");
                    }
                }
            }

            @Override
            public void onFailure(Call<Results> call, Throwable t) {
                callback.onTodosFetched(false, null, -1, t.getLocalizedMessage());
            }
        });
    }
}
