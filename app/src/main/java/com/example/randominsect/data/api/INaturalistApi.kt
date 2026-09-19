package com.example.randominsect.data.api

import android.content.Context
import android.util.Log
import androidx.compose.ui.platform.LocalContext
import com.example.randominsect.data.db.BlackList.BlacklistDao
import com.example.randominsect.data.db.FavoriteInsect.InsectEntity
import com.example.randominsect.data.db.InsectDatabase
import com.example.randominsect.data.quota.QuotaManager
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlinx.serialization.json.longOrNull
import java.io.File

object InsectApi {
    private lateinit var appContext: Context
    private lateinit var blacklistDao: BlacklistDao
    private lateinit var client: HttpClient
    private lateinit var quotaManager: QuotaManager

    @Volatile
    private var isInitialized = false

    /**
     * Call this ONCE in your Application class onCreate()
     */
    fun init(
        context: Context,
        blacklistDao: BlacklistDao,
        client: HttpClient,
    ) {
        if (!isInitialized) {
            synchronized(this) {
                if (!isInitialized) {
                    // Force applicationContext to prevent leaking Activity instances
                    this.appContext = context.applicationContext
                    this.blacklistDao = blacklistDao
                    this.client = client
                    this.isInitialized = true
		    this.quotaManager = QuotaManager(this.appContext) // Init QuotaManager
                }
            }
        }
    }

    suspend fun getRandomInsect(): InsectEntity? {
      val canMakeRequest = quotaManager.tryConsumeQuota()
	      if (!canMakeRequest) {
		  val timeUntilNextRecharge = quotaManager.getTimeUntilNextRechargeMs()
		  throw InsectApiException.QuotaExhaustedException(remainingMs = timeUntilNextRecharge)
	      }


        val randomPage = (1..1000).random()
	val insects_to_exclude = blacklistDao.getBlacklistQueryString() ?: ""
        Log.d("insectapp","insects to exclude are $insects_to_exclude")

        val rootJson: JsonObject
        try{

	  rootJson=
            client
                .get("https://api.inaturalist.org/v1/observations") {
                    parameter("iconic_taxa", "Insecta")
                    parameter("per_page", 1)
                    parameter("page", randomPage)
                    parameter("photos", "true")
                    parameter("quality_grade", "research")
                    parameter("without_taxon_id",insects_to_exclude )
                    parameter(
                        "fields",
                        "(taxon:(name:!t,preferred_common_name:!t,wikipedia_url:!t,default_photo:(medium_url:!t)))",
                    )
                }.body()

	      } catch (e: Exception) {
              throw InsectApiException.NetworkException(e)
          }

        val result = rootJson["results"]?.jsonArray?.firstOrNull()?.jsonObject ?: return null
        val taxon = result["taxon"]?.jsonObject

        val id = result["id"]?.jsonPrimitive?.longOrNull ?: 0L
        val commonName = taxon?.get("preferred_common_name")?.jsonPrimitive?.content
        val wikipediaUrl = taxon?.get("wikipedia_url")?.jsonPrimitive?.content
        val scientificName = taxon?.get("name")?.jsonPrimitive?.content
        val mediumUrl =
            taxon
                ?.get("default_photo")
                ?.jsonObject
                ?.get("medium_url")
                ?.jsonPrimitive
                ?.content

        // Download image to local file if URL exists
        var localImageFile: File? = null
        var imagePath: String? = null

        if (mediumUrl != null) {

            localImageFile = File(this.appContext?.cacheDir, "insect_$id.jpg")

            val bytes: ByteArray = client.get(mediumUrl).body()
            localImageFile.writeBytes(bytes)

            imagePath = localImageFile.absolutePath
        }

        return InsectEntity(
            id = id,
            commonName = commonName,
            scientificName = scientificName,
            wikipedia_url = wikipediaUrl,
            image_path = imagePath,
        )
    }
}
