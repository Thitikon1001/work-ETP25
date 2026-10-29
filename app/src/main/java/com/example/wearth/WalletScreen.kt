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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthCardGreen
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGold
import com.example.ui.theme.WearthPointsGoldBg
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

@Composable
fun WalletScreen(
  customer: Customer,
  transactions: List<HistoryTransaction>,
  onOpenTierModal: () -> Unit,
  onOpenQrModal: () -> Unit
) {
  val expiringBatch = customer.batches.find { it.isExpiringSoon }

  // Next tier progress calculation (General: 0-99, Brand: 100-199, Premium: 200+)
  val (nextTierName, pointsNeeded, progress) = when {
    customer.points < 100 -> Triple("แบรนด์", 100 - customer.points, customer.points / 100f)
    customer.points < 200 -> Triple("พรีเมียม", 200 - customer.points, (customer.points - 100) / 100f)
    else -> Triple("สูงสุดแล้ว", 0, 1f)
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("wallet_screen")
  ) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 90.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Header
      item {
        Text(
          text = "คะแนนของฉัน",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthTextPrimary
        )
      }

      // 1. Big Card: แต้มสะสมปัจจุบัน
      item {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = WearthCardGreen),
          modifier = Modifier
            .fillMaxWidth()
            .testTag("wallet_big_points_card")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .padding(22.dp)
          ) {
            Column {
              Text(
                text = "แต้มสะสมปัจจุบัน",
                color = Color(0xFFA7F3D0),
                style = MaterialTheme.typography.bodyMedium
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.Bottom) {
                Text(
                  text = "${customer.points}",
                  color = Color.White,
                  fontSize = 44.sp,
                  fontWeight = FontWeight.ExtraBold,
                  lineHeight = 46.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                  text = "แต้ม",
                  color = Color(0xFFD1FAE5),
                  fontSize = 20.sp,
                  fontWeight = FontWeight.Bold,
                  modifier = Modifier.padding(bottom = 6.dp)
                )
              }
              Text(
                text = "(= ${customer.points} บาท สำหรับใช้แลกเสื้อในร้าน)",
                color = Color(0xFFFDE68A),
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold
              )
            }

            // Leaf graphic
            Icon(
              imageVector = Icons.Default.Spa,
              contentDescription = null,
              tint = Color.White.copy(alpha = 0.25f),
              modifier = Modifier
                .size(64.dp)
                .align(Alignment.CenterEnd)
            )
          }
        }
      }

      // 2. Member Tier & Progress
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text(
                text = "ระดับปัจจุบัน : ${customer.tierName}",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WearthTextPrimary
              )
              Text(
                text = if (pointsNeeded > 0) "อีก $pointsNeeded แต้ม เพื่อเลื่อนระดับถัดไป" else "ระดับสูงสุดแล้ว",
                style = MaterialTheme.typography.bodySmall,
                color = WearthTealGreen,
                fontWeight = FontWeight.SemiBold
              )
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
              progress = progress.coerceIn(0f, 1f),
              modifier = Modifier
                .fillMaxWidth()
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)),
              color = WearthDarkGreen,
              trackColor = Color(0xFFE2E8F0)
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
              onClick = onOpenTierModal,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = WearthLightTeal),
              modifier = Modifier
                .fillMaxWidth()
                .testTag("view_tier_benefits_button")
            ) {
              Text(
                text = "ดูสิทธิประโยชน์แต่ละระดับ",
                color = WearthDarkGreen,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
              )
            }
          }
        }
      }

      // 3. Batches & Expiry (แต้มมีอายุ 6 เดือน, FIFO)
      item {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(text = "📅", fontSize = 18.sp)
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "วันหมดอายุของแต้ม (อายุแต้ม 6 เดือน)",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WearthTextPrimary
              )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "ระบบใช้แต้มก้อนที่เก่าที่สุดก่อนอัตโนมัติ (FIFO)",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (customer.batches.isEmpty()) {
              Text(
                text = "ไม่มียอดแต้มค้าง",
                style = MaterialTheme.typography.bodyMedium,
                color = WearthTextSecondary
              )
            } else {
              customer.batches.forEach { batch ->
                Row(
                  modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(if (batch.isExpiringSoon) Color(0xFFFEF2F2) else WearthBackground)
                    .padding(12.dp),
                  horizontalArrangement = Arrangement.SpaceBetween,
                  verticalAlignment = Alignment.CenterVertically
                ) {
                  Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Text(
                        text = "${batch.amount} แต้ม (= ${batch.amount} บาท)",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (batch.isExpiringSoon) Color(0xFFDC2626) else WearthTextPrimary
                      )
                      if (batch.isExpiringSoon) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                          text = "⚠️ ใกล้หมดอายุ!",
                          color = Color(0xFFDC2626),
                          fontSize = 11.sp,
                          fontWeight = FontWeight.Bold
                        )
                      }
                    }
                    Text(
                      text = "ได้รับ: ${batch.dateEarned} • หมดอายุ: ${batch.expiryDate}",
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
      }

      // 4. History List (ประวัติการได้รับ - ใช้แต้ม)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ประวัติการได้รับ - ใช้แต้ม",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
        }
      }

      items(transactions) { tx ->
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Box(
                modifier = Modifier
                  .size(40.dp)
                  .clip(CircleShape)
                  .background(
                    if (tx.pointsDelta > 0) WearthLightTeal else Color(0xFFF3E8FF)
                  ),
                contentAlignment = Alignment.Center
              ) {
                Icon(
                  imageVector = if (tx.pointsDelta > 0) Icons.Default.Add else Icons.Default.Remove,
                  contentDescription = null,
                  tint = if (tx.pointsDelta > 0) WearthDarkGreen else Color(0xFF7C3AED),
                  modifier = Modifier.size(20.dp)
                )
              }

              Spacer(modifier = Modifier.width(12.dp))

              Column {
                Text(
                  text = tx.title,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold,
                  color = WearthTextPrimary
                )
                Text(
                  text = "${tx.date}${if (tx.cashPaid > 0) " • จ่ายเงินสดส่วนต่าง ${tx.cashPaid} บาท" else ""}",
                  style = MaterialTheme.typography.bodySmall,
                  color = WearthTextSecondary,
                  fontSize = 11.sp
                )
                if (tx.note.isNotBlank()) {
                  Text(
                    text = tx.note,
                    style = MaterialTheme.typography.bodySmall,
                    color = WearthTealGreen,
                    fontSize = 11.sp
                  )
                }
              }
            }

            // Points Delta Number (+120 or -140)
            Text(
              text = if (tx.pointsDelta > 0) "+ ${tx.pointsDelta}" else "${tx.pointsDelta}",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.ExtraBold,
              color = if (tx.pointsDelta > 0) WearthDarkGreen else Color(0xFFDC2626)
            )
          }
        }
      }
    }

    // Sticky Bottom Button: "สแกน QR เพื่อใช้แต้ม"
    Box(
      modifier = Modifier
        .align(Alignment.BottomCenter)
        .fillMaxWidth()
        .background(Color.White.copy(alpha = 0.95f))
        .padding(horizontal = 20.dp, vertical = 14.dp)
    ) {
      Button(
        onClick = onOpenQrModal,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
        modifier = Modifier
          .fillMaxWidth()
          .height(50.dp)
          .testTag("scan_qr_to_use_points_button")
      ) {
        Icon(
          imageVector = Icons.Default.QrCodeScanner,
          contentDescription = null,
          tint = Color.White
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "สแกน QR เพื่อใช้แต้ม",
          color = Color.White,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}
