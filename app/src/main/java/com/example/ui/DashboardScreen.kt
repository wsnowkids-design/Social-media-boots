package com.example.ui

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.AchievementBadge
import com.example.data.CompletedTip
import com.example.data.GrowthTip
import com.example.data.ViralChallenge
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.SlateBlueCard
import com.example.ui.theme.SoftIcyBlueText
import com.example.ui.theme.SecondaryIcyBlue
import com.example.viewmodel.SocialViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val username by viewModel.username.collectAsState()
    val niche by viewModel.niche.collectAsState()
    val primaryPlatform by viewModel.primaryPlatform.collectAsState()
    
    val statsSummary by viewModel.statsSummary.collectAsState()
    val leaderboard by viewModel.leaderboard.collectAsState()
    val challenges by viewModel.challenges.collectAsState()
    val completedTips by viewModel.completedTipsList.collectAsState()
    val badges by viewModel.badges.collectAsState()

    // Collect social connection states dynamically
    val connectedInstagram by viewModel.connectedInstagram.collectAsState()
    val instagramEmail by viewModel.instagramEmail.collectAsState()
    val instagramPhone by viewModel.instagramPhone.collectAsState()

    val connectedTikTok by viewModel.connectedTikTok.collectAsState()
    val tiktokEmail by viewModel.tiktokEmail.collectAsState()
    val tiktokPhone by viewModel.tiktokPhone.collectAsState()

    val connectedYouTube by viewModel.connectedYouTube.collectAsState()
    val youtubeEmail by viewModel.youtubeEmail.collectAsState()
    val youtubePhone by viewModel.youtubePhone.collectAsState()

    val connectedFacebook by viewModel.connectedFacebook.collectAsState()
    val facebookEmail by viewModel.facebookEmail.collectAsState()
    val facebookPhone by viewModel.facebookPhone.collectAsState()

    var showConnectDialogForPlatform by remember { mutableStateOf<String?>(null) }
    var connectEmail by remember { mutableStateOf("") }
    var connectPhone by remember { mutableStateOf("") }
    var connectVerificationStep by remember { mutableStateOf(1) }
    var connectOtpReceived by remember { mutableStateOf("") }
    var connectOtpInput by remember { mutableStateOf("") }
    var connectOtpError by remember { mutableStateOf("") }

    var selectedTipForDialog by remember { mutableStateOf<GrowthTip?>(null) }
    var selectedBadgeForDialog by remember { mutableStateOf<AchievementBadge?>(null) }
    var showProfileEditDialog by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // App Spacer top
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Hero Header Profile
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_hero_card"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    MaterialTheme.colorScheme.surface
                                )
                            )
                        )
                        .padding(20.dp)
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(Brush.radialGradient(colors = listOf(ElectricBlue, NeonBlue))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = username.take(2).uppercase(),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 20.sp,
                                        color = Color.Black
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = "@$username",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 18.sp,
                                        color = Color.White
                                    )
                                    Text(
                                        text = "Niche: $niche",
                                        fontSize = 13.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            IconButton(
                                onClick = { showProfileEditDialog = true },
                                modifier = Modifier
                                    .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                                    .size(36.dp)
                                    .testTag("edit_profile_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Profile",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), thickness = 1.dp)

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(
                                    text = "ORGANIC AUDIENCE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp
                                )
                                Text(
                                    text = String.format("%,d", statsSummary.totalFollowers),
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ElectricBlue
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(
                                    text = "INFLUENCE SCORE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    letterSpacing = 1.sp
                                )
                                val influenceScore = calculateInfluenceScore(statsSummary.totalFollowers)
                                Text(
                                    text = "$influenceScore/100",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- SOCIAL PROFILE INTEGRATION PORTAL ---
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("social_connections_panel"),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = SlateBlueCard),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "Connect Accounts",
                                tint = ElectricBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Text(
                                text = "Integrate Profiles",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(ElectricBlue.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "SECURE OTP",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ElectricBlue
                            )
                        }
                    }

                    Text(
                        text = "Verify and connect your profiles using native Gmail email & phone verification pin numbers.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    // Display social connections list
                    val socialApps = listOf(
                        Triple("Instagram", connectedInstagram, instagramEmail),
                        Triple("TikTok", connectedTikTok, tiktokEmail),
                        Triple("YouTube", connectedYouTube, youtubeEmail),
                        Triple("Facebook", connectedFacebook, facebookEmail)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        socialApps.forEach { (platformName, isConnected, connectedEmail) ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                                    .padding(vertical = 10.dp, horizontal = 12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (isConnected) ElectricBlue.copy(alpha = 0.15f)
                                                else MaterialTheme.colorScheme.surfaceVariant
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = when (platformName) {
                                                "Instagram" -> Icons.Default.Face
                                                "TikTok" -> Icons.Default.PlayArrow
                                                "YouTube" -> Icons.Default.List
                                                else -> Icons.Default.Share
                                            },
                                            contentDescription = platformName,
                                            tint = if (isConnected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }

                                    Column {
                                        Text(
                                            text = platformName,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                        Text(
                                            text = if (isConnected) connectedEmail else "Not connected",
                                            fontSize = 11.sp,
                                            color = if (isConnected) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant,
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }

                                if (isConnected) {
                                    IconButton(
                                        onClick = { viewModel.disconnectSocialProfile(platformName) },
                                        modifier = Modifier
                                            .background(MaterialTheme.colorScheme.surface, CircleShape)
                                            .size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "Disconnect",
                                            tint = MaterialTheme.colorScheme.error,
                                            modifier = Modifier.size(14.dp)
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { showConnectDialogForPlatform = platformName },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = ElectricBlue,
                                            contentColor = Color.Black
                                        ),
                                        shape = RoundedCornerShape(8.dp),
                                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp),
                                        modifier = Modifier.height(28.dp)
                                    ) {
                                        Text(
                                            text = "Link",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Daily Tips list
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Daily Growth Strategies",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${completedTips.size}/8 Done",
                        fontSize = 12.sp,
                        color = ElectricBlue,
                        fontWeight = FontWeight.Medium
                    )
                }

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    items(viewModel.repository.growthTips) { tip ->
                        val isDone = completedTips.any { it.tipId == tip.id }
                        Card(
                            modifier = Modifier
                                .width(220.dp)
                                .height(145.dp)
                                .clickable { selectedTipForDialog = tip }
                                .testTag("growth_tip_${tip.id}"),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            shape = RoundedCornerShape(16.dp),
                            border = if (isDone) BorderStroke(1.5.dp, ElectricBlue) else BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(14.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Row(
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .background(
                                                    when (tip.platform) {
                                                        "Instagram" -> Color(0xFFFF3F80)
                                                        "TikTok" -> Color.Black
                                                        "YouTube" -> Color(0xFFFF0000)
                                                        else -> Color(0xFF1877F2)
                                                    }.copy(alpha = 0.2f),
                                                    shape = RoundedCornerShape(4.dp)
                                                )
                                                .padding(horizontal = 6.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = tip.platform,
                                                fontSize = 9.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = when (tip.platform) {
                                                    "Instagram" -> Color(0xFFFF3F80)
                                                    "TikTok" -> Color.White
                                                    "YouTube" -> Color(0xFFFF4D4D)
                                                    else -> Color(0xFF1877F2)
                                                }
                                            )
                                        }
                                        if (isDone) {
                                            Icon(
                                                imageVector = Icons.Default.CheckCircle,
                                                contentDescription = "Read",
                                                tint = ElectricBlue,
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = tip.title,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        maxLines = 2,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }
                                Text(
                                    text = tip.category,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Gamified badges / rewards unlocked
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Achievements & Badges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(badges) { badge ->
                        Box(
                            modifier = Modifier
                                .size(70.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (badge.isUnlocked) MaterialTheme.colorScheme.surfaceVariant else SlateBlueCard.copy(alpha = 0.5f))
                                .border(
                                    width = if (badge.isUnlocked) 1.5.dp else 1.dp,
                                    color = if (badge.isUnlocked) ElectricBlue else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                )
                                .clickable { selectedBadgeForDialog = badge }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = when (badge.iconName) {
                                        "rocket_launch" -> Icons.Default.PlayArrow
                                        "psychology" -> Icons.Default.Edit
                                        "show_chart" -> Icons.Default.Share
                                        "emoji_events" -> Icons.Default.Star
                                        "calculate" -> Icons.Default.Add
                                        else -> Icons.Default.DateRange
                                    },
                                    contentDescription = badge.title,
                                    tint = if (badge.isUnlocked) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    modifier = Modifier.size(24.dp)
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = badge.title.split(" ").last(),
                                    fontSize = 9.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    color = if (badge.isUnlocked) Color.White else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        // Challenges Center
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "Viral Growth Challenges",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Column(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    challenges.forEach { challenge ->
                        ChallengeItem(
                            challenge = challenge,
                            onAccept = { viewModel.acceptChallenge(challenge.id) },
                            onIncrement = {
                                viewModel.incrementChallenge(
                                    challenge.id,
                                    challenge.completedDays,
                                    challenge.totalDays
                                )
                            },
                            onReset = { viewModel.resetChallenge(challenge.id) }
                        )
                    }
                }
            }
        }

        // Integrated Local Creator Leaderboard
        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Global Growth Leaderboard",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Grow organic subscribers to rise in rankings among top boosters weekly.",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    shape = RoundedCornerShape(16.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ) {
                    Column {
                        leaderboard.forEachIndexed { index, user ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(if (user.isCurrentUser) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent)
                                    .padding(vertical = 12.dp, horizontal = 16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "#${user.rank}",
                                        fontWeight = FontWeight.Bold,
                                        color = if (user.rank <= 3) ElectricBlue else Color.White,
                                        modifier = Modifier.width(32.dp),
                                        fontSize = 15.sp
                                    )
                                    Box(
                                        modifier = Modifier
                                            .size(32.dp)
                                            .clip(CircleShape)
                                            .background(if (user.isCurrentUser) ElectricBlue else SlateBlueCard),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = user.username.drop(1).take(1).uppercase(),
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = if (user.isCurrentUser) Color.Black else Color.White
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(
                                                text = user.username,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                fontSize = 14.sp
                                            )
                                            if (user.isCurrentUser) {
                                                Box(
                                                    modifier = Modifier
                                                        .padding(horizontal = 4.dp)
                                                        .background(ElectricBlue.copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp))
                                                        .padding(horizontal = 4.dp, vertical = 1.dp)
                                                ) {
                                                    Text("YOU", fontSize = 8.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }
                                        Text(
                                            text = user.primaryPlatform,
                                            fontSize = 11.sp,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                                Text(
                                    text = String.format("%,d", user.followers),
                                    fontWeight = FontWeight.Bold,
                                    color = if (user.isCurrentUser) ElectricBlue else Color.White,
                                    fontSize = 14.sp
                                )
                            }
                            if (index < leaderboard.lastIndex) {
                                Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp)
                            }
                        }
                    }
                }
            }
        }

        // Spacer Bottom for system height navigation standard
        item { Spacer(modifier = Modifier.windowInsetsBottomHeight(WindowInsets.navigationBars).height(74.dp)) }
    }

    // --- DIALOGS ---

    // Tip detail dialog
    selectedTipForDialog?.let { tip ->
        val isDone = completedTips.any { it.tipId == tip.id }
        Dialog(onDismissRequest = { selectedTipForDialog = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = tip.platform.uppercase(),
                            color = ElectricBlue,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(Color.White.copy(alpha = 0.1f), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(tip.difficulty, fontSize = 10.sp, color = Color.White, fontWeight = FontWeight.Medium)
                        }
                    }

                    Text(
                        text = tip.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("STRATEGY SUMMARY", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(tip.summary, fontSize = 14.sp, color = Color.White, lineHeight = 20.sp)
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text("ACTION STEPS", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text(tip.actionSteps, fontSize = 14.sp, color = SoftIcyBlueText, lineHeight = 20.sp)
                    }

                    Button(
                        onClick = {
                            viewModel.toggleTipCompleted(tip.id, isDone)
                            selectedTipForDialog = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (isDone) MaterialTheme.colorScheme.surfaceVariant else ElectricBlue,
                            contentColor = if (isDone) Color.White else Color.Black
                        ),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("dialog_tip_complete_action")
                    ) {
                        Icon(
                            imageVector = if (isDone) Icons.Default.Close else Icons.Default.Check,
                            contentDescription = "Success",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isDone) "Mark as Incompleted" else "Completed organic strategy!",
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // Badge details dialog
    selectedBadgeForDialog?.let { badge ->
        Dialog(onDismissRequest = { selectedBadgeForDialog = null }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .clip(CircleShape)
                            .background(if (badge.isUnlocked) ElectricBlue else SlateBlueCard),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = badge.title,
                            tint = if (badge.isUnlocked) Color.Black else Color.White.copy(alpha = 0.3f),
                            modifier = Modifier.size(36.dp)
                        )
                    }
                    Text(
                        text = badge.title,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = badge.description,
                        fontSize = 14.sp,
                        color = SoftIcyBlueText,
                        textAlign = TextAlign.Center
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (badge.isUnlocked) "UNLOCKED REWARD ✔" else "NOT COMPLETED YET",
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp,
                            color = if (badge.isUnlocked) ElectricBlue else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    TextButton(onClick = { selectedBadgeForDialog = null }) {
                        Text("Close Details", color = Color.White)
                    }
                }
            }
        }
    }

    // Profile Setup Dialogue
    if (showProfileEditDialog) {
        var tempUser by remember { mutableStateOf(username) }
        var tempNiche by remember { mutableStateOf(niche) }
        var tempPlatform by remember { mutableStateOf(primaryPlatform) }

        Dialog(onDismissRequest = { showProfileEditDialog = false }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(4.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Configure Social Profile",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    OutlinedTextField(
                        value = tempUser,
                        onValueChange = { tempUser = it },
                        label = { Text("Your Handle (@optional)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("profile_username_input")
                    )

                    Column {
                        Text("Your Social Niche:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        val niches = listOf("Tech & Gadgets", "Fitness & Health", "Fashion & Beauty", "Food & Cooking", "Business & Finance", "Travel & Lifestyle", "Gaming")
                        ScrollableTabRow(
                            selectedTabIndex = niches.indexOf(tempNiche).coerceAtLeast(0),
                            edgePadding = 0.dp,
                            containerColor = Color.Transparent,
                            indicator = {}
                        ) {
                            for (item in niches) {
                                Tab(
                                    selected = tempNiche == item,
                                    onClick = { tempNiche = item },
                                    text = @Composable { Text(item, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                                    selectedContentColor = ElectricBlue,
                                    unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Column {
                        Text("Primary Social Media:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        val platforms = listOf("Instagram", "TikTok", "YouTube", "Facebook")
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            platforms.forEach { item ->
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (tempPlatform == item) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { tempPlatform = item }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = item,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (tempPlatform == item) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.updateProfile(tempUser, tempNiche, tempPlatform)
                            showProfileEditDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_profile_button")
                    ) {
                        Text("Apply Settings", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Secure Profile OTP dialog
    showConnectDialogForPlatform?.let { platformName ->
        val context = LocalContext.current
        Dialog(onDismissRequest = {
            showConnectDialogForPlatform = null
            connectEmail = ""
            connectPhone = ""
            connectVerificationStep = 1
            connectOtpReceived = ""
            connectOtpInput = ""
            connectOtpError = ""
        }) {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Header
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Link $platformName",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color.White
                            )
                            Text(
                                text = "Step $connectVerificationStep of 2",
                                fontSize = 11.sp,
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        IconButton(onClick = {
                            showConnectDialogForPlatform = null
                            connectEmail = ""
                            connectPhone = ""
                            connectVerificationStep = 1
                            connectOtpReceived = ""
                            connectOtpInput = ""
                            connectOtpError = ""
                        }) {
                            Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = Color.White)
                        }
                    }

                    Divider(color = MaterialTheme.colorScheme.surfaceVariant, thickness = 1.dp)

                    if (connectVerificationStep == 1) {
                        // Phase 1: Credentials Entry
                        Text(
                            text = "Provide your registered email account and phone number to verify and fetch organic statistics.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        OutlinedTextField(
                            value = connectEmail,
                            onValueChange = { connectEmail = it },
                            label = { Text("Email Account (e.g. Gmail)") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("connect_email_input")
                        )

                        OutlinedTextField(
                            value = connectPhone,
                            onValueChange = { connectPhone = it },
                            label = { Text("Phone Number") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("connect_phone_input")
                        )

                        Button(
                            onClick = {
                                if (connectEmail.isBlank() || !connectEmail.contains("@")) {
                                    Toast.makeText(context, "Please enter a valid email address.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                if (connectPhone.isBlank() || connectPhone.length < 5) {
                                    Toast.makeText(context, "Please enter a valid phone number.", Toast.LENGTH_SHORT).show()
                                    return@Button
                                }
                                // Generate a secure 6 digit OTP random pin code
                                val randomOtp = (100000..999999).random().toString()
                                connectOtpReceived = randomOtp
                                connectVerificationStep = 2
                                Toast.makeText(context, "Security code generated! Choose delivery method below.", Toast.LENGTH_LONG).show()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(48.dp)
                                .testTag("generate_otp_button")
                        ) {
                            Text("Generate security pin", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        // Phase 2: Send and Verify OTP Code
                        Text(
                            text = "We have generated security OTP verification code $connectOtpReceived for your $platformName link request.",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            // Gmail Integration Intent trigger
                            Button(
                                onClick = {
                                    val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("mailto:")
                                        putExtra(Intent.EXTRA_EMAIL, arrayOf(connectEmail))
                                        putExtra(Intent.EXTRA_SUBJECT, "[Social Boost] Verification Pin Code for $platformName")
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            "Hello,\n\nYour security OTP verification pin code to link your $platformName social profile in Social Boost App is:\n\n👉  $connectOtpReceived  👈\n\nPlease enter this code back in the app to complete integration.\n\nHappy boosting!"
                                        )
                                    }
                                    try {
                                        context.startActivity(Intent.createChooser(emailIntent, "Send Code with Gmail"))
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No email apps found. Copy pin: $connectOtpReceived", Toast.LENGTH_LONG).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("send_otp_gmail")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Email, contentDescription = "Gmail", modifier = Modifier.size(16.dp))
                                    Text("Send to Gmail", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // Phone Integration Intent trigger
                            Button(
                                onClick = {
                                    val smsIntent = Intent(Intent.ACTION_SENDTO).apply {
                                        data = Uri.parse("smsto:$connectPhone")
                                        putExtra("sms_body", "[Social Boost] Verification code to link $platformName profile is: $connectOtpReceived")
                                    }
                                    try {
                                        context.startActivity(smsIntent)
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "No messaging application found.", Toast.LENGTH_LONG).show()
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = Color.White),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("send_otp_phone")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Icon(imageVector = Icons.Default.Phone, contentDescription = "SMS", modifier = Modifier.size(16.dp))
                                    Text("Send to Phone", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        OutlinedTextField(
                            value = connectOtpInput,
                            onValueChange = {
                                connectOtpInput = it
                                connectOtpError = ""
                            },
                            label = { Text("Enter 6-digit PIN") },
                            singleLine = true,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = ElectricBlue,
                                unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                            ),
                            modifier = Modifier.fillMaxWidth().testTag("connect_otp_input")
                        )

                        if (connectOtpError.isNotEmpty()) {
                            Text(
                                text = connectOtpError,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.error,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            TextButton(
                                onClick = { connectVerificationStep = 1 },
                                modifier = Modifier.weight(1f).testTag("connect_back_button")
                            ) {
                                Text("Back", color = Color.White)
                            }

                            Button(
                                onClick = {
                                    if (connectOtpInput.trim() == connectOtpReceived) {
                                        viewModel.connectSocialProfile(platformName, connectEmail, connectPhone)
                                        Toast.makeText(context, "$platformName account connected successfully!", Toast.LENGTH_SHORT).show()
                                        showConnectDialogForPlatform = null
                                        connectEmail = ""
                                        connectPhone = ""
                                        connectVerificationStep = 1
                                        connectOtpReceived = ""
                                        connectOtpInput = ""
                                        connectOtpError = ""
                                    } else {
                                        connectOtpError = "Invalid verification code. Please try again."
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                                modifier = Modifier
                                    .weight(1f)
                                    .height(44.dp)
                                    .testTag("verify_otp_button")
                            ) {
                                Text("Verify & Connect", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ChallengeItem(
    challenge: ViralChallenge,
    onAccept: () -> Unit,
    onIncrement: () -> Unit,
    onReset: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("challenge_${challenge.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = challenge.title,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "Platform: ${challenge.platform}",
                        fontSize = 11.sp,
                        color = ElectricBlue
                    )
                }

                if (challenge.isAccepted) {
                    Box(
                        modifier = Modifier
                            .background(ElectricBlue.copy(alpha = 0.2f), RoundedCornerShape(4.dp))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text("Active", fontSize = 10.sp, color = ElectricBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Text(
                challenge.description,
                fontSize = 13.sp,
                color = SoftIcyBlueText,
                lineHeight = 18.sp
            )

            if (challenge.isAccepted) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Daily Progression: ${challenge.completedDays}/${challenge.totalDays} days scale",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = "${(challenge.completedDays * 100) / challenge.totalDays}%",
                            fontSize = 12.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    LinearProgressIndicator(
                        progress = { challenge.completedDays.toFloat() / challenge.totalDays },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .clip(RoundedCornerShape(3.dp)),
                        color = ElectricBlue,
                        trackColor = MaterialTheme.colorScheme.surfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = onIncrement,
                            enabled = challenge.completedDays < challenge.totalDays,
                            colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp)
                                .testTag("challenge_increment_${challenge.id}")
                        ) {
                            Text("Mark Day Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = onReset,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                            border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            modifier = Modifier
                                .height(40.dp)
                                .testTag("challenge_reset_${challenge.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Refresh, contentDescription = "Reset flag", modifier = Modifier.size(16.dp))
                        }
                    }
                }
            } else {
                Button(
                    onClick = onAccept,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant, contentColor = Color.White),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .testTag("challenge_accept_${challenge.id}")
                ) {
                    Text("Accept Consistency Challenge", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Utility calculate Influence score based on followers count
fun calculateInfluenceScore(followers: Long): Int {
    if (followers <= 0) return 5
    if (followers < 1000) return 15
    if (followers < 10000) return 35
    if (followers < 50000) return 55
    if (followers < 200000) return 75
    return 95
}
