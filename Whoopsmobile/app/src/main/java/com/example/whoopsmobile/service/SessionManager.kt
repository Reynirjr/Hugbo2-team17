package com.example.whoopsmobile.service

import android.content.Context
import android.content.SharedPreferences

/**
 * US6: Stores current user phone for order association.
 * Also stores selected menu_id when user picks a restaurant.
 */
object SessionManager {

    private const val PREFS_NAME = "whoops_session"
    private const val KEY_PHONE = "customer_phone"
    private const val KEY_MENU_ID = "current_menu_id"
    private const val KEY_QUEUE_MINUTES = "restaurant_queue_minutes"

    private var prefs: SharedPreferences? = null

    fun init(context: Context) {
        if (prefs == null) prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    private fun requirePrefs(): SharedPreferences = prefs ?: error("SessionManager not initialized. Call init(context) in Application or MainActivity.")

    var customerPhone: String?
        get() = requirePrefs().getString(KEY_PHONE, null)
        set(value) = requirePrefs().edit().putString(KEY_PHONE, value).apply()

    var currentMenuId: Long
        get() = requirePrefs().getLong(KEY_MENU_ID, 1L)
        set(value) = requirePrefs().edit().putLong(KEY_MENU_ID, value).apply()

    /** Restaurant/store queue minutes (from store_settings). Used for order estimated_ready_at and wait time display. */
    var restaurantQueueMinutes: Int?
        get() = requirePrefs().getInt(KEY_QUEUE_MINUTES, -1).takeIf { it >= 0 }
        set(value) = requirePrefs().edit().putInt(KEY_QUEUE_MINUTES, value ?: -1).apply()

    fun isLoggedIn(): Boolean = !customerPhone.isNullOrBlank()

    fun clear() {
        requirePrefs().edit().clear().apply()
    }
}
