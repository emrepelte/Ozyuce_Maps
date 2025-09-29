package com.ozyuce.maps.feature.dashboard

import androidx.compose.ui.test.assertHasClickAction
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.ozyuce.maps.BaseComposeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
class DashboardScreenTest : BaseComposeTest() {

    @Test
    fun whenServiceInactive_startButtonIsVisible() {
        composeTestRule.onNodeWithText("Servisi Başlat")
            .assertIsDisplayed()
            .assertIsEnabled()
            .assertHasClickAction()
    }

    @Test
    fun whenServiceStarted_stopButtonIsShown() {
        composeTestRule.onNodeWithText("Servisi Başlat").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Servisi Bitir")
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun whenServiceStopped_startButtonReturns() {
        composeTestRule.onNodeWithText("Servisi Başlat").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Servisi Bitir").performClick()
        composeTestRule.waitForIdle()

        composeTestRule.onNodeWithText("Servisi Başlat")
            .assertIsDisplayed()
            .assertIsEnabled()
    }
}
