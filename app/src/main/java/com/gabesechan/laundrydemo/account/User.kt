package com.gabesechan.laundrydemo.account

sealed class User(val id: String, val name: String, val email: String?, val phone: String?) {
   class RealUser(id_: String, name_: String, email_: String?, phone_: String?):
      User(id_, name_, email_, phone_)
   data object NoUser: User("", "", "", "")

   fun isLoggedIn(): Boolean {
      return !(this is NoUser)
   }
}