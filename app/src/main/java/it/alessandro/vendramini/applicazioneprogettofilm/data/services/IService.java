package it.alessandro.vendramini.applicazioneprogettofilm.data.services;

import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Results;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface IService {

    @GET ("movie/upcoming")
    Call<Results> getMovieUpcoming(@Query("api_key") String api_key,
                                   @Query("language") String language,
                                   @Query("page") int page);
}
