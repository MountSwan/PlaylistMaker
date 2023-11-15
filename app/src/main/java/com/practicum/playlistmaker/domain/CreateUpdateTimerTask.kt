package com.practicum.playlistmaker.domain

interface CreateUpdateTimerTask {
    fun execute(): Runnable
}