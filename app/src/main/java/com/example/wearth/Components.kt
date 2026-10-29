package com.example.wearth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.QrCode
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Storefront
import androidx.compose.material3.Badge
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
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

/**
 * ป้ายต้นแบบ (Prototype Badge) ด้านบนสุดของแอป
 */
@Composable
fun PrototypeTopBanner(
  userMode: UserMode,
  currentCustomer: Customer,
  onSwitchMode: (UserMode) -> Unit,
  onOpenCustomerSwitcher: () -> Unit
) {
  Column(
    modifier = Modifier
      .fillMaxWidth()
      .background(WearthDarkGreen)
  ) {
    // Top tiny badge
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color(0xFF0F3E22))
        .padding(horizontal = 16.dp, vertical = 4.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .clip(CircleShape)
            .background(Color(0xFF34D399))
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
          text = "ต้นแบบ (Prototype) • 1 แต้ม = 1 บาท",
          color = Color(0xFFD1FAE5),
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Medium
        )
      }

      // Demo role switcher button
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF1E5E38))
          .clickable {
            val nextMode = if (userMode == UserMode.CUSTOMER) UserMode.STAFF else UserMode.CUSTOMER
            onSwitchMode(nextMode)
          }
          .padding(horizontal = 10.dp, vertical = 2.dp)
          .testTag("toggle_demo_mode_button"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = Icons.Default.SwapHoriz,
          contentDescription = "สลับโหมด",
          tint = Color.White,
          modifier = Modifier.size(14.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (userMode == UserMode.CUSTOMER) "สลับเป็นโหมดพนักงาน" else "สลับเป็นโหมดลูกค้า",
          color = Color.White,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }

    // Role Indicator & Quick Switcher Row
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 16.dp, vertical = 6.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      if (userMode == UserMode.CUSTOMER) {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0x33FFFFFF))
            .clickable { onOpenCustomerSwitcher() }
            .padding(horizontal = 10.dp, vertical = 4.dp)
            .testTag("switch_customer_profile_button"),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.Person,
            contentDescription = "ผู้ใช้",
            tint = Color(0xFFA7F3D0),
            modifier = Modifier.size(16.dp)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = "ผู้ใช้: ${currentCustomer.name} (${PointsCalculator.formatPointsWithBaht(currentCustomer.points)}) ▾",
            color = Color.White,
            style = MaterialTheme.typography.labelMedium
          )
        }
      } else {
        Row(
          modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(WearthPointsGold.copy(alpha = 0.25f))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "🛠️ โหมดพนักงานร้าน WEARTH (เคาน์เตอร์)",
            color = Color(0xFFFDE68A),
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold
          )
        }
      }

      // App Tag
      Text(
        text = "WEARTH",
        color = Color(0xFF6EE7B7),
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = 1.sp
      )
    }
  }
}

/**
 * แถบเมนูล่าง 5 ปุ่มสำหรับลูกค้า:
 * หน้าแรก | ร้านค้า | ฝากเสื้อ (ปุ่มเด่นตรงกลาง) | แต้ม | โปรไฟล์
 */
@Composable
fun CustomerBottomNavBar(
  activeTab: CustomerTab,
  onTabSelected: (CustomerTab) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    containerColor = Color.White,
    tonalElevation = 8.dp,
    modifier = modifier.testTag("customer_bottom_nav_bar")
  ) {
    // 1. หน้าแรก
    NavigationBarItem(
      selected = activeTab == CustomerTab.HOME,
      onClick = { onTabSelected(CustomerTab.HOME) },
      icon = {
        Icon(
          imageVector = if (activeTab == CustomerTab.HOME) Icons.Outlined.Home else Icons.Outlined.Home,
          contentDescription = "หน้าแรก"
        )
      },
      label = { Text("หน้าแรก", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthDarkGreen,
        selectedTextColor = WearthDarkGreen,
        indicatorColor = WearthLightTeal
      ),
      modifier = Modifier.testTag("tab_home")
    )

    // 2. ร้านค้า (เลือกเสื้อ)
    NavigationBarItem(
      selected = activeTab == CustomerTab.SHOP,
      onClick = { onTabSelected(CustomerTab.SHOP) },
      icon = {
        Icon(
          imageVector = Icons.Outlined.Storefront,
          contentDescription = "ร้านค้า"
        )
      },
      label = { Text("ร้านค้า", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthShopPurple,
        selectedTextColor = WearthShopPurple,
        indicatorColor = WearthShopPurpleBg
      ),
      modifier = Modifier.testTag("tab_shop")
    )

    // 3. ฝากเสื้อ / รับแต้ม (Center Highlight Button)
    NavigationBarItem(
      selected = activeTab == CustomerTab.DEPOSIT,
      onClick = { onTabSelected(CustomerTab.DEPOSIT) },
      icon = {
        Box(
          modifier = Modifier
            .size(38.dp)
            .clip(CircleShape)
            .background(if (activeTab == CustomerTab.DEPOSIT) WearthTealGreen else WearthDarkGreen),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.QrCodeScanner,
            contentDescription = "ฝากเสื้อ",
            tint = Color.White,
            modifier = Modifier.size(20.dp)
          )
        }
      },
      label = {
        Text(
          "ฝากเสื้อ",
          style = MaterialTheme.typography.labelSmall,
          fontWeight = FontWeight.Bold,
          color = if (activeTab == CustomerTab.DEPOSIT) WearthTealGreen else WearthTextSecondary
        )
      },
      colors = NavigationBarItemDefaults.colors(
        indicatorColor = Color.Transparent
      ),
      modifier = Modifier.testTag("tab_deposit")
    )

    // 4. แต้ม / กระเป๋าแต้ม
    NavigationBarItem(
      selected = activeTab == CustomerTab.WALLET,
      onClick = { onTabSelected(CustomerTab.WALLET) },
      icon = {
        Icon(
          imageVector = Icons.Outlined.QrCode,
          contentDescription = "แต้ม"
        )
      },
      label = { Text("แต้ม", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthPointsGoldText,
        selectedTextColor = WearthPointsGoldText,
        indicatorColor = WearthPointsGoldBg
      ),
      modifier = Modifier.testTag("tab_wallet")
    )

    // 5. โปรไฟล์
    NavigationBarItem(
      selected = activeTab == CustomerTab.PROFILE,
      onClick = { onTabSelected(CustomerTab.PROFILE) },
      icon = {
        Icon(
          imageVector = Icons.Outlined.Person,
          contentDescription = "โปรไฟล์"
        )
      },
      label = { Text("โปรไฟล์", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthDarkGreen,
        selectedTextColor = WearthDarkGreen,
        indicatorColor = WearthLightTeal
      ),
      modifier = Modifier.testTag("tab_profile")
    )
  }
}

/**
 * แถบเมนูสำหรับพนักงาน:
 * ประเมินเสื้อ | ยืนยันการแลก | สต็อก | แดชบอร์ด
 */
@Composable
fun StaffBottomNavBar(
  activeTab: StaffTab,
  onTabSelected: (StaffTab) -> Unit,
  modifier: Modifier = Modifier
) {
  NavigationBar(
    containerColor = Color.White,
    tonalElevation = 8.dp,
    modifier = modifier.testTag("staff_bottom_nav_bar")
  ) {
    NavigationBarItem(
      selected = activeTab == StaffTab.APPRAISAL,
      onClick = { onTabSelected(StaffTab.APPRAISAL) },
      icon = { Icon(Icons.Default.ShoppingBag, contentDescription = "ประเมินเสื้อ") },
      label = { Text("ประเมินเสื้อ", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthDarkGreen,
        indicatorColor = WearthLightTeal
      ),
      modifier = Modifier.testTag("staff_tab_appraisal")
    )

    NavigationBarItem(
      selected = activeTab == StaffTab.CONFIRM_REDEEM,
      onClick = { onTabSelected(StaffTab.CONFIRM_REDEEM) },
      icon = { Icon(Icons.Default.QrCodeScanner, contentDescription = "ยืนยันการแลก") },
      label = { Text("ยืนยันแลก", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthTealGreen,
        indicatorColor = WearthDepositBg
      ),
      modifier = Modifier.testTag("staff_tab_confirm")
    )

    NavigationBarItem(
      selected = activeTab == StaffTab.PICKUP_DELIVERY,
      onClick = { onTabSelected(StaffTab.PICKUP_DELIVERY) },
      icon = { Icon(Icons.Default.SwapHoriz, contentDescription = "รับ-ส่งถึงที่") },
      label = { Text("รับ-ส่งถึงที่", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthTealGreen,
        indicatorColor = WearthLightTeal
      ),
      modifier = Modifier.testTag("staff_tab_pickup_delivery")
    )

    NavigationBarItem(
      selected = activeTab == StaffTab.INVENTORY,
      onClick = { onTabSelected(StaffTab.INVENTORY) },
      icon = { Icon(Icons.Default.Storefront, contentDescription = "สต็อก") },
      label = { Text("สต็อกเสื้อ", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthShopPurple,
        indicatorColor = WearthShopPurpleBg
      ),
      modifier = Modifier.testTag("staff_tab_inventory")
    )

    NavigationBarItem(
      selected = activeTab == StaffTab.DASHBOARD,
      onClick = { onTabSelected(StaffTab.DASHBOARD) },
      icon = { Icon(Icons.Default.Star, contentDescription = "แดชบอร์ด") },
      label = { Text("แดชบอร์ด", style = MaterialTheme.typography.labelSmall) },
      colors = NavigationBarItemDefaults.colors(
        selectedIconColor = WearthPointsGoldText,
        indicatorColor = WearthPointsGoldBg
      ),
      modifier = Modifier.testTag("staff_tab_dashboard")
    )
  }
}

/**
 * ป๊อปอัปสลับบัญชีลูกค้าสำหรับทดสอบเดโม
 */
@Composable
fun CustomerSwitcherDialog(
  customers: List<Customer>,
  currentCustomerId: String,
  onSelect: (String) -> Unit,
  onDismiss: () -> Unit
) {
  Dialog(onDismissRequest = onDismiss) {
    Card(
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = Color.White),
      modifier = Modifier.fillMaxWidth()
    ) {
      Column(
        modifier = Modifier.padding(20.dp)
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "เลือกโปรไฟล์ลูกค้าตัวอย่าง",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "ปิด")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        customers.forEach { customer ->
          val isSelected = customer.id == currentCustomerId
          val hasExpiringSoon = customer.batches.any { it.isExpiringSoon }

          Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(
              containerColor = if (isSelected) WearthLightTeal else WearthBackground
            ),
            border = if (isSelected) BorderStroke(1.5.dp, WearthTealGreen) else null,
            modifier = Modifier
              .fillMaxWidth()
              .padding(vertical = 4.dp)
              .clickable { onSelect(customer.id) }
              .testTag("select_customer_${customer.id}")
          ) {
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Text(
                    text = customer.name,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                  )
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(
                    text = "(${customer.phone})",
                    style = MaterialTheme.typography.bodySmall
                  )
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                  text = PointsCalculator.formatPointsWithBaht(customer.points),
                  style = MaterialTheme.typography.bodyMedium,
                  color = WearthPointsGoldText,
                  fontWeight = FontWeight.SemiBold
                )
                if (hasExpiringSoon) {
                  Text(
                    text = "⚠️ มีแต้มใกล้หมดอายุใน 5 วัน",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFDC2626),
                    fontWeight = FontWeight.Bold
                  )
                }
              }

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "เลือกอยู่",
                  tint = WearthDarkGreen
                )
              }
            }
          }
        }
      }
    }
  }
}
