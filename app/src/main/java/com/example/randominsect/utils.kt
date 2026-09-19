package com.example.randominsect
import java.io.File
import android.content.Context
import com.example.randominsect.data.AppContextProvider

import java.util.concurrent.TimeUnit


public fun moveToPermanentStorage(sourceFile: File): String {
        // Create a dedicated directory inside app internal storage
	val context = AppContextProvider.get()
        val permanentDir = File(context.filesDir, "saved_insects").apply {
            if (!exists()) mkdirs()
        }

        // Target file path inside permanent storage
        val targetFile = File(permanentDir, sourceFile.name)

        // Move the file (fall back to copy + delete if moving across filesystems)
        if (!sourceFile.renameTo(targetFile)) {
            sourceFile.copyTo(targetFile, overwrite = true)
            sourceFile.delete()

        }

        return targetFile.absolutePath
    }


public fun formatDuration(ms: Long): String {
    // Return early for 0 or negative values
    if (ms <= 0) return "00:00:00"

    val hours = TimeUnit.MILLISECONDS.toHours(ms)
    val minutes = TimeUnit.MILLISECONDS.toMinutes(ms) % 60
    val seconds = TimeUnit.MILLISECONDS.toSeconds(ms) % 60

    // Formats into zero-padded double digits (e.g., 02:05:09)
    return String.format("%02d:%02d:%02d", hours, minutes, seconds)
}
