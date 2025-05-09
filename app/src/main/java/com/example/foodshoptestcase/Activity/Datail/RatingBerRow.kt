package com.example.foodshoptestcase.Activity.Datail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.foodshoptestcase.R

@Composable
fun RatingBarRow(
    rating:Double
){
    Row (
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .padding(top=16.dp)
            .padding(horizontal = 16.dp)
    ){
        Text(
            text="Выберите вес",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        Image(
            painter = painterResource(R.drawable.star),
            contentDescription = null,
            modifier = Modifier.padding(end=8.dp)
        )
        Text(
            text ="$rating Рейтинг",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}