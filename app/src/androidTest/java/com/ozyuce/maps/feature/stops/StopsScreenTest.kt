package com.ozyuce.maps.feature.stops

import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.ozyuce.maps.BaseComposeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class StopsScreenTest : BaseComposeTest() {

    @Before
    override fun setup() {
        super.setup()
        navigateToStops(composeTestRule)
    }

    @Test
    fun whenSearching_listIsFiltered() {
        composeTestRule.onNodeWithTag("stops_search_field")
            .performClick()
            .performTextInput("Personel 1")

        composeTestRule.onNodeWithTag("stops_search_field")
            .assertTextContains("Personel 1")

        composeTestRule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                composeTestRule.onAllNodesWithTag("person_item_1").fetchSemanticsNodes()
            }.getOrDefault(emptyList()).isEmpty()
        }

        composeTestRule.onNodeWithTag("person_item_0")
            .assertIsDisplayed()
    }

    @Test
    fun whenStatusChanged_snackbarAndUndoDisplayed() {
        composeTestRule.onNodeWithTag("person_item_0").performClick()

        composeTestRule.onNodeWithText("Personel 1 durumu güncellendi")
            .assertIsDisplayed()
        composeTestRule.onNodeWithText("Geri Al")
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("person_item_0")
            .assertTextContains("Gelmedi", substring = true)
    }

    @Test
    fun whenUndoClicked_statusIsReverted() {
        composeTestRule.onNodeWithTag("person_item_0").performClick()
        composeTestRule.onNodeWithText("Geri Al").performClick()

        composeTestRule.onNodeWithTag("person_item_0")
            .assertTextContains("Bindi", substring = true)
    }

    private fun navigateToStops(rule: ComposeTestRule) {
        rule.onNodeWithTag("nav_stops").performClick()
        rule.waitForIdle()
    }
}
