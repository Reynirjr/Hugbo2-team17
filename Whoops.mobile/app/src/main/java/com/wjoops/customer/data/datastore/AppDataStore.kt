package com.wjoops.customer.data.datastore

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.wjoops.customer.domain.models.AuthState
import com.wjoops.customer.domain.models.Basket
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

private val Context.dataStore by preferencesDataStore(name = "wjoops_prefs")

interface AuthStore {
    val authState: Flow<AuthState>
    suspend fun updateAuth(state: AuthState)
    suspend fun logout()
}

interface BasketSnapshotStore {
    suspend fun storeBasketSnapshot(basket: Basket)
}

class AppDataStore(
    private val appContext: Context,
    private val json: Json,
) : AuthStore, BasketSnapshotStore {
    private object Keys {
        val PHONE = stringPreferencesKey("phone")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val ACCESS_TOKEN = stringPreferencesKey("access_token")
        val REFRESH_TOKEN = stringPreferencesKey("refresh_token")
        val USER_ID = stringPreferencesKey("user_id")
        val LAST_BASKET_JSON = stringPreferencesKey("last_basket_json")
        val DEBUG_BASE_URL = stringPreferencesKey("debug_base_url")
    }

    override val authState: Flow<AuthState> = appContext.dataStore.data.map { prefs ->
        AuthState(
            phone = prefs[Keys.PHONE],
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            accessToken = prefs[Keys.ACCESS_TOKEN],
            refreshToken = prefs[Keys.REFRESH_TOKEN],
            userId = prefs[Keys.USER_ID],
        )
    }

    val lastBasketJson: Flow<String?> = appContext.dataStore.data.map { it[Keys.LAST_BASKET_JSON] }

    val debugBaseUrl: Flow<String?> = appContext.dataStore.data.map { it[Keys.DEBUG_BASE_URL] }

    suspend fun setDebugBaseUrl(baseUrl: String?) {
        appContext.dataStore.edit { prefs ->
            if (baseUrl.isNullOrBlank()) prefs.remove(Keys.DEBUG_BASE_URL) else prefs[Keys.DEBUG_BASE_URL] = baseUrl
        }
    }

    override suspend fun updateAuth(state: AuthState) {
        appContext.dataStore.edit { prefs ->
            state.phone?.let { prefs[Keys.PHONE] = it } ?: prefs.remove(Keys.PHONE)
            prefs[Keys.IS_LOGGED_IN] = state.isLoggedIn
            state.accessToken?.let { prefs[Keys.ACCESS_TOKEN] = it } ?: prefs.remove(Keys.ACCESS_TOKEN)
            state.refreshToken?.let { prefs[Keys.REFRESH_TOKEN] = it } ?: prefs.remove(Keys.REFRESH_TOKEN)
            state.userId?.let { prefs[Keys.USER_ID] = it } ?: prefs.remove(Keys.USER_ID)
        }
    }

    override suspend fun logout() {
        appContext.dataStore.edit { prefs ->
            prefs.remove(Keys.ACCESS_TOKEN)
            prefs.remove(Keys.REFRESH_TOKEN)
            prefs.remove(Keys.USER_ID)
            prefs[Keys.IS_LOGGED_IN] = false
        }
    }

    override suspend fun storeBasketSnapshot(basket: Basket) {
        val snapshot = json.encodeToString(basket)
        appContext.dataStore.edit { prefs ->
            prefs[Keys.LAST_BASKET_JSON] = snapshot
        }
    }
}

