package com.ozyuce.maps.core.ui.preview

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.ozyuce.maps.core.designsystem.theme.OzyuceTheme
import com.ozyuce.maps.core.ui.components.appbar.OzyuceTopAppBar
import com.ozyuce.maps.core.ui.components.buttons.PrimaryButton
import com.ozyuce.maps.core.ui.components.buttons.SecondaryButton
import com.ozyuce.maps.core.ui.components.cards.KpiCard
import com.ozyuce.maps.core.ui.components.chips.PersonStatus
import com.ozyuce.maps.core.ui.components.chips.StatusChip
import com.ozyuce.maps.core.ui.components.items.PersonListItem
import com.ozyuce.maps.core.ui.components.states.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun ComponentsPreview() {
    OzyuceTheme {
        Scaffold(
            topBar = {
                OzyuceTopAppBar(
                    title = "Bile?en Galerisi",
                    actions = {
                        IconButton(onClick = {}) {
                            Icon(Icons.Rounded.Settings, "Ayarlar")
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // Buttons
                Text(
                    text = "Butonlar",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                PrimaryButton(
                    text = "Servisi Ba?lat",
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                SecondaryButton(
                    text = "?ptal Et",
                    onClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // KPI Cards
                Text(
                    text = "KPI Kartlar?",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    KpiCard(
                        title = "Toplam Personel",
                        value = "156",
                        description = "Aktif servis",
                        modifier = Modifier.weight(1f)
                    )
                    KpiCard(
                        title = "Durak Say?s?",
                        value = "24",
                        description = "3 hat",
                        modifier = Modifier.weight(1f)
                    )
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Status Chips
                Text(
                    text = "Durum ?ipleri",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                Row(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StatusChip(status = PersonStatus.ON_BOARD)
                    StatusChip(status = PersonStatus.ABSENT)
                    StatusChip(status = PersonStatus.PENDING)
                }

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Person List Items
                Text(
                    text = "Personel Listesi",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                PersonListItem(
                    name = "Ahmet Y?lmaz",
                    department = "Yaz?l?m Geli?tirme",
                    status = PersonStatus.ON_BOARD,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(8.dp))
                PersonListItem(
                    name = "Ay?e Demir",
                    department = "?nsan Kaynaklar?",
                    status = PersonStatus.ABSENT,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Divider(modifier = Modifier.padding(vertical = 16.dp))

                // Empty State
                Text(
                    text = "Bo? Durum",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(16.dp)
                )
                EmptyState(
                    icon = Icons.Rounded.SearchOff,
                    title = "Sonu? Bulunamad?",
                    description = "Arama kriterlerinize uygun sonu? bulunamad?. L?tfen farkl? bir arama yapmay? deneyin.",
                    ctaText = "Yeni Arama",
                    onCtaClick = {},
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(32.dp))
            }
        }
    }
}
