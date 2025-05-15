package com.example.foodshoptestcase.Activity.Dashboard

import android.content.Intent
import android.os.Bundle
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
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Activity.Cart.CartActivity
import com.example.foodshoptestcase.Activity.Favorite.FavoriteActivity
import com.example.foodshoptestcase.Activity.ListItems.FullListItemsActivity
import com.example.foodshoptestcase.Activity.Order.OrderActivity
import com.example.foodshoptestcase.Activity.Profile.ProfileActivity
import com.example.foodshoptestcase.Activity.Search.SearchActivity
import com.example.foodshoptestcase.Activity.SignIn.RegisterActivity
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ViewModel.MainViewModel
import com.google.firebase.auth.FirebaseAuth

class MainActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            DashboardScreen(
                onCartClick = { startActivity(Intent(this, CartActivity::class.java)) },
                onSearchClick = { startActivity(Intent(this, SearchActivity::class.java)) },
                onAllItemClick = { startActivity(Intent(this, FullListItemsActivity::class.java)) },
                onFavoriteClick = { startActivity(Intent(this, FavoriteActivity::class.java)) },
                onOrderClick = { startActivity(Intent(this, OrderActivity::class.java)) },
                onProfileClick = { startActivity(Intent(this, ProfileActivity::class.java)) }
            )
        }
    }
}

@Composable
fun DashboardScreen(
    onCartClick: () -> Unit,
    onFavoriteClick: () -> Unit,
    onOrderClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSearchClick: () -> Unit,
    onAllItemClick: () -> Unit
) {
    val viewModel: MainViewModel = viewModel()
    val banners by viewModel.banners.observeAsState(initial = mutableListOf())
    val categories by viewModel.categories.observeAsState(initial = mutableListOf())
    val bestSeller by viewModel.items.observeAsState(initial = mutableListOf())
    val context = LocalContext.current
    val user by remember { mutableStateOf(FirebaseAuth.getInstance().currentUser) }

    // Проверка авторизации
    LaunchedEffect(Unit) {
        if (user == null) {
            context.startActivity(Intent(context, RegisterActivity::class.java))
            (context as? BaseActivity)?.finish()
        }
    }

    var showBannerLoading by remember { mutableStateOf(banners.isEmpty()) }
    var showCategoryLoading by remember { mutableStateOf(categories.isEmpty()) }
    var showBestSellerLoading by remember { mutableStateOf(bestSeller.isEmpty()) }

    LaunchedEffect(banners) {
        showBannerLoading = banners.isEmpty()
    }
    LaunchedEffect(categories) {
        showCategoryLoading = categories.isEmpty()
    }
    LaunchedEffect(bestSeller) {
        showBestSellerLoading = bestSeller.isEmpty()
    }

    ConstraintLayout(modifier = Modifier.background(Color.White)) {
        val (scrollList, bottomMenu) = createRefs()

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .constrainAs(scrollList) {
                    top.linkTo(parent.top)
                    bottom.linkTo(parent.bottom)
                    end.linkTo(parent.end)
                    start.linkTo(parent.start)
                }
        ) {
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
                        Text(
                            text = user?.displayName ?: "Гость",
                            color = Color.Black,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Row {
                        Image(
                            painter = painterResource(R.drawable.search_icon),
                            contentDescription = null,
                            modifier = Modifier.clickable { onSearchClick() }
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Image(
                            painter = painterResource(R.drawable.bell_icon),
                            contentDescription = null
                        )
                    }
                }
            }
            item {
                if (showBannerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    Banners(banners)
                }
            }
            item {
                Text(
                    "Категории",
                    color = Color.Black,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                )
            }
            item {
                if (showCategoryLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
                    CategoryList(categories)
                }
            }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 36.dp)
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Лучшие продукты",
                        color = Color.Black,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Все продукты",
                        color = colorResource(R.color.midBrown),
                        modifier = Modifier.clickable { onAllItemClick() }
                    )
                }
            }
            item {
                if (showBestSellerLoading) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else {
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
            onCartClick = onCartClick,
            onFavoriteClick = onFavoriteClick,
            onOrderClick = onOrderClick,
            onProfileClick = onProfileClick
        )
    }
}