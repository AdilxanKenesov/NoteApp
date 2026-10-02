package uz.gita.notesapp.utils

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.NoCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import kotlinx.coroutines.CancellationException

const val SERVER_CLIENT_ID =
    "188821018345-4j494fgjpfjlt3q6nm2cj5psu4lg3tv5.apps.googleusercontent.com"

suspend fun getGoogleIdToken(
    context: Context,
    credentialManager: CredentialManager
): String {
    val option = GetGoogleIdOption.Builder()
        .setFilterByAuthorizedAccounts(false)
        .setServerClientId(SERVER_CLIENT_ID)
        .setAutoSelectEnabled(true)
        .build()

    val request = GetCredentialRequest.Builder()
        .addCredentialOption(option)
        .build()

    val credential = credentialManager.getCredential(context, request).credential

    if (credential !is CustomCredential ||
        credential.type != GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
    ) {
        throw Exception("Google sign-in failed")
    }

    return GoogleIdTokenCredential.createFrom(credential.data).idToken
}

/**
 * Same as [getGoogleIdToken] but never throws. A user closing the account picker is not
 * an error, so that case fails with a null message and the screen stays quiet.
 */
suspend fun requestGoogleIdToken(
    context: Context,
    credentialManager: CredentialManager
): Result<String> = try {
    Result.success(getGoogleIdToken(context, credentialManager))
} catch (e: CancellationException) {
    throw e
} catch (e: GetCredentialCancellationException) {
    Result.failure(Exception(null as String?))
} catch (e: NoCredentialException) {
    Result.failure(Exception("No Google account on this device"))
} catch (e: Exception) {
    Result.failure(Exception("Google sign-in failed"))
}
