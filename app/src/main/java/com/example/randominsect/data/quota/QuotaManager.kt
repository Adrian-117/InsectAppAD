package com.example.randominsect.data.quota

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import java.util.concurrent.TimeUnit
import com.example.randominsect.Config

class QuotaManager(context: Context) {

    private val prefs: SharedPreferences = 
        context.applicationContext.getSharedPreferences("quota_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val MAX_QUOTA = 5
        private const val KEY_REMAINING_QUOTA = "remaining_quota"
        private const val KEY_LAST_RECHARGE_TIME = "last_recharge_time"
        
        // 3 hours in milliseconds (10,800,000 ms)
        private val RECHARGE_INTERVAL_MS = TimeUnit.HOURS.toMillis(3) 
    }

    /**
     * Checks if a request is allowed and automatically consumes 1 quota unit if available.
     * Returns true if request can proceed, false if out of quota.
     */
    @Synchronized
    fun tryConsumeQuota(): Boolean {

        if (!Config.QuotaManagerActive) {
	  return true
	}
        


        Log.d("insectapp","Consuming quota,left we got: ${this.getRemainingQuotaInternal()}")
        refreshQuota() // Recalculate accrued quota based on elapsed time

        val currentQuota = getRemainingQuotaInternal()
        return if (currentQuota > 0) {
            setQuota(currentQuota - 1)
            true
        } else {
            false
        }
    }

    /**
     * Returns current available quota without consuming it (useful for UI display).
     */
    @Synchronized
    fun getAvailableQuota(): Int {
        refreshQuota()
        return getRemainingQuotaInternal()
    }

    /**
     * Calculates time remaining in milliseconds until the next quota point is recharged.
     * Returns 0 if already at MAX_QUOTA.
     */
    @Synchronized
    fun getTimeUntilNextRechargeMs(): Long {
        refreshQuota()
        if (getRemainingQuotaInternal() >= MAX_QUOTA) return 0L

        val lastRechargeTime = prefs.getLong(KEY_LAST_RECHARGE_TIME, System.currentTimeMillis())
        val elapsedTime = System.currentTimeMillis() - lastRechargeTime
        val remainder = elapsedTime % RECHARGE_INTERVAL_MS

        return RECHARGE_INTERVAL_MS - remainder
    }

    /**
     * Checks elapsed time since last recharge and adds earned quota points.
     */
    private fun refreshQuota() {
        var currentQuota = getRemainingQuotaInternal()
        val lastRechargeTime = prefs.getLong(KEY_LAST_RECHARGE_TIME, System.currentTimeMillis())
        val now = System.currentTimeMillis()

        if (currentQuota >= MAX_QUOTA) {
            // Reset recharge timer if full
            prefs.edit().putLong(KEY_LAST_RECHARGE_TIME, now).apply()
            return
        }

        val elapsedTime = now - lastRechargeTime
        val earnedQuota = (elapsedTime / RECHARGE_INTERVAL_MS).toInt()

        if (earnedQuota > 0) {
            val newQuota = (currentQuota + earnedQuota).coerceAtMost(MAX_QUOTA)
            // Advance lastRechargeTime by full 3-hour increments consumed
            val updatedRechargeTime = lastRechargeTime + (earnedQuota * RECHARGE_INTERVAL_MS)

            prefs.edit()
                .putInt(KEY_REMAINING_QUOTA, newQuota)
                .putLong(KEY_LAST_RECHARGE_TIME, if (newQuota == MAX_QUOTA) now else updatedRechargeTime)
                .apply()
        }
    }

    private fun getRemainingQuotaInternal(): Int {
        return prefs.getInt(KEY_REMAINING_QUOTA, MAX_QUOTA)
    }

    private fun setQuota(newQuota: Int) {
        val now = System.currentTimeMillis()
        val editor = prefs.edit().putInt(KEY_REMAINING_QUOTA, newQuota)
        
        // If dropping below MAX_QUOTA for the first time, start the recharge timer
        if (getRemainingQuotaInternal() == MAX_QUOTA && newQuota < MAX_QUOTA) {
            editor.putLong(KEY_LAST_RECHARGE_TIME, now)
        }
        
        editor.apply()
    }
}
