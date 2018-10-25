package com.arturs.stafanovics.crypto.view

import com.arturs.stafanovics.crypto.crypto.Des
import com.arturs.stafanovics.crypto.crypto.DesState
import com.arturs.stafanovics.crypto.toBinaryString
import com.arturs.stafanovics.crypto.toHexString
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.control.TableView
import tornadofx.*
import java.util.*


class MainView : View("Data Encryption Standard") {
    private val key = SimpleStringProperty().apply { value = "F0CCAAF556678F" }
    private val message = SimpleStringProperty().apply { value = "0123456789ABCDEF" }
    private val output = SimpleStringProperty()
    private val tableData = FXCollections.observableArrayList<DesState>()
    private val des = Des()
    private var binaryOutput = false
    private var lastAction = "none"
    private lateinit var table: TableView<*>

    override val root = vbox {
        borderpane {
            top = form {
                fieldset {
                    field("Key (binary or hex):") {
                        textfield(key)
                    }
                    field("Message (binary or hex):") {
                        textfield(message)
                    }
                }
            }

            center = vbox {
                hbox {
                    label("Output: ")
                    textfield(output) {
                        isEditable = false
                        minWidth = 550.0
                    }
                    vboxConstraints {
                        marginLeft = 10.0
                    }
                }


                tableview(tableData) {
                    table = this
                    readonlyColumn("Stage", DesState::stage) {
                        isSortable = false
                    }
                    readonlyColumn("Key", DesState::key){
                        isSortable = false
                    }.cellFormat {
                        text = if (binaryOutput) it?.toBinaryString() ?: ""
                        else it?.toHexString() ?: ""
                    }
                    readonlyColumn("Message", DesState::mes){
                        isSortable = false
                    }.cellFormat {
                        text = if (binaryOutput) it?.toBinaryString() else it.toHexString()
                    }
                    columnResizePolicy = SmartResize.POLICY
                    vboxConstraints {
                        margin = Insets(10.0)
                    }
                    prefHeight = 800.0
                }
            }

            right = vbox {
                checkbox("Binary output") {
                    action {
                        binaryOutput = isSelected
                        when (lastAction) {
                            "enc" -> encrypt()
                            "dec" -> decrypt()
                        }
                    }
                    vboxConstraints {
                        marginLeft = 5.0
                        marginRight = 5.0
                    }
                }
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
                button("Decrypt") {
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
            tableData.clear()
            tableData.setAll(des.history)
            SmartResize.POLICY.requestResize(table)

        }
    }

    private fun decrypt() {
        lastAction = "dec"
        runAsync {
            output.value = getDesOutput(false)
            tableData.clear()
            tableData.setAll(des.history)
            SmartResize.POLICY.requestResize(table)
        }
    }

    private fun getDesOutput(encrypt: Boolean = true) = try {
        if (key.value.length != 14 && key.value.length != 54) throw Exception(
                "Key must be of length - binary 54 or hexadecimal 14"
        )
        if (message.value.length != 16 && message.value.length != 64) throw Exception(
                "Message must be of length - binary 64 or hexadecimal 16"
        )
        if (encrypt) des.encrypt(key.value, message.value, binaryOutput) else des.decrypt(key.value, message.value, binaryOutput)
    } catch (e: Exception) {
        e.message
    }
}
