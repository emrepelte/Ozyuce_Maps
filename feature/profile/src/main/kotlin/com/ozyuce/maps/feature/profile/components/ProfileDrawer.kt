package com.ozyuce.maps.feature.profile.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DrawerState
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.size
import androidx.hilt.navigation.compose.hiltViewModel
import com.ozyuce.maps.feature.profile.ProfileEvent
import com.ozyuce.maps.feature.profile.ProfileUiEvent
import com.ozyuce.maps.feature.profile.ProfileViewModel
import com.ozyuce.maps.feature.profile.data.Language
import com.ozyuce.maps.feature.profile.data.ThemeMode
import kotlinx.coroutines.launch

@Composable
fun ProfileDrawer(
    drawerState: DrawerState,
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            if (event is ProfileUiEvent.LogoutSuccess) {
                onLogout()
            }
        }
    }
    
    ModalDrawerSheet(
        modifier = Modifier
            .width(300.dp)
            .fillMaxHeight()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                modifier = Modifier.height(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Profil Ayarları",
                style = MaterialTheme.typography.headlineSmall
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            Text(
                text = "Görünüm",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "Tema",
                subtitle = when (uiState.themeMode) {
                    ThemeMode.SYSTEM -> "Sistem"
                    ThemeMode.LIGHT -> "Açık"
                    ThemeMode.DARK -> "Koyu"
                },
                icon = Icons.Rounded.Palette,
                onClick = { viewModel.onEvent(ProfileEvent.ShowThemePicker) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Dil",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "Uygulama Dili",
                subtitle = when (uiState.language) {
                    Language.TR -> "Türkçe"
                    Language.EN -> "English"
                },
                icon = Icons.Rounded.Language,
                onClick = { viewModel.onEvent(ProfileEvent.ShowLanguagePicker) }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Güvenlik",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.Start)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            SettingsItem(
                title = "Biyometrik Kilit",
                subtitle = if (uiState.isBiometricEnabled) "Açık" else "Kapalı",
                icon = Icons.Rounded.Fingerprint,
                trailing = {
                    Switch(
                        checked = uiState.isBiometricEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.onEvent(ProfileEvent.BiometricToggled(enabled))
                        }
                    )
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            FilledTonalButton(
                onClick = { 
                    viewModel.onEvent(ProfileEvent.LogoutRequested)
                    coroutineScope.launch {
                        drawerState.close()
                    }
                },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Çıkış Yap")
            }
        }
        
        if (uiState.showThemePicker) {
            ThemePickerDialog(
                selectedTheme = uiState.themeMode,
                onThemeSelected = { theme ->
                    viewModel.onEvent(ProfileEvent.ThemeModeChanged(theme))
                    viewModel.onEvent(ProfileEvent.DismissThemePicker)
                },
                onDismiss = { viewModel.onEvent(ProfileEvent.DismissThemePicker) }
            )
        }
        
        if (uiState.showLanguagePicker) {
            LanguagePickerDialog(
                selectedLanguage = uiState.language,
                onLanguageSelected = { language ->
                    viewModel.onEvent(ProfileEvent.LanguageChanged(language))
                    viewModel.onEvent(ProfileEvent.DismissLanguagePicker)
                },
                onDismiss = { viewModel.onEvent(ProfileEvent.DismissLanguagePicker) }
            )
        }
        
        if (uiState.showLogoutDialog) {
            LogoutDialog(
                onConfirm = { viewModel.onEvent(ProfileEvent.LogoutConfirmed) },
                onDismiss = { viewModel.onEvent(ProfileEvent.DismissDialog) }
            )
        }
    }
}
