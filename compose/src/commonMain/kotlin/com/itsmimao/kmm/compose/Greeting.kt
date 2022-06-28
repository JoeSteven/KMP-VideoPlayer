package com.itsmimao.kmm.compose

class Greeting {
    fun greeting(): String {
        return "Hello, ${Platform().platform}!"
    }
}