package com.griffith.habittracker.View

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import com.griffith.habittracker.Controller.TaskController
import com.griffith.habittracker.Model.Task
import androidx.compose.material.icons.filled.Delete

@Composable
fun TaskSection() {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }  // Show "Add Task" popup?
    var newTaskTitle by remember { mutableStateOf("") }  // Text user types for new task

    // Task to be deleted (if null, no deletion is pending)
    var taskToDelete by remember { mutableStateOf<Task?>(null) }  // Which task to delete?

    // Use this state to force recomposition when tasks change
    var refreshTrigger by remember { mutableStateOf(0) }
    val tasks by remember(refreshTrigger) {
        mutableStateOf(TaskController.getTasks(context))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Header with title and add button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Daily Tasks",
                    style = MaterialTheme.typography.titleLarge
                )

                // Add button now in the header
                IconButton(onClick = { showAddDialog = true }) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add Task",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            // Task list
            LazyColumn {
                items(tasks) { task ->
                    TaskItem(
                        task = task,
                        onToggle = {
                            TaskController.toggleTask(context, task.id)
                            refreshTrigger++
                        },
                        onDelete = {
                            // Set the task to delete, which will show the confirmation dialog
                            taskToDelete = task
                        },
                        onRename = { newTitle ->
                            // Rename the task
                            TaskController.renameTask(context, task.id, newTitle)
                            refreshTrigger++
                        }
                    )
                }
            }
        }
    }

    // Add task dialog
    if (showAddDialog) {
        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Add New Task") },
            text = {
                OutlinedTextField(
                    value = newTaskTitle,
                    onValueChange = { newTaskTitle = it },
                    label = { Text("Task") },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTaskTitle.isNotBlank()) {  // Check for empty tasks
                            TaskController.addTask(context, newTaskTitle)  // Save to storage
                            newTaskTitle = ""
                            showAddDialog = false
                            refreshTrigger++
                        }
                    }
                ) {
                    Text("Add")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // Confirmation dialog for task deletion
    if (taskToDelete != null) {
        AlertDialog(
            onDismissRequest = { taskToDelete = null },
            title = { Text("Delete Task") },
            text = { Text("Are you sure you want to delete '${taskToDelete!!.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        TaskController.deleteTask(context, taskToDelete!!.id)
                        taskToDelete = null
                        refreshTrigger++
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { taskToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TaskItem(task: Task, onToggle: () -> Unit, onDelete: () -> Unit, onRename: (String) -> Unit) {
    var showRenameDialog by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(  // Check or uncheck
            checked = task.isCompleted,
            onCheckedChange = { onToggle() }
        )

        Text(
            text = task.title,
            style = MaterialTheme.typography.bodyLarge,
            textDecoration = if (task.isCompleted) TextDecoration.LineThrough else TextDecoration.None,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp)
                .clickable { showRenameDialog = true } // Click to rename
        )

        if (task.isCompleted) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Completed",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(end = 8.dp)
            )
        }

        // Delete button only for custom tasks
        if (!task.isDefault) {
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete Task",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }

    // Rename dialog
    if (showRenameDialog) {
        var newTitle by remember { mutableStateOf(task.title) }

        AlertDialog(
            onDismissRequest = { showRenameDialog = false },
            title = { Text("Rename Task") },
            text = {
                OutlinedTextField(
                    value = newTitle,
                    onValueChange = { newTitle = it },
                    label = { Text("Task name") }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newTitle.isNotBlank()) {
                            onRename(newTitle)
                            showRenameDialog = false
                        }
                    }
                ) { Text("Save") }
            },
            dismissButton = {
                TextButton(onClick = { showRenameDialog = false }) { Text("Cancel") }
            }
        )
    }
}