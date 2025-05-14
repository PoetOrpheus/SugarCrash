package com.example.foodshoptestcase.Activity.Dashboard

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Activity.Cart.CartActivity
import com.example.foodshoptestcase.Activity.Favorite.FavoriteActivity
import com.example.foodshoptestcase.Activity.ListItems.FullListItemsActivity
import com.example.foodshoptestcase.Activity.Order.OrderActivity
import com.example.foodshoptestcase.Activity.Profile.ProfileActivity
import com.example.foodshoptestcase.Activity.Search.SearchActivity
import com.example.foodshoptestcase.Domain.CategoryModel
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.Domain.SliderModel
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ViewModel.MainViewModel

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen(
                onCartClick = { startActivity(Intent(this, CartActivity::class.java)) },
                onSearchClick = { startActivity(Intent(this,SearchActivity::class.java)) },
                onAllItemClick = { startActivity(Intent(this,FullListItemsActivity::class.java))},
                onFavoriteClick = {startActivity(Intent(this,FavoriteActivity::class.java))},
                onOrderClick = {startActivity(Intent(this,OrderActivity::class.java))},
                onProfileClick = {startActivity(Intent(this,ProfileActivity::class.java))}
            )
        }
    }
}



@Composable
fun DashboardScreen(
    onCartClick:()->Unit,
    onFavoriteClick:()->Unit,
    onOrderClick:()->Unit,
    onProfileClick:()->Unit,
    onSearchClick:()->Unit,
    onAllItemClick:()->Unit
) {
    val viewModel = MainViewModel()
    val banners = remember { mutableStateListOf<SliderModel>() }
    val categories = remember { mutableStateListOf<CategoryModel>() }
    val bestSeller = remember { mutableStateListOf<ItemsModel>() }

    var showBannerLoading by remember { mutableStateOf(true) }
    var showCategoryLoading by remember { mutableStateOf(true) }
    var showBestSellerLoading by remember { mutableStateOf(true) }

    //banner
    LaunchedEffect(Unit) {
        viewModel.loadBanner().observeForever {
            banners.clear()
            banners.addAll(it)
            showBannerLoading=false
            Log.e("banner","Баннер должен был загрузиться в launchedEffect\n" +
                    "showBannerLoading:$showBannerLoading")
        }
    }



    //category
    LaunchedEffect(Unit) {
        viewModel.loadCategory().observeForever {
            categories.clear()
            categories.addAll(it)
            showCategoryLoading=false
            Log.e("category","Категории должны был загрузиться в launchedEffect\n" +
                    "showCategoryLoading:$showCategoryLoading")
        }
    }

    //BestSeller
    LaunchedEffect(Unit) {
        viewModel.loadBestSeller().observeForever {
            bestSeller.clear()
            bestSeller.addAll(it)
            showBestSellerLoading=false
            Log.e("bestSeller","Лучшие продукты должны был загрузиться в launchedEffect\n" +
                    "showCategoryLoading:$showBestSellerLoading")
        }
    }


    ConstraintLayout(modifier = Modifier.background(Color.White)) {
        val (scrollList, bottomMenu) = createRefs()

        LazyColumn(modifier = Modifier
            .fillMaxSize()
            .constrainAs(scrollList) {
                top.linkTo(parent.top)
                bottom.linkTo(parent.bottom)
                end.linkTo(parent.end)
                start.linkTo(parent.start)
            })
        {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 70.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(text = "С возвращением", color = Color.Black)
                        Text(text = "Мария",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold)

                    }
                    Row(){
                    Image(
                        painter = painterResource(R.drawable.search_icon),
                        contentDescription = null,
                        modifier = Modifier.clickable { onSearchClick() })
                    Spacer(modifier = Modifier.width(16.dp))
                        Image(painter = painterResource(R.drawable.bell_icon),
                            contentDescription = null)
                    }

                }
            }
            //Banners
            item {
                if(showBannerLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                else{
                    Log.e("banner","Сейчас должен загузиться баннер")
                    Banners(banners)
                }
            }

            item {
                Text("Категории ", color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp))
            }

            item{
                if (showCategoryLoading){
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                else{
                    CategoryList(categories)
                }
            }

            item{
                Row(modifier=Modifier
                    .fillMaxWidth()
                    .padding(top = 36.dp)
                    .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        text ="Лучшие продукты",
                        color=Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "Все продукты",
                        color= colorResource(R.color.midBrown),
                        modifier = Modifier.clickable {
                            onAllItemClick()
                        }

                    )
                }
            }

            item {
                if (showBestSellerLoading){
                    Box(
                        modifier=Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ){
                        CircularProgressIndicator()
                    }
                }
                else{
                    ListItems(bestSeller)
                }
            }
        }
        BottomMenu(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(bottomMenu) {
                    bottom.linkTo(parent.bottom)
                },
            onCartClick =onCartClick,
            onFavoriteClick=onFavoriteClick,
            onOrderClick=onOrderClick,
            onProfileClick=onProfileClick
        )

    }
}