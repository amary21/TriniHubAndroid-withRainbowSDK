package com.amary21.trinihub.app

import android.app.Application
import com.ale.rainbowsdk.RainbowSdk


class App : Application() {
    override fun onCreate() {
        super.onCreate()

        RainbowSdk.instance().initialize(
            this,
            "0ae77d403ff211ea99fa4daad3b6a28a",
            "lYjaJFCwOjjg76WSUdpXoBKFafbIwokQvEydoyOo65VMzvfh9DfUXBpzOVO1Av7u"
        )
    }
}