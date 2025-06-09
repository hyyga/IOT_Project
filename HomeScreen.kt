package com.example.iot.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.example.iot.ui.components.AeratorControl
import com.example.iot.ui.components.AeratorControlWithLogic
import com.example.iot.ui.components.HeaderComponent
import com.example.iot.ui.components.WaterSummaryCard
import com.example.iot.ui.theme.NavyBackground

@Composable
fun HomeScreen(
    pH: Double?,
    voltage: Double?,
    aeratorOn: Boolean,
    isAuto: Boolean,
    onToggleAerator: () -> Unit,
    onToggleAuto: (Boolean) -> Unit,
    temperature: Double?
) {
    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(NavyBackground, NavyBackground)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundGradient)
            .padding(16.dp)
    ) {
        HeaderComponent()
        Spacer(modifier = Modifier.height(16.dp))
        WaterSummaryCard(pH = pH, oxygenRaw = voltage, temperature = temperature)
        Spacer(modifier = Modifier.height(16.dp))

        AeratorControlWithLogic(
            pH = pH,
            isAutoMode = isAuto,
            isAeratorOn = aeratorOn,
            onToggleAutoMode = onToggleAuto,
            onToggleAerator = onToggleAerator
        )
    }
}