package com.mateusrodcosta.apps.lontramusic.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.ColorPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImagePainter
import coil3.compose.rememberAsyncImagePainter
import coil3.toBitmap
import com.mateusrodcosta.apps.lontramusic.data.ArtworkColorPreference
import com.mateusrodcosta.apps.lontramusic.data.ArtworkModel
import com.mateusrodcosta.apps.lontramusic.data.getArtworkColor
import com.mateusrodcosta.apps.lontramusic.ui.theme.LocalDarkTheme

@Immutable
sealed class Artwork {
    abstract fun getColor(artworkColorPreference: ArtworkColorPreference): Color

    @Immutable
    data class Track(val track: com.mateusrodcosta.apps.lontramusic.data.Track) : Artwork() {
        override fun getColor(artworkColorPreference: ArtworkColorPreference): Color {
            return track.getArtworkColor(artworkColorPreference)
        }
    }

    @Immutable
    data class Icon(val icon: ImageVector, val color: Color) : Artwork() {
        override fun getColor(artworkColorPreference: ArtworkColorPreference): Color {
            return color
        }
    }
}

@Composable
fun ArtworkImage(
    artwork: Artwork,
    artworkColorPreference: ArtworkColorPreference,
    shape: Shape,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    sticky: Boolean = false,
) {
    val darkTheme = LocalDarkTheme.current
    val icon =
        when (artwork) {
            is Artwork.Track -> Icons.Outlined.MusicNote
            is Artwork.Icon -> artwork.icon
        }

    Box(modifier = modifier.clip(shape)) {
        val color =
            remember(artwork, artworkColorPreference) {
                artwork.getColor(artworkColorPreference)
            }

        // Persistent image and color management
        // Initialize from global buffers to ensure zero-frame gap on creation
        var displayedImage by remember { mutableStateOf(if (sticky) lastSuccessfulImage else null) }
        var displayedColor by remember { mutableStateOf(if (sticky) lastSuccessfulColor else null) }

        if (artwork is Artwork.Track && artwork.track.hasArtwork) {
            val artworkModel = remember(artwork.track.artworkHash, artwork.track.artworkSourcePath, artwork.track.artworkType) {
                ArtworkModel(
                    type = artwork.track.artworkType,
                    source = artwork.track.artworkSourcePath,
                    hash = artwork.track.artworkHash,
                    id = artwork.track.id,
                    path = artwork.track.path
                )
            }

            val painter = rememberAsyncImagePainter(
                model = artworkModel,
                placeholder = ColorPainter(color),
            )
            val state by painter.state.collectAsState()

            // Update pixels as soon as Success is reached
            LaunchedEffect(state, color) {
                val currentState = state
                if (currentState is AsyncImagePainter.State.Success) {
                    val bitmap = currentState.result.image.toBitmap().asImageBitmap()
                    displayedImage = bitmap
                    displayedColor = color
                    if (sticky) {
                        lastSuccessfulImage = bitmap
                        lastSuccessfulColor = color
                    }
                }
            }

            val imageToRender = displayedImage
            val colorToRender = displayedColor ?: color

            // 1. Bottom Layer: Underlay Placeholder
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(
                            if (darkTheme) lerp(colorToRender, Color.Black, 0.4f)
                            else lerp(colorToRender, Color.White, 0.9f)
                        )
            )

            // 2. Image Layer: Uses static Bitmap instead of stateful Painter
            if (imageToRender != null) {
                Image(
                    painter = BitmapPainter(imageToRender),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = contentScale,
                )
            } else {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                    Icon(icon, null, tint = colorToRender ?: color, modifier = Modifier.fillMaxSize(0.5f))
                }
            }
        } else {
            // No artwork: show current track's theme instantly
            Box(
                modifier =
                    Modifier.fillMaxSize()
                        .background(
                            if (darkTheme) lerp(color, Color.Black, 0.4f)
                            else lerp(color, Color.White, 0.9f)
                        ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = color, modifier = Modifier.fillMaxSize(0.5f))
            }
        }
    }
}

// Global buffers to hold the last successfully loaded pixels for "sticky" transitions.
private var lastSuccessfulImage: ImageBitmap? by mutableStateOf(null)
private var lastSuccessfulColor: Color? by mutableStateOf(null)
