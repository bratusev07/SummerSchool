package ru.bratusev.summerschool

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.bratusev.summerschool.MainActivity.Companion.database
import ru.bratusev.summerschool.ui.theme.StatusDone
import ru.bratusev.summerschool.ui.theme.StatusInProgress
import ru.bratusev.summerschool.ui.theme.StatusTodo

@Composable
fun getLocalizedStatus(status: String): String {
    return when (status) {
        TaskStatus.NEW -> stringResource(id = R.string.task_status_new)
        TaskStatus.TODO -> stringResource(id = R.string.task_status_todo)
        TaskStatus.IN_PROGRESS -> stringResource(id = R.string.task_status_in_progress)
        TaskStatus.DONE -> stringResource(id = R.string.task_status_done)
        else -> status
    }
}

@Composable
fun getTaskStatusColor(status: String): Color {
    return when (status) {
        TaskStatus.NEW -> StatusTodo
        TaskStatus.TODO -> StatusTodo
        TaskStatus.IN_PROGRESS -> StatusInProgress
        TaskStatus.DONE -> StatusDone
        else -> MaterialTheme.colorScheme.surface
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(modifier: Modifier = Modifier) {
    var tasks by remember { mutableStateOf(emptyList<Task>()) }

    // Чтение из БД всех элементов
    LaunchedEffect(Unit) {
        database.taskDao().getAllItems().collect { dbTasks ->
            // Преобразование ответа из БД в "обычные" задачи
            val updatedTasks = dbTasks.map { item ->
                Task(item.id, item.title, item.description, item.status, item.creationDate)
            }
            tasks = updatedTasks
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.task_list)) },
            )
        }
    ) { paddingValues ->
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            items(tasks, key = { it.id }) { task ->
                TaskItem(
                    task = task,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun TaskItem(modifier: Modifier = Modifier, task: Task) {
    Card(
        modifier = modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = getTaskStatusColor(status = task.status)
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(task.title, style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            Text(task.description, style = MaterialTheme.typography.bodyMedium)
            Spacer(Modifier.height(8.dp))
            Text(
                text = getLocalizedStatus(status = task.status),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.End
            )
        }
    }
}