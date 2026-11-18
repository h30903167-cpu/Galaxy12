package com.example.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.R
import com.example.data.AppLinkConfig
import com.example.model.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class DasaniViewModel : ViewModel() {

    private val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    private val _userAccount = MutableStateFlow(
        UserAccount(
            phone = "+91 98765 43210",
            loginPassword = "password123",
            withdrawalPassword = "123456",
            inviteCode = "c07cdf4c",
            walletBalance = 600.0, // Initial demo balance so user can test buying Product A immediately!
            totalIncome = 0.0,
            todayIncome = 0.0,
            totalRecharged = 600.0,
            totalWithdrawn = 0.0,
            isLoggedIn = true
        )
    )
    val userAccount: StateFlow<UserAccount> = _userAccount.asStateFlow()

    private val _selectedCategory = MutableStateFlow(ProductCategory.DAY_INCOME)
    val selectedCategory: StateFlow<ProductCategory> = _selectedCategory.asStateFlow()

    private val _allProducts = MutableStateFlow(
        listOf(
            // Top 1: ₹300 Plan (LIVE)
            DasaniProduct(
                id = "prod_gl12_300",
                name = "GL-12 Seeder (300 Plan)",
                category = ProductCategory.DAY_INCOME,
                price = 300.0,
                dailyProfit = 75.0,
                durationDays = 120,
                totalProfit = 9000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl12,
                description = "Galaxy GL-12 Manual Seeder Starter Machine (₹300 Plan • ₹75 Daily Profit)",
                isLocked = false
            ),
            // Top 2: ₹500 Plan (LIVE)
            DasaniProduct(
                id = "prod_gl12_500",
                name = "GL-12 Seeder (500 Plan)",
                category = ProductCategory.DAY_INCOME,
                price = 500.0,
                dailyProfit = 125.0,
                durationDays = 120,
                totalProfit = 15000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl12,
                description = "Galaxy GL-12 Manual Seeder Standard Machine (₹500 Plan • ₹125 Daily Profit)",
                isLocked = false
            ),
            // Blocked / Locked Plans
            DasaniProduct(
                id = "prod_gl10",
                name = "GL-10 Galaxy Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 1800.0,
                dailyProfit = 450.0,
                durationDays = 120,
                totalProfit = 54000.0,
                limitPerUser = 2,
                drawableRes = R.drawable.galaxy_product_gl10,
                description = "Galaxy GL-10 Automatic Seed Planter & Cultivator",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_gl14",
                name = "GL-14 Multi-Crop Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 4900.0,
                dailyProfit = 1350.0,
                durationDays = 120,
                totalProfit = 162000.0,
                limitPerUser = 3,
                drawableRes = R.drawable.galaxy_product_gl14,
                description = "Multi-Crop Corn, Peanut, Soybean & Wheat Seeder Unit",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_gl16",
                name = "GL-16 Heavy Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 12000.0,
                dailyProfit = 3600.0,
                durationDays = 120,
                totalProfit = 432000.0,
                limitPerUser = 3,
                drawableRes = R.drawable.galaxy_product_vip,
                description = "Heavy Duty Stainless Steel Agricultural Seeding Rig",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_tractor_seeder",
                name = "Galaxy Tractor Seeder",
                category = ProductCategory.DAY_INCOME,
                price = 35000.0,
                dailyProfit = 11200.0,
                durationDays = 120,
                totalProfit = 1344000.0,
                limitPerUser = 2,
                drawableRes = R.drawable.galaxy_seeder_box,
                description = "Commercial High-Yield Tractor Attachable Seeding Machine",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            DasaniProduct(
                id = "prod_ultra_1lakh",
                name = "Galaxy Mega Seeder (1 Lakh)",
                category = ProductCategory.DAY_INCOME,
                price = 100000.0,
                dailyProfit = 34000.0,
                durationDays = 120,
                totalProfit = 4080000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl12,
                description = "Galaxy Industrial Farm Seeding Fleet (₹1 Lakh Investment)",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are live for buying."
            ),
            // VIP Seeder Category Products (All Blocked)
            DasaniProduct(
                id = "vip_seeder_1",
                name = "VIP Seeder 1",
                category = ProductCategory.VIP_INCOME,
                price = 1200.0,
                dailyProfit = 600.0,
                durationDays = 3,
                totalProfit = 1800.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_vip,
                description = "High Speed 3-Day Return Galaxy Seeder VIP Plan",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            ),
            DasaniProduct(
                id = "vip_seeder_2",
                name = "VIP Seeder 2",
                category = ProductCategory.VIP_INCOME,
                price = 3800.0,
                dailyProfit = 2200.0,
                durationDays = 3,
                totalProfit = 6600.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl10,
                description = "Advanced VIP Rapid Yield Galaxy Agricultural Plan",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            ),
            DasaniProduct(
                id = "vip_seeder_3",
                name = "VIP Seeder 3",
                category = ProductCategory.VIP_INCOME,
                price = 10000.0,
                dailyProfit = 6500.0,
                durationDays = 3,
                totalProfit = 19500.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_product_gl14,
                description = "VIP Executive Farm Equipment Yield Program",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            ),
            DasaniProduct(
                id = "vip_mega_1lakh",
                name = "VIP Mega Seeder (1 Lakh)",
                category = ProductCategory.VIP_INCOME,
                price = 100000.0,
                dailyProfit = 75000.0,
                durationDays = 3,
                totalProfit = 225000.0,
                limitPerUser = 1,
                drawableRes = R.drawable.galaxy_seeder_box,
                description = "Exclusive 1 Lakh Fast-Yield VIP Machine",
                isLocked = true,
                lockMessage = "🔒 Blocked by Admin. VIP plans are currently closed. Only ₹300 and ₹500 are live."
            )
        )
    )
    val allProducts: StateFlow<List<DasaniProduct>> = _allProducts.asStateFlow()

    private val _myDevices = MutableStateFlow<List<PurchasedProduct>>(emptyList())
    val myDevices: StateFlow<List<PurchasedProduct>> = _myDevices.asStateFlow()

    private val _rechargeHistory = MutableStateFlow(
        listOf(
            RechargeRecord(
                id = "rec_init",
                orderNo = "RC${System.currentTimeMillis() % 1000000}",
                amount = 600.0,
                channel = "UPI FastPay (GPay/PhonePe)",
                utr = "431298765421",
                date = "2026-11-08 08:30",
                status = "Success"
            )
        )
    )
    val rechargeHistory: StateFlow<List<RechargeRecord>> = _rechargeHistory.asStateFlow()

    private val _withdrawalHistory = MutableStateFlow(
        listOf(
            WithdrawalRecord(
                id = "wd_init",
                orderNo = "WD782910",
                amount = 250.0,
                fee = 12.5,
                finalAmount = 237.5,
                payoutMethod = "Bank Account",
                accountNumberOrUpi = "392019482910",
                holderName = "Galaxy Equipment Partner",
                ifsc = "SBIN0001234",
                status = "Success",
                date = "2026-11-07 19:15",
                videoProofUrl = "https://example.com/withdrawal_proof_demo.mp4"
            )
        )
    )
    val withdrawalHistory: StateFlow<List<WithdrawalRecord>> = _withdrawalHistory.asStateFlow()

    private val _teamMembers = MutableStateFlow<List<TeamMember>>(emptyList())
    val teamMembers: StateFlow<List<TeamMember>> = _teamMembers.asStateFlow()

    private val _currentTeamLevel = MutableStateFlow(1)
    val currentTeamLevel: StateFlow<Int> = _currentTeamLevel.asStateFlow()

    private val _tutorialVideo = MutableStateFlow(
        WithdrawalTutorialVideo(
            id = "vid_1",
            title = "Official Galaxy Seeder Withdrawal Tutorial & Instant Proof",
            videoUrl = "https://example.com/galaxy_seeder_withdrawal_guide.mp4",
            duration = "02:45",
            description = "Learn how to bind your bank card / UPI, submit withdrawals from ₹200 to ₹1,00,000, and receive funds directly within 10-30 minutes."
        )
    )
    val tutorialVideo: StateFlow<WithdrawalTutorialVideo> = _tutorialVideo.asStateFlow()

    private val _serviceMessages = MutableStateFlow(
        listOf(
            ServiceChatMessage(
                id = "msg_1",
                sender = "Galaxy Seeder Support",
                message = "Namaste! Welcome to Galaxy Manual Seeder 24/7 Official Customer Support. How can we assist you with Recharge, Equipment Buying, or Bank Withdrawal today?",
                time = "Online",
                isFromUser = false
            )
        )
    )
    val serviceMessages: StateFlow<List<ServiceChatMessage>> = _serviceMessages.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _incomingReferralCode = MutableStateFlow<String?>(null)
    val incomingReferralCode: StateFlow<String?> = _incomingReferralCode.asStateFlow()

    fun applyReferralLink(rawUri: String?) {
        val code = AppLinkConfig.extractReferralCode(rawUri) ?: return
        _incomingReferralCode.value = code
        _toastMessage.value = "Referral link applied: $code"
    }

    fun selectCategory(category: ProductCategory) {
        _selectedCategory.value = category
    }

    fun selectTeamLevel(level: Int) {
        _currentTeamLevel.value = level
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    fun login(phoneInput: String, passInput: String): Boolean {
        val current = _userAccount.value
        val cleanInput = phoneInput.trim()
        if (passInput.isBlank() || cleanInput.isBlank()) {
            _toastMessage.value = "Please enter valid UPI ID / Phone and password."
            return false
        }
        val isUpi = cleanInput.contains("@")
        val updated = if (isUpi) {
            current.copy(
                boundUpiId = cleanInput,
                phone = if (current.phone.isNotBlank()) current.phone else "+91 98765 43210",
                loginPassword = passInput,
                isLoggedIn = true,
                registeredWithUpi = true
            )
        } else {
            val formattedPhone = if (cleanInput.startsWith("+91")) cleanInput else "+91 $cleanInput"
            current.copy(
                phone = formattedPhone,
                loginPassword = passInput,
                isLoggedIn = true
            )
        }
        _userAccount.value = updated
        _toastMessage.value = "Login Successful! Welcome to Galaxy Seeder."
        return true
    }

    fun register(
        phoneInput: String,
        pass1: String,
        pass2: String,
        withdrawPass: String,
        invite: String,
        upiIdInput: String = ""
    ): Pair<Boolean, String> {
        val cleanInput = phoneInput.trim()
        val customUpi = upiIdInput.trim()
        val isUpiMode = cleanInput.contains("@") || customUpi.contains("@")

        if (!isUpiMode && cleanInput.length < 10) {
            return Pair(false, "Please enter a valid 10-digit phone number or UPI ID (e.g. name@upi)")
        }
        if (isUpiMode && cleanInput.length < 4 && customUpi.length < 4) {
            return Pair(false, "Please enter a valid UPI ID (e.g. yourname@paytm)")
        }
        if (pass1.length < 4) {
            return Pair(false, "Login password must be at least 4 characters")
        }
        if (pass1 != pass2) {
            return Pair(false, "Passwords do not match")
        }
        if (withdrawPass.length < 4) {
            return Pair(false, "Withdrawal password must be at least 4 digits")
        }

        val resolvedUpi = when {
            customUpi.isNotBlank() -> customUpi
            cleanInput.contains("@") -> cleanInput
            else -> "${cleanInput}@paytm"
        }

        val resolvedPhone = if (!cleanInput.contains("@") && cleanInput.isNotBlank()) {
            if (cleanInput.startsWith("+91")) cleanInput else "+91 $cleanInput"
        } else {
            "+91 98765 43210"
        }

        _userAccount.value = _userAccount.value.copy(
            phone = resolvedPhone,
            boundUpiId = resolvedUpi,
            registeredWithUpi = isUpiMode,
            loginPassword = pass1,
            withdrawalPassword = withdrawPass,
            inviteCode = if (invite.isNotBlank()) invite else "c07cdf4c",
            walletBalance = 600.0,
            isLoggedIn = true
        )
        _toastMessage.value = "Registration Successful with UPI: $resolvedUpi! ₹600 Gift activated."
        return Pair(true, "Success")
    }

    fun logout() {
        _userAccount.value = _userAccount.value.copy(isLoggedIn = false)
        _toastMessage.value = "Logged out successfully"
    }

    // Recharge flow with support up to 1 Lakh and beyond
    fun recharge(amount: Double, channel: String, utr: String): Boolean {
        if (amount < 100) {
            _toastMessage.value = "Minimum recharge amount is ₹100"
            return false
        }
        val order = "RC${System.currentTimeMillis() % 10000000}"
        val record = RechargeRecord(
            id = UUID.randomUUID().toString(),
            orderNo = order,
            amount = amount,
            channel = channel,
            utr = if (utr.isNotBlank()) utr else "${System.currentTimeMillis()}",
            date = dateFormat.format(Date()),
            status = "Success"
        )
        val currentAcc = _userAccount.value
        _userAccount.value = currentAcc.copy(
            walletBalance = currentAcc.walletBalance + amount,
            totalRecharged = currentAcc.totalRecharged + amount
        )
        _rechargeHistory.value = listOf(record) + _rechargeHistory.value
        _toastMessage.value = "Recharge of ₹${amount.toLong()} Successful! Balance updated."
        return true
    }

    // Purchase product
    fun buyProduct(product: DasaniProduct): Pair<Boolean, String> {
        if (product.isLocked) {
            _toastMessage.value = "⚠️ This plan is blocked! Only ₹300 and ₹500 plans are live."
            return Pair(false, "This equipment is currently blocked. Only ₹300 and ₹500 plans are available for live purchase.")
        }

        val currentAcc = _userAccount.value
        if (currentAcc.walletBalance < product.price) {
            val needed = product.price - currentAcc.walletBalance
            return Pair(false, "Insufficient balance! Need ₹${needed.toLong()} more. Please recharge.")
        }

        // Check user limit
        val existingCount = _myDevices.value.count { it.productId == product.id }
        if (existingCount >= product.limitPerUser) {
            return Pair(false, "Purchase limit of ${product.limitPerUser} reached for ${product.name}.")
        }

        // Deduct price and add to devices
        val newBalance = currentAcc.walletBalance - product.price
        val newDevice = PurchasedProduct(
            id = UUID.randomUUID().toString(),
            productId = product.id,
            name = product.name,
            price = product.price,
            dailyProfit = product.dailyProfit,
            daysRemaining = product.durationDays,
            totalDays = product.durationDays,
            totalClaimed = 0.0,
            purchaseDate = dateFormat.format(Date()),
            canClaimToday = true,
            drawableRes = product.drawableRes
        )

        _userAccount.value = currentAcc.copy(
            walletBalance = newBalance
        )
        _myDevices.value = listOf(newDevice) + _myDevices.value
        _toastMessage.value = "Congratulations! ${product.name} purchased successfully."
        return Pair(true, "Success")
    }

    fun claimDailyProfit(deviceId: String) {
        val device = _myDevices.value.find { it.id == deviceId } ?: return
        if (!device.canClaimToday) {
            _toastMessage.value = "Today's profit already claimed for this device!"
            return
        }

        val profit = device.dailyProfit
        val updatedDevices = _myDevices.value.map {
            if (it.id == deviceId) {
                it.copy(
                    totalClaimed = it.totalClaimed + profit,
                    daysRemaining = (it.daysRemaining - 1).coerceAtLeast(0),
                    canClaimToday = false
                )
            } else it
        }
        _myDevices.value = updatedDevices

        val acc = _userAccount.value
        _userAccount.value = acc.copy(
            walletBalance = acc.walletBalance + profit,
            totalIncome = acc.totalIncome + profit,
            todayIncome = acc.todayIncome + profit
        )
        _toastMessage.value = "₹${profit.toLong()} daily profit claimed and added to wallet!"
    }

    fun claimAllDailyProfits() {
        val claimable = _myDevices.value.filter { it.canClaimToday }
        if (claimable.isEmpty()) {
            _toastMessage.value = "No pending profits to claim right now."
            return
        }
        val totalProfit = claimable.sumOf { it.dailyProfit }
        val updated = _myDevices.value.map {
            if (it.canClaimToday) {
                it.copy(
                    totalClaimed = it.totalClaimed + it.dailyProfit,
                    daysRemaining = (it.daysRemaining - 1).coerceAtLeast(0),
                    canClaimToday = false
                )
            } else it
        }
        _myDevices.value = updated

        val acc = _userAccount.value
        _userAccount.value = acc.copy(
            walletBalance = acc.walletBalance + totalProfit,
            totalIncome = acc.totalIncome + totalProfit,
            todayIncome = acc.todayIncome + totalProfit
        )
        _toastMessage.value = "Total ₹${totalProfit.toLong()} collected to your wallet!"
    }

    // Submit withdrawal with withdrawal password check & optional video proof
    fun submitWithdrawal(
        amount: Double,
        bankName: String,
        accountNo: String,
        holderName: String,
        ifsc: String,
        upiId: String,
        withdrawalPasswordInput: String,
        videoProofUrl: String?
    ): Pair<Boolean, String> {
        val currentAcc = _userAccount.value
        if (amount < 200.0) {
            return Pair(false, "Minimum withdrawal amount is ₹200")
        }
        if (amount > currentAcc.walletBalance) {
            return Pair(false, "Withdrawal amount cannot exceed available balance of ₹${currentAcc.walletBalance.toLong()}")
        }
        if (withdrawalPasswordInput != currentAcc.withdrawalPassword) {
            return Pair(false, "Incorrect Withdrawal Password! Please enter the 4-6 digit code set during registration.")
        }
        if (accountNo.isBlank() && upiId.isBlank()) {
            return Pair(false, "Please provide Bank Account Number or UPI ID")
        }

        val fee = amount * 0.05 // 5% fee
        val net = amount - fee
        val order = "WD${System.currentTimeMillis() % 1000000}"

        val record = WithdrawalRecord(
            id = UUID.randomUUID().toString(),
            orderNo = order,
            amount = amount,
            fee = fee,
            finalAmount = net,
            payoutMethod = if (upiId.isNotBlank()) "UPI: $upiId" else "Bank: $bankName",
            accountNumberOrUpi = if (upiId.isNotBlank()) upiId else accountNo,
            holderName = holderName,
            ifsc = ifsc,
            status = "Success",
            date = dateFormat.format(Date()),
            videoProofUrl = videoProofUrl
        )

        _userAccount.value = currentAcc.copy(
            walletBalance = currentAcc.walletBalance - amount,
            totalWithdrawn = currentAcc.totalWithdrawn + amount,
            boundBankName = bankName.ifBlank { currentAcc.boundBankName },
            boundAccountNo = accountNo.ifBlank { currentAcc.boundAccountNo },
            boundHolderName = holderName.ifBlank { currentAcc.boundHolderName },
            boundIfsc = ifsc.ifBlank { currentAcc.boundIfsc },
            boundUpiId = upiId.ifBlank { currentAcc.boundUpiId }
        )
        _withdrawalHistory.value = listOf(record) + _withdrawalHistory.value
        _toastMessage.value = "Withdrawal of ₹${amount.toLong()} Submitted! Net ₹${net.toLong()} dispatched."
        return Pair(true, "Success")
    }

    fun updateWithdrawalVideo(title: String, url: String, desc: String) {
        _tutorialVideo.value = WithdrawalTutorialVideo(
            title = title,
            videoUrl = url,
            description = desc
        )
        _toastMessage.value = "Withdrawal Tutorial Video updated successfully!"
    }

    fun sendServiceMessage(text: String) {
        if (text.isBlank()) return
        val userMsg = ServiceChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "You",
            message = text,
            time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
            isFromUser = true
        )
        _serviceMessages.value = _serviceMessages.value + userMsg

        // Automated smart support reply
        viewModelScope.launch {
            val replyText = when {
                text.contains("recharge", ignoreCase = true) || text.contains("1 lakh", ignoreCase = true) || text.contains("100000", ignoreCase = true) ->
                    "To recharge, tap 'Recharge' on the Home screen. You can select ₹550 up to ₹1,00,000! Copy our official UPI ID, pay via GPay/PhonePe, and enter the 12-digit UTR for instant credit."
                text.contains("withdraw", ignoreCase = true) || text.contains("video", ignoreCase = true) || text.contains("paisa", ignoreCase = true) ->
                    "Withdrawals are open 24/7. Minimum is ₹200. Bind your Bank/UPI, enter your Withdrawal Password, and watch our Withdrawal Video tutorial right on the Withdraw page. Funds arrive within 10-30 minutes!"
                text.contains("buy", ignoreCase = true) || text.contains("product", ignoreCase = true) || text.contains("income", ignoreCase = true) || text.contains("seeder", ignoreCase = true) ->
                    "GL-12 Manual Seeder costs ₹550 and yields ₹125 daily for 120 days (Total ₹15,000). VIP Seeder plans give fast 3-day high yields. Ensure your wallet has sufficient balance, then tap 'Buy now'!"
                else ->
                    "Thank you for contacting Galaxy Seeder 24/7 Care. Our live agent is reviewing your query. You can also join our official Telegram Channel or WhatsApp Support from the links below."
            }
            val botMsg = ServiceChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "Galaxy Care",
                message = replyText,
                time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date()),
                isFromUser = false
            )
            _serviceMessages.value = _serviceMessages.value + botMsg
        }
    }

    fun showToast(message: String) {
        _toastMessage.value = message
    }

    fun simulateAddDemoBonus(amount: Double) {
        val acc = _userAccount.value
        _userAccount.value = acc.copy(
            walletBalance = acc.walletBalance + amount
        )
        _toastMessage.value = "₹${amount.toLong()} added to wallet!"
    }

    fun simulateAddTeamMember(phone: String, rechargeAmt: Double) {
        val commission = rechargeAmt * 0.20 // 20% Level 1
        val newMember = TeamMember(
            phone = phone,
            level = 1,
            recharge = rechargeAmt,
            commissionEarned = commission,
            date = dateFormat.format(Date())
        )
        _teamMembers.value = listOf(newMember) + _teamMembers.value
        val acc = _userAccount.value
        _userAccount.value = acc.copy(
            walletBalance = acc.walletBalance + commission,
            totalIncome = acc.totalIncome + commission
        )
        _toastMessage.value = "New Level 1 member joined! You earned ₹${commission.toLong()} (20% bonus)."
    }
}
