package com.atech.advancednotesremastered

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.atech.advancednotesremastered.data.Note
import com.atech.advancednotesremastered.data.NoteDatabase
import com.atech.advancednotesremastered.data.NoteEntity
import com.atech.advancednotesremastered.ui.theme.AdvancedNotesRemasteredTheme
import com.atech.advancednotesremastered.viewmodel.EditorScreenViewModel
import com.atech.advancednotesremastered.viewmodel.MainScreenViewModel
import kotlinx.serialization.Serializable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class MainActivity : ComponentActivity() {

    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            NoteDatabase::class.java,
            "notes.db"
        ).build()
    }

    private val mainScreenViewModel by viewModels<MainScreenViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return MainScreenViewModel(db.dao) as T
            }
        }
    }

    private val editorScreenViewModel by viewModels<EditorScreenViewModel> {
        object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return EditorScreenViewModel(db.dao) as T
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        enableEdgeToEdge(
            navigationBarStyle = SystemBarStyle.light(0, 0)
        )
        setContent {
            val navController = rememberNavController()
            NavHost(
                navController = navController,
                startDestination = MainScreen
            ) {
                composable<MainScreen> {
                    MainScreen(navController, mainScreenViewModel)
                }
                composable<EditorScreen> {
                    EditorScreen(
                        navController,
                        editorScreenViewModel,
                        it.toRoute<EditorScreen>().noteId
                    )
                }
            }
        }
    }
}

//@Preview
@Composable
fun MainScreen(navController: NavController, viewModel: MainScreenViewModel = viewModel()) {
    val noteEntities = viewModel.notes.collectAsState().value
    AdvancedNotesRemasteredTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentColor = MaterialTheme.colorScheme.primary,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = {
                        navController.navigate(EditorScreen())
                    },
                    modifier = Modifier.size(64.dp),
                    shape = RoundedCornerShape(32.dp),
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    elevation = FloatingActionButtonDefaults.elevation(4.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.add_icon),
                        contentDescription = null
                    )
                }
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = stringResource(R.string.my_notes),
                        modifier = Modifier
                            .padding(start = 8.dp)
                            .weight(1F),
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Icon(
                        painter = painterResource(R.drawable.menu_icon),
                        contentDescription = null
                    )
                }
                SearchBarPlaceholder(Modifier.padding(vertical = 16.dp))
                LazyVerticalStaggeredGrid(
                    columns = StaggeredGridCells.Fixed(2),
                    modifier = Modifier.fillMaxSize(),
                    verticalItemSpacing = 16.dp,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(noteEntities) { noteEntity ->
                        NoteCard(
                            noteEntity = noteEntity,
                            onClick = { navController.navigate(EditorScreen(noteEntity.id)) },
                            onFavouriteClick = { viewModel.switchFavourite(noteEntity) }
                        )
                    }
                }
            }
        }
    }
}

//@Preview
@SuppressLint("RememberReturnType")
@Composable
fun EditorScreen(
    navController: NavController,
    viewModel: EditorScreenViewModel = viewModel(),
    noteId: Int?
) {
    remember { viewModel.setContent(noteId) }
    AdvancedNotesRemasteredTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(R.drawable.back_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            navController.navigateUp()
                        }
                    )
                    if (viewModel.title.isNotEmpty() || viewModel.text.isNotEmpty()) {
                        Icon(
                            painter = painterResource(R.drawable.check),
                            contentDescription = null,
                            modifier = Modifier.clickable {
                                viewModel.upsert()
                                navController.navigateUp()
                            },
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                        .weight(1F)
                        .verticalScroll(rememberScrollState())
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(viewModel.color)
                        )
                        BasicTextField(
                            value = viewModel.title,
                            onValueChange = {
                                viewModel.title =
                                    if (it.length <= 20) it else it.substring(0, 20)
                            },
                            modifier = Modifier.padding(start = 4.dp),
                            textStyle = MaterialTheme.typography.titleLarge,
                            singleLine = true,
                            decorationBox = { innerTextField ->
                                if (viewModel.title.isEmpty()) {
                                    Text(
                                        text = stringResource(R.string.note_title_hint),
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        style = MaterialTheme.typography.titleLarge
                                    )
                                }
                                innerTextField()
                            }
                        )
                    }
                    BasicTextField(
                        value = viewModel.text,
                        onValueChange = { viewModel.text = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        textStyle = MaterialTheme.typography.bodyLarge,
                        decorationBox = { innerTextField ->
                            if (viewModel.text.isEmpty()) {
                                Text(
                                    text = stringResource(R.string.note_text_hint),
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    style = MaterialTheme.typography.bodyLarge
                                )
                            }
                            innerTextField()
                        }
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Icon(
                        painter = painterResource(R.drawable.color_icon),
                        contentDescription = null
                    )
                    Icon(
                        painter = painterResource(R.drawable.image_icon),
                        contentDescription = null
                    )
                    Icon(
                        painter = painterResource(R.drawable.delete_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.delete()
                            navController.navigateUp()
                        },
                        tint = Color(192, 64, 64, 255)

                    )
                    Icon(
                        painter = painterResource(if (viewModel.isFavourite) R.drawable.favourite_filled_icon else R.drawable.favourite_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable {
                            viewModel.isFavourite = !viewModel.isFavourite
                        },
                        tint = if (viewModel.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Icon(
                        painter = painterResource(R.drawable.share_icon),
                        contentDescription = null
                    )
                }
            }
        }
    }
}

@Composable
fun SearchBarPlaceholder(modifier: Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(32.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(R.drawable.search_icon),
                contentDescription = null,
                modifier = Modifier.padding(8.dp)
            )
            Text(
                text = stringResource(R.string.search_hint),
                style = MaterialTheme.typography.titleMedium
            )
        }
    }
}

@Composable
fun NoteCard(noteEntity: NoteEntity, onClick: () -> Unit, onFavouriteClick: () -> Unit) {
    val note = Note(noteEntity)
    Card(
        modifier = Modifier.clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
            disabledContentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Column {
//            Image(
//                painter = painterResource(R.drawable.navy),
//                contentDescription = null
//            )
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        modifier = Modifier.weight(1F),
                        text = note.title,
                        color = MaterialTheme.colorScheme.onSurface,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Icon(
                        modifier = Modifier
                            .align(Alignment.Top)
                            .clickable { onFavouriteClick() },
                        painter = painterResource(if (note.isFavourite) R.drawable.star_filled_icon else R.drawable.star_icon),
                        contentDescription = null,
                        tint = if (note.isFavourite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(4.dp)
                        .background(note.color)
                )
                Text(
                    text = note.text,
                    color = MaterialTheme.colorScheme.onSurface,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).format(note.date),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Preview
@Composable
fun NoteCardPreview() {
    AdvancedNotesRemasteredTheme {
        Box(Modifier.size(256.dp)) {
            NoteCard(
                noteEntity = Note(
                    "Amazing trip",
                    "The trip was very lovely!",
                    Color.Green,
                    LocalDateTime.of(2024, 8, 21, 15, 41, 38, 13),
                ).toEntity(),
                onClick = {},
                onFavouriteClick = {}
            )
        }
    }
}

@Serializable
object MainScreen

@Serializable
data class EditorScreen(val noteId: Int? = null)