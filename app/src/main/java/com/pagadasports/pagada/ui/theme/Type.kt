package com.pagadasports.pagada.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.pagadasports.pagada.R

val MontserratFamily = FontFamily(
    Font(R.font.montserrat_thin, FontWeight.Thin, FontStyle.Normal),
    Font(R.font.montserrat_thinitalic, FontWeight.Thin, FontStyle.Italic),
    Font(R.font.montserrat_extralight, FontWeight.ExtraLight, FontStyle.Normal),
    Font(R.font.montserrat_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
    Font(R.font.montserrat_light, FontWeight.Light, FontStyle.Normal),
    Font(R.font.montserrat_lightitalic, FontWeight.Light, FontStyle.Italic),
    Font(R.font.montserrat_regular, FontWeight.Normal, FontStyle.Normal),
    Font(R.font.montserrat_italic, FontWeight.Normal, FontStyle.Italic),
    Font(R.font.montserrat_medium, FontWeight.Medium, FontStyle.Normal),
    Font(R.font.montserrat_mediumitalic, FontWeight.Medium, FontStyle.Italic),
    Font(R.font.montserrat_semibold, FontWeight.SemiBold, FontStyle.Normal),
    Font(R.font.montserrat_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
    Font(R.font.montserrat_bold, FontWeight.Bold, FontStyle.Normal),
    Font(R.font.montserrat_bolditalic, FontWeight.Bold, FontStyle.Italic),
    Font(R.font.montserrat_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
    Font(R.font.montserrat_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
    Font(R.font.montserrat_black, FontWeight.Black, FontStyle.Normal),
    Font(R.font.montserrat_blackitalic, FontWeight.Black, FontStyle.Italic)
)

// Set of Material typography styles to start with
val Typography = Typography(
    displayLarge = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Normal),
    displayMedium = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Normal),
    displaySmall = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Normal),
    headlineLarge = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Bold),
    headlineSmall = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = 0.sp
    ),
    titleMedium = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Medium),
    titleSmall = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
        letterSpacing = 0.5.sp
    ),
    bodyMedium = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Normal),
    bodySmall = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Normal),
    labelLarge = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontFamily = MontserratFamily, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(
        fontFamily = MontserratFamily,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp,
        letterSpacing = 0.5.sp
    )
)
