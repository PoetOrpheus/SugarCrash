package com.example.foodshoptestcase.Activity.Dashboard

import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import coil.compose.AsyncImage
import com.example.foodshoptestcase.Activity.Datail.DetailActivity
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.R




@Composable
fun ListItemsFullSizeVertical(items: List<ItemsModel>){
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        items(items.size){ index: Int ->
            BestSellerItem(items,index)
        }
    }
}

@Composable
fun ListItems(items: List<ItemsModel>){
    LazyRow(
        modifier = Modifier.padding(top=8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 12.dp)
    ) {
        items(items.size){ index: Int ->
            BestSellerItem(items,index)
        }
    }
}


@Composable
fun BestSellerItem(items:List<ItemsModel>, pos:Int){
    val context= LocalContext.current

    Column(
        modifier = Modifier
            .padding(4.dp)
            .wrapContentHeight()
    ) {
        AsyncImage(
            model = items[pos].picUrl.firstOrNull(),
            contentDescription = null,
            modifier = Modifier
                .width(180.dp)
                .background(colorResource(R.color.lightGrey), shape = RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .height(180.dp)
                .clickable {
                    val intent = Intent(context, DetailActivity::class.java).apply {
                        putExtra("object", items[pos])
                    }
                    startActivity(context,intent,null)
                }, contentScale = ContentScale.Crop
        )
        Text(
            text = items[pos].title,
            color= Color.Black,
            fontSize=16.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier
                .padding(top=8.dp)
        )
        Row(modifier=Modifier
            .width(175.dp)
            .padding(top = 4.dp)) {
            Row {
                Image(
                    painter = painterResource(R.drawable.star),contentDescription = null,
                    modifier=Modifier.align(Alignment.CenterVertically))
                Spacer(modifier=Modifier.width(8.dp))
                Text(
                    items[pos].rating.toString(),
                    color=Color.Black,
                    fontSize = 15.sp
                )
            }

        Text(
            text="${items[pos].price} ₽",
            color= colorResource(R.color.darkBrown),
            textAlign = TextAlign.End,
            modifier=Modifier.fillMaxWidth(),
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold
        )
    }}
}