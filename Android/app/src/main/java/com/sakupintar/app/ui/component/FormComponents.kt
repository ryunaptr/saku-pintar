package com.sakupintar.app.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sakupintar.app.ui.theme.ColorBorder
import com.sakupintar.app.ui.theme.ColorPrimary
import com.sakupintar.app.ui.theme.ColorSurface
import com.sakupintar.app.ui.theme.ColorTextMuted
import com.sakupintar.app.ui.theme.ColorTextPrimary

@Composable
fun FormLabel(teks: String) {
    Text(
        text = teks,
        style = MaterialTheme.typography.bodyMedium,
        color = ColorTextPrimary,
        fontWeight = FontWeight.Medium
    )
}

@Composable
fun formFieldColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = ColorPrimary,
    unfocusedBorderColor = ColorBorder,
    focusedContainerColor = ColorSurface,
    unfocusedContainerColor = ColorSurface
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownPilihan(
    nilai: String,
    opsi: List<String>,
    placeholder: String = "Pilih...",
    onNilaiDipilih: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = nilai,
            onValueChange = {},
            readOnly = true,
            placeholder = {
                Text(
                    text = placeholder,
                    style = MaterialTheme.typography.bodyMedium,
                    color = ColorTextMuted
                )
            },
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            shape = RoundedCornerShape(12.dp),
            colors = formFieldColors(),
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(MenuAnchorType.PrimaryNotEditable)
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            containerColor = ColorSurface
        ) {
            opsi.forEach { pilihan ->
                DropdownMenuItem(
                    text = {
                        Text(
                            text = pilihan,
                            style = MaterialTheme.typography.bodyMedium,
                            color = ColorTextPrimary
                        )
                    },
                    onClick = {
                        onNilaiDipilih(pilihan)
                        expanded = false
                    }
                )
            }
        }
    }
}