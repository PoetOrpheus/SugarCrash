package com.example.foodshoptestcase.Activity.Profile

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.Rect
import android.net.Uri
import android.os.Bundle
import android.util.Log
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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color as ComposeColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.rememberAsyncImagePainter
import com.example.foodshoptestcase.Activity.Cart.CartActivity
import com.example.foodshoptestcase.Activity.Dashboard.BottomMenu
import com.example.foodshoptestcase.Activity.Dashboard.MainActivity
import com.example.foodshoptestcase.Activity.Favorite.FavoriteActivity
import com.example.foodshoptestcase.Activity.Order.OrderActivity
import com.example.foodshoptestcase.Activity.SignIn.VhodActivity
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ui.theme.FoodShopTestCaseTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import java.io.File
import java.io.FileOutputStream

// Сохранение и обрезка изображения локально
fun saveAndCropImageToInternalStorage(context: Context, imageUri: Uri): File? {
    return try {
        val inputStream = context.contentResolver.openInputStream(imageUri)
        if (inputStream == null) {
            Log.e("Profile", "Не удалось открыть InputStream для URI: $imageUri")
            return null
        }
        val bitmap = BitmapFactory.decodeStream(inputStream)
        inputStream.close()

        if (bitmap == null) {
            Log.e("Profile", "Не удалось декодировать битмап из URI: $imageUri")
            return null
        }

        // Обрезка изображения в круг
        val croppedBitmap = getCircularBitmap(bitmap)
        bitmap.recycle()

        val file = File(context.filesDir, "profile_photo.jpg")
        FileOutputStream(file).use { out ->
            croppedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            Log.d("Profile", "Изображение успешно сохранено: ${file.absolutePath}")
        }
        croppedBitmap.recycle()
        file
    } catch (e: Exception) {
        Log.e("Profile", "Ошибка при сохранении изображения: ${e.message}", e)
        null
    }
}

private fun getCircularBitmap(bitmap: Bitmap): Bitmap {
    val size = minOf(bitmap.width, bitmap.height)
    val x = (bitmap.width - size) / 2
    val y = (bitmap.height - size) / 2
    val squareBitmap = Bitmap.createBitmap(bitmap, x, y, size, size)

    val output = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(output)
    val paint = Paint()
    val rect = Rect(0, 0, size, size)
    paint.isAntiAlias = true
    canvas.drawARGB(0, 0, 0, 0)
    paint.color = Color.WHITE
    canvas.drawCircle(size / 2f, size / 2f, size / 2f, paint)
    paint.xfermode = PorterDuffXfermode(PorterDuff.Mode.SRC_IN)
    canvas.drawBitmap(squareBitmap, rect, rect, paint)
    squareBitmap.recycle()
    return output
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
                    },
                    //Для боттом меню
                    onCartClick = { startActivity(Intent(this, CartActivity::class.java)) },
                    onFavoriteClick = { startActivity(Intent(this, FavoriteActivity::class.java)) },
                    onOrderClick = { startActivity(Intent(this, OrderActivity::class.java)) },
                    onHomeClick = { startActivity(Intent(this, MainActivity::class.java)) },
                    )
            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBackClick: () -> Unit,
    onOrdersClick: () -> Unit,
    onAddressClick: () -> Unit,
    onPaymentClick: () -> Unit,
    onLogoutClick: () -> Unit,
    onCartClick:()->Unit,
    onFavoriteClick:()->Unit,
    onOrderClick:()->Unit,
    onHomeClick:()->Unit,
) {
    val user = FirebaseAuth.getInstance().currentUser
    var isEditing by remember { mutableStateOf(false) }
    var name by remember { mutableStateOf(user?.displayName ?: "") }
    var localPhotoPath by remember { mutableStateOf<String?>(null) }
    var photoTimestamp by remember { mutableStateOf(0L) }
    val context = LocalContext.current

    // Загрузка локального фото при старте
    LaunchedEffect(Unit) {
        val file = File(context.filesDir, "profile_photo.jpg")
        if (file.exists()) {
            localPhotoPath = file.absolutePath
            photoTimestamp = file.lastModified()
            Log.d("Profile", "Локальное фото найдено: $localPhotoPath")
        } else {
            Log.d("Profile", "Локальное фото не найдено")
        }
    }

    // Лаунчер для выбора изображения
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { selectedUri ->
            Log.d("Profile", "Выбрано изображение: $selectedUri")
            val file = saveAndCropImageToInternalStorage(context, selectedUri)
            if (file != null) {
                localPhotoPath = file.absolutePath
                photoTimestamp = System.currentTimeMillis()
                Log.d("Profile", "Фото обновлено: $localPhotoPath, timestamp: $photoTimestamp")
            } else {
                Log.e("Profile", "Не удалось сохранить фото")
                // Можно добавить Toast для уведомления пользователя
                android.widget.Toast.makeText(context, "Ошибка при сохранении фото", android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }
    Scaffold(
        bottomBar = {
            BottomMenu(
                modifier = Modifier.fillMaxWidth(),
                onCartClick = onCartClick,
                onFavoriteClick = onFavoriteClick,
                onOrderClick = onOrderClick,
                onHomeClick = onHomeClick
            )
        }
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = androidx.compose.ui.graphics.Color.White
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight()
                    .background(androidx.compose.ui.graphics.Color.White)
                    .padding(16.dp)
            ) {
                ConstraintLayout(
                    modifier = Modifier
                        .padding(top = 36.dp)
                        .background(androidx.compose.ui.graphics.Color.White)
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
                    colors = CardDefaults.cardColors(containerColor = ComposeColor.White)
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
                                .background(ComposeColor.LightGray)
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
                                            focusedBorderColor = ComposeColor.Blue,
                                            unfocusedBorderColor = ComposeColor.Gray
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = user?.email ?: "Нет почты",
                                        fontSize = 16.sp,
                                        color = ComposeColor.Black.copy(alpha = 0.6f),
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        } else {
                            Text(
                                text = name.ifEmpty { "Гость" },
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = ComposeColor.Black,
                                modifier = Modifier
                                    .clickable { isEditing = true }
                                    .padding(vertical = 8.dp)
                            )
                            HorizontalDivider(
                                modifier = Modifier.padding(vertical = 8.dp),
                                thickness = 1.dp,
                                color = ComposeColor.Gray
                            )
                            Text(
                                text = user?.email ?: "Нет почты",
                                fontSize = 16.sp,
                                color = ComposeColor.Black.copy(alpha = 0.6f),
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
                            containerColor = ComposeColor.Blue,
                            contentColor = ComposeColor.White
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
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onLogoutClick() }
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = "Выйти",
                        fontSize = 16.sp,
                        color = ComposeColor.Black,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
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
            color = ComposeColor.Black,
            fontWeight = FontWeight.Medium
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = ComposeColor.Gray.copy(alpha = 0.2f)
    )
}