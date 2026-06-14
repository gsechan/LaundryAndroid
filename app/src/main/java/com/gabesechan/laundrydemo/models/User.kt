package com.gabesechan.laundrydemo.models

import kotlinx.serialization.Serializable

@Serializable
data class User(
   val name: String,
   val email: String?,
   val phone: String,
   val addresses: List<Address>) {

   companion object {
       val  NoUser = User("", "", "", emptyList())
   }

   fun isLoggedIn(): Boolean {
      return this != NoUser
   }
}
