package com.ozyuce.maps.feature.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Fingerprint
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.Logout
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.feature.profile.components.LanguagePickerDialog
import com.ozyuce.maps.feature.profile.components.LogoutDialog
import com.ozyuce.maps.feature.profile.components.ThemePickerDialog
import com.ozyuce.maps.feature.profile.components.SettingsItem
import com.ozyuce.maps.feature.profile.data.Language
import com.ozyuce.maps.feature.profile.data.ThemeMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onLogout: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState = viewModel.uiState.collectAsStateWithLifecycle().value
    val snackbarHostState = remember { SnackbarHostState() }

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

    LaunchedEffect(viewModel) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is ProfileUiEvent.ShowSnackbar -> snackbarHostState.showSnackbar(event.message)
                ProfileUiEvent.LogoutSuccess -> onLogout()
            }
        }
    }

    Scaffold(
        topBar = {
            OzyuceTopAppBar(
                title = "Profil"
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Text(
                text = "Gorunum",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            SettingsItem(
                title = "Tema",
                subtitle = when (uiState.themeMode) {
                    ThemeMode.SYSTEM -> "Sistem"
                    ThemeMode.LIGHT -> "Acik"
                    ThemeMode.DARK -> "Koyu"
                },
                icon = Icons.Rounded.Palette,
                onClick = { viewModel.onEvent(ProfileEvent.ShowThemePicker) }
            )

            Text(
                text = "Dil",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            SettingsItem(
                title = "Uygulama Dili",
                subtitle = when (uiState.language) {
                    Language.TR -> "Turkce"
                    Language.EN -> "English"
                },
                icon = Icons.Rounded.Language,
                onClick = { viewModel.onEvent(ProfileEvent.ShowLanguagePicker) }
            )

            Text(
                text = "Guvenlik",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(16.dp)
            )

            SettingsItem(
                title = "Biyometrik Kilit",
                subtitle = if (uiState.isBiometricEnabled) "Açık" else "Kapalı",
                icon = Icons.Rounded.Fingerprint,
                trailing = {
                    Switch(
                        checked = uiState.isBiometricEnabled,
                        onCheckedChange = { enabled ->
                            viewModel.onEvent(ProfileEvent.BiometricToggled(enabled))
                        },
                        enabled = uiState.isBiometricAvailable
                    )
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            FilledTonalButton(
                onClick = { viewModel.onEvent(ProfileEvent.LogoutRequested) },
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    contentColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Cikis Yap")
            }
        }

        if (uiState.showLogoutDialog) {
            LogoutDialog(
                onConfirm = { viewModel.onEvent(ProfileEvent.LogoutConfirmed) },
                onDismiss = { viewModel.onEvent(ProfileEvent.DismissDialog) }
            )
        }
    }
}





