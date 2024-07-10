package com.ih.m2.core.di

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.MavericksViewModelComponent
import com.airbnb.mvrx.hilt.ViewModelKey
import com.ih.m2.ui.pages.account.AccountViewModel
import com.ih.m2.ui.pages.carddetail.CardDetailViewModel
import com.ih.m2.ui.pages.cardlist.CardListViewModel
import com.ih.m2.ui.pages.createcard.CreateCardViewModel
import com.ih.m2.ui.pages.home.HomeViewModel
import com.ih.m2.ui.pages.home.HomeViewModelV2
import com.ih.m2.ui.pages.login.LoginViewModel
import com.ih.m2.ui.pages.solution.SolutionViewModel
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
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModel(factory: HomeViewModel.Factory): AssistedViewModelFactory<*, *>

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
}