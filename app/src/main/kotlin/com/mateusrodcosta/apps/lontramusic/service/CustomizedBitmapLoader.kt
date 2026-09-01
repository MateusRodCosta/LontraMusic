package com.mateusrodcosta.apps.lontramusic.service

import android.content.ContentUris
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.net.toUri
import androidx.media3.common.MediaMetadata
import androidx.media3.common.util.BitmapLoader
import androidx.media3.common.util.UnstableApi
import coil3.SingletonImageLoader
import coil3.request.ImageRequest
import coil3.toBitmap
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.mateusrodcosta.apps.lontramusic.URI_KEY
import com.mateusrodcosta.apps.lontramusic.globals.GlobalData
import java.util.concurrent.Executors
import kotlinx.coroutines.runBlocking

@UnstableApi
class CustomizedBitmapLoader(private val context: Context) : BitmapLoader {
    private val listeningExecutorService = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor())
    private val imageLoader = SingletonImageLoader.get(context)

    override fun supportsMimeType(mimeType: String): Boolean = true

    override fun decodeBitmap(data: ByteArray): ListenableFuture<Bitmap> {
        return listeningExecutorService.submit<Bitmap> {
            runBlocking {
                val request = ImageRequest.Builder(context)
                    .data(data)
                    .build()
                imageLoader.execute(request).image!!.toBitmap()
            }
        }
    }

    override fun loadBitmap(uri: Uri): ListenableFuture<Bitmap> {
        return listeningExecutorService.submit<Bitmap> {
            runBlocking {
                val request = ImageRequest.Builder(context)
                    .data(uri)
                    .build()
                imageLoader.execute(request).image!!.toBitmap()
            }
        }
    }

    override fun loadBitmapFromMetadata(metadata: MediaMetadata): ListenableFuture<Bitmap>? {
        val uri = metadata.extras?.getString(URI_KEY)?.toUri() ?: return null

        return listeningExecutorService.submit<Bitmap> {
            runBlocking {
                val id = try { ContentUris.parseId(uri) } catch (_: Exception) { -1L }
                val track = if (id != -1L) GlobalData.libraryIndex.value.tracks[id] else null

                val request = ImageRequest.Builder(context)
                    .data(track ?: uri)
                    .build()
                imageLoader.execute(request).image!!.toBitmap()
            }
        }
    }
}
