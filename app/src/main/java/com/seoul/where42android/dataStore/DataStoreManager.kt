//package com.seoul.where42android.dataStore
//
//import android.content.Context
//import androidx.datastore.preferences.core.Preferences
//import androidx.datastore.preferences.core.booleanPreferencesKey
//import androidx.datastore.preferences.core.edit
//import androidx.datastore.preferences.core.intPreferencesKey
//import androidx.datastore.preferences.core.stringPreferencesKey
//import androidx.datastore.preferences.preferencesDataStore
//import kotlinx.coroutines.flow.first
//import kotlinx.coroutines.flow.map
////object DataStoreKeys {
////    val ACCESS_TOKEN = stringPreferencesKey("accesstoken")
////    val INTRA_ID = intPreferencesKey("intraId")
////    val AGREEMENT = booleanPreferencesKey("agreement")
////}
//object DataStoreManager {
//    private val Context.dataStoreInstance by preferencesDataStore(name = "user_preferences")
//
//    private val ACCESS_TOKEN = stringPreferencesKey("accesstoken")
//    private val INTRA_ID = intPreferencesKey("intraId")
//    private val AGREEMENT = booleanPreferencesKey("agreement")
//
//    private fun getDataStore(context: Context) = context.dataStoreInstance
//
//    suspend fun saveData(context: Preferences.Key<String>, key: String, value: Any) {
//        getDataStore(context).edit { preferences ->
//            when (value) {
//                is String -> preferences[key as Preferences.Key<String>] = value
//                is Int -> preferences[key as Preferences.Key<Int>] = value
//                is Boolean -> preferences[key as Preferences.Key<Boolean>] = value
//                else -> throw IllegalArgumentException("Unsupported data type")
//            }
//        }
//    }
//
//    suspend fun <T> getData(context: Context, key: Preferences.Key<T>): T? {
//        return getDataStore(context).data.map { preferences -> preferences[key] }.first()
//    }
//
//    suspend fun clearData(context: Context) {
//        getDataStore(context).edit { it.clear() }
//    }
//}
//
