package com.example.watchlist

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.watchlist.ui.theme.WatchListTheme
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Checkbox
import androidx.compose.material3.TextField
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.viewmodel.compose.viewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WatchListTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    WatchListScreen()
                }
            }
        }
    }
}

@Composable
fun WatchListScreen(modifier: Modifier = Modifier, watchListViewModel: WatchListViewModel = viewModel()) {
    Column(modifier = modifier.padding(16.dp, top = 40.dp)) {
        //WaterCounter()

        AddMovie(watchListViewModel = watchListViewModel)

        WatchListMoviesList(
            list = watchListViewModel.movies,
            onCheckedMovie = { movie, checked ->
                watchListViewModel.changeMovieChecked(movie, checked)
            },
            onRemoveMovie = { movie -> watchListViewModel.remove(movie) }
        )
    }
}

@Composable
fun WatchListMoviesList(
    list: List<WatchListMovie>,
    onCheckedMovie: (WatchListMovie, Boolean) -> Unit,
    onRemoveMovie: (WatchListMovie) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier = modifier) {
        items(
            items = list,
            key = { movie -> movie.id }
        ) { movie ->
            WatchListMovieItem(
                movieName = movie.title,
                checked = movie.checked,
                onCheckedChange = {checked -> onCheckedMovie(movie, checked)},
                onRemove = { onRemoveMovie(movie)}
            )
        }
    }
}


@Composable
fun WatchListMovieItem(
    movieName: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            text = movieName
        )
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
        IconButton(onClick = onRemove) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}

@Composable
fun AddMovie(modifier: Modifier = Modifier, watchListViewModel: WatchListViewModel) {
    var newMovieTitle by remember { mutableStateOf("") }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        TextField(
            value = newMovieTitle,
            onValueChange = { newMovieTitle = it },
            label = { Text("Enter movie title") },
            modifier = Modifier
                .weight(1f)
                .padding(end = 8.dp)
        )

        Button(
            onClick = {
                if (newMovieTitle.isNotBlank()) {
                    watchListViewModel.add(newMovieTitle)
                    newMovieTitle = ""
                }
            }
        ) {
            Text("Add")
        }
    }
}


@Composable
fun WaterCounter(modifier: Modifier = Modifier) {
    Column(modifier = modifier.padding(16.dp, top = 40.dp)) {
        var count by rememberSaveable { mutableStateOf(0) }

        if (count > 0) {
            /*
            var showTask by remember { mutableStateOf(true) }
            if (showTask) {
                WaterTask(
                    onClose = { showTask = false},
                    taskName = "Have you taken your 15 minute walk today?"
                )
            }

            */
            Text("You've had $count glasses.")
        }
        Row(Modifier.padding(top = 8.dp)) {
            Button(onClick = { count++ }, enabled = count < 5) {
                Text("Add one")
            }
            /*
            Button(
                onClick = { count = 0 },
                Modifier.padding(start = 8.dp)) {
                Text("Clear water count")
            }
             */
        }
    }
}

/*
@Composable
fun WaterTask(
    taskName: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier, verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f).padding(start = 16.dp),
            text = taskName
        )
        IconButton(onClick = onClose) {
            Icon(Icons.Filled.Close, contentDescription = "Close")
        }
    }
}
*/

@Preview(showBackground = true)
@Composable
fun WatchListPreview() {
    WatchListTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            WatchListScreen()
        }
    }
}