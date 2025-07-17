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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import ru.bratusev.summerschool.ui.theme.StatusDone
import ru.bratusev.summerschool.ui.theme.StatusInProgress
import ru.bratusev.summerschool.ui.theme.StatusTodo

/** Маппер статуса в его название для отображения */
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

/** Маппер статуса в его цвет для отображения */
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
    // Создание списка задач с разными данными
    val tasks by remember {
        mutableStateOf(
            listOf(
                Task(
                    id = 0,
                    title = "Title 0",
                    description = "Description 0",
                    status = TaskStatus.TODO,
                    creationDate = 0
                ), Task(
                    id = 1,
                    title = "Title 1",
                    description = "Description 1",
                    status = TaskStatus.TODO,
                    creationDate = 1
                ), Task(
                    id = 2,
                    title = "Title 2",
                    description = "Description 2",
                    status = TaskStatus.TODO,
                    creationDate = 2
                ), Task(
                    id = 3,
                    title = "Title 3",
                    description = "Description 3",
                    status = TaskStatus.TODO,
                    creationDate = 3
                ), Task(
                    id = 4,
                    title = "Title 4",
                    description = "Description 4",
                    status = TaskStatus.IN_PROGRESS,
                    creationDate = 4
                ), Task(
                    id = 5,
                    title = "Title 5",
                    description = "Description 5",
                    status = TaskStatus.IN_PROGRESS,
                    creationDate = 5
                ), Task(
                    id = 6,
                    title = "Title 6",
                    description = "Description 6",
                    status = TaskStatus.DONE,
                    creationDate = 6
                ), Task(
                    id = 7,
                    title = "Title 7",
                    description = "Description 7",
                    status = TaskStatus.DONE,
                    creationDate = 7
                ), Task(
                    id = 8,
                    title = "Title 8",
                    description = "Description 8",
                    status = TaskStatus.DONE,
                    creationDate = 8
                ), Task(
                    id = 9,
                    title = "Title 9",
                    description = "Description 9",
                    status = TaskStatus.NEW,
                    creationDate = 9
                )
            )
        )
    }


    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.task_list)) },
            )
        },
    ) { paddingValues ->
        // Прокручивающийся вертикальный список с задачами
        LazyColumn(
            Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            // Заполнение списка элементами
            // items - набор эллементов для отображения
            // key - способ получения уникальньго идентификатора для отслеживания изменений
            items(items = tasks, key = { it.id }) { task ->
                // Передача объекта задачи для заполнения её карточки данными
                TaskItem(
                    task = task, modifier = Modifier.padding(vertical = 8.dp)
                )
            }
        }
    }
}

/** Кастомная карточка для отображения одной задачи */
@Composable
fun TaskItem(modifier: Modifier = Modifier, task: Task) {
    Card(
        modifier = modifier.fillMaxWidth(),
        // elevation - закругление углов карточки
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        // получаем стандартные цвета для карточек и делаем такой же набор но с изменением
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
                // style - указываем стиль текста (высоту, толщину, начертание...)
                style = MaterialTheme.typography.bodySmall, modifier = Modifier.fillMaxWidth(),
                // textAlign - выравнивание именно текста внутри Text()
                textAlign = TextAlign.End
            )
        }
    }
}