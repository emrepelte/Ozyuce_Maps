package com.ozyuce.maps.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozyuce.maps.navigation.HandleUiEvents
import com.ozyuce.maps.ui.theme.OzyuzeMapsTheme

/**
 * Giri? ekran? (ge?ici - ilerleyen ad?mlarda ViewModel ile g?ncellenecek)
 */
@Composable
fun LoginScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: AuthViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle UI events
    HandleUiEvents(
        uiEvents = viewModel.uiEvent,
        navController = navController,
        snackbarHostState = snackbarHostState
    )
    
    var email by remember { mutableStateOf("test@example.com") } // Demo i?in default
    var password by remember { mutableStateOf("password123") } // Demo i?in default

    Box(modifier = modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "OzyuceMaps",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
            
            Text(
                text = "Giri? Yap?n",
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(vertical = 32.dp)
            )
            
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("?ifre") },
                visualTransformation = PasswordVisualTransformation(),
                enabled = !uiState.isLoading,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Button(
                onClick = { 
                    if (email.isNotBlank() && password.isNotBlank()) {
                        viewModel.login(email, password)
                    }
                },
                enabled = !uiState.isLoading && email.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (uiState.isLoading) {
                    CircularProgressIndicator(color = MaterialTheme.colorScheme.onPrimary)
                } else {
                    Text("Giri? Yap")
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            TextButton(
                onClick = { navController.navigate("register") },
                enabled = !uiState.isLoading
            ) {
                Text("Hesab?n?z yok mu? Kay?t olun")
            }
        }
        
        // Loading overlay
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

// Preview kald?r?ld? - NavController dependency nedeniyle
// UI testleri i?in Compose UI Testing kullan?lacak
