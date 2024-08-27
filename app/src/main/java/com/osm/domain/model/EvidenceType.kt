package com.osm.domain.model

import com.osm.ui.utils.AUDIO_CLOSE
import com.osm.ui.utils.AUDIO_CREATION
import com.osm.ui.utils.AUDIO_PS
import com.osm.ui.utils.IMG_CLOSE
import com.osm.ui.utils.IMG_CREATION
import com.osm.ui.utils.IMG_PS
import com.osm.ui.utils.VIDEO_CLOSE
import com.osm.ui.utils.VIDEO_CREATION
import com.osm.ui.utils.VIDEO_PS

enum class EvidenceType(type: String) {
    IMCR(IMG_CREATION),
    VICR(VIDEO_CREATION),
    AUCR(AUDIO_CREATION),
    IMCL(IMG_CLOSE),
    VICL(VIDEO_CLOSE),
    AUCL(AUDIO_CLOSE),
    IMPS(IMG_PS),
    AUPS(AUDIO_PS),
    VIPS(VIDEO_PS)
}