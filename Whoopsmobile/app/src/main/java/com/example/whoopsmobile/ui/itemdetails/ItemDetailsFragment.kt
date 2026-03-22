package com.example.whoopsmobile.ui.itemdetails

import android.graphics.Typeface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
import com.example.whoopsmobile.data.HagaMenuData
import com.example.whoopsmobile.data.api.ApiHelper
import com.example.whoopsmobile.model.Ingredient
import com.example.whoopsmobile.service.BasketService

class ItemDetailsFragment : Fragment() {

    private var itemId: Int = 0
    private var quantity: Int = 1

    private lateinit var tvItemName: TextView
    private lateinit var tvItemDescription: TextView
    private lateinit var tvItemPrice: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var btnDecrease: ImageButton
    private lateinit var btnIncrease: ImageButton
    private lateinit var btnAddToBasket: Button
    private lateinit var btnBreyta: Button
    private lateinit var ingredientsPanel: LinearLayout

    private var allIngredients: List<Ingredient> = emptyList()
    private var defaultIngredientIds: List<Int> = emptyList()
    private val checkedIngredientIds = mutableSetOf<Int>()
    private var ingredientsLoaded = false

    // Mutual exclusivity pairs: ingredient name -> its "lítið" counterpart name
    private val litidPairs = mapOf(
        "Mæjó" to "Lítið mæjó",
        "Lítið mæjó" to "Mæjó",
        "Sinnep" to "Lítið sinnep",
        "Lítið sinnep" to "Sinnep",
        "Tómatsósa" to "Lítil tómatsósa",
        "Lítil tómatsósa" to "Tómatsósa"
    )

    // Map ingredient IDs to their CheckBox widgets for programmatic unchecking
    private val checkBoxMap = mutableMapOf<Int, CheckBox>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_item_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvItemName = view.findViewById(R.id.tvItemName)
        tvItemDescription = view.findViewById(R.id.tvItemDescription)
        tvItemPrice = view.findViewById(R.id.tvItemPrice)
        tvQuantity = view.findViewById(R.id.tvQuantity)
        btnDecrease = view.findViewById(R.id.btnDecrease)
        btnIncrease = view.findViewById(R.id.btnIncrease)
        btnAddToBasket = view.findViewById(R.id.btnAddToBasket)
        btnBreyta = view.findViewById(R.id.btnBreyta)
        ingredientsPanel = view.findViewById(R.id.ingredientsPanel)

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        val item = BasketService.getItemById(itemId)
        if (item == null) {
            Toast.makeText(requireContext(), getString(R.string.item_not_found), Toast.LENGTH_SHORT).show()
            activity?.onBackPressedDispatcher?.onBackPressed()
            return
        }

        tvItemName.text = item.name
        tvItemDescription.text = item.description
        tvItemPrice.text = "${item.priceIsk} ISK"

        quantity = 1
        tvQuantity.text = quantity.toString()

        btnDecrease.setOnClickListener {
            if (quantity > 1) {
                quantity--
                tvQuantity.text = quantity.toString()
            }
        }
        btnIncrease.setOnClickListener {
            quantity++
            tvQuantity.text = quantity.toString()
        }

        btnBreyta.setOnClickListener {
            if (ingredientsPanel.visibility == View.GONE) {
                if (!ingredientsLoaded) {
                    loadIngredients()
                }
                ingredientsPanel.visibility = View.VISIBLE
                btnBreyta.text = getString(R.string.breyta) + " ▲"
            } else {
                ingredientsPanel.visibility = View.GONE
                btnBreyta.text = getString(R.string.breyta) + " ▼"
            }
        }
        btnBreyta.text = getString(R.string.breyta) + " ▼"

        btnAddToBasket.setOnClickListener {
            val addedIngredients = if (ingredientsLoaded) {
                allIngredients.filter { ig ->
                    ig.id in checkedIngredientIds &&
                    ig.id !in defaultIngredientIds
                }
            } else emptyList()

            val removedIngredients = if (ingredientsLoaded) {
                allIngredients.filter { ig ->
                    ig.id in defaultIngredientIds && ig.id !in checkedIngredientIds
                }
            } else emptyList()

            BasketService.addItem(item, quantity, addedIngredients, removedIngredients)
            Toast.makeText(requireContext(), getString(R.string.add_to_basket), Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.openBasketFromItemDetails()
        }
    }

    private fun loadIngredients() {
        ingredientsLoaded = true
        Thread {
            val api = ApiHelper()
            var ingredients = api.getIngredients()
            var defaultIds = api.getItemIngredientIds(itemId)

            if (ingredients.isEmpty()) {
                ingredients = HagaMenuData.ingredients
            }
            if (defaultIds.isEmpty()) {
                defaultIds = HagaMenuData.itemIngredientDefaults[itemId] ?: emptyList()
            }

            allIngredients = ingredients
            defaultIngredientIds = defaultIds
            checkedIngredientIds.clear()
            checkedIngredientIds.addAll(defaultIds)

            activity?.runOnUiThread {
                buildIngredientsUI()
            }
        }.start()
    }

    private fun buildIngredientsUI() {
        ingredientsPanel.removeAllViews()
        checkBoxMap.clear()

        val categoryOrder = listOf("extra", "ostur", "sosur", "alegg")
        val categoryNames = mapOf(
            "extra" to getString(R.string.category_extra),
            "ostur" to getString(R.string.category_ostur),
            "sosur" to getString(R.string.category_sosur),
            "alegg" to getString(R.string.category_alegg)
        )

        for (cat in categoryOrder) {
            val catIngredients = allIngredients.filter { it.category == cat }
            if (catIngredients.isEmpty()) continue

            val header = TextView(requireContext()).apply {
                text = categoryNames[cat] ?: cat
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(0xFF291317.toInt())
                setPadding(0, 24, 0, 8)
            }
            ingredientsPanel.addView(header)

            if (cat == "ostur") {
                buildCheeseRadioGroup(catIngredients)
            } else {
                buildCheckboxGroup(catIngredients)
            }
        }
    }

    /** Cheese: radio-style — pick one or none. Tapping the selected one deselects it. */
    private fun buildCheeseRadioGroup(cheeseIngredients: List<Ingredient>) {
        val radioGroup = RadioGroup(requireContext()).apply {
            orientation = RadioGroup.VERTICAL
        }

        // Determine which cheese is default-checked
        val defaultCheese = cheeseIngredients.find { it.id in checkedIngredientIds }

        for (ig in cheeseIngredients) {
            val rb = RadioButton(requireContext()).apply {
                text = ig.name
                id = View.generateViewId()
                textSize = 15f
                setTextColor(0xFF291317.toInt())
                setPadding(8, 4, 0, 4)
                isChecked = ig.id in checkedIngredientIds
            }
            radioGroup.addView(rb)

            // Allow deselecting by tapping the already-selected radio button
            rb.setOnClickListener {
                if (ig.id in checkedIngredientIds) {
                    // Already was checked — deselect
                    rb.isChecked = false
                    radioGroup.clearCheck()
                    checkedIngredientIds.remove(ig.id)
                } else {
                    // Remove all other cheese from checked set, add this one
                    cheeseIngredients.forEach { checkedIngredientIds.remove(it.id) }
                    checkedIngredientIds.add(ig.id)
                }
                updatePriceDisplay()
            }
        }

        ingredientsPanel.addView(radioGroup)
    }

    /** Standard checkboxes with mutual-exclusivity for lítið pairs */
    private fun buildCheckboxGroup(ingredients: List<Ingredient>) {
        for (ig in ingredients) {
            val label = if (ig.extraPriceIsk > 0) {
                "${ig.name} +${ig.extraPriceIsk} kr"
            } else {
                ig.name
            }

            val cb = CheckBox(requireContext()).apply {
                text = label
                isChecked = ig.id in checkedIngredientIds
                textSize = 15f
                setTextColor(0xFF291317.toInt())
                setPadding(8, 4, 0, 4)
                setOnCheckedChangeListener { _, checked ->
                    if (checked) {
                        checkedIngredientIds.add(ig.id)
                        // Enforce mutual exclusivity for lítið pairs
                        val exclusiveName = litidPairs[ig.name]
                        if (exclusiveName != null) {
                            val exclusiveIg = allIngredients.find { it.name == exclusiveName }
                            if (exclusiveIg != null && exclusiveIg.id in checkedIngredientIds) {
                                checkedIngredientIds.remove(exclusiveIg.id)
                                checkBoxMap[exclusiveIg.id]?.isChecked = false
                            }
                        }
                    } else {
                        checkedIngredientIds.remove(ig.id)
                    }
                    updatePriceDisplay()
                }
            }
            checkBoxMap[ig.id] = cb
            ingredientsPanel.addView(cb)
        }
    }

    private fun updatePriceDisplay() {
        val item = BasketService.getItemById(itemId) ?: return
        val extrasPrice = allIngredients
            .filter { it.id in checkedIngredientIds && it.extraPriceIsk > 0 && it.id !in defaultIngredientIds }
            .sumOf { it.extraPriceIsk }
        val totalPrice = item.priceIsk + extrasPrice
        tvItemPrice.text = "$totalPrice ISK"
    }

    companion object {
        private const val ARG_ITEM_ID = "item_id"

        fun newInstance(itemId: Int): ItemDetailsFragment {
            return ItemDetailsFragment().apply {
                arguments = Bundle().apply { putInt(ARG_ITEM_ID, itemId) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(ARG_ITEM_ID, 0)?.let { itemId = it }
    }
}
