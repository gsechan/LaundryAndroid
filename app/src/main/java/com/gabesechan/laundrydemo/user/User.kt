package com.gabesechan.laundrydemo.user

sealed class User(val id: String, val name: String, val email: String?, val phone: String?) {
   class RealUser(idVal: String, nameVal: String, emailVal: String?, phoneVal: String?):
      User(idVal, nameVal, emailVal, phoneVal)
   data object NoUser: User("", "", "", "")

   fun isLoggedIn(): Boolean {
      return this !is NoUser
   }
}