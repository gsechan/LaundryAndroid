# LaundryAndroid

This is an Android app I developed as a POC for a friend's startup.  It was meant to be part of a multi-tenant POS system for laundromats and dry cleaners.  In particular, this was the mobile app component meant for customers to download and order pickup and delivery. The app was mostly hand coded, although AI assiestance was used in parts, particularly generating tests.

The general architecture is a Jetpack Compose front end uisng Jetpack Navigation to go between screens.  Screens use an MVVM pattern.  Retrofit was used for the networking layer.  Please note that the checked in version hits a local dev server, not our public test realm.  So to try this app out, you will also need to run the server repo, including Postgres as a database.

Each package should have it's own documentation file, please read there to understand how that section works.

For this root directory, we hold only the MainActivity and root composable.  The MainActivity simply delays the end of the splash sreen until we've validated our saved login info, and displays MainScreenComposable when done.  MainScreenComposable sets up the navigation graph to either be the set of all logged in screens, or the set of all the logged out screens, and points you at the entry point for either experience.  It collects the logged in user as state, so if you ever log in or out, you are instantly moved to the other navigation graph.


