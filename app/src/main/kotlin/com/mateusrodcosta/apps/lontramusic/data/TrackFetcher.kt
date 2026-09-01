package com.mateusrodcosta.apps.lontramusic.data

import androidx.core.net.toUri
import coil3.ImageLoader
import coil3.asImage
import coil3.decode.DataSource
import coil3.fetch.FetchResult
import coil3.fetch.Fetcher
import coil3.fetch.ImageFetchResult
import coil3.key.Keyer
import coil3.request.Options
import coil3.size.pxOrElse
import java.io.File

class TrackFetcher(
    private val data: Track,
    private val options: Options,
    private val imageLoader: ImageLoader,
) : Fetcher {

    override suspend fun fetch(): FetchResult? {
        val context = options.context

        // 1. Direct Routing: If it's an external file, let Coil handle it natively
        if (data.artworkType == ArtworkType.EXTERNAL && data.artworkSourcePath != null) {
            val file = File(data.artworkSourcePath)
            if (file.exists()) {
                return imageLoader.components.newFetcher(file, options, imageLoader)?.first?.fetch()
            }
        }

        // 2. Direct Routing: If it's MediaStore, let Coil handle the Uri natively
        if (data.artworkType == ArtworkType.MEDIA_STORE && data.artworkSourcePath != null) {
            val uri = data.artworkSourcePath.toUri()
            return imageLoader.components.newFetcher(uri, options, imageLoader)?.first?.fetch()
        }

        // 3. Fallback/Embedded: Use our custom loader for Opus/Ogg or if direct routing failed
        val sizeLimit = options.size.width.pxOrElse { 0 }.coerceAtLeast(options.size.height.pxOrElse { 0 })
            .takeIf { it > 0 }

        val bitmap = loadArtwork(
            context = context,
            id = data.id,
            path = data.path,
            highRes = true,
            sizeLimit = sizeLimit,
            crop = true
        ) ?: return null

        return ImageFetchResult(
            image = bitmap.asImage(),
            isSampled = true,
            dataSource = DataSource.DISK
        )
    }

    class Factory : Fetcher.Factory<Track> {
        override fun create(data: Track, options: Options, imageLoader: ImageLoader): Fetcher {
            return TrackFetcher(data, options, imageLoader)
        }
    }
}

class TrackKeyer : Keyer<Track> {
    override fun key(data: Track, options: Options): String {
        val folder = data.path.substringBeforeLast('/', "")
        return if (data.hasArtwork) {
            if (data.artworkHash != null) {
                // Use the actual artwork hash for perfect de-duplication of identical embedded art
                "embedded_${data.artworkHash}"
            } else if (data.vibrantColor != null || data.mutedColor != null) {
                // Fallback to palette signature if hash is missing (e.g. from an old index)
                "embedded_${folder}_${data.album}_${data.vibrantColor?.value}_${data.mutedColor?.value}"
            } else {
                // Last resort: unique track key
                "track_${data.id}_${data.version}"
            }
        } else {
            // No embedded art: use the folder path as the key
            "folder_$folder"
        }
    }
}
