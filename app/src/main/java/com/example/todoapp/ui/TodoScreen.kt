package com.example.todoapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.viewmodel.TodoViewModel
import com.example.todoapp.model.Todo

enum class Filter { ALL, ACTIVE, DONE }

@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {
    val todos by vm.todos.collectAsState()

    var text by rememberSaveable { mutableStateOf("") }
    var query by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(Filter.ALL) }

    val visibleTodos = when (filter) {
        Filter.ALL -> todos
        Filter.ACTIVE -> todos.filter { !it.isDone }
        Filter.DONE -> todos.filter { it.isDone }
    }

    val filteredTodos = visibleTodos.filter { it.title.contains(query, ignoreCase = true) }

    Column(Modifier.padding(16.dp)) {

        OutlinedTextField(
            value = text,
            onValueChange = { text = it },
            label = { Text("Tambah tugas...") },
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = {
                if (text.isNotBlank()) {
                    vm.addTask(text.trim())
                    text = ""
                }
            },
            modifier = Modifier.padding(vertical = 8.dp)
        ) { Text("Tambah") }

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Filter.values().forEach { f ->
                val selected = filter == f
                val label = when (f) {
                    Filter.ALL -> "Semua"
                    Filter.ACTIVE -> "Aktif"
                    Filter.DONE -> "Selesai"
                }
                if (selected) {
                    FilledTonalButton(onClick = { filter = f }) { Text(label) }
                } else {
                    OutlinedButton(onClick = { filter = f }) { Text(label) }
                }
            }
        }

        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Cari tugas") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp)
        )

        Divider(Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(filteredTodos) { todo: Todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.id) },
                    onDelete = { vm.deleteTask(todo.id) }
                )
            }
        }
    }
}
