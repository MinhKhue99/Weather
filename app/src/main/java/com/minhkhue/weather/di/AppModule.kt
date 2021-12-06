package com.minhkhue.weather.di

import com.minhkhue.weather.BuildConfig
import com.minhkhue.weather.api.ApiHelper
import com.minhkhue.weather.api.ApiHelperImpl
import com.minhkhue.weather.api.ApiService
import com.minhkhue.weather.utils.Constants
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
	
	@Provides
	fun provideUrl() = Constants.BASE_URL
	
	@Provides
	@Singleton
	fun provideOkHttpClient() = if (BuildConfig.DEBUG) {
		val loggingInterceptor = HttpLoggingInterceptor()
		loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
		OkHttpClient.Builder()
			.addInterceptor(loggingInterceptor)
			.build()
	} else OkHttpClient
		.Builder()
		.build()
	
	@Provides
	@Singleton
	fun provideRetrofit(okHttpClient: OkHttpClient, url: String): Retrofit =
		Retrofit.Builder()
			.addConverterFactory(GsonConverterFactory.create())
			.baseUrl(url)
			.client(okHttpClient)
			.build()
	
	@Provides
	@Singleton
	fun provideApiService(retrofit: Retrofit): ApiService = retrofit.create(ApiService::class.java)
	
	@Provides
	@Singleton
	fun provideAPIHelper(apiHelper: ApiHelperImpl): ApiHelper = apiHelper
}