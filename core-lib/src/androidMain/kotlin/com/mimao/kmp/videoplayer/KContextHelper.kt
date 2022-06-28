package com.mimao.kmp.videoplayer

import android.content.Context

object KContextHelper {
    private var provide: () -> Context = {
        throw Error("KContextHelper.provideContext must be implemented")
    }

    fun context(): Context {
        return provide.invoke()
    }

    fun provideContext(block: () -> Context) {
        provide = block
    }
}