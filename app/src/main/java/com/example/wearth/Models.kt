package com.example.wearth

enum class ClothingLevel(val label: String) {
  GENERAL("ทั่วไป"),
  BRAND("แบรนด์")
}

enum class ClothingCondition(val label: String) {
  VERY_GOOD("ดีมาก"),
  GOOD("ดี")
}

enum class ItemStatus(val label: String) {
  AVAILABLE("ว่าง"),
  RESERVED("ถูกจอง"),
  SOLD("ขายแล้ว")
}

data class ClothingItem(
  val id: String,
  val name: String,
  val category: String, // เช่น เสื้อยืด, เสื้อเชิ้ต, แจ็กเก็ต, กางเกง
  val brandName: String? = null,
  val level: ClothingLevel,
  val condition: ClothingCondition,
  val size: String, // S, M, L, XL
  val price: Int,
  val appraisalPrice: Int,
  val status: ItemStatus = ItemStatus.AVAILABLE,
  val imageDrawableName: String,
  val isRecommended: Boolean = false,
  val description: String = ""
) {
  val isBrand: Boolean
    get() = level == ClothingLevel.BRAND
}

data class PointsBatch(
  val id: String,
  val amount: Int,
  val dateEarned: String,
  val expiryDate: String,
  val isExpiringSoon: Boolean = false,
  val note: String
)

/**
 * ข้อมูลการปักหมุดที่อยู่สำหรับบริการรับและส่งถึงที่
 */
data class DeliveryAddress(
  val id: String = "addr_default",
  val label: String = "บ้าน", // บ้าน, คอนโด, ที่ทำงาน, อื่นๆ
  val recipientName: String,
  val phone: String,
  val addressText: String,
  val district: String = "เขตวัฒนา",
  val province: String = "กรุงเทพฯ",
  val postalCode: String = "10110",
  val noteToRider: String = "",
  val latitude: Double = 13.736717,
  val longitude: Double = 100.561234
) {
  val fullDisplayAddress: String
    get() = "$addressText แขวง/ตำบล $district $province $postalCode"
}

/**
 * ประเภทการจัดส่งสินค้า (ตอนแลกเสื้อ)
 */
enum class DeliveryType(val label: String, val defaultFee: Int) {
  STORE_PICKUP("รับเองที่ร้าน WEARTH", 0),
  HOME_DELIVERY("จัดส่งพัสดุถึงบ้าน (Eco Delivery)", 35)
}

/**
 * ประเภทการนำส่งเสื้อ (ตอนนำเสื้อมาฝาก/แลกแต้ม)
 */
enum class DepositIntakeType(val label: String, val defaultFee: Int) {
  STORE_DROPOFF("นำมาส่งที่ร้านด้วยตนเอง", 0),
  HOME_PICKUP("เรียกรถเข้ารับเสื้อถึงบ้าน (WEARTH Eco-Rider)", 40)
}

data class Customer(
  val id: String,
  val name: String,
  val phone: String,
  val points: Int,
  val batches: List<PointsBatch> = emptyList(),
  val totalClothesDeposited: Int = 0,
  val totalRedeemedTimes: Int = 0,
  val tierName: String = "ทั่วไป",
  val savedAddresses: List<DeliveryAddress> = emptyList()
)

data class RedeemOrder(
  val id: String,
  val orderCode: String, // เช่น #WE-7842
  val customerId: String,
  val customerName: String,
  val customerPhone: String,
  val itemId: String,
  val itemName: String,
  val itemPrice: Int,
  val pointsUsed: Int,
  val cashPaid: Int,
  val dateTime: String,
  val status: String = "รอรับของหน้าร้าน", // รอรับของหน้าร้าน, กำลังจัดเตรียมพัสดุ, ไรเดอร์กำลังนำส่ง, จัดส่งสำเร็จ, รับของสำเร็จ
  val deliveryType: DeliveryType = DeliveryType.STORE_PICKUP,
  val shippingAddress: DeliveryAddress? = null,
  val shippingFee: Int = 0,
  val trackingCode: String? = null
)

data class DepositRequest(
  val id: String,
  val trackingCode: String = "#PK-${(1000..9999).random()}",
  val customerId: String,
  val customerName: String,
  val phone: String,
  val category: String,
  val brandName: String,
  val size: String,
  val condition: ClothingCondition,
  val appointmentDate: String,
  val appointmentTime: String,
  val status: String = "นัดหมายแล้ว", // นัดหมายแล้ว, ไรเดอร์กำลังเดินทางเข้ารับ, ไรเดอร์รับเสื้อแล้ว, ตรวจสภาพสำเร็จ
  val intakeType: DepositIntakeType = DepositIntakeType.STORE_DROPOFF,
  val pickupAddress: DeliveryAddress? = null,
  val pickupFee: Int = 0,
  val numberOfClothes: Int = 1
)

data class HistoryTransaction(
  val id: String,
  val title: String,
  val date: String,
  val pointsDelta: Int, // +120 หรือ -140
  val cashPaid: Int = 0,
  val note: String = ""
)

enum class UserMode {
  CUSTOMER,
  STAFF
}

enum class CustomerTab {
  HOME,
  SHOP,
  DEPOSIT,
  WALLET,
  PROFILE
}

enum class StaffTab {
  APPRAISAL,
  CONFIRM_REDEEM,
  PICKUP_DELIVERY, // จัดการงานรับ-ส่งถึงที่
  INVENTORY,
  DASHBOARD
}

/**
 * ผลการประเมินราคาเสื้อผ้าด่วนจากการสแกนด้วยกล้อง CameraX
 */
data class QuickAppraisalResult(
  val id: String = java.util.UUID.randomUUID().toString(),
  val itemName: String,
  val category: String,
  val detectedBrand: String,
  val level: ClothingLevel,
  val condition: ClothingCondition,
  val size: String = "M",
  val estimatedAppraisalPrice: Int,
  val pointsReward: Int = (estimatedAppraisalPrice * 1.25).toInt(),
  val recommendedSellingPrice: Int = (estimatedAppraisalPrice * 1.6).toInt(),
  val confidenceScore: Int = 94,
  val detectedFabric: String = "ผ้าคอตตอน 100% สภาพดี ไร้รอยขาด",
  val imageUri: String? = null,
  val scannedTime: String = "เมื่อสักครู่"
)
