package com.ozyuce.maps.core.ui.components.appbar

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OzyuceTopAppBar(
    title: String,
    modifier: Modifier = Modifier,
    navigateUp: (() -> Unit)? = null,
    onProfileClick: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {}
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            if (navigateUp != null) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Rounded.ArrowBack,
                        contentDescription = "Geri git"
                    )
                }
            }
        },
        actions = { 
            if (onProfileClick != null) {
                IconButton(onClick = onProfileClick) {
                    Icon(
                        imageVector = Icons.Rounded.AccountCircle,
                        contentDescription = "Profil"
                    )
                }
            }
            actions() 
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = modifier
    )
}
