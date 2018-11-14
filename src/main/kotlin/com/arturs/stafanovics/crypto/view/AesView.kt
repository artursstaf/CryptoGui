package com.arturs.stafanovics.crypto.view

import com.arturs.stafanovics.crypto.crypto.aes.Aes
import com.arturs.stafanovics.crypto.crypto.aes.AesState
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.geometry.Insets
import javafx.scene.control.TableView
import tornadofx.*

class AesView : View("Advanced Encryption Standard") {
    private val keyProp = SimpleStringProperty().apply { value = "2b7e151628aed2a6abf7158809cf4f3c" }
    private val messageProp = SimpleStringProperty().apply { value = "3243f6a8885a308d313198a2e0370734" }
    private val output = SimpleStringProperty()
    private val tableData = FXCollections.observableArrayList<AesState>()
    private val aes = Aes()
    private lateinit var table: TableView<*>

    override val root = vbox {
        borderpane {
            top = form {
                fieldset {
                    field("Key: ") {
                        textfield(keyProp)
                    }
                    field("Message: ") {
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
                    readonlyColumn("Stage", AesState::stage) {
                        isSortable = false
                    }
                    readonlyColumn("Message", AesState::mes){
                        isSortable = false
                    }.cellFormat {
                        text = it
                    }
                    readonlyColumn("Key", AesState::key){
                        isSortable = false
                    }.cellFormat {
                        text = it
                    }
                    columnResizePolicy = SmartResize.POLICY
                    vboxConstraints {
                        margin = Insets(10.0)
                    }
                    prefHeight = 800.0
                }
            }

            right = vbox {
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
        output.value = getAesOutput(true)
        tableData.clear()
        tableData.setAll(aes.history)
        SmartResize.POLICY.requestResize(table)
    }

    private fun decrypt() {
        output.value = getAesOutput(false)
        tableData.clear()
        tableData.setAll(aes.history)
        SmartResize.POLICY.requestResize(table)
    }

    private fun getAesOutput(encrypt: Boolean) = try {
        val key = keyProp.value.replace(" ", "")
        val mes = messageProp.value.replace(" ", "")

        if (key.length != 32) throw Exception("Key must be of length - hexadecimal 32")
        if (mes.length != 32) throw Exception("Message must be of length - hexadecimal 32")

        if (encrypt) aes.encrypt(key, mes) else aes.decrypt(key, mes)
    } catch (e: Exception) {
        e.message
    }
}
