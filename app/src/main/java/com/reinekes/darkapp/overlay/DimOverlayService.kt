package com.reinekes.darkapp.overlay

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.provider.Settings
import android.view.Gravity
import android.view.View
import android.view.WindowManager
import androidx.core.app.NotificationCompat
import com.reinekes.darkapp.MainActivity
import com.reinekes.darkapp.R
import com.reinekes.darkapp.dimming.DimFilter
import com.reinekes.darkapp.dimming.OverlayColor

class DimOverlayService : Service() {
    private val windowManager by lazy { getSystemService(WINDOW_SERVICE) as WindowManager }
    private var overlayView: View? = null

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                removeOverlay()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
            ACTION_START, null -> {
                if (!Settings.canDrawOverlays(this)) {
                    stopSelf()
                    return START_NOT_STICKY
                }
                val percent = intent?.getIntExtra(EXTRA_PERCENT, 36)?.coerceIn(0, 100) ?: 36
                val filter = intent?.getStringExtra(EXTRA_FILTER)
                    ?.let { runCatching { DimFilter.valueOf(it) }.getOrNull() }
                    ?: DimFilter.Neutral

                createChannel()
                startForeground(NOTIFICATION_ID, buildNotification(percent, filter))
                showOverlay(percent, filter)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        removeOverlay()
        super.onDestroy()
    }

    private fun showOverlay(percent: Int, filter: DimFilter) {
        val color = OverlayColor.argb(percent, filter)
        overlayView?.let {
            it.setBackgroundColor(color)
            return
        }

        val view = View(this).apply {
            setBackgroundColor(color)
            importantForAccessibility = View.IMPORTANT_FOR_ACCESSIBILITY_NO
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.MATCH_PARENT,
            overlayType(),
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE or
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
                WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            PixelFormat.TRANSLUCENT,
        ).apply {
            gravity = Gravity.TOP or Gravity.START
        }

        windowManager.addView(view, params)
        overlayView = view
    }

    private fun removeOverlay() {
        overlayView?.let { view ->
            runCatching { windowManager.removeView(view) }
        }
        overlayView = null
    }

    private fun overlayType(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            "Screen dimmer",
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = "Shows that Dark App dimming is active"
            setShowBadge(false)
        }
        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private fun buildNotification(percent: Int, filter: DimFilter) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle("Dark App is dimming the screen")
            .setContentText("$percent% dim, ${filter.label.lowercase()} tone")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(openAppIntent())
            .addAction(
                R.drawable.ic_notification,
                "Stop",
                PendingIntent.getService(
                    this,
                    2,
                    stopIntent(this),
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
                ),
            )
            .build()

    private fun openAppIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this,
            1,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
    }

    companion object {
        const val ACTION_START = "com.reinekes.darkapp.START_DIM"
        const val ACTION_STOP = "com.reinekes.darkapp.STOP_DIM"
        const val EXTRA_PERCENT = "extra_percent"
        const val EXTRA_FILTER = "extra_filter"

        private const val CHANNEL_ID = "screen_dimmer"
        private const val NOTIFICATION_ID = 40

        fun startIntent(context: Context, percent: Int, filter: DimFilter): Intent =
            Intent(context, DimOverlayService::class.java).apply {
                action = ACTION_START
                putExtra(EXTRA_PERCENT, percent)
                putExtra(EXTRA_FILTER, filter.name)
            }

        fun stopIntent(context: Context): Intent =
            Intent(context, DimOverlayService::class.java).apply {
                action = ACTION_STOP
            }
    }
}
