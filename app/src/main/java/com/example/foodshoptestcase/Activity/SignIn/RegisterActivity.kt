package com.example.foodshoptestcase.Activity.SignIn

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.lifecycleScope
import com.example.foodshoptestcase.Activity.BaseActivity
import com.example.foodshoptestcase.Activity.Dashboard.MainActivity
import com.example.foodshoptestcase.R
import com.example.foodshoptestcase.ui.theme.FoodShopTestCaseTheme
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class RegisterActivity : BaseActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FoodShopTestCaseTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RegisterScreen(googleAuthUiClient = googleAuthUiClient)
                }
            }
        }
    }
}

@Composable
fun RegisterScreen(googleAuthUiClient: GoogleAuthUiClient) {
    val context = LocalContext.current
    val activity = context as? ComponentActivity
    var isLoading by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isPasswordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                activity?.lifecycleScope?.launch {
                    val signInResult = googleAuthUiClient.signInWithIntent(
                        intent = result.data ?: return@launch
                    )
                    if (signInResult.data != null) {
                        Toast.makeText(context, "Вход успешен", Toast.LENGTH_LONG).show()
                        context.startActivity(Intent(context, MainActivity::class.java))
                        (context as? BaseActivity)?.finish()
                    } else {
                        Toast.makeText(context, "Ошибка входа: ${signInResult.errorMessage}", Toast.LENGTH_LONG).show()
                        isLoading = false
                    }
                }
            } else {
                isLoading = false
                Toast.makeText(context, "Sign-in cancelled", Toast.LENGTH_SHORT).show()
            }
        }
    )

    LaunchedEffect(Unit) {
        if (FirebaseAuth.getInstance().currentUser != null) {
            context.startActivity(Intent(context, MainActivity::class.java))
            (context as? BaseActivity)?.finish()
        }
    }

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            LoadingCircle()
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Создать аккаунт",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            Spacer(modifier = Modifier.height(24.dp))
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                leadingIcon = {
                    Image(
                        painter = painterResource(R.drawable.email),
                        contentDescription = "Email"
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Пароль") },
                leadingIcon = {
                    Image(
                        painter = painterResource(id = R.drawable.bell),
                        contentDescription = "Password"
                    )
                },
                trailingIcon = {
                    IconButton(onClick = { isPasswordVisible = !isPasswordVisible }) {
                        Image(
                            painter = painterResource(
                                id = if (isPasswordVisible) R.drawable.eye else R.drawable.closed_eye
                            ),
                            contentDescription = "Toggle password visibility"
                        )
                    }
                },
                visualTransformation = if (isPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                shape = RoundedCornerShape(12.dp)
            )
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = {
                    if (email.isNotEmpty() && password.isNotEmpty()) {
                        isLoading = true
                        errorMessage = null
                        activity?.lifecycleScope?.launch {
                            try {
                                val result = FirebaseAuth.getInstance()
                                    .createUserWithEmailAndPassword(email, password)
                                    .await()
                                if (result.user != null) {
                                    Toast.makeText(context, "Регистрация успешна", Toast.LENGTH_LONG).show()
                                    context.startActivity(Intent(context, MainActivity::class.java))
                                    (context as? BaseActivity)?.finish()
                                } else {
                                    errorMessage = "Не удалось зарегистрироваться"
                                }
                            } catch (e: Exception) {
                                errorMessage = when {
                                    e.message?.contains("email address is already in use") == true ->
                                        "Этот email уже используется"
                                    e.message?.contains("password is invalid") == true ->
                                        "Пароль должен быть не менее 6 символов"
                                    else -> e.message ?: "Ошибка регистрации"
                                }
                            } finally {
                                isLoading = false
                            }
                        }
                    } else {
                        errorMessage = "Введите email и пароль"
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Зарегистрироваться", fontSize = 16.sp)
            }
            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = Color.Red,
                    modifier = Modifier.padding(top = 8.dp),
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(32.dp))
            Text(
                text = "ИЛИ",
                fontSize = 16.sp,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    isLoading = true
                    activity?.lifecycleScope?.launch {
                        val signInIntentSender = googleAuthUiClient.signIn()
                        if (signInIntentSender != null) {
                            launcher.launch(IntentSenderRequest.Builder(signInIntentSender).build())
                        } else {
                            isLoading = false
                            Toast.makeText(context, "Не удалось начать вход", Toast.LENGTH_SHORT).show()
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google",
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Продолжить с Google", fontSize = 16.sp)
            }
        }
    }
}

@Composable
fun LoadingCircle() {
    val infiniteTransition = rememberInfiniteTransition(label = "loading_rotation")
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 1000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    CircularProgressIndicator(
        modifier = Modifier
            .size(48.dp)
            .rotate(rotation),
        color = MaterialTheme.colorScheme.primary
    )
}