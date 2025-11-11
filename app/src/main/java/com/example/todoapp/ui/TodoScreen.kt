package com.example.todoapp.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.todoapp.viewmodel.TodoViewModel
enum class Filter { ALL, ACTIVE, DONE }

@Composable
fun TodoScreen(vm: TodoViewModel = viewModel()) {
    val todos by vm.todos.collectAsState()

    var text by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf(Filter.ALL) }

    val visibleTodos = when (filter) {
        Filter.ALL -> todos
        Filter.ACTIVE -> todos.filter { !it.isDone }
        Filter.DONE -> todos.filter { it.isDone }
    }

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
                val buttonText = when (f) {
                    Filter.ALL -> "Semua"
                    Filter.ACTIVE -> "Aktif"
                    Filter.DONE -> "Selesai"
                }
                if (selected) {
                    FilledTonalButton(onClick = { filter = f }) { Text(buttonText) }
                } else {
                    OutlinedButton(onClick = { filter = f }) { Text(buttonText) }
                }
            }
        }

        Divider(Modifier.padding(vertical = 8.dp))

        LazyColumn {
            items(visibleTodos) { todo ->
                TodoItem(
                    todo = todo,
                    onToggle = { vm.toggleTask(todo.id) },
                    onDelete = { vm.deleteTask(todo.id) }
                )
            }
        }
    }
}