package com.ozyuce.maps.feature.map

import android.Manifest
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import com.ozyuce.maps.BaseComposeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import androidx.test.rule.GrantPermissionRule

@HiltAndroidTest
class MapScreenTest : BaseComposeTest() {

    @get:Rule(order = 2)
    val permissionRule: GrantPermissionRule =
        GrantPermissionRule.grant(Manifest.permission.ACCESS_FINE_LOCATION)

    @Before
    override fun setup() {
        super.setup()
        navigateToMap(composeTestRule)
    }

    @Test
    fun whenScreenOpens_bottomSheetIsVisible() {
        composeTestRule.onNodeWithTag("map_bottom_sheet")
            .assertIsDisplayed()
    }

    @Test
    fun bottomSheetCanBeDismissed() {
        composeTestRule.onNodeWithTag("map_bottom_sheet_handle")
            .performTouchInput {
                val start = center
                val end = Offset(start.x, start.y + 600f)
                swipe(start = start, end = end, durationMillis = 600)
            }

        waitForBottomSheetDismissal()
    }

    @Test
    fun afterNavigatingBackToMap_bottomSheetOpensAgain() {
        composeTestRule.onNodeWithTag("map_bottom_sheet_handle")
            .performTouchInput {
                val start = center
                val end = Offset(start.x, start.y + 600f)
                swipe(start = start, end = end, durationMillis = 600)
            }

        waitForBottomSheetDismissal()

        composeTestRule.onNodeWithTag("nav_dashboard").performClick()
        composeTestRule.waitForIdle()
        composeTestRule.onNodeWithTag("nav_map").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithTag("map_bottom_sheet")
            .assertIsDisplayed()
    }

    private fun waitForBottomSheetDismissal() {
        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeTestRule.onAllNodesWithTag("map_bottom_sheet").fetchSemanticsNodes()
            }.map { it.isEmpty() }.getOrDefault(true)
        }
    }

    private fun navigateToMap(rule: ComposeTestRule) {
        rule.onNodeWithTag("nav_map").performClick()
        rule.waitForIdle()
    }
}

