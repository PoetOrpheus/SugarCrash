package com.example.foodshoptestcase.Activity.Favorite

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Activity.Datail.DetailActivity
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.Helper.ManagmentCart
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ui.theme.FoodShopTestCaseTheme

class FavoriteActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodShopTestCaseTheme {
                FavoriteScreen(
                    managmentCart = ManagmentCart(this),
                    onBackClick = { finish() },
                    onItemClick = { item ->
                        val intent = Intent(this, DetailActivity::class.java)
                        intent.putExtra("object", item)
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@Composable
fun FavoriteScreen(
    managmentCart: ManagmentCart,
    onBackClick: () -> Unit,
    onItemClick: (ItemsModel) -> Unit
) {
    val favoriteItems = remember { mutableStateOf(managmentCart.getFavoriteList()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
        ) {
            val (backBtn, title) = createRefs()
            Text(
                text = "Избранное",
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) { centerTo(parent) }
            )
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .clickable { onBackClick() }
            )
        }

        if (favoriteItems.value.isEmpty()) {
            Text(
                text = "Избранное пусто",
                color = Color.Black,
                fontSize = 18.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {
                items(favoriteItems.value) { item ->
                    FavoriteItemCard(
                        item = item,
                        onClick = { onItemClick(item) },
                        onToggleFavorite = {
                            managmentCart.toggleFavorite(item)
                            favoriteItems.value = managmentCart.getFavoriteList()
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun FavoriteItemCard(
    item: ItemsModel,
    onClick: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = rememberAsyncImagePainter(model = item.picUrl.firstOrNull() ?: ""),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .background(Color.LightGray, shape = RoundedCornerShape(4.dp))
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = 8.dp)
        ) {
            Text(
                text = item.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Text(
                text = "${item.price} ₽",
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Image(
            painter = painterResource(if (item.lovers) R.drawable.fav_icon_selected else R.drawable.fav_icon),
            contentDescription = null,
            modifier = Modifier
                .size(24.dp)
                .clickable { onToggleFavorite() }
        )
    }
}