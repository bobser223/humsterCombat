package com.example.cryptoscam.presentation.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun FreeBitcoinDialog(onDismiss: () -> Unit) {
    var revealed by remember { mutableStateOf(false) }
    var number by remember { mutableStateOf("") }
    var expiry by remember { mutableStateOf("") }
    var cvv by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = { Icon(Icons.Filled.MonetizationOn, contentDescription = null) },
        title = {
            Text(if (revealed) "Вас щойно заскамили" else "🎉 Безкоштовний біткоїн!")
        },
        text = {
            if (revealed) {
                Text(
                    "Ви мамонт чи бабця, пройдіть курси з інтернет гігієни"
                )
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Ви мільйонний відвідувач! Введіть дані картки, щоб отримати 1 BTC")
                    OutlinedTextField(
                        value = number, onValueChange = { number = it },
                        label = { Text("Номер картки") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = expiry, onValueChange = { expiry = it },
                        label = { Text("ММ/РР") }, singleLine = true
                    )
                    OutlinedTextField(
                        value = cvv, onValueChange = { cvv = it },
                        label = { Text("CVV") }, singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            if (revealed) {
                TextButton(onClick = onDismiss) { Text("Зрозуміло") }
            } else {
                TextButton(onClick = {
                    number = ""; expiry = ""; cvv = ""
                    revealed = true
                }) { Text("Отримати BTC") }
            }
        },
        dismissButton = {
            if (!revealed) TextButton(onClick = onDismiss) { Text("Ні, дякую") }
        }
    )
}