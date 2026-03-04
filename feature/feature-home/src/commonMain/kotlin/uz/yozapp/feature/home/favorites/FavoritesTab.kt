package uz.yozapp.feature.home.favorites

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.stringResource
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorStar
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextHint
import uz.yozapp.core.ui.theme.ColorTextPrimary
import uz.yozapp.core.ui.theme.ColorTextSecondary

// ── Main composable ───────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FavoritesTab(
    state: FavoritesState,
    onIntent: (FavoritesIntent) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorBgWhite)
    ) {
        FavoritesTopBar(onFilterClick = { onIntent(FavoritesIntent.FilterClicked) })

        if (state.filteredSalons.isEmpty()) {
            FavoritesEmptyState()
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.filteredSalons, key = { it.id }) { salon ->
                    FavoriteSalonCard(
                        salon = salon,
                        onRemove = { onIntent(FavoritesIntent.RemoveFavorite(salon.id)) }
                    )
                }
            }
        }
    }

    // Filter bottom sheet
    if (state.showFilterSheet) {
        ModalBottomSheet(
            onDismissRequest = { onIntent(FavoritesIntent.DismissFilter) },
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true),
            containerColor = ColorBgWhite
        ) {
            FilterSheetContent(
                selectedFilter = state.selectedFilter,
                onSelect = { onIntent(FavoritesIntent.SelectFilter(it)) }
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun FavoritesTopBar(onFilterClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 8.dp, top = 16.dp, bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(Res.string.favorites_title),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            color = ColorTextPrimary,
            modifier = Modifier.weight(1f)
        )
        IconButton(onClick = onFilterClick) {
            Icon(
                imageVector = Icons.Default.FilterList,
                contentDescription = null,
                tint = ColorTextPrimary,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

// ── Empty state ───────────────────────────────────────────────────────────────

@Composable
private fun FavoritesEmptyState() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.Default.FolderOpen,
                contentDescription = null,
                tint = ColorTextHint,
                modifier = Modifier.size(72.dp)
            )
            Text(
                text = stringResource(Res.string.favorites_empty),
                fontSize = 15.sp,
                color = ColorTextGray
            )
        }
    }
}

// ── Favorite salon card ───────────────────────────────────────────────────────

@Composable
private fun FavoriteSalonCard(
    salon: FavoriteSalon,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = ColorBgWhite)
    ) {
        Column {
            // Image area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .background(salon.imageBg)
            ) {
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Info
            Column(modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = salon.name,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = ColorTextPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${salon.rating}",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = ColorTextPrimary
                        )
                        Spacer(Modifier.width(2.dp))
                        Icon(
                            imageVector = Icons.Default.Star,
                            contentDescription = null,
                            tint = ColorStar,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }

                Spacer(Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = salon.address,
                        fontSize = 13.sp,
                        color = ColorTextSecondary,
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = stringResource(Res.string.reviews_format, salon.reviewCount),
                        fontSize = 13.sp,
                        color = ColorTextGray
                    )
                }
            }
        }
    }
}

// ── Filter bottom sheet content ───────────────────────────────────────────────

@Composable
private fun FilterSheetContent(
    selectedFilter: FavoriteFilter,
    onSelect: (FavoriteFilter) -> Unit
) {
    val allLabel     = stringResource(Res.string.favorites_filter_all)
    val beautyLabel  = stringResource(Res.string.favorites_filter_beauty)
    val clinicLabel  = stringResource(Res.string.favorites_filter_clinics)

    val items = listOf(
        FavoriteFilter.All     to allLabel,
        FavoriteFilter.Beauty  to beautyLabel,
        FavoriteFilter.Clinics to clinicLabel
    )

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp)
    ) {
        items.forEach { (filter, label) ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null
                    ) { onSelect(filter) }
                    .padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    fontSize = 16.sp,
                    fontWeight = if (filter == selectedFilter) FontWeight.SemiBold else FontWeight.Normal,
                    color = ColorTextPrimary,
                    modifier = Modifier.weight(1f)
                )
                if (filter == selectedFilter) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = uz.yozapp.core.ui.theme.ColorPrimary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
            if (filter != FavoriteFilter.Clinics) {
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    color = ColorDivider
                )
            }
        }
    }
}