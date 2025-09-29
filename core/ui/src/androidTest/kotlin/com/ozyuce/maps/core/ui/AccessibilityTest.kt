package com.ozyuce.maps.core.ui

import androidx.compose.material3.Text
import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import com.ozyuce.maps.core.ui.util.accessibilityLabel
import com.ozyuce.maps.core.ui.util.accessibleClickable
import org.junit.Rule
import org.junit.Test

class AccessibilityTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun testAccessibleClickable() {
        var clicked = false
        composeTestRule.setContent {
            Text(
                text = "Test Button",
                modifier = Modifier.accessibleClickable(
                    onClick = { clicked = true },
                    contentDescription = "Test button description"
                )
            )
        }

        // Minimum boyut kontrol?
        composeTestRule
            .onNodeWithText("Test Button")
            .assertHeightIsAtLeast(48.dp)
            .assertWidthIsAtLeast(48.dp)

        // TalkBack etiketi kontrol?
        composeTestRule
            .onNodeWithContentDescription("Test button description")
            .assertExists()

        // T?klama kontrol?
        composeTestRule
            .onNodeWithText("Test Button")
            .performClick()
        assert(clicked)
    }

    @Test
    fun testAccessibilityLabel() {
        composeTestRule.setContent {
            Text(
                text = "Test Text",
                modifier = Modifier.accessibilityLabel(
                    label = "Test accessibility label"
                )
            )
        }

        composeTestRule
            .onNodeWithContentDescription("Test accessibility label")
            .assertExists()
    }
}
