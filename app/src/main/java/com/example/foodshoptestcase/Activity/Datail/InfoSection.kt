package com.example.foodshoptestcase.Activity.Datail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.foodshoptestcase.Domain.ItemsModel
import com.example.foodshoptestcase.R

@Composable
fun InfoSection(item:ItemsModel){
    Column (
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ){
        Row (
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ){
            Text(
                text = item.title,
                fontSize = 23.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .weight(1f)
            )
            Text(
                text = "${item.price} ₽",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        }
        Text(
            text = "Продавец",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = 8.dp)
        )
        Row (
            verticalAlignment = Alignment.CenterVertically
        ){
            AsyncImage(
                model=item.sellerPic,
                contentDescription = null,
                modifier = Modifier.size(60.dp)
            )
            Text(
                text = item.sellerName,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .weight(1f)
            )
            Row {
                Image(
                    painter = painterResource(R.drawable.message),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                )
                Image(
                    painter = painterResource(R.drawable.call),
                    contentDescription = null
                )
            }
        }



    }
}