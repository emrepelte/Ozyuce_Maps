package com.ozyuce.maps.core.ui.util

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

/**
 * Liste durumunu hatirlayan ve performans optimizasyonlari iceren yardimci fonksiyon
 */
@Composable
fun rememberOptimizedLazyListState(): LazyListState {
    val listState = rememberLazyListState()

    // Scroll durumunu takip et
    var isScrollInProgress by remember { mutableStateOf(false) }

    LaunchedEffect(listState) {
        snapshotFlow { listState.isScrollInProgress }
            .distinctUntilChanged()
            .filter { it }
            .collect {
                isScrollInProgress = true
            }
    }

    // Scroll durdugunda yapilacak islemler
    LaunchedEffect(isScrollInProgress) {
        if (!isScrollInProgress) {
            // Scroll durdugunda ek islemler yapilabilir
        }
    }

    return listState
}

/**
 * Liste ogelerinin stable key uretmesi icin yardimci fonksiyon
 */
@Composable
fun <T> stableKeyFor(item: T, keySelector: (T) -> Any): Any = remember(item) {
    keySelector(item)
}
