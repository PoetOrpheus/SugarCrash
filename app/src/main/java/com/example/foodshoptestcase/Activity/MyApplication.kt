package com.example.foodshoptestcase.Activity

import android.app.Application
import com.example.foodshoptestcase.Repository.MainRepository
import com.google.firebase.database.FirebaseDatabase

class MyApplication : Application() {
    lateinit var repository: MainRepository

    override fun onCreate() {
        super.onCreate()
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        repository = MainRepository.getInstance(this)
        repository.refreshDataFromFirebase()
    }
}