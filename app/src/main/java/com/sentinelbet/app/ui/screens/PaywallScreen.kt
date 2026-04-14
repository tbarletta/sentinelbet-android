package com.sentinelbet.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Paywall / Upsell screen shown when a FREE user tries to access a PRO feature.
 *
 * This screen does NOT process payments directly — it links to the web checkout
 * flow or Google Play Billing (to be integrated in BILL-01).
 *
 * For now, it shows the value proposition and a CTA button.
 */
@Composable
fun PaywallScreen(
    onBack: () -> Unit,
    onUpgrade: () -> Unit = {},
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // ── Header ─────────────────────────────────────────────────────────
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack, modifier = Modifier.size(36.dp)) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Voltar",
                    tint = MaterialTheme.colorScheme.onSurface,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        // ── Crown badge ─────────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(24.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f))
                .border(2.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.4f), RoundedCornerShape(24.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = Icons.Filled.WorkspacePremium,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(44.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text       = "SentinelBets Pro",
            style      = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color      = MaterialTheme.colorScheme.onBackground,
            textAlign  = TextAlign.Center,
        )
        Text(
            text      = "Desbloqueie análises ilimitadas com IA\ne maximize seus retornos.",
            fontSize  = 14.sp,
            color     = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier  = Modifier.padding(top = 6.dp),
        )

        Spacer(Modifier.height(32.dp))

        // ── Feature list ────────────────────────────────────────────────────
        ProFeatureItem(
            icon  = Icons.Filled.AutoAwesome,
            title = "Análise IA Ilimitada",
            desc  = "Análise preditiva por Claude Sonnet sem limite diário.",
        )
        ProFeatureItem(
            icon  = Icons.Filled.MonetizationOn,
            title = "Value Bets Ilimitadas",
            desc  = "Veja todas as apostas de valor, não apenas as 3 do plano free.",
        )
        ProFeatureItem(
            icon  = Icons.Filled.LiveTv,
            title = "Live Premium",
            desc  = "Alertas em tempo real para oportunidades em jogos ao vivo.",
        )
        ProFeatureItem(
            icon  = Icons.Filled.FileDownload,
            title = "Exportação de Histórico",
            desc  = "Exporte suas apostas em CSV para análise avançada.",
        )
        ProFeatureItem(
            icon  = Icons.Filled.TrendingUp,
            title = "ROI Avançado",
            desc  = "Métricas detalhadas de bankroll e performance por período.",
        )

        Spacer(Modifier.height(32.dp))

        // ── Pricing card ─────────────────────────────────────────────────────
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.08f))
                .border(1.dp, MaterialTheme.colorScheme.primary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text       = "R$ 29,90",
                style      = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color      = MaterialTheme.colorScheme.primary,
            )
            Text(
                text     = "por mês · cancele quando quiser",
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }

        Spacer(Modifier.height(20.dp))

        // ── CTA button ───────────────────────────────────────────────────────
        Button(
            onClick  = onUpgrade,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape    = RoundedCornerShape(14.dp),
        ) {
            Icon(
                imageVector = Icons.Filled.WorkspacePremium,
                contentDescription = null,
                modifier = Modifier.size(20.dp),
            )
            Spacer(Modifier.width(8.dp))
            Text(
                text       = "Assinar Pro",
                fontWeight = FontWeight.Bold,
                fontSize   = 16.sp,
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text     = "Pagamento seguro via Google Play",
            fontSize = 11.sp,
            color    = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
        )

        Spacer(Modifier.height(24.dp))
    }
}

@Composable
private fun ProFeatureItem(
    icon: ImageVector,
    title: String,
    desc: String,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint     = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(18.dp),
            )
        }
        Spacer(Modifier.width(14.dp))
        Column {
            Text(
                text       = title,
                fontWeight = FontWeight.SemiBold,
                fontSize   = 14.sp,
                color      = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text     = desc,
                fontSize = 12.sp,
                color    = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 2.dp),
            )
        }
    }
}
