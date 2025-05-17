package com.example.foodshoptestcase.Activity.Profile

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.foodshoptestcase.Activity.Order.OrderActivity
import com.example.foodshoptestcase.Activity.SignIn.VhodActivity
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ui.theme.FoodShopTestCaseTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.File

// Сохранение изображения локально
fun saveImageToInternalStorage(context: Context, imageUri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(imageUri) ?: return null
        val file = File(context.filesDir, "profile_photo.jpg")
        file.outputStream().use { outputStream ->
            inputStream.copyTo(outputStream)
        }
        file
    } catch (e: Exception) {
        null
    }
}

class ProfileActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodShopTestCaseTheme {
                ProfileScreen(
                    onBackClick = { finish() },
                    onOrdersClick = {
                        startActivity(Intent(this, OrderActivity::class.java))
                    },
                    onAddressClick = { /* TODO: Реализовать переход к экрану адреса */ },
                    onPaymentClick = { /* TODO: Реализовать переход к экрану оплаты */ },
                    onLogoutClick = {
                        FirebaseAuth.getInstance().signOut()
                        val intent = Intent(this, VhodActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    val user = FirebaseAuth.getInstance().currentUser
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var localPhotoPath by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Лаунчер для выбора изображения
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            val file = saveImageToInternalStorage(context, selectedUri)
            file?.let {
                localPhotoPath = it.absolutePath
                // TODO: В будущем загрузить это изображение в Firebase Storage и обновить user.photoUrl
            }
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White
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
                val (backBtn, titleTxt) = createRefs()

                Text(
                    text = "Профиль",
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .constrainAs(titleTxt) { centerTo(parent) }
                )
                Image(
                    painter = painterResource(R.drawable.back),
                    contentDescription = null,
                    modifier = Modifier
                        .clickable { onBackClick() }
                        .constrainAs(backBtn) {
                            top.linkTo(parent.top)
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                        }
                )
            }

            // Карточка профиля
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Фото профиля
                    val photoPainter = when {
                        localPhotoPath != null -> rememberAsyncImagePainter(model = localPhotoPath)
                        user?.photoUrl != null -> rememberAsyncImagePainter(model = user.photoUrl)
                        else -> painterResource(id = R.drawable.profile)
                    }
                    Image(
                        painter = photoPainter,
                        contentDescription = "Фото профиля",
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                            .clickable { launcher.launch("image/*") }
                    )
                    Spacer(modifier = Modifier.height(24.dp))

                    // Имя и почта
                    if (isEditing) {
                        AnimatedVisibility(
                            visible = isEditing,
                            enter = fadeIn(),
                            exit = fadeOut()
                        ) {
                            Column {
                                OutlinedTextField(
                                    value = name,
                                    onValueChange = { name = it },
                                    label = { Text("Имя") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Color.Blue,
                                        unfocusedBorderColor = Color.Gray
                                    )
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = user?.email ?: "Нет почты",
                                    fontSize = 16.sp,
                                    color = Color.Black.copy(alpha = 0.6f),
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    } else {
                        Text(
                            text = name.ifEmpty { "Гость" },
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier
                                .clickable { isEditing = true }
                                .padding(vertical = 8.dp)
                        )
                        Divider(
                            color = Color.Gray,
                            thickness = 1.dp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Text(
                            text = user?.email ?: "Нет почты",
                            fontSize = 16.sp,
                            color = Color.Black.copy(alpha = 0.6f),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }

            // Кнопка сохранения
            AnimatedVisibility(
                visible = isEditing,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Button(
                    onClick = {
                        val profileUpdates = UserProfileChangeRequest.Builder()
                            .setDisplayName(name)
                            .build()
                        user?.updateProfile(profileUpdates)?.addOnCompleteListener {
                            if (it.isSuccessful) {
                                isEditing = false
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Blue,
                        contentColor = Color.White
                    )
                ) {
                    Text("Сохранить", fontSize = 16.sp, fontWeight = FontWeight.Medium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Меню
            ProfileOption(
                title = "Мои заказы",
                onClick = onOrdersClick
            )
            ProfileOption(
                title = "Адрес доставки",
                onClick = onAddressClick
            )
            ProfileOption(
                title = "Способы оплаты",
                onClick = onPaymentClick
            )
            ProfileOption(
                title = "Выйти",
                onClick = onLogoutClick
            )
        }
    }
}

@Composable
fun ProfileOption(title: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = Color.Black,
            fontWeight = FontWeight.Medium
        )
    }
    Divider(
        color = Color.Gray.copy(alpha = 0.2f),
        thickness = 1.dp,
        modifier = Modifier.padding(horizontal = 16.dp)
    )
}