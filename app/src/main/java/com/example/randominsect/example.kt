package com.example.randominsect

import android.util.Log
import com.example.randominsect.data.api.InsectApi
import com.example.randominsect.data.api.InsectApiException
import com.example.randominsect.data.repository.BlacklistRepository
import com.example.randominsect.data.repository.FavoriteRepository
import com.example.randominsect.formatDuration
import kotlinx.coroutines.flow.first

suspend fun example() {
    try {
        val insect = InsectApi.getRandomInsect()
        Log.d("insectapp", "Insect $insect")

        insect?.let {
            FavoriteRepository.getInstance().insert(insect)
        }
    } catch (e: InsectApiException.QuotaExhaustedException) {
        Log.d("insectapp", "Quota limit exceeded ${formatDuration(e.remainingMs)} ")
    }

    val items = BlacklistRepository.getInstance().allBlacklistTaxa.first()
    items.forEach { taxon ->

        if (taxon?.id == 3L) {
            val updatedTaxon = taxon.copy(isBlacklisted = true)
            BlacklistRepository.getInstance().update(updatedTaxon)
        }

        Log.d("insectapp", "Taxon: ${taxon.id} - ${taxon.description} $taxon")
    }
}
