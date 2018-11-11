package com.arturs.stafanovics.crypto

import java.util.*

@ExperimentalUnsignedTypes
fun BitSet.toHexString() = this.toLongArray().foldRight("") {
    elem, res -> res + elem.toULong().toString(16).padStart(16, '0')
}

@ExperimentalUnsignedTypes
fun BitSet.toBinaryString() = this.toLongArray().foldRight(""){
    elem, res -> res + elem.toULong().toString(2).padStart(64, '0')
}
