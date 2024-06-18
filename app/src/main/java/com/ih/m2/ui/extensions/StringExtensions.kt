package com.ih.m2.ui.extensions

import android.content.Context
import com.ih.m2.R
import com.ih.m2.ui.utils.ALL_OPEN_CARDS
import com.ih.m2.ui.utils.ASSIGNED_CARDS
import com.ih.m2.ui.utils.CLEAN_FILTERS
import com.ih.m2.ui.utils.CLOSED_CARDS
import com.ih.m2.ui.utils.EXPIRED_CARDS
import com.ih.m2.ui.utils.MY_OPEN_CARDS
import com.ih.m2.ui.utils.UNASSIGNED_CARDS


fun String.toFilterStatus(context: Context): String {
    return when (this) {
        context.getString(R.string.all_open_cards) -> ALL_OPEN_CARDS
        context.getString(R.string.my_open_cards) -> MY_OPEN_CARDS
        context.getString(R.string.assigned_cards) -> ASSIGNED_CARDS
        context.getString(R.string.unassigned_cards) -> UNASSIGNED_CARDS
        context.getString(R.string.expired_cards) -> EXPIRED_CARDS
        context.getString(R.string.closed_cards) -> CLOSED_CARDS
        CLEAN_FILTERS -> CLEAN_FILTERS
        else -> ALL_OPEN_CARDS
    }
}