package com.example.whoopsmobile.ui.itemdetails

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
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
        btnAddToBasket.setOnClickListener {
            BasketService.addItem(item, quantity)
            Toast.makeText(requireContext(), getString(R.string.add_to_basket), Toast.LENGTH_SHORT).show()
            (activity as? MainActivity)?.openBasketFromItemDetails()
        }
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
