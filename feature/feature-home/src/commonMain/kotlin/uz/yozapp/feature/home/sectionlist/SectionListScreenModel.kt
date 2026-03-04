package uz.yozapp.feature.home.sectionlist

import uz.yozapp.core.ui.mvi.MviScreenModel
import uz.yozapp.feature.home.home.highRatedSalons
import uz.yozapp.feature.home.home.nearbySalons
import uz.yozapp.feature.home.home.popularSalons

internal class SectionListScreenModel(
    title: String,
    sectionIndex: Int
) : MviScreenModel<SectionListState, SectionListIntent, SectionListEffect>(
    initialState = SectionListState(
        title = title,
        salons = when (sectionIndex) {
            0    -> nearbySalons
            1    -> popularSalons
            else -> highRatedSalons
        }
    )
) {

    override fun handleIntent(intent: SectionListIntent) {
        when (intent) {
            SectionListIntent.BackClicked   -> emitEffect(SectionListEffect.NavigateBack)
            SectionListIntent.SearchClicked -> { /* TODO */ }
        }
    }
}