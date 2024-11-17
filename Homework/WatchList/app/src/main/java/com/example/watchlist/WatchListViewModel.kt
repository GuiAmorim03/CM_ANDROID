package com.example.watchlist

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.ViewModel

class WatchListViewModel : ViewModel() {
    private val _movies = getWatchListMovies().toMutableStateList()
    val movies: List<WatchListMovie>
        get() = _movies

    fun add(title: String) {
        val newId = (_movies.maxOfOrNull { it.id } ?: 0) + 1
        _movies.add(WatchListMovie(newId, title))
    }

    fun remove(item: WatchListMovie) {
        _movies.remove(item)
    }

    fun changeMovieChecked(item: WatchListMovie, checked: Boolean) =
        _movies.find { it.id == item.id }?.let { movie ->
            movie.checked = checked
    }

}

private fun getWatchListMovies(): List<WatchListMovie> {
    val movieTitles = listOf(
        "Gladiator II",
        "Venom 3: The Last Dance",
        "The Penguin",
        "Joker 2",
    )

    return movieTitles.mapIndexed { index, title ->
        WatchListMovie(index + 1, title)
    }
}
