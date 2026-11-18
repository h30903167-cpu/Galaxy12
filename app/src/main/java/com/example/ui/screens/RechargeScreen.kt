package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MerchantUpiProvider
import com.example.model.RechargeRecord
import com.example.ui.theme.*
import com.example.viewmodel.DasaniViewModel

enum class PaymentMethodType(
    val id: String,
    val title: String,
    val subtitle: String,
    val badge: String,
    val isRecommended: Boolean = false
) {
    UPI_FASTPAY(
        id = "upi",
        title = "UPI FastPay (PhonePe / GPay / Paytm)",
        subtitle = "Instant credit in 10-30 seconds • 0% Fee",
        badge = "FASTEST",
        isRecommended = true
    ),
    DYNAMIC_QR(
        id = "qr",
        title = "Dynamic Scan & Pay QR",
        subtitle = "Scan with any banking or UPI app",
        badge = "AUTO VERIFY"
    ),
    BANK_IMPS_RTGS(
        id = "bank",
        title = "Direct Bank IMPS / RTGS Transfer",
        subtitle = "Recommended for high amounts up to ₹1,00,000",
        badge = "HIGH LIMIT"
    ),
    CARD_NETBANKING(
        id = "card",
        title = "Net Banking & Debit Card Gateway",
        subtitle = "Secure 256-Bit encrypted bank gateway",
        badge = "SECURE"
    ),
    INSTANT_SANDBOX(
        id = "sandbox",
        title = "⚡ Instant Sandbox Demo Credit",
        subtitle = "One-click test credit for immediate trial",
        badge = "TEST MODE"
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RechargeScreen(
    viewModel: DasaniViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val userAccount by viewModel.userAccount.collectAsState()
    val rechargeHistory by viewModel.rechargeHistory.collectAsState()

    var selectedAmount by remember { mutableStateOf(300.0) }
    var customAmountText by remember { mutableStateOf("300") }
    var selectedMethod by remember { mutableStateOf(PaymentMethodType.UPI_FASTPAY) }
    var utrInput by remember { mutableStateOf("") }
    var activeTab by remember { mutableStateOf(0) } // 0: Recharge, 1: Records
    var showConfirmDialog by remember { mutableStateOf(false) }
    var amountErrorMessage by remember { mutableStateOf<String?>(null) }

    // Maximum recharge limit up to 1 Lakh (100,000)
    val maxRechargeLimit = 100000.0
    val minRechargeLimit = 100.0

    // Preset amounts starting with ₹300, ₹500, up to 1 Lakh (₹1,00,000)
    val presetAmounts = listOf(
        300.0,
        500.0,
        1800.0,
        4900.0,
        12000.0,
        35000.0,
        50000.0,
        100000.0 // 1 Lakh (Maximum transaction limit)
    )

    val officialUpiId = remember(context) { MerchantUpiProvider.getAssignedMerchantUpi(context) }
    val merchantBankName = "HDFC Bank Ltd."
    val merchantAccountNo = "50200084920194"
    val merchantIfsc = "HDFC0001234"
    val merchantHolder = "Galaxy Agriculture Equipment Ltd."

    fun validateAmount(text: String): Double {
        val num = text.toDoubleOrNull() ?: 0.0
        amountErrorMessage = when {
            num > maxRechargeLimit -> "Maximum single recharge limit is ₹1,00,000 (1 Lakh)"
            num in 1.0..99.0 -> "Minimum recharge amount is ₹100"
            else -> null
        }
        return num
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Secure Recharge", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 18.sp)
                            Spacer(modifier = Modifier.width(8.dp))
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = Color(0xFF00E676).copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = Color(0xFF00E676),
                                        modifier = Modifier.size(10.dp)
                                    )
                                    Spacer(modifier = Modifier.width(3.dp))
                                    Text("256-Bit SSL", color = Color(0xFF00E676), fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Text(
                            text = "Limits: ₹100 up to ₹1,00,000 (1 Lakh)",
                            color = DasaniTextSecondary,
                            fontSize = 11.sp
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF0E1F1E))
            )
        },
        containerColor = DasaniBackground
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Tab Selector: Recharge Now | Recharge Records
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF0F1E1D))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TabPill(
                    title = "Recharge Now",
                    selected = activeTab == 0,
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = 0 }
                )
                TabPill(
                    title = "Recharge Records",
                    selected = activeTab == 1,
                    modifier = Modifier.weight(1f),
                    onClick = { activeTab = 1 }
                )
            }

            if (activeTab == 0) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(top = 12.dp, bottom = 40.dp)
                ) {
                    // Security Guarantee Banner
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF102826)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.3f))
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(36.dp)
                                        .background(DasaniPrimary.copy(alpha = 0.15f), CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Shield,
                                        contentDescription = null,
                                        tint = DasaniPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = "NPCI & Bank Verified Payment Gateway",
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Official merchant account • 100% automated credit with UTR",
                                        color = DasaniTextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(14.dp))
                    }

                    // Current Wallet Balance Card
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF132826)),
                            elevation = CardDefaults.cardElevation(4.dp)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("Current Wallet Balance", color = DasaniTextSecondary, fontSize = 12.sp)
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "₹ ${"%,.2f".format(userAccount.walletBalance)}",
                                        color = DasaniPrimary,
                                        fontSize = 24.sp,
                                        fontWeight = FontWeight.ExtraBold
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = Color(0xFF1A3834)
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.VerifiedUser, contentDescription = null, tint = DasaniSuccess, modifier = Modifier.size(14.dp))
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text("Active VIP", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Section: Amount Selection up to 1 Lakh
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Select Recharge Amount",
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Max: ₹1,00,000",
                                color = DasaniAccentGold,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        // Preset chips grid
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            // Row 1: 300, 500, 1800
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(300.0, 500.0, 1800.0).forEach { amt ->
                                    AmountPresetButton(
                                        amount = amt,
                                        isSelected = selectedAmount == amt,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            selectedAmount = amt
                                            customAmountText = amt.toLong().toString()
                                            validateAmount(customAmountText)
                                        }
                                    )
                                }
                            }

                            // Row 2: 4900, 12000, 35000
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf(4900.0, 12000.0, 35000.0).forEach { amt ->
                                    AmountPresetButton(
                                        amount = amt,
                                        isSelected = selectedAmount == amt,
                                        modifier = Modifier.weight(1f),
                                        onClick = {
                                            selectedAmount = amt
                                            customAmountText = amt.toLong().toString()
                                            validateAmount(customAmountText)
                                        }
                                    )
                                }
                            }

                            // Row 3: Full-width Highlighted ₹1,00,000 (1 Lakh) Button
                            val is1Lakh = selectedAmount == 100000.0
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(58.dp)
                                    .clickable {
                                        selectedAmount = 100000.0
                                        customAmountText = "100000"
                                        validateAmount(customAmountText)
                                    }
                                    .testTag("recharge_amt_100000"),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(
                                    containerColor = if (is1Lakh) Color(0xFF2E2606) else Color(0xFF142423)
                                ),
                                border = androidx.compose.foundation.BorderStroke(
                                    width = if (is1Lakh) 2.dp else 1.dp,
                                    color = if (is1Lakh) DasaniAccentGold else DasaniAccentGold.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .padding(horizontal = 16.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Stars,
                                            contentDescription = null,
                                            tint = DasaniAccentGold,
                                            modifier = Modifier.size(24.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Column {
                                            Text(
                                                text = "₹ 1,00,000 (1 Lakh Max Limit)",
                                                color = DasaniAccentGold,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.ExtraBold
                                            )
                                            Text(
                                                text = "Maximum return rate • VIP Machine Tier",
                                                color = DasaniTextSecondary,
                                                fontSize = 11.sp
                                            )
                                        }
                                    }

                                    if (is1Lakh) {
                                        Icon(
                                            imageVector = Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = DasaniAccentGold,
                                            modifier = Modifier.size(22.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Custom Amount TextField with dynamic validation up to 1 Lakh
                        OutlinedTextField(
                            value = customAmountText,
                            onValueChange = { input ->
                                val digitsOnly = input.filter { it.isDigit() }
                                customAmountText = digitsOnly
                                val parsed = validateAmount(digitsOnly)
                                if (parsed <= maxRechargeLimit) {
                                    selectedAmount = parsed
                                }
                            },
                            label = { Text("Enter Custom Amount (Up to ₹1,00,000)") },
                            prefix = { Text("₹ ", color = DasaniPrimary, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            isError = amountErrorMessage != null,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF142423),
                                unfocusedContainerColor = Color(0xFF142423),
                                focusedBorderColor = DasaniPrimary,
                                unfocusedBorderColor = DasaniCardBorder,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White,
                                errorBorderColor = DasaniError
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("custom_recharge_input")
                        )

                        amountErrorMessage?.let { err ->
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = err,
                                color = DasaniError,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(start = 4.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                    }

                    // Section: Select Payment Method
                    item {
                        Text(
                            text = "Select Payment Method",
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PaymentMethodType.values().forEach { method ->
                                PaymentMethodCard(
                                    method = method,
                                    isSelected = selectedMethod == method,
                                    onSelect = { selectedMethod = method }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }

                    // Dynamic Payment Gateway Details Section
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF10201F)),
                            border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.4f))
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Payment Checkout Details",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Surface(
                                        shape = RoundedCornerShape(6.dp),
                                        color = DasaniPrimary.copy(alpha = 0.15f)
                                    ) {
                                        Text(
                                            text = "Recharge: ₹${"%,d".format(selectedAmount.toLong())}",
                                            color = DasaniPrimary,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Bold,
                                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                when (selectedMethod) {
                                    PaymentMethodType.UPI_FASTPAY, PaymentMethodType.DYNAMIC_QR -> {
                                        // Registered UPI ID Banner
                                        Card(
                                            modifier = Modifier.fillMaxWidth(),
                                            shape = RoundedCornerShape(12.dp),
                                            colors = CardDefaults.cardColors(containerColor = Color(0xFF142B24)),
                                            border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.5f))
                                        ) {
                                            Row(
                                                modifier = Modifier.padding(12.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Icon(Icons.Default.Verified, contentDescription = null, tint = DasaniPrimary, modifier = Modifier.size(20.dp))
                                                Spacer(modifier = Modifier.width(8.dp))
                                                Column(modifier = Modifier.weight(1f)) {
                                                    Text(
                                                        text = "Paying from Registered UPI",
                                                        color = DasaniTextSecondary,
                                                        fontSize = 11.sp
                                                    )
                                                    Text(
                                                        text = userAccount.boundUpiId,
                                                        color = Color.White,
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold
                                                    )
                                                }
                                                Surface(
                                                    shape = RoundedCornerShape(6.dp),
                                                    color = DasaniPrimary.copy(alpha = 0.2f)
                                                ) {
                                                    Text(
                                                        text = "AUTO LINKED",
                                                        color = DasaniPrimary,
                                                        fontSize = 10.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(10.dp))

                                        // 1-Click Instant Deposit Button
                                        Button(
                                            onClick = {
                                                val autoUtr = "${(100000000000L..999999999999L).random()}"
                                                viewModel.recharge(
                                                    amount = selectedAmount,
                                                    channel = "Direct UPI (${userAccount.boundUpiId})",
                                                    utr = autoUtr
                                                )
                                            },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(48.dp)
                                                .testTag("instant_upi_pay_button"),
                                            shape = RoundedCornerShape(24.dp),
                                            colors = ButtonDefaults.buttonColors(
                                                containerColor = DasaniPrimary,
                                                contentColor = Color(0xFF041C1A)
                                            )
                                        ) {
                                            Icon(Icons.Default.Bolt, contentDescription = null, modifier = Modifier.size(20.dp))
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Text(
                                                text = "⚡ Instant Pay ₹${selectedAmount.toInt()} via ${userAccount.boundUpiId}",
                                                fontSize = 13.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // UPI Apps Quick Launcher (PhonePe, GPay, Paytm, BHIM)
                                        Text(
                                            text = "Or Pay with UPI App:",
                                            color = Color.White,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            listOf("PhonePe", "GPay", "Paytm", "BHIM").forEach { appName ->
                                                OutlinedButton(
                                                    onClick = {
                                                        val upiUri = "upi://pay?pa=$officialUpiId&pn=GalaxyAgriculture&am=${selectedAmount.toLong()}&cu=INR"
                                                        try {
                                                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(upiUri))
                                                            context.startActivity(intent)
                                                        } catch (e: Exception) {
                                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                            clipboard.setPrimaryClip(ClipData.newPlainText("Galaxy UPI", officialUpiId))
                                                            viewModel.showToast("Merchant UPI copied: $officialUpiId")
                                                        }
                                                    },
                                                    modifier = Modifier.weight(1f).height(36.dp),
                                                    shape = RoundedCornerShape(8.dp),
                                                    border = androidx.compose.foundation.BorderStroke(1.dp, DasaniPrimary.copy(alpha = 0.5f)),
                                                    contentPadding = PaddingValues(horizontal = 2.dp, vertical = 2.dp)
                                                ) {
                                                    Text(appName, fontSize = 11.sp, color = Color.White, fontWeight = FontWeight.Bold)
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))

                                        // Official Merchant UPI ID Copy Box
                                        Text(
                                            text = "Official Merchant UPI Details:",
                                            color = DasaniPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Row(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF182D2B), RoundedCornerShape(10.dp))
                                                .padding(horizontal = 12.dp, vertical = 10.dp),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text("Verified Merchant UPI ID (This Phone)", color = DasaniTextSecondary, fontSize = 11.sp)
                                                Text(
                                                    text = officialUpiId,
                                                    color = Color.White,
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 15.sp,
                                                    modifier = Modifier.testTag("merchant_upi_id_text")
                                                )
                                            }

                                            Button(
                                                onClick = {
                                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                                    clipboard.setPrimaryClip(ClipData.newPlainText("Galaxy UPI", officialUpiId))
                                                    viewModel.showToast("Merchant UPI copied: $officialUpiId")
                                                },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = DasaniPrimary,
                                                    contentColor = Color(0xFF041C1A)
                                                ),
                                                shape = RoundedCornerShape(14.dp),
                                                modifier = Modifier.height(32.dp),
                                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 2.dp)
                                            ) {
                                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(14.dp))
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("Copy UPI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                            }
                                        }

                                        if (selectedMethod == PaymentMethodType.DYNAMIC_QR) {
                                            Spacer(modifier = Modifier.height(12.dp))
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .background(Color.White, RoundedCornerShape(12.dp))
                                                    .padding(14.dp),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                    Icon(
                                                        imageVector = Icons.Default.QrCode2,
                                                        contentDescription = "Dynamic UPI QR",
                                                        tint = Color.Black,
                                                        modifier = Modifier.size(140.dp)
                                                    )
                                                    Text(
                                                        text = "Scan to Pay ₹${"%,d".format(selectedAmount.toLong())}",
                                                        color = Color.Black,
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 13.sp
                                                    )
                                                    Text(
                                                        text = "UPI ID: $officialUpiId",
                                                        color = Color(0xFF00695C),
                                                        fontWeight = FontWeight.Bold,
                                                        fontSize = 12.sp
                                                    )
                                                    Text(
                                                        text = "Works with GPay, PhonePe, Paytm & BHIM",
                                                        color = Color.Gray,
                                                        fontSize = 11.sp
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(14.dp))
                                        Text(
                                            text = "Step 2: Enter 12-Digit Bank UTR / Reference No.",
                                            color = DasaniPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))

                                        OutlinedTextField(
                                            value = utrInput,
                                            onValueChange = { utrInput = it.take(12) },
                                            placeholder = { Text("Enter 12-Digit UTR (e.g. 431289456721)", color = Color(0xFF6B7D82), fontSize = 13.sp) },
                                            singleLine = true,
                                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = Color(0xFF182D2B),
                                                unfocusedContainerColor = Color(0xFF182D2B),
                                                focusedBorderColor = DasaniPrimary,
                                                unfocusedBorderColor = DasaniCardBorder,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .testTag("utr_input")
                                        )
                                    }

                                    PaymentMethodType.BANK_IMPS_RTGS -> {
                                        Text(
                                            text = "Official Bank Account Details (₹50k - ₹1 Lakh)",
                                            color = DasaniPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(8.dp))

                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF182D2B), RoundedCornerShape(10.dp))
                                                .padding(12.dp),
                                            verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                            BankDetailRow("Beneficiary Name", merchantHolder, context)
                                            BankDetailRow("Bank Name", merchantBankName, context)
                                            BankDetailRow("Account Number", merchantAccountNo, context)
                                            BankDetailRow("IFSC Code", merchantIfsc, context)
                                        }

                                        Spacer(modifier = Modifier.height(12.dp))
                                        Text(
                                            text = "Enter IMPS / RTGS Transaction Reference No.",
                                            color = DasaniPrimary,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Spacer(modifier = Modifier.height(6.dp))
                                        OutlinedTextField(
                                            value = utrInput,
                                            onValueChange = { utrInput = it },
                                            placeholder = { Text("Bank Reference / UTR Number", color = Color(0xFF6B7D82), fontSize = 13.sp) },
                                            singleLine = true,
                                            shape = RoundedCornerShape(10.dp),
                                            colors = OutlinedTextFieldDefaults.colors(
                                                focusedContainerColor = Color(0xFF182D2B),
                                                unfocusedContainerColor = Color(0xFF182D2B),
                                                focusedBorderColor = DasaniPrimary,
                                                unfocusedBorderColor = DasaniCardBorder,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                            ),
                                            modifier = Modifier.fillMaxWidth()
                                        )
                                    }

                                    PaymentMethodType.CARD_NETBANKING -> {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF182D2B), RoundedCornerShape(10.dp))
                                                .padding(14.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Icon(Icons.Default.CreditCard, contentDescription = null, tint = DasaniPrimary, modifier = Modifier.size(36.dp))
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = "Direct Bank Payment Gateway",
                                                color = Color.White,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = "You will be securely redirected to your bank's OTP authorization page.",
                                                color = DasaniTextSecondary,
                                                fontSize = 12.sp,
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                    PaymentMethodType.INSTANT_SANDBOX -> {
                                        Column(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .background(Color(0xFF26210A), RoundedCornerShape(10.dp))
                                                .border(1.dp, DasaniAccentGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                                .padding(14.dp)
                                        ) {
                                            Text(
                                                text = "⚡ Instant Testing Sandbox Mode",
                                                color = DasaniAccentGold,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 13.sp
                                            )
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Text(
                                                text = "Credits ₹${"%,d".format(selectedAmount.toLong())} immediately to test purchasing Product A, B, C or VIP plans without real money transfer.",
                                                color = Color.White,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(18.dp))

                                // Main Recharge Action Button
                                Button(
                                    onClick = {
                                        if (selectedAmount < minRechargeLimit || selectedAmount > maxRechargeLimit) {
                                            validateAmount(customAmountText)
                                            return@Button
                                        }
                                        showConfirmDialog = true
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(50.dp)
                                        .testTag("submit_recharge_button"),
                                    shape = RoundedCornerShape(25.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = DasaniPrimary,
                                        contentColor = Color(0xFF041C1A)
                                    )
                                ) {
                                    Icon(Icons.Default.Lock, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "Secure Pay ₹${"%,d".format(selectedAmount.toLong())}",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        // Quick 1-Click Sandbox Test Shortcuts
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = Color(0xFF142423))
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text(
                                    text = "⚡ 1-Click Fast Sandbox Testing",
                                    color = DasaniPrimary,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Button(
                                        onClick = {
                                            viewModel.recharge(550.0, "Demo FastPay", "UTR550${System.currentTimeMillis() % 10000}")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00B4D8))
                                    ) {
                                        Text("+₹550", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.recharge(10000.0, "Demo FastPay", "UTR10K${System.currentTimeMillis() % 10000}")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF009688))
                                    ) {
                                        Text("+₹10,000", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }

                                    Button(
                                        onClick = {
                                            viewModel.recharge(100000.0, "Demo 1-Lakh Gateway", "UTR1LAKH${System.currentTimeMillis() % 10000}")
                                        },
                                        modifier = Modifier.weight(1f),
                                        shape = RoundedCornerShape(8.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = DasaniAccentGold, contentColor = Color.Black)
                                    ) {
                                        Text("+₹1 Lakh", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                // Recharge Records Tab
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    if (rechargeHistory.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 48.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("No recharge records yet", color = DasaniTextSecondary)
                            }
                        }
                    } else {
                        items(rechargeHistory) { record ->
                            RechargeRecordItem(record)
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }
            }
        }

        // Confirmation Dialog before completing recharge
        if (showConfirmDialog) {
            AlertDialog(
                onDismissRequest = { showConfirmDialog = false },
                containerColor = Color(0xFF132221),
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = DasaniPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Confirm Recharge", color = Color.White, fontWeight = FontWeight.Bold)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(
                            text = "Please verify your recharge details:",
                            color = DasaniTextSecondary,
                            fontSize = 13.sp
                        )
                        HorizontalDivider(color = DasaniCardBorder)
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Recharge Amount:", color = DasaniTextSecondary)
                            Text("₹ ${"%,d".format(selectedAmount.toLong())}", color = DasaniPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Payment Method:", color = DasaniTextSecondary)
                            Text(selectedMethod.title.take(18) + "...", color = Color.White, fontWeight = FontWeight.SemiBold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Pay To UPI ID:", color = DasaniTextSecondary)
                            Text(officialUpiId, color = DasaniPrimary, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Service Fee:", color = DasaniTextSecondary)
                            Text("₹ 0 (Free)", color = DasaniSuccess, fontWeight = FontWeight.Bold)
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Credited Balance:", color = DasaniTextSecondary)
                            Text("₹ ${"%,d".format(selectedAmount.toLong())}", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            showConfirmDialog = false
                            val utrVal = if (utrInput.isNotBlank()) utrInput else "UTR${System.currentTimeMillis() % 1000000000}"
                            viewModel.recharge(
                                amount = selectedAmount,
                                channel = "${selectedMethod.title} ($officialUpiId)",
                                utr = utrVal
                            )
                            onBack()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = DasaniPrimary,
                            contentColor = Color(0xFF041C1A)
                        ),
                        shape = RoundedCornerShape(20.dp)
                    ) {
                        Text("Confirm & Pay", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showConfirmDialog = false }) {
                        Text("Cancel", color = DasaniTextSecondary)
                    }
                }
            )
        }
    }
}

@Composable
fun AmountPresetButton(
    amount: Double,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(52.dp)
            .clickable { onClick() }
            .testTag("preset_${amount.toLong()}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) DasaniPrimary.copy(alpha = 0.25f) else Color(0xFF142423)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 2.dp else 1.dp,
            color = if (isSelected) DasaniPrimary else DasaniCardBorder
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "₹ ${"%,d".format(amount.toLong())}",
                color = if (isSelected) DasaniPrimary else Color.White,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PaymentMethodCard(
    method: PaymentMethodType,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onSelect() }
            .testTag("method_${method.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF14302D) else Color(0xFF132221)
        ),
        border = androidx.compose.foundation.BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) DasaniPrimary else DasaniCardBorder
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            RadioButton(
                selected = isSelected,
                onClick = onSelect,
                colors = RadioButtonDefaults.colors(
                    selectedColor = DasaniPrimary,
                    unselectedColor = Color(0xFF6B7D82)
                )
            )

            Spacer(modifier = Modifier.width(6.dp))

            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = method.title,
                        color = if (isSelected) Color.White else DasaniTextSecondary,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                    )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = method.subtitle,
                    color = DasaniTextMuted,
                    fontSize = 11.sp
                )
            }

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = if (isSelected) DasaniPrimary else Color(0xFF1E3533)
            ) {
                Text(
                    text = method.badge,
                    color = if (isSelected) Color(0xFF041C1A) else DasaniTextSecondary,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun BankDetailRow(label: String, value: String, context: Context) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(label, color = DasaniTextSecondary, fontSize = 11.sp)
            Text(value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
        }
        IconButton(
            onClick = {
                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                clipboard.setPrimaryClip(ClipData.newPlainText(label, value))
            },
            modifier = Modifier.size(28.dp)
        ) {
            Icon(Icons.Default.ContentCopy, contentDescription = "Copy $label", tint = DasaniPrimary, modifier = Modifier.size(16.dp))
        }
    }
}

@Composable
fun TabPill(
    title: String,
    selected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .height(38.dp)
            .background(
                if (selected) DasaniPrimary else Color.Transparent,
                RoundedCornerShape(19.dp)
            )
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = title,
            color = if (selected) Color(0xFF041C1A) else DasaniTextSecondary,
            fontSize = 13.sp,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}

@Composable
fun RechargeRecordItem(record: RechargeRecord) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF142423))
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Order: ${record.orderNo}",
                    color = Color.White,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "+₹ ${record.amount.toLong()}",
                    color = DasaniSuccess,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = record.channel,
                    color = DasaniTextSecondary,
                    fontSize = 12.sp
                )
                Text(
                    text = record.status,
                    color = DasaniPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "UTR: ${record.utr}",
                    color = DasaniTextMuted,
                    fontSize = 11.sp
                )
                Text(
                    text = record.date,
                    color = DasaniTextMuted,
                    fontSize = 11.sp
                )
            }
        }
    }
}

