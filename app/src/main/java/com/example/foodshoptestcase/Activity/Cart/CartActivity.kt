package com.example.foodshoptestcase.Activity.Cart

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Helper.ManagmentCart
import com.example.foodshoptestcase.R

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            CartScreen(ManagmentCart(this), onBackClick = {finish()})
        }
    }
}


@Composable
fun CartScreen(
    managmentCart: ManagmentCart= ManagmentCart(LocalContext.current),
    onBackClick:()->Unit
){
    var cartItem= remember{ mutableStateOf(managmentCart.getListCart())}
    val tax=remember{ mutableStateOf(0.0)}
    calculatorCart(managmentCart,tax)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight()
            .background(Color.White)
            .padding(16.dp)
    ){
        ConstraintLayout (
            modifier = Modifier
                .padding(top = 36.dp)
                .background(Color.White)
        ){
            val (backBtn,cartTxt)=createRefs()
            Text(
                text="Ваши покупки",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 25.sp,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(cartTxt) { centerTo(parent) }
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
        if (cartItem.value.isEmpty()){
            Text(
                text = "Корзина пустая",
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        } else {
            CartList(cartItems = cartItem.value, managmentCart) {
                cartItem.value = managmentCart.getListCart()
                calculatorCart(managmentCart, tax)
            }
            CartSummary(
                itemTotal = managmentCart.getTotalFee(),
                tax=tax.value,
                delivery = 10.0
            )

        }
    }

}

fun calculatorCart(
    managmentCart: ManagmentCart,
    tax:MutableState<Double>
){
    val percentTax=0.02
    tax.value=Math.round((managmentCart.getTotalFee()*percentTax)*100)/100.0

}