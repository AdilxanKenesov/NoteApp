package uz.gita.notesapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import uz.gita.notesapp.domain.model.NoteImage
import uz.gita.notesapp.domain.model.NoteType
import uz.gita.notesapp.domain.model.NoteUIData
import uz.gita.notesapp.ui.theme.NotesTheme
import uz.gita.notesapp.utils.toCardDate
import java.io.File

@Composable
fun TypeDot(color: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(color)
    )
}

@Composable
fun TypeLabel(type: NoteType, modifier: Modifier = Modifier, suffix: String = "") {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        TypeDot(color = NotesTheme.colors.typeColor(type))
        Text(
            text = type.title + suffix,
            style = MaterialTheme.typography.labelSmall,
            color = NotesTheme.colors.faint
        )
    }
}

@Composable
fun ImageThumb(
    path: String,
    size: Dp,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null
) {
    AsyncImage(
        model = if (path.startsWith("/")) File(path) else path,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        modifier = modifier
            .size(size)
            .clip(MaterialTheme.shapes.small)
            .background(NotesTheme.colors.surface2)
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
    )
}

@Composable
fun NoteCard(
    note: NoteUIData,
    image: NoteImage?,
    onClick: () -> Unit,
    onFavourite: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(MaterialTheme.shapes.large)
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .clickable(onClick = onClick)
            .padding(start = 16.dp, top = 14.dp, bottom = 14.dp, end = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (image != null) {
            ImageThumb(path = image.path, size = 56.dp)
            Spacer(modifier = Modifier.width(12.dp))
        }

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                TypeLabel(type = note.type, modifier = Modifier.weight(1f))
                Text(
                    text = note.createdAt.toCardDate(),
                    style = MaterialTheme.typography.labelSmall,
                    color = NotesTheme.colors.faint,
                    maxLines = 1
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            if (note.description.isNotBlank() && image == null) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = note.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = NotesTheme.colors.muted,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        IconButton(onClick = onFavourite, modifier = Modifier.align(Alignment.Top)) {
            Icon(
                imageVector = if (note.favourite) Icons.Filled.Star else Icons.Outlined.StarBorder,
                contentDescription = if (note.favourite) "Remove from favourites" else "Add to favourites",
                tint = if (note.favourite) NotesTheme.colors.star else NotesTheme.colors.faint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
