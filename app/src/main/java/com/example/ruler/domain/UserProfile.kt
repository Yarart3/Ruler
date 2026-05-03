package com.example.ruler.domain

data class UserProfile(
    val id: String,
    val email: String,
    val username: String,
    val birthDate: String,
    val address: String,
    val country: String,
    val phone: String,
    val acceptsMarketingEmails: Boolean
)

fun UserProfile.hasRequiredLocalData(): Boolean {
    return username.isNotBlank() &&
        birthDate.isNotBlank() &&
        address.isNotBlank() &&
        country.isNotBlank() &&
        phone.isNotBlank()
}
