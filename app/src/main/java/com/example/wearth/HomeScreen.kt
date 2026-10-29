package com.example.wearth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import com.example.ui.theme.WearthCardGreen
import com.example.ui.theme.WearthCardGreenGradientEnd
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthDepositBg
import com.example.ui.theme.WearthDepositColor
import com.example.ui.theme.WearthGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGold
import com.example.ui.theme.WearthPointsGoldBg
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthShopPurple
import com.example.ui.theme.WearthShopPurpleBg
import com.example.ui.theme.WearthSurface
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

@Composable
fun HomeScreen(
  customer: Customer,
  bestSellerItems: List<ClothingItem>,
  onNavigateToTab: (CustomerTab) -> Unit,
  onSelectItem: (ClothingItem) -> Unit,
  onOpenCameraScanner: () -> Unit,
  onOpenRates: () -> Unit,
  onOpenTier: () -> Unit
) {
  val context = LocalContext.current
  val expiringBatch = customer.batches.find { it.isExpiringSoon }

  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("home_screen_content"),
    contentPadding = PaddingValues(bottom = 24.dp)
  ) {
    // 1. Header with WEARTH logo & Notification
    item {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .background(Color.White)
          .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
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
              imageVector = Icons.Default.Spa,
              contentDescription = "Logo",
              tint = Color.White,
              modifier = Modifier.size(20.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = "WEARTH",
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.ExtraBold,
              color = WearthDarkGreen,
              letterSpacing = 1.sp
            )
            Text(
              text = "แลกเสื้อ สะสมแต้ม ลดขยะสิ่งทอ",
              style = MaterialTheme.typography.bodySmall,
              fontSize = 11.sp,
              color = WearthTealGreen
            )
          }
        }

        IconButton(
          onClick = { /* notification action */ },
          modifier = Modifier.testTag("notification_button")
        ) {
          Icon(
            imageVector = Icons.Default.Notifications,
            contentDescription = "การแจ้งเตือน",
            tint = WearthDarkGreen
          )
        }
      }
    }

    // 2. Greeting
    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp, vertical = 14.dp)
      ) {
        Text(
          text = "สวัสดี, ${customer.name}",
          style = MaterialTheme.typography.headlineMedium,
          fontWeight = FontWeight.Bold,
          color = WearthTextPrimary
        )
        Text(
          text = "มาแลกเสื้อ สะสมแต้ม ลดขยะสิ่งทอกันเถอะ!",
          style = MaterialTheme.typography.bodyMedium,
          color = WearthTextSecondary
        )
      }
    }

    // 3. Big Card: Points Balance
    item {
      Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        Card(
          shape = RoundedCornerShape(24.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          modifier = Modifier
            .fillMaxWidth()
            .clickable { onNavigateToTab(CustomerTab.WALLET) }
            .testTag("home_points_card")
        ) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .background(
                brush = Brush.linearGradient(
                  colors = listOf(WearthCardGreen, WearthCardGreenGradientEnd)
                )
              )
              .padding(22.dp)
          ) {
            // Decorative background leaf circle
            Box(
              modifier = Modifier
                .size(100.dp)
                .offset(x = 40.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(Color.White.copy(alpha = 0.06f))
                .align(Alignment.TopEnd)
            )

            Column {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
              ) {
                Column {
                  Text(
                    text = "แต้มของคุณ (บัตรกำนัลของร้าน)",
                    color = Color(0xFFA7F3D0),
                    style = MaterialTheme.typography.bodyMedium
                  )
                  Spacer(modifier = Modifier.height(4.dp))
                  Row(verticalAlignment = Alignment.Bottom) {
                    Text(
                      text = "${customer.points}",
                      color = Color.White,
                      fontSize = 42.sp,
                      fontWeight = FontWeight.ExtraBold,
                      lineHeight = 44.sp
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
                    text = "(= ${customer.points} บาท)",
                    color = Color(0xFFFDE68A),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                  )
                }

                // Forward button circle with leaf
                Box(
                  modifier = Modifier
                    .size(46.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f)),
                  contentAlignment = Alignment.Center
                ) {
                  Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "ดูประวัติแต้ม",
                    tint = Color.White
                  )
                }
              }

              Spacer(modifier = Modifier.height(16.dp))

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "ดูประวัติแต้ม >",
                  color = Color.White,
                  style = MaterialTheme.typography.labelMedium,
                  fontWeight = FontWeight.Medium
                )

                Text(
                  text = "ระดับ: ${customer.tierName}",
                  color = Color(0xFFBBF7D0),
                  style = MaterialTheme.typography.labelSmall,
                  modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.White.copy(alpha = 0.15f))
                    .padding(horizontal = 8.dp, vertical = 3.dp)
                )
              }
            }
          }
        }
      }
    }

    // 4. Points Expiring Alert (If any)
    if (expiringBatch != null) {
      item {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF2F2)),
          border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFCA5A5)),
          modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp)
            .clickable { onNavigateToTab(CustomerTab.WALLET) }
            .testTag("expiring_points_alert_banner")
        ) {
          Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "⏰", fontSize = 24.sp)
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "แจ้งเตือน: ${expiringBatch.amount} แต้ม (${expiringBatch.amount} บาท) ใกล้หมดอายุ!",
                style = MaterialTheme.typography.titleSmall,
                color = Color(0xFFB91C1C),
                fontWeight = FontWeight.Bold
              )
              Text(
                text = "หมดอายุใน 5 วัน (อายุแต้ม 6 เดือน) รีบนำมาแลกเสื้อกันนะ!",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF7F1D1D)
              )
            }
          }
        }
      }
    }

    // 4.1 CameraX Fast Appraisal Scanner Banner
    item {
      Spacer(modifier = Modifier.height(14.dp))
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE2E8F0)),
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp)
          .clickable { onOpenCameraScanner() }
          .testTag("home_scan_camera_banner")
      ) {
        Row(
          modifier = Modifier.padding(14.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(46.dp)
              .clip(CircleShape)
              .background(WearthDarkGreen),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = Icons.Default.CameraAlt,
              contentDescription = "กล้อง CameraX",
              tint = Color(0xFFA7F3D0),
              modifier = Modifier.size(24.dp)
            )
          }

          Spacer(modifier = Modifier.width(12.dp))

          Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Text(
                text = "สแกนประเมินราคาเสื้อผ้า",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WearthDarkGreen
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "CameraX",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF059669))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = "ส่องเสื้อผ้าประเมินราคา รับแต้มทันใจ (+25%)",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary,
              fontSize = 12.sp
            )
          }

          Icon(
            imageVector = Icons.Default.ArrowForward,
            contentDescription = null,
            tint = WearthDarkGreen,
            modifier = Modifier.size(18.dp)
          )
        }
      }
    }

    // 5. Quick Actions (3 buttons like the reference screenshot)
    item {
      Spacer(modifier = Modifier.height(16.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
      ) {
        // Button 1: นำเสื้อมาแลก
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(CustomerTab.DEPOSIT) }
            .testTag("quick_action_deposit")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(WearthDepositBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = WearthDepositColor,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "นำเสื้อมาแลก",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
          }
        }

        // Button 2: เลือกซื้อเสื้อ
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(CustomerTab.SHOP) }
            .testTag("quick_action_shop")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(WearthShopPurpleBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.ShoppingBag,
                contentDescription = null,
                tint = WearthShopPurple,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "เลือกซื้อเสื้อ",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
          }
        }

        // Button 3: วิธีใช้งาน / อัตรา
        Card(
          shape = RoundedCornerShape(18.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .weight(1f)
            .clickable { onOpenRates() }
            .testTag("quick_action_howto")
        ) {
          Column(
            modifier = Modifier.padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(WearthPointsGoldBg),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.HelpOutline,
                contentDescription = null,
                tint = WearthPointsGoldText,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
              text = "วิธีใช้งาน",
              style = MaterialTheme.typography.labelMedium,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
          }
        }
      }
    }

    // 6. Promotional Thrift Banner (matching mockup with image)
    item {
      Spacer(modifier = Modifier.height(18.dp))
      Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        Card(
          shape = RoundedCornerShape(20.dp),
          elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
          modifier = Modifier
            .fillMaxWidth()
            .height(110.dp)
            .clickable { onNavigateToTab(CustomerTab.SHOP) }
        ) {
          Box(modifier = Modifier.fillMaxSize()) {
            Image(
              painter = painterResource(id = ImageHelper.getDrawableId(context, "banner_thrift_rack")),
              contentDescription = "เสื้อผ้ามือสองคุณภาพดี",
              modifier = Modifier.fillMaxSize(),
              contentScale = ContentScale.Crop
            )
            // Dark gradient overlay
            Box(
              modifier = Modifier
                .fillMaxSize()
                .background(
                  Brush.horizontalGradient(
                    colors = listOf(
                      Color.Black.copy(alpha = 0.82f),
                      Color.Black.copy(alpha = 0.40f)
                    )
                  )
                )
            )
            Column(
              modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
              verticalArrangement = Arrangement.Center
            ) {
              Text(
                text = "เสื้อผ้ามือสอง คุณภาพดี",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
              )
              Spacer(modifier = Modifier.height(4.dp))
              Text(
                text = "คัดแล้ว | ซักแล้ว | พร้อมใส่ 🌿",
                color = Color(0xFFA7F3D0),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.Medium
              )
            }
          }
        }
      }
    }

    // 7. Best Sellers (สินค้าขายดี 🔥 ดูทั้งหมด >)
    item {
      Spacer(modifier = Modifier.height(20.dp))
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .padding(horizontal = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Text(
            text = "สินค้าขายดี",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = WearthTextPrimary
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(text = "🔥", fontSize = 16.sp)
        }
        Text(
          text = "ดูทั้งหมด >",
          style = MaterialTheme.typography.labelMedium,
          color = WearthTealGreen,
          fontWeight = FontWeight.Bold,
          modifier = Modifier.clickable { onNavigateToTab(CustomerTab.SHOP) }
        )
      }
      Spacer(modifier = Modifier.height(10.dp))
    }

    item {
      LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        items(bestSellerItems) { item ->
          BestSellerCard(item = item, onClick = { onSelectItem(item) })
        }
      }
    }

    // 8. 3-Step Concept Card ("ฝากเสื้อ > ได้แต้ม > ใช้แต้ม")
    item {
      Spacer(modifier = Modifier.height(24.dp))
      Box(modifier = Modifier.padding(horizontal = 20.dp)) {
        Card(
          shape = RoundedCornerShape(20.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          Column(modifier = Modifier.padding(18.dp)) {
            Text(
              text = "หมุนเวียนแฟชั่นง่ายๆ ใน 3 ขั้นตอน",
              style = MaterialTheme.typography.titleSmall,
              fontWeight = FontWeight.Bold,
              color = WearthDarkGreen
            )
            Spacer(modifier = Modifier.height(12.dp))

            StepRow(
              number = "1",
              title = "ฝากเสื้อที่ร้าน",
              desc = "นำเสื้อผ้าที่ไม่ใส่แล้วมาส่งที่ร้านเพื่อตรวจสภาพ",
              color = WearthDepositColor
            )
            Spacer(modifier = Modifier.height(10.dp))
            StepRow(
              number = "2",
              title = "เลือกรับแต้มคุ้มกว่า",
              desc = "รับแต้ม = ราคาประเมิน × 1.25 (1 แต้ม = 1 บาท)",
              color = WearthPointsGoldText
            )
            Spacer(modifier = Modifier.height(10.dp))
            StepRow(
              number = "3",
              title = "ใช้แต้มแลกเสื้อใหม่",
              desc = "เลือกเสื้อที่ชอบ ใช้แต้มแทนเงินสด เติมเงินเฉพาะส่วนต่าง",
              color = WearthShopPurple
            )

            Spacer(modifier = Modifier.height(14.dp))
            Button(
              onClick = onOpenRates,
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = WearthLightTeal),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(
                text = "ดูตารางอัตราประเมินและการใช้แต้ม",
                color = WearthDarkGreen,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.labelMedium
              )
            }
          }
        }
      }
    }
  }
}

@Composable
fun BestSellerCard(
  item: ClothingItem,
  onClick: () -> Unit
) {
  val context = LocalContext.current
  Card(
    shape = RoundedCornerShape(16.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .width(140.dp)
      .clickable { onClick() }
      .testTag("best_seller_card_${item.id}")
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
      ) {
        Image(
          painter = painterResource(id = ImageHelper.getDrawableId(context, item.imageDrawableName)),
          contentDescription = item.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Level Badge
        val badgeBg = if (item.isBrand) WearthShopPurple else WearthDarkGreen
        Text(
          text = item.level.label,
          color = Color.White,
          style = MaterialTheme.typography.labelSmall,
          fontSize = 10.sp,
          fontWeight = FontWeight.Bold,
          modifier = Modifier
            .padding(6.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(badgeBg)
            .padding(horizontal = 6.dp, vertical = 2.dp)
        )
      }

      Column(modifier = Modifier.padding(10.dp)) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.titleSmall,
          maxLines = 1,
          fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
          text = "฿ ${item.price}",
          style = MaterialTheme.typography.bodyMedium,
          fontWeight = FontWeight.ExtraBold,
          color = WearthDarkGreen
        )
      }
    }
  }
}

@Composable
fun StepRow(
  number: String,
  title: String,
  desc: String,
  color: Color
) {
  Row(verticalAlignment = Alignment.Top) {
    Box(
      modifier = Modifier
        .size(26.dp)
        .clip(CircleShape)
        .background(color.copy(alpha = 0.15f)),
      contentAlignment = Alignment.Center
    ) {
      Text(
        text = number,
        color = color,
        fontWeight = FontWeight.Bold,
        fontSize = 13.sp
      )
    }
    Spacer(modifier = Modifier.width(10.dp))
    Column {
      Text(
        text = title,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = WearthTextPrimary
      )
      Text(
        text = desc,
        style = MaterialTheme.typography.bodySmall,
        color = WearthTextSecondary
      )
    }
  }
}
