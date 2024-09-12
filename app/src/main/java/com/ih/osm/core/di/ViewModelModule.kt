package com.ih.osm.core.di

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.MavericksViewModelComponent
import com.airbnb.mvrx.hilt.ViewModelKey
import com.ih.osm.ui.pages.account.AccountViewModel
import com.ih.osm.ui.pages.carddetail.CardDetailViewModel
import com.ih.osm.ui.pages.cardlist.CardListViewModel
import com.ih.osm.ui.pages.createcard.CreateCardViewModel
import com.ih.osm.ui.pages.home.HomeViewModelV2
import com.ih.osm.ui.pages.login.LoginViewModel
import com.ih.osm.ui.pages.password.RestoreAccountViewModel
import com.ih.osm.ui.pages.profile.ProfileViewModel
import com.ih.osm.ui.pages.solution.SolutionViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(LoginViewModel::class)
    fun bindLoginViewModel(factory: LoginViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(AccountViewModel::class)
    fun bindAccountViewModel(factory: AccountViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModelV2::class)
    fun bindHomeViewModelV2(factory: HomeViewModelV2.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(CardListViewModel::class)
    fun bindCardListViewModel(factory: CardListViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(CreateCardViewModel::class)
    fun bindCreateCardViewModel(factory: CreateCardViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(CardDetailViewModel::class)
    fun bindCardDetailViewModel(factory: CardDetailViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(SolutionViewModel::class)
    fun bindSolutionViewModel(factory: SolutionViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(ProfileViewModel::class)
    fun bindProfileViewModel(factory: ProfileViewModel.Factory): AssistedViewModelFactory<*, *>

    @Binds
    @IntoMap
    @ViewModelKey(RestoreAccountViewModel::class)
    fun bindRestoreAccountViewModel(factory: RestoreAccountViewModel.Factory): AssistedViewModelFactory<*, *>
}
