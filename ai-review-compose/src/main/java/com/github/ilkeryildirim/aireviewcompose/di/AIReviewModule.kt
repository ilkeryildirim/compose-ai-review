package com.github.ilkeryildirim.aireviewcompose.di

import android.content.Context
import com.github.ilkeryildirim.aireviewcompose.core.analyzers.ComprehensiveAnalyzer
import com.github.ilkeryildirim.aireviewcompose.core.providers.DomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.core.providers.DefaultDomainInsightProvider
import com.github.ilkeryildirim.aireviewcompose.core.ProviderFactory
import com.github.ilkeryildirim.aireviewcompose.providers.openai.OpenAIProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Simple Hilt module for AI Review Compose dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object AIReviewModule {
    
    @Provides
    @Singleton
    fun provideProviderFactory(
        @ApplicationContext context: Context
    ): ProviderFactory {
        return ProviderFactory(context)
    }
    
    @Provides
    @Singleton
    fun provideDefaultDomainInsightProvider(): DomainInsightProvider {
        return DefaultDomainInsightProvider()
    }
    
    
} 