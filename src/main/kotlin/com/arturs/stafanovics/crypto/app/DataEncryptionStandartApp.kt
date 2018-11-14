package com.arturs.stafanovics.crypto.app

import com.arturs.stafanovics.crypto.view.AesView
import javafx.scene.Scene
import javafx.scene.paint.Color
import javafx.stage.Stage
import tornadofx.*

class DataEncryptionStandartApp: App(AesView::class, Styles::class){
    override fun start(stage: Stage) {
        stage.minHeight = 300.0
        stage.minHeight = 300.0
        stage.height = 725.0
        stage.width = 800.0
        super.start(stage)
    }

    override fun createPrimaryScene(view: UIComponent): Scene {
        return super.createPrimaryScene(view).apply { fill = Color.valueOf("#EDEDED") }
    }
}