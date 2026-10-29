package com.example.wearth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

@Composable
fun RedeemSuccessDialog(
  order: RedeemOrder,
  onDismiss: () -> Unit,
  onSwitchToStaffToConfirm: () -> Unit
) {
  val isHomeDelivery = order.deliveryType == DeliveryType.HOME_DELIVERY

  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(24.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
      modifier = Modifier
        .fillMaxWidth()
        .testTag("redeem_success_dialog")
    ) {
      Column(
        modifier = Modifier.padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Success Icon Circle
        Box(
          modifier = Modifier
            .size(60.dp)
            .clip(CircleShape)
            .background(WearthLightTeal),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = if (isHomeDelivery) Icons.Default.LocalShipping else Icons.Default.Check,
            contentDescription = null,
            tint = WearthDarkGreen,
            modifier = Modifier.size(36.dp)
          )
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (isHomeDelivery) "แลกและจัดส่งถึงบ้านสำเร็จ!" else "แลกเสื้อสำเร็จ!",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthDarkGreen
        )

        Text(
          text = if (isHomeDelivery)
            "ระบบตัดแต้มและบันทึกคิวจัดส่งพัสดุเรียบร้อยแล้ว"
          else
            "ระบบตัดแต้มและสร้างรหัสรับของเรียบร้อยแล้ว",
          style = MaterialTheme.typography.bodySmall,
          color = WearthTextSecondary,
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Code Box (Tracking Code or Pickup Code)
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = WearthBackground),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = if (isHomeDelivery) "เลขติดตามพัสดุ (Tracking Code)" else "รหัสรับของหน้าร้าน",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = if (isHomeDelivery) (order.trackingCode ?: order.orderCode) else order.orderCode,
              style = MaterialTheme.typography.headlineLarge,
              fontWeight = FontWeight.ExtraBold,
              color = WearthDarkGreen,
              letterSpacing = 2.sp
            )

            if (!isHomeDelivery) {
              Spacer(modifier = Modifier.height(10.dp))
              Box(
                modifier = Modifier
                  .size(100.dp)
                  .clip(RoundedCornerShape(12.dp))
                  .background(Color.White)
                  .padding(8.dp),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = Icons.Default.QrCode,
                  contentDescription = "QR Code รับของ",
                  modifier = Modifier.size(80.dp),
                  tint = WearthDarkGreen
                )
              }
            } else {
              Spacer(modifier = Modifier.height(8.dp))
              order.shippingAddress?.let { addr ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color.White)
                    .padding(10.dp),
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Icon(Icons.Default.PinDrop, contentDescription = null, tint = WearthTealGreen, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Column {
                    Text(
                      text = "จัดส่งถึง: ${addr.label} (${addr.recipientName})",
                      fontWeight = FontWeight.Bold,
                      style = MaterialTheme.typography.bodySmall,
                      color = WearthDarkGreen
                    )
                    Text(
                      text = addr.fullDisplayAddress,
                      style = MaterialTheme.typography.bodySmall,
                      color = WearthTextSecondary,
                      fontSize = 11.sp
                    )
                  }
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Item & Payment Summary
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF9FAFB))
            .padding(12.dp)
        ) {
          Text(
            text = "สินค้า: ${order.itemName}",
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "• ใช้แต้มไป: ${order.pointsUsed} แต้ม (= ${order.pointsUsed} บาท)",
            style = MaterialTheme.typography.bodySmall,
            color = WearthPointsGoldText,
            fontWeight = FontWeight.SemiBold
          )
          if (order.cashPaid > 0) {
            Text(
              text = if (isHomeDelivery)
                "• ยอดชำระรวม: ${order.cashPaid} บาท (รวมค่าส่ง ฿${order.shippingFee})"
              else
                "• ยอดเงินสดที่ต้องชำระที่หน้าร้าน: ${order.cashPaid} บาท",
              style = MaterialTheme.typography.bodySmall,
              color = Color(0xFFDC2626),
              fontWeight = FontWeight.Bold
            )
          } else {
            Text(
              text = "• ชำระครบด้วยแต้ม (ไม่ต้องจ่ายเงินเพิ่ม)",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTealGreen,
              fontWeight = FontWeight.SemiBold
            )
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Primary dismiss button
        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
          modifier = Modifier
            .fillMaxWidth()
            .height(48.dp)
            .testTag("dismiss_redeem_success_button")
        ) {
          Text(
            text = "เข้าใจแล้ว / ปิดหน้าต่าง",
            fontWeight = FontWeight.Bold,
            color = Color.White
          )
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Quick shortcut to Staff mode
        OutlinedButton(
          onClick = onSwitchToStaffToConfirm,
          shape = RoundedCornerShape(14.dp),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("shortcut_to_staff_confirm")
        ) {
          Text(
            text = if (isHomeDelivery) "🛠️ สลับไปโหมดพนักงานเพื่อดูคิวจัดส่งพัสดุ" else "🛠️ สลับไปโหมดพนักงานเพื่อยืนยันรับของ",
            fontSize = 12.sp,
            color = WearthDarkGreen,
            fontWeight = FontWeight.SemiBold
          )
        }
      }
    }
  }
}
