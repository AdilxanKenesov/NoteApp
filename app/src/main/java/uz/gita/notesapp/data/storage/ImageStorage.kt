package uz.gita.notesapp.data.storage

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import androidx.exifinterface.media.ExifInterface
import androidx.core.net.toUri
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.max
import androidx.core.graphics.scale

/**
 * Keeps note images in the app's private storage: files/images/<noteId>/<uuid>.jpg.
 * Images are downscaled so a photo from the camera doesn't take several megabytes.
 */
@Singleton
class ImageStorage @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val root = File(context.filesDir, "images")

    fun save(noteId: String, uri: String): File {
        val source = uri.toUri()
        val bitmap = decodeScaled(source)?.let { rotateByExif(source, it) }
            ?: throw Exception("Couldn't read the image")

        val dir = File(root, noteId).apply { mkdirs() }
        val file = File(dir, "${UUID.randomUUID()}.jpg")
        file.outputStream().use { bitmap.compress(Bitmap.CompressFormat.JPEG, 85, it) }
        bitmap.recycle()
        return file
    }

    fun delete(path: String) {
        File(path).delete()
    }

    fun deleteNote(noteId: String) {
        File(root, noteId).deleteRecursively()
    }

    fun deleteAll() {
        root.deleteRecursively()
    }

    fun newCameraFile(): File {
        val dir = File(context.cacheDir, "camera").apply { mkdirs() }
        return File(dir, "${UUID.randomUUID()}.jpg")
    }

    private fun decodeScaled(uri: Uri): Bitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        context.contentResolver.openInputStream(uri)?.use { BitmapFactory.decodeStream(it, null, bounds) }

        var sample = 1
        while (max(bounds.outWidth, bounds.outHeight) / sample > MAX_SIDE * 2) sample *= 2

        val options = BitmapFactory.Options().apply { inSampleSize = sample }
        val decoded = context.contentResolver.openInputStream(uri)?.use {
            BitmapFactory.decodeStream(it, null, options)
        } ?: return null

        val longest = max(decoded.width, decoded.height)
        if (longest <= MAX_SIDE) return decoded

        val scale = MAX_SIDE.toFloat() / longest
        val scaled =
            decoded.scale((decoded.width * scale).toInt(), (decoded.height * scale).toInt())
        if (scaled != decoded) decoded.recycle()
        return scaled
    }

    private fun rotateByExif(uri: Uri, bitmap: Bitmap): Bitmap {
        val orientation = context.contentResolver.openInputStream(uri)?.use {
            ExifInterface(it).getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL
            )
        } ?: ExifInterface.ORIENTATION_NORMAL

        val degrees = when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> 90f
            ExifInterface.ORIENTATION_ROTATE_180 -> 180f
            ExifInterface.ORIENTATION_ROTATE_270 -> 270f
            else -> return bitmap
        }
        val matrix = Matrix().apply { postRotate(degrees) }
        val rotated = Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        if (rotated != bitmap) bitmap.recycle()
        return rotated
    }

    private companion object {
        const val MAX_SIDE = 1600
    }
}
