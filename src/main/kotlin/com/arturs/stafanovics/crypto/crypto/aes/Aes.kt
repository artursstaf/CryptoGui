package com.arturs.stafanovics.crypto.crypto.aes

import java.util.*
import kotlin.experimental.xor

data class AesState(val stage: String, val key: String, val mes: String)

@ExperimentalUnsignedTypes
class Aes {

    companion object {
        private val sBox = intArrayOf(
                0x63, 0x7c, 0x77, 0x7b, 0xf2, 0x6b, 0x6f, 0xc5, 0x30, 0x01, 0x67, 0x2b, 0xfe, 0xd7, 0xab, 0x76,
                0xca, 0x82, 0xc9, 0x7d, 0xfa, 0x59, 0x47, 0xf0, 0xad, 0xd4, 0xa2, 0xaf, 0x9c, 0xa4, 0x72, 0xc0,
                0xb7, 0xfd, 0x93, 0x26, 0x36, 0x3f, 0xf7, 0xcc, 0x34, 0xa5, 0xe5, 0xf1, 0x71, 0xd8, 0x31, 0x15,
                0x04, 0xc7, 0x23, 0xc3, 0x18, 0x96, 0x05, 0x9a, 0x07, 0x12, 0x80, 0xe2, 0xeb, 0x27, 0xb2, 0x75,
                0x09, 0x83, 0x2c, 0x1a, 0x1b, 0x6e, 0x5a, 0xa0, 0x52, 0x3b, 0xd6, 0xb3, 0x29, 0xe3, 0x2f, 0x84,
                0x53, 0xd1, 0x00, 0xed, 0x20, 0xfc, 0xb1, 0x5b, 0x6a, 0xcb, 0xbe, 0x39, 0x4a, 0x4c, 0x58, 0xcf,
                0xd0, 0xef, 0xaa, 0xfb, 0x43, 0x4d, 0x33, 0x85, 0x45, 0xf9, 0x02, 0x7f, 0x50, 0x3c, 0x9f, 0xa8,
                0x51, 0xa3, 0x40, 0x8f, 0x92, 0x9d, 0x38, 0xf5, 0xbc, 0xb6, 0xda, 0x21, 0x10, 0xff, 0xf3, 0xd2,
                0xcd, 0x0c, 0x13, 0xec, 0x5f, 0x97, 0x44, 0x17, 0xc4, 0xa7, 0x7e, 0x3d, 0x64, 0x5d, 0x19, 0x73,
                0x60, 0x81, 0x4f, 0xdc, 0x22, 0x2a, 0x90, 0x88, 0x46, 0xee, 0xb8, 0x14, 0xde, 0x5e, 0x0b, 0xdb,
                0xe0, 0x32, 0x3a, 0x0a, 0x49, 0x06, 0x24, 0x5c, 0xc2, 0xd3, 0xac, 0x62, 0x91, 0x95, 0xe4, 0x79,
                0xe7, 0xc8, 0x37, 0x6d, 0x8d, 0xd5, 0x4e, 0xa9, 0x6c, 0x56, 0xf4, 0xea, 0x65, 0x7a, 0xae, 0x08,
                0xba, 0x78, 0x25, 0x2e, 0x1c, 0xa6, 0xb4, 0xc6, 0xe8, 0xdd, 0x74, 0x1f, 0x4b, 0xbd, 0x8b, 0x8a,
                0x70, 0x3e, 0xb5, 0x66, 0x48, 0x03, 0xf6, 0x0e, 0x61, 0x35, 0x57, 0xb9, 0x86, 0xc1, 0x1d, 0x9e,
                0xe1, 0xf8, 0x98, 0x11, 0x69, 0xd9, 0x8e, 0x94, 0x9b, 0x1e, 0x87, 0xe9, 0xce, 0x55, 0x28, 0xdf,
                0x8c, 0xa1, 0x89, 0x0d, 0xbf, 0xe6, 0x42, 0x68, 0x41, 0x99, 0x2d, 0x0f, 0xb0, 0x54, 0xbb, 0x16
        )
        private val invSbox = intArrayOf(
                0x52, 0x09, 0x6a, 0xd5, 0x30, 0x36, 0xa5, 0x38, 0xbf, 0x40, 0xa3, 0x9e, 0x81, 0xf3, 0xd7, 0xfb,
                0x7c, 0xe3, 0x39, 0x82, 0x9b, 0x2f, 0xff, 0x87, 0x34, 0x8e, 0x43, 0x44, 0xc4, 0xde, 0xe9, 0xcb,
                0x54, 0x7b, 0x94, 0x32, 0xa6, 0xc2, 0x23, 0x3d, 0xee, 0x4c, 0x95, 0x0b, 0x42, 0xfa, 0xc3, 0x4e,
                0x08, 0x2e, 0xa1, 0x66, 0x28, 0xd9, 0x24, 0xb2, 0x76, 0x5b, 0xa2, 0x49, 0x6d, 0x8b, 0xd1, 0x25,
                0x72, 0xf8, 0xf6, 0x64, 0x86, 0x68, 0x98, 0x16, 0xd4, 0xa4, 0x5c, 0xcc, 0x5d, 0x65, 0xb6, 0x92,
                0x6c, 0x70, 0x48, 0x50, 0xfd, 0xed, 0xb9, 0xda, 0x5e, 0x15, 0x46, 0x57, 0xa7, 0x8d, 0x9d, 0x84,
                0x90, 0xd8, 0xab, 0x00, 0x8c, 0xbc, 0xd3, 0x0a, 0xf7, 0xe4, 0x58, 0x05, 0xb8, 0xb3, 0x45, 0x06,
                0xd0, 0x2c, 0x1e, 0x8f, 0xca, 0x3f, 0x0f, 0x02, 0xc1, 0xaf, 0xbd, 0x03, 0x01, 0x13, 0x8a, 0x6b,
                0x3a, 0x91, 0x11, 0x41, 0x4f, 0x67, 0xdc, 0xea, 0x97, 0xf2, 0xcf, 0xce, 0xf0, 0xb4, 0xe6, 0x73,
                0x96, 0xac, 0x74, 0x22, 0xe7, 0xad, 0x35, 0x85, 0xe2, 0xf9, 0x37, 0xe8, 0x1c, 0x75, 0xdf, 0x6e,
                0x47, 0xf1, 0x1a, 0x71, 0x1d, 0x29, 0xc5, 0x89, 0x6f, 0xb7, 0x62, 0x0e, 0xaa, 0x18, 0xbe, 0x1b,
                0xfc, 0x56, 0x3e, 0x4b, 0xc6, 0xd2, 0x79, 0x20, 0x9a, 0xdb, 0xc0, 0xfe, 0x78, 0xcd, 0x5a, 0xf4,
                0x1f, 0xdd, 0xa8, 0x33, 0x88, 0x07, 0xc7, 0x31, 0xb1, 0x12, 0x10, 0x59, 0x27, 0x80, 0xec, 0x5f,
                0x60, 0x51, 0x7f, 0xa9, 0x19, 0xb5, 0x4a, 0x0d, 0x2d, 0xe5, 0x7a, 0x9f, 0x93, 0xc9, 0x9c, 0xef,
                0xa0, 0xe0, 0x3b, 0x4d, 0xae, 0x2a, 0xf5, 0xb0, 0xc8, 0xeb, 0xbb, 0x3c, 0x83, 0x53, 0x99, 0x61,
                0x17, 0x2b, 0x04, 0x7e, 0xba, 0x77, 0xd6, 0x26, 0xe1, 0x69, 0x14, 0x63, 0x55, 0x21, 0x0c, 0x7d
        )

        private val rCon = byteArrayOf(0x01, 0x02, 0x04, 0x08, 0x10, 0x20, 0x40, 0x80.toByte(), 0x1b, 0x36)
    }

    val history = mutableListOf<AesState>()

    fun encrypt(key: String, message: String): String {
        val mesState = getByteArray(message).toTwoDimensional()
        val subKeys = getSubKeys(getByteArray(key).toTwoDimensional())
        history.clear()
        history.add(AesState("Initial", key, message))

        addRoundKey(mesState, subKeys[0])
        history.add(AesState("Add round key", getHexString(subKeys[0]), getHexString(mesState)))

        for (i in 1..9) {
            substitution(mesState)
            shiftRows(mesState)
            mixColumns(mesState)
            addRoundKey(mesState, subKeys[i])
            history.add(AesState("Round $i", getHexString(subKeys[i]), getHexString(mesState)))
        }

        substitution(mesState)
        shiftRows(mesState)
        addRoundKey(mesState, subKeys[10])
        history.add(AesState("Round 10", getHexString(subKeys[10]), getHexString(mesState)))

        return getHexString(mesState)
    }

    fun decrypt(key: String, message: String): String {
        val mesState = getByteArray(message).toTwoDimensional()
        val subKeys = getSubKeys(getByteArray(key).toTwoDimensional()).reversed()
        history.clear()
        history.add(AesState("Initial", key, message))

        addRoundKey(mesState, subKeys[0])
        history.add(AesState("Add round key", getHexString(subKeys[0]), getHexString(mesState)))

        for (i in 1..9) {
            invShiftRows(mesState)
            invSubstitution(mesState)
            addRoundKey(mesState, subKeys[i])
            invMixColumns(mesState)
            history.add(AesState("Round $i", getHexString(subKeys[i]), getHexString(mesState)))
        }

        invShiftRows(mesState)
        invSubstitution(mesState)
        addRoundKey(mesState, subKeys[10])
        history.add(AesState("Round 10", getHexString(subKeys[10]), getHexString(mesState)))

        return getHexString(mesState)
    }

    private fun getSubKeys(parentKey: Array<ByteArray>) = mutableListOf(parentKey).also { list ->
        for (i in 1..10) {
            list.add(getSubKey(list[i - 1], i))
        }
    }

    private fun getSubKey(previous: Array<ByteArray>, index: Int): Array<ByteArray> {
        val subKey = Array(4) { ByteArray(4) }
        subKeyFillFirstColumn(subKey, previous, index)
        // Fill rest columns 1-3
        for (col in 1..3) {
            for (row in 0..3) {
                subKey[row][col] = previous[row][col] xor subKey[row][col - 1]
            }
        }
        return subKey
    }

    private fun subKeyFillFirstColumn(subKey: Array<ByteArray>, previous: Array<ByteArray>, index: Int) {
        // Rotate column
        for (i in 0..3) {
            subKey[i][0] = previous[(i + 1) % 4][3]
        }
        // SubBytes
        for (i in 0..3) {
            subKey[i][0] = getSubstitutionValue(subKey[i][0], sBox)
        }
        // XOR first word with prev key first word
        for (i in 0..3) {
            subKey[i][0] = subKey[i][0] xor previous[i][0]
        }
        // XOR With Rcon
        subKey[0][0] = subKey[0][0] xor rCon[index - 1]
    }

    private fun addRoundKey(mes: Array<ByteArray>, key: Array<ByteArray>) {
        for (i in 0..3) {
            for (y in 0..3) {
                mes[i][y] = mes[i][y] xor key[i][y]
            }
        }
    }

    private fun mixColumns(arr: Array<ByteArray>) {
        val new = Array(4) { ByteArray(4) }
        for (column in 0..3) {
            new[0][column] = (arr[0][column] galoisMulti 2) xor (arr[1][column] galoisMulti 3) xor arr[2][column] xor arr[3][column]
            new[1][column] = arr[0][column] xor (arr[1][column] galoisMulti 2) xor (arr[2][column] galoisMulti 3) xor arr[3][column]
            new[2][column] = arr[0][column] xor arr[1][column] xor (arr[2][column] galoisMulti 2) xor (arr[3][column] galoisMulti 3)
            new[3][column] = (arr[0][column] galoisMulti 3) xor arr[1][column] xor arr[2][column] xor (arr[3][column] galoisMulti 2)
        }
        new.copyInto(arr)
    }

    private fun invMixColumns(arr: Array<ByteArray>) {
        val new = Array(4) { ByteArray(4) }
        for (column in 0..3) {
            new[0][column] = (arr[0][column] galoisMulti 0x0e) xor (arr[1][column] galoisMulti 0x0b) xor (arr[2][column] galoisMulti 0x0d) xor (arr[3][column] galoisMulti 0x09)
            new[1][column] = (arr[0][column] galoisMulti 0x09) xor (arr[1][column] galoisMulti 0x0e) xor (arr[2][column] galoisMulti 0x0b) xor (arr[3][column] galoisMulti 0x0d)
            new[2][column] = (arr[0][column] galoisMulti 0x0d) xor (arr[1][column] galoisMulti 0x09) xor (arr[2][column] galoisMulti 0x0e) xor (arr[3][column] galoisMulti 0x0b)
            new[3][column] = (arr[0][column] galoisMulti 0x0b) xor (arr[1][column] galoisMulti 0x0d) xor (arr[2][column] galoisMulti 0x09) xor (arr[3][column] galoisMulti 0x0e)
        }
        new.copyInto(arr)
    }

    private fun shiftRows(mes: Array<ByteArray>) {
        for (i in 0..3) {
            mes[i].shiftRow(i).copyInto(mes[i])
        }
    }

    private fun invShiftRows(mes: Array<ByteArray>) {
        for (i in 0..3) {
            mes[i].invShiftRow(i).copyInto(mes[i])
        }
    }

    private fun ByteArray.shiftRow(count: Int) = (this.drop(count) + this.take(count)).toByteArray()
    private fun ByteArray.invShiftRow(count: Int) = (this.takeLast(count) + this.dropLast(count)).toByteArray()

    private fun substitution(mes: Array<ByteArray>) {
        mes.forEachIndexed { rowInd, row ->
            row.forEachIndexed { colInd, elem ->
                mes[rowInd][colInd] = getSubstitutionValue(elem, sBox)
            }
        }
    }

    private fun invSubstitution(mes: Array<ByteArray>) {
        mes.forEachIndexed { rowInd, row ->
            row.forEachIndexed { colInd, elem ->
                mes[rowInd][colInd] = getSubstitutionValue(elem, invSbox)
            }
        }
    }

    private fun getSubstitutionValue(elem: Byte, box: IntArray): Byte {
        val row = elem.toInt() shr 4 and 0b1111
        val col = elem.toInt() and 0b1111
        return box[row * 16 + col].toByte()
    }

    private infix fun Byte.galoisMulti(by: Byte): Byte {
        // Stores left side multiplied by {02} powers, which is multiplying by x
        // {02}, {04}, {08}, {10} ... {40}
        val xTimesPowers = ByteArray(7)
        xTimesPowers[0] = this.xTime()
        for(i in 1..6) {
            xTimesPowers[i] = xTimesPowers[i-1].xTime()
        }

        var sum = if (by.toInt() and 1 == 1) this else 0
        // For each 1 bit in right side + the corresponding x power
        for (i in 1..7) {
            if ((by.toInt() ushr i) and 1 == 1) {
                sum = sum xor xTimesPowers[i - 1]
            }
        }
        return sum
    }

    // Multiplication by x in finite field
    private fun Byte.xTime() = ((this.toInt() shl 1) xor ((this.toInt() ushr 7) and 0x1b)).toByte()

    private fun ByteArray.toTwoDimensional() = Array(4) { row ->
        ByteArray(4) { col -> this[row + 4 * col] }
    }

    private fun Array<ByteArray>.toOneDimensional() = ByteArray(16) { this[it % 4][it / 4] }

    private fun getHexString(mes: Array<ByteArray>) =
            BitSet.valueOf(mes.toOneDimensional().reversedArray()).toHexString()

    // Parse hex string into byte one dimensional byte array
    private fun getByteArray(str: String): ByteArray {
        val left = str.substring(0..15).toULong(16).toLong()
        val right = str.substring(16..31).toULong(16).toLong()
        return BitSet.valueOf(longArrayOf(right, left)).toByteArray().copyInto(ByteArray(16)).reversedArray()
    }
}

@ExperimentalUnsignedTypes
fun BitSet.toHexString() = this.toLongArray().foldRight("") { elem, res ->
    res + elem.toULong().toString(16).padStart(16, '0')
}

@ExperimentalUnsignedTypes
fun main() {
    print("Key: ")
    val key = readLine()!!
    print("Message: ")
    val message = readLine()!!
    val aes = Aes()

    val encryptedMessage = aes.encrypt(key, message)
    println("Encrpyted message: $encryptedMessage")
    println("Encryption steps: ")
    println("Stage | Message | Key")
    aes.history.forEach {
        println("${it.stage} | ${it.mes} | ${it.key}")
    }

    val decrypted = aes.decrypt(key, encryptedMessage)
    println()
    println("Decrypted message: $decrypted")
    println("Decryption steps: ")
    println("Stage | Message | Key")
    aes.history.forEach {
        println("${it.stage} | ${it.mes} | ${it.key}")
    }
}
