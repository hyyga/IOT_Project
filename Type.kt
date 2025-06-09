package com.example.iot.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.example.iot.R

val CalSans = FontFamily(
    Font(R.font.calsans_regular, FontWeight.Normal),
    Font(R.font.inter_medium, FontWeight.Medium),
    Font(R.font.inter_bold, FontWeight.Bold)
)

val Typography = Typography(
    headlineLarge = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Normal, fontSize = 32.sp),
    headlineMedium = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Normal, fontSize = 28.sp),
    headlineSmall = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Bold, fontSize = 26.sp),

    titleLarge = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Normal, fontSize = 23.sp),
    titleMedium = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Normal, fontSize = 20.sp),
    titleSmall = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Medium, fontSize = 14.sp),

    bodyLarge = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Medium, fontSize = 16.sp),
    bodyMedium = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    bodySmall = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Medium, fontSize = 12.sp),

    labelLarge = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Medium, fontSize = 14.sp),
    labelMedium = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Medium, fontSize = 12.sp),
    labelSmall = TextStyle(fontFamily = CalSans, fontWeight = FontWeight.Medium, fontSize = 11.sp)
)
