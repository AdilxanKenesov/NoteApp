package uz.gita.notesapp.utils

import com.google.firebase.FirebaseNetworkException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthRecentLoginRequiredException
import com.google.firebase.auth.FirebaseAuthUserCollisionException

/** Short, human message for an error shown in a snackbar. */
fun Throwable.userMessage(): String = when (this) {
    is FirebaseNetworkException -> "No internet connection"
    is FirebaseTooManyRequestsException -> "Too many attempts. Try again later"
    is FirebaseAuthUserCollisionException -> "This email is already registered"
    is FirebaseAuthInvalidUserException -> "No account with this email"
    is FirebaseAuthInvalidCredentialsException -> "Wrong email or password"
    is FirebaseAuthRecentLoginRequiredException -> "Please sign in again and retry"
    else -> message?.takeIf { it.isNotBlank() } ?: "Something went wrong"
}
