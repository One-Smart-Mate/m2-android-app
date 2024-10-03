package com.ih.osm.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.ih.osm.ui.theme.PaddingToolbar

@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun <T> PullToRefreshLazyColumn(
    items: List<T>,
    content: @Composable (T) -> Unit,
    header: @Composable () -> Unit,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier,
    lazyListState: LazyListState = rememberLazyListState()
) {
    val pullToRefreshState =
        rememberPullRefreshState(
            onRefresh = onRefresh,
            refreshing = isRefreshing
        )
    Box(
        modifier =
        modifier
            .pullRefresh(pullToRefreshState)
    ) {
        LazyColumn(
            state = lazyListState,
            modifier =
            Modifier
                .fillMaxSize()
        ) {
            stickyHeader {
                header()
            }
            item {
                AnimatedVisibility(visible = items.isEmpty()) {
                    EmptyData(modifier = Modifier.fillMaxSize().padding(top = PaddingToolbar))
                }
            }
            items(items) {
                content(it)
            }
        }

        PullRefreshIndicator(
            state = pullToRefreshState,
            modifier =
            Modifier
                .align(Alignment.TopCenter),
            refreshing = isRefreshing
        )
    }
}
