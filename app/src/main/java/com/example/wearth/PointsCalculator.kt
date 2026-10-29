package com.example.wearth

import kotlin.math.floor
import kotlin.math.min
import kotlin.math.roundToInt

/**
 * ฟังก์ชันและค่าคอนฟิกสำหรับการคำนวณแต้มและราคาของร้าน WEARTH
 * แยกเป็นไฟล์เดี่ยวเพื่อให้ปรับอัตราได้สะดวกในอนาคต
 */
object PointsConfig {
  /** อัตราคูณเมื่อเลือกรับแต้มแทนเงินสด (เช่น P * 1.25) */
  const val POINTS_MULTIPLIER = 1.25

  /** อัตราคูณราคาขายหน้าร้านแนะนำจากราคาประเมิน (เช่น P * 1.60) */
  const val SELLING_PRICE_MULTIPLIER = 1.60

  /** สัดส่วนแต้มสูงสุดที่ใช้ได้สำหรับเสื้อแบรนด์ (70%) */
  const val BRAND_MAX_POINTS_RATIO = 0.70

  /** สัดส่วนแต้มสูงสุดที่ใช้ได้สำหรับเสื้อทั่วไป (100%) */
  const val GENERAL_MAX_POINTS_RATIO = 1.00

  /** มูลค่า 1 แต้ม = 1 บาท */
  const val BAHT_PER_POINT = 1

  /** อายุแต้ม (เดือน) */
  const val POINTS_EXPIRY_MONTHS = 6
}

data class RedemptionCalculation(
  val itemPrice: Int,
  val isBrand: Boolean,
  val maxPointsAllowed: Int,
  val pointsUsed: Int,
  val cashRequired: Int,
  val remainingCustomerPoints: Int
)

object PointsCalculator {

  /**
   * คำนวณแต้มที่ได้รับเมื่อนำเสื้อมาฝาก
   * สูตร: P * 1.25
   */
  fun calculatePointsOffer(appraisalPrice: Int): Int {
    if (appraisalPrice <= 0) return 0
    return (appraisalPrice * PointsConfig.POINTS_MULTIPLIER).roundToInt()
  }

  /**
   * คำนวณราคาขายหน้าร้านที่แนะนำจากราคาประเมิน
   * สูตร: P * 1.6
   */
  fun calculateRecommendedSellingPrice(appraisalPrice: Int): Int {
    if (appraisalPrice <= 0) return 0
    return (appraisalPrice * PointsConfig.SELLING_PRICE_MULTIPLIER).roundToInt()
  }

  /**
   * คำนวณการใช้แต้มแลกเสื้อ
   * - เสื้อทั่วไป: ใช้แต้มได้เต็มราคา 100%
   * - เสื้อแบรนด์: ใช้แต้มได้ไม่เกิน 70% ของราคา
   * ส่วนที่เหลือจ่ายเป็นเงินสด
   *
   * ตัวอย่างทดสอบ (1):
   * เสื้อทั่วไป 96 บาท, ลูกค้ามี 75 แต้ม -> ใช้ 75 แต้ม + จ่าย 21 บาท
   *
   * ตัวอย่างทดสอบ (2):
   * เสื้อแบรนด์ 320 บาท, ลูกค้ามี 250 แต้ม -> ใช้ 224 แต้ม (70%) + จ่าย 96 บาท เหลือ 26 แต้ม
   */
  fun calculateRedemption(
    itemPrice: Int,
    isBrand: Boolean,
    customerPoints: Int
  ): RedemptionCalculation {
    val maxPointsAllowed = if (isBrand) {
      floor(itemPrice * PointsConfig.BRAND_MAX_POINTS_RATIO).toInt()
    } else {
      (itemPrice * PointsConfig.GENERAL_MAX_POINTS_RATIO).toInt()
    }

    val pointsUsed = min(customerPoints, maxPointsAllowed)
    val cashRequired = (itemPrice - pointsUsed).coerceAtLeast(0)
    val remainingCustomerPoints = (customerPoints - pointsUsed).coerceAtLeast(0)

    return RedemptionCalculation(
      itemPrice = itemPrice,
      isBrand = isBrand,
      maxPointsAllowed = maxPointsAllowed,
      pointsUsed = pointsUsed,
      cashRequired = cashRequired,
      remainingCustomerPoints = remainingCustomerPoints
    )
  }

  /**
   * ฟอร์แมตแสดงแต้มคู่กับบาทเสมอตามกติกา
   * ตัวอย่าง: "75 แต้ม (= 75 บาท)"
   */
  fun formatPointsWithBaht(points: Int): String {
    val baht = points * PointsConfig.BAHT_PER_POINT
    return "$points แต้ม (= $baht บาท)"
  }
}
