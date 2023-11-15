package com.practicum.playlistmaker.domain.usecases

import com.practicum.playlistmaker.domain.CreateUpdateTimerTask

class CreateUpdateTimerTaskUseCase(private val createUpdateTimerTask: CreateUpdateTimerTask) {

    fun execute(): Runnable {
        return createUpdateTimerTask.execute()
    }

}