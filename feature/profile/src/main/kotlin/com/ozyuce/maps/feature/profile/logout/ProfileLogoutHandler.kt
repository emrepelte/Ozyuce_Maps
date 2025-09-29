package com.ozyuce.maps.feature.profile.logout

import com.ozyuce.maps.core.common.result.Result

/**
 * Abstraction to execute the logout flow from the profile feature without
 * coupling to app-level authentication details.
 */
interface ProfileLogoutHandler {
    suspend fun logout(): Result<Unit>
}
