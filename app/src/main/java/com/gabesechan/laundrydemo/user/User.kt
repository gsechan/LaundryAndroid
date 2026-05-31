package com.gabesechan.laundrydemo.user

sealed class User(
   val id: String,
   val name: String,
   val email: String?,
   val phone: String?,
   val addresses: List<Address>) {
   class RealUser(idVal: String, nameVal: String, emailVal: String?, phoneVal: String?, addressVal:List<Address>):
      User(idVal, nameVal, emailVal, phoneVal, addressVal )
   data object NoUser: User("", "", "", "", emptyList())

   fun isLoggedIn(): Boolean {
      return this !is NoUser
   }
}

data class Address(
   val street1: String,
   val street2: String?,
   val city: String,
   val state: String,
   val country: String,
   val postcode: String
)