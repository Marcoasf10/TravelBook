package pt.ipleiria.travelbook.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

@Composable
fun DraggableNote(
    note: String,
    notes: MutableList<String>,
    itemHeight: Float = 60f,
    onValueChange: (String) -> Unit = {},
    onRemove: () -> Unit = {}
) {
    var offsetY by remember { mutableStateOf(0f) }
    val animatedOffset by animateFloatAsState(targetValue = offsetY)

    var isDragging by remember { mutableStateOf(false) }
    val backgroundColor = if (isDragging) MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    else MaterialTheme.colorScheme.background

    val density = LocalDensity.current
    val animatedDp = with(density) { animatedOffset.toDp() }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .offset(y = animatedDp)
            .background(color = backgroundColor, shape = MaterialTheme.shapes.small)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { offsetY = 0f; isDragging = false },
                    onDragCancel = { offsetY = 0f; isDragging = false },
                    onDrag = { change, dragAmount ->
                        change.consume()
                        val tentativeOffset = offsetY + dragAmount.y
                        val maxOffset = (notes.size - 1) * itemHeight
                        offsetY = tentativeOffset.coerceIn(-itemHeight * notes.indexOf(note), maxOffset - itemHeight * notes.indexOf(note))

                        val index = notes.indexOf(note)
                        val newIndex = ((index + (offsetY / itemHeight).toInt()).coerceIn(0, notes.size - 1))
                        if (newIndex != index) {
                            notes.removeAt(index)
                            notes.add(newIndex, note)
                            offsetY = 0f
                        }
                    }
                )
            }
            .padding(vertical = 4.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = note,
                onValueChange = { onValueChange(it) },
                modifier = Modifier.weight(1f)
            )
            IconButton(onClick = { onRemove() }) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}