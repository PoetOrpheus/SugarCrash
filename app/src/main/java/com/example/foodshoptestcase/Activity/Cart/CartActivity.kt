package com.example.foodshoptestcase.Activity.Cart

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableDoubleStateOf
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
import com.example.foodshoptestcase.Activity.Dashboard.BottomMenu
import com.example.foodshoptestcase.Activity.Dashboard.MainActivity
import com.example.foodshoptestcase.Activity.Favorite.FavoriteActivity
import com.example.foodshoptestcase.Activity.Order.OrderActivity
import com.example.foodshoptestcase.Activity.Profile.ProfileActivity
import com.example.foodshoptestcase.Helper.ManagmentCart
import com.example.foodshoptestcase.R

class CartActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent{
            CartScreen(
                ManagmentCart(this),
                onBackClick = {finish()},
                //Для боттом меню
                onProfileClick = { startActivity(Intent(this, ProfileActivity::class.java)) },
                onFavoriteClick = { startActivity(Intent(this, FavoriteActivity::class.java)) },
                onOrderClick = { startActivity(Intent(this, OrderActivity::class.java)) },
                onHomeClick = { startActivity(Intent(this, MainActivity::class.java)) },
                )
        }
    }
}


@SuppressLint("MutableCollectionMutableState", "UnusedMaterialScaffoldPaddingParameter")
@Composable
fun CartScreen(
    managmentCart: ManagmentCart = ManagmentCart(LocalContext.current),
    onBackClick: () -> Unit,
    onHomeClick:()->Unit,
    onFavoriteClick:()->Unit,
    onOrderClick:()->Unit,
    onProfileClick:()->Unit,
) {
    var cartItem = remember { mutableStateOf(managmentCart.listCart) }
    val tax = remember { mutableDoubleStateOf(0.0) }
    calculatorCart(managmentCart, tax)

    Scaffold(
        bottomBar = {
            BottomMenu(
                modifier = Modifier.fillMaxWidth(),
                onHomeClick = onHomeClick,
                onFavoriteClick = onFavoriteClick,
                onOrderClick = onOrderClick,
                onProfileClick = onProfileClick
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .background(Color.White)
                .padding(16.dp)
        ) {
            ConstraintLayout(
                modifier = Modifier
                    .padding(top = 36.dp)
                    .background(Color.White)
            ) {
                val (backBtn, cartTxt) = createRefs()
                Text(
                    text = "Ваши покупки",
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
            if (cartItem.value.isEmpty()) {
                Text(
                    text = "Корзина пустая",
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            } else {
                CartList(cartItems = cartItem.value, managmentCart) {
                    cartItem.value = managmentCart.listCart
                    calculatorCart(managmentCart, tax)
                }
                CartSummary(
                    itemTotal = managmentCart.getTotalFee(),
                    tax = tax.doubleValue,
                    delivery = 450.0
                )

            }
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