package com.example.whoopsmobile.ui.combo

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
import androidx.cardview.widget.CardView
import androidx.fragment.app.Fragment
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
import com.example.whoopsmobile.data.ComboDefinitions
import com.example.whoopsmobile.data.HagaMenuData
import com.example.whoopsmobile.data.IngredientRestrictions
import com.example.whoopsmobile.data.api.ApiHelper
import com.example.whoopsmobile.model.ComboChoice
import com.example.whoopsmobile.model.ComboSelection
import com.example.whoopsmobile.model.ComboStepConfig
import com.example.whoopsmobile.model.CustomizationMode
import com.example.whoopsmobile.model.Ingredient
import com.example.whoopsmobile.model.Item
import com.example.whoopsmobile.service.BasketService

class ComboBuilderFragment : Fragment() {

    private var comboItemId: Int = 0
    private var comboItem: Item? = null
    private var steps: List<ComboStepConfig> = emptyList()
    private var currentStep: Int = 0

    // Selections per step. For multi-count steps, stores multiple selections.
    private val selections = mutableListOf<MutableList<SelectionState>>()

    private lateinit var tvComboTitle: TextView
    private lateinit var tvStepIndicator: TextView
    private lateinit var tvStepTitle: TextView
    private lateinit var tvSelectionHint: TextView
    private lateinit var tvInfoText: TextView
    private lateinit var choicesContainer: LinearLayout
    private lateinit var tvComboPrice: TextView
    private lateinit var btnNext: Button

    // Ingredient data
    private var allIngredients: List<Ingredient> = emptyList()
    private var ingredientsLoaded = false

    // Per-choice customization state
    data class SelectionState(
        val choice: ComboChoice,
        val checkedIngredientIds: MutableSet<Int> = mutableSetOf(),
        val defaultIngredientIds: List<Int> = emptyList()
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_combo_builder, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvComboTitle = view.findViewById(R.id.tvComboTitle)
        tvStepIndicator = view.findViewById(R.id.tvStepIndicator)
        tvStepTitle = view.findViewById(R.id.tvStepTitle)
        tvSelectionHint = view.findViewById(R.id.tvSelectionHint)
        tvInfoText = view.findViewById(R.id.tvInfoText)
        choicesContainer = view.findViewById(R.id.choicesContainer)
        tvComboPrice = view.findViewById(R.id.tvComboPrice)
        btnNext = view.findViewById(R.id.btnNext)

        view.findViewById<ImageButton>(R.id.btnBack).setOnClickListener {
            if (currentStep > 0) {
                currentStep--
                renderStep()
            } else {
                activity?.onBackPressedDispatcher?.onBackPressed()
            }
        }

        comboItem = BasketService.getItemById(comboItemId)
        if (comboItem == null) {
            Toast.makeText(requireContext(), getString(R.string.item_not_found), Toast.LENGTH_SHORT).show()
            activity?.onBackPressedDispatcher?.onBackPressed()
            return
        }

        steps = ComboDefinitions.getSteps(comboItemId)
        tvComboTitle.text = comboItem!!.name

        // Initialize selection state for each step
        selections.clear()
        for (step in steps) {
            selections.add(mutableListOf())
        }

        // Load ingredients then render first step
        loadIngredients()
    }

    private fun loadIngredients() {
        Thread {
            val api = ApiHelper()
            var ingredients = api.getIngredients()
            if (ingredients.isEmpty()) {
                ingredients = HagaMenuData.ingredients
            }
            allIngredients = ingredients
            ingredientsLoaded = true

            activity?.runOnUiThread {
                renderStep()
            }
        }.start()
    }

    private fun renderStep() {
        if (currentStep >= steps.size) return
        val step = steps[currentStep]

        tvStepIndicator.text = getString(R.string.combo_step_of, currentStep + 1, steps.size)
        tvStepTitle.text = step.stepLabel

        // Show selection count hint for multi-select
        if (step.count > 1) {
            val selected = selections[currentStep].size
            tvSelectionHint.text = getString(R.string.combo_selected_count, selected, step.count)
            tvSelectionHint.visibility = View.VISIBLE
        } else {
            tvSelectionHint.visibility = View.GONE
        }

        // Show info text for fjölskyldu fries
        if (comboItemId == 3 && currentStep == steps.size - 1) {
            // Last step of family deal - show fries included note
            tvInfoText.text = getString(R.string.combo_fries_included)
            tvInfoText.visibility = View.VISIBLE
        } else {
            tvInfoText.visibility = View.GONE
        }

        // Button text
        val isLastStep = currentStep == steps.size - 1
        btnNext.text = if (isLastStep) getString(R.string.add_to_basket) else getString(R.string.combo_next)

        btnNext.setOnClickListener {
            if (!validateStep()) return@setOnClickListener
            if (isLastStep) {
                addComboToBasket()
            } else {
                currentStep++
                renderStep()
            }
        }

        renderChoices(step)
        updatePrice()
    }

    private fun validateStep(): Boolean {
        val step = steps[currentStep]
        val selected = selections[currentStep]
        if (selected.size < step.count) {
            Toast.makeText(requireContext(), getString(R.string.combo_pick_required), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun renderChoices(step: ComboStepConfig) {
        choicesContainer.removeAllViews()

        if (step.count == 1) {
            renderSingleSelectChoices(step)
        } else {
            renderMultiSelectChoices(step)
        }
    }

    private fun renderSingleSelectChoices(step: ComboStepConfig) {
        val currentSelection = selections[currentStep].firstOrNull()

        for (choice in step.choices) {
            val choiceView = createChoiceCard(choice, choice == currentSelection?.choice)

            choiceView.setOnClickListener {
                // Set this as the selection
                val defaults = getDefaultIngredientIds(choice.item.id)
                val state = SelectionState(choice, defaults.toMutableSet(), defaults)
                selections[currentStep] = mutableListOf(state)
                renderStep() // Re-render to update selection highlight
            }

            choicesContainer.addView(choiceView)

            // Show breyta panel if this choice is selected and customizable
            if (currentSelection?.choice == choice && step.allowCustomization &&
                choice.customizationMode != CustomizationMode.NONE) {
                addCustomizationPanel(currentSelection)
            }
        }
    }

    private fun renderMultiSelectChoices(step: ComboStepConfig) {
        val currentSelections = selections[currentStep]

        // For multi-select, we render numbered slots
        for (slotIndex in 0 until step.count) {
            val slotSelection = currentSelections.getOrNull(slotIndex)

            val slotHeader = TextView(requireContext()).apply {
                text = "${slotIndex + 1}"
                textSize = 16f
                setTypeface(null, Typeface.BOLD)
                setTextColor(0xFF291317.toInt())
                setPadding(0, if (slotIndex > 0) 32 else 0, 0, 8)
            }
            choicesContainer.addView(slotHeader)

            for (choice in step.choices) {
                val isSelected = slotSelection?.choice == choice
                val choiceView = createChoiceCard(choice, isSelected)

                choiceView.setOnClickListener {
                    val defaults = getDefaultIngredientIds(choice.item.id)
                    val state = SelectionState(choice, defaults.toMutableSet(), defaults)

                    // Replace or add at this slot index
                    if (slotIndex < currentSelections.size) {
                        currentSelections[slotIndex] = state
                    } else {
                        currentSelections.add(state)
                    }
                    renderStep()
                }

                choicesContainer.addView(choiceView)
            }

            // Show customization for selected slot item
            if (slotSelection != null && step.allowCustomization &&
                slotSelection.choice.customizationMode != CustomizationMode.NONE) {
                addCustomizationPanel(slotSelection)
            }
        }

        // Update hint
        tvSelectionHint.text = getString(R.string.combo_selected_count, currentSelections.size, step.count)
    }

    private fun createChoiceCard(choice: ComboChoice, isSelected: Boolean): LinearLayout {
        val card = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = android.view.Gravity.CENTER_VERTICAL
            setPadding(24, 20, 24, 20)
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 8, 0, 8)
            layoutParams = lp

            if (isSelected) {
                setBackgroundColor(0xFFE8D5C4.toInt())
            } else {
                setBackgroundColor(0xFFEEECE0.toInt())
            }
        }

        val nameView = TextView(requireContext()).apply {
            text = choice.item.name
            textSize = 16f
            setTextColor(0xFF291317.toInt())
            layoutParams = LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            if (isSelected) setTypeface(null, Typeface.BOLD)
        }
        card.addView(nameView)

        val priceView = TextView(requireContext()).apply {
            text = if (choice.priceDelta == 0) {
                getString(R.string.combo_included)
            } else {
                getString(R.string.combo_upgrade, choice.priceDelta)
            }
            textSize = 14f
            setTextColor(if (choice.priceDelta > 0) 0xFFBC4315.toInt() else 0xFF555555.toInt())
        }
        card.addView(priceView)

        if (isSelected) {
            val checkView = TextView(requireContext()).apply {
                text = " ✓"
                textSize = 18f
                setTextColor(0xFFBC4315.toInt())
            }
            card.addView(checkView)
        }

        return card
    }

    private fun addCustomizationPanel(state: SelectionState) {
        val breytaBtn = Button(requireContext()).apply {
            text = getString(R.string.breyta) + " ▼"
            textSize = 14f
            setPadding(24, 12, 24, 12)
            setBackgroundColor(0xFF291317.toInt())
            setTextColor(0xFFF6F4E4.toInt())
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 4, 0, 4)
            layoutParams = lp
        }

        val panel = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(16, 16, 16, 16)
            setBackgroundColor(0xFFEEECE0.toInt())
            visibility = View.GONE
            val lp = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            lp.setMargins(0, 0, 0, 8)
            layoutParams = lp
        }

        breytaBtn.setOnClickListener {
            if (panel.visibility == View.GONE) {
                if (panel.childCount == 0) {
                    buildCustomizationContent(panel, state)
                }
                panel.visibility = View.VISIBLE
                breytaBtn.text = getString(R.string.breyta) + " ▲"
            } else {
                panel.visibility = View.GONE
                breytaBtn.text = getString(R.string.breyta) + " ▼"
            }
        }

        choicesContainer.addView(breytaBtn)
        choicesContainer.addView(panel)
    }

    private fun buildCustomizationContent(panel: LinearLayout, state: SelectionState) {
        val itemId = state.choice.item.id
        val mode = state.choice.customizationMode

        var ingredients = allIngredients.toList()

        // Apply ingredient restrictions
        val allowedIds = IngredientRestrictions.allowedIngredientIds[itemId]
        if (allowedIds != null) {
            ingredients = ingredients.filter { it.id in allowedIds }
        }

        // Apply price overrides
        val overrides = IngredientRestrictions.priceOverrides[itemId]
        if (overrides != null) {
            ingredients = ingredients.map { ig ->
                val override = overrides[ig.id]
                if (override != null) ig.copy(extraPriceIsk = override) else ig
            }
        }

        if (mode == CustomizationMode.KIDS) {
            buildKidsCustomization(panel, state, ingredients)
        } else {
            buildFullCustomization(panel, state, ingredients)
        }
    }

    private fun buildKidsCustomization(panel: LinearLayout, state: SelectionState, ingredients: List<Ingredient>) {
        val checkBoxMap = mutableMapOf<Int, CheckBox>()

        // Extras
        val extras = ingredients.filter { it.id in IngredientRestrictions.kidsBurgerAllowedExtraIds }
        if (extras.isNotEmpty()) {
            panel.addView(makeCategoryHeader(getString(R.string.category_extra)))
            for (ig in extras) {
                val label = if (ig.extraPriceIsk > 0) "${ig.name} +${ig.extraPriceIsk} kr" else ig.name
                val cb = CheckBox(requireContext()).apply {
                    text = label
                    isChecked = ig.id in state.checkedIngredientIds
                    textSize = 15f
                    setTextColor(0xFF291317.toInt())
                    setOnCheckedChangeListener { _, checked ->
                        if (checked) state.checkedIngredientIds.add(ig.id)
                        else state.checkedIngredientIds.remove(ig.id)
                        updateAukaOsturLabelCombo(state, checkBoxMap, ingredients)
                        updatePrice()
                    }
                }
                checkBoxMap[ig.id] = cb
                panel.addView(cb)
            }
        }

        // Sauces as radio
        val sauces = ingredients.filter { it.id in IngredientRestrictions.kidsBurgerSauceIds }
        if (sauces.isNotEmpty()) {
            panel.addView(makeCategoryHeader(getString(R.string.category_sosur)))
            val rg = RadioGroup(requireContext()).apply { orientation = RadioGroup.VERTICAL }
            for (ig in sauces) {
                val rb = RadioButton(requireContext()).apply {
                    text = ig.name
                    id = View.generateViewId()
                    textSize = 15f
                    setTextColor(0xFF291317.toInt())
                    isChecked = ig.id in state.checkedIngredientIds
                }
                rg.addView(rb)
                rb.setOnClickListener {
                    if (ig.id in state.checkedIngredientIds) {
                        rb.isChecked = false
                        rg.clearCheck()
                        state.checkedIngredientIds.remove(ig.id)
                    } else {
                        sauces.forEach { state.checkedIngredientIds.remove(it.id) }
                        state.checkedIngredientIds.add(ig.id)
                    }
                    updatePrice()
                }
            }
            panel.addView(rg)
        }
    }

    private fun buildFullCustomization(panel: LinearLayout, state: SelectionState, ingredients: List<Ingredient>) {
        val checkBoxMap = mutableMapOf<Int, CheckBox>()
        val categoryOrder = listOf("extra", "ostur", "sosur", "alegg")
        val categoryNames = mapOf(
            "extra" to getString(R.string.category_extra),
            "ostur" to getString(R.string.category_ostur),
            "sosur" to getString(R.string.category_sosur),
            "alegg" to getString(R.string.category_alegg)
        )

        val litidPairs = mapOf(
            "Mæjó" to "Lítið mæjó", "Lítið mæjó" to "Mæjó",
            "Sinnep" to "Lítið sinnep", "Lítið sinnep" to "Sinnep",
            "Tómatsósa" to "Lítil tómatsósa", "Lítil tómatsósa" to "Tómatsósa"
        )

        for (cat in categoryOrder) {
            val catIngredients = ingredients.filter { it.category == cat }
            if (catIngredients.isEmpty()) continue

            panel.addView(makeCategoryHeader(categoryNames[cat] ?: cat))

            if (cat == "ostur") {
                // Radio group for cheese
                val rg = RadioGroup(requireContext()).apply { orientation = RadioGroup.VERTICAL }
                for (ig in catIngredients) {
                    val rb = RadioButton(requireContext()).apply {
                        text = ig.name
                        id = View.generateViewId()
                        textSize = 15f
                        setTextColor(0xFF291317.toInt())
                        isChecked = ig.id in state.checkedIngredientIds
                    }
                    rg.addView(rb)
                    rb.setOnClickListener {
                        if (ig.id in state.checkedIngredientIds) {
                            rb.isChecked = false
                            rg.clearCheck()
                            state.checkedIngredientIds.remove(ig.id)
                        } else {
                            catIngredients.forEach { state.checkedIngredientIds.remove(it.id) }
                            state.checkedIngredientIds.add(ig.id)
                        }
                        updatePrice()
                    }
                }
                panel.addView(rg)
            } else {
                // Checkboxes
                for (ig in catIngredients) {
                    val label = if (ig.extraPriceIsk > 0) "${ig.name} +${ig.extraPriceIsk} kr" else ig.name
                    val cb = CheckBox(requireContext()).apply {
                        text = label
                        isChecked = ig.id in state.checkedIngredientIds
                        textSize = 15f
                        setTextColor(0xFF291317.toInt())
                        setOnCheckedChangeListener { _, checked ->
                            if (checked) {
                                state.checkedIngredientIds.add(ig.id)
                                val exclusiveName = litidPairs[ig.name]
                                if (exclusiveName != null) {
                                    val exclusiveIg = ingredients.find { it.name == exclusiveName }
                                    if (exclusiveIg != null && exclusiveIg.id in state.checkedIngredientIds) {
                                        state.checkedIngredientIds.remove(exclusiveIg.id)
                                        checkBoxMap[exclusiveIg.id]?.isChecked = false
                                    }
                                }
                            } else {
                                state.checkedIngredientIds.remove(ig.id)
                            }
                            updateAukaOsturLabelCombo(state, checkBoxMap, ingredients)
                            updatePrice()
                        }
                    }
                    checkBoxMap[ig.id] = cb
                    panel.addView(cb)
                }
            }
        }
    }

    private fun updateAukaOsturLabelCombo(state: SelectionState, checkBoxMap: Map<Int, CheckBox>, ingredients: List<Ingredient>) {
        val aukaKjotChecked = 1 in state.checkedIngredientIds
        for (id in listOf(2, 17)) {
            checkBoxMap[id]?.let { cb ->
                val ig = ingredients.find { it.id == id }
                if (ig != null) {
                    val price = if (aukaKjotChecked) ig.extraPriceIsk * 2 else ig.extraPriceIsk
                    cb.text = if (price > 0) "${ig.name} +${price} kr" else ig.name
                }
            }
        }
    }

    private fun makeCategoryHeader(title: String): TextView {
        return TextView(requireContext()).apply {
            text = title
            textSize = 16f
            setTypeface(null, Typeface.BOLD)
            setTextColor(0xFF291317.toInt())
            setPadding(0, 24, 0, 8)
        }
    }

    private fun getDefaultIngredientIds(itemId: Int): List<Int> {
        return HagaMenuData.itemIngredientDefaults[itemId] ?: emptyList()
    }

    private fun updatePrice() {
        val basePrice = comboItem?.priceIsk ?: 0
        var extras = 0

        for (stepSelections in selections) {
            for (state in stepSelections) {
                extras += state.choice.priceDelta
                // Add ingredient extras
                val aukaKjotChecked = 1 in state.checkedIngredientIds
                for (igId in state.checkedIngredientIds) {
                    if (igId !in state.defaultIngredientIds) {
                        val ig = allIngredients.find { it.id == igId }
                        if (ig != null && ig.extraPriceIsk > 0) {
                            val price = if ((igId == 2 || igId == 17) && aukaKjotChecked) {
                                ig.extraPriceIsk * 2
                            } else {
                                ig.extraPriceIsk
                            }
                            // Check for price overrides
                            val overridePrice = IngredientRestrictions.priceOverrides[state.choice.item.id]?.get(igId)
                            extras += overridePrice ?: price
                        }
                    }
                }
            }
        }

        tvComboPrice.text = "${basePrice + extras} ISK"
    }

    private fun addComboToBasket() {
        val item = comboItem ?: return

        val comboSelections = mutableListOf<ComboSelection>()
        for (stepIndex in steps.indices) {
            val step = steps[stepIndex]
            for (state in selections[stepIndex]) {
                val addedIngredients = allIngredients.filter { ig ->
                    ig.id in state.checkedIngredientIds && ig.id !in state.defaultIngredientIds
                }.map { ig ->
                    // Apply price overrides
                    val overridePrice = IngredientRestrictions.priceOverrides[state.choice.item.id]?.get(ig.id)
                    if (overridePrice != null) ig.copy(extraPriceIsk = overridePrice) else ig
                }.let { ingredients ->
                    // Apply auka ostur doubling
                    val hasAukaKjot = ingredients.any { it.id == 1 }
                    if (hasAukaKjot) {
                        ingredients.map { ig ->
                            if (ig.id == 2 || ig.id == 17) ig.copy(extraPriceIsk = ig.extraPriceIsk * 2) else ig
                        }
                    } else ingredients
                }

                val removedIngredients = allIngredients.filter { ig ->
                    ig.id in state.defaultIngredientIds && ig.id !in state.checkedIngredientIds
                }

                comboSelections.add(
                    ComboSelection(
                        stepLabel = step.stepLabel,
                        item = state.choice.item,
                        priceDelta = state.choice.priceDelta,
                        addedIngredients = addedIngredients,
                        removedIngredients = removedIngredients
                    )
                )
            }
        }

        // For fjölskyldu tilboð, add the included large fries as a selection
        if (comboItemId == 3) {
            val largeFries = HagaMenuData.items.find { it.id == 14 }
            if (largeFries != null) {
                comboSelections.add(
                    ComboSelection(
                        stepLabel = "Franskar",
                        item = largeFries,
                        priceDelta = 0
                    )
                )
            }
        }

        BasketService.addComboItem(item, 1, comboSelections)
        Toast.makeText(requireContext(), getString(R.string.add_to_basket), Toast.LENGTH_SHORT).show()
        (activity as? MainActivity)?.openBasketFromItemDetails()
    }

    companion object {
        private const val ARG_COMBO_ITEM_ID = "combo_item_id"

        fun newInstance(comboItemId: Int): ComboBuilderFragment {
            return ComboBuilderFragment().apply {
                arguments = Bundle().apply { putInt(ARG_COMBO_ITEM_ID, comboItemId) }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getInt(ARG_COMBO_ITEM_ID, 0)?.let { comboItemId = it }
    }
}
