package com.example.whoopsmobile.ui.orderstatus

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.whoopsmobile.MainActivity
import com.example.whoopsmobile.R
import com.example.whoopsmobile.service.OrderService
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

/**
 * Shows the status of a single order (UML: OrderStatusFragment, loadOrderStatus, refreshStatus).
 * Displays "Skráð" and "Áætlað klárt" as relative/countdown text that updates every minute.
 */
class OrderStatusFragment : Fragment() {

    private var orderId: Long = 0L
    private lateinit var tvOrderId: TextView
    private lateinit var tvOrderStatus: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var tvTotalIsk: TextView
    private lateinit var tvEstimatedReadyAt: TextView
    private lateinit var progressStatus: ProgressBar
    private lateinit var btnRefreshStatus: Button
    private lateinit var btnBackToRestaurants: Button

    private var createdAtMillis: Long? = null
    private var estimatedReadyAtMillis: Long? = null

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            updateTimeDisplays()
            handler.postDelayed(this, INTERVAL_MS)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderId = arguments?.getLong(ARG_ORDER_ID) ?: 0L
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_order_status, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        tvOrderId = view.findViewById(R.id.tvOrderId)
        tvOrderStatus = view.findViewById(R.id.tvOrderStatus)
        tvCreatedAt = view.findViewById(R.id.tvCreatedAt)
        tvTotalIsk = view.findViewById(R.id.tvTotalIsk)
        tvEstimatedReadyAt = view.findViewById(R.id.tvEstimatedReadyAt)
        progressStatus = view.findViewById(R.id.progressStatus)
        btnRefreshStatus = view.findViewById(R.id.btnRefreshStatus)
        btnBackToRestaurants = view.findViewById(R.id.btnBackToRestaurants)

        tvOrderId.text = orderId.toString()

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }
        btnRefreshStatus.setOnClickListener { loadOrderStatus() }
        btnBackToRestaurants.setOnClickListener {
            (activity as? MainActivity)?.openRestaurantListClearBackStack()
        }

        loadOrderStatus()
    }

    override fun onResume() {
        super.onResume()
        startTimerUpdates()
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

    private fun startTimerUpdates() {
        handler.removeCallbacks(updateRunnable)
        if (createdAtMillis != null || estimatedReadyAtMillis != null) {
            updateTimeDisplays()
            handler.postDelayed(updateRunnable, INTERVAL_MS)
        }
    }

    private fun updateTimeDisplays() {
        if (!isAdded) return
        val now = System.currentTimeMillis()

        createdAtMillis?.let { created ->
            tvCreatedAt.text = formatMinutesAgo(now - created)
        } ?: run { tvCreatedAt.text = "—" }

        estimatedReadyAtMillis?.let { readyAt ->
            tvEstimatedReadyAt.text = formatCountdown(readyAt - now)
        } ?: run { tvEstimatedReadyAt.text = "—" }
    }

    private fun formatMinutesAgo(diffMs: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        return when {
            minutes < 0 -> getString(R.string.order_time_ago_now)
            minutes == 0L -> getString(R.string.order_time_ago_now)
            minutes == 1L -> getString(R.string.order_time_ago_1_min)
            else -> getString(R.string.order_time_ago_minutes, minutes.toInt())
        }
    }

    private fun formatCountdown(diffMs: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        return when {
            minutes <= 0 -> getString(R.string.order_ready_now)
            minutes == 1L -> getString(R.string.order_ready_in_1_min)
            else -> getString(R.string.order_ready_in_minutes, minutes.toInt())
        }
    }

    private fun loadOrderStatus() {
        if (orderId <= 0L) {
            tvOrderStatus.text = "—"
            return
        }
        progressStatus.visibility = View.VISIBLE
        tvOrderStatus.text = getString(R.string.order_status_loading)
        tvCreatedAt.text = "—"
        tvEstimatedReadyAt.text = "—"
        val act = activity
        Thread {
            val order = OrderService.getOrderStatus(orderId)
            act?.runOnUiThread {
                if (act.isDestroyed) return@runOnUiThread
                progressStatus.visibility = View.GONE
                if (order != null) {
                    tvOrderStatus.text = order.status
                    tvTotalIsk.text = order.totalIsk?.let { "$it ISK" } ?: "—"
                    createdAtMillis = order.createdAt?.let { parseIsoToMillis(it) }
                    estimatedReadyAtMillis = order.estimatedReadyAt?.let { parseIsoToMillis(it) }
                    updateTimeDisplays()
                    startTimerUpdates()
                } else {
                    tvOrderStatus.text = "—"
                    tvCreatedAt.text = "—"
                    tvTotalIsk.text = "—"
                    tvEstimatedReadyAt.text = "—"
                    createdAtMillis = null
                    estimatedReadyAtMillis = null
                }
            }
        }.start()
    }

    private fun parseIsoToMillis(iso: String): Long? {
        if (iso.isBlank()) return null
        val trimmed = iso.trim()
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
            "yyyy-MM-dd'T'HH:mm:ssXXX",
            "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
            "yyyy-MM-dd'T'HH:mm:ss'Z'",
            "yyyy-MM-dd HH:mm:ss.SSSSSSXXX",
            "yyyy-MM-dd HH:mm:ss.SSSXXX",
            "yyyy-MM-dd HH:mm:ssXXX"
        )
        for (pattern in formats) {
            try {
                val sdf = SimpleDateFormat(pattern, Locale.US).apply {
                    timeZone = TimeZone.getTimeZone("UTC")
                }
                return sdf.parse(trimmed)?.time
            } catch (_: Exception) { }
        }
        return null
    }

    companion object {
        private const val ARG_ORDER_ID = "order_id"
        private const val INTERVAL_MS = 60_000L

        fun newInstance(orderId: Long): OrderStatusFragment {
            return OrderStatusFragment().apply {
                arguments = Bundle().apply { putLong(ARG_ORDER_ID, orderId) }
            }
        }
    }
}
