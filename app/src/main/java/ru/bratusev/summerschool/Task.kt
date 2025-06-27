package ru.bratusev.summerschool

data class Task(
    val id: Long,
    val title: String,
    val description: String,
    val status: String,
    val creationDate: Long
)

object TaskStatus {
    const val NEW = "new"
    const val TODO = "todo"
    const val IN_PROGRESS = "in progress"
    const val DONE = "done"
}
