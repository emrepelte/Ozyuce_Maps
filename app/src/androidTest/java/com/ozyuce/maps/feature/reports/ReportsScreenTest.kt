package com.ozyuce.maps.feature.reports

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.ComposeTestRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.ozyuce.maps.BaseComposeTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Before
import org.junit.Test

@HiltAndroidTest
class ReportsScreenTest : BaseComposeTest() {

    @Before
    override fun setup() {
        super.setup()
        navigateToReports(composeTestRule)
    }

    @Test
    fun whenFilterTypeChanges_kpisAreUpdated() {
        composeTestRule.onNodeWithTag("reports_kpi_grid")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Hafta").performClick()

        waitUntilTextVisible(composeTestRule, "52s 10dk")
        composeTestRule.onNodeWithText("865").assertIsDisplayed()
    }

    @Test
    fun whenFilterTypeChanges_chartsReflectNewData() {
        composeTestRule.onNodeWithText("Ay").performClick()

        waitUntilTextVisible(composeTestRule, "4. Hafta")
        composeTestRule.onNodeWithTag("reports_bar_chart")
            .performScrollTo()
            .assertIsDisplayed()
    }

    private fun navigateToReports(rule: ComposeTestRule) {
        rule.onNodeWithTag("nav_reports").performClick()
        rule.waitForIdle()
    }

    private fun waitUntilTextVisible(rule: ComposeTestRule, text: String) {
        rule.waitUntil(timeoutMillis = 5_000) {
            runCatching {
                rule.onNodeWithText(text, useUnmergedTree = true).fetchSemanticsNode()
            }.isSuccess
        }
    }
}
