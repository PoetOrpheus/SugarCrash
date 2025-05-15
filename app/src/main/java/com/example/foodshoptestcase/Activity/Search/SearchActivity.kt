package com.example.foodshoptestcase.Activity.Search

import android.app.Application
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Activity.Datail.DetailActivity
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ViewModel.MainViewModel

class SearchActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SearchScreen(onBackClick = { finish() })
        }
    }
}

@Composable
fun SearchScreen(
    onBackClick: () -> Unit,
) {
    val viewModel: MainViewModel = viewModel()
    val context = LocalContext.current
    val allProducts by viewModel.items.observeAsState(initial = null)
    var searchQuery by remember { mutableStateOf("") }

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
                text = "Поиск",
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(titleTxt) { centerTo(parent) },
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                color = colorResource(R.color.darkBrown)
            )
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Назад",
                modifier = Modifier
                    .clickable { onBackClick() }
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            SearchBar(
                query = searchQuery,
                onQueryChange = { searchQuery = it },
                onSearchClick = {
                    val intent = Intent(context, SearchResultsActivity::class.java).apply {
                        putExtra("query", searchQuery)
                    }
                    context.startActivity(intent)
                }
            )

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

                Column {
                    if (searchQuery.isEmpty()) {
                        Text(
                            text = "Начните вводить запрос",
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    } else if (filteredProducts.isEmpty()) {
                        Text(
                            text = "Товары не найдены",
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(top = 8.dp),
                            color = Color.Gray,
                            fontSize = 16.sp
                        )
                    } else {
                        LazyColumn(
                            contentPadding = PaddingValues(vertical = 8.dp)
                        ) {
                            items(filteredProducts) { product ->
                                ProductItem(product, onClick = {
                                    val intent = Intent(context, DetailActivity::class.java).apply {
                                        putExtra("object", product)
                                    }
                                    context.startActivity(intent)
                                })
                                Divider(
                                    color = colorResource(R.color.lightGrey),
                                    thickness = 1.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(query: String, onQueryChange: (String) -> Unit, onSearchClick: () -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White),
        placeholder = { Text("Поиск товаров") },
        leadingIcon = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Иконка поиска",
                    tint = colorResource(R.color.midBrown)
                )
            }
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Очистить поиск",
                        tint = colorResource(R.color.midBrown)
                    )
                }
            }
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        keyboardActions = KeyboardActions(onAny = {onSearchClick()}),
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = colorResource(R.color.midBrown),
            unfocusedIndicatorColor = Color.Gray,
            cursorColor = colorResource(R.color.darkBrown)
        )
    )
}

@Composable
fun ProductItem(product: ItemsModel, onClick: () -> Unit) {
    Text(
        text = product.title,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable { onClick() },
        fontSize = 16.sp,
        color = colorResource(R.color.darkBrown)
    )
}