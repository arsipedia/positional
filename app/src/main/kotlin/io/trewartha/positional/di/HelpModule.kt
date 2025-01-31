package io.trewartha.positional.di

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.FragmentComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import io.noties.markwon.Markwon

@Module
@InstallIn(FragmentComponent::class)
class HelpModule {

    @Provides
    fun markwon(@ApplicationContext context: Context): Markwon = Markwon.create(context)
}
