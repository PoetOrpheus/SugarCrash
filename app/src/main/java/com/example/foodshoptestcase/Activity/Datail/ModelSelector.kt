package com.example.foodshoptestcase.Activity.Datail

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshoptestcase.R
import java.nio.channels.Selector

@Composable
fun ModelSelector(
    models:List<String>,
    selectedModelIndex:Int,
    onModelSelected:(Int)->Unit
){
    LazyRow (
        modifier = Modifier.padding(vertical = 8.dp),
        contentPadding = PaddingValues(start = 16.dp)
    ){
        itemsIndexed(models){index,model->
            Box(modifier = Modifier
                .padding(end=16.dp)
                .height(40.dp)
                .background(
                    if (index==selectedModelIndex) {
                        colorResource(R.color.green)
                    }
                    else{
                        colorResource(R.color.lightGrey)
                    },
                    shape = RoundedCornerShape(10.dp)
                )
                .clickable { onModelSelected(index) }
                .padding(horizontal = 16.dp)
            ){
                Text(
                   text = model,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color=
                    if(index==selectedModelIndex) colorResource(R.color.white)
                    else colorResource(R.color.black),
                    modifier = Modifier.align(Alignment.Center)
                )
            }
            
        }
    }
}