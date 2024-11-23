package com.ih.osm.core.di

import com.airbnb.mvrx.hilt.AssistedViewModelFactory
import com.airbnb.mvrx.hilt.MavericksViewModelComponent
import com.airbnb.mvrx.hilt.ViewModelKey
import com.ih.osm.ui.pages.account.AccountViewModel
import com.ih.osm.ui.pages.createcard.CreateCardViewModel
import com.ih.osm.ui.pages.home.HomeViewModel
import com.ih.osm.ui.pages.login.LoginViewModel
import com.ih.osm.ui.pages.password.RestoreAccountViewModel
import com.ih.osm.ui.pages.profile.ProfileViewModel
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.multibindings.IntoMap

@Module
@InstallIn(MavericksViewModelComponent::class)
interface ViewModelModule {

    @Binds
    @IntoMap
    @ViewModelKey(HomeViewModel::class)
    fun bindHomeViewModelV2(factory: HomeViewModel.Factory): AssistedViewModelFactory<*, *>


    @Binds
    @IntoMap
    @ViewModelKey(CreateCardViewModel::class)
    fun bindCreateCardViewModel(
        factory: CreateCardViewModel.Factory
    ): AssistedViewModelFactory<*, *>
//


    @Binds
    @IntoMap
    @ViewModelKey(RestoreAccountViewModel::class)
    fun bindRestoreAccountViewModel(
        factory: RestoreAccountViewModel.Factory
    ): AssistedViewModelFactory<*, *>
}
