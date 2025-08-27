package com.thmanyah.task.domain.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

val ContentItem.displayTitle: String
    get() = title.trim().takeIf { it.isNotBlank() } ?: "Untitled"

val ContentItem.displayAuthor: String?
    get() = author?.trim()?.takeIf { it.isNotBlank() }

val ContentItem.displayDescription: String?
    get() = description?.trim()?.takeIf { it.isNotBlank() }

val ContentItem.formattedDuration: String?
    get() = duration?.let { formatDurationString(it) }

val ContentItem.formattedPublishDate: String?
    get() = publishDate?.let { formatDateString(it) }

val ContentItem.contentTypeDisplayName: String
    get() = when (this) {
        is ContentItem.Podcast -> "Podcast"
        is ContentItem.Episode -> "Episode"
        is ContentItem.AudioBook -> "Audio Book"
        is ContentItem.AudioArticle -> "Audio Article"
    }

val ContentItem.Podcast.formattedEpisodeCount: String?
    get() = episodeCount?.let { count ->
        when (count) {
            0 -> "No episodes"
            1 -> "1 episode"
            else -> "$count episodes"
        }
    }

val ContentItem.Episode.formattedEpisodeNumber: String?
    get() = episodeNumber?.let { "Episode $it" }

val ContentItem.AudioBook.formattedChapterCount: String?
    get() = chapters?.let { count ->
        when (count) {
            0 -> "No chapters"
            1 -> "1 chapter"
            else -> "$count chapters"
        }
    }

val ContentItem.AudioBook.displayLanguage: String?
    get() = language?.let { lang ->
        Locale.forLanguageTag(lang).displayLanguage?.takeIf { it.isNotBlank() } ?: lang
    }

private fun formatDurationString(duration: String): String? {
    return try {
        val durationInSeconds = duration.toLongOrNull() ?: return duration
        val kotlinDuration = durationInSeconds.seconds
        formatDuration(kotlinDuration)
    } catch (e: Exception) {
        duration
    }
}

private fun formatDuration(duration: Duration): String {
    val totalSeconds = duration.inWholeSeconds
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60

    return when {
        hours > 0 -> String.format("%d:%02d:%02d", hours, minutes, seconds)
        minutes > 0 -> String.format("%d:%02d", minutes, seconds)
        else -> "${seconds}s"
    }
}

private fun formatDateString(dateString: String): String? {
    return try {
        val inputFormats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd",
            "MM/dd/yyyy",
            "dd/MM/yyyy"
        )
        
        for (pattern in inputFormats) {
            try {
                val inputFormat = SimpleDateFormat(pattern, Locale.US)
                val date = inputFormat.parse(dateString)
                if (date != null) {
                    return formatDateForDisplay(date)
                }
            } catch (e: Exception) {
                continue
            }
        }
        
        dateString
    } catch (e: Exception) {
        dateString
    }
}

private fun formatDateForDisplay(date: Date): String {
    val now = Date()
    val diffInMillis = now.time - date.time
    val diffInDays = diffInMillis / (24 * 60 * 60 * 1000)

    return when {
        diffInDays < 1 -> "Today"
        diffInDays < 2 -> "Yesterday"
        diffInDays < 7 -> "$diffInDays days ago"
        diffInDays < 30 -> "${diffInDays / 7} weeks ago"
        diffInDays < 365 -> "${diffInDays / 30} months ago"
        else -> SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(date)
    }
}