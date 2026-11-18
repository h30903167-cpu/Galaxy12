package com.example.model

enum class ProductCategory(val title: String) {
    DAY_INCOME("Day Income"),
    VIP_INCOME("VIP Income")
}

data class DasaniProduct(
    val id: String,
    val name: String,
    val category: ProductCategory,
    val price: Double,
    val dailyProfit: Double,
    val durationDays: Int,
    val totalProfit: Double,
    val limitPerUser: Int = 1,
    val drawableRes: Int,
    val description: String = "Galaxy GL-12 Manual Seeder Unit (Stainless Steel & New Iron Clamp)",
    val isLocked: Boolean = false,
    val lockMessage: String = "🔒 Blocked by Admin. Only ₹300 and ₹500 plans are currently live for purchase."
)

typealias GalaxyProduct = DasaniProduct

data class PurchasedProduct(
    val id: String,
    val productId: String,
    val name: String,
    val price: Double,
    val dailyProfit: Double,
    val daysRemaining: Int,
    val totalDays: Int,
    val totalClaimed: Double,
    val purchaseDate: String,
    val canClaimToday: Boolean = true,
    val drawableRes: Int
)

data class RechargeRecord(
    val id: String,
    val orderNo: String,
    val amount: Double,
    val channel: String,
    val utr: String,
    val date: String,
    val status: String = "Success"
)

data class WithdrawalRecord(
    val id: String,
    val orderNo: String,
    val amount: Double,
    val fee: Double,
    val finalAmount: Double,
    val payoutMethod: String,
    val accountNumberOrUpi: String,
    val holderName: String,
    val ifsc: String,
    val status: String = "Success",
    val date: String,
    val videoProofUrl: String? = null
)

data class TeamMember(
    val phone: String,
    val level: Int,
    val recharge: Double,
    val commissionEarned: Double,
    val date: String
)

data class UserAccount(
    val phone: String = "+91 98765 43210",
    val loginPassword: String = "123456",
    val withdrawalPassword: String = "123456",
    val inviteCode: String = "c07cdf4c",
    val walletBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val todayIncome: Double = 0.0,
    val totalRecharged: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val isLoggedIn: Boolean = true,
    val boundBankName: String = "State Bank of India",
    val boundAccountNo: String = "392019482910",
    val boundHolderName: String = "Galaxy Equipment Partner",
    val boundIfsc: String = "SBIN0001234",
    val boundUpiId: String = "user@upi",
    val registeredWithUpi: Boolean = false
)

data class WithdrawalTutorialVideo(
    val id: String = "vid_1",
    val title: String = "Official Galaxy Seeder Withdrawal Guide & Payment Proof",
    val videoUrl: String = "https://example.com/galaxy_seeder_withdrawal_guide.mp4",
    val duration: String = "02:45",
    val description: String = "Watch the step-by-step tutorial on how to bind your bank card, submit instant withdrawal, and receive funds in your bank account in 10-30 minutes."
)

data class ServiceChatMessage(
    val id: String,
    val sender: String,
    val message: String,
    val time: String,
    val isFromUser: Boolean
)
