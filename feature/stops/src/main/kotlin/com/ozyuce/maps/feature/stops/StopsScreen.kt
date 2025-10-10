package com.ozyuce.maps.feature.stops

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ozyuce.maps.core.designsystem.theme.OzyuceColors
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.core.ui.components.chips.PersonStatus
import com.ozyuce.maps.core.ui.events.UiEvent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StopsScreen(
    onNavigate: (String) -> Unit,
    onProfileClick: () -> Unit = {},
    viewModel: StopsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    var showAddPersonSheet by remember { mutableStateOf(false) }
    var showAddPersonForm by remember { mutableStateOf(false) }
    var personName by remember { mutableStateOf("") }
    var personDepartment by remember { mutableStateOf("") }

    LaunchedEffect(key1 = true) {
        viewModel.uiEvent.collect { event ->
            when (event) {
                is UiEvent.ShowSnackbar -> {
                    val result = snackbarHostState.showSnackbar(
                        message = event.message,
                        actionLabel = event.actionLabel,
                        duration = SnackbarDuration.Short
                    )
                    if (result == SnackbarResult.ActionPerformed) {
                        event.onActionClick?.invoke()
                    }
                }
                is UiEvent.Navigate -> onNavigate(event.route)
                else -> Unit
            }
        }
    }

    Scaffold(
        topBar = {
            OzyuceTopAppBar(
                title = "Duraklar",
                showLogo = true,
                onProfileClick = onProfileClick,
                actions = {
                    FilledIconButton(
                        onClick = { /* TODO: Paylaş */ },
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Share,
                            contentDescription = "Paylaş"
                        )
                    }
                    FilledIconButton(
                        onClick = { /* TODO: İndir */ }
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.KeyboardArrowDown,
                            contentDescription = "İndir"
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { showAddPersonSheet = true },
                modifier = Modifier.shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
                shape = RoundedCornerShape(16.dp),
                containerColor = OzyuceColors.Primary
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Yeni Personel Ekle",
                    tint = Color.White
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp)
        ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Search + Filter
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onEvent(StopsEvent.SearchQueryChanged(it)) },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("Personel ara...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Rounded.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(20.dp),
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                unfocusedBorderColor = MaterialTheme.colorScheme.outlineVariant,
                                focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f),
                                unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.9f)
                            ),
                            singleLine = true
                        )
                        
                        OutlinedButton(
                            onClick = {},
                            modifier = Modifier.height(56.dp),
                            shape = RoundedCornerShape(12.dp),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.Settings,
                                contentDescription = "Filtre",
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Filtre")
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Segmented Control (Tabs)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        listOf(
                            PersonFilter.ALL to "Tümü",
                            PersonFilter.ON_BOARD to "Binen",
                            PersonFilter.REMAINING to "Kalan"
                        ).forEach { (filter, label) ->
                            val isSelected = uiState.selectedFilter == filter
                            Button(
                                onClick = { viewModel.onEvent(StopsEvent.FilterChanged(filter)) },
                                modifier = Modifier.weight(1f).height(40.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = if (isSelected) OzyuceColors.Primary else MaterialTheme.colorScheme.surface,
                                    contentColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                shape = RoundedCornerShape(16.dp),
                                border = if (!isSelected) BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant) else null,
                                elevation = if (isSelected) ButtonDefaults.buttonElevation(defaultElevation = 4.dp) else null,
                                contentPadding = PaddingValues(0.dp)
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Stats Cards
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        StatsCard(
                            label = "Toplam",
                            value = uiState.totalCount,
                            color = OzyuceColors.Primary,
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            label = "Binen",
                            value = uiState.onBoardCount,
                            color = Color(0xFF22C55E),
                            modifier = Modifier.weight(1f)
                        )
                        StatsCard(
                            label = "Kalan",
                            value = uiState.remainingCount,
                            color = Color(0xFFF59E0B),
                            modifier = Modifier.weight(1f)
                        )
                    }

            Spacer(modifier = Modifier.height(16.dp))

            // Person List
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 8.dp)
                ) {
                    items(
                        items = uiState.filteredPersons,
                        key = { it.id }
                    ) { person ->
                        PersonListItemModern(
                            person = person,
                            onClick = { viewModel.onEvent(StopsEvent.TogglePersonStatus(person.id)) }
                        )
                        if (person != uiState.filteredPersons.last()) {
                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp),
                                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }

    // Add Person Bottom Sheet
    if (showAddPersonSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                showAddPersonSheet = false
                showAddPersonForm = false
                personName = ""
                personDepartment = ""
            },
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (showAddPersonForm) {
                    // Add Person Form
                    Text(
                        text = "Yeni Personel Ekle",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    OutlinedTextField(
                        value = personName,
                        onValueChange = { personName = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Ad Soyad") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Person,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = personDepartment,
                        onValueChange = { personDepartment = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("Departman") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Rounded.Home,
                                contentDescription = null,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        shape = RoundedCornerShape(12.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showAddPersonForm = false
                                personName = ""
                                personDepartment = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("İptal")
                        }

                        Button(
                            onClick = {
                                viewModel.onEvent(StopsEvent.AddPerson(personName, personDepartment))
                                showAddPersonSheet = false
                                showAddPersonForm = false
                                personName = ""
                                personDepartment = ""
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = OzyuceColors.Primary
                            )
                        ) {
                            Text("Kaydet")
                        }
                    }
                } else {
                    // Selection Menu
                    Text(
                        text = "Yeni Kayıt",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    OutlinedButton(
                        onClick = { showAddPersonForm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Person,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Yeni Personel")
                    }

                    OutlinedButton(
                        onClick = { showAddPersonSheet = false },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Search,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Durak Seç")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun StatsCard(
    label: String,
    value: Int,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value.toString(),
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = color
                )
            )
        }
    }
}

@Composable
private fun PersonListItemModern(
    person: PersonUiState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Surface(
            modifier = Modifier.size(40.dp),
            shape = CircleShape,
            color = OzyuceColors.PrimarySoft,
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f))
        ) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = person.name.split(" ").mapNotNull { it.firstOrNull() }.joinToString(""),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        color = OzyuceColors.Primary
                    )
                )
            }
        }

        // Name & Department
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = person.name,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            )
            Text(
                text = person.department,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }

        // Status Badge
        StatusBadgeModern(status = person.status)
    }
}

@Composable
private fun StatusBadgeModern(
    status: PersonStatus,
    modifier: Modifier = Modifier
) {
    val badge = when (status) {
        PersonStatus.ON_BOARD -> StatusBadgeStyle(
            background = Color(0xFFDCFCE7),
            textColor = Color(0xFF15803D),
            dotColor = Color(0xFF22C55E),
            label = "Bindi"
        )
        PersonStatus.ABSENT -> StatusBadgeStyle(
            background = Color(0xFFFEE2E2),
            textColor = Color(0xFF991B1B),
            dotColor = Color(0xFFEF4444),
            label = "Gelmedi"
        )
        PersonStatus.PENDING -> StatusBadgeStyle(
            background = Color(0xFFFEF3C7),
            textColor = Color(0xFF92400E),
            dotColor = Color(0xFFF59E0B),
            label = "Bekliyor"
        )
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = badge.background
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(badge.dotColor)
            )
            Text(
                text = badge.label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Medium,
                    color = badge.textColor
                )
            )
        }
    }
}

private data class StatusBadgeStyle(
    val background: Color,
    val textColor: Color,
    val dotColor: Color,
    val label: String
)
