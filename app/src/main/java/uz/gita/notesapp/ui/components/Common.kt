package uz.gita.notesapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import uz.gita.notesapp.R
import uz.gita.notesapp.ui.theme.NotesTheme

/** The launcher artwork on a rounded tile, used on the auth screens. */
@Composable
fun AppLogo(size: Dp, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(size * 0.3f))
            .background(MaterialTheme.colorScheme.primary)
    ) {
        Image(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            contentDescription = null,
            modifier = Modifier.size(size)
        )
    }
}

@Composable
fun EmptyState(text: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(modifier = Modifier.size(width = 120.dp, height = 100.dp)) {
            Box(
                modifier = Modifier
                    .offset(x = 4.dp, y = 16.dp)
                    .size(width = 78.dp, height = 74.dp)
                    .rotate(-9f)
                    .clip(RoundedCornerShape(16.dp))
                    .background(NotesTheme.colors.surface2)
            )
            Column(
                modifier = Modifier
                    .offset(x = 26.dp, y = 4.dp)
                    .size(width = 84.dp, height = 90.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                Line(fraction = 0.6f, height = 7.dp, accent = true)
                Line(fraction = 0.9f, height = 5.dp)
                Line(fraction = 0.7f, height = 5.dp)
            }
        }
        Text(
            text = text,
            style = MaterialTheme.typography.titleSmall,
            color = NotesTheme.colors.muted
        )
    }
}

@Composable
private fun Line(fraction: Float, height: Dp, accent: Boolean = false) {
    Box(
        modifier = Modifier
            .fillMaxWidth(fraction)
            .height(height)
            .clip(RoundedCornerShape(4.dp))
            .background(if (accent) MaterialTheme.colorScheme.primary else NotesTheme.colors.surface2)
    )
}

@Composable
fun Avatar(name: String, size: Dp, modifier: Modifier = Modifier, imageUrl: String? = null) {
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(50))
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.initials(),
            color = MaterialTheme.colorScheme.primary,
            style = if (size > 56.dp) MaterialTheme.typography.headlineSmall else MaterialTheme.typography.labelMedium
        )
        if (imageUrl != null) {
            AsyncImage(
                model = imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(size)
            )
        }
    }
}

private fun String.initials(): String =
    split(" ", ".", "_").filter { it.isNotBlank() }.take(2)
        .joinToString("") { it.first().uppercase() }
        .ifEmpty { "?" }
