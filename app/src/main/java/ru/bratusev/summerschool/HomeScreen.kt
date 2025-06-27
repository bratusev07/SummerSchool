package ru.bratusev.summerschool

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import ru.bratusev.summerschool.MainActivity.Companion.database
import ru.bratusev.summerschool.data.TaskEntity

val globalTasks = listOf<Task>(
    Task(0, "Title 0", "Description 0"),
    Task(1, "Title 1", "Description 1"),
    Task(2, "Title 2", "Description 2"),
    Task(3, "Title 3", "Description 3"),
    Task(4, "Title 4", "Description 4"),
    Task(5, "Title 5", "Description 5"),
    Task(6, "Title 6", "Description 6"),
    Task(7, "Title 7", "Description 7"),
    Task(8, "Title 8", "Description 8"),
    Task(9, "Title 9", "Description 9"),
    Task(10, "Title 10", "Description 10"),
    Task(11, "Title 11", "Description 11"),
    Task(12, "Title 12", "Description 12"),
    Task(13, "Title 13", "Description 13"),
    Task(14, "Title 14", "Description 14"),
    Task(15, "Title 15", "Description 15"),
    Task(16, "Title 16", "Description 16"),
    Task(17, "Title 17", "Description 17"),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        launch {
            database.taskDao().getAllItems().collect { dbTasks ->
                tasks = dbTasks.map { item ->
                    Task(item.id, item.title, item.description)
                }
            }
        }

        if (database.taskDao().getAllItems().first().isEmpty()) {
            saveTasksToDb(globalTasks)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text("Task list") },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                coroutineScope.launch {
                    val newIdForTask = (tasks.maxOfOrNull { it.id } ?: 0L) + 1
                    val newTask = TaskEntity(
                        id = newIdForTask,
                        title = "Title $newIdForTask",
                        description = "Description $newIdForTask"
                    )
                    database.taskDao().insertItems(listOf(newTask))
                }
            }) {
                Icon(Icons.Filled.Add, contentDescription = "Add task")
            }
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskItem(task = task, modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}

@Composable
fun TaskItem(modifier: Modifier = Modifier, task: Task) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(task.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(task.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private suspend fun saveTasksToDb(tasks: List<Task>) {
    val entities =
        tasks.map { TaskEntity(id = it.id, title = it.title, description = it.description) }

    database.taskDao().insertItems(entities)
}