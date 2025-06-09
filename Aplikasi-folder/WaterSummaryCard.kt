package com.example.iot.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.iot.R
import com.example.iot.ui.theme.Grey1
import com.example.iot.ui.theme.Teal3

@Composable
fun WaterSummaryCard(pH: Double?, oxygenRaw: Double?, temperature: Double?) {
    val oxygenStatus = inferOxygenLevelFromPH(pH, temperature)
    val bgColor = oxygenLevelColor(pH, temperature).copy(alpha = 0.2f)
    val textColor = oxygenLevelColor(pH, temperature)


    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        // Oxygen Level Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(color = bgColor, shape = RoundedCornerShape(12.dp))
                .padding(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(id = when (oxygenStatus) {
                        "Low" -> R.drawable.low
                        "High" -> R.drawable.high
                        else -> R.drawable.good
                    }),
                    contentDescription = "Oxygen Status Icon",
                    modifier = Modifier.size(48.dp)
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Oxygen level is $oxygenStatus",
                        color = textColor,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = when (oxygenStatus) {
                            "Low" -> "Consider adding aeration"
                            "High" -> "Consider reducing aeration"
                            else -> "Everything is optimal"
                        },
                        color = Grey1,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            // pH Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .background(color = Teal3.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "pH Value",
                        color = Grey1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = pH?.toString() ?: "...",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }

            // Temperature Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(110.dp)
                    .background(color = Teal3.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp))
                    .padding(16.dp)
            ) {
                Column(
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Temperature",
                        color = Grey1,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = temperature?.let { String.format("%.1f°C", it) } ?: "...",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineSmall
                    )
                }
            }
        }
    }
}

fun inferOxygenLevelFromPH(pH: Double?, temperature: Double?): String = when {
    temperature != null && temperature > 29.0 -> "Low"
    pH == null -> "..."
    pH < 6.5 -> "Low"
    pH > 7.5 -> "High"
    else -> "Good"
}

fun oxygenLevelColor(pH: Double?, temperature: Double?): Color = when (inferOxygenLevelFromPH(pH, temperature)) {
    "Low" -> Color(0xFFF3A008)
    "High" -> Color(0xFFF4405F)
    "Good" -> Color(0xFF4BC849)
    else -> Color.Gray
}
