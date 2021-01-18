package com.sasa.mskrpoject

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.webkit.WebSettings
import android.widget.Toast
import bolts.AppLinks
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.AppsFlyerLibCore.LOG_TAG
import com.facebook.FacebookSdk
import com.facebook.appevents.AppEventsConstants
import com.facebook.appevents.AppEventsLogger
import com.facebook.applinks.AppLinkData
import com.onesignal.OneSignal
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.util.*


class MainActivity : AppCompatActivity() {
    private lateinit var okHttpClient : OkHttpClient
    private lateinit var preferences: SharedPreferences
    private lateinit var finalUrl: String
    var check = Check()
    private val handler = Handler(Looper.getMainLooper())
    private val conversionTask = object : Runnable {
        override fun run() {
            GlobalScope.launch {
                val json = getConversion()
                val eventName = "event"
                val valueName = "value"
                if (json.has(eventName)) {
                    val value =
                        json.optString(valueName) ?: " " // при пустом value отправляем пробел
                    sendOnesignalEvent(json.optString(eventName), value)
                    sendFacebookEvent(json.optString(eventName), value)
                    sendAppsflyerEvent(json.optString(eventName), value)
                }
            }
            handler.postDelayed(this, 15000)
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        preferences = this.getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
        check.check(this)
        val deeplink = preferences.getString("PREFS_DEEPLINK", null)
        initOkHttpClient()
        apsFlyer()
        if (deeplink == null) {
            // Если диплинка в хранилище нет, берём из фейсбук коллбека
            getDeeplinkFromFacebook()
        } else {
            // Иначе начинаем обработку диплинка
            processDeeplinkAndStart(deeplink)
        }
        processDeeplinkAndStart(deeplink ?: "")

    }
    private fun initOkHttpClient() {
        okHttpClient = OkHttpClient.Builder()
            .followSslRedirects(false)
            .followRedirects(false)
            .addNetworkInterceptor {
                it.proceed(
                    it.request().newBuilder()
                        .header("User-Agent", WebSettings.getDefaultUserAgent(this))
                        .build()
                )
            }.build()

    }
    fun getConversion(): JSONObject {
        val conversionUrl = "https://freerun.site/conversion.php"
        return try {
            val response = okHttpClient // Делаем запрос, добавив к ссылке click_id
                .newCall(Request.Builder().url("$conversionUrl?click_id=${getClickId()}").build())
                .execute()
            JSONObject(response.body?.string() ?: "{}")
        } catch (ex: Exception) {
            JSONObject("{}")
        }
    }

    private fun getDeeplinkFromFacebook() {
        FacebookSdk.setAutoInitEnabled(true)
        FacebookSdk.fullyInitialize()
        AppLinkData.fetchDeferredAppLinkData(applicationContext) { appLinkData ->
            val uri: Uri? =
                appLinkData?.targetUri ?: AppLinks.getTargetUrlFromInboundIntent(this, intent)

            // В переменную uri записывается отложенный диплинк appLinkData.targetUri
            // Если он равен null, то диплинк берется из интента (getTargetUrlFromInboundIntent)
            if (uri != null && uri.query != null) {
                processDeeplinkAndStart(uri.query!!) // передаем параметры диплинка дальше и обрабатываем
                preferences.edit().putString("PREFS_DEEPLINK", uri.query!!).apply()
            } else {
                processDeeplinkAndStart("") // передаем пустую строку в метод обработки диплинка
            }

        }
    }

    private fun getClickId(): String {
        // Пробуем получить click_id из хранилища
        // Если его там нет, получим null
        var clickId = preferences.getString("PREFS_CLICK_ID", null)
        if (clickId == null) {
            // в случае если в хранилище нет click_id, генерируем новый
            clickId = UUID.randomUUID().toString()
            preferences.edit().putString("PREFS_CLICK_ID", clickId)
                .apply() // и сохраняем в хранилище
        }
        return clickId
    }
    private fun apsFlyer(){
        val devKey = "qrdZGj123456789"; // ЗДЕСЬ ДОЛЖЕН БЫТЬ ВАШ КЛЮЧ, ПОЛУЧЕННЫЙ ИЗ APPSFLYER !!!
        val conversionDataListener  = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                data?.let { cvData ->
                    cvData.map {
                        Log.i(LOG_TAG, "conversion_attribute:  ${it.key} = ${it.value}")
                    }
                }
            }

            override fun onConversionDataFail(error: String?) {
                Log.e(LOG_TAG, "error onAttributionFailure :  $error")
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                data?.map {
                    Log.d(LOG_TAG, "onAppOpen_attribute: ${it.key} = ${it.value}")
                }
            }

            override fun onAttributionFailure(error: String?) {
                Log.e(LOG_TAG, "error onAttributionFailure :  $error")
            }
        }

        AppsFlyerLib.getInstance().init(devKey, conversionDataListener, this)
        AppsFlyerLib.getInstance().startTracking(this)

    }
    fun sendOnesignalEvent(key: String, value: String) {
        OneSignal.sendTag(key, value)
    }

    // Отправка события в Facebook
    fun sendFacebookEvent(key: String, value: String) {
        val fb = AppEventsLogger.newLogger(this)

        val bundle = Bundle()
        when (key) {
            "reg" -> {
                bundle.putString(AppEventsConstants.EVENT_PARAM_CONTENT, value)
                fb.logEvent(AppEventsConstants.EVENT_NAME_COMPLETED_REGISTRATION, bundle)
            }
            "dep" -> {
                bundle.putString(AppEventsConstants.EVENT_PARAM_CONTENT, value)
                fb.logEvent(AppEventsConstants.EVENT_NAME_ADDED_TO_CART, bundle)
            }
        }
    }

    // Отправка события в Appsflyer
    private fun sendAppsflyerEvent(key: String, value: String) {
        val values = HashMap<String, Any>()
        values[key] = value
        AppsFlyerLib.getInstance().trackEvent(this, key, values)
    }

    private fun processDeeplinkAndStart(deeplink: String) {
        // Данная ссылка лишь пример
        val trackingUrl = "https://tracksystem.com/index.php?key=abgirgqhar8i4hrghqui34h"

        val clickId = getClickId()
        val sourceId = BuildConfig.APPLICATION_ID
        finalUrl = trackingUrl + "&" + "click_id=" + clickId + "&" + "source=" + sourceId
        if (!deeplink.isBlank()) {
            finalUrl = finalUrl + "&" + deeplink
        }

        // Инициализируем весь процесс с помощью корутин для удобства обращения к сети
        GlobalScope.launch(Dispatchers.Main) {
            val isBot = withContext(Dispatchers.IO) {
                check.check(applicationContext) }
            if (isBot) { // Если бот открываем игру-заглушку
                // GameActivity::class.java - пример активити с заглушкой
//                startActivity(Intent(this@MainActivity, GameActivity::class.java))
//                finish()
                Toast.makeText(applicationContext, "Game", Toast.LENGTH_LONG).show()
            } else {
                handler.post(conversionTask) // Запускаем проверку конверсии по таймеру (Пункт 6)
//                myWebView.loadUrl(finalUrl) // Открываем вебвью с сформированной ссылкой
//                progressBar.visibility = View.GONE
//                myWebView.visibility = View.VISIBLE
    Toast.makeText(applicationContext, "web", Toast.LENGTH_LONG).show()
                // дополнительные отправки событий в Onesignal

                // Отправка тега "nobot" со значением "1" в OneSignal
                OneSignal.sendTag("nobot", "1")

                // Отправка бандла приложения
                OneSignal.sendTag("bundle", BuildConfig.APPLICATION_ID)

                // Отправка параметра stream из диплинка
                var streamId = Uri.parse("?" + deeplink).getQueryParameter("stream")
                if (!streamId.isNullOrBlank()) { // если stream не пустой, отправляем
                    OneSignal.sendTag("stream", streamId)
                }
            }
        }
    }
}
