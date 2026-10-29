package com.example.wearth

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Moped
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthBorder
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthDepositBg
import com.example.ui.theme.WearthDepositColor
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGold
import com.example.ui.theme.WearthPointsGoldBg
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

@Composable
fun DepositScreen(
  customer: Customer,
  pinnedAddress: DeliveryAddress,
  existingRequests: List<DepositRequest> = emptyList(),
  activeAppraisal: QuickAppraisalResult? = null,
  onOpenCameraScanner: () -> Unit,
  onClearActiveAppraisal: () -> Unit = {},
  onOpenRates: () -> Unit,
  onOpenLocationPicker: () -> Unit,
  onSubmitDeposit: (DepositRequest) -> Unit
) {
  var intakeType by remember { mutableStateOf(DepositIntakeType.HOME_PICKUP) }
  var clothesCount by remember { mutableIntStateOf(3) }
  var category by remember { mutableStateOf("เสื้อยืด") }
  var brandName by remember { mutableStateOf("") }
  var size by remember { mutableStateOf("M") }
  var condition by remember { mutableStateOf(ClothingCondition.VERY_GOOD) }
  var appointmentDate by remember { mutableStateOf("วันนี้ (14:00 - 16:00 น.)") }
  var imageAttached by remember { mutableStateOf(false) }
  var showSuccessDialog by remember { mutableStateOf(false) }
  var createdTrackingCode by remember { mutableStateOf("") }

  LaunchedEffect(activeAppraisal) {
    activeAppraisal?.let {
      category = it.category
      if (it.level == ClothingLevel.BRAND) {
        brandName = it.detectedBrand
      }
      size = it.size
      condition = it.condition
      imageAttached = true
    }
  }

  val pickupFee = if (intakeType == DepositIntakeType.STORE_DROPOFF || clothesCount >= 3) 0 else 40

  val categories = listOf("เสื้อยืด", "เสื้อเชิ้ต", "แจ็กเก็ต", "กางเกง", "รวมหลายประเภท")
  val sizes = listOf("S", "M", "L", "XL", "คละไซซ์")
  val timeSlots = listOf(
    "วันนี้ (14:00 - 16:00 น.)",
    "พรุ่งนี้ (10:00 - 12:00 น.)",
    "พรุ่งนี้ (14:00 - 16:00 น.)",
    "วันเสาร์นี้ (11:00 - 13:00 น.)"
  )

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("deposit_screen"),
    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // 1. Header
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = "แลกเสื้อ / ส่งเสื้อรับแต้ม",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = WearthDarkGreen
          )
          Text(
            text = "เลือกนำมาส่งที่ร้าน หรือเรียกรถเข้ารับถึงหน้าบ้าน",
            style = MaterialTheme.typography.bodyMedium,
            color = WearthTextSecondary
          )
        }
      }
    }

    // 1.1 CameraX Quick Appraisal Scanner Card
    item {
      if (activeAppraisal != null) {
        // Active Appraisal Applied Banner
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = WearthLightTeal),
          border = BorderStroke(1.5.dp, WearthTealGreen),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("active_appraisal_banner")
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.Top
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(WearthDarkGreen),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = Color(0xFFA7F3D0),
                    modifier = Modifier.size(20.dp)
                  )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = "ข้อมูลจากการสแกน CameraX ✨",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = WearthDarkGreen
                  )
                  Text(
                    text = activeAppraisal.itemName,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = WearthTextPrimary
                  )
                }
              }

              IconButton(
                onClick = onClearActiveAppraisal,
                modifier = Modifier.size(28.dp)
              ) {
                Icon(
                  imageVector = Icons.Default.Close,
                  contentDescription = "ล้างข้อมูลสแกน",
                  tint = WearthTextSecondary,
                  modifier = Modifier.size(18.dp)
                )
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(Color.White)
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "ประเมินรับซื้อเบื้องต้น",
                  style = MaterialTheme.typography.labelSmall,
                  color = WearthTextSecondary
                )
                Text(
                  text = "฿${activeAppraisal.estimatedAppraisalPrice}",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.Bold,
                  color = WearthTextPrimary
                )
              }

              Column(horizontalAlignment = Alignment.End) {
                Text(
                  text = "แต้มสะสม WEARTH (+25%)",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color(0xFF059669),
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "${activeAppraisal.pointsReward} แต้ม (= ${activeAppraisal.pointsReward} บ.)",
                  style = MaterialTheme.typography.titleMedium,
                  fontWeight = FontWeight.ExtraBold,
                  color = Color(0xFF059669)
                )
              }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              Text(
                text = "📸 สแกนเสื้อตัวอื่นใหม่ >",
                color = WearthTealGreen,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                  .clickable { onOpenCameraScanner() }
                  .padding(4.dp)
              )
            }
          }
        }
      } else {
        // Quick Scan CTA Button
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          border = BorderStroke(1.dp, Color(0xFFE5E7EB)),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpenCameraScanner() }
            .testTag("open_camera_scanner_banner")
        ) {
          Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Box(
              modifier = Modifier
                .size(48.dp)
                .clip(CircleShape)
                .background(WearthDarkGreen),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.CameraAlt,
                contentDescription = "สแกนด้วยกล้อง",
                tint = Color(0xFFA7F3D0),
                modifier = Modifier.size(26.dp)
              )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                  text = "สแกนเสื้อผ้าด้วยกล้อง (CameraX)",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = WearthDarkGreen
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "ใหม่",
                  style = MaterialTheme.typography.labelSmall,
                  color = Color.White,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color(0xFFE11D48))
                    .padding(horizontal = 6.dp, vertical = 1.dp)
                )
              }
              Spacer(modifier = Modifier.height(2.dp))
              Text(
                text = "ส่องกล้องถ่ายป้ายและเนื้อผ้า ประเมินราคาและแต้มด่วนอัตโนมัติ",
                style = MaterialTheme.typography.bodySmall,
                color = WearthTextSecondary,
                fontSize = 12.sp
              )
            }
          }
        }
      }
    }

    // Active Pickup Tracker Card (If customer has ongoing requests)
    val myActiveRequests = existingRequests.filter { it.customerId == customer.id }
    if (myActiveRequests.isNotEmpty()) {
      item {
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = WearthLightTeal),
          border = BorderStroke(1.dp, WearthTealGreen.copy(alpha = 0.4f)),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "🛵", fontSize = 18.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = "งานเข้ารับเสื้อที่กำลังดำเนินการ",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = WearthDarkGreen
                )
              }
              Text(
                text = "${myActiveRequests.size} รายการ",
                style = MaterialTheme.typography.labelSmall,
                color = WearthTealGreen,
                fontWeight = FontWeight.Bold
              )
            }

            Spacer(modifier = Modifier.height(8.dp))

            myActiveRequests.take(2).forEach { req ->
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(vertical = 4.dp)
                  .clip(RoundedCornerShape(10.dp))
                  .background(Color.White)
                  .padding(10.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Column {
                  Text(
                    text = "${req.trackingCode} • ${req.category}",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                  )
                  Text(
                    text = "เวลานัด: ${req.appointmentDate}",
                    style = MaterialTheme.typography.bodySmall,
                    color = WearthTextSecondary,
                    fontSize = 11.sp
                  )
                }

                Text(
                  text = req.status,
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = WearthDarkGreen,
                  modifier = Modifier
                    .clip(RoundedCornerShape(6.dp))
                    .background(WearthLightTeal)
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }
    }

    // 2. Select Intake Method: Store Dropoff vs Home Pickup
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(18.dp)) {
          Text(
            text = "เลือกช่องทางการส่งเสื้อ",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WearthDarkGreen
          )
          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            // Option 1: Home Pickup (Highlight)
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (intakeType == DepositIntakeType.HOME_PICKUP) WearthDepositBg else WearthBackground
              ),
              border = if (intakeType == DepositIntakeType.HOME_PICKUP) BorderStroke(2.dp, WearthTealGreen) else null,
              modifier = Modifier
                .weight(1f)
                .clickable { intakeType = DepositIntakeType.HOME_PICKUP }
                .testTag("select_home_pickup_option")
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (intakeType == DepositIntakeType.HOME_PICKUP) WearthTealGreen else Color(0xFFCBD5E1)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Moped, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "รับเสื้อถึงที่ 🛵",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (intakeType == DepositIntakeType.HOME_PICKUP) WearthDarkGreen else WearthTextPrimary
                )
                Text(
                  text = "ไรเดอร์ไปรับถึงบ้าน",
                  style = MaterialTheme.typography.bodySmall,
                  fontSize = 11.sp,
                  color = WearthTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "3 ตัวขึ้นไป ฟรี!",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = WearthTealGreen
                )
              }
            }

            // Option 2: Store Drop-off
            Card(
              shape = RoundedCornerShape(16.dp),
              colors = CardDefaults.cardColors(
                containerColor = if (intakeType == DepositIntakeType.STORE_DROPOFF) WearthLightTeal else WearthBackground
              ),
              border = if (intakeType == DepositIntakeType.STORE_DROPOFF) BorderStroke(2.dp, WearthDarkGreen) else null,
              modifier = Modifier
                .weight(1f)
                .clickable { intakeType = DepositIntakeType.STORE_DROPOFF }
                .testTag("select_store_dropoff_option")
            ) {
              Column(
                modifier = Modifier.padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
              ) {
                Box(
                  modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(if (intakeType == DepositIntakeType.STORE_DROPOFF) WearthDarkGreen else Color(0xFFCBD5E1)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                  text = "ส่งที่ร้านเอง 🏬",
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = if (intakeType == DepositIntakeType.STORE_DROPOFF) WearthDarkGreen else WearthTextPrimary
                )
                Text(
                  text = "จุดรับหน้าร้าน WEARTH",
                  style = MaterialTheme.typography.bodySmall,
                  fontSize = 11.sp,
                  color = WearthTextSecondary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                  text = "ไม่มีค่าบริการ",
                  style = MaterialTheme.typography.labelSmall,
                  fontWeight = FontWeight.Bold,
                  color = WearthDarkGreen
                )
              }
            }
          }
        }
      }
    }

    // 3. Location Pinning Section (When Home Pickup is chosen)
    if (intakeType == DepositIntakeType.HOME_PICKUP) {
      item {
        Card(
          shape = RoundedCornerShape(22.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "📍", fontSize = 20.sp)
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                  Text(
                    text = "ปักหมุดตำแหน่งที่ให้ไรเดอร์เข้ารับ",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = WearthDarkGreen
                  )
                  Text(
                    text = "ตรวจสอบหมุดแผนที่และรายละเอียดที่อยู่",
                    style = MaterialTheme.typography.bodySmall,
                    color = WearthTextSecondary
                  )
                }
              }

              OutlinedButton(
                onClick = onOpenLocationPicker,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, WearthTealGreen),
                modifier = Modifier.testTag("open_location_picker_from_deposit")
              ) {
                Icon(Icons.Default.EditLocation, contentDescription = null, modifier = Modifier.size(16.dp), tint = WearthTealGreen)
                Spacer(modifier = Modifier.width(4.dp))
                Text("แก้ไขหมุด", fontSize = 12.sp, color = WearthTealGreen, fontWeight = FontWeight.Bold)
              }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Display current pinned address
            PinnedAddressSummaryCard(
              address = pinnedAddress,
              onEditClick = onOpenLocationPicker
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Quantity selector (Free if >= 3)
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(WearthBackground)
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Column {
                Text(
                  text = "จำนวนเสื้อผ้าที่ต้องการส่ง",
                  style = MaterialTheme.typography.bodyMedium,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = if (clothesCount >= 3) "✓ ฟรีค่าบริการเข้ารับถึงบ้าน!" else "ส่ง 3 ตัวขึ้นไปเพื่อรับสิทธิ์เข้ารับฟรี",
                  style = MaterialTheme.typography.bodySmall,
                  color = if (clothesCount >= 3) WearthTealGreen else Color(0xFFB45309),
                  fontWeight = FontWeight.SemiBold
                )
              }

              Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                  onClick = { if (clothesCount > 1) clothesCount-- },
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                ) {
                  Icon(Icons.Default.Remove, contentDescription = null, modifier = Modifier.size(16.dp))
                }
                Spacer(modifier = Modifier.width(12.dp))
                Text(
                  text = "$clothesCount ตัว",
                  fontWeight = FontWeight.ExtraBold,
                  fontSize = 16.sp,
                  color = WearthDarkGreen
                )
                Spacer(modifier = Modifier.width(12.dp))
                IconButton(
                  onClick = { clothesCount++ },
                  modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(WearthDarkGreen)
                ) {
                  Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                }
              }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Fee calculation summary
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("ค่าบริการเข้ารับถึงบ้าน:", style = MaterialTheme.typography.bodySmall, color = WearthTextSecondary)
              Text(
                text = if (pickupFee == 0) "ฟรี (0 บาท)" else "$pickupFee บาท (ใช้ 40 แต้มแทนได้)",
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Bold,
                color = if (pickupFee == 0) WearthTealGreen else WearthPointsGoldText
              )
            }
          }
        }
      }
    }

    // 4. Details Form (Category, Brand, Size, Condition, Appointment Time)
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(20.dp)) {
          Text(
            text = "ข้อมูลเสื้อผ้าและการนัดหมาย",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Category
          Text(text = "1. ประเภทเสื้อผ้า", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            categories.take(3).forEach { cat ->
              FilterChip(
                selected = category == cat,
                onClick = { category = cat },
                label = { Text(cat, fontSize = 12.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = WearthTealGreen,
                  selectedLabelColor = Color.White
                )
              )
            }
          }
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
          ) {
            categories.drop(3).forEach { cat ->
              FilterChip(
                selected = category == cat,
                onClick = { category = cat },
                label = { Text(cat, fontSize = 12.sp) },
                shape = RoundedCornerShape(10.dp),
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = WearthTealGreen,
                  selectedLabelColor = Color.White
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Brand Name
          Text(text = "2. แบรนด์ (ถ้ามี)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          OutlinedTextField(
            value = brandName,
            onValueChange = { brandName = it },
            placeholder = { Text("เช่น Uniqlo, Zara, หรือพิมพ์ 'ทั่วไป'") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
              focusedBorderColor = WearthTealGreen,
              unfocusedBorderColor = WearthBorder
            )
          )

          Spacer(modifier = Modifier.height(14.dp))

          // Condition
          Text(text = "3. สภาพเสื้อผ้า", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            FilterChip(
              selected = condition == ClothingCondition.VERY_GOOD,
              onClick = { condition = ClothingCondition.VERY_GOOD },
              label = { Text("ดีมาก (ไม่มีตำหนิ)") },
              shape = RoundedCornerShape(12.dp)
            )
            FilterChip(
              selected = condition == ClothingCondition.GOOD,
              onClick = { condition = ClothingCondition.GOOD },
              label = { Text("ดี (มีร่องรอยการใช้งาน)") },
              shape = RoundedCornerShape(12.dp)
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Attach Photo Simulation
          Text(text = "4. รูปถ่ายเสื้อผ้า (จำลอง)", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
          Spacer(modifier = Modifier.height(6.dp))
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(if (imageAttached) WearthLightTeal else WearthBackground)
              .clickable { imageAttached = !imageAttached }
              .padding(14.dp),
            contentAlignment = Alignment.Center
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (imageAttached) Icons.Default.CheckCircle else Icons.Default.AddPhotoAlternate,
                contentDescription = null,
                tint = if (imageAttached) WearthDarkGreen else WearthTextSecondary
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = if (imageAttached) "✓ แนบรูปถ่ายเสื้อแล้ว (คลิกเปลี่ยน)" else "+ แตะเพื่อจำลองการแนบรูปเสื้อ",
                color = if (imageAttached) WearthDarkGreen else WearthTextSecondary,
                fontWeight = if (imageAttached) FontWeight.Bold else FontWeight.Normal,
                style = MaterialTheme.typography.bodyMedium
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Appointment Time
          Text(
            text = if (intakeType == DepositIntakeType.HOME_PICKUP) "5. ช่วงเวลาที่ให้ไรเดอร์เข้ารับเสื้อ" else "5. เวลานำมาส่งที่สาขา",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          timeSlots.forEach { slot ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(if (appointmentDate == slot) WearthLightTeal else Color(0xFFF9FAFB))
                .clickable { appointmentDate = slot }
                .padding(horizontal = 12.dp, vertical = 8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(
                imageVector = Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = if (appointmentDate == slot) WearthDarkGreen else WearthTextSecondary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = slot,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = if (appointmentDate == slot) FontWeight.Bold else FontWeight.Normal,
                color = if (appointmentDate == slot) WearthDarkGreen else WearthTextPrimary
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Mandatory Notice
          Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = WearthPointsGoldBg),
            border = BorderStroke(1.dp, WearthPointsGold.copy(alpha = 0.5f)),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(12.dp),
              verticalAlignment = Alignment.Top
            ) {
              Icon(
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = WearthPointsGoldText,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "ร้านจะประเมินราคาที่ร้านอีกครั้ง เมื่อเสื้อถึงสาขาจะส่งผลการประเมินราคา P เข้าแอปของคุณทันที พร้อมตัวเลือกรับเงินสด P บาท หรือรับแต้มสุดคุ้ม P × 1.25 แต้ม",
                style = MaterialTheme.typography.bodySmall,
                color = WearthPointsGoldText,
                fontWeight = FontWeight.Medium
              )
            }
          }

          Spacer(modifier = Modifier.height(20.dp))

          // Submit Button
          Button(
            onClick = {
              val code = "#PK-${(1000..9999).random()}"
              createdTrackingCode = code
              val req = DepositRequest(
                id = "dep_${System.currentTimeMillis()}",
                trackingCode = code,
                customerId = customer.id,
                customerName = customer.name,
                phone = customer.phone,
                category = "$category ($clothesCount ตัว)",
                brandName = brandName.ifBlank { "ทั่วไป" },
                size = size,
                condition = condition,
                appointmentDate = appointmentDate,
                appointmentTime = appointmentDate,
                intakeType = intakeType,
                pickupAddress = if (intakeType == DepositIntakeType.HOME_PICKUP) pinnedAddress else null,
                pickupFee = pickupFee,
                numberOfClothes = clothesCount,
                status = if (intakeType == DepositIntakeType.HOME_PICKUP) "ไรเดอร์กำลังเดินทางเข้ารับ" else "นัดหมายแล้ว"
              )
              onSubmitDeposit(req)
              showSuccessDialog = true
            },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (intakeType == DepositIntakeType.HOME_PICKUP) WearthTealGreen else WearthDarkGreen
            ),
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("submit_deposit_request_button")
          ) {
            Icon(
              imageVector = if (intakeType == DepositIntakeType.HOME_PICKUP) Icons.Default.Moped else Icons.Default.Check,
              contentDescription = null,
              tint = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (intakeType == DepositIntakeType.HOME_PICKUP) "ยืนยันเรียกรถเข้ารับเสื้อถึงที่" else "ยืนยันนัดส่งเสื้อที่สาขา",
              color = Color.White,
              fontWeight = FontWeight.Bold,
              style = MaterialTheme.typography.titleSmall
            )
          }
        }
      }
    }
  }

  // Success Dialog with Tracking Code
  if (showSuccessDialog) {
    Dialog(onDismissRequest = { showSuccessDialog = false }) {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.padding(16.dp)
      ) {
        Column(
          modifier = Modifier.padding(22.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Box(
            modifier = Modifier
              .size(56.dp)
              .clip(CircleShape)
              .background(WearthLightTeal),
            contentAlignment = Alignment.Center
          ) {
            Icon(Icons.Default.Check, contentDescription = null, tint = WearthDarkGreen, modifier = Modifier.size(32.dp))
          }
          Spacer(modifier = Modifier.height(14.dp))
          Text(
            text = if (intakeType == DepositIntakeType.HOME_PICKUP) "เรียกรถเข้ารับสำเร็จ!" else "บันทึกการนัดหมายแล้ว!",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = WearthDarkGreen
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "รหัสติดตามงาน: $createdTrackingCode",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.ExtraBold,
            color = WearthTealGreen
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (intakeType == DepositIntakeType.HOME_PICKUP)
              "ไรเดอร์จะไปรับเสื้อตามตำแหน่งที่ปักหมุดไว้ (${pinnedAddress.label}: ${pinnedAddress.addressText}) ในช่วงเวลา $appointmentDate"
            else
              "นำเสื้อมาส่งได้ที่ร้านตามเวลานัดหมาย เมื่อเสื้อถึงสาขาจะประเมินราคาให้ทันที",
            style = MaterialTheme.typography.bodyMedium,
            color = WearthTextSecondary,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center
          )
          Spacer(modifier = Modifier.height(18.dp))
          Button(
            onClick = { showSuccessDialog = false },
            colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("ตกลง", fontWeight = FontWeight.Bold)
          }
        }
      }
    }
  }
}
