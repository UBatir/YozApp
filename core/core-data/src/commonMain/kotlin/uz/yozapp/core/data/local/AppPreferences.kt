package uz.yozapp.core.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class AppPreferences(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val KEY_AUTH_TOKEN = stringPreferencesKey("auth_token")
        private val KEY_USER_NAME  = stringPreferencesKey("user_name")
        private val KEY_USER_PHONE = stringPreferencesKey("user_phone")
        private val KEY_IS_GUEST   = booleanPreferencesKey("is_guest")
    }

    val authToken: Flow<String?> = dataStore.data.map { it[KEY_AUTH_TOKEN] }
    val userName:  Flow<String?> = dataStore.data.map { it[KEY_USER_NAME] }
    val userPhone: Flow<String?> = dataStore.data.map { it[KEY_USER_PHONE] }
    val isGuest:   Flow<Boolean> = dataStore.data.map { it[KEY_IS_GUEST] ?: true }

    suspend fun saveAuthToken(token: String) {
        dataStore.edit { it[KEY_AUTH_TOKEN] = token }
    }

    suspend fun saveUser(name: String, phone: String) {
        dataStore.edit { prefs ->
            prefs[KEY_USER_NAME]  = name
            prefs[KEY_USER_PHONE] = phone
            prefs[KEY_IS_GUEST]   = false
        }
    }

    suspend fun continueAsGuest() {
        dataStore.edit { it[KEY_IS_GUEST] = true }
    }

    suspend fun logout() {
        dataStore.edit { prefs ->
            prefs.remove(KEY_USER_NAME)
            prefs.remove(KEY_USER_PHONE)
            prefs.remove(KEY_AUTH_TOKEN)
            prefs[KEY_IS_GUEST] = true
        }
    }
}