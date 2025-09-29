package com.ozyuce.maps.feature.auth.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import com.ozyuce.maps.core.common.Constants
import com.ozyuce.maps.feature.auth.domain.model.UserRole
import com.ozyuce.maps.ui.theme.OzyuzeMapsTheme

/**
 * Kay?t ekran? (ge?ici - ilerleyen ad?mlarda ViewModel ile g?ncellenecek)
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.CUSTOMER) }
    var isRoleDropdownExpanded by remember { mutableStateOf(false) }
    
    val roles = listOf(
        UserRole.DRIVER to "S?r?c?",
        UserRole.CUSTOMER to "M??teri"
    )
    
    Column(
        modifier = modifier
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
            text = "Hesap Olu?turun",
            style = MaterialTheme.typography.titleMedium,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 32.dp)
        )
        
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Ad Soyad") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("?ifre") },
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        ExposedDropdownMenuBox(
            expanded = isRoleDropdownExpanded,
            onExpandedChange = { isRoleDropdownExpanded = it },
            modifier = Modifier.fillMaxWidth()
        ) {
            OutlinedTextField(
                value = roles.find { it.first == selectedRole }?.second ?: "",
                onValueChange = {},
                readOnly = true,
                label = { Text("Kullan?c? Tipi") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isRoleDropdownExpanded) },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor()
            )
            
            ExposedDropdownMenu(
                expanded = isRoleDropdownExpanded,
                onDismissRequest = { isRoleDropdownExpanded = false }
            ) {
                roles.forEach { (role, displayName) ->
                    DropdownMenuItem(
                        text = { Text(displayName) },
                        onClick = {
                            selectedRole = role
                            isRoleDropdownExpanded = false
                        }
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Button(
            onClick = onNavigateToDashboard,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Kay?t Ol")
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        TextButton(onClick = onNavigateToLogin) {
            Text("Zaten hesab?n?z var m?? Giri? yap?n")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterScreenPreview() {
    OzyuzeMapsTheme {
        RegisterScreen()
    }
}
