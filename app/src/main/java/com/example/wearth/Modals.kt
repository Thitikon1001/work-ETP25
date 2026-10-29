package com.example.wearth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthBorder
import com.example.ui.theme.WearthDarkGreen
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

/**
 * หน้าต่างอัตราการรับซื้อและแต้ม (ตรงตามภาพอ้างอิงและตัวอย่างคำนวณ)
 */
@Composable
fun RatesDialog(onDismiss: () -> Unit) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(WearthBackground)
        .testTag("rates_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        // Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.ArrowBack, contentDescription = "ย้อนกลับ")
            }
            Text(
              text = "อัตราการรับซื้อและแต้ม",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "ปิด")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Subtitle banner: 1 แต้ม = 1 บาท
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(WearthLightTeal)
            .padding(14.dp)
        ) {
          Text(
            text = "1 แต้ม = 1 บาท ของราคาป้าย",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WearthDarkGreen
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Table Card (Matching Screenshot table)
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            // Table Header
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Text("ระดับ", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 13.sp)
              Text("เงินสด", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f), fontSize = 13.sp, textAlign = TextAlign.Center)
              Text("รับแต้ม", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.2f), fontSize = 13.sp, textAlign = TextAlign.Center, color = WearthTealGreen)
              Text("ใช้แต้มสูงสุด", fontWeight = FontWeight.Bold, modifier = Modifier.weight(1.3f), fontSize = 13.sp, textAlign = TextAlign.End)
            }

            Divider(color = WearthBorder)

            // Row 1: ทั่วไป
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("ทั่วไป", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), color = WearthDarkGreen)
              Text("P บาท", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)
              Text("P × 1.25", modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center, fontSize = 12.sp, color = WearthTealGreen, fontWeight = FontWeight.Bold)
              Text("100% เต็ม", modifier = Modifier.weight(1.3f), textAlign = TextAlign.End, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }

            Divider(color = WearthBorder)

            // Row 2: แบรนด์
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Text("แบรนด์", fontWeight = FontWeight.SemiBold, modifier = Modifier.weight(1f), color = WearthShopPurple)
              Text("P บาท", modifier = Modifier.weight(1f), textAlign = TextAlign.Center, fontSize = 12.sp)
              Text("P × 1.25", modifier = Modifier.weight(1.2f), textAlign = TextAlign.Center, fontSize = 12.sp, color = WearthShopPurple, fontWeight = FontWeight.Bold)
              Text("สูงสุด 70%", modifier = Modifier.weight(1.3f), textAlign = TextAlign.End, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
          }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Example Calculation Card (ตัวอย่างการคำนวณ)
        Text(
          text = "ตัวอย่างการคำนวณจากระบบจริง",
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold,
          color = WearthTextPrimary
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Example 1 Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "(1) เสื้อระดับทั่วไป",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = WearthDarkGreen
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "• ประเมินราคารับซื้อ P = 60 บาท\n• เลือกรับแต้ม = 60 × 1.25 = 75 แต้ม (= 75 บาท)\n• ราคาขายหน้าร้านแนะนำ = 60 × 1.6 = 96 บาท\n• ตอนนำแต้มมาแลกเสื้อราคา 96 บาท: ใช้แต้มได้เต็ม 100% -> ใช้ 75 แต้ม + จ่ายเงินสด 21 บาท",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary,
              lineHeight = 20.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Example 2 Card
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(16.dp)) {
            Text(
              text = "(2) เสื้อระดับแบรนด์",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = WearthShopPurple
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "• ประเมินราคารับซื้อ P = 200 บาท\n• เลือกรับแต้ม = 200 × 1.25 = 250 แต้ม (= 250 บาท)\n• ราคาขายหน้าร้านแนะนำ = 200 × 1.6 = 320 บาท\n• ตอนนำแต้มมาแลกเสื้อแบรนด์ 320 บาท: ใช้แต้มได้ไม่เกิน 70% ของราคา (= 224 แต้ม) -> ใช้ 224 แต้ม + จ่ายเงินสด 96 บาท คงเหลือแต้มในกระเป๋า 26 แต้ม",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary,
              lineHeight = 20.sp
            )
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("ปิดหน้าต่าง")
        }
      }
    }
  }
}

/**
 * หน้าระดับสมาชิก (Member Tiers)
 */
@Composable
fun TierDialog(onDismiss: () -> Unit) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Surface(
      modifier = Modifier
        .fillMaxSize()
        .background(WearthBackground)
        .testTag("tier_dialog")
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .verticalScroll(rememberScrollState())
          .padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onDismiss) {
              Icon(Icons.Default.ArrowBack, contentDescription = "ย้อนกลับ")
            }
            Text(
              text = "ระดับสมาชิก",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
          }
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "ปิด")
          }
        }

        Spacer(modifier = Modifier.height(16.dp))

        TierCard(
          name = "ทั่วไป",
          pointsRange = "0 - 99 แต้ม",
          desc = "เริ่มต้นสะสมแต้ม",
          bgColor = WearthLightTeal,
          accentColor = WearthDarkGreen
        )

        Spacer(modifier = Modifier.height(12.dp))

        TierCard(
          name = "แบรนด์",
          pointsRange = "100 - 199 แต้ม",
          desc = "สิทธิพิเศษมากขึ้น",
          bgColor = WearthShopPurpleBg,
          accentColor = WearthShopPurple
        )

        Spacer(modifier = Modifier.height(12.dp))

        TierCard(
          name = "พรีเมียม",
          pointsRange = "200 แต้มขึ้นไป",
          desc = "รับสิทธิพิเศษสูงสุด",
          bgColor = WearthPointsGoldBg,
          accentColor = WearthPointsGoldText
        )

        Spacer(modifier = Modifier.height(20.dp))

        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "สิทธิพิเศษเพิ่มเติม",
              style = MaterialTheme.typography.titleMedium,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
            Spacer(modifier = Modifier.height(10.dp))
            BenefitItem("ส่วนลดพิเศษในวันเกิด 10%")
            BenefitItem("กิจกรรมพิเศษและสิทธิ์เลือกเสื้อคอลเลกชันใหม่ก่อนใคร")
            BenefitItem("รับแต้มเพิ่มจากการรีวิวเสื้อผ้าที่นำมาแลก")
          }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(14.dp),
          colors = ButtonDefaults.buttonColors(containerColor = WearthDarkGreen),
          modifier = Modifier.fillMaxWidth()
        ) {
          Text("ปิดหน้าต่าง")
        }
      }
    }
  }
}

@Composable
fun TierCard(
  name: String,
  pointsRange: String,
  desc: String,
  bgColor: Color,
  accentColor: Color
) {
  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = bgColor),
    modifier = Modifier.fillMaxWidth()
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(46.dp)
            .clip(CircleShape)
            .background(accentColor.copy(alpha = 0.2f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(Icons.Default.Star, contentDescription = null, tint = accentColor)
        }
        Spacer(modifier = Modifier.width(14.dp))
        Column {
          Text(text = name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = accentColor)
          Text(text = desc, style = MaterialTheme.typography.bodySmall, color = WearthTextSecondary)
        }
      }

      Text(
        text = pointsRange,
        style = MaterialTheme.typography.labelSmall,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
          .clip(RoundedCornerShape(8.dp))
          .background(Color.White.copy(alpha = 0.8f))
          .padding(horizontal = 8.dp, vertical = 4.dp),
        color = accentColor
      )
    }
  }
}

@Composable
fun BenefitItem(text: String) {
  Row(
    modifier = Modifier.padding(vertical = 4.dp),
    verticalAlignment = Alignment.CenterVertically
  ) {
    Icon(
      imageVector = Icons.Default.Check,
      contentDescription = null,
      tint = WearthDarkGreen,
      modifier = Modifier.size(18.dp)
    )
    Spacer(modifier = Modifier.width(8.dp))
    Text(
      text = text,
      style = MaterialTheme.typography.bodyMedium,
      color = WearthTextPrimary
    )
  }
}

/**
 * หน้าจอ QR Code สำหรับให้พนักงานสแกนตัดแต้ม
 */
@Composable
fun QrDialog(customer: Customer, onDismiss: () -> Unit) {
  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(Color(0xFF10281E))
        .padding(24.dp)
        .testTag("qr_dialog"),
      contentAlignment = Alignment.Center
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Text(
          text = "สแกน QR เพื่อใช้แต้ม",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = Color.White
        )
        Text(
          text = "ยื่นให้พนักงานที่เคาน์เตอร์ WEARTH เพื่อสแกนแต้ม",
          style = MaterialTheme.typography.bodySmall,
          color = Color(0xFFA7F3D0)
        )

        Spacer(modifier = Modifier.height(28.dp))

        // Center Viewfinder with QR Code
        Box(
          modifier = Modifier
            .size(240.dp)
            .clip(RoundedCornerShape(24.dp))
            .background(Color.White)
            .padding(20.dp),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.QrCode,
            contentDescription = "QR Code แต้ม",
            modifier = Modifier.fillMaxSize(),
            tint = WearthDarkGreen
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "${customer.name} • ${customer.phone}",
          color = Color.White,
          style = MaterialTheme.typography.titleMedium,
          fontWeight = FontWeight.Bold
        )
        Text(
          text = "แต้มคงเหลือ: ${PointsCalculator.formatPointsWithBaht(customer.points)}",
          color = Color(0xFFFDE68A),
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
          onClick = onDismiss,
          shape = RoundedCornerShape(16.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1F5E3B)),
          modifier = Modifier
            .fillMaxWidth(0.7f)
            .height(48.dp)
        ) {
          Text("ย้อนกลับ", color = Color.White, fontWeight = FontWeight.Bold)
        }
      }
    }
  }
}
