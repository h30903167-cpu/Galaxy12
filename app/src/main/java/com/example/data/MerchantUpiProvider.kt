package com.example.data

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import java.security.MessageDigest
import java.util.UUID

object MerchantUpiProvider {
    private const val PREFS_NAME = "galaxy_merchant_prefs"
    private const val KEY_ASSIGNED_UPI = "assigned_merchant_upi"

    val MERCHANT_UPI_POOL = listOf(
        "jayray8257@nyes",
        "jayray120@nyes",
        "komaraj988@naviaxis",
        "komalraj988@naviaxis",
        "shivanimtpkyp@mvhdfc",
        "komalrajput988@ptyes",
        "Madhukant220@ybl",
        "Madhukant220@ibl",
        "Madhukant220@axl"
    )

    @SuppressLint("HardwareIds")
    fun getAssignedMerchantUpi(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val saved = prefs.getString(KEY_ASSIGNED_UPI, null)
        if (!saved.isNullOrBlank() && saved in MERCHANT_UPI_POOL) {
            return saved
        }

        val androidId = runCatching {
            Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
        }.getOrNull()

        val deviceSeed = if (!androidId.isNullOrBlank() && androidId != "9774d56d682e549c") {
            androidId
        } else {
            UUID.randomUUID().toString()
        }

        val assigned = selectUpiForDeviceKey(deviceSeed)
        prefs.edit().putString(KEY_ASSIGNED_UPI, assigned).apply()
        return assigned
    }

    fun selectUpiForDeviceKey(deviceKey: String): String {
        val digest = MessageDigest.getInstance("SHA-256").digest(deviceKey.toByteArray(Charsets.UTF_8))
        val unsignedInt =
            ((digest[0].toLong() and 0xFFL) shl 24) or
            ((digest[1].toLong() and 0xFFL) shl 16) or
            ((digest[2].toLong() and 0xFFL) shl 8) or
            (digest[3].toLong() and 0xFFL)
        val index = (unsignedInt % MERCHANT_UPI_POOL.size).toInt()
        return MERCHANT_UPI_POOL[index]
    }
}
