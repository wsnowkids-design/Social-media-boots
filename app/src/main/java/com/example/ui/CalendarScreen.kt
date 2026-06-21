package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.ContentSchedule
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.MidnightBlack
import com.example.ui.theme.SlateBlueCard
import com.example.ui.theme.SecondaryIcyBlue
import com.example.viewmodel.SocialViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CalendarScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val schedules by viewModel.schedules.collectAsState()
    var showAddScheduleDialog by remember { mutableStateOf(false) }

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
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Content Strategy Planner",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Plan and organize your upcoming publications across ecosystems.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showAddScheduleDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("add_planned_post_trigger")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Add Post", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Plan", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Summary metric planned posts
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val scheduledCount = schedules.count { it.status == "Scheduled" }
                val publishedCount = schedules.count { it.status == "Published" }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text("UPCOMING DRAFTS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text("$scheduledCount Scheduled", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                    }
                }

                Box(
                    modifier = Modifier
                        .weight(1f)
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(12.dp))
                        .border(
                            BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                            RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp)
                ) {
                    Column {
                        Text("COMPLETED POSTS", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, fontWeight = FontWeight.Bold)
                        Text("$publishedCount Published", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }
            }
        }

        // Section header List
        item {
            Text(
                text = "Publication Queue (" + schedules.size + ")",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (schedules.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = "Empty",
                            tint = ElectricBlue.copy(alpha = 0.4f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No content scheduled yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Draft your next great post copy directly using the AI or plan manually.",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        } else {
            items(schedules) { schedule ->
                ScheduleRow(
                    schedule = schedule,
                    onUpdateStatus = { newStatus -> viewModel.updateScheduleStatus(schedule.id, newStatus) },
                    onDelete = { viewModel.deleteSchedule(schedule.id) }
                )
            }
        }

        item { Spacer(modifier = Modifier.height(74.dp)) }
    }

    // Modal Add Planned Post Dialogue
    if (showAddScheduleDialog) {
        var tempTitle by remember { mutableStateOf("") }
        var tempCaption by remember { mutableStateOf("") }
        var tempPlatform by remember { mutableStateOf("Instagram") }
        var tempType by remember { mutableStateOf("Reel") } // Reel, Post, Video, Story
        var tempDaysAdvance by remember { mutableStateOf(1) } // 1, 2, 3 days

        Dialog(onDismissRequest = { showAddScheduleDialog = false }) {
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
                        text = "Plan New Social Post",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    OutlinedTextField(
                        value = tempTitle,
                        onValueChange = { tempTitle = it },
                        label = { Text("Planned Title") },
                        placeholder = { Text("e.g. Wednesday coding vlog") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth().testTag("add_schedule_title")
                    )

                    OutlinedTextField(
                        value = tempCaption,
                        onValueChange = { tempCaption = it },
                        label = { Text("Caption Hooks & Description") },
                        placeholder = { Text("Draft details...") },
                        maxLines = 4,
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth().testTag("add_schedule_caption")
                    )

                    Column {
                        Text("Target Social Platform:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Instagram", "TikTok", "YouTube", "Facebook").forEach { platform ->
                                val active = tempPlatform == platform
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { tempPlatform = platform }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = platform,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text("Format:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("Reel", "Post", "Story", "Video").forEach { type ->
                                val active = tempType == type
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { tempType = type }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = type,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Column {
                        Text("Publication Date Range:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf(
                                Pair(1, "Tomorrow"),
                                Pair(2, "In 2 Days"),
                                Pair(5, "In 5 Days")
                            ).forEach { (days, label) ->
                                val active = tempDaysAdvance == days
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(if (active) ElectricBlue else MaterialTheme.colorScheme.surfaceVariant)
                                        .clickable { tempDaysAdvance = days }
                                        .padding(vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = if (active) Color.Black else Color.White
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = {
                            if (tempTitle.isBlank()) {
                                Toast.makeText(context, "Please enter a schedule title!", Toast.LENGTH_SHORT).show()
                            } else {
                                val launchTime = System.currentTimeMillis() + (tempDaysAdvance * 86400000L)
                                viewModel.createSchedule(tempTitle, tempCaption, tempPlatform, launchTime, tempType)
                                showAddScheduleDialog = false
                                Toast.makeText(context, "Added Content Schedule item!", Toast.LENGTH_SHORT).show()
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_schedule_button")
                    ) {
                        Text("Confirm Planned Entry", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun ScheduleRow(
    schedule: ContentSchedule,
    onUpdateStatus: (String) -> Unit,
    onDelete: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("MMM d, yyyy - h:mm a", Locale.getDefault()) }
    val formattedTime = remember(schedule.scheduledTime) { sdf.format(Date(schedule.scheduledTime)) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("schedule_row_${schedule.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box(
                        modifier = Modifier
                            .background(
                                when (schedule.platform) {
                                    "Instagram" -> Color(0xFFFF3F80)
                                    "TikTok" -> Color.Black
                                    "YouTube" -> Color(0xFFFF0000)
                                    else -> Color(0xFF1877F2)
                                }.copy(alpha = 0.2f),
                                idPlatformCircleShape()
                            )
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = schedule.platform,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (schedule.platform) {
                                "Instagram" -> Color(0xFFFF3F80)
                                "TikTok" -> Color.White
                                "YouTube" -> Color(0xFFFF4D4D)
                                else -> Color(0xFF1877F2)
                            }
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(4.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(text = schedule.type, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                when (schedule.status) {
                                    "Published" -> Color(0xFF4CAF50).copy(alpha = 0.15f)
                                    else -> ElectricBlue.copy(alpha = 0.15f)
                                },
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = schedule.status.uppercase(),
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (schedule.status) {
                                "Published" -> Color(0xFF81C784)
                                else -> ElectricBlue
                            }
                        )
                    }

                    IconButton(onClick = onDelete, modifier = Modifier.size(28.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete planned post",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Column {
                Text(
                    text = schedule.title,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 15.sp
                )
                if (schedule.caption.isNotBlank()) {
                    Text(
                        text = schedule.caption,
                        fontSize = 13.sp,
                        color = SecondaryIcyBlue,
                        maxLines = 2,
                        lineHeight = 17.sp,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }

            Divider(color = MaterialTheme.colorScheme.background, thickness = 1.dp)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Scheduled: $formattedTime", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                if (schedule.status == "Scheduled") {
                    Button(
                        onClick = { onUpdateStatus("Published") },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                        modifier = Modifier.height(28.dp).testTag("publish_schedule_button_${schedule.id}")
                    ) {
                        Icon(imageVector = Icons.Default.CheckCircle, contentDescription = "Mark", modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Mark Published", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

fun idPlatformCircleShape(): RoundedCornerShape {
    return RoundedCornerShape(4.dp)
}
