package com.ozyuce.maps.feature.reports.components.charts

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ozyuce.maps.feature.reports.ChartData
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ChartsTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun donutChart_showsCorrectLegend() {
        val testData = listOf(
            ChartData("Test1", 50, 0xFF10B981),
            ChartData("Test2", 30, 0xFFEF4444)
        )

        composeTestRule.setContent {
            DonutChart(data = testData)
        }

        composeTestRule.onNodeWithText("Test1: 50").assertExists()
        composeTestRule.onNodeWithText("Test2: 30").assertExists()
        composeTestRule.onNodeWithText("80").assertExists() // Toplam
    }

    @Test
    fun barChart_showsCorrectLabels() {
        val testData = listOf(
            ChartData("08:00", 25, 0xFF10B981),
            ChartData("09:00", 45, 0xFF10B981)
        )

        composeTestRule.setContent {
            BarChart(data = testData)
        }

        composeTestRule.onNodeWithText("08:00").assertExists()
        composeTestRule.onNodeWithText("09:00").assertExists()
    }
}
