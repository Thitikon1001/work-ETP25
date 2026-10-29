package com.example.wearth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthBorder
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthDepositBg
import com.example.ui.theme.WearthGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGold
import com.example.ui.theme.WearthPointsGoldBg
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthShopPurple
import com.example.ui.theme.WearthShopPurpleBg
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailSheet(
  item: ClothingItem,
  customer: Customer,
  pinnedAddress: DeliveryAddress,
  onOpenLocationPicker: () -> Unit,
  onDismiss: () -> Unit,
  onConfirmRedeem: (calc: RedemptionCalculation, deliveryType: DeliveryType, address: DeliveryAddress?, shippingFee: Int) -> Unit
) {
  val context = LocalContext.current
  val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
  var showConfirmDialog by remember { mutableStateOf(false) }

  // Delivery Choice: Store Pickup vs Home Delivery
  var deliveryType by remember { mutableStateOf(DeliveryType.HOME_DELIVERY) }
  val shippingFee = if (deliveryType == DeliveryType.HOME_DELIVERY) 35 else 0

  // Calculation based on strict rules
  val calc = remember(item, customer.points) {
    PointsCalculator.calculateRedemption(
      itemPrice = item.price,
      isBrand = item.isBrand,
      customerPoints = customer.points
    )
  }

  val totalCashToPay = calc.cashRequired + shippingFee

  ModalBottomSheet(
    onDismissRequest = onDismiss,
    sheetState = sheetState,
    containerColor = Color.White,
    shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
  ) {
    Column(
      modifier = Modifier
        .fillMaxWidth()
        .verticalScroll(rememberScrollState())
        .padding(horizontal = 20.dp, vertical = 8.dp)
        .testTag("item_detail_sheet")
    ) {
      // Top bar with close button
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = "รายละเอียดสินค้า",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        IconButton(onClick = onDismiss) {
          Icon(Icons.Default.Close, contentDescription = "ปิด")
        }
      }

      // Large Image
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(240.dp)
          .clip(RoundedCornerShape(20.dp))
      ) {
        Image(
          painter = painterResource(id = ImageHelper.getDrawableId(context, item.imageDrawableName)),
          contentDescription = item.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Level Tag
        Text(
          text = if (item.isBrand) "แบรนด์ (ใช้แต้มได้สูงสุด 70%)" else "ทั่วไป (ใช้แต้มได้ 100%)",
          color = Color.White,
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          modifier = Modifier
            .padding(12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(if (item.isBrand) WearthShopPurple else WearthDarkGreen)
            .padding(horizontal = 10.dp, vertical = 4.dp)
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // Title & Price
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column(modifier = Modifier.weight(1f)) {
          Text(
            text = item.name,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
          if (!item.brandName.isNullOrBlank()) {
            Text(
              text = "แบรนด์: ${item.brandName}",
              style = MaterialTheme.typography.bodyMedium,
              color = WearthTealGreen,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = "฿ ${item.price}",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.ExtraBold,
            color = WearthDarkGreen
          )
          Text(
            text = "ราคาขายหน้าร้าน",
            style = MaterialTheme.typography.bodySmall,
            color = WearthTextSecondary
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Attributes: Size, Condition, Category
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        AttributeChip(label = "ไซซ์: ${item.size}")
        AttributeChip(label = "สภาพ: ${item.condition.label}")
        AttributeChip(label = "หมวด: ${item.category}")
      }

      Spacer(modifier = Modifier.height(10.dp))
      Text(
        text = item.description,
        style = MaterialTheme.typography.bodyMedium,
        color = WearthTextSecondary
      )

      Spacer(modifier = Modifier.height(18.dp))

      // --- DELIVERY METHOD SELECTOR (ส่งถึงที่ vs รับที่ร้าน) ---
      Text(
        text = "เลือกวิธีการรับสินค้า:",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = WearthDarkGreen
      )
      Spacer(modifier = Modifier.height(8.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Option: จัดส่งพัสดุถึงบ้าน (Eco Delivery)
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (deliveryType == DeliveryType.HOME_DELIVERY) WearthDepositBg else WearthBackground
          ),
          border = if (deliveryType == DeliveryType.HOME_DELIVERY) BorderStroke(1.5.dp, WearthTealGreen) else null,
          modifier = Modifier
            .weight(1f)
            .clickable { deliveryType = DeliveryType.HOME_DELIVERY }
            .testTag("select_delivery_home")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.LocalShipping,
              contentDescription = null,
              tint = if (deliveryType == DeliveryType.HOME_DELIVERY) WearthTealGreen else Color.Gray,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "จัดส่งถึงบ้าน 🚚",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = if (deliveryType == DeliveryType.HOME_DELIVERY) WearthDarkGreen else WearthTextPrimary
              )
              Text(
                text = "ค่าส่ง 35 บาท",
                style = MaterialTheme.typography.bodySmall,
                color = WearthTealGreen,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }

        // Option: รับเองที่ร้าน
        Card(
          shape = RoundedCornerShape(14.dp),
          colors = CardDefaults.cardColors(
            containerColor = if (deliveryType == DeliveryType.STORE_PICKUP) WearthLightTeal else WearthBackground
          ),
          border = if (deliveryType == DeliveryType.STORE_PICKUP) BorderStroke(1.5.dp, WearthDarkGreen) else null,
          modifier = Modifier
            .weight(1f)
            .clickable { deliveryType = DeliveryType.STORE_PICKUP }
            .testTag("select_delivery_store")
        ) {
          Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Storefront,
              contentDescription = null,
              tint = if (deliveryType == DeliveryType.STORE_PICKUP) WearthDarkGreen else Color.Gray,
              modifier = Modifier.size(24.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Column {
              Text(
                text = "รับเองที่ร้าน 🏬",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
                color = if (deliveryType == DeliveryType.STORE_PICKUP) WearthDarkGreen else WearthTextPrimary
              )
              Text(
                text = "ฟรี ไม่มีค่าส่ง",
                style = MaterialTheme.typography.bodySmall,
                color = WearthDarkGreen,
                fontWeight = FontWeight.SemiBold
              )
            }
          }
        }
      }

      // If Home Delivery: show pinned address
      if (deliveryType == DeliveryType.HOME_DELIVERY) {
        Spacer(modifier = Modifier.height(12.dp))
        PinnedAddressSummaryCard(
          address = pinnedAddress,
          onEditClick = onOpenLocationPicker
        )
      }

      Spacer(modifier = Modifier.height(16.dp))

      // --- AUTOMATIC POINTS CALCULATION BOX ---
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = WearthPointsGoldBg),
        border = BorderStroke(1.5.dp, WearthPointsGold.copy(alpha = 0.5f)),
        modifier = Modifier
          .fillMaxWidth()
          .testTag("points_calculation_box")
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "🪙", fontSize = 20.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "กล่องคำนวณการใช้แต้มแลกเสื้อ",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WearthPointsGoldText
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          // Rule description
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Info,
              contentDescription = null,
              tint = WearthPointsGoldText,
              modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (item.isBrand)
                "เสื้อแบรนด์: ใช้แต้มได้สูงสุด 70% ของราคา (${calc.maxPointsAllowed} แต้ม) ส่วนที่เหลือจ่ายเป็นเงิน"
              else
                "เสื้อทั่วไป: ใช้แต้มได้เต็ม 100% ของราคา (${calc.maxPointsAllowed} แต้ม)",
              style = MaterialTheme.typography.bodySmall,
              color = WearthPointsGoldText,
              fontWeight = FontWeight.Medium
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          // Line 1: Your points
          CalculationRow(
            label = "แต้มที่คุณมี (${customer.name})",
            value = PointsCalculator.formatPointsWithBaht(customer.points),
            isBold = false
          )

          // Line 2: Points to use
          CalculationRow(
            label = "ใช้แต้มแลกค่าเสื้อ",
            value = "- ${calc.pointsUsed} แต้ม (= ${calc.pointsUsed} บาท)",
            valueColor = WearthDarkGreen,
            isBold = true
          )

          // Line 3: Cash to pay for item
          CalculationRow(
            label = "เงินสดส่วนต่างค่าเสื้อ",
            value = "${calc.cashRequired} บาท",
            valueColor = if (calc.cashRequired > 0) Color(0xFFB45309) else WearthTealGreen,
            isBold = true
          )

          // Line 4: Shipping Fee (If Home Delivery)
          if (deliveryType == DeliveryType.HOME_DELIVERY) {
            CalculationRow(
              label = "ค่าจัดส่งพัสดุถึงบ้าน",
              value = "+ $shippingFee บาท",
              valueColor = WearthShopPurple,
              isBold = true
            )
          }

          Divider(
            color = WearthPointsGold.copy(alpha = 0.3f),
            modifier = Modifier.padding(vertical = 8.dp)
          )

          // Line 5: Total Cash
          CalculationRow(
            label = "รวมยอดเงินสดที่ต้องชำระทั้งหมด",
            value = "$totalCashToPay บาท",
            valueColor = if (totalCashToPay > 0) Color(0xFFDC2626) else WearthDarkGreen,
            isBold = true
          )

          // Line 6: Remaining Points
          CalculationRow(
            label = "แต้มคงเหลือหลังแลก",
            value = "${calc.remainingCustomerPoints} แต้ม (= ${calc.remainingCustomerPoints} บาท)",
            valueColor = WearthTextPrimary,
            isBold = true
          )
        }
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Summary Statement
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .clip(RoundedCornerShape(12.dp))
          .background(WearthLightTeal)
          .padding(12.dp)
      ) {
        Text(
          text = if (deliveryType == DeliveryType.HOME_DELIVERY) {
            "สรุป: ใช้ ${calc.pointsUsed} แต้ม + ยอดชำระ ${totalCashToPay} บาท (รวมค่าจัดส่งถึงที่)"
          } else {
            "สรุป: ใช้ ${calc.pointsUsed} แต้ม" + (if (totalCashToPay > 0) " + ชำระหน้าร้าน ${totalCashToPay} บาท" else " (ไม่มีค่าใช้จ่ายเพิ่ม)")
          },
          color = WearthDarkGreen,
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.bodyMedium
        )
      }

      Spacer(modifier = Modifier.height(18.dp))

      // Button "แลกเลย"
      Button(
        onClick = { showConfirmDialog = true },
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
        modifier = Modifier
          .fillMaxWidth()
          .height(52.dp)
          .testTag("confirm_redeem_button")
      ) {
        Icon(
          imageVector = Icons.Default.ShoppingBag,
          contentDescription = null,
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = if (deliveryType == DeliveryType.HOME_DELIVERY) "แลกและจัดส่งถึงบ้าน (จำลองการชำระ)" else "แลกเลย (รับที่ร้าน)",
          color = Color.White,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Confirmation Alert Dialog
  if (showConfirmDialog) {
    androidx.compose.material3.AlertDialog(
      onDismissRequest = { showConfirmDialog = false },
      title = {
        Text(
          text = if (deliveryType == DeliveryType.HOME_DELIVERY) "ยืนยันการแลกและจัดส่งถึงบ้าน" else "ยืนยันการแลกเสื้อ",
          fontWeight = FontWeight.Bold,
          color = WearthDarkGreen
        )
      },
      text = {
        Column {
          Text(text = "ต้องการแลก '${item.name}' หรือไม่?")
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "• วิธีการรับ: ${deliveryType.label}\n" +
              (if (deliveryType == DeliveryType.HOME_DELIVERY) "• จัดส่งไปที่: ${pinnedAddress.fullDisplayAddress}\n" else "") +
              "• ใช้แต้ม: ${calc.pointsUsed} แต้ม (= ${calc.pointsUsed} บาท)\n" +
              "• ยอดเงินสดรวม: $totalCashToPay บาท",
            style = MaterialTheme.typography.bodyMedium,
            color = WearthTextSecondary
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            showConfirmDialog = false
            onConfirmRedeem(
              calc,
              deliveryType,
              if (deliveryType == DeliveryType.HOME_DELIVERY) pinnedAddress else null,
              shippingFee
            )
          },
          colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
          modifier = Modifier.testTag("modal_confirm_redeem")
        ) {
          Text("ยืนยันแลกเสื้อ")
        }
      },
      dismissButton = {
        androidx.compose.material3.TextButton(onClick = { showConfirmDialog = false }) {
          Text("ยกเลิก")
        }
      }
    )
  }
}

@Composable
fun CalculationRow(
  label: String,
  value: String,
  valueColor: Color = WearthTextPrimary,
  isBold: Boolean = false
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodyMedium,
      color = WearthTextSecondary
    )
    Text(
      text = value,
      style = MaterialTheme.typography.bodyMedium,
      color = valueColor,
      fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal
    )
  }
}

@Composable
fun AttributeChip(label: String) {
  Box(
    modifier = Modifier
      .clip(RoundedCornerShape(8.dp))
      .background(WearthBackground)
      .padding(horizontal = 10.dp, vertical = 6.dp)
  ) {
    Text(
      text = label,
      style = MaterialTheme.typography.bodySmall,
      color = WearthTextPrimary,
      fontWeight = FontWeight.Medium
    )
  }
}
