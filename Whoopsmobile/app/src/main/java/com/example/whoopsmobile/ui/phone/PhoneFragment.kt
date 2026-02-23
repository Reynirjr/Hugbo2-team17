package com.example.whoopsmobile.ui.phone

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
import com.example.whoopsmobile.service.SessionManager

class PhoneFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_phone, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val etPhone = view.findViewById<EditText>(R.id.etPhone)
        view.findViewById<Button>(R.id.btnContinue).setOnClickListener {
            val phone = etPhone.text?.toString()?.trim()
            if (phone.isNullOrBlank()) {
                Toast.makeText(requireContext(), getString(R.string.phone_hint), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            SessionManager.customerPhone = phone
            (activity as? MainActivity)?.openRestaurantList()
        }
    }
}
