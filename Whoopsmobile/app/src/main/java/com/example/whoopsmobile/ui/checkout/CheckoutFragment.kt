package com.example.whoopsmobile.ui.checkout

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
import com.example.whoopsmobile.service.BasketService
import com.example.whoopsmobile.service.OrderService
import com.example.whoopsmobile.service.SessionManager

class CheckoutFragment : Fragment() {

    private lateinit var etPhone: EditText
    private lateinit var rvItems: RecyclerView
    private lateinit var tvTotal: TextView
    private lateinit var adapter: CheckoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_checkout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        etPhone = view.findViewById(R.id.etPhone)
        rvItems = view.findViewById(R.id.rvCheckoutItems)
        tvTotal = view.findViewById(R.id.tvTotal)

        etPhone.setText(SessionManager.customerPhone ?: "")

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        adapter = CheckoutAdapter(BasketService.getBasket().items.toList())
        rvItems.layoutManager = LinearLayoutManager(requireContext())
        rvItems.adapter = adapter

        val items = BasketService.getBasket().items.toList()
        adapter.updateItems(items)
        tvTotal.text = "${getString(R.string.total)}: ${BasketService.calculateTotal()} ISK"

        view.findViewById<Button>(R.id.btnConfirmOrder).setOnClickListener { confirmOrder() }
    }

    private fun confirmOrder() {
        val phone = etPhone.text?.toString()?.trim()
        if (phone.isNullOrBlank()) {
            Toast.makeText(requireContext(), getString(R.string.phone_hint), Toast.LENGTH_SHORT).show()
            return
        }
        SessionManager.customerPhone = phone
        requireView().findViewById<Button>(R.id.btnConfirmOrder).isEnabled = false
        Thread {
            val orderId = OrderService.createOrder()
            requireActivity().runOnUiThread {
                requireView().findViewById<Button>(R.id.btnConfirmOrder).isEnabled = true
                if (orderId != null) {
                    BasketService.clearBasket()
                    Toast.makeText(requireContext(), getString(R.string.order_placed), Toast.LENGTH_LONG).show()
                    (activity as? MainActivity)?.openRestaurantListClearBackStack()
                } else {
                    Toast.makeText(requireContext(), getString(R.string.order_error), Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }
}
