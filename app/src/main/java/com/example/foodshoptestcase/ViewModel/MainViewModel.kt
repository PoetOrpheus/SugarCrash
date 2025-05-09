package com.example.foodshoptestcase.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import com.example.foodshoptestcase.Domain.CategoryModel
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.Domain.SliderModel
import com.example.foodshoptestcase.Repository.MainRepository

class MainViewModel():ViewModel() {
    private val repository=MainRepository()

    fun loadBanner():LiveData<MutableList<SliderModel>>{
        return repository.loadBanner()
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        return repository.loadCategory()
    }

    fun loadBestSeller():LiveData<MutableList<ItemsModel>>{
        return repository.loadBestSeller()
    }
}