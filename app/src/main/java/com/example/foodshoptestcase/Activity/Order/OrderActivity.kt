package com.example.foodshoptestcase.Activity.Order

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.R

class OrderActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            OrderScreen(
                onBackCLick = {finish()}
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun OrderScreen(
    onBackCLick:()->Unit={}
) {
    Column (
        modifier =Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
            .padding(16.dp)
    ){
        ConstraintLayout (
            modifier = Modifier
                .padding(top=36.dp)
                .background(Color.White)
        ){
            val (backBtn,titleTxt)=createRefs()

            Text(
                text = "История заказов",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(titleTxt){centerTo(parent)}
            )
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = null,
                modifier = Modifier
                    .clickable { onBackCLick() }
                    .constrainAs(backBtn){
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
            )
        }
        Text(
            text = "История заказов пуста",
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )
    }
}