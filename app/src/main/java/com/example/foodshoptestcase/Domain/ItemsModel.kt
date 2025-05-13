package com.example.foodshoptestcase.Domain

import java.io.Serializable

data class ItemsModel(
    var title:String="",
    var description:String="",
    var picUrl:ArrayList<String> =ArrayList(),
    var size:ArrayList<String> = ArrayList(),
    var price:Double=0.0,
    var rating:Double=0.0,
    var numberInCart:Int=0,
    var showRecommended:Boolean=false,
    var categoryId:String="",
    var sellerName:String="",
    var sellerPic:String="",
    var sellerTell:Int=0,
    var lovers:Boolean=false
):Serializable {
}
