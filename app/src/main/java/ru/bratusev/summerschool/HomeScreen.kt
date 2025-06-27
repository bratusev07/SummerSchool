package ru.bratusev.summerschool

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import ru.bratusev.summerschool.MainActivity.Companion.database
import ru.bratusev.summerschool.data.TaskEntity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import androidx.compose.runtime.rememberUpdatedState
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
    val coroutineScope = rememberCoroutineScope()
    var showAddTaskDialog by remember { mutableStateOf(false) }
    var selectedTask by remember { mutableStateOf<Task?>(null) }
    val sheetState = rememberModalBottomSheetState()
    var showTaskDetailsSheet by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editTitle by remember { mutableStateOf("") }
    var editDescription by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        database.taskDao().getAllItems().collect { dbTasks ->
            val updatedTasks = dbTasks.map { item ->
                Task(item.id, item.title, item.description, item.status, item.creationDate)
            }
            tasks = updatedTasks

            val newTasks = updatedTasks.filter { it.status == TaskStatus.NEW }
            if (newTasks.isNotEmpty()) {
                newTasks.forEach { task ->
                    val taskEntity = TaskEntity(
                        id = task.id,
                        title = task.title,
                        description = task.description,
                        status = TaskStatus.TODO,
                        creationDate = task.creationDate
                    )
                    database.taskDao().updateItem(taskEntity)
                }
            }
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { Text(stringResource(id = R.string.task_list)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddTaskDialog = true }) {
                Icon(Icons.Filled.Add, contentDescription = stringResource(id = R.string.add_task))
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
                TaskItem(
                    task = task,
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickable {
                            selectedTask = task
                            showTaskDetailsSheet = true
                        }
                )
            }
        }
    }

    if (showAddTaskDialog) {
        AddTaskDialog(
            onDismiss = { showAddTaskDialog = false },
            onTaskAdd = { title, description ->
                coroutineScope.launch {
                    val newTask = TaskEntity(
                        title = title,
                        description = description,
                        status = TaskStatus.NEW,
                        creationDate = System.currentTimeMillis()
                    )
                    database.taskDao().insertItem(newTask)
                    showAddTaskDialog = false
                }
            }
        )
    }

    if (showTaskDetailsSheet) {
        ModalBottomSheet(
            onDismissRequest = { showTaskDetailsSheet = false },
            sheetState = sheetState
        ) {
            selectedTask?.let { task ->
                TaskDetailsSheet(
                    task = task,
                    onDelete = {
                        coroutineScope.launch {
                            database.taskDao().deleteItem(
                                TaskEntity(
                                    id = task.id,
                                    title = task.title,
                                    description = task.description,
                                    status = task.status,
                                    creationDate = task.creationDate
                                )
                            )
                            showTaskDetailsSheet = false
                        }
                    },
                    onStatusChange = { newStatus ->
                        coroutineScope.launch {
                            val updatedTask = task.copy(status = newStatus)
                            database.taskDao().updateItem(
                                TaskEntity(
                                    id = updatedTask.id,
                                    title = updatedTask.title,
                                    description = updatedTask.description,
                                    status = updatedTask.status,
                                    creationDate = updatedTask.creationDate
                                )
                            )
                            selectedTask = updatedTask
                        }
                    },
                    onEditClick = {
                        editTitle = task.title
                        editDescription = task.description
                        showEditDialog = true
                    }
                )
            }
        }
    }

    if (showEditDialog && selectedTask != null) {
        EditTaskDialog(
            initialTitle = editTitle,
            initialDescription = editDescription,
            onDismiss = { showEditDialog = false },
            onSave = { newTitle, newDescription ->
                coroutineScope.launch {
                    val updatedTask = selectedTask!!.copy(title = newTitle, description = newDescription)
                    database.taskDao().updateItem(
                        TaskEntity(
                            id = updatedTask.id,
                            title = updatedTask.title,
                            description = updatedTask.description,
                            status = updatedTask.status,
                            creationDate = updatedTask.creationDate
                        )
                    )
                    selectedTask = updatedTask
                    showEditDialog = false
                    showTaskDetailsSheet = false
                }
            }
        )
    }
}

@Composable
fun AddTaskDialog(
    onDismiss: () -> Unit,
    onTaskAdd: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.add_new_task)) },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.title)) }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.description)) }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onTaskAdd(title, description) }) {
                Text(stringResource(id = R.string.add))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsSheet(
    task: Task,
    onDelete: () -> Unit,
    onStatusChange: (String) -> Unit,
    onEditClick: () -> Unit
) {
    var isStatusDropdownExpanded by remember { mutableStateOf(false) }
    val statuses = listOf(TaskStatus.TODO, TaskStatus.IN_PROGRESS, TaskStatus.DONE)
    val dateFormat = remember { SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(task.title, style = MaterialTheme.typography.titleLarge)
            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = stringResource(id = R.string.edit_task))
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(id = R.string.delete_task))
                }
            }
        }
        Spacer(Modifier.height(8.dp))
        Text(task.description, style = MaterialTheme.typography.bodyMedium)
        Spacer(Modifier.height(16.dp))

        Box {
            ExposedDropdownMenuBox(
                expanded = isStatusDropdownExpanded,
                onExpandedChange = { isStatusDropdownExpanded = !isStatusDropdownExpanded }
            ) {
                TextField(
                    value = getLocalizedStatus(status = task.status),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(id = R.string.status)) },
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = isStatusDropdownExpanded)
                    },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = isStatusDropdownExpanded,
                    onDismissRequest = { isStatusDropdownExpanded = false }
                ) {
                    statuses.forEach { status ->
                        DropdownMenuItem(
                            text = { Text(getLocalizedStatus(status = status)) },
                            onClick = {
                                onStatusChange(status)
                                isStatusDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(Modifier.height(16.dp))
        Text(
            text = stringResource(id = R.string.created, dateFormat.format(Date(task.creationDate))),
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.align(Alignment.End)
        )
    }
}

@Composable
fun EditTaskDialog(
    initialTitle: String,
    initialDescription: String,
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit
) {
    var title by remember { mutableStateOf(initialTitle) }
    var description by remember { mutableStateOf(initialDescription) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(id = R.string.edit_task)) },
        text = {
            Column {
                TextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(stringResource(id = R.string.title)) }
                )
                Spacer(Modifier.height(8.dp))
                TextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(stringResource(id = R.string.description)) }
                )
            }
        },
        confirmButton = {
            Button(onClick = { onSave(title, description) }) {
                Text(stringResource(id = R.string.save))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(id = R.string.cancel))
            }
        }
    )
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