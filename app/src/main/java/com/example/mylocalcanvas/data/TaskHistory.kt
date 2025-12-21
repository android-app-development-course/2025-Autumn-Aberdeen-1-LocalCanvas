package com.example.mylocalcanvas.data

enum class TaskStatus {
    RUNNING,
    COMPLETED,
    FAILED
}

data class TaskHistoryItem(
    val id: String,
    val title: String,
    val workflowType: String,
    val strength: Int,
    val steps: Int,
    val timeMillis: Long,
    val status: TaskStatus,
    val resultUrl: String? = null
)
