package com.ozyuce.maps.feature.dashboard.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material.icons.rounded.Build
import androidx.compose.material.icons.rounded.ExitToApp
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Place
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.ozyuce.maps.navigation.HandleUiEvents

/**
 * Dashboard ana ekran?
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    navController: NavHostController,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
    viewModel: DashboardViewModel = hiltViewModel(),
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    
    // Handle UI events
    HandleUiEvents(
        uiEvents = viewModel.uiEvent,
        navController = navController,
        snackbarHostState = snackbarHostState
    )
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "OzyuceMaps Dashboard",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        modifier = modifier
    ) { paddingValues ->
        
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp)
            ) {
                // Kullan?c? Bilgileri
                UserInfoCard(
                    userRole = uiState.userRole,
                    isDriver = uiState.isDriver
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Ana Men?
                Text(
                    text = "Ana Men?",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 16.dp)
                )
                
                // Men? Grid
                MenuGrid(
                    isDriver = uiState.isDriver,
                    onMenuItemClick = viewModel::onMenuItemClicked
                )
            }
        }
    }
}

@Composable
private fun UserInfoCard(
    userRole: String,
    isDriver: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Rounded.AccountCircle,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column {
                Text(
                    text = "Ho? geldiniz!",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = if (isDriver) "S?r?c?" else "M??teri",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuGrid(
    isDriver: Boolean,
    onMenuItemClick: (DashboardMenuItem) -> Unit
) {
    val menuItems = getMenuItems(isDriver)
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(menuItems) { menuItem ->
            MenuItemCard(
                menuItem = menuItem,
                onClick = { onMenuItemClick(menuItem.type) }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MenuItemCard(
    menuItem: DashboardMenuItemData,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp),
        colors = CardDefaults.cardColors(
            containerColor = menuItem.backgroundColor
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = menuItem.icon,
                contentDescription = null,
                modifier = Modifier.size(32.dp),
                tint = menuItem.iconTint
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = menuItem.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = menuItem.textColor,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Men? ??esi verisi
 */
private data class DashboardMenuItemData(
    val type: DashboardMenuItem,
    val title: String,
    val icon: ImageVector,
    val backgroundColor: Color,
    val iconTint: Color,
    val textColor: Color
)

@Composable
private fun getMenuItems(isDriver: Boolean): List<DashboardMenuItemData> {
    val primaryColor = MaterialTheme.colorScheme.primary
    val secondaryColor = MaterialTheme.colorScheme.secondary
    val tertiaryColor = MaterialTheme.colorScheme.tertiary
    val errorColor = MaterialTheme.colorScheme.error
    val surfaceColor = MaterialTheme.colorScheme.surface
    
    val onPrimary = MaterialTheme.colorScheme.onPrimary
    val onSecondary = MaterialTheme.colorScheme.onSecondary
    val onTertiary = MaterialTheme.colorScheme.onTertiary
    val onError = MaterialTheme.colorScheme.onError
    
    return buildList {
        if (isDriver) {
            // S?r?c? i?in ?zel men?ler
            add(
                DashboardMenuItemData(
                    type = DashboardMenuItem.SERVICE,
                    title = "Servis Y?netimi",
                    icon = Icons.Rounded.Build,
                    backgroundColor = primaryColor,
                    iconTint = onPrimary,
                    textColor = onPrimary
                )
            )
            
            add(
                DashboardMenuItemData(
                    type = DashboardMenuItem.STOPS,
                    title = "Durak Kontrol?",
                    icon = Icons.Rounded.LocationOn,
                    backgroundColor = secondaryColor,
                    iconTint = onSecondary,
                    textColor = onSecondary
                )
            )
        }
        
        // Ortak men?ler
        add(
            DashboardMenuItemData(
                type = DashboardMenuItem.MAP,
                title = "Harita",
                icon = Icons.Rounded.Place,
                backgroundColor = tertiaryColor,
                iconTint = onTertiary,
                textColor = onTertiary
            )
        )
        
        add(
            DashboardMenuItemData(
                type = DashboardMenuItem.REPORTS,
                title = "Raporlar",
                icon = Icons.Rounded.Info,
                backgroundColor = surfaceColor,
                iconTint = MaterialTheme.colorScheme.onSurface,
                textColor = MaterialTheme.colorScheme.onSurface
            )
        )
        
        add(
            DashboardMenuItemData(
                type = DashboardMenuItem.LOGOUT,
                title = "??k??",
                icon = Icons.Rounded.ExitToApp,
                backgroundColor = errorColor,
                iconTint = onError,
                textColor = onError
            )
        )
    }
}
