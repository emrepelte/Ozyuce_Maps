package com.ozyuce.maps.core.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.filters.LargeTest
import com.ozyuce.maps.core.ui.components.loading.Skeleton
import com.ozyuce.maps.core.ui.util.rememberOptimizedLazyListState
import com.ozyuce.maps.core.ui.util.stableKeyFor
import org.junit.Rule
import org.junit.Test

@LargeTest
class PerformanceTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testListPerformance() {
        val items = List(100) { "Item $it" }

        composeTestRule.setContent {
            LazyColumn(
                state = rememberOptimizedLazyListState(),
                modifier = Modifier.fillMaxSize()
            ) {
                items(
                    items = items,
                    key = { stableKeyFor(it) { item -> item } }
                ) { item ->
                    Text(text = item)
                }
            }
        }

        // Jank ?l??m? i?in frame timing
        composeTestRule
            .onRoot()
            .captureFrames(timeoutMillis = 5000) { frameStats ->
                // Jank y?zdesi hesaplama
                val totalFrames = frameStats.size
                val jankyFrames = frameStats.count { it.durationMillis > 16 } // 60 FPS i?in 16ms
                val jankPercentage = (jankyFrames.toFloat() / totalFrames) * 100

                // Jank y?zdesi %3'ten az olmal?
                assert(jankPercentage < 3.0f) {
                    "Jank percentage is too high: $jankPercentage%"
                }
            }
    }

    @Test
    fun testSkeletonLoading() {
        composeTestRule.setContent {
            Skeleton(
                isLoading = true,
                modifier = Modifier.fillMaxSize()
            ) {
                Text("Content")
            }
        }

        // Frame timing kontrol?
        composeTestRule
            .onRoot()
            .captureFrames(timeoutMillis = 2000) { frameStats ->
                val avgFrameTime = frameStats.map { it.durationMillis }.average()
                
                // Ortalama frame s?resi 16ms'den az olmal?
                assert(avgFrameTime < 16.0) {
                    "Average frame time is too high: ${avgFrameTime}ms"
                }
            }
    }
}
