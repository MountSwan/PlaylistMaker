package com.practicum.playlistmaker.library.domain.models

sealed interface EndCase {
    object NominativeSingle: EndCase
    object NominativeMultiple: EndCase
    object Genitive: EndCase
}
