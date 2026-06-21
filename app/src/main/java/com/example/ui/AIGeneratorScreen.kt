package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.SlateBlueCard
import com.example.viewmodel.SocialViewModel

@Composable
fun AIGeneratorScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val username by viewModel.username.collectAsState()
    val defaultNiche by viewModel.niche.collectAsState()
    val defaultPlatform by viewModel.primaryPlatform.collectAsState()

    val aiResponse by viewModel.aiResponse.collectAsState()
    val aiLoading by viewModel.aiLoading.collectAsState()

    var activePlatform by remember { mutableStateOf(defaultPlatform) }
    var activeNiche by remember { mutableStateOf(defaultNiche) }
    var topicTopicText by remember { mutableStateOf("") }
    var generatorType by remember { mutableStateOf("Ideas") } // Caption, Ideas, Plan

    var showAddToCalendarDialog by remember { mutableStateOf(false) }
    
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item { Spacer(modifier = Modifier.height(8.dp)) }

        // Header Title
        item {
            Column {
                Text(
                    text = "Social Boost AI Creator",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Generate hyper-targeted, high-engagement viral materials optimized for your niche.",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        // Segment Tabs: Ideas, Captions, Strategy
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                val types = listOf(
                    Pair("Ideas", "Viral Ideas"),
                    Pair("Caption", "Caption Maker"),
                    Pair("Plan", "Content Plan")
                )
                types.forEach { (key, label) ->
                    val isActive = generatorType == key
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isActive) ElectricBlue else Color.Transparent)
                            .clickable { generatorType = key }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isActive) Color.Black else Color.White
                        )
                    }
                }
            }
        }

        // Input Form Parameter Card
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(18.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Platform selector
                    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "TARGET SOCIAL NETWORK",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf("Instagram", "TikTok", "YouTube", "Facebook").forEach { platform ->
                                val selected = activePlatform == platform
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (selected) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { activePlatform = platform }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = platform,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (selected) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    // Niche selection input
                    OutlinedTextField(
                        value = activeNiche,
                        onValueChange = { activeNiche = it },
                        label = { Text("Your Branding Niche") },
                        placeholder = { Text("e.g. Fitness, Travel, Crypto...") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("ai_niche_input")
                    )

                    // Topic or Keywords of post
                    OutlinedTextField(
                        value = topicTopicText,
                        onValueChange = { topicTopicText = it },
                        label = { Text("Core Topic or Keyword") },
                        placeholder = {
                            when (generatorType) {
                                "Caption" -> "e.g. My morning habits, Gym motivation..."
                                "Ideas" -> "e.g. Cooking tips, Coding tutorials..."
                                else -> "e.g. 30-day coding marathon..."
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("ai_topic_input")
                    )

                    // Generate trigger button
                    Button(
                        onClick = {
                            if (topicTopicText.isBlank()) {
                                Toast.makeText(context, "Please enter a core keyword or topic first!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.generateAIContent(activePlatform, activeNiche, topicTopicText, generatorType)
                            }
                        },
                        enabled = !aiLoading,
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("ai_generate_button")
                    ) {
                        if (aiLoading) {
                            CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(20.dp))
                        } else {
                            Icon(imageVector = Icons.Default.Create, contentDescription = "AI")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Consult Social Boost AI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }

        // Response Output Card
        item {
            AnimatedVisibility(
                visible = aiResponse != null || aiLoading,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateBlueCard),
                    shape = RoundedCornerShape(18.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                    modifier = Modifier.fillMaxWidth().testTag("ai_response_card")
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "Response",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = "Social Boost Strategy Draft",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = ElectricBlue
                                )
                            }

                            if (!aiLoading && aiResponse != null && !aiResponse!!.startsWith("Error")) {
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    IconButton(
                                        onClick = {
                                            clipboardManager.setText(AnnotatedString(aiResponse ?: ""))
                                            Toast.makeText(context, "Copied recommendation details to clipboard!", Toast.LENGTH_SHORT).show()
                                        },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Share,
                                            contentDescription = "Copy Content",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    IconButton(
                                        onClick = { showAddToCalendarDialog = true },
                                        modifier = Modifier.size(28.dp).testTag("save_ai_to_calendar_trigger")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = "Save to Calendar",
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Divider(color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), thickness = 1.dp)

                        if (aiLoading) {
                            Column(
                                modifier = Modifier.fillMaxWidth().padding(24.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                CircularProgressIndicator(color = ElectricBlue, modifier = Modifier.size(32.dp))
                                Spacer(modifier = Modifier.height(14.dp))
                                Text(
                                    text = "AI Brainstorming copywriting drafts...",
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            Text(
                                text = aiResponse ?: "",
                                fontSize = 14.sp,
                                color = Color.White,
                                lineHeight = 21.sp,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(74.dp)) }
    }

    // Floating add AI plan to Calendar scheduler dialog
    if (showAddToCalendarDialog && aiResponse != null) {
        var eventTitle by remember { mutableStateOf("Planned: $topicTopicText") }
        var postType by remember { mutableStateOf("Reel") } // Reel, Story, Post
        var timeDaysInAdvance by remember { mutableStateOf(1) } // days in advance

        Dialog(onDismissRequest = { showAddToCalendarDialog = false }) {
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
                        text = "Schedule AI Content Draft",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    OutlinedTextField(
                        value = eventTitle,
                        onValueChange = { eventTitle = it },
                        label = { Text("Planned Post Title") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricBlue,
                            unfocusedBorderColor = MaterialTheme.colorScheme.surfaceVariant
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("ai_schedule_title")
                    )

                    Column {
                        Text("Post Format:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        listOf("Reel", "Video", "Post", "Story").forEach { type ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { postType = type }
                                    .padding(vertical = 4.dp)
                            ) {
                                RadioButton(
                                    selected = postType == type,
                                    onClick = { postType = type },
                                    colors = RadioButtonDefaults.colors(selectedColor = ElectricBlue)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(type, color = Color.White, fontSize = 13.sp)
                            }
                        }
                    }

                    Column {
                        Text("Schedule Publication Day Tracker:", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            listOf(
                                Pair(1, "Tomorrow"),
                                Pair(2, "In 2 Days"),
                                Pair(3, "In 3 Days")
                            ).forEach { (days, label) ->
                                val active = timeDaysInAdvance == days
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (active) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { timeDaysInAdvance = days }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            val scheduledTime = System.currentTimeMillis() + (timeDaysInAdvance * 86400000L)
                            viewModel.createSchedule(
                                title = eventTitle,
                                caption = aiResponse!!.take(600), // restrict length
                                platform = activePlatform,
                                time = scheduledTime,
                                type = postType
                            )
                            showAddToCalendarDialog = false
                            Toast.makeText(context, "Saved scheduled content item directly in calendar DB!", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("confirm_ai_calendar_schedule_button")
                    ) {
                        Text("Confirm Schedule", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
