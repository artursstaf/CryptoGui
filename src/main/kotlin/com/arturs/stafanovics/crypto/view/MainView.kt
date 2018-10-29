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

class MainView : View("Data Encryption Standard") {
    private val keyProp = SimpleStringProperty().apply { value = "F0CCAAF556678F" }
    private val messageProp = SimpleStringProperty().apply { value = "0123456789ABCDEF" }
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
                        textfield(keyProp)
                    }
                    field("Message (binary or hex):") {
                        textfield(messageProp)
                    }
                }
            }

            center = vbox {
                hbox {
                    label("Output: ")
                    textfield(output) {
                        isEditable = false
                        minWidth = 600.0
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
                    readonlyColumn("Message", DesState::mes){
                        isSortable = false
                    }.cellFormat {
                        text = if (binaryOutput) it.toBinaryString().replace("(.{8})".toRegex(), "$1 ")
                        else it.toHexString()
                    }
                    readonlyColumn("Key", DesState::key){
                        isSortable = false
                    }.cellFormat {
                        text = if (binaryOutput) it?.toBinaryString()?.replace("(.{8})".toRegex(), "$1 ") ?: ""
                        else it?.toHexString() ?: ""
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
        output.value = getDesOutput(true)
        tableData.clear()
        tableData.setAll(des.history)
        SmartResize.POLICY.requestResize(table)
    }

    private fun decrypt() {
        lastAction = "dec"
        output.value = getDesOutput(false)
        tableData.clear()
        tableData.setAll(des.history)
        SmartResize.POLICY.requestResize(table)
    }

    private fun getDesOutput(encrypt: Boolean = true) = try {
        val key = keyProp.value.replace(" ", "")
        val mes = messageProp.value.replace(" ", "")

        if (key.length != 14 && key.length != 54)
            throw Exception("Key must be of length - binary 54 or hexadecimal 14")
        if (mes.length != 16 && mes.length != 64)
            throw Exception("Message must be of length - binary 64 or hexadecimal 16")

        val output = if (encrypt) des.encrypt(key, mes) else des.decrypt(key, mes)
        if(!binaryOutput) output else output.toULong(16).toString(2).padStart(64, '0').replace("(.{8})".toRegex(), "$1 ")
    } catch (e: Exception) {
        e.message
    }
}
