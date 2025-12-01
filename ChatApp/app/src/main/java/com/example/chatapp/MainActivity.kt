package com.example.chatapp

import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.webkit.WebChromeClient
import android.webkit.JavascriptInterface
import android.widget.FrameLayout
import android.widget.Toast
import android.Manifest
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat

class MainActivity : ComponentActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 100
    }

    private lateinit var webView: WebView

    class WebAppInterface(val activity: MainActivity) {
        @JavascriptInterface
        fun getDeviceInfo(): String {
            return "品牌: ${Build.BRAND}, 型号: ${Build.MODEL}, 系统: ${Build.VERSION.RELEASE}"
        }

        @JavascriptInterface
        fun showToast(msg: String) {
            activity.runOnUiThread {
                Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show()
            }
        }

        // 获取麦克风权限
        @JavascriptInterface
        fun requestMicrophonePermission(): String {
            return if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.RECORD_AUDIO
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                "已获得麦克风权限"
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    PERMISSION_REQUEST_CODE
                )
                "正在请求麦克风权限..."
            }
        }

        // 获取视频权限
        @JavascriptInterface
        fun requestCameraPermission(): String {
            return if (ContextCompat.checkSelfPermission(
                    activity,
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                "已获得摄像头权限"
            } else {
                ActivityCompat.requestPermissions(
                    activity,
                    arrayOf(Manifest.permission.CAMERA),
                    PERMISSION_REQUEST_CODE
                )
                "正在请求摄像头权限..."
            }
        }

        // 推送应用通知
        @JavascriptInterface
        fun sendNotification(title: String, message: String) {
            activity.runOnUiThread {
                activity.createNotification(title, message)
            }
        }
    }

    private fun createNotification(title: String, message: String) {
        val channelId = "chat_notification"
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // 创建通知频道（Android 8.0+ 需要）
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "聊天通知",
                NotificationManager.IMPORTANCE_DEFAULT
            )
            notificationManager.createNotificationChannel(channel)
        }

        // 构建通知
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        // 发送通知
        notificationManager.notify(System.currentTimeMillis().toInt(), notification)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    WebView(context).apply {
                        webView = this
                        layoutParams = FrameLayout.LayoutParams(
                            ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT
                        )

                        webViewClient = WebViewClient()
                        webChromeClient = WebChromeClient()

                        settings.apply {
                            javaScriptEnabled = true
                            useWideViewPort = true
                            loadWithOverviewMode = true
                            domStorageEnabled = true
                            databaseEnabled = true
                            textZoom = 100
                            allowFileAccess = true
                            setSupportZoom(false)
                            builtInZoomControls = false
                            displayZoomControls = false
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                                mixedContentMode = 0
                            }
                        }

                        isFocusable = true
                        isFocusableInTouchMode = true

                        addJavascriptInterface(WebAppInterface(this@MainActivity), "Android")

                        loadUrl("file:///android_asset/chat.html")
                    }
                }
            )
        }
    }

    // 处理权限请求结果
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == PERMISSION_REQUEST_CODE) {
            val messages = mutableListOf<String>()

            for (i in permissions.indices) {
                when {
                    grantResults[i] == PackageManager.PERMISSION_GRANTED -> {
                        messages.add("${permissions[i]} 已授予")
                    }
                    else -> {
                        messages.add("${permissions[i]} 被拒绝")
                    }
                }
            }

            Toast.makeText(this, messages.joinToString("\n"), Toast.LENGTH_SHORT).show()
        }
    }
}