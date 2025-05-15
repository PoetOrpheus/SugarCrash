package com.example.foodshoptestcase.Activity.Search

import android.app.Application
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ViewModel.MainViewModel
import android.content.Intent
import androidx.compose.ui.text.style.TextOverflow
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshoptestcase.Activity.Datail.DetailActivity
import com.example.foodshoptestcase.Domain.ItemsModel

class SearchResultsActivity : AppCompatActivity() {
    private lateinit var query: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        query = intent.getStringExtra("query") as String
        setContent {
            SearchResultScreen(
                backClick = { finish() },
                searchQuery = query
            )
        }
    }
}

@Composable
private fun SearchResultScreen(
    backClick: () -> Unit,
    searchQuery: String
) {
    val viewModel: MainViewModel = viewModel()

    val allProducts by viewModel.items.observeAsState(initial = null)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp, start = 16.dp, end = 16.dp)
        ) {
            val (backBtn, titleTxt) = createRefs()

            Text(
                text = "Результаты поиска",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = colorResource(R.color.darkBrown),
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(titleTxt) { centerTo(parent) }
            )
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Назад",
                modifier = Modifier
                    .clickable { backClick() }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }

        AnimatedVisibility(
            visible = allProducts == null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = colorResource(R.color.midBrown))
            }
        }

        AnimatedVisibility(
            visible = allProducts != null,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            val filteredProducts by remember(searchQuery, allProducts) {
                derivedStateOf {
                    if (searchQuery.isNotEmpty() && allProducts != null) {
                        allProducts!!.filter { it.title.contains(searchQuery, ignoreCase = true) }
                    } else {
                        emptyList()
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 18.dp)
            ) {
                if (filteredProducts.isEmpty()) {
                    Text(
                        text = "Такого товара нет",
                        fontSize = 25.sp,
                        textAlign = TextAlign.Center,
                        fontWeight = FontWeight.Bold,
                        color = Color.Gray,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(vertical = 8.dp)
                    ) {
                        items(filteredProducts.size) { index ->
                            ProductCard(filteredProducts[index])
                        }
                    }
                }
            }
        }
    }
}
