package com.ozyuce.maps.feature.stops.domain.validation

private val PHONE_REGEX = Regex("^05\\d{9}$")

object PhoneNumberValidator {
    fun validate(rawPhoneNumber: String?): Boolean {
        if (rawPhoneNumber.isNullOrBlank()) return true
        val digits = extractDigits(rawPhoneNumber)
        val normalized = normalize(digits) ?: return false
        return PHONE_REGEX.matches(normalized)
    }

    fun format(rawPhoneNumber: String?): String? {
        if (rawPhoneNumber.isNullOrBlank()) return null
        val digits = extractDigits(rawPhoneNumber)
        val normalized = normalize(digits) ?: return null
        return buildString {
            normalized.forEachIndexed { index, char ->
                append(char)
                when (index) {
                    3, 6, 8 -> if (index != normalized.lastIndex) append(' ')
                }
            }
        }
    }

    private fun extractDigits(input: String): String =
        input.filter(Char::isDigit)

    private fun normalize(digits: String): String? {
        if (digits.isEmpty()) return null
        var sanitized = digits

        sanitized = when {
            sanitized.startsWith("90") -> sanitized.removePrefix("90")
            sanitized.startsWith("0") -> sanitized.removePrefix("0")
            else -> sanitized
        }

        if (sanitized.length != 10) return null
        if (!sanitized.startsWith('5')) return null

        return "0$sanitized"
    }
}

