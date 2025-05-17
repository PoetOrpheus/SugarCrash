package com.example.foodshoptestcase.Activity.Profile

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Activity.Order.OrderActivity
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ui.theme.FoodShopTestCaseTheme
import com.google.firebase.auth.FirebaseAuth

class ProfileActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodShopTestCaseTheme {
                ProfileScreen(
                    onBackClick = { finish() },
                    onOrdersClick = { startActivity(Intent(this, OrderActivity::class.java)) },
                    onAddressClick = { /* TODO: Переход на экран адреса */ },
                    onPaymentClick = { /* TODO: Переход на экран оплаты */ },
                    onLogoutClick = {
                        FirebaseAuth.getInstance().signOut()

                    }
                )
            }
        }
    }
}

@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        // Заголовок и кнопка "Назад"
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 36.dp)
        ) {
            val (backBtn, title) = createRefs()
            Text(
                text = "Профиль",
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                fontSize = 25.sp,
                color = Color.Black,
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(title) { centerTo(parent) }
            )
            Image(
                painter = painterResource(R.drawable.back),
                contentDescription = "Back",
                modifier = Modifier
                    .constrainAs(backBtn) {
                        top.linkTo(parent.top)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                    }
                    .clickable { onBackClick() }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Аватар пользователя
        Image(
            painter = painterResource(R.drawable.profile),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(Color.LightGray)
                .align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Имя пользователя
        Text(
            text = user?.displayName ?: "Гость",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        // Email
        Text(
            text = user?.email ?: "Нет данных",
            fontSize = 16.sp,
            color = Color.Gray,
            modifier = Modifier.align(Alignment.CenterHorizontally)
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Кнопки профиля
        ProfileOption(title = "Мои заказы", onClick = onOrdersClick)
        ProfileOption(title = "Адрес доставки", onClick = onAddressClick)
        ProfileOption(title = "Способы оплаты", onClick = onPaymentClick)
        ProfileOption(title = "Выйти", onClick = onLogoutClick)
    }
}

@Composable
fun ProfileOption(
    title: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .background(Color.White, shape = RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black
        )
    }
}