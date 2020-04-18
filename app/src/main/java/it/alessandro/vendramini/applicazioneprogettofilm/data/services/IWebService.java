package it.alessandro.vendramini.applicazioneprogettofilm.data.services;

import java.util.List;

import it.alessandro.vendramini.applicazioneprogettofilm.data.model.Film;

public interface IWebService {

    void onTodosFetched(boolean success, List<Film> listaFilm, int errorCode, String errorMessage);
}
