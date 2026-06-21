package com.example.ui

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.GrowthMetric
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.NeonBlue
import com.example.ui.theme.SlateBlueCard
import com.example.ui.theme.SecondaryIcyBlue
import com.example.ui.theme.SoftIcyBlueText
import com.example.viewmodel.SocialViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun TrackerScreen(
    viewModel: SocialViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.metrics.collectAsState()
    val calcResult by viewModel.calcResult.collectAsState()

    var selectedPlatformFilter by remember { mutableStateOf("All") }
    var showLogMetricDialog by remember { mutableStateOf(false) }

    // Calculator values
    var followersInput by remember { mutableStateOf("") }
    var likesInput by remember { mutableStateOf("") }
    var commentsInput by remember { mutableStateOf("") }
    var sharesInput by remember { mutableStateOf("") }

    val context = LocalContext.current

    // Filter metrics list
    val filteredMetrics = remember(metrics, selectedPlatformFilter) {
        if (selectedPlatformFilter == "All") {
            metrics
        } else {
            metrics.filter { it.platform == selectedPlatformFilter }
        }
    }

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
                        text = "Analytics & Utilities",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Monitor organic growth tracks and optimize delivery schedules.",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = { showLogMetricDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.testTag("log_metric_trigger")
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "Log", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        // Platform filter Tabs
        item {
            ScrollableTabRow(
                selectedTabIndex = listOf("All", "Instagram", "TikTok", "YouTube", "Facebook").indexOf(selectedPlatformFilter).coerceAtLeast(0),
                edgePadding = 0.dp,
                containerColor = MaterialTheme.colorScheme.surface,
                modifier = Modifier.clip(RoundedCornerShape(12.dp)),
                indicator = {}
            ) {
                for (platform in listOf("All", "Instagram", "TikTok", "YouTube", "Facebook")) {
                    val isSelected = selectedPlatformFilter == platform
                    Tab(
                        selected = isSelected,
                        onClick = { selectedPlatformFilter = platform },
                        text = @Composable { Text(platform, fontSize = 11.sp, fontWeight = FontWeight.Bold) },
                        selectedContentColor = ElectricBlue,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        // Custom Canvas Followers trajectory Chart!
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Followers Trajectory: $selectedPlatformFilter Line Graph",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    if (filteredMetrics.size < 2) {
                        // Empty states message
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Info,
                                    contentDescription = "No data",
                                    tint = ElectricBlue.copy(alpha = 0.5f),
                                    modifier = Modifier.size(32.dp)
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = "Log at least 2 metrics for $selectedPlatformFilter to render growth trajectory Bezier graphs here.",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    } else {
                        // Custom Canvas Draw bezier path line graph!
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(160.dp)
                                .background(MaterialTheme.colorScheme.background, RoundedCornerShape(12.dp))
                                .padding(horizontal = 8.dp, vertical = 12.dp)
                        ) {
                            val w = size.width
                            val h = size.height

                            val maxVal = filteredMetrics.maxOf { it.followers }.toFloat()
                            val minVal = filteredMetrics.minOf { it.followers }.toFloat()
                            val range = if (maxVal == minVal) 1.0f else (maxVal - minVal)

                            val path = Path()
                            val fillPath = Path()

                            val stepX = w / (filteredMetrics.size - 1)

                            // Generate smooth coordinates
                            val coords = filteredMetrics.mapIndexed { idx, metric ->
                                val x = idx * stepX
                                val pct = (metric.followers - minVal) / range
                                val y = h - (pct * h)
                                Offset(x, y)
                            }

                            // Begin smooth line bezier drawing
                            coords.forEachIndexed { idx, point ->
                                if (idx == 0) {
                                    path.moveTo(point.x, point.y)
                                    fillPath.moveTo(point.x, h)
                                    fillPath.lineTo(point.x, point.y)
                                } else {
                                    val prev = coords[idx - 1]
                                    val conX1 = prev.x + (point.x - prev.x) / 2
                                    val conY1 = prev.y
                                    val conX2 = prev.x + (point.x - prev.x) / 2
                                    val conY2 = point.y

                                    path.cubicTo(conX1, conY1, conX2, conY2, point.x, point.y)
                                    fillPath.cubicTo(conX1, conY1, conX2, conY2, point.x, point.y)
                                }

                                if (idx == coords.lastIndex) {
                                    fillPath.lineTo(point.x, h)
                                    fillPath.close()
                                }
                            }

                            // Draw glowing brush shading
                            drawPath(
                                path = fillPath,
                                brush = Brush.verticalGradient(
                                    colors = listOf(ElectricBlue.copy(alpha = 0.25f), Color.Transparent)
                                )
                            )

                            // Draw Bezier line
                            drawPath(
                                path = path,
                                color = ElectricBlue,
                                style = Stroke(width = 3.dp.toPx())
                            )

                            // Draw point circles
                            coords.forEach { point ->
                                drawCircle(
                                    color = NeonBlue,
                                    radius = 4.dp.toPx(),
                                    center = point
                                )
                                drawCircle(
                                    color = Color.White,
                                    radius = 2.dp.toPx(),
                                    center = point
                                )
                            }
                        }

                        // Display chart bounds
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "Min: " + String.format("%,d", filteredMetrics.minOf { it.followers }),
                                fontSize = 10.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Max: " + String.format("%,d", filteredMetrics.maxOf { it.followers }),
                                fontSize = 10.sp,
                                color = ElectricBlue,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        // Engagement benchmark Evaluator calculator
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
                    Text(
                        text = "Organic Engagement Calculator",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "Evaluate your average reach density based on interactions weight.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = followersInput,
                            onValueChange = { followersInput = it },
                            label = { Text("Followers") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                            modifier = Modifier.weight(1f).testTag("calc_followers")
                        )
                        OutlinedTextField(
                            value = likesInput,
                            onValueChange = { likesInput = it },
                            label = { Text("Likes Avg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                            modifier = Modifier.weight(1f).testTag("calc_likes")
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = commentsInput,
                            onValueChange = { commentsInput = it },
                            label = { Text("Comments Avg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                            modifier = Modifier.weight(1f).testTag("calc_comments")
                        )
                        OutlinedTextField(
                            value = sharesInput,
                            onValueChange = { sharesInput = it },
                            label = { Text("Shares Avg") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                            modifier = Modifier.weight(1f).testTag("calc_shares")
                        )
                    }

                    Button(
                        onClick = {
                            if (followersInput.isBlank()) {
                                Toast.makeText(context, "Please enter your follower count baseline!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.calculateEngagement(followersInput, likesInput, commentsInput, sharesInput)
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(44.dp)
                            .testTag("calc_evaluate_button")
                    ) {
                        Text("Evaluate Rate Metrics", fontWeight = FontWeight.Bold)
                    }

                    calcResult?.let { res ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(12.dp))
                                .padding(12.dp)
                        ) {
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Interaction Rate:", fontSize = 12.sp, color = SecondaryIcyBlue)
                                    Text("${res.rate}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                                }
                                Row(
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Engagement Status:", fontSize = 12.sp, color = SecondaryIcyBlue)
                                    Text(res.rating, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = "Advice: " + res.advice,
                                    fontSize = 12.sp,
                                    color = SoftIcyBlueText,
                                    lineHeight = 16.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Best organic posting scheduler times suggestions
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text(
                        text = "Algorithm-Peak Time Suggestions",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    val platformTimes = mapOf(
                        "Instagram" to listOf("Monday 9AM", "Wednesday 12PM (Peak)", "Friday 3PM"),
                        "TikTok" to listOf("Tuesday 10AM", "Thursday 7PM (Peak)", "Sunday 8AM"),
                        "YouTube" to listOf("Thursday 2PM", "Friday 1PM", "Saturday 10AM (Peak)"),
                        "Facebook" to listOf("Monday 11AM", "Wednesday 1PM (Peak)", "Thursday 10AM")
                    )

                    val times = platformTimes[if (selectedPlatformFilter == "All") "Instagram" else selectedPlatformFilter] ?: emptyList()

                    Text(
                        text = "Peak traffic hours are calculated according to organic target indexes for " + (if (selectedPlatformFilter == "All") "Instagram" else selectedPlatformFilter) + ":",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        times.forEach { time ->
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = "Time",
                                    tint = ElectricBlue,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(time, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Historical Track Log list
        item {
            Text(
                text = "Metric History Logs (" + filteredMetrics.size + ")",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }

        if (filteredMetrics.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        "No tracking logs recorded yet. Tap the top 'Log' button to submit organic data.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredMetrics.reversed()) { log ->
                MetricRow(log = log, onDelete = { viewModel.deleteMetric(log) })
            }
        }

        item { Spacer(modifier = Modifier.height(74.dp)) }
    }

    // Modal Log Metric Dialog
    if (showLogMetricDialog) {
        var tempPlatform by remember { mutableStateOf(if (selectedPlatformFilter == "All") "Instagram" else selectedPlatformFilter) }
        var tempFollowers by remember { mutableStateOf("") }
        var tempLikes by remember { mutableStateOf("") }
        var tempER by remember { mutableStateOf("") }
        var tempPosts by remember { mutableStateOf("") }

        Dialog(onDismissRequest = { showLogMetricDialog = false }) {
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
                        text = "Log Professional Metrics",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )

                    // Platform buttons row
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text("Social Channel:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
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

                    OutlinedTextField(
                        value = tempFollowers,
                        onValueChange = { tempFollowers = it },
                        label = { Text("Followers count") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth().testTag("log_followers_input")
                    )

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = tempLikes,
                            onValueChange = { tempLikes = it },
                            label = { Text("Total Likes") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = tempER,
                            onValueChange = { tempER = it },
                            label = { Text("ER % (e.g. 3.2)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = tempPosts,
                        onValueChange = { tempPosts = it },
                        label = { Text("Total Posts uploaded") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = ElectricBlue),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Button(
                        onClick = {
                            val f = tempFollowers.toLongOrNull() ?: 0L
                            val l = tempLikes.toLongOrNull() ?: 0L
                            val er = tempER.toDoubleOrNull() ?: 0.0
                            val p = tempPosts.toIntOrNull() ?: 0

                            if (f <= 0) {
                                Toast.makeText(context, "Please enter a valid follower count!", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.logMetric(tempPlatform, f, l, er, p)
                                showLogMetricDialog = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = ElectricBlue, contentColor = Color.Black),
                        modifier = Modifier.fillMaxWidth().height(48.dp).testTag("save_metric_button")
                    ) {
                        Text("Save Analytics Record", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun MetricRow(
    log: GrowthMetric,
    onDelete: () -> Unit
) {
    val sdf = remember { SimpleDateFormat("MMMM d, yyyy", Locale.getDefault()) }
    val formattedDate = remember(log.date) { sdf.format(Date(log.date)) }

    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .background(
                                when (log.platform) {
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
                            text = log.platform,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold,
                            color = when (log.platform) {
                                "Instagram" -> Color(0xFFFF3F80)
                                "TikTok" -> Color.White
                                "YouTube" -> Color(0xFFFF4D4D)
                                else -> Color(0xFF1877F2)
                            }
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = formattedDate, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Column {
                        Text("Followers", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("%,d", log.followers), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("Likes", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(String.format("%,d", log.likes), fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Column {
                        Text("ER %", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text("${log.engagementRate}%", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = ElectricBlue)
                    }
                }
            }

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete log entry",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
