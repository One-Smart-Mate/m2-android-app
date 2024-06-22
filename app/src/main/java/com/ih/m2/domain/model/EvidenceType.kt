package com.ih.m2.domain.model

import com.ih.m2.ui.utils.AUDIO_CREATION
import com.ih.m2.ui.utils.IMG_CREATION
import com.ih.m2.ui.utils.VIDEO_CREATION

enum class EvidenceType(type: String) {
    IMCR(IMG_CREATION),VICR(VIDEO_CREATION),AUCR(AUDIO_CREATION)
}