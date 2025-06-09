package com.example.iot.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.iot.ui.theme.Aqua1
import com.example.iot.ui.theme.Coral1
import com.example.iot.ui.theme.Coral2
import com.example.iot.ui.theme.Grey1
import com.example.iot.ui.theme.Navy1
import com.example.iot.ui.theme.Navy3
import com.example.iot.ui.theme.Teal2
import com.example.iot.ui.theme.Teal3
import com.example.iot.ui.theme.Yellow1
import com.example.iot.ui.theme.Yellow2
import kotlinx.coroutines.delay

@Composable
fun AeratorControlWithLogic(
    pH: Double?,
    isAutoMode: Boolean,
    isAeratorOn: Boolean,
    onToggleAutoMode: (Boolean) -> Unit,
    onToggleAerator: () -> Unit
) {

    LaunchedEffect(pH, isAutoMode, isAeratorOn) {
        if (isAutoMode && isAeratorOn && pH != null && pH >= 7.0) {
            onToggleAerator()
        }
    }

    AeratorControl(
        isAutoMode = isAutoMode,
        isAeratorOn = isAeratorOn,
        onToggleAutoMode = onToggleAutoMode,
        onManualToggle = onToggleAerator
    )
}

@Composable
fun AeratorControl(
    isAutoMode: Boolean,
    isAeratorOn: Boolean,
    onToggleAutoMode: (Boolean) -> Unit,
    onManualToggle: () -> Unit
) {
    var secondsRunning by remember { mutableStateOf(0) }

    LaunchedEffect(isAeratorOn, isAutoMode) {
        if (isAeratorOn && !isAutoMode) {
            secondsRunning = 0
            while (true) {
                delay(1000)
                secondsRunning++
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .background(Teal3.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Text(
            text = "System Controls",
            color = Color.White,
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Auto Mode
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Aqua1.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Auto Mode",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isAutoMode) "Auto is ON" else "Auto is OFF",
                    color = Grey1,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Switch(
                checked = isAutoMode,
                onCheckedChange = { onToggleAutoMode(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.Black,
                    uncheckedThumbColor = Color.Black,
                    checkedTrackColor = Aqua1,
                    uncheckedTrackColor = Color.Gray
                )
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Manual Control
        Text(
            text = "Manual Control",
            color = Color.White,
            style = MaterialTheme.typography.titleMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = onManualToggle,
            enabled = !isAutoMode,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isAeratorOn) Coral1 else Aqua1,
                disabledContainerColor = Color.Gray,
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (isAeratorOn) "Turn Off" else "Turn On",
                style = MaterialTheme.typography.labelLarge
            )
        }

        if (isAeratorOn && !isAutoMode) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Running for: $secondsRunning seconds",
                color = Grey1,
                style = MaterialTheme.typography.bodySmall
            )
        }

        if (isAutoMode) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Manual control is disabled while Auto Mode is active.",
                color = Grey1,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
