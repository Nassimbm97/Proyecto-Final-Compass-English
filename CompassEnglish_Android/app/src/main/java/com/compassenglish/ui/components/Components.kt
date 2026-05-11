package com.compassenglish.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compassenglish.ui.theme.*

// Campo de texto doradito

@Composable
fun GoldTextField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    leadingIcon: ImageVector,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            Text(placeholder, color = TextLight, fontSize = 14.sp)
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = null,
                tint = GoldPrimary,
                modifier = Modifier.size(22.dp)
            )
        },
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        singleLine = true,
        shape = RoundedCornerShape(16.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor  = GoldSurfaceCard,
            unfocusedContainerColor = GoldSurfaceCard,
            focusedBorderColor     = GoldPrimary,
            unfocusedBorderColor   = Color.Transparent,
            focusedTextColor       = TextDark,
            unfocusedTextColor     = TextDark,
        ),
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RoundedCornerShape(16.dp))
    )
}


// Botón dorado principal

@Composable
fun GoldButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(0.dp),
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .shadow(6.dp, RoundedCornerShape(50.dp))
            .background(
                if (enabled)
                    Brush.verticalGradient(listOf(GoldLight, GoldPrimary))
                else
                    Brush.verticalGradient(listOf(Color.LightGray, Color.Gray)),
                RoundedCornerShape(50.dp)
            )
    ) {
        Text(
            text = text,
            color = if (enabled) TextDark else Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 16.sp,
            letterSpacing = 0.5.sp
        )
    }
}


// Tarjeta del menú principal (donde va ajustes, ejercicios....)

@Composable
fun MenuCard(
    title: String,
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = GoldSurfaceCard),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .aspectRatio(1f)
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = emoji,
                fontSize = 40.sp
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = title,
                color = TextDark,
                fontWeight = FontWeight.SemiBold,
                fontSize = 14.sp
            )
        }
    }
}


@Composable
fun ExerciseCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .background(GoldSurfaceCard, RoundedCornerShape(20.dp))
            .border(
                width = 3.dp,
                brush = Brush.verticalGradient(listOf(GoldLight, GoldPrimary)),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp)
    ) {
        Column(content = content)
    }
}


// Opción de multiple choice

@Composable
fun OptionButton(
    text: String,
    state: OptionState,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (bg, border, textColor) = when (state) {
        OptionState.CORRECT  -> Triple(Color(0xFFE8F5E9), Color(0xFF4CAF50), Color(0xFF2E7D32))
        OptionState.WRONG    -> Triple(Color(0xFFFFEBEE), Color(0xFFE53935), Color(0xFFB71C1C))
        OptionState.DEFAULT  -> Triple(GoldSurfaceCard, GoldBorder, TextDark)
        OptionState.DISABLED -> Triple(Color(0xFFF5F5F5), Color(0xFFDDDDDD), Color(0xFF999999))
    }

    OutlinedButton(
        onClick = onClick,
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(containerColor = bg),
        border = androidx.compose.foundation.BorderStroke(1.5.dp, border),
        modifier = modifier.fillMaxWidth().height(52.dp)
    ) {
        Text(text, color = textColor, fontWeight = FontWeight.Medium, fontSize = 15.sp)
    }
}

enum class OptionState { DEFAULT, CORRECT, WRONG, DISABLED }
