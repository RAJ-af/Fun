package com.itsraj.funkytalk.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage

@Composable
fun CircularFlag(
    code: String,
    size: Dp = 20.dp,
    modifier: Modifier = Modifier
) {
    AsyncImage(
        model = "https://hatscripts.github.io/circle-flags/flags/${code.lowercase()}.svg",
        contentDescription = null,
        modifier = modifier
            .size(size)
            .clip(CircleShape)
    )
}
