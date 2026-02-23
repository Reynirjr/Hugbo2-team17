package com.example.whoopsmobile

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.whoopsmobile.service.SessionManager
import com.example.whoopsmobile.ui.basket.BasketFragment
import com.example.whoopsmobile.ui.checkout.CheckoutFragment
import com.example.whoopsmobile.ui.itemdetails.ItemDetailsFragment
import com.example.whoopsmobile.ui.menu.MenuFragment
import com.example.whoopsmobile.ui.phone.PhoneFragment
import com.example.whoopsmobile.ui.restaurantlist.RestaurantListFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        SessionManager.init(this)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if (savedInstanceState == null) {
            val startFragment = if (SessionManager.isLoggedIn()) RestaurantListFragment() else PhoneFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, startFragment)
                .commit()
        }
    }

    fun openRestaurantList() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, RestaurantListFragment())
            .commit()
    }

    /** After placing order: go to restaurant list and clear back stack. */
    fun openRestaurantListClearBackStack() {
        supportFragmentManager.popBackStack(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, RestaurantListFragment())
            .commit()
    }

    fun openMenu(menuId: Long) {
        SessionManager.currentMenuId = menuId
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, MenuFragment())
            .addToBackStack(null)
            .commit()
    }

    fun openBasket() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, BasketFragment())
            .addToBackStack(null)
            .commit()
    }

    /** Opens basket after adding from item details; pops item details so back from basket goes to menu. */
    fun openBasketFromItemDetails() {
        supportFragmentManager.popBackStack()
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, BasketFragment())
            .addToBackStack(null)
            .commit()
    }

    fun openItemDetails(itemId: Int) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, ItemDetailsFragment.newInstance(itemId))
            .addToBackStack(null)
            .commit()
    }

    fun openCheckout() {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, CheckoutFragment())
            .addToBackStack(null)
            .commit()
    }
}
