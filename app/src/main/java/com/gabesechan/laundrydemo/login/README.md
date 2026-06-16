## Overview

The login module has three main parts.  First, it stores the information about the logged in user (if any).  Second, it manages storage of the authentication token to authenticate subsequent requests.  Third, it has the UI for logging a user in and creating an account (a forgot password flow does not yet exist).

###  Authenticating with the server

TO authenticate with the server, we send a phone number and password to it.  The server will respond with a token.  That token is saved in the TokenStorage class which manaages both runtime access to it, and storing it for future launches of the client.  

###Authenticating per request

The token stored in token storage is read by the networking layer and attached automatically in the Authorization header as a bearer token.  No work is needed by any other module to send the token.  

###Accessing the current user

The UserRepository holds the current user.  If not logged in, it returns User.NoUser instead.  You can also check userRepository.current.isLoggedIn() to tell if the user is logged in or not.