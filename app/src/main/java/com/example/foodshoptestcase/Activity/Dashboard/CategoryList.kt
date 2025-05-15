package com.example.foodshoptestcase.Activity.Dashboard

import android.content.Intent
import android.os.Handler
import android.os.Looper
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.foodshoptestcase.Activity.ListItems.ListItemsActivity
import com.example.foodshoptestcase.Domain.CategoryModel
import com.example.foodshoptestcase.R

@Composable
fun CategoryList(categories: MutableList<CategoryModel>) {
    var selectedIndex by remember { mutableStateOf(-1) }
    val context = LocalContext.current

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp)
    )
    {
        items(categories.size) { index ->
            Category(item = categories[index], isSelected = selectedIndex == index,
                onItemClick = {
                    selectedIndex = index
                    Handler(Looper.getMainLooper()).postDelayed({
                        val intent=Intent(context, ListItemsActivity::class.java).apply {
                            putExtra("id",categories[index].id.toString())
                            putExtra("title",categories[index].title)
                        }
                        startActivity(context,intent,null)
                    }, 500)
                }
            )

        }
    }
}

@Composable
fun Category(
    item: CategoryModel,
    isSelected: Boolean,
    onItemClick: () -> Unit
) {
    Column(
        modifier = Modifier.clickable(onClick = onItemClick),
        horizontalAlignment = Alignment.CenterHorizontally ){
        AsyncImage(
            model = (item.picUrl),
            contentDescription = item.title,
            modifier = Modifier
                .size(if (isSelected) 60.dp else (50.dp))
                .background(
                    color = if (isSelected) colorResource(R.color.brown) else colorResource(R.color.lightGrey),
                    shape = RoundedCornerShape(100.dp)
                ),
            contentScale = ContentScale.Inside
        )
        Spacer(modifier = Modifier.padding(top=8.dp))
        Text(
            text=item.title,
            color= colorResource(R.color.darkBrown),
            fontSize=12.sp,
            fontWeight=FontWeight.Bold
        )
    }
}