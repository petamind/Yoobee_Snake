package com.tungnd.yoobeesnake.gamecomponents

interface GameLoop {
    fun start()
    fun pause()
    fun resume()
    fun update()
    fun draw()
    fun stop()
}