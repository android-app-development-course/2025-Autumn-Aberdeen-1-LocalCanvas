package com.example.mylocalcanvas.data

import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object TaskHistoryStore {
    private val moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    private val listType = Types.newParameterizedType(
        List::class.java,
        TaskHistoryItem::class.java
    )
    private val adapter = moshi.adapter<List<TaskHistoryItem>>(listType)

    private fun historyFile(context: Context): File {
        return File(context.filesDir, "task_history.json")
    }

    suspend fun load(context: Context): List<TaskHistoryItem> = withContext(Dispatchers.IO) {
        val file = historyFile(context)
        if (!file.exists()) return@withContext emptyList()
        val json = file.readText()
        adapter.fromJson(json) ?: emptyList()
    }

    suspend fun upsert(context: Context, item: TaskHistoryItem) = withContext(Dispatchers.IO) {
        val existing = load(context)
        val index = existing.indexOfFirst { it.id == item.id }
        val updated = if (index >= 0) {
            existing.toMutableList().apply { set(index, item) }
        } else {
            listOf(item) + existing
        }
        val json = adapter.toJson(updated)
        historyFile(context).writeText(json)
    }
}
