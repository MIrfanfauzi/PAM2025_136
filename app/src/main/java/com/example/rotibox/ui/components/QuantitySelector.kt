package com.example.rotibox.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Quantity Selector Component
 * Dengan warna coklat lebih terang dan input keyboard
 */
@Composable
fun QuantitySelector(
    quantity: Int,
    onQuantityChange: (Int) -> Unit,
    maxStock: Int = Int.MAX_VALUE,
    modifier: Modifier = Modifier,
    compact: Boolean = false // Tambah parameter untuk mode compact
) {
    var textValue by remember(quantity) { mutableStateOf(quantity.toString()) }
    
    // Warna coklat lebih terang
    val buttonColor = Color(0xFFA67C52) // Coklat terang
    
    // Ukuran berdasarkan mode
    val buttonSize = if (compact) 36.dp else 48.dp
    val textFieldWidth = if (compact) 60.dp else 80.dp
    val iconSize = if (compact) 16.dp else 20.dp
    
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(if (compact) 4.dp else 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Button Minus
        FilledTonalButton(
            onClick = {
                if (quantity > 1) {
                    val newQty = quantity - 1
                    onQuantityChange(newQty)
                    textValue = newQty.toString()
                }
            },
            modifier = Modifier.size(buttonSize),
            shape = RoundedCornerShape(if (compact) 8.dp else 12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = buttonColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp),
            enabled = quantity > 1
        ) {
            Icon(
                Icons.Default.Remove,
                contentDescription = "Kurangi",
                modifier = Modifier.size(iconSize)
            )
        }
        
        // TextField Input
        OutlinedTextField(
            value = textValue,
            onValueChange = { newValue ->
                // Only allow digits
                if (newValue.isEmpty()) {
                    textValue = ""
                } else if (newValue.all { it.isDigit() }) {
                    textValue = newValue
                    val newQty = newValue.toIntOrNull() ?: 1
                    if (newQty >= 1 && newQty <= maxStock) {
                        onQuantityChange(newQty)
                    } else if (newQty > maxStock) {
                        textValue = maxStock.toString()
                        onQuantityChange(maxStock)
                    }
                }
            },
            modifier = Modifier.width(textFieldWidth),
            textStyle = LocalTextStyle.current.copy(
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = if (compact) 14.sp else 16.sp
            ),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = buttonColor,
                unfocusedBorderColor = MaterialTheme.colorScheme.outline
            ),
            shape = RoundedCornerShape(if (compact) 8.dp else 12.dp)
        )
        
        // Button Plus
        FilledTonalButton(
            onClick = {
                if (quantity < maxStock) {
                    val newQty = quantity + 1
                    onQuantityChange(newQty)
                    textValue = newQty.toString()
                }
            },
            modifier = Modifier.size(buttonSize),
            shape = RoundedCornerShape(if (compact) 8.dp else 12.dp),
            colors = ButtonDefaults.filledTonalButtonColors(
                containerColor = buttonColor,
                contentColor = Color.White
            ),
            contentPadding = PaddingValues(0.dp),
            enabled = quantity < maxStock
        ) {
            Icon(
                Icons.Default.Add,
                contentDescription = "Tambah",
                modifier = Modifier.size(iconSize)
            )
        }
    }
    
    // Reset text if quantity changes externally
    LaunchedEffect(quantity) {
        if (textValue.toIntOrNull() != quantity) {
            textValue = quantity.toString()
        }
    }
}
