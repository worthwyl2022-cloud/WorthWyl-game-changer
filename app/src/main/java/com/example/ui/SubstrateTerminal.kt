package com.example.ui

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.core.substrate.SubstrateCore
import kotlinx.coroutines.launch

val NeonGreen = Color(0xFF00FF41)
val DeepBg = Color(0xFF0A0A0C)
val PanelBg = Color(0xFF131418)
val Amber = Color(0xFFFFB000)
val Subdued = Color(0xFF4A4B50)

@Composable
fun SubstrateTerminal(core: SubstrateCore) {
    val coroutineScope = rememberCoroutineScope()
    var input by remember { mutableStateOf("") }
    val log = remember { mutableStateListOf<LogEntry>() }
    val listState = rememberLazyListState()
    
    var metrics by remember { mutableStateOf(core.status()) }
    var immuneDirectives by remember { mutableStateOf(core.immune.directives()) }
    
    LaunchedEffect(Unit) {
        log.add(LogEntry(LogType.SYSTEM, "CRANIUM CORE v3.4.0 INITIALIZED."))
        log.add(LogEntry(LogType.SYSTEM, "UNITY PRINCIPLE LOCKED (mass: 18.0)."))
        log.add(LogEntry(LogType.SYSTEM, "Awaiting intention injection..."))
    }
    
    LaunchedEffect(log.size) {
        if (log.isNotEmpty()) {
            listState.animateScrollToItem(log.size - 1)
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBg)
            .padding(16.dp)
            .systemBarsPadding()
    ) {
        // Top HUD
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PanelBg)
                .border(1.dp, Subdued, RoundedCornerShape(8.dp))
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text("CRANIUM CORE v3.4.0", color = NeonGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                MetricBar("AROUSAL", metrics["arousal"] ?: 0.0, 1.6)
                MetricBar("ENERGY", metrics["field_energy"] ?: 0.0, 10.0)
                MetricBar("COHERENCE", metrics["coherence"] ?: 0.0, 1.0)
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f), horizontalAlignment = Alignment.End) {
                Text("DIRECTIVES", color = Subdued, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                val dirs = if (immuneDirectives.isEmpty()) "ADVANCE" else immuneDirectives.joinToString(" | ")
                Text(dirs, color = Amber, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
                
                Spacer(modifier = Modifier.height(16.dp))
                Text("ATOMS", color = Subdued, fontFamily = FontFamily.Monospace, fontSize = 12.sp)
                Text("\${core.field.memory.allActive().size}", color = Color.White, fontFamily = FontFamily.Monospace)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Log output
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(Color.Black)
                .border(1.dp, Subdued.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            items(log) { entry ->
                when (entry.type) {
                    LogType.USER -> Text("> \${entry.text}", color = Amber, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(vertical = 4.dp))
                    LogType.SYSTEM -> Text(entry.text, color = NeonGreen, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(vertical = 4.dp))
                    LogType.ERROR -> Text(entry.text, color = Color.Red, fontFamily = FontFamily.Monospace, modifier = Modifier.padding(vertical = 4.dp))
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Input
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(PanelBg)
                .border(1.dp, Subdued, RoundedCornerShape(8.dp))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("> ", color = NeonGreen, fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold)
            BasicTextField(
                value = input,
                onValueChange = { input = it },
                textStyle = TextStyle(color = Color.White, fontFamily = FontFamily.Monospace, fontSize = 16.sp),
                cursorBrush = SolidColor(NeonGreen),
                modifier = Modifier.weight(1f).padding(start = 8.dp),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(
                    onSend = {
                        val txt = input
                        if (txt.isNotBlank()) {
                            log.add(LogEntry(LogType.USER, txt))
                            input = ""
                            coroutineScope.launch {
                                val out = core.injectIntention(txt)
                                log.add(LogEntry(if (out.startsWith("[IMMUNE") || out.startsWith("[SUBSTRATE")) LogType.ERROR else LogType.SYSTEM, out))
                                metrics = core.status()
                                immuneDirectives = core.immune.directives()
                            }
                        }
                    }
                )
            )
        }
    }
}

@Composable
fun MetricBar(label: String, value: Double, max: Double) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp)) {
        Text(label.padEnd(10), color = Subdued, fontFamily = FontFamily.Monospace, fontSize = 10.sp, modifier = Modifier.width(70.dp))
        val progress = (value / max).toFloat().coerceIn(0f, 1f)
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier.weight(1f).height(6.dp).clip(RoundedCornerShape(3.dp)),
            color = NeonGreen,
            trackColor = Subdued.copy(alpha = 0.3f),
        )
    }
}

enum class LogType { USER, SYSTEM, ERROR }
data class LogEntry(val type: LogType, val text: String)
