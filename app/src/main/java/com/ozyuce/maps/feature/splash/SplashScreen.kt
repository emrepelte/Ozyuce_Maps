package com.ozyuce.maps.feature.splash

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.*
import com.ozyuce.maps.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    onSplashFinished: () -> Unit
) {
    var isAnimationFinished by remember { mutableStateOf(false) }
    
    LaunchedEffect(isAnimationFinished) {
        if (isAnimationFinished) {
            delay(500) // Animasyon bittikten sonra kısa bir bekleme
            onSplashFinished()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo
            Image(
                painter = painterResource(id = R.drawable.logo_ozyuce),
                contentDescription = "Özyüce Logo",
                modifier = Modifier
                    .size(200.dp)
                    .padding(16.dp),
                contentScale = ContentScale.Fit
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Şirket Adı
            Text(
                text = "ÖZYÜCE TURİZM",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 24.sp
                ),
                color = Color(0xFFE31E24) // Özyüce kırmızısı
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Lottie Animasyonu
            val composition by rememberLottieComposition(
                LottieCompositionSpec.Asset("splash/splash_animation.json")
            )
            
            val progress by animateLottieCompositionAsState(
                composition = composition,
                iterations = 1,
                restartOnPlay = false,
                speed = 1f,
                isPlaying = true
            )
            
            LottieAnimation(
                composition = composition,
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            )
            
            LaunchedEffect(progress) {
                if (progress == 1f) {
                    isAnimationFinished = true
                }
            }
        }
    }
}
