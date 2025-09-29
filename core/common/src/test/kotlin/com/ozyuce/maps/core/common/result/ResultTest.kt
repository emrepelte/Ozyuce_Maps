package com.ozyuce.maps.core.common.result.Result

import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class ResultTest {

    @Test
    fun `success result should return data`() {
        val result = Result.success("test")
        assertTrue(result.isSuccess)
        assertEquals("test", result.getOrNull())
        assertEquals("test", result.getOrThrow())
    }

    @Test
    fun `error result should return exception`() {
        val exception = Exception("test error")
        val result = Result.error(exception)
        assertTrue(result.isError)
        assertNull(result.getOrNull())
        try {
            result.getOrThrow()
            fail("Should throw exception")
        } catch (e: Exception) {
            assertEquals(exception, e)
        }
    }

    @Test
    fun `loading result should return null`() {
        val result = Result.loading()
        assertTrue(result.isLoading)
        assertNull(result.getOrNull())
        try {
            result.getOrThrow()
            fail("Should throw exception")
        } catch (e: Exception) {
            assertTrue(e is IllegalStateException)
        }
    }

    @Test
    fun `fold should execute correct lambda`() = runBlocking {
        val successResult = Result.success("test")
        val errorResult = Result.error(Exception("test error"))
        val loadingResult = Result.loading()

        val successValue = successResult.fold(
            onSuccess = { "success: $it" },
            onError = { "error: ${it.message}" },
            onLoading = { "loading" }
        )
        assertEquals("success: test", successValue)

        val errorValue = errorResult.fold(
            onSuccess = { "success: $it" },
            onError = { "error: ${it.message}" },
            onLoading = { "loading" }
        )
        assertEquals("error: test error", errorValue)

        val loadingValue = loadingResult.fold(
            onSuccess = { "success: $it" },
            onError = { "error: ${it.message}" },
            onLoading = { "loading" }
        )
        assertEquals("loading", loadingValue)
    }
}
