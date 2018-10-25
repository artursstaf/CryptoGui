package com.arturs.stafanovics.crypto.crypto

import java.lang.NumberFormatException
import java.util.*


class Des(val asBinary: Boolean = false) {

    companion object {
        private val circularShiftTable = intArrayOf(1, 1, 2, 2, 2, 2, 2, 2, 1, 2, 2, 2, 2, 2, 2, 1)

        private val compressionPermutationTable =
                intArrayOf(14, 17, 11, 24, 1, 5, 3, 28, 15, 6, 21, 10,
                        23, 19, 12, 4, 26, 8, 16, 7, 27, 20, 13, 2,
                        41, 52, 31, 37, 47, 55, 30, 40, 51, 45, 33, 48,
                        44, 49, 39, 56, 34, 53, 46, 42, 50, 36, 29, 32
                ).reversedArray()

        private val expansionPermutationTable =
                intArrayOf(32, 1, 2, 3, 4, 5, 4, 5, 6, 7, 8, 9,
                        8, 9, 10, 11, 12, 13, 12, 13, 14, 15, 16, 17,
                        16, 17, 18, 19, 20, 21, 20, 21, 22, 23, 24, 25,
                        24, 25, 26, 27, 28, 29, 28, 29, 30, 31, 32, 1
                ).reversedArray()

        private val sBoxTable =
                listOf(
                        intArrayOf(
                                14, 4, 13, 1, 2, 15, 11, 8, 3, 10, 6, 12, 5, 9, 0, 7,
                                0, 15, 7, 4, 14, 2, 13, 1, 10, 6, 12, 11, 9, 5, 3, 8,
                                4, 1, 14, 8, 13, 6, 2, 11, 15, 12, 9, 7, 3, 10, 5, 0,
                                15, 12, 8, 2, 4, 9, 1, 7, 5, 11, 3, 14, 10, 0, 6, 13
                        ),
                        intArrayOf(
                                15, 1, 8, 14, 6, 11, 3, 4, 9, 7, 2, 13, 12, 0, 5, 10,
                                3, 13, 4, 7, 15, 2, 8, 14, 12, 0, 1, 10, 6, 9, 11, 5,
                                0, 14, 7, 11, 10, 4, 13, 1, 5, 8, 12, 6, 9, 3, 2, 15,
                                13, 8, 10, 1, 3, 15, 4, 2, 11, 6, 7, 12, 0, 5, 14, 9
                        ),
                        intArrayOf(
                                10, 0, 9, 14, 6, 3, 15, 5, 1, 13, 12, 7, 11, 4, 2, 8,
                                13, 7, 0, 9, 3, 4, 6, 10, 2, 8, 5, 14, 12, 11, 15, 1,
                                13, 6, 4, 9, 8, 15, 3, 0, 11, 1, 2, 12, 5, 10, 14, 7,
                                1, 10, 13, 0, 6, 9, 8, 7, 4, 15, 14, 3, 11, 5, 2, 12
                        ),
                        intArrayOf(
                                7, 13, 14, 3, 0, 6, 9, 10, 1, 2, 8, 5, 11, 12, 4, 15,
                                13, 8, 11, 5, 6, 15, 0, 3, 4, 7, 2, 12, 1, 10, 14, 9,
                                10, 6, 9, 0, 12, 11, 7, 13, 15, 1, 3, 14, 5, 2, 8, 4,
                                3, 15, 0, 6, 10, 1, 13, 8, 9, 4, 5, 11, 12, 7, 2, 14
                        ),
                        intArrayOf(
                                2, 12, 4, 1, 7, 10, 11, 6, 8, 5, 3, 15, 13, 0, 14, 9,
                                14, 11, 2, 12, 4, 7, 13, 1, 5, 0, 15, 10, 3, 9, 8, 6,
                                4, 2, 1, 11, 10, 13, 7, 8, 15, 9, 12, 5, 6, 3, 0, 14,
                                11, 8, 12, 7, 1, 14, 2, 13, 6, 15, 0, 9, 10, 4, 5, 3
                        ),
                        intArrayOf(
                                12, 1, 10, 15, 9, 2, 6, 8, 0, 13, 3, 4, 14, 7, 5, 11,
                                10, 15, 4, 2, 7, 12, 9, 5, 6, 1, 13, 14, 0, 11, 3, 8,
                                9, 14, 15, 5, 2, 8, 12, 3, 7, 0, 4, 10, 1, 13, 11, 6,
                                4, 3, 2, 12, 9, 5, 15, 10, 11, 14, 1, 7, 6, 0, 8, 13
                        ),
                        intArrayOf(
                                4, 11, 2, 14, 15, 0, 8, 13, 3, 12, 9, 7, 5, 10, 6, 1,
                                13, 0, 11, 7, 4, 9, 1, 10, 14, 3, 5, 12, 2, 15, 8, 6,
                                1, 4, 11, 13, 12, 3, 7, 14, 10, 15, 6, 8, 0, 5, 9, 2,
                                6, 11, 13, 8, 1, 4, 10, 7, 9, 5, 0, 15, 14, 2, 3, 12
                        ),
                        intArrayOf(
                                13, 2, 8, 4, 6, 15, 11, 1, 10, 9, 3, 14, 5, 0, 12, 7,
                                1, 15, 13, 8, 10, 3, 7, 4, 12, 5, 6, 11, 0, 14, 9, 2,
                                7, 11, 4, 1, 9, 12, 14, 2, 0, 6, 10, 13, 15, 3, 5, 8,
                                2, 1, 14, 7, 4, 10, 8, 13, 15, 12, 9, 0, 3, 5, 6, 11
                        )
                ).reversed()

        private val pBoxTable =
                intArrayOf(16, 7, 20, 21, 29, 12, 28, 17, 1, 15, 23, 26, 5, 18, 31, 10,
                        2, 8, 24, 14, 32, 27, 3, 9, 19, 13, 30, 6, 22, 11, 4, 25
                ).reversedArray()

        private val initialPermutation=
                intArrayOf(
                        58, 50, 42, 34, 26, 18, 10, 2,
                        60, 52, 44, 36, 28, 20, 12, 4,
                        62, 54, 46, 38, 30, 22, 14, 6,
                        64, 56, 48, 40, 32, 24, 16, 8,
                        57, 49, 41, 33, 25, 17, 9, 1,
                        59, 51, 43, 35, 27, 19, 11, 3,
                        61, 53, 45, 37, 29, 21, 13, 5,
                        63, 55, 47, 39, 31, 23, 15, 7
                ).reversedArray()

        private val finalPermutation =
                intArrayOf(
                        40, 8, 48, 16, 56, 24, 64, 32,
                        39, 7, 47, 15, 55, 23, 63, 31,
                        38, 6, 46, 14, 54, 22, 62, 30,
                        37, 5, 45, 13, 53, 21, 61, 29,
                        36, 4, 44, 12, 52, 20, 60, 28,
                        35, 3, 43, 11, 51, 19, 59, 27,
                        34, 2, 42, 10, 50, 18, 58, 26,
                        33, 1, 41, 9, 49, 17, 57, 25
                ).reversedArray()
        private val newLine =  System.getProperty("line.separator")
    }

    var outputMessage = ""

    fun encrypt(key: String, message: String) = runRounds(key, message, revKeys = false)

    fun decrypt(key: String, message: String) = runRounds(key, message, revKeys = true)

    private fun runRounds(k: String, m: String, revKeys: Boolean = false): String {
        printInitialSetup(revKeys, k, m)

        val keys = getSubKeys(getBitSet(k)).let {
            if (!revKeys) it else it.reversed()
        }

        var message = getBitSet(m)
        message = message.initialPermutation()
        var (left, right) = message.get(32, 64) to message.get(0, 32)

        for (round in 0 .. 15){
            printRound(round, keys[round], combineBitSets(left, right))
            val newLeftRight = performRound(keys[round], left, right)
            left = newLeftRight.first
            right = newLeftRight.second
        }

        // swap before final permutation
        message = combineBitSets(left = right, right = left)

        return message.finalPermutation().let {
            if (asBinary) it.toBinaryString() else it.toHexString()
        }.also {
            outputMessage += ("Final message: $it")
        }
    }

    private fun printInitialSetup(revKeys: Boolean, k: String, message: String) {
        outputMessage = ""
        outputMessage += ("Running ${if (!revKeys) "encryption" else "decryption"}") + newLine
        outputMessage += ("Initial key: ${getBitSet(k).let { if (asBinary) it.toBinaryString() else it.toHexString()
        }}")
        outputMessage += (" | Initial message: ${getBitSet(message).let {
            if (asBinary) it.toBinaryString() else it.toHexString()
        }}") + newLine
    }

    private fun printRound(round: Int, keys: BitSet, message: BitSet) {
        outputMessage += ("Round: ${round + 1} | SubKey: ${keys.let {
            if (asBinary) it.toBinaryString() else it.toHexString() }} | ")
        outputMessage += ("Message : ${message.let {
            if (asBinary) it.toBinaryString() else it.toHexString() }}") + newLine
    }

    private fun combineBitSets(left: BitSet, right: BitSet): BitSet {
        val combined  = (left.toLongArray()[0] shl 32) or right.toLongArray()[0]
        return BitSet.valueOf(longArrayOf(combined))
    }

    private fun getSubKeys(key: BitSet): List<BitSet>{
        val list = mutableListOf<BitSet>()
        for(i in 0 .. 15){
            key.keyShift(i)
            list.add(BitSet.valueOf(key.toLongArray()))
        }
        return list
    }

    private fun getBitSet(str: String): BitSet {
        var asLong = try {
            str.toULong(16)
        }catch(e: NumberFormatException){
            str.toULong(2)
        }

        val bitSet = BitSet(64)

        (0 .. 63).forEach {
            bitSet[it] = (asLong and 0b1u) == 1uL
            asLong = asLong shr 1
        }

        return bitSet
    }

    private fun performRound(key: BitSet, left: BitSet, right: BitSet): Pair<BitSet, BitSet> {
        val fDRight = f(key.compressionPermutation(), right)
        left.xor(fDRight)
        return right to left
    }

    private fun f(key: BitSet, right: BitSet): BitSet {
        val expanded = right.expansionPermutation()
        expanded.xor(key)
        val sboxed = expanded.sBoxSubstition()
        return sboxed.pBoxPermutation()
    }

    private fun BitSet.keyShift(round: Int) {
        this.circularShift()
        if(circularShiftTable[round] == 2) this.circularShift()
    }

    private fun BitSet.circularShift() {
        val leftCarry= this[55]
        val rightCarry = this[27]
        val shifted = ((this.toLongArray()[0]) shl 1) and 0xFFFFFFFFFFFFFF
        val bs = BitSet.valueOf(longArrayOf(shifted))
        bs[0] = rightCarry
        bs[28] = leftCarry
        this.clear()
        this.or(bs)
    }

    private fun BitSet.sBoxSubstition(): BitSet {
        val bs = BitSet(32)
        for (whichSBox in 0 .. 7) {
            val sBoxIndex = this.getSBoxIndex(whichSBox * 6)
            val bits = sBoxTable[whichSBox][sBoxIndex]
            bs.setSBoxBits(whichSBox, bits)
        }
        return bs
    }

    private fun BitSet.setSBoxBits(offset: Int, bits: Int) {
        var remainingBits = bits
        (0..3).forEach {
            this[offset * 4 + it] = (remainingBits and 1) == 1
            remainingBits = remainingBits shr 1
        }
    }

    private fun BitSet.getSBoxIndex(offset: Int): Int {
        val row = (this[offset + 5].toInt() shl 1) + this[offset].toInt()
        val column = (offset + 4 downTo offset + 1).fold( 0 ) { acc, ind -> (acc shl 1) + this[ind].toInt() }
        return row * 16 +  column
    }

    private fun BitSet.finalPermutation() = performPermutation(this, finalPermutation, 64)
    private fun BitSet.initialPermutation() = performPermutation(this, initialPermutation, 64)
    private fun BitSet.pBoxPermutation() = performPermutation(this, pBoxTable, 32)
    private fun BitSet.expansionPermutation() = performPermutation(this, expansionPermutationTable, 32)
    private fun BitSet.compressionPermutation() = performPermutation(this, compressionPermutationTable, 56)

    private fun performPermutation(bitSet: BitSet, permutations: IntArray, size: Int) = BitSet(size).also { newBs ->
        permutations.forEachIndexed { ind, perm -> newBs[ind] = bitSet[size - perm] }
    }

    @ExperimentalUnsignedTypes
    private fun BitSet.toHexString() = this.toBinaryString().toULong(2).toString(16).padStart(16, '0')
    private fun BitSet.toBinaryString(): String {
        val sb = StringBuilder(64)
        (0 .. 63).forEach { sb.append( if(this[it]) '1' else '0') }
        return sb.reversed().toString().padStart(64, '0')
    }
    private fun Boolean.toInt() = if (this) 1 else 0
}


fun main() {
    print("Key: ")
    val key = readLine()!!
    print("Message: ")
    val message = readLine()!!
    val des = Des(asBinary = false)
    val encryptedMessage = des.encrypt(key, message)
    val decrypted = des.decrypt(key, encryptedMessage)
}
