package com.arturs.stafanovics.crypto.view

import com.arturs.stafanovics.crypto.crypto.Des
import javafx.beans.property.SimpleStringProperty
import tornadofx.*


class MainView : View("Data Encryption Standard") {
    private val key = SimpleStringProperty().apply { value = "F0CCAAF556678F" }
    private val message = SimpleStringProperty().apply { value = "0123456789ABCDEF" }
    private val output = SimpleStringProperty()
    private var des = Des(asBinary = false)
    private var lastAction = "none"

    override val root = vbox {
        borderpane {
            top = form {
                fieldset {
                    field("Key:") {
                        textfield(key)
                    }
                    field("Message:") {
                        textfield(message)
                    }
                }
            }

            center = vbox {
                label("Output: ") {
                    vboxConstraints {
                        marginLeft = 10.0
                    }
                }
                textarea(output) {
                    vboxConstraints {
                        marginLeft = 10.0
                        marginRight = 10.0
                        marginBottom = 10.0
                    }
                    prefRowCount = 20
                    isEditable = false
                }
            }

            right = vbox {
                checkbox("Binary output: ") {
                    action {
                        des = if (isSelected) Des(asBinary = true) else Des(asBinary = false)
                        when (lastAction) {
                            "enc" -> encrypt()
                            "dec" -> decrypt()
                        }

                    }
                }.also { check(true) }
                button("Encrypt") {
                    action {
                        encrypt()
                    }
                    vboxConstraints {
                        marginTop = 10.0
                        marginBottom = 10.0
                        marginRight = 10.0
                    }
                }
                button("decrypt") {
                    action {
                        decrypt()
                    }
                    vboxConstraints {
                        marginRight = 10.0
                    }
                }
            }
        }
    }

    private fun encrypt() {
        lastAction = "enc"
        runAsync {
            output.value = getDesOutput(true)
        }
    }

    private fun decrypt() {
        lastAction = "dec"
        runAsync {
            output.value = getDesOutput(false)
        }
    }

    private fun getDesOutput(encrypt: Boolean = true) = try {
        if (key.value.length != 14 && key.value.length != 54) throw Exception(
                "Key must be of length - binary 54 or hexadecimal 14"
        )
        if (message.value.length != 16 && message.value.length != 64) throw Exception(
                "Message must be of length - binary 64 or hexadecimal 16"
        )
        if (encrypt) des.encrypt(key.value, message.value) else des.decrypt(key.value, message.value)
        des.outputMessage
    } catch (e: Exception) {
        e.message
    }
}
