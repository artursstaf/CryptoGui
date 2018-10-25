package com.arturs.stafanovics.crypto

import java.util.*

fun BitSet.toHexString() = this.toBinaryString().toULong(2).toString(16).padStart(16, '0')
fun BitSet.toBinaryString(): String {
    val sb = StringBuilder(64)
    (0 .. 63).forEach { sb.append( if(this[it]) '1' else '0') }
    return sb.reversed().toString().padStart(64, '0')
}