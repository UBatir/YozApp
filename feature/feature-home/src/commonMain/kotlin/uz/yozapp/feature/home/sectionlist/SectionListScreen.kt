package uz.yozapp.feature.home.sectionlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf
import uz.yozapp.core.ui.res.Res
import uz.yozapp.feature.home.home.SalonItem
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorStar
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextPrimary

// ── Screen ────────────────────────────────────────────────────────────────────

data class SectionListScreen(
    val title: String,
    val sectionIndex: Int
) : Screen {

    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val model = koinScreenModel<SectionListScreenModel> { parametersOf(title, sectionIndex) }
        val state by model.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    SectionListEffect.NavigateBack -> navigator.pop()
                }
            }
        }

        SectionListContent(state = state, onIntent = model::handleIntent)
    }
}

// ── Content ───────────────────────────────────────────────────────────────────

@Composable
private fun SectionListContent(state: SectionListState, onIntent: (SectionListIntent) -> Unit) {
    Column(modifier = Modifier.fillMaxSize().background(ColorBgWhite).statusBarsPadding()) {
        SectionListTopBar(
            title = state.title,
            onBack = { onIntent(SectionListIntent.BackClicked) },
            onSearch = { onIntent(SectionListIntent.SearchClicked) }
        )
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                horizontal = 20.dp,
                vertical = 16.dp
            )
        ) {
            items(state.salons) { salon ->
                SalonFullCard(salon)
            }
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun SectionListTopBar(title: String, onBack: () -> Unit, onSearch: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onBack) {
            Icon(
                Icons.AutoMirrored.Filled.ArrowBack,
                contentDescription = stringResource(Res.string.home_cd_back),
                tint = ColorTextPrimary
            )
        }
        Text(
            text = title,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextPrimary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onSearch) {
            Icon(
                Icons.Default.Search,
                contentDescription = stringResource(Res.string.home_cd_search),
                tint = ColorTextPrimary
            )
        }
    }
}

// ── Salon full card ───────────────────────────────────────────────────────────

@Composable
private fun SalonFullCard(salon: SalonItem) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = ColorBgWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider)
    ) {
        Column {
            Box {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(salon.imageBg)
                )
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(12.dp)
                        .size(36.dp)
                        .background(Color.White.copy(alpha = 0.9f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.FavoriteBorder,
                        contentDescription = stringResource(Res.string.home_cd_add_to_favorites),
                        tint = ColorTextGray,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = salon.name,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = ColorTextPrimary
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(text = salon.subtitle, fontSize = 13.sp, color = ColorTextGray)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${salon.rating}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Medium,
                        color = ColorTextPrimary
                    )
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = ColorStar,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }

            Text(
                text = stringResource(Res.string.reviews_format, salon.reviewCount),
                fontSize = 12.sp,
                color = ColorTextGray,
                modifier = Modifier
                    .padding(horizontal = 14.dp)
                    .padding(bottom = 12.dp)
            )
        }
    }
}