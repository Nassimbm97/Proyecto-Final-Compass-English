package com.compassenglish.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.compassenglish.data.api.RankingItem
import com.compassenglish.data.api.RetrofitClient
import com.compassenglish.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun RankingScreen(
    currentUserId: Int,
    onBack: () -> Unit
) {
    var ranking   by remember { mutableStateOf<List<RankingItem>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        scope.launch {
            try {
                val resp = RetrofitClient.api.getRanking(20)
                if (resp.isSuccessful) ranking = resp.body() ?: emptyList()
            } catch (_: Exception) {}
            isLoading = false
        }
    }

    Column(
        modifier = Modifier.fillMaxSize().background(GoldSurface)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.Default.ArrowBack, null, tint = GoldDark)
            }
            Text("🏆  Ranking global", color = TextDark,
                fontWeight = FontWeight.Bold, fontSize = 20.sp)
        }

        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = GoldPrimary)
            }
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Top 3 podio
                if (ranking.size >= 3) {
                    item {
                        PodiumRow(ranking.take(3), currentUserId)
                        Spacer(Modifier.height(12.dp))
                    }
                }
                itemsIndexed(ranking.drop(3)) { idx, item ->
                    RankingRow(
                        position = idx + 4,
                        item     = item,
                        isMe     = item.id == currentUserId
                    )
                }
                item { Spacer(Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun PodiumRow(top3: List<RankingItem>, currentUserId: Int) {
    val order = listOf(1, 0, 2) // 2º, 1º, 3º visualmente
    val medals = listOf("🥇", "🥈", "🥉")
    val heights = listOf(80.dp, 100.dp, 60.dp)

    Row(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        order.forEachIndexed { visualIdx, dataIdx ->
            if (dataIdx < top3.size) {
                val item = top3[dataIdx]
                val isMe = item.id == currentUserId
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(medals[dataIdx], fontSize = 24.sp)
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(if (isMe) GoldPrimary else GoldBorder),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            item.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                            color = if (isMe) Color.White else TextDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        item.username,
                        color = if (isMe) GoldDark else TextDark,
                        fontWeight = if (isMe) FontWeight.Bold else FontWeight.Normal,
                        fontSize = 11.sp,
                        maxLines = 1
                    )
                    Text(
                        "${item.points} pts",
                        color = GoldDark,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.8f)
                            .height(heights[visualIdx])
                            .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                            .background(
                                when (dataIdx) {
                                    0 -> Color(0xFFFFD700)
                                    1 -> Color(0xFFC0C0C0)
                                    else -> Color(0xFFCD7F32)
                                }.copy(alpha = 0.35f)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun RankingRow(position: Int, item: RankingItem, isMe: Boolean) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isMe) GoldBorder.copy(alpha = 0.3f) else GoldSurfaceCard
        ),
        elevation = CardDefaults.cardElevation(if (isMe) 3.dp else 1.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "#$position",
                color = TextLight,
                fontSize = 13.sp,
                modifier = Modifier.width(36.dp)
            )
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isMe) GoldPrimary else GoldBorder),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    item.username.firstOrNull()?.uppercaseChar()?.toString() ?: "?",
                    color = if (isMe) Color.White else TextDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }
            Spacer(Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    item.username,
                    color = if (isMe) GoldDark else TextDark,
                    fontWeight = if (isMe) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 14.sp
                )
                Text(
                    item.levelEstimated.lowercase().replaceFirstChar { it.uppercaseChar() },
                    color = TextLight,
                    fontSize = 11.sp
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.EmojiEvents, null,
                    tint = GoldPrimary, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(3.dp))
                Text(
                    "${item.points}",
                    color = GoldDark,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }
        }
    }
}
