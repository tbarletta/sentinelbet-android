package com.sentinelbet.app.ui.screens

import android.app.Activity
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sentinelbet.app.viewmodel.AuthState
import com.sentinelbet.app.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    authVm: AuthViewModel,
    onAuthenticated: () -> Unit,
) {
    val authState by authVm.authState.collectAsState()
    val context   = LocalContext.current

    // Navigate away when authenticated
    LaunchedEffect(authState) {
        if (authState is AuthState.Authenticated) onAuthenticated()
    }

    var tab           by remember { mutableStateOf(LoginTab.SignIn) }
    var email         by remember { mutableStateOf("") }
    var password      by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val isLoading = authState is AuthState.Loading
    val errorMsg  = (authState as? AuthState.Error)?.message

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(64.dp))

            // ── Logo / App Name ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        Brush.radialGradient(
                            listOf(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f),
                                MaterialTheme.colorScheme.primary,
                            )
                        )
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = Icons.Filled.Shield,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.size(40.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text       = "SentinelBet",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                text       = "Análise inteligente de apostas esportivas",
                fontSize   = 13.sp,
                color      = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign  = TextAlign.Center,
                modifier   = Modifier.padding(top = 4.dp),
            )

            Spacer(Modifier.height(40.dp))

            // ── Tab: Entrar / Criar conta ────────────────────────────────────
            LoginTabRow(
                selected  = tab,
                onSelect  = {
                    tab = it
                    authVm.clearError()
                },
            )

            Spacer(Modifier.height(24.dp))

            // ── Google Sign-In ───────────────────────────────────────────────
            OutlinedButton(
                onClick  = {
                    val activity = context as? Activity ?: return@OutlinedButton
                    authVm.signInWithGoogle(activity)
                },
                enabled  = !isLoading,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
                border   = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.outline,
                ),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.onSurface,
                ),
            ) {
                Icon(
                    imageVector = Icons.Filled.AccountCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint     = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.width(12.dp))
                Text(
                    text       = "Continuar com Google",
                    fontWeight = FontWeight.Medium,
                    fontSize   = 15.sp,
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Divider ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(0.4f))
                Text(
                    text     = "  ou  ",
                    color    = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 12.sp,
                )
                HorizontalDivider(modifier = Modifier.weight(1f), color = MaterialTheme.colorScheme.outline.copy(0.4f))
            }

            Spacer(Modifier.height(20.dp))

            // ── Email field ──────────────────────────────────────────────────
            OutlinedTextField(
                value         = email,
                onValueChange = { email = it },
                modifier      = Modifier.fillMaxWidth(),
                label         = { Text("Email") },
                leadingIcon   = {
                    Icon(Icons.Filled.Email, contentDescription = null,
                         tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction    = ImeAction.Next,
                ),
                singleLine    = true,
                enabled       = !isLoading,
                shape         = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(12.dp))

            // ── Password field ───────────────────────────────────────────────
            OutlinedTextField(
                value         = password,
                onValueChange = { password = it },
                modifier      = Modifier.fillMaxWidth(),
                label         = { Text("Senha") },
                leadingIcon   = {
                    Icon(Icons.Filled.Lock, contentDescription = null,
                         tint = MaterialTheme.colorScheme.onSurfaceVariant)
                },
                trailingIcon  = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.VisibilityOff
                                          else Icons.Filled.Visibility,
                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                visualTransformation = if (passwordVisible) VisualTransformation.None
                                       else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction    = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(
                    onDone = {
                        if (tab == LoginTab.SignIn) authVm.signInWithEmail(email, password)
                        else                         authVm.signUpWithEmail(email, password)
                    }
                ),
                singleLine    = true,
                enabled       = !isLoading,
                shape         = RoundedCornerShape(12.dp),
            )

            Spacer(Modifier.height(8.dp))

            // ── Error message ────────────────────────────────────────────────
            AnimatedVisibility(visible = errorMsg != null) {
                Text(
                    text     = errorMsg ?: "",
                    color    = if (errorMsg?.startsWith("Conta criada") == true)
                                   MaterialTheme.colorScheme.primary
                               else
                                   MaterialTheme.colorScheme.error,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Primary action button ────────────────────────────────────────
            Button(
                onClick  = {
                    if (tab == LoginTab.SignIn) authVm.signInWithEmail(email, password)
                    else                         authVm.signUpWithEmail(email, password)
                },
                enabled  = !isLoading && email.isNotBlank() && password.length >= 6,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape    = RoundedCornerShape(12.dp),
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        color    = MaterialTheme.colorScheme.onPrimary,
                        strokeWidth = 2.dp,
                    )
                } else {
                    Text(
                        text       = if (tab == LoginTab.SignIn) "Entrar" else "Criar conta",
                        fontWeight = FontWeight.Bold,
                        fontSize   = 15.sp,
                    )
                }
            }

            Spacer(Modifier.height(40.dp))

            // ── Free plan disclaimer ─────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(10.dp))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Filled.CheckCircle,
                    contentDescription = null,
                    tint     = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp),
                )
                Spacer(Modifier.width(10.dp))
                Column {
                    Text(
                        text       = "Plano Gratuito disponível",
                        color      = MaterialTheme.colorScheme.onSurface,
                        fontSize   = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text     = "Dashboard, bankroll e histórico sem custo.",
                        color    = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp,
                    )
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}

private enum class LoginTab { SignIn, SignUp }

@Composable
private fun LoginTabRow(
    selected: LoginTab,
    onSelect: (LoginTab) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(4.dp),
    ) {
        LoginTab.entries.forEach { tab ->
            val isSelected = selected == tab
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        if (isSelected) MaterialTheme.colorScheme.surface
                        else            MaterialTheme.colorScheme.surfaceVariant
                    )
                    .then(
                        if (isSelected) Modifier.border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(10.dp)
                        ) else Modifier
                    )
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                TextButton(
                    onClick = { onSelect(tab) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        text       = if (tab == LoginTab.SignIn) "Entrar" else "Criar conta",
                        color      = if (isSelected) MaterialTheme.colorScheme.primary
                                     else            MaterialTheme.colorScheme.onSurfaceVariant,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                        fontSize   = 14.sp,
                    )
                }
            }
        }
    }
}
