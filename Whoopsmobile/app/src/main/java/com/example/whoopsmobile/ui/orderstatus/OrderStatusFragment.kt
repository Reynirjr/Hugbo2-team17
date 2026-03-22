package com.example.whoopsmobile.ui.orderstatus

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.whoopsmobile.R
import com.example.whoopsmobile.service.OrderService
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.roundToInt

class OrderStatusFragment : Fragment() {
    private lateinit var step1: View
    private lateinit var step2: View
    private lateinit var step3: View
    private lateinit var line1: View
    private lateinit var line2: View
    private var orderId: Long = 0L
    private lateinit var tvOrderId: TextView
    private lateinit var tvCreatedAt: TextView
    private lateinit var tvTotalIsk: TextView
    private lateinit var tvEstimatedReadyAt: TextView
    private lateinit var tvDistance: TextView
    private lateinit var progressStatus: ProgressBar
    private lateinit var btnRefreshStatus: Button

    private val restaurantLat = 64.144354
    private val restaurantLng = -21.961650
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
        step1 = view.findViewById(R.id.step1)
        step2 = view.findViewById(R.id.step2)
        step3 = view.findViewById(R.id.step3)
        line1 = view.findViewById(R.id.line1)
        line2 = view.findViewById(R.id.line2)
        tvOrderId = view.findViewById(R.id.tvOrderId)
        tvOrderStatus = view.findViewById(R.id.tvOrderStatus)
        tvCreatedAt = view.findViewById(R.id.tvCreatedAt)
        tvTotalIsk = view.findViewById(R.id.tvTotalIsk)
        tvEstimatedReadyAt = view.findViewById(R.id.tvEstimatedReadyAt)
        tvDistance = view.findViewById(R.id.tvDistance)
        progressStatus = view.findViewById(R.id.progressStatus)
        btnRefreshStatus = view.findViewById(R.id.btnRefreshStatus)

        tvOrderId.text = orderId.toString()

        view.findViewById<View>(R.id.btnBack).setOnClickListener {
            activity?.onBackPressedDispatcher?.onBackPressed()
        }

        btnRefreshStatus.setOnClickListener { loadOrderStatus() }

        loadOrderStatus()
        checkLocationPermissionAndUpdate()
    }

    private fun updateProgress(status: String?) {
        val currentStep = when (status?.lowercase()) {
            "received" -> 1
            "preparing" -> 2
            "ready" -> 3
            else -> 0
        }

        // Dots
        val steps = listOf(step1, step2, step3)
        steps.forEachIndexed { index, view ->
            if (index < currentStep) {
                view.setBackgroundResource(R.drawable.circle_active)
            } else {
                view.setBackgroundResource(R.drawable.circle_inactive)
            }
        }

        // Lines
        if (currentStep >= 2) {
            line1.setBackgroundResource(R.drawable.progress_line_active)
        } else {
            line1.setBackgroundResource(R.drawable.progress_line_inactive)
        }

        if (currentStep >= 3) {
            line2.setBackgroundResource(R.drawable.progress_line_active)
        } else {
            line2.setBackgroundResource(R.drawable.progress_line_inactive)
        }
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

        createdAtMillis?.let {
            tvCreatedAt.text = formatMinutesAgo(now - it)
        } ?: run { tvCreatedAt.text = "—" }

        estimatedReadyAtMillis?.let {
            tvEstimatedReadyAt.text = formatCountdown(it - now)
        } ?: run { tvEstimatedReadyAt.text = "—" }
    }

    private fun formatMinutesAgo(diffMs: Long): String {
        val minutes = TimeUnit.MILLISECONDS.toMinutes(diffMs)
        return when {
            minutes <= 0 -> getString(R.string.order_time_ago_now)
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

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val results = FloatArray(1)
        Location.distanceBetween(lat1, lon1, lat2, lon2, results)
        return results[0] / 1000.0
    }

    private fun updateDistance() {
        val context = context ?: return
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        try {
            val location =
                locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                    ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (location != null) {
                val distance = calculateDistanceKm(
                    location.latitude,
                    location.longitude,
                    restaurantLat,
                    restaurantLng
                )

                val text = if (distance < 1) {
                    val meters = (distance * 1000).toInt()
                    "Fjarlægð: $meters m frá veitingastað"
                } else {
                    val km = distance.roundToInt()
                    "Fjarlægð: $km km frá veitingastað"
                }

                tvDistance.text = text
            } else {
                tvDistance.text = "Fjarlægð: óþekkt"
            }

        } catch (e: SecurityException) {
            tvDistance.text = "Engin staðsetningarheimild"
        }
    }

    private fun checkLocationPermissionAndUpdate() {
        if (requireContext().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            updateDistance()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 1 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            updateDistance()
        }
    }

    private fun loadOrderStatus() {
        if (orderId <= 0L) return

        progressStatus.visibility = View.VISIBLE


        val act = activity
        Thread {

            val order = OrderService.getOrderStatus(orderId)

            act?.runOnUiThread {
                if (act.isDestroyed) return@runOnUiThread

                progressStatus.visibility = View.GONE

                if (order != null) {
                    tvOrderStatus.text = getStatusTextIcelandic(order.status)
                    updateProgress(order.status)
                    tvTotalIsk.text = order.totalIsk?.let { "$it ISK" } ?: "—"
                    createdAtMillis = order.createdAt?.let { parseIsoToMillis(it) }
                    estimatedReadyAtMillis = order.estimatedReadyAt?.let { parseIsoToMillis(it) }

                    updateTimeDisplays()
                    startTimerUpdates()
                }
            }
        }.start()
    }

    private fun parseIsoToMillis(iso: String): Long? {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            sdf.parse(iso)?.time
        } catch (e: Exception) {
            null
        }
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
