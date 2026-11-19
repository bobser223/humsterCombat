package com.example.cryptoscam.presentation.compose

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun StatusMessage(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(text, modifier = modifier)
}