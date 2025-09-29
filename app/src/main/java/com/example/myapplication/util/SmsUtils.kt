package com.example.myapplication.util

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

object SmsUtils {
    private const val SMS_PERMISSION_CODE = 101

    fun sendSms(activity: Activity, phoneNumber: String, message: String) {
        // Kiểm tra quyền trước
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.SEND_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Nếu chưa có quyền -> xin quyền
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(Manifest.permission.SEND_SMS),
                SMS_PERMISSION_CODE
            )
        } else {
            // Nếu đã có quyền -> gửi SMS
            val smsManager = SmsManager.getDefault()
            smsManager.sendTextMessage(phoneNumber, null, message, null, null)
        }
    }
}