package com.arturs.stafanovics.crypto

import java.util.*

fun BitSet.toHexString() = this.toLongArray()[0].toULong().toString(16).padStart(16, '0')
fun BitSet.toBinaryString() = this.toLongArray()[0].toULong().toString(2).padStart(64, '0')