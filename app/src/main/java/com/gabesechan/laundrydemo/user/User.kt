package com.gabesechan.laundrydemo.user

import kotlinx.serialization.Serializable

@Serializable
class User(
   val name: String,
   val email: String?,
   val phone: String?,
   val addresses: List<Address>) {

   companion object {
       val  NoUser = User("", "", "", emptyList())
   }

   fun isLoggedIn(): Boolean {
      return this != NoUser
   }
}

@Serializable
data class Address(
   val id: String,
   val street1: String,
   val street2: String?,
   val city: String,
   val state: String,
   val country: String,
   val postcode: String
)