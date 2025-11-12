package id.antasari.p6minda_230104040129.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// [Mengubah 1678886400000 -> "15 Mar, 2023, 08:00 PM"]
fun formatTimestamp(ts: Long): String {
    val sdf = SimpleDateFormat("dd MMM, yyyy, hh:mma", Locale.getDefault())
    return sdf.format(Date(ts))
}