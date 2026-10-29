package com.example.wearth

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
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.ContactSupport
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PinDrop
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.SwapHoriz
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WearthBackground
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthPointsGoldText
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

@Composable
fun ProfileScreen(
  customer: Customer,
  onOpenCustomerSwitcher: () -> Unit,
  onOpenLocationPicker: () -> Unit = {},
  onOpenTierModal: () -> Unit,
  onNavigateToTab: (CustomerTab) -> Unit,
  onSwitchToStaff: () -> Unit
) {
  LazyColumn(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("profile_screen"),
    contentPadding = PaddingValues(20.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Header Title
    item {
      Text(
        text = "โปรไฟล์",
        style = MaterialTheme.typography.headlineMedium,
        fontWeight = FontWeight.Bold,
        color = WearthTextPrimary
      )
    }

    // 1. Profile Avatar Card
    item {
      Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
      ) {
        Row(
          modifier = Modifier.padding(20.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Avatar circle with initial letter
          Box(
            modifier = Modifier
              .size(64.dp)
              .clip(CircleShape)
              .background(Color(0xFF4A705B)),
            contentAlignment = Alignment.Center
          ) {
            Text(
              text = customer.name.firstOrNull()?.toString() ?: "M",
              color = Color.White,
              fontSize = 28.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.width(16.dp))

          Column {
            Text(
              text = customer.name,
              style = MaterialTheme.typography.titleLarge,
              fontWeight = FontWeight.Bold,
              color = WearthTextPrimary
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "เบอร์โทร: ${customer.phone}",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
              text = "ระดับสมาชิก: ${customer.tierName}",
              style = MaterialTheme.typography.labelSmall,
              color = WearthDarkGreen,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(WearthLightTeal)
                .padding(horizontal = 8.dp, vertical = 2.dp)
            )
          }
        }
      }
    }

    // 2. 3 Stats Cards Row (matching screenshot)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
      ) {
        // Stat 1: แต้มสะสม
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(CustomerTab.WALLET) }
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "${customer.points}",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = WearthDarkGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "แต้มสะสม",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary,
              fontSize = 11.sp
            )
          }
        }

        // Stat 2: เสื้อที่นำมาแลก
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(CustomerTab.DEPOSIT) }
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "${customer.totalClothesDeposited}",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = WearthTealGreen
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "เสื้อที่นำมาแลก",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary,
              fontSize = 11.sp
            )
          }
        }

        // Stat 3: ครั้งที่ใช้แต้ม
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.White),
          modifier = Modifier
            .weight(1f)
            .clickable { onNavigateToTab(CustomerTab.WALLET) }
        ) {
          Column(
            modifier = Modifier.padding(14.dp),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Text(
              text = "${customer.totalRedeemedTimes}",
              style = MaterialTheme.typography.headlineMedium,
              fontWeight = FontWeight.ExtraBold,
              color = WearthPointsGoldText
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
              text = "ครั้งที่ใช้แต้ม",
              style = MaterialTheme.typography.bodySmall,
              color = WearthTextSecondary,
              fontSize = 11.sp
            )
          }
        }
      }
    }

    // 3. Menu items
    item {
      Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        modifier = Modifier.fillMaxWidth()
      ) {
        Column(modifier = Modifier.padding(vertical = 8.dp)) {
          ProfileMenuItem(
            icon = Icons.Default.History,
            title = "ประวัติการทำรายการ",
            onClick = { onNavigateToTab(CustomerTab.WALLET) }
          )
          ProfileMenuItem(
            icon = Icons.Default.PinDrop,
            title = "ปักหมุดที่อยู่รับ-ส่งสินค้า (Eco Map Pin)",
            onClick = onOpenLocationPicker
          )
          ProfileMenuItem(
            icon = Icons.Default.Star,
            title = "สิทธิประโยชน์แต่ละระดับ",
            onClick = onOpenTierModal
          )
          ProfileMenuItem(
            icon = Icons.Default.LocalOffer,
            title = "คูปอง / โปรโมชั่น",
            onClick = { /* Promo code */ }
          )
          ProfileMenuItem(
            icon = Icons.Default.Person,
            title = "สลับบัญชีผู้ใช้ทดสอบ",
            onClick = onOpenCustomerSwitcher
          )
          ProfileMenuItem(
            icon = Icons.Default.ContactSupport,
            title = "ติดต่อเรา / สาขา WEARTH",
            onClick = { /* Contact */ }
          )
        }
      }
    }

    // 4. Switch to Staff Action
    item {
      Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = WearthLightTeal),
        modifier = Modifier
          .fillMaxWidth()
          .clickable { onSwitchToStaff() }
      ) {
        Row(
          modifier = Modifier.padding(16.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.SwapHoriz,
              contentDescription = null,
              tint = WearthDarkGreen
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = "เข้าสู่โหมดพนักงานร้าน",
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                color = WearthDarkGreen
              )
              Text(
                text = "ประเมินราคา, ตัดแต้ม, เช็คสต็อก, ดูแดชบอร์ด",
                style = MaterialTheme.typography.bodySmall,
                color = WearthTealGreen
              )
            }
          }
          Icon(
            imageVector = Icons.Default.ChevronRight,
            contentDescription = null,
            tint = WearthDarkGreen
          )
        }
      }
    }
  }
}

@Composable
fun ProfileMenuItem(
  icon: ImageVector,
  title: String,
  onClick: () -> Unit
) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .padding(horizontal = 18.dp, vertical = 14.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Row(verticalAlignment = Alignment.CenterVertically) {
      Icon(
        imageVector = icon,
        contentDescription = null,
        tint = WearthDarkGreen,
        modifier = Modifier.size(20.dp)
      )
      Spacer(modifier = Modifier.width(14.dp))
      Text(
        text = title,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.SemiBold,
        color = WearthTextPrimary
      )
    }
    Icon(
      imageVector = Icons.Default.ChevronRight,
      contentDescription = null,
      tint = Color(0xFF94A3B8),
      modifier = Modifier.size(18.dp)
    )
  }
}
