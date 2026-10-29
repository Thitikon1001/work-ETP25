package com.example.wearth

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EditLocation
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Navigation
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthBorder
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthDepositBg
import com.example.ui.theme.WearthDepositColor
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthShopPurple
import com.example.ui.theme.WearthShopPurpleBg
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary
import kotlin.math.roundToInt

/**
 * แผนที่ปักหมุดจำลองแบบโต้ตอบได้ (Interactive Simulated Map Canvas)
 */
@Composable
fun InteractiveMapPinView(
  modifier: Modifier = Modifier,
  initialAddressTitle: String = "จุดปักหมุดปัจจุบัน",
  onLocationChanged: (Double, Double, String) -> Unit = { _, _, _ -> }
) {
  var pinOffsetX by remember { mutableFloatStateOf(0f) }
  var pinOffsetY by remember { mutableFloatStateOf(0f) }
  var isDragging by remember { mutableStateOf(false) }
  var currentAddressDesc by remember { mutableStateOf(initialAddressTitle) }

  // Sample locations around Bangkok
  val presetLocations = listOf(
    Triple("สุขุมวิท 39", 13.7367, 100.5612),
    Triple("อโศกมนตรี (Asok)", 13.7380, 100.5600),
    Triple("ทองหล่อ (Thong Lo)", 13.7315, 100.5815),
    Triple("สยาม (Siam)", 13.7450, 100.5340)
  )

  Column(modifier = modifier) {
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(200.dp)
        .clip(RoundedCornerShape(20.dp))
        .background(Color(0xFFE5EDE6))
        .pointerInput(Unit) {
          detectTapGestures { tapOffset ->
            pinOffsetX = tapOffset.x - size.width / 2f
            pinOffsetY = tapOffset.y - size.height / 2f
            currentAddressDesc = "ตำแหน่งที่แตะเลือกใหม่"
            onLocationChanged(13.7367 + (pinOffsetY * 0.0001), 100.5612 + (pinOffsetX * 0.0001), currentAddressDesc)
          }
        }
        .pointerInput(Unit) {
          detectDragGestures(
            onDragStart = { isDragging = true },
            onDragEnd = {
              isDragging = false
              currentAddressDesc = "ตำแหน่งที่ลากปักหมุดใหม่"
              onLocationChanged(13.7367 + (pinOffsetY * 0.0001), 100.5612 + (pinOffsetX * 0.0001), currentAddressDesc)
            },
            onDragCancel = { isDragging = false }
          ) { change, dragAmount ->
            change.consume()
            pinOffsetX += dragAmount.x
            pinOffsetY += dragAmount.y
          }
        }
    ) {
      // Map Canvas Drawing (Stylized Roads, River, Green Parks, Buildings)
      Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Parks (Green Areas)
        drawRoundRect(
          color = Color(0xFFD4E7D5),
          topLeft = Offset(w * 0.05f, h * 0.1f),
          size = Size(w * 0.35f, h * 0.4f),
          cornerRadius = CornerRadius(16f, 16f)
        )
        drawRoundRect(
          color = Color(0xFFD4E7D5),
          topLeft = Offset(w * 0.65f, h * 0.45f),
          size = Size(w * 0.3f, h * 0.45f),
          cornerRadius = CornerRadius(16f, 16f)
        )

        // River / Canal (Blue curvy band)
        val riverPath = Path().apply {
          moveTo(0f, h * 0.75f)
          quadraticBezierTo(w * 0.35f, h * 0.85f, w * 0.55f, h * 0.65f)
          quadraticBezierTo(w * 0.8f, h * 0.45f, w, h * 0.5f)
        }
        drawPath(
          path = riverPath,
          color = Color(0xFFBADCF5),
          style = Stroke(width = 24.dp.toPx(), cap = StrokeCap.Round)
        )

        // Secondary Streets (White)
        val streetColor = Color.White
        drawLine(
          color = streetColor,
          start = Offset(0f, h * 0.3f),
          end = Offset(w, h * 0.3f),
          strokeWidth = 14.dp.toPx()
        )
        drawLine(
          color = streetColor,
          start = Offset(w * 0.3f, 0f),
          end = Offset(w * 0.3f, h),
          strokeWidth = 14.dp.toPx()
        )
        drawLine(
          color = streetColor,
          start = Offset(w * 0.72f, 0f),
          end = Offset(w * 0.72f, h),
          strokeWidth = 14.dp.toPx()
        )

        // Main Express Road (Orange/Yellow Highway)
        drawLine(
          color = Color(0xFFFED7AA),
          start = Offset(0f, h * 0.55f),
          end = Offset(w, h * 0.25f),
          strokeWidth = 18.dp.toPx()
        )

        // Buildings (Small Rectangles)
        val bldgColor = Color(0xFFCBD5E1)
        drawRect(color = bldgColor, topLeft = Offset(w * 0.15f, h * 0.6f), size = Size(28.dp.toPx(), 22.dp.toPx()))
        drawRect(color = bldgColor, topLeft = Offset(w * 0.45f, h * 0.15f), size = Size(36.dp.toPx(), 26.dp.toPx()))
        drawRect(color = bldgColor, topLeft = Offset(w * 0.55f, h * 0.75f), size = Size(32.dp.toPx(), 24.dp.toPx()))
      }

      // Compass / Current GPS Button on Map
      Box(
        modifier = Modifier
          .align(Alignment.TopEnd)
          .padding(10.dp)
          .size(36.dp)
          .clip(CircleShape)
          .background(Color.White)
          .clickable {
            pinOffsetX = 0f
            pinOffsetY = 0f
            currentAddressDesc = "ตำแหน่ง GPS ปัจจุบัน (สุขุมวิท 39)"
            onLocationChanged(13.736717, 100.561234, currentAddressDesc)
          },
        contentAlignment = Alignment.Center
      ) {
        Icon(
          imageVector = Icons.Default.MyLocation,
          contentDescription = "GPS",
          tint = WearthDarkGreen,
          modifier = Modifier.size(20.dp)
        )
      }

      // Instruction Pill on top of map
      Box(
        modifier = Modifier
          .align(Alignment.TopStart)
          .padding(10.dp)
          .clip(RoundedCornerShape(8.dp))
          .background(Color.Black.copy(alpha = 0.65f))
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Text(
          text = if (isDragging) "กำลังลากหมุด..." else "👆 แตะหรือลากเพื่อย้ายหมุด",
          color = Color.White,
          fontSize = 10.sp,
          fontWeight = FontWeight.Medium
        )
      }

      // Center Pin Marker
      Box(
        modifier = Modifier
          .align(Alignment.Center)
          .offset { IntOffset(pinOffsetX.roundToInt(), (pinOffsetY - 18.dp.toPx()).roundToInt()) }
      ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
          // Info bubble above pin
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(8.dp))
              .background(WearthDarkGreen)
              .padding(horizontal = 8.dp, vertical = 4.dp)
          ) {
            Text(
              text = currentAddressDesc,
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }

          // Pin Icon with shadow
          Icon(
            imageVector = Icons.Default.LocationOn,
            contentDescription = "หมุดตำแหน่ง",
            tint = Color(0xFFDC2626),
            modifier = Modifier.size(38.dp)
          )

          // Pin ground shadow
          Box(
            modifier = Modifier
              .offset(y = (-6).dp)
              .size(14.dp, 6.dp)
              .clip(CircleShape)
              .background(Color.Black.copy(alpha = 0.35f))
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Quick Location Chips
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
      presetLocations.forEach { (name, lat, lng) ->
        FilterChip(
          selected = currentAddressDesc.contains(name),
          onClick = {
            currentAddressDesc = name
            pinOffsetX = (lng - 100.5612).toFloat() * 1000f
            pinOffsetY = (lat - 13.7367).toFloat() * 1000f
            onLocationChanged(lat, lng, name)
          },
          label = { Text(name, fontSize = 11.sp) },
          shape = RoundedCornerShape(10.dp),
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = WearthLightTeal,
            selectedLabelColor = WearthDarkGreen
          )
        )
      }
    }
  }
}

/**
 * ป๊อปอัปเลือกและแก้ไขที่อยู่ปักหมุดแบบเต็มรูปแบบ
 */
@Composable
fun LocationPickerDialog(
  initialAddress: DeliveryAddress,
  onAddressConfirmed: (DeliveryAddress) -> Unit,
  onDismiss: () -> Unit
) {
  var selectedLabel by remember { mutableStateOf(initialAddress.label) }
  var recipientName by remember { mutableStateOf(initialAddress.recipientName) }
  var phone by remember { mutableStateOf(initialAddress.phone) }
  var addressText by remember { mutableStateOf(initialAddress.addressText) }
  var district by remember { mutableStateOf(initialAddress.district) }
  var province by remember { mutableStateOf(initialAddress.province) }
  var postalCode by remember { mutableStateOf(initialAddress.postalCode) }
  var noteToRider by remember { mutableStateOf(initialAddress.noteToRider) }

  var currentLat by remember { mutableStateOf(initialAddress.latitude) }
  var currentLng by remember { mutableStateOf(initialAddress.longitude) }

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .padding(vertical = 20.dp)
        .testTag("location_picker_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(WearthLightTeal),
              contentAlignment = Alignment.Center
            ) {
              Icon(Icons.Default.PinDrop, contentDescription = null, tint = WearthDarkGreen)
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "ปักหมุดที่อยู่รับ-ส่งสินค้า",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = WearthDarkGreen
              )
              Text(
                text = "ระบุตำแหน่งที่ชัดเจนเพื่อให้ไรเดอร์เดินทางถูก",
                style = MaterialTheme.typography.bodySmall,
                color = WearthTextSecondary
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "ปิด")
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // 1. Interactive Map
        InteractiveMapPinView(
          initialAddressTitle = addressText.ifBlank { "สุขุมวิท 39" },
          onLocationChanged = { lat, lng, desc ->
            currentLat = lat
            currentLng = lng
            if (addressText.isBlank()) {
              addressText = desc
            }
          }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 2. Preset Category Chips (บ้าน / คอนโด / ที่ทำงาน)
        Text(text = "ประเภทสถานที่:", style = MaterialTheme.typography.bodySmall, fontWeight = FontWeight.Bold)
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
          listOf("บ้าน", "คอนโด", "ที่ทำงาน", "อื่นๆ").forEach { label ->
            FilterChip(
              selected = selectedLabel == label,
              onClick = { selectedLabel = label },
              label = { Text(label, fontSize = 12.sp) },
              shape = RoundedCornerShape(10.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = WearthTealGreen,
                selectedLabelColor = Color.White
              )
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // 3. Name & Phone
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = recipientName,
            onValueChange = { recipientName = it },
            label = { Text("ชื่อผู้ติดต่อ") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("location_picker_name_input")
          )
          OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text("เบอร์โทรศัพท์") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier
              .weight(1f)
              .testTag("location_picker_phone_input")
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // 4. Detailed Address
        OutlinedTextField(
          value = addressText,
          onValueChange = { addressText = it },
          label = { Text("บ้านเลขที่, ชื่ออาคาร/หมู่บ้าน, ซอย, ถนน") },
          placeholder = { Text("เช่น คอนโด แอชตัน อโศก ชั้น 18 ห้อง 1804 ถนนสุขุมวิท 21") },
          shape = RoundedCornerShape(12.dp),
          maxLines = 2,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("location_picker_address_input")
        )

        Spacer(modifier = Modifier.height(10.dp))

        // District & Postal Code
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
          OutlinedTextField(
            value = district,
            onValueChange = { district = it },
            label = { Text("เขต/อำเภอ") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
          OutlinedTextField(
            value = postalCode,
            onValueChange = { postalCode = it },
            label = { Text("รหัสไปรษณีย์") },
            shape = RoundedCornerShape(12.dp),
            singleLine = true,
            modifier = Modifier.weight(1f)
          )
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Note to Rider
        OutlinedTextField(
          value = noteToRider,
          onValueChange = { noteToRider = it },
          label = { Text("ข้อความถึงไรเดอร์ (ถ้ามี)") },
          placeholder = { Text("เช่น ฝากไว้ที่ป้อม รปภ. / โทรหาก่อนถึง 10 นาที") },
          shape = RoundedCornerShape(12.dp),
          singleLine = true,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Save & Confirm Button
        Button(
          onClick = {
            val confirmed = DeliveryAddress(
              id = "addr_${System.currentTimeMillis()}",
              label = selectedLabel,
              recipientName = recipientName.ifBlank { "คุณลูกค้า" },
              phone = phone.ifBlank { "081-234-5678" },
              addressText = addressText.ifBlank { "สุขุมวิท 39" },
              district = district.ifBlank { "เขตวัฒนา" },
              province = province.ifBlank { "กรุงเทพฯ" },
              postalCode = postalCode.ifBlank { "10110" },
              noteToRider = noteToRider,
              latitude = currentLat,
              longitude = currentLng
            )
            onAddressConfirmed(confirmed)
          },
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
          modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .testTag("confirm_location_pin_button")
        ) {
          Icon(Icons.Default.Check, contentDescription = null, tint = Color.White)
          Spacer(modifier = Modifier.width(8.dp))
          Text("ยืนยันตำแหน่งปักหมุด", fontWeight = FontWeight.Bold, color = Color.White)
        }
      }
    }
  }
}

/**
 * การ์ดแสดงตำแหน่งปักหมุดขนาดกะทัดรัด พร้อมปุ่มกดแก้ไข/เปลี่ยนหมุด
 */
@Composable
fun PinnedAddressSummaryCard(
  address: DeliveryAddress,
  onEditClick: () -> Unit,
  modifier: Modifier = Modifier,
  badgeText: String = "ตำแหน่งปักหมุด"
) {
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    border = BorderStroke(1.dp, WearthLightTeal),
    modifier = modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.Top
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(WearthLightTeal),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = when (address.label) {
              "บ้าน" -> Icons.Default.Home
              "คอนโด" -> Icons.Default.Apartment
              "ที่ทำงาน" -> Icons.Default.Work
              else -> Icons.Default.LocationOn
            },
            contentDescription = null,
            tint = WearthDarkGreen,
            modifier = Modifier.size(20.dp)
          )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = "${address.label}: ${address.recipientName}",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "(${address.phone})",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary
            )
          }

          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = address.fullDisplayAddress,
            style = MaterialTheme.typography.bodySmall,
            color = WearthTextSecondary,
            lineHeight = 16.sp
          )

          if (address.noteToRider.isNotBlank()) {
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "📝 โน้ตถึงไรเดอร์: ${address.noteToRider}",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTealGreen,
              fontSize = 11.sp
            )
          }
        }
      }

      // Edit Button
      IconButton(
        onClick = onEditClick,
        modifier = Modifier.testTag("edit_pinned_address_button")
      ) {
        Icon(
          imageVector = Icons.Default.EditLocation,
          contentDescription = "แก้ไขหมุด",
          tint = WearthTealGreen
        )
      }
    }
  }
}
