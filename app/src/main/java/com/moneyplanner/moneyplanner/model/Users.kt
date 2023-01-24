package com.moneyplanner.moneyplanner.model


data class Users(
    var email: String? = null,
    val gendar: String? = null,
    val id: String? = null,
    val status: Boolean = false,
    val userName: String? = null,
    val userType: String? = null,
    val verified: Boolean = false,
)
