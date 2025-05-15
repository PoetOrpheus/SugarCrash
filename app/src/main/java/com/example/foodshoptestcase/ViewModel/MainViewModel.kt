package com.example.foodshoptestcase.ViewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodshoptestcase.Domain.CategoryModel
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.Domain.SliderModel
import com.example.foodshoptestcase.Repository.MainRepository

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = MainRepository.getInstance(application)

    val banners: LiveData<MutableList<SliderModel>> = repository.banners
    val categories: LiveData<MutableList<CategoryModel>> = repository.categories
    val items: LiveData<MutableList<ItemsModel>> = repository.items

    fun getFilteredItems(id: String): LiveData<MutableList<ItemsModel>> = repository.getFilteredItems(id)
}