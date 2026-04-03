package com.sentinelbet.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.*
import com.sentinelbet.app.ui.theme.*

// ── Card ──────────────────────────────────────────────────────────────────────

@Composable
fun SbCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp)),
    ) { content() }
}

@Composable
fun SbCardHeader(
    title: String,
    modifier: Modifier = Modifier,
    leading: @Composable (() -> Unit)? = null,
    trailing: @Composable (() -> Unit)? = null,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        if (leading != null) {
            leading()
            Spacer(Modifier.width(8.dp))
        }
        Text(
            text     = title.uppercase(),
            style    = MaterialTheme.typography.labelSmall.copy(
                fontSize      = 11.sp,
                fontWeight    = FontWeight.W700,
                letterSpacing = 1.sp,
                color         = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier.weight(1f),
        )
        if (trailing != null) trailing()
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline, thickness = 0.5.dp)
}

// ── KPI Card ──────────────────────────────────────────────────────────────────

@Composable
fun KpiCard(
    label: String,
    value: String,
    accentColor: Color,
    icon: ImageVector,
    subtitle: String? = null,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
            .padding(12.dp),
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(14.dp))
                Spacer(Modifier.width(6.dp))
                Text(label.uppercase(), style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, letterSpacing = 1.2.sp))
            }
            Spacer(Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.ExtraBold, color = accentColor))
            if (subtitle != null) {
                Text(subtitle, style = MaterialTheme.typography.bodySmall.copy(fontSize = 9.sp, color = MaterialTheme.colorScheme.onSurfaceVariant))
            }
        }
    }
}

// ── Probability Bar ────────────────────────────────────────────────────────────

@Composable
fun ProbabilityBar(label: String, probability: Double, color: Color, modifier: Modifier = Modifier) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 11.sp))
            Text("${"%.1f".format(probability * 100)}%", style = MaterialTheme.typography.bodySmall.copy(color = color, fontWeight = FontWeight.W700, fontSize = 11.sp))
        }
        Spacer(Modifier.height(4.dp))
        Box(
            Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(MaterialTheme.colorScheme.outline),
        ) {
            Box(
                Modifier
                    .fillMaxWidth(probability.toFloat().coerceIn(0f, 1f))
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(2.dp))
                    .background(color),
            )
        }
        Spacer(Modifier.height(8.dp))
    }
}

// ── Pulse Dot ─────────────────────────────────────────────────────────────────

@Composable
fun PulseDot(color: Color, size: Dp = 8.dp, modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 1f, targetValue = 0.3f,
        animationSpec = infiniteRepeatable(tween(800), RepeatMode.Reverse),
        label = "dotAlpha",
    )
    Box(modifier = modifier.size(size).clip(CircleShape).background(color.copy(alpha = alpha)))
}

// ── Button ────────────────────────────────────────────────────────────────────

@Composable
fun SbButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    loading: Boolean = false,
    enabled: Boolean = true,
    icon: ImageVector? = null,
) {
    Button(
        onClick  = onClick,
        enabled  = enabled && !loading,
        modifier = modifier.fillMaxWidth().height(48.dp),
        colors   = ButtonDefaults.buttonColors(
            containerColor         = MaterialTheme.colorScheme.primary,
            contentColor           = MaterialTheme.colorScheme.onPrimary,
            disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f),
            disabledContentColor   = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.6f),
        ),
        shape = RoundedCornerShape(8.dp),
    ) {
        if (loading) {
            CircularProgressIndicator(
                color       = MaterialTheme.colorScheme.onPrimary,
                strokeWidth = 2.dp,
                modifier    = Modifier.size(18.dp),
            )
        } else {
            if (icon != null) {
                Icon(icon, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(label.uppercase(), fontWeight = FontWeight.ExtraBold, fontSize = 13.sp, letterSpacing = 1.5.sp)
        }
    }
}

// ── TextField ─────────────────────────────────────────────────────────────────

@Composable
fun SbTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    keyboardType: KeyboardType = KeyboardType.Text,
    leadingIcon: @Composable (() -> Unit)? = null,
    isPassword: Boolean = false,
) {
    OutlinedTextField(
        value             = value,
        onValueChange     = onValueChange,
        label             = { Text(label) },
        modifier          = modifier.fillMaxWidth(),
        leadingIcon       = leadingIcon,
        singleLine        = true,
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions   = KeyboardOptions(keyboardType = keyboardType),
        colors            = OutlinedTextFieldDefaults.colors(
            focusedBorderColor   = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        ),
        shape = RoundedCornerShape(8.dp),
    )
}
