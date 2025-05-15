package com.example.foodshoptestcase.Repository

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodshoptestcase.Domain.CategoryModel
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.Domain.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.firebase.database.*
import com.google.gson.reflect.TypeToken

class MainRepository private constructor(context: Context) {

    private val firebaseDatabase=FirebaseDatabase.getInstance("https://foodshoptestcasefirebase-default-rtdb.firebaseio.com/")
    private val sharedPreferences = context.getSharedPreferences("app_cache", Context.MODE_PRIVATE)
    private val gson = Gson()

    // LiveData для каждого типа данных
    private val _banners = MutableLiveData<MutableList<SliderModel>>()
    val banners: LiveData<MutableList<SliderModel>> = _banners
    private val _categories = MutableLiveData<MutableList<CategoryModel>>()
    val categories: LiveData<MutableList<CategoryModel>> = _categories
    private val _items = MutableLiveData<MutableList<ItemsModel>>()
    val items: LiveData<MutableList<ItemsModel>> = _items

    init {
        loadFromCache()
    }

    private fun loadFromCache() {
        //Загрузка баннеров
        val bannersJson=sharedPreferences.getString("cached_banners",null)
        if (bannersJson!=null){
            val type=object :TypeToken<MutableList<SliderModel>>(){}.type
            _banners.value=gson.fromJson(bannersJson,type)
        }

        //Загрузка категорий
        val categoriesJson=sharedPreferences.getString("cached_categories",null)
        if (categoriesJson!=null){
            val type=object :TypeToken<MutableList<CategoryModel>>(){}.type
            _categories.value=gson.fromJson(categoriesJson,type)
        }

        //Загрузка эллементов
        val itemsJson=sharedPreferences.getString("cached_items",null)
        if (itemsJson!=null){
            val type=object : TypeToken<MutableList<ItemsModel>>(){}.type
            _items.value=gson.fromJson(itemsJson,type)
        }
    }

    fun refreshDataFromFirebase() {
        // Загрузка баннеров
        loadBannersFromFirebase { newBanners ->
            val newBannersJson = gson.toJson(newBanners)
            val cachedBannersJson = sharedPreferences.getString("cached_banners", null)
            if (cachedBannersJson == null || cachedBannersJson != newBannersJson) {
                sharedPreferences.edit().putString("cached_banners", newBannersJson).apply()
                _banners.postValue(newBanners)
            }
        }

        // Загрузка категорий
        loadCategoriesFromFirebase { newCategories ->
            val newCategoriesJson = gson.toJson(newCategories)
            val cachedCategoriesJson = sharedPreferences.getString("cached_categories", null)
            if (cachedCategoriesJson == null || cachedCategoriesJson != newCategoriesJson) {
                sharedPreferences.edit().putString("cached_categories", newCategoriesJson).apply()
                _categories.postValue(newCategories)
            }
        }

        // Загрузка элементов
        loadItemsFromFirebase { newItems ->
            val newItemsJson = gson.toJson(newItems)
            val cachedItemsJson = sharedPreferences.getString("cached_items", null)
            if (cachedItemsJson == null || cachedItemsJson != newItemsJson) {
                sharedPreferences.edit().putString("cached_items", newItemsJson).apply()
                _items.postValue(newItems)
            }
        }
    }

    private fun loadBannersFromFirebase(callback: (MutableList<SliderModel>) -> Unit) {
        val query = firebaseDatabase.getReference("Banner")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val banners = mutableListOf<SliderModel>()
                for (ds in snapshot.children) {
                    val banner = ds.getValue(SliderModel::class.java)
                    banner?.let { banners.add(it) }
                }
                callback(banners)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    private fun loadCategoriesFromFirebase(callback: (MutableList<CategoryModel>) -> Unit) {
        val query = firebaseDatabase.getReference("Category")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val categories = mutableListOf<CategoryModel>()
                for (ds in snapshot.children) {
                    val category = ds.getValue(CategoryModel::class.java)
                    category?.let { categories.add(it) }
                }
                callback(categories)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    private fun loadItemsFromFirebase(callback: (MutableList<ItemsModel>) -> Unit) {
        val query = firebaseDatabase.getReference("Items")
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<ItemsModel>()
                for (ds in snapshot.children) {
                    val item = ds.getValue(ItemsModel::class.java)
                    item?.let { items.add(it) }
                }
                callback(items)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(mutableListOf())
            }
        })
    }

    fun getFilteredItems(id: String): LiveData<MutableList<ItemsModel>> {
        val filteredLiveData = MediatorLiveData<MutableList<ItemsModel>>()
        filteredLiveData.addSource(items) { allItems ->
            filteredLiveData.value = allItems?.filter { it.categoryId == id }?.toMutableList()
        }
        return filteredLiveData
    }

    companion object {
        @Volatile
        private var instance: MainRepository? = null

        fun getInstance(context: Context): MainRepository {
            return instance ?: synchronized(this) {
                instance ?: MainRepository(context).also { instance = it }
            }
        }
    }
}