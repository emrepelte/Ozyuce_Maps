package com.ozyuce.maps.feature.profile.logout

import com.ozyuce.maps.core.common.result.OzyuceResult

/**
 * Abstraction to execute the logout flow from the profile feature without
 * coupling to app-level authentication details.
 */
interface ProfileLogoutHandler {
    suspend fun logout(): OzyuceResult<Unit>
}
