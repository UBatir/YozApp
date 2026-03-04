package uz.yozapp.core.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

// ── LargeTitle — 46sp ─────────────────────────────────────────────────────────
val TextLargeTitle         = TextStyle(fontSize = 46.sp, fontWeight = FontWeight.Normal,   lineHeight = 52.sp)
val TextLargeTitleBold     = TextStyle(fontSize = 46.sp, fontWeight = FontWeight.Bold,     lineHeight = 52.sp)

// ── LargeTitle-2 — 34sp ───────────────────────────────────────────────────────
val TextLargeTitle2        = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Normal,   lineHeight = 40.sp)
val TextLargeTitle2Bold    = TextStyle(fontSize = 34.sp, fontWeight = FontWeight.Bold,     lineHeight = 40.sp)

// ── Title-1 — 28sp ────────────────────────────────────────────────────────────
val TextTitle1             = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Normal,   lineHeight = 34.sp)
val TextTitle1Bold         = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold,     lineHeight = 34.sp)

// ── Title-2 — 24sp ────────────────────────────────────────────────────────────
val TextTitle2             = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Normal,   lineHeight = 30.sp)
val TextTitle2Bold         = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Bold,     lineHeight = 30.sp)

// ── Title-3 — 20sp ────────────────────────────────────────────────────────────
val TextTitle3             = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Normal,   lineHeight = 26.sp)
val TextTitle3SemiBold     = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold, lineHeight = 26.sp)

// ── Body — 17sp ───────────────────────────────────────────────────────────────
val TextBody               = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Normal,   lineHeight = 24.sp)
val TextBodyMedium         = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.Medium,   lineHeight = 24.sp)

// ── Body-2 — 15sp ─────────────────────────────────────────────────────────────
val TextBody2              = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Normal,   lineHeight = 22.sp)
val TextBody2Medium        = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.Medium,   lineHeight = 22.sp)

// ── Button ────────────────────────────────────────────────────────────────────
val TextButtonLarge        = TextStyle(fontSize = 17.sp, fontWeight = FontWeight.SemiBold, lineHeight = 22.sp)
val TextButtonMedium       = TextStyle(fontSize = 15.sp, fontWeight = FontWeight.SemiBold, lineHeight = 20.sp)
val TextButtonSmall        = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 18.sp)

// ── Caption-1 — 13sp ──────────────────────────────────────────────────────────
val TextCaption1           = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Normal,   lineHeight = 18.sp)
val TextCaption1Medium     = TextStyle(fontSize = 13.sp, fontWeight = FontWeight.Medium,   lineHeight = 18.sp)

// ── Caption-2 — 11sp ──────────────────────────────────────────────────────────
val TextCaption2           = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Normal,   lineHeight = 16.sp)
val TextCaption2Medium     = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Medium,   lineHeight = 16.sp)
val TextCaption2Bold       = TextStyle(fontSize = 11.sp, fontWeight = FontWeight.Bold,     lineHeight = 16.sp)

// ── MaterialTheme Typography mapping ──────────────────────────────────────────
val Typography = Typography(
    displayLarge  = TextLargeTitle2Bold,
    displayMedium = TextTitle1Bold,
    displaySmall  = TextTitle2Bold,
    headlineLarge = TextTitle1Bold,
    headlineMedium = TextTitle2Bold,
    headlineSmall = TextTitle3SemiBold,
    titleLarge    = TextTitle3SemiBold,
    titleMedium   = TextBodyMedium,
    titleSmall    = TextBody2Medium,
    bodyLarge     = TextBody,
    bodyMedium    = TextBody2,
    bodySmall     = TextCaption1,
    labelLarge    = TextButtonLarge,
    labelMedium   = TextButtonMedium,
    labelSmall    = TextCaption2Medium,
)