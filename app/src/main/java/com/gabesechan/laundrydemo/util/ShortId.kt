package com.gabesechan.laundrydemo.util

import java.util.UUID

private const val CROCKFORD = "0123456789ABCDEFGHJKMNPQRSTVWXYZ"

fun uuidToShortId(uuidString: String): String {
    val uuid = UUID.fromString(uuidString)
    val folded = uuid.mostSignificantBits xor uuid.leastSignificantBits
    val chars = CharArray(13)
    var v = folded
    for (i in 12 downTo 0) {
        chars[i] = CROCKFORD[(v and 0x1F).toInt()]
        v = v ushr 5
    }
    return String(chars)
}
