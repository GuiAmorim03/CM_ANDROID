package com.example.watchlist

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

class WatchListMovie(
    val id: Int,
    val title: String,
    initialChecked: Boolean = false
) {
    var checked by mutableStateOf(initialChecked)
}