package com.example.wearth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthBorder
import com.example.ui.theme.WearthCardGreen
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthDepositBg
import com.example.ui.theme.WearthDepositColor
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGold
import com.example.ui.theme.WearthPointsGoldBg
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthShopPurple
import com.example.ui.theme.WearthShopPurpleBg
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

/**
 * หน้าจอ 1: พนักงานประเมินเสื้อ
 */
@Composable
fun StaffAppraisalScreen(
  customers: List<Customer>,
  onCompleteAppraisal: (
    customer: Customer,
    clothingName: String,
    category: String,
    brandName: String?,
    level: ClothingLevel,
    condition: ClothingCondition,
    size: String,
    appraisalPrice: Int,
    sellingPrice: Int,
    chosenPayoutIsPoints: Boolean
  ) -> Unit
) {
  var phoneQuery by remember { mutableStateOf("081-234-5678") }
  var selectedCustomer by remember { mutableStateOf(customers.firstOrNull()) }

  var clothingName by remember { mutableStateOf("เสื้อยืดสกรีนวินเทจ") }
  var category by remember { mutableStateOf("เสื้อยืด") }
  var brandName by remember { mutableStateOf("Street Style") }
  var level by remember { mutableStateOf(ClothingLevel.GENERAL) }
  var condition by remember { mutableStateOf(ClothingCondition.VERY_GOOD) }
  var size by remember { mutableStateOf("L") }

  var appraisalPriceText by remember { mutableStateOf("60") }
  var customSellingPriceText by remember { mutableStateOf("") }
  var chosenPayoutIsPoints by remember { mutableStateOf(true) }

  var showSuccessDialog by remember { mutableStateOf(false) }
  var lastAppraisalSummary by remember { mutableStateOf("") }

  val appraisalPrice = appraisalPriceText.toIntOrNull() ?: 0
  val calculatedPoints = PointsCalculator.calculatePointsOffer(appraisalPrice)
  val recommendedSelling = PointsCalculator.calculateRecommendedSellingPrice(appraisalPrice)
  val actualSellingPrice = customSellingPriceText.toIntOrNull() ?: recommendedSelling

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("staff_appraisal_screen"),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header
    item {
      Column {
        Text(
          text = "ประเมินราคารับซื้อเสื้อผ้า",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthDarkGreen
        )
        Text(
          text = "ค้นหาลูกค้า, ตรวจสภาพเสื้อ, และกำหนดราคาประเมิน P",
          style = MaterialTheme.typography.bodyMedium,
          color = WearthTextSecondary
        )
      }
    }

    // Step 1: Customer Phone Search
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "1. ค้นหาลูกค้าด้วยเบอร์โทร",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = phoneQuery,
            onValueChange = { input ->
              phoneQuery = input
              selectedCustomer = customers.find { it.phone.replace("-", "").contains(input.replace("-", "")) }
            },
            placeholder = { Text("กรอกเบอร์โทรลูกค้า เช่น 081-234-5678") },
            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = WearthDarkGreen) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("staff_phone_search_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = WearthTealGreen,
              unfocusedBorderColor = WearthBorder
            )
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Quick fill chips
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            customers.forEach { c ->
              FilterChip(
                selected = selectedCustomer?.id == c.id,
                onClick = {
                  selectedCustomer = c
                  phoneQuery = c.phone
                },
                label = { Text("${c.name}", fontSize = 12.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = WearthLightTeal,
                  selectedLabelColor = WearthDarkGreen
                )
              )
            }
          }

          // Customer Info Display
          selectedCustomer?.let { c ->
            Spacer(modifier = Modifier.height(10.dp))
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(WearthLightTeal)
                .padding(12.dp)
            ) {
              Column {
                Text(
                  text = "ลูกค้า: ${c.name} (${c.phone})",
                  fontWeight = FontWeight.Bold,
                  color = WearthDarkGreen,
                  style = MaterialTheme.typography.titleSmall
                )
                Text(
                  text = "แต้มคงเหลือปัจจุบัน: ${PointsCalculator.formatPointsWithBaht(c.points)}",
                  style = MaterialTheme.typography.bodyMedium,
                  color = WearthTealGreen
                )
              }
            }
          }
        }
      }
    }

    // Step 2: Clothing Details
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "2. รายละเอียดเสื้อผ้าที่นำมาส่ง",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
          Spacer(modifier = Modifier.height(10.dp))

          OutlinedTextField(
            value = clothingName,
            onValueChange = { clothingName = it },
            label = { Text("ชื่อรายการเสื้อ") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            OutlinedTextField(
              value = category,
              onValueChange = { category = it },
              label = { Text("หมวดหมู่") },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
              value = brandName,
              onValueChange = { brandName = it },
              label = { Text("แบรนด์") },
              shape = RoundedCornerShape(12.dp),
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Level: General vs Brand
          Text(text = "ระดับเสื้อผ้า:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            FilterChip(
              selected = level == ClothingLevel.GENERAL,
              onClick = { level = ClothingLevel.GENERAL },
              label = { Text("ทั่วไป (ใช้แต้มได้ 100%)") },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = WearthLightTeal,
                selectedLabelColor = WearthDarkGreen
              )
            )
            FilterChip(
              selected = level == ClothingLevel.BRAND,
              onClick = { level = ClothingLevel.BRAND },
              label = { Text("แบรนด์ (ใช้แต้มได้ 70%)") },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = WearthShopPurpleBg,
                selectedLabelColor = WearthShopPurple
              )
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          // Condition: Very Good vs Good
          Text(text = "สภาพ:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            FilterChip(
              selected = condition == ClothingCondition.VERY_GOOD,
              onClick = { condition = ClothingCondition.VERY_GOOD },
              label = { Text("ดีมาก") },
              shape = RoundedCornerShape(12.dp)
            )
            FilterChip(
              selected = condition == ClothingCondition.GOOD,
              onClick = { condition = ClothingCondition.GOOD },
              label = { Text("ดี") },
              shape = RoundedCornerShape(12.dp)
            )
          }
        }
      }
    }

    // Step 3: Appraisal Price & Automatic Calculation Box
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "3. ราคาประเมิน P (บาท)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = appraisalPriceText,
            onValueChange = { appraisalPriceText = it },
            placeholder = { Text("เช่น 60 หรือ 200") },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("staff_appraisal_price_input"),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = WearthTealGreen,
              unfocusedBorderColor = WearthBorder
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Calculation Card
          Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WearthPointsGoldBg),
            border = BorderStroke(1.dp, WearthPointsGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Text(
                text = "ผลการคำนวณอัตโนมัติตามกติกา WEARTH:",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = WearthPointsGoldText
              )
              Spacer(modifier = Modifier.height(8.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("ทางเลือก 1: เงินสด (P)", style = MaterialTheme.typography.bodySmall)
                Text("$appraisalPrice บาท", fontWeight = FontWeight.Bold)
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("ทางเลือก 2: แต้ม (P × 1.25)", style = MaterialTheme.typography.bodySmall, color = WearthTealGreen)
                Text(
                  "$calculatedPoints แต้ม (= $calculatedPoints บาท)",
                  fontWeight = FontWeight.Bold,
                  color = WearthTealGreen
                )
              }
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Text("ราคาขายหน้าร้านแนะนำ (P × 1.6)", style = MaterialTheme.typography.bodySmall)
                Text("$recommendedSelling บาท", fontWeight = FontWeight.Bold, color = WearthDarkGreen)
              }
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Editable Selling Price
          OutlinedTextField(
            value = customSellingPriceText,
            onValueChange = { customSellingPriceText = it },
            label = { Text("ราคาขายหน้าร้านจริง (ค่าเริ่มต้น $recommendedSelling บาท)") },
            placeholder = { Text("$recommendedSelling") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Payout Selection
          Text(
            text = "ผู้ฝากเลือกรับ:",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            FilterChip(
              selected = chosenPayoutIsPoints,
              onClick = { chosenPayoutIsPoints = true },
              label = {
                Text(
                  "🎁 รับแต้ม $calculatedPoints แต้ม (คุ้มกว่า +25%)",
                  fontWeight = FontWeight.Bold
                )
              },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = WearthLightTeal,
                selectedLabelColor = WearthDarkGreen
              ),
              modifier = Modifier.weight(1f)
            )

            FilterChip(
              selected = !chosenPayoutIsPoints,
              onClick = { chosenPayoutIsPoints = false },
              label = {
                Text(
                  "💵 รับเงินสด $appraisalPrice บาท",
                  fontWeight = FontWeight.Bold
                )
              },
              shape = RoundedCornerShape(12.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Color(0xFFE2E8F0),
                selectedLabelColor = WearthTextPrimary
              ),
              modifier = Modifier.weight(1f)
            )
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Submit Appraisal Button
          Button(
            onClick = {
              val customer = selectedCustomer ?: customers.first()
              onCompleteAppraisal(
                customer,
                clothingName,
                category,
                brandName,
                level,
                condition,
                size,
                appraisalPrice,
                actualSellingPrice,
                chosenPayoutIsPoints
              )
              lastAppraisalSummary = if (chosenPayoutIsPoints) {
                "เพิ่มแต้มให้ ${customer.name} จำนวน $calculatedPoints แต้ม (= $calculatedPoints บาท) และเพิ่มเสื้อ '$clothingName' เข้าสต็อกเรียบร้อยแล้ว!"
              } else {
                "บันทึกจ่ายเงินสดให้ ${customer.name} จำนวน $appraisalPrice บาท และเพิ่มเสื้อ '$clothingName' เข้าสต็อกเรียบร้อยแล้ว!"
              }
              showSuccessDialog = true
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("submit_appraisal_button")
          ) {
            Text(
              text = "ยืนยันการประเมิน & บันทึกเข้าระบบ",
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
          }
        }
      }
    }
  }

  if (showSuccessDialog) {
    Dialog(onDismissRequest = { showSuccessDialog = false }) {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(16.dp)
      ) {
        Column(
          modifier = Modifier.padding(20.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(54.dp)
              .clip(CircleShape)
              .background(WearthLightTeal),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = WearthDarkGreen)
          }
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = "ประเมินและบันทึกสำเร็จ!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = WearthDarkGreen
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = lastAppraisalSummary,
            style = MaterialTheme.typography.bodyMedium,
            color = WearthTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(18.dp))
          Button(
            onClick = { showSuccessDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen)
          ) {
            Text("ตกลง")
          }
        }
      }
    }
  }
}

/**
 * หน้าจอ 2: พนักงานยืนยันการแลก (กรอกรหัสรับของ ตัดแต้มและบันทึกเงิน)
 */
@Composable
fun StaffConfirmRedeemScreen(
  orders: List<RedeemOrder>,
  onConfirmPickup: (String) -> Boolean
) {
  var orderCodeInput by remember { mutableStateOf("") }
  var feedbackMessage by remember { mutableStateOf<String?>(null) }
  var isError by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("staff_confirm_redeem_screen"),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column {
        Text(
          text = "ยืนยันการแลกเสื้อ / ส่งมอบของ",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthDarkGreen
        )
        Text(
          text = "กรอกรหัสรับของจากลูกค้าเพื่อตัดแต้มและบันทึกเงินสดที่รับ",
          style = MaterialTheme.typography.bodyMedium,
          color = WearthTextSecondary
        )
      }
    }

    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "กรอกรหัสรับของ (เช่น #WE-7842)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(8.dp))

          OutlinedTextField(
            value = orderCodeInput,
            onValueChange = {
              orderCodeInput = it
              feedbackMessage = null
            },
            placeholder = { Text("#WE-XXXX") },
            leadingIcon = { Icon(Icons.Default.QrCodeScanner, contentDescription = null, tint = WearthDarkGreen) },
            shape = RoundedCornerShape(14.dp),
            singleLine = true,
            modifier = Modifier
              .fillMaxWidth()
              .testTag("staff_pickup_code_input")
          )

          Spacer(modifier = Modifier.height(14.dp))

          Button(
            onClick = {
              if (orderCodeInput.isBlank()) {
                feedbackMessage = "กรุณากรอกรหัสรับของ"
                isError = true
              } else {
                val success = onConfirmPickup(orderCodeInput)
                if (success) {
                  feedbackMessage = "✓ ยืนยันการส่งมอบและบันทึกการรับเงินสำเร็จแล้ว!"
                  isError = false
                } else {
                  feedbackMessage = "ไม่พบรหัสรับของนี้ในระบบ หรืออาจรับไปแล้ว"
                  isError = true
                }
              }
            },
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
            modifier = Modifier
              .fillMaxWidth()
              .height(48.dp)
              .testTag("staff_confirm_pickup_button")
          ) {
            Text("ยืนยันการส่งมอบเสื้อ & ชำระเงิน", fontWeight = FontWeight.Bold)
          }

          feedbackMessage?.let { msg ->
            Spacer(modifier = Modifier.height(10.dp))
            Text(
              text = msg,
              color = if (isError) Color(0xFFDC2626) else WearthDarkGreen,
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.bodyMedium
            )
          }
        }
      }
    }

    item {
      Text(
        text = "รายการคำสั่งแลกเสื้อล่าสุด (${orders.size} รายการ)",
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = WearthTextPrimary
      )
    }

    items(orders) { order ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { orderCodeInput = order.orderCode }
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = order.orderCode,
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = WearthDarkGreen
            )
            Text(
              text = order.status,
              color = if (order.status == "รับของสำเร็จ") WearthDarkGreen else WearthShopPurple,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (order.status == "รับของสำเร็จ") WearthLightTeal else WearthShopPurpleBg)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }

          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "ลูกค้า: ${order.customerName} (${order.customerPhone})",
            style = MaterialTheme.typography.bodyMedium
          )
          Text(
            text = "เสื้อ: ${order.itemName} (ราคาป้าย ฿${order.itemPrice})",
            style = MaterialTheme.typography.bodySmall,
            color = WearthTextSecondary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(
              text = "แต้มที่ตัด: ${order.pointsUsed} แต้ม",
              color = WearthPointsGoldText,
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
            Text(
              text = "เงินสดที่ต้องรับ: ${order.cashPaid} บาท",
              color = Color(0xFFB45309),
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
        }
      }
    }
  }
}

/**
 * หน้าจอ 3: สต็อกเสื้อในร้าน
 */
@Composable
fun StaffInventoryScreen(
  items: List<ClothingItem>
) {
  var selectedStatusFilter by remember { mutableStateOf<ItemStatus?>(null) }

  val filtered = items.filter { item ->
    selectedStatusFilter == null || item.status == selectedStatusFilter
  }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("staff_inventory_screen"),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    item {
      Column {
        Text(
          text = "สต็อกเสื้อในร้าน WEARTH",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthDarkGreen
        )
        Text(
          text = "รายการเสื้อ สถานะว่าง, ถูกจอง, ขายแล้ว",
          style = MaterialTheme.typography.bodyMedium,
          color = WearthTextSecondary
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        FilterChip(
          selected = selectedStatusFilter == null,
          onClick = { selectedStatusFilter = null },
          label = { Text("ทั้งหมด (${items.size})") }
        )
        FilterChip(
          selected = selectedStatusFilter == ItemStatus.AVAILABLE,
          onClick = { selectedStatusFilter = ItemStatus.AVAILABLE },
          label = { Text("ว่าง (${items.count { it.status == ItemStatus.AVAILABLE }})") }
        )
        FilterChip(
          selected = selectedStatusFilter == ItemStatus.RESERVED,
          onClick = { selectedStatusFilter = ItemStatus.RESERVED },
          label = { Text("ถูกจอง (${items.count { it.status == ItemStatus.RESERVED }})") }
        )
        FilterChip(
          selected = selectedStatusFilter == ItemStatus.SOLD,
          onClick = { selectedStatusFilter = ItemStatus.SOLD },
          label = { Text("ขายแล้ว (${items.count { it.status == ItemStatus.SOLD }})") }
        )
      }
    }

    items(filtered) { item ->
      Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = item.name,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = item.level.label,
                fontSize = 10.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                  .clip(RoundedCornerShape(4.dp))
                  .background(if (item.isBrand) WearthShopPurple else WearthDarkGreen)
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "หมวด: ${item.category} • ไซซ์: ${item.size} • สภาพ: ${item.condition.label}",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary,
              fontSize = 11.sp
            )
            Text(
              text = "ราคาขาย: ฿${item.price} (รับซื้อมา ฿${item.appraisalPrice})",
              style = MaterialTheme.typography.bodySmall,
              color = WearthDarkGreen,
              fontWeight = FontWeight.SemiBold
            )
          }

          // Status Badge
          Text(
            text = item.status.label,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = when (item.status) {
              ItemStatus.AVAILABLE -> WearthTealGreen
              ItemStatus.RESERVED -> WearthShopPurple
              ItemStatus.SOLD -> Color.Gray
            },
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(
                when (item.status) {
                  ItemStatus.AVAILABLE -> WearthLightTeal
                  ItemStatus.RESERVED -> WearthShopPurpleBg
                  ItemStatus.SOLD -> Color(0xFFF1F5F9)
                }
              )
              .padding(horizontal = 10.dp, vertical = 4.dp)
          )
        }
      }
    }
  }
}

/**
 * หน้าจอ 4: แดชบอร์ดสรุป (เสื้อที่รับวันนี้, แต้มที่แจก, เงินสดที่รับ, สต็อก + กราฟแท่ง)
 */
@Composable
fun StaffDashboardScreen(
  evaluatedToday: Int,
  pointsDistributed: Int,
  cashReceived: Int,
  totalStock: Int
) {
  val days = listOf("จ.", "อ.", "พ.", "พฤ.", "ศ.", "ส.", "อา.")
  val weeklyCounts = listOf(4, 7, 5, 8, 12, 16, evaluatedToday)

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("staff_dashboard_screen"),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column {
        Text(
          text = "แดชบอร์ดสรุปประจำวัน",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthDarkGreen
        )
        Text(
          text = "ภาพรวมผลการดำเนินงานและการหมุนเวียนแฟชั่น",
          style = MaterialTheme.typography.bodyMedium,
          color = WearthTextSecondary
        )
      }
    }

    // 4 Key Stat Cards
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "เสื้อที่รับวันนี้",
          value = "$evaluatedToday ตัว",
          color = WearthDarkGreen,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "แต้มที่แจกไป",
          value = "$pointsDistributed แต้ม",
          color = WearthTealGreen,
          modifier = Modifier.weight(1f)
        )
      }
    }

    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        MetricCard(
          title = "เงินสดที่รับได้",
          value = "฿$cashReceived",
          color = WearthPointsGoldText,
          modifier = Modifier.weight(1f)
        )
        MetricCard(
          title = "เสื้อในสต็อกรวม",
          value = "$totalStock ตัว",
          color = WearthShopPurple,
          modifier = Modifier.weight(1f)
        )
      }
    }

    // Weekly Intake Bar Chart
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "สถิติจำนวนเสื้อที่รับเข้ามาสัปดาห์นี้ (ตัว)",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
          Spacer(modifier = Modifier.height(16.dp))

          // Compose Canvas Bar Chart
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .height(140.dp)
          ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
              val maxVal = (weeklyCounts.maxOrNull() ?: 16).toFloat()
              val barWidth = 28.dp.toPx()
              val spacing = (size.width - (barWidth * weeklyCounts.size)) / (weeklyCounts.size + 1)

              weeklyCounts.forEachIndexed { index, count ->
                val x = spacing + index * (barWidth + spacing)
                val barHeight = (count / maxVal) * (size.height - 30.dp.toPx())
                val y = size.height - 24.dp.toPx() - barHeight

                // Draw Bar
                drawRoundRect(
                  color = if (index == weeklyCounts.lastIndex) Color(0xFF14532D) else Color(0xFF86EFAC),
                  topLeft = Offset(x, y),
                  size = Size(barWidth, barHeight),
                  cornerRadius = CornerRadius(6.dp.toPx(), 6.dp.toPx())
                )
              }
            }

            // Labels below bars
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .padding(horizontal = 10.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              days.forEach { day ->
                Text(
                  text = day,
                  style = MaterialTheme.typography.bodySmall,
                  fontSize = 11.sp,
                  color = WearthTextSecondary
                )
              }
            }
          }
        }
      }
    }

    // Eco Impact Card
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WearthLightTeal),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "🌱 ผลกระทบเชิงบวกต่อสิ่งแวดล้อม",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WearthDarkGreen
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "ลดขยะสิ่งทอสะสม: ${(totalStock * 0.45).toInt()} กิโลกรัม\nประหยัดน้ำในการผลิตเสื้อผ้าใหม่ได้กว่า ${(totalStock * 2700)} ลิตร",
            style = MaterialTheme.typography.bodyMedium,
            color = WearthTealGreen,
            lineHeight = 22.sp
          )
        }
      }
    }
  }
}

@Composable
fun MetricCard(
  title: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = modifier
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = title,
        style = MaterialTheme.typography.bodySmall,
        color = WearthTextSecondary,
        fontSize = 11.sp
      )
      Spacer(modifier = Modifier.height(4.dp))
      Text(
        text = value,
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.ExtraBold,
        color = color
      )
    }
  }
}

/**
 * หน้าจอจัดการงานรับเสื้อถึงบ้าน และจัดส่งพัสดุถึงบ้าน สำหรับพนักงาน
 */
@Composable
fun StaffPickupDeliveryScreen(
  depositRequests: List<DepositRequest>,
  orders: List<RedeemOrder>,
  onUpdatePickupStatus: (String, String) -> Unit,
  onUpdateDeliveryStatus: (String, String) -> Unit,
  onNavigateToAppraisalWithCustomer: (String) -> Unit
) {
  var selectedSubTab by remember { mutableStateOf(0) } // 0 = งานรับเสื้อถึงบ้าน, 1 = งานจัดส่งพัสดุ

  val homePickupRequests = depositRequests.filter { it.intakeType == DepositIntakeType.HOME_PICKUP }
  val homeDeliveryOrders = orders.filter { it.deliveryType == DeliveryType.HOME_DELIVERY }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("staff_pickup_delivery_screen"),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    item {
      Column {
        Text(
          text = "จัดการงานรับ-ส่งถึงที่ (Eco Delivery & Rider)",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthDarkGreen
        )
        Text(
          text = "ติดตามตำแหน่งปักหมุด คิวเข้ารับเสื้อ และการจัดส่งพัสดุ",
          style = MaterialTheme.typography.bodyMedium,
          color = WearthTextSecondary
        )
      }
    }

    // Sub-Tabs
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(14.dp))
          .background(Color.White)
          .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
      ) {
        Button(
          onClick = { selectedSubTab = 0 },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (selectedSubTab == 0) WearthDarkGreen else Color.Transparent
          ),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "🛵 งานรับเสื้อ (${homePickupRequests.size})",
            color = if (selectedSubTab == 0) Color.White else WearthTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Button(
          onClick = { selectedSubTab = 1 },
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(
            containerColor = if (selectedSubTab == 1) WearthTealGreen else Color.Transparent
          ),
          modifier = Modifier.weight(1f)
        ) {
          Text(
            text = "🚚 จัดส่งพัสดุ (${homeDeliveryOrders.size})",
            color = if (selectedSubTab == 1) Color.White else WearthTextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }
      }
    }

    if (selectedSubTab == 0) {
      // --- SUB-TAB 0: งานเข้ารับเสื้อถึงบ้าน ---
      if (homePickupRequests.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
              Text("ยังไม่มีรายการเรียกรถเข้ารับเสื้อถึงบ้าน", color = WearthTextSecondary)
            }
          }
        }
      }

      items(homePickupRequests) { req ->
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = req.trackingCode,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = WearthDarkGreen
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "(${req.numberOfClothes} ตัว)",
                  style = MaterialTheme.typography.bodySmall,
                  color = WearthTealGreen,
                  fontWeight = FontWeight.Bold
                )
              }

              Text(
                text = req.status,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = WearthDarkGreen,
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(WearthLightTeal)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "ลูกค้า: ${req.customerName} • โทร: ${req.phone}",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.SemiBold
            )
            Text(
              text = "ประเภทเสื้อ: ${req.category} (แบรนด์: ${req.brandName})",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary
            )
            Text(
              text = "ช่วงเวลานัดเข้ารับ: ${req.appointmentDate}",
              style = MaterialTheme.typography.bodySmall,
              color = WearthDarkGreen,
              fontWeight = FontWeight.Bold
            )

            // Pinned Address Box
            req.pickupAddress?.let { addr ->
              Spacer(modifier = Modifier.height(10.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(WearthBackground)
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍 ปักหมุด: ${addr.label} (${addr.recipientName})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = WearthDarkGreen)
                  }
                  Text(text = addr.fullDisplayAddress, fontSize = 11.sp, color = WearthTextSecondary)
                  if (addr.noteToRider.isNotBlank()) {
                    Text(text = "📝 โน้ตถึงไรเดอร์: ${addr.noteToRider}", fontSize = 11.sp, color = WearthTealGreen, fontWeight = FontWeight.SemiBold)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Action Buttons for Staff/Rider
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (req.status != "ไรเดอร์รับเสื้อแล้ว" && req.status != "ตรวจสภาพสำเร็จ") {
                Button(
                  onClick = { onUpdatePickupStatus(req.id, "ไรเดอร์กำลังเดินทางเข้ารับ") },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = WearthTealGreen),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("กำลังไปรับ", fontSize = 11.sp)
                }

                Button(
                  onClick = { onUpdatePickupStatus(req.id, "ไรเดอร์รับเสื้อแล้ว") },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("รับเสื้อแล้ว", fontSize = 11.sp)
                }
              } else {
                Button(
                  onClick = {
                    onUpdatePickupStatus(req.id, "ตรวจสภาพสำเร็จ")
                    onNavigateToAppraisalWithCustomer(req.phone)
                  },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5336)),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Text("✨ เสื้อถึงสาขาแล้ว -> ไปหน้าประเมินราคา", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
              }
            }
          }
        }
      }
    } else {
      // --- SUB-TAB 1: งานจัดส่งพัสดุถึงบ้าน ---
      if (homeDeliveryOrders.isEmpty()) {
        item {
          Card(
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            modifier = Modifier.fillMaxWidth()
          ) {
            Box(modifier = Modifier.padding(32.dp), contentAlignment = Alignment.Center) {
              Text("ยังไม่มีรายการจัดส่งพัสดุถึงบ้าน", color = WearthTextSecondary)
            }
          }
        }
      }

      items(homeDeliveryOrders) { order ->
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = order.orderCode,
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = WearthDarkGreen
                )
                Text(
                  text = "เลขพัสดุ: ${order.trackingCode ?: "TH-WE-XXXX"}",
                  style = MaterialTheme.typography.bodySmall,
                  color = WearthTealGreen,
                  fontWeight = FontWeight.Bold
                )
              }

              Text(
                text = order.status,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = if (order.status == "จัดส่งสำเร็จ") WearthDarkGreen else WearthShopPurple,
                modifier = Modifier
                  .clip(RoundedCornerShape(8.dp))
                  .background(if (order.status == "จัดส่งสำเร็จ") WearthLightTeal else WearthShopPurpleBg)
                  .padding(horizontal = 8.dp, vertical = 3.dp)
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
              text = "สินค้า: ${order.itemName} (ราคาป้าย ฿${order.itemPrice})",
              style = MaterialTheme.typography.bodyMedium,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "ผู้รับ: ${order.customerName} • โทร: ${order.customerPhone}",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary
            )

            // Pinned Delivery Address
            order.shippingAddress?.let { addr ->
              Spacer(modifier = Modifier.height(10.dp))
              Box(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .background(WearthBackground)
                  .padding(10.dp)
              ) {
                Column {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "📍 ปักหมุดส่งถึง: ${addr.label} (${addr.recipientName})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = WearthDarkGreen)
                  }
                  Text(text = addr.fullDisplayAddress, fontSize = 11.sp, color = WearthTextSecondary)
                  if (addr.noteToRider.isNotBlank()) {
                    Text(text = "📝 โน้ต: ${addr.noteToRider}", fontSize = 11.sp, color = WearthTealGreen)
                  }
                }
              }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Delivery Status Progress Buttons
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              if (order.status != "จัดส่งสำเร็จ") {
                Button(
                  onClick = { onUpdateDeliveryStatus(order.id, "ไรเดอร์กำลังนำส่ง") },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = WearthTealGreen),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("กำลังนำส่ง", fontSize = 11.sp)
                }

                Button(
                  onClick = { onUpdateDeliveryStatus(order.id, "จัดส่งสำเร็จ") },
                  shape = RoundedCornerShape(10.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
                  modifier = Modifier.weight(1f)
                ) {
                  Text("จัดส่งสำเร็จแล้ว", fontSize = 11.sp)
                }
              } else {
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                  horizontalArrangement = Arrangement.Center
                ) {
                  Text("✓ จัดส่งพัสดุถึงมือลูกค้าเรียบร้อยแล้ว", color = WearthDarkGreen, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }
          }
        }
      }
    }
  }
}
