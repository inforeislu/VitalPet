package com.inforeis.vitalpet.services

import android.app.Activity
import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

enum class SubscriptionPlan(
    val productId: String,
    val titleKey: String,
    val priceKey: String,
    val priceValue: Double,
    val durationKey: String,
    val isTrialAvailable: Boolean = false
) {
    MONTHLY(
        productId = "vitalpet_pro_monthly",
        titleKey = "Plano Mensal",
        priceKey = "3,99 € / mês",
        priceValue = 3.99,
        durationKey = "Flexibilidade total, cancele quando desejar.",
        isTrialAvailable = false
    ),
    YEARLY(
        productId = "vitalpet_pro_yearly",
        titleKey = "Plano Anual",
        priceKey = "19,99 € / ano",
        priceValue = 19.99,
        durationKey = "Equivale a 1,66 € / mês",
        isTrialAvailable = true
    ),
    LIFETIME(
        productId = "vitalpet_pro_lifetime",
        titleKey = "Acesso Vitalício",
        priceKey = "49,99 €",
        priceValue = 49.99,
        durationKey = "Pagamento único, seu para sempre sem mensalidades.",
        isTrialAvailable = false
    )
}

object SubscriptionManager : PurchasesUpdatedListener {
    private const val TAG = "SubscriptionManager"
    private const val PREFS_NAME = "vitalpet_prefs"
    private const val KEY_IS_PREMIUM = "is_user_premium"
    private const val KEY_PLAN_ID = "active_subscription_plan"
    private const val KEY_EXPIRY_TIMESTAMP = "subscription_expiry_ts"

    private var prefs: SharedPreferences? = null
    private var billingClient: BillingClient? = null
    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    private val _isPremium = MutableStateFlow(false)
    val isPremium: StateFlow<Boolean> = _isPremium.asStateFlow()

    private val _currentPlan = MutableStateFlow<SubscriptionPlan?>(null)
    val currentPlan: StateFlow<SubscriptionPlan?> = _currentPlan.asStateFlow()

    private val _isBillingReady = MutableStateFlow(false)
    val isBillingReady: StateFlow<Boolean> = _isBillingReady.asStateFlow()

    private var activePurchaseCallback: ((Boolean, String?) -> Unit)? = null

    fun initialize(context: Context) {
        val sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs = sp
        val isPrem = sp.getBoolean(KEY_IS_PREMIUM, false)
        val planStr = sp.getString(KEY_PLAN_ID, null)

        _isPremium.value = isPrem
        _currentPlan.value = planStr?.let { id ->
            SubscriptionPlan.entries.find { it.productId == id }
        }

        // Initialize Google Play Billing Client 7.x
        try {
            billingClient = BillingClient.newBuilder(context.applicationContext)
                .setListener(this)
                .enablePendingPurchases(
                    PendingPurchasesParams.newBuilder()
                        .enableOneTimeProducts()
                        .build()
                )
                .build()

            connectBillingClient()
        } catch (e: Exception) {
            Log.e(TAG, "Erro ao inicializar BillingClient: ${e.message}")
        }
    }

    private fun connectBillingClient() {
        val client = billingClient ?: return
        if (client.isReady) return

        client.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    _isBillingReady.value = true
                    queryAvailableProducts()
                    queryActivePurchases()
                } else {
                    _isBillingReady.value = false
                    Log.w(TAG, "Setup do Billing falhou: ${billingResult.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                _isBillingReady.value = false
                Log.d(TAG, "Billing desconectado. Tentará reconectar na próxima operação.")
            }
        })
    }

    /**
     * Query product metadata and pricing from Google Play Console
     */
    private fun queryAvailableProducts() {
        val client = billingClient ?: return
        if (!client.isReady) return

        val productList = listOf(
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionPlan.MONTHLY.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionPlan.YEARLY.productId)
                .setProductType(BillingClient.ProductType.SUBS)
                .build(),
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(SubscriptionPlan.LIFETIME.productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        )

        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        client.queryProductDetailsAsync(params) { billingResult, queryProductDetailsResult ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (details in queryProductDetailsResult.productDetailsList) {
                    productDetailsMap[details.productId] = details
                    Log.d(TAG, "Produto carregado: ${details.productId} (${details.title})")
                }
            }
        }
    }

    /**
     * Queries active subscriptions and in-app purchases on startup
     */
    fun queryActivePurchases() {
        val client = billingClient ?: return
        if (!client.isReady) return

        // 1. Check Subscriptions
        val subsParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.SUBS)
            .build()

        client.queryPurchasesAsync(subsParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    processPurchase(purchase)
                }
            }
        }

        // 2. Check In-App (Lifetime)
        val inappParams = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()

        client.queryPurchasesAsync(inappParams) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                for (purchase in purchases) {
                    processPurchase(purchase)
                }
            }
        }
    }

    /**
     * Executes purchase flow via Google Play Billing
     */
    fun purchase(
        plan: SubscriptionPlan,
        activity: Activity? = null,
        onResult: (Boolean, String?) -> Unit
    ) {
        val client = billingClient
        val details = productDetailsMap[plan.productId]

        if (client != null && client.isReady && activity != null && details != null) {
            activePurchaseCallback = onResult

            val productDetailsParamsList = if (details.productType == BillingClient.ProductType.SUBS) {
                val offerToken = details.subscriptionOfferDetails?.firstOrNull()?.offerToken.orEmpty()
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .setOfferToken(offerToken)
                        .build()
                )
            } else {
                listOf(
                    BillingFlowParams.ProductDetailsParams.newBuilder()
                        .setProductDetails(details)
                        .build()
                )
            }

            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()

            val responseCode = client.launchBillingFlow(activity, flowParams).responseCode
            if (responseCode != BillingClient.BillingResponseCode.OK) {
                activePurchaseCallback = null
                onResult(false, "Erro ao iniciar compra: código $responseCode")
            }
        } else {
            // Billing not ready (client not connected or product details not yet loaded)
            // Do NOT activate plan locally — fail gracefully so the user retries
            val reason = when {
                client == null -> "Serviço de pagamento não inicializado."
                !client.isReady -> "Serviço de pagamento não disponível. Tente novamente."
                activity == null -> "Contexto inválido para iniciar compra."
                else -> "Detalhes do produto ainda a carregar. Aguarde e tente novamente."
            }
            onResult(false, reason)
        }
    }

    /**
     * Listener callback when purchases are updated by Google Play
     */
    override fun onPurchasesUpdated(billingResult: BillingResult, purchases: List<Purchase>?) {
        val callback = activePurchaseCallback
        activePurchaseCallback = null

        when (billingResult.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                if (purchases != null) {
                    for (purchase in purchases) {
                        processPurchase(purchase)
                    }
                    callback?.invoke(true, null)
                } else {
                    callback?.invoke(false, "Nenhuma compra retornada.")
                }
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                callback?.invoke(false, "Compra cancelada pelo utilizador.")
            }
            else -> {
                callback?.invoke(false, "Erro na compra: ${billingResult.debugMessage}")
            }
        }
    }

    private fun processPurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            // Acknowledge if not already acknowledged
            if (!purchase.isAcknowledged) {
                val ackParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()

                billingClient?.acknowledgePurchase(ackParams) { ackResult ->
                    if (ackResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Compra confirmada (acknowledged) com sucesso.")
                    }
                }
            }

            // Identify plan and activate
            val plan = SubscriptionPlan.entries.find { it.productId in purchase.products }
                ?: SubscriptionPlan.YEARLY

            activatePlanLocally(plan)
        }
    }

    private fun activatePlanLocally(plan: SubscriptionPlan) {
        prefs?.let { sp ->
            sp.edit()
                .putBoolean(KEY_IS_PREMIUM, true)
                .putString(KEY_PLAN_ID, plan.productId)
                .apply()
        }
        _isPremium.value = true
        _currentPlan.value = plan
    }

    /**
     * Restores previous purchases directly from Google Play Billing
     */
    fun restorePurchases(onResult: (Boolean, Boolean) -> Unit) {
        val client = billingClient
        if (client != null && client.isReady) {
            var foundAny = false

            val subsParams = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            client.queryPurchasesAsync(subsParams) { resSubs, purchasesSubs ->
                if (resSubs.responseCode == BillingClient.BillingResponseCode.OK) {
                    for (p in purchasesSubs) {
                        if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
                            processPurchase(p)
                            foundAny = true
                        }
                    }
                }

                val inappParams = QueryPurchasesParams.newBuilder()
                    .setProductType(BillingClient.ProductType.INAPP)
                    .build()

                client.queryPurchasesAsync(inappParams) { resInapp, purchasesInapp ->
                    if (resInapp.responseCode == BillingClient.BillingResponseCode.OK) {
                        for (p in purchasesInapp) {
                            if (p.purchaseState == Purchase.PurchaseState.PURCHASED) {
                                processPurchase(p)
                                foundAny = true
                            }
                        }
                    }

                    if (!foundAny) {
                        // Check local backup
                        val localPrem = prefs?.getBoolean(KEY_IS_PREMIUM, false) == true
                        if (localPrem) {
                            val planStr = prefs?.getString(KEY_PLAN_ID, null)
                            val plan = SubscriptionPlan.entries.find { it.productId == planStr }
                            _isPremium.value = true
                            _currentPlan.value = plan
                            onResult(true, true)
                            return@queryPurchasesAsync
                        }
                    }

                    onResult(true, foundAny)
                }
            }
        } else {
            // Local fallback
            val sp = prefs
            if (sp != null) {
                val wasPremium = sp.getBoolean(KEY_IS_PREMIUM, false)
                if (wasPremium) {
                    _isPremium.value = true
                    val planStr = sp.getString(KEY_PLAN_ID, null)
                    _currentPlan.value = planStr?.let { id ->
                        SubscriptionPlan.entries.find { it.productId == id }
                    }
                    onResult(true, true)
                    return
                }
            }
            onResult(true, false)
        }
    }

    /**
     * Resets subscription (for testing / demo purposes)
     */
    fun resetSubscriptionForTesting() {
        prefs?.edit()
            ?.putBoolean(KEY_IS_PREMIUM, false)
            ?.remove(KEY_PLAN_ID)
            ?.remove(KEY_EXPIRY_TIMESTAMP)
            ?.apply()
        _isPremium.value = false
        _currentPlan.value = null
    }
}
