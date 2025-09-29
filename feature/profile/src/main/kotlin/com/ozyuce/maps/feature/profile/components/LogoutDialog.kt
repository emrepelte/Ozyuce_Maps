package com.ozyuce.maps.feature.profile.components

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource

@Composable
fun LogoutDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Çıkış Yap")
        },
        text = {
            Text("Oturumu kapatmak istediğinize emin misiniz?")
        },
        confirmButton = {
            TextButton(
                onClick = onConfirm
            ) {
                Text("Çıkış Yap")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ) {
                Text("İptal")
            }
        }
    )
}
