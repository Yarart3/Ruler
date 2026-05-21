package com.example.ruler

import android.content.Context
import android.net.Uri
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.ruler.data.local.RulerDatabase
import com.example.ruler.data.repository.TripImageRepositoryImpl
import java.io.File
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class TripImageRepositoryTest {

    private lateinit var context: Context
    private lateinit var database: RulerDatabase
    private lateinit var repository: TripImageRepositoryImpl

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        database = Room.inMemoryDatabaseBuilder(context, RulerDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        repository = TripImageRepositoryImpl(database.tripImageDao(), context)
    }

    @After
    fun tearDown() {
        database.close()
        File(context.filesDir, "trip_gallery").deleteRecursively()
    }

    @Test
    fun addImages_copiesSelectedImagesIntoAppStorage() = runBlocking {
        val sourceFile = createTempImageFile("source-copy-test.jpg")

        repository.addImages("trip-1", listOf(Uri.fromFile(sourceFile).toString()))

        val storedImages = repository.getImagesForTrip("trip-1").first()
        assertEquals(1, storedImages.size)
        val storedFile = File(requireNotNull(Uri.parse(storedImages.single().uri).path))
        assertTrue(storedFile.exists())
        assertTrue(storedFile.absolutePath.contains("${File.separator}trip_gallery${File.separator}trip-1${File.separator}"))
        assertFalse(storedFile.absolutePath == sourceFile.absolutePath)
        assertEquals(sourceFile.readBytes().toList(), storedFile.readBytes().toList())
    }

    @Test
    fun deleteImage_removesStoredFileAndDatabaseEntry() = runBlocking {
        val sourceFile = createTempImageFile("source-delete-test.jpg")
        repository.addImages("trip-2", listOf(Uri.fromFile(sourceFile).toString()))
        val storedImage = repository.getImagesForTrip("trip-2").first().single()
        val storedFile = File(requireNotNull(Uri.parse(storedImage.uri).path))

        repository.deleteImage(storedImage.id)

        assertFalse(storedFile.exists())
        assertTrue(repository.getImagesForTrip("trip-2").first().isEmpty())
    }

    private fun createTempImageFile(name: String): File {
        return File(context.cacheDir, name).apply {
            writeBytes(byteArrayOf(1, 2, 3, 4, 5, 6))
        }
    }
}
