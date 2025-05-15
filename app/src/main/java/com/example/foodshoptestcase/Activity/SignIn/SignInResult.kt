package com.example.foodshoptestcase.Activity.SignIn


data class SignInResult(
    val data:UserData?,
    val errorMessage:String?
)
data class UserData(
    val userId:String="19",
    val username:String?="Денис",
    val profilePictureUrl:String?="pICTURE"
)