package com.example

import com.example.wearth.PointsCalculator
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {

  @Test
  fun testCase1_GeneralClothingCalculation() {
    // (1) ประเมินเสื้อทั่วไป 60 บาท -> ได้ 75 แต้ม
    val pointsOffer = PointsCalculator.calculatePointsOffer(60)
    assertEquals(75, pointsOffer)

    // ราคาขายแนะนำ 60 * 1.6 = 96 บาท
    val sellingPrice = PointsCalculator.calculateRecommendedSellingPrice(60)
    assertEquals(96, sellingPrice)

    // แลกเสื้อราคา 96 บาท = ใช้ 75 แต้ม + จ่าย 21 บาท
    val redemption = PointsCalculator.calculateRedemption(
      itemPrice = 96,
      isBrand = false,
      customerPoints = 75
    )
    assertEquals(75, redemption.pointsUsed)
    assertEquals(21, redemption.cashRequired)
    assertEquals(0, redemption.remainingCustomerPoints)
  }

  @Test
  fun testCase2_BrandClothingCalculation() {
    // (2) ประเมินเสื้อแบรนด์ 200 บาท -> ได้ 250 แต้ม
    val pointsOffer = PointsCalculator.calculatePointsOffer(200)
    assertEquals(250, pointsOffer)

    // ราคาขายแนะนำ 200 * 1.6 = 320 บาท
    val sellingPrice = PointsCalculator.calculateRecommendedSellingPrice(200)
    assertEquals(320, sellingPrice)

    // แลกเสื้อแบรนด์ 320 บาท = ใช้ 224 แต้ม (70%) + จ่าย 96 บาท เหลือ 26 แต้ม
    val redemption = PointsCalculator.calculateRedemption(
      itemPrice = 320,
      isBrand = true,
      customerPoints = 250
    )
    assertEquals(224, redemption.maxPointsAllowed)
    assertEquals(224, redemption.pointsUsed)
    assertEquals(96, redemption.cashRequired)
    assertEquals(26, redemption.remainingCustomerPoints)
  }

  @Test
  fun testFormatPointsWithBaht() {
    val formatted = PointsCalculator.formatPointsWithBaht(75)
    assertEquals("75 แต้ม (= 75 บาท)", formatted)
  }

  @Test
  fun testCameraQuickAppraisalResultFormulas() {
    val resultGeneral = com.example.wearth.QuickAppraisalResult(
      itemName = "เสื้อยืดทั่วไป",
      category = "เสื้อยืด",
      detectedBrand = "ทั่วไป",
      level = com.example.wearth.ClothingLevel.GENERAL,
      condition = com.example.wearth.ClothingCondition.GOOD,
      estimatedAppraisalPrice = 60
    )
    // P = 60, Points = P * 1.25 = 75, Selling = P * 1.6 = 96
    assertEquals(75, resultGeneral.pointsReward)
    assertEquals(96, resultGeneral.recommendedSellingPrice)

    val resultBrand = com.example.wearth.QuickAppraisalResult(
      itemName = "แจ็กเก็ตยีนส์แบรนด์",
      category = "แจ็กเก็ต",
      detectedBrand = "Levi's",
      level = com.example.wearth.ClothingLevel.BRAND,
      condition = com.example.wearth.ClothingCondition.VERY_GOOD,
      estimatedAppraisalPrice = 200
    )
    // P = 200, Points = P * 1.25 = 250, Selling = P * 1.6 = 320
    assertEquals(250, resultBrand.pointsReward)
    assertEquals(320, resultBrand.recommendedSellingPrice)
  }
}
