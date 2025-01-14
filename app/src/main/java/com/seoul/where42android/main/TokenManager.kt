package com.seoul.where42android.utils

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

object TokenManager {
    // DataStore 선언
    private val Context.dataStoreInstance by preferencesDataStore(name = "user_preferences")
    private lateinit var appContext: Context

    // Preference Keys
    private val ACCESS_TOKEN = stringPreferencesKey("accesstoken")
    private val INTRA_ID = intPreferencesKey("intraId")
    private val AGREEMENT = booleanPreferencesKey("agreement")

    // AccessToken Flow
    private val accessTokenFlow = MutableStateFlow("notoken")

    /**
     * TokenManager 초기화 - Application Context 설정
     */
    fun initialize(context: Context) {
        appContext = context.applicationContext

        // 초기화 시 DataStore에서 AccessToken 로드
        runBlocking {
            val storedToken = getData(ACCESS_TOKEN) ?: "notoken"
            accessTokenFlow.value = storedToken
        }
    }

    /**
     * 현재 AccessToken 반환
     */
     fun getAccessToken(): String {
        return accessTokenFlow.value
    }

    /**
     * AccessToken 설정 및 저장
     */
    suspend fun setAccessToken(newToken: String) {
        accessTokenFlow.value = newToken
        appContext.dataStoreInstance.edit { preferences ->
            preferences[ACCESS_TOKEN] = newToken
        }
    }

    /**
     * IntraId 설정 및 DataStore 저장
     */
    suspend fun setIntraId(newIntraId: Int) {
        appContext.dataStoreInstance.edit { preferences ->
            preferences[INTRA_ID] = newIntraId
        }
    }

    /**
     * Agreement 설정 및 DataStore 저장
     */
    suspend fun setAgreement(newAgreement: Boolean) {
        appContext.dataStoreInstance.edit { preferences ->
            preferences[AGREEMENT] = newAgreement
        }
    }

    /**
     * DataStore에서 AccessToken 가져오기
     */
    suspend fun getAccessTokenFromDataStore(): String? {
        val preferences = appContext.dataStoreInstance.data.firstOrNull()
        return preferences?.get(ACCESS_TOKEN)
    }

    /**
     * DataStore에서 IntraId 가져오기
     */
    suspend fun getIntraId(): Int? {
        val preferences = appContext.dataStoreInstance.data.firstOrNull()
        return preferences?.get(INTRA_ID)
    }

    /**
     * DataStore에서 Agreement 가져오기
     */
    suspend fun getAgreement(): Boolean? {
        val preferences = appContext.dataStoreInstance.data.firstOrNull()
        return preferences?.get(AGREEMENT)
    }


    /**
     * AccessToken Flow 관찰
     */
    fun observeAccessToken(): MutableStateFlow<String> {
        return accessTokenFlow
    }

    /**
     * AccessToken 초기화 (로그아웃 시 호출)
     */
    suspend fun clearAccessToken() {
        accessTokenFlow.value = "notoken"
        saveData(ACCESS_TOKEN, "notoken")
    }

    /**
     * DataStore에 데이터 저장
     */
    private suspend fun <T> saveData(key: Preferences.Key<T>, value: T) {
        appContext.dataStoreInstance.edit { preferences ->
            preferences[key] = value
        }
    }


    /**
     * DataStore에서 데이터 읽기
     */
    private suspend fun <T> getData(key: Preferences.Key<T>): T? {
        return appContext.dataStoreInstance.data.map { preferences -> preferences[key] }.first()
    }

    /**
     * DataStore에서 모든 데이터 삭제
     */
    suspend fun clearAllData() {
        appContext.dataStoreInstance.edit { it.clear() }
    }

    suspend fun printAllData() {
        val allData = appContext.dataStoreInstance.data.firstOrNull()
        if (allData != null) {
            val accessToken = allData[ACCESS_TOKEN] ?: "notoken"
            val intraId = allData[INTRA_ID] ?: "Not Set"
            val agreement = allData[AGREEMENT] ?: "Not Set"

//            println("AccessToken: $accessToken")
//            println("IntraId: $intraId")
//            println("Agreement: $agreement")
        } else {
            println("DataStore is empty.")
        }
    }
}

