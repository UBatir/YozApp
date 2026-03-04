package uz.yozapp.feature.home.home

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import org.jetbrains.compose.resources.stringResource
import uz.yozapp.feature.home.bookings.BookingsScreenModel
import uz.yozapp.feature.home.bookings.BookingsTab
import uz.yozapp.feature.home.favorites.FavoritesScreenModel
import uz.yozapp.feature.home.favorites.FavoritesTab
import uz.yozapp.feature.home.location.LocationScreen
import uz.yozapp.feature.home.notifications.NotificationsScreen
import uz.yozapp.feature.home.profile.ProfileScreen
import uz.yozapp.feature.home.sectionlist.SectionListScreen
import uz.yozapp.core.ui.res.Res
import uz.yozapp.core.ui.res.*
import uz.yozapp.core.ui.theme.ColorBgLight
import uz.yozapp.core.ui.theme.ColorBgWhite
import uz.yozapp.core.ui.theme.ColorDivider
import uz.yozapp.core.ui.theme.ColorError
import uz.yozapp.core.ui.theme.ColorPrimary
import uz.yozapp.core.ui.theme.ColorSalonBg1
import uz.yozapp.core.ui.theme.ColorSalonBg2
import uz.yozapp.core.ui.theme.ColorSalonBg3
import uz.yozapp.core.ui.theme.ColorSalonBg4
import uz.yozapp.core.ui.theme.ColorSalonBg5
import uz.yozapp.core.ui.theme.ColorSalonBg6
import uz.yozapp.core.ui.theme.ColorStar
import uz.yozapp.core.ui.theme.ColorTextGray
import uz.yozapp.core.ui.theme.ColorTextPrimary

// ── Data models ───────────────────────────────────────────────────────────────

internal data class SalonItem(
    val id: Int,
    val name: String,
    val subtitle: String,
    val rating: Double,
    val reviewCount: Int,
    val imageBg: Color = ColorSalonBg1
)

internal data class CategoryData(val label: String, val isAdd: Boolean = false)

// ── Mock data ─────────────────────────────────────────────────────────────────

internal val mockCategories = listOf(
    CategoryData("Клиники",    isAdd = true),
    CategoryData("Салоны кр..."),
    CategoryData("Косметол..."),
    CategoryData("Массаж"),
    CategoryData("Барбершо...")
)

internal val nearbySalons = listOf(
    SalonItem(1, "Sadoqat Beauty Bar", "0.5 км от вас",  4.7, 7, ColorSalonBg1),
    SalonItem(2, "Noor Studio",        "2 км от вас",    4.7, 7, ColorSalonBg2)
)

internal val popularSalons = listOf(
    SalonItem(3, "Lola Beauty",  "ул. Нукус, 74",    4.7, 7, ColorSalonBg3),
    SalonItem(4, "Shine Room",   "ул. Дархан, 12А",  4.7, 7, ColorSalonBg4)
)

internal val highRatedSalons = listOf(
    SalonItem(5, "Bloom Beauty Lab", "ул. Абдуллы Кадыри, 45",     5.0, 7, ColorSalonBg5),
    SalonItem(6, "Nafis Saloni",     "проспект Амира Темура, 12",  4.9, 7, ColorSalonBg6)
)

// ── Screen ────────────────────────────────────────────────────────────────────

class HomeScreen : Screen {

    @Composable
    override fun Content() {
        val navigator      = LocalNavigator.currentOrThrow
        val model          = koinScreenModel<HomeScreenModel>()
        val bookingsModel   = koinScreenModel<BookingsScreenModel>()
        val favoritesModel  = koinScreenModel<FavoritesScreenModel>()
        val state           by model.state.collectAsState()
        val bookingsState   by bookingsModel.state.collectAsState()
        val favoritesState  by favoritesModel.state.collectAsState()

        LaunchedEffect(Unit) {
            model.effect.collect { effect ->
                when (effect) {
                    HomeEffect.NavigateToNotifications -> navigator.push(NotificationsScreen())
                    HomeEffect.NavigateToProfile       -> navigator.push(ProfileScreen())
                    is HomeEffect.NavigateToSectionList -> navigator.push(
                        SectionListScreen(effect.title, effect.sectionIndex)
                    )
                }
            }
        }

        Scaffold(
            containerColor = ColorBgWhite,
            bottomBar = {
                HomeBottomBar(
                    selectedIndex = state.selectedTabIndex,
                    onSelect = { model.handleIntent(HomeIntent.SelectTab(it)) }
                )
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (state.selectedTabIndex) {
                    0 -> MainHomeTab(state = state, onIntent = model::handleIntent)
                    1 -> BookingsTab(
                        state = bookingsState,
                        onIntent = bookingsModel::handleIntent,
                        onAddressClick = { address -> navigator.push(LocationScreen(address)) }
                    )
                    2 -> FavoritesTab(state = favoritesState, onIntent = favoritesModel::handleIntent)
                }
            }
        }
    }
}

// ── Main tab ──────────────────────────────────────────────────────────────────

@Composable
private fun MainHomeTab(state: HomeState, onIntent: (HomeIntent) -> Unit) {
    val nearbyLabel    = stringResource(Res.string.home_section_nearby)
    val popularLabel   = stringResource(Res.string.home_section_popular)
    val highRatedLabel = stringResource(Res.string.home_section_high_rated)

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(ColorBgWhite),
        contentPadding = PaddingValues(bottom = 16.dp)
    ) {
        item {
            HomeTopBar(
                userName = state.userName,
                hasNotification = state.hasNotification,
                onNotificationsClick = { onIntent(HomeIntent.NotificationsClicked) },
                onProfileClick = { onIntent(HomeIntent.ProfileClicked) }
            )
        }
        item {
            CategoryRow(
                selectedIndex = state.selectedCategoryIndex,
                onSelect = { onIntent(HomeIntent.SelectCategory(it)) }
            )
        }
        item { Spacer(Modifier.height(8.dp)) }
        item {
            SectionBlock(
                title = nearbyLabel,
                salons = nearbySalons,
                onSeeAll = { onIntent(HomeIntent.SeeAllClicked(nearbyLabel, 0)) }
            )
        }
        item {
            SectionBlock(
                title = popularLabel,
                salons = popularSalons,
                onSeeAll = { onIntent(HomeIntent.SeeAllClicked(popularLabel, 1)) }
            )
        }
        item {
            SectionBlock(
                title = highRatedLabel,
                salons = highRatedSalons,
                onSeeAll = { onIntent(HomeIntent.SeeAllClicked(highRatedLabel, 2)) }
            )
        }
    }
}

// ── Top bar ───────────────────────────────────────────────────────────────────

@Composable
private fun HomeTopBar(
    userName: String,
    hasNotification: Boolean,
    onNotificationsClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(42.dp)
                .background(ColorPrimary, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onProfileClick
                ),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = userName.firstOrNull()?.uppercase() ?: "U",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(Modifier.width(12.dp))

        Text(
            text = userName,
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = ColorTextPrimary
        )

        Spacer(Modifier.weight(1f))

        Box(
            modifier = Modifier
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null,
                    onClick = onNotificationsClick
                )
                .padding(4.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = stringResource(Res.string.home_cd_notifications),
                tint = ColorTextPrimary,
                modifier = Modifier.size(26.dp)
            )
            if (hasNotification) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(ColorError, CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
        }
    }
}

// ── Category row ──────────────────────────────────────────────────────────────

@Composable
private fun CategoryRow(selectedIndex: Int, onSelect: (Int) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        itemsIndexed(mockCategories) { index, cat ->
            CategoryItem(
                category = cat,
                selected = index == selectedIndex,
                onClick = { onSelect(index) }
            )
        }
    }
}

@Composable
private fun CategoryItem(category: CategoryData, selected: Boolean, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null,
            onClick = onClick
        )
    ) {
        Box(
            modifier = Modifier
                .size(60.dp)
                .background(if (selected) ColorPrimary else ColorBgLight, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (category.isAdd) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = null,
                    tint = if (selected) Color.White else ColorTextGray,
                    modifier = Modifier.size(22.dp)
                )
            } else {
                CategoryIconPlaceholder(selected)
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            text = category.label,
            fontSize = 11.sp,
            color = if (selected) ColorPrimary else ColorTextGray,
            maxLines = 1
        )
    }
}

@Composable
private fun CategoryIconPlaceholder(selected: Boolean) {
    val color = if (selected) Color.White else ColorTextGray
    Box(modifier = Modifier.size(22.dp)) {
        Box(
            Modifier
                .size(16.dp, 3.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
                .align(Alignment.Center)
        )
        Box(
            Modifier
                .size(3.dp, 16.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(color)
                .align(Alignment.Center)
        )
    }
}

// ── Section block ─────────────────────────────────────────────────────────────

@Composable
private fun SectionBlock(title: String, salons: List<SalonItem>, onSeeAll: () -> Unit) {
    Column(modifier = Modifier.padding(top = 8.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(title, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = ColorTextPrimary)
            Text(
                text = stringResource(Res.string.see_all),
                fontSize = 14.sp,
                color = ColorPrimary,
                modifier = Modifier.clickable(onClick = onSeeAll)
            )
        }

        val rows = salons.chunked(2)
        Column(
            modifier = Modifier.padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            rows.forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    row.forEach { salon ->
                        SalonCard(salon = salon, modifier = Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

// ── Salon card (2-col grid) ───────────────────────────────────────────────────

@Composable
private fun SalonCard(salon: SalonItem, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        colors = CardDefaults.cardColors(containerColor = ColorBgWhite),
        border = androidx.compose.foundation.BorderStroke(1.dp, ColorDivider)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(130.dp)
                    .background(salon.imageBg)
            )
            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)) {
                Text(
                    text = salon.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = ColorTextPrimary,
                    maxLines = 1
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = salon.subtitle,
                    fontSize = 11.sp,
                    color = ColorTextGray,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "${salon.rating}",
                        fontSize = 11.sp,
                        color = ColorTextPrimary,
                        fontWeight = FontWeight.Medium
                    )
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        tint = ColorStar,
                        modifier = Modifier.size(12.dp).padding(start = 2.dp)
                    )
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = stringResource(Res.string.reviews_format, salon.reviewCount),
                        fontSize = 11.sp,
                        color = ColorTextGray
                    )
                }
            }
        }
    }
}

// ── Bottom navigation ─────────────────────────────────────────────────────────

private data class NavItem(val label: String, val icon: ImageVector, val iconSelected: ImageVector)

@Composable
private fun HomeBottomBar(selectedIndex: Int, onSelect: (Int) -> Unit) {
    val navItems = listOf(
        NavItem(stringResource(Res.string.home_tab_main),      Icons.Default.Home,          Icons.Default.Home),
        NavItem(stringResource(Res.string.home_tab_bookings),  Icons.Default.FavoriteBorder, Icons.Default.FavoriteBorder),
        NavItem(stringResource(Res.string.home_tab_favorites), Icons.Default.Favorite,      Icons.Default.Favorite)
    )

    Column {
        HorizontalDivider(color = ColorDivider, thickness = 1.dp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(ColorBgWhite)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            navItems.forEachIndexed { index, item ->
                val selected = index == selectedIndex
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null
                        ) { onSelect(index) }
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = if (selected) item.iconSelected else item.icon,
                        contentDescription = item.label,
                        tint = if (selected) ColorPrimary else ColorTextGray,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = item.label,
                        fontSize = 11.sp,
                        color = if (selected) ColorPrimary else ColorTextGray,
                        fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal
                    )
                    Spacer(Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(if (selected) 20.dp else 0.dp)
                            .background(ColorPrimary, CircleShape)
                    )
                }
            }
        }
    }
}

// ── Placeholder tabs ──────────────────────────────────────────────────────────

@Composable
private fun PlaceholderTab(title: String) {
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(title, fontSize = 18.sp, color = ColorTextGray)
    }
}