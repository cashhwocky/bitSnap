package com.bitdroid.snap

import android.app.Application
import com.bitdroid.snap.ui.Bit

class BitSnapApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Bit.apply(this)
    }
}
