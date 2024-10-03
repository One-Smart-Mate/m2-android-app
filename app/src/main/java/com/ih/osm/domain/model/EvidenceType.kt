package com.ih.osm.domain.model

import com.ih.osm.ui.utils.AUDIO_CLOSE
import com.ih.osm.ui.utils.AUDIO_CREATION
import com.ih.osm.ui.utils.AUDIO_PS
import com.ih.osm.ui.utils.IMG_CLOSE
import com.ih.osm.ui.utils.IMG_CREATION
import com.ih.osm.ui.utils.IMG_PS
import com.ih.osm.ui.utils.VIDEO_CLOSE
import com.ih.osm.ui.utils.VIDEO_CREATION
import com.ih.osm.ui.utils.VIDEO_PS

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
