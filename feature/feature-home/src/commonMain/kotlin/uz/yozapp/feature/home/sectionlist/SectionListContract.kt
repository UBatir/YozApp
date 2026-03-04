package uz.yozapp.feature.home.sectionlist

import uz.yozapp.feature.home.home.SalonItem

// ── MVI Contract ──────────────────────────────────────────────────────────────

internal data class SectionListState(
    val title: String,
    val salons: List<SalonItem>
)

internal sealed interface SectionListIntent {
    data object BackClicked : SectionListIntent
    data object SearchClicked : SectionListIntent
}

internal sealed interface SectionListEffect {
    data object NavigateBack : SectionListEffect
}