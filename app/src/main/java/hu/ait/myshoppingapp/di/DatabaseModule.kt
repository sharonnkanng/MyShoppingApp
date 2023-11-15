package hu.ait.myshoppingapp.di

import hu.ait.myshoppingapp.data.ShoppingAppDatabase
import hu.ait.myshoppingapp.data.ShoppingDAO
import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DatabaseModule {
    @Provides
    fun provideTodoDao(appDatabase: ShoppingAppDatabase): ShoppingDAO {
        return appDatabase.shoppingDao()
    }

    @Provides
    @Singleton
    fun provideTodoAppDatabase(@ApplicationContext appContext: Context): ShoppingAppDatabase {
        return ShoppingAppDatabase.getDatabase(appContext)
    }

}