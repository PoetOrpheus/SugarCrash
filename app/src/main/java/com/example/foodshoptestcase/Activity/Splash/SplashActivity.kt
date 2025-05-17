package com.example.foodshoptestcase.Activity.Splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Activity.Dashboard.MainActivity
import com.example.foodshoptestcase.Activity.SignIn.RegisterActivity
import com.example.foodshoptestcase.Activity.SignIn.VhodActivity
import com.example.foodshoptestcase.R

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SplashScreen(
                onClickButton={startActivity(Intent(this,MainActivity::class.java))},
                onClickVhod = { startActivity(Intent(this, VhodActivity::class.java)) },
                onClickReg = {startActivity(Intent(this,RegisterActivity::class.java))}
            )
        }
    }
}

@Composable
fun SplashScreen(onClickButton:()->Unit,onClickVhod: () -> Unit, onClickReg: () -> Unit) {
    // Получаем текущую ширину экрана в dp
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp.toFloat()

    // Масштабирование относительно базовой ширины экрана в Figma (320 dp)
    val scaleFactor = screenWidthDp / 320f

    // Определение масштабированных размеров
    val imageTopPaddingDp: Dp = (39f * scaleFactor+30).dp
    val imageWidthDp: Dp = (213f * scaleFactor).dp
    val imageHeightDp: Dp = (194f * scaleFactor).dp
    val paddingBetweenImageAndFirstTextDp: Dp = (30f * scaleFactor+30).dp
    val firstTextFontSizeSp: TextUnit = (25f * scaleFactor-2).sp
    val paddingBetweenFirstAndSecondTextDp: Dp = (54f * scaleFactor).dp
    val secondTextFontSizeSp: TextUnit = (12f * scaleFactor+2).sp
    val paddingBetweenSecondTextAndButtonDp: Dp = (23f * scaleFactor).dp
    val buttonWidthDp: Dp = (280f * scaleFactor).dp
    val buttonFontSizeSp: TextUnit = (17f * scaleFactor).sp
    val paddingBetweenButtonAndThirdTextDp: Dp = (11f * scaleFactor).dp
    val thirdTextFontSizeSp: TextUnit = (15f * scaleFactor).sp
    val paddingBetweenThirdAndFourthTextDp: Dp = (11f * scaleFactor).dp
    val fourthTextFontSizeSp: TextUnit = (15f * scaleFactor).sp

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorResource(id = R.color.brownForSplash)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(imageTopPaddingDp))
        Image(
            painter = painterResource(id = R.drawable.splash),
            contentDescription = null,
            modifier = Modifier.size(width = imageWidthDp, height = imageHeightDp)
        )
        Spacer(modifier = Modifier.height(paddingBetweenImageAndFirstTextDp))
        Text(
            text = "Сладости уже в пути…\nНачните день с радости!",
            fontSize = firstTextFontSizeSp,
            fontWeight = FontWeight.SemiBold,
            color = Color.Black,
            lineHeight = 60.sp,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(paddingBetweenFirstAndSecondTextDp))
        Text(
            text = "Выбирайте сладости от лучших и \n" +
                    "проверенных продавцов!" +
                    "\nПолучайте индивидуальные рекомендации!",
            fontSize = secondTextFontSizeSp,
            color = Color.Black,
            textAlign = TextAlign.Center,
            lineHeight = 35.sp
        )
        Spacer(modifier = Modifier.height(paddingBetweenSecondTextAndButtonDp))
        Button(
            onClick = onClickButton,
            modifier = Modifier.width(buttonWidthDp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(R.color.purple)
            ),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text(text = "Приступим",
                fontSize = buttonFontSizeSp,
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(paddingBetweenButtonAndThirdTextDp))
        Text(
            text = "Уже есть аккаунт? Войти",
            fontSize = thirdTextFontSizeSp,
            color = Color.Black,
            modifier = Modifier.clickable(onClick = onClickVhod)
        )
        Spacer(modifier = Modifier.height(paddingBetweenThirdAndFourthTextDp))
        Text(
            text = "Регистрация",
            fontSize = fourthTextFontSizeSp,
            color = Color.Black,
            modifier = Modifier.clickable(onClick = onClickReg)
        )
    }
}

@Preview
@Composable
fun SplashScreenPreview() {
    SplashScreen(onClickVhod = {}, onClickReg = {}, onClickButton = {})
}