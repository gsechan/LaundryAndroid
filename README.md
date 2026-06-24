# LaundryAndroid

This is an Android app I developed as a POC for a friend's startup.  It was meant to be part of a multi-tenant POS system for laundromats and dry cleaners.  In particular, this was the mobile app component meant for customers to download and order pickup and delivery. The app was mostly hand coded, although AI assistance was used in parts, particularly generating tests.

The general architecture is a Jetpack Compose front end using Jetpack Navigation to go between screens.  Screens use an MVVM pattern.  Retrofit was used for the networking layer.  Please note that the checked in version hits a local dev server, not our public test realm.  So to try this app out, you will also need to run the server repo, including Postgres as a database.  The matching server is in the LaundryDemo project on my github
