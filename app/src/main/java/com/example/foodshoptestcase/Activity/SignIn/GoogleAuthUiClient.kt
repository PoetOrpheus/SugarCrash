package com.example.foodshoptestcase.Activity.SignIn

import android.content.Context
import android.content.Intent
import android.content.IntentSender
import android.util.Log
import com.example.foodshoptestcase.R
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.auth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

@Suppress("DEPRECATION")
class GoogleAuthUiClient(
    private val context: Context,
    private val oneTapClient: SignInClient
) {
    private val auth = Firebase.auth


    //Функция для начала процесса входа в систему
    suspend fun signIn(): IntentSender?{
        val result=try{
            oneTapClient.beginSignIn(
                buildSignInRequest()
            ).await()
        } catch (e:Exception){
            e.printStackTrace()
            Log.e("GoogleSignIn","Sign in failed",e)
            if (e is CancellationException) throw e
            null
        }
        return result?.pendingIntent?.intentSender
    }

    //Функция для завершения процесса входа с Intent
    suspend fun signInWithIntent(intent: Intent):SignInResult{
        //Получение учетных данных из Intent
        val credential=oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken=credential.googleIdToken
        val googleCredentials=GoogleAuthProvider.getCredential(googleIdToken,null)

        //Попытка войти в Firebase с полученными учетными данными
        return try{
            val user=auth.signInWithCredential(googleCredentials).await().user
            Log.d("GoogleSignIn","Sign in successful:${user?.displayName}")
            SignInResult(
                data = user?.run {
                    UserData(
                        userId = uid,
                        username = displayName,
                        profilePictureUrl = photoUrl?.toString()
                    )
                },
                errorMessage = null
            )
        }catch (e:Exception){
            Log.e("GoogleSignIn","Sign in failed", e)
            e.printStackTrace()
            if (e is CancellationException) throw e
            SignInResult(
                data = null,
                errorMessage = e.message
            )
        }
    }

    //Функция для выхода из системы
    suspend fun signOut(){
        try {
            oneTapClient.signOut().await()
            auth.signOut()
            Log.d("GoogleSignIn","Sign out successful")
        } catch (e:Exception){
            Log.e("GoogleSignIn","Sign out failed",e)
            e.printStackTrace()
            if (e is CancellationException) throw e
        }
    }

    //Функция для получения данных о текущем авторизованном пользователе
    fun getSignedInUser():UserData?=auth.currentUser?.run {
        UserData(
            userId = uid,
            username = displayName,
            profilePictureUrl = photoUrl?.toString()
        )
    }

    //Построение запроса на вход
    private fun buildSignInRequest(): BeginSignInRequest {
        return BeginSignInRequest.builder()
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    .setServerClientId(context.getString(R.string.web_client_id))
                    .setFilterByAuthorizedAccounts(false) // Показывать все учетные записи
                    .build()
            )
            .build()
    }
}