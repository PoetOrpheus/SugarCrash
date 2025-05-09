package com.example.foodshoptestcase.Repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.foodshoptestcase.Domain.CategoryModel
import com.example.foodshoptestcase.Domain.ItemModel
import com.example.foodshoptestcase.Domain.SliderModel
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class MainRepository {

    private val firebaseDatabase=FirebaseDatabase.getInstance("https://foodshoptestcasefirebase-default-rtdb.firebaseio.com/")

    fun loadBanner():LiveData<MutableList<SliderModel>>{
        val listData=MutableLiveData<MutableList<SliderModel>>()
        val ref=firebaseDatabase.getReference("Banner")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists= mutableListOf<SliderModel>()
                for(childSnapshot in snapshot.children){
                    val item= childSnapshot.getValue(SliderModel::class.java)
                    item?.let{lists.add(it)}
                }
                listData.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadCategory():LiveData<MutableList<CategoryModel>>{
        val listData=MutableLiveData<MutableList<CategoryModel>>()
        val ref=firebaseDatabase.getReference("Category")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists= mutableListOf<CategoryModel>()
                for(childSnapshot in snapshot.children){
                    val item= childSnapshot.getValue(CategoryModel::class.java)
                    item?.let{lists.add(it)}
                }
                listData.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }

    fun loadBestSeller():LiveData<MutableList<ItemModel>>{
        val listData=MutableLiveData<MutableList<ItemModel>>()
        val ref=firebaseDatabase.getReference("Items")

        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val lists= mutableListOf<ItemModel>()
                for(childSnapshot in snapshot.children){
                    val item= childSnapshot.getValue(ItemModel::class.java)
                    item?.let{lists.add(it)}
                }
                listData.value=lists
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }

        })
        return listData
    }
}