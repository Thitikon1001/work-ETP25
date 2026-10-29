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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthShopPurple
import com.example.ui.theme.WearthShopPurpleBg
import com.example.ui.theme.WearthSurface
import com.example.ui.theme.WearthTealGreen
import com.example.ui.theme.WearthTextPrimary
import com.example.ui.theme.WearthTextSecondary

@Composable
fun ShopScreen(
  items: List<ClothingItem>,
  searchQuery: String,
  selectedCategory: String,
  selectedLevelFilter: ClothingLevel?,
  onSearchChange: (String) -> Unit,
  onCategorySelect: (String) -> Unit,
  onLevelFilterSelect: (ClothingLevel?) -> Unit,
  onSelectItem: (ClothingItem) -> Unit
) {
  val context = LocalContext.current
  val categories = listOf("ทั้งหมด", "เสื้อยืด", "เสื้อเชิ้ต", "แจ็กเก็ต", "กางเกง")

  Column(
    modifier = Modifier
      .fillMaxSize()
      .background(WearthBackground)
      .testTag("shop_screen")
  ) {
    // Header Title
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(Color.White)
        .padding(horizontal = 20.dp, vertical = 14.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "ร้านค้า",
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.Bold,
        color = WearthTextPrimary
      )
      Spacer(modifier = Modifier.width(8.dp))
      Text(
        text = "(${items.size} ชิ้น)",
        style = MaterialTheme.typography.bodyMedium,
        color = WearthTextSecondary
      )
    }

    LazyVerticalGrid(
      columns = GridCells.Fixed(2),
      contentPadding = PaddingValues(16.dp),
      horizontalArrangement = Arrangement.spacedBy(12.dp),
      verticalArrangement = Arrangement.spacedBy(14.dp),
      modifier = Modifier.fillMaxSize()
    ) {
      // 1. Search Bar
      item(span = { GridItemSpan(2) }) {
        OutlinedTextField(
          value = searchQuery,
          onValueChange = onSearchChange,
          placeholder = { Text("ค้นหาชื่อสินค้า / ประเภท", color = WearthTextSecondary) },
          leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = "ค้นหา", tint = WearthDarkGreen)
          },
          shape = RoundedCornerShape(16.dp),
          colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color.White,
            unfocusedContainerColor = Color.White,
            focusedBorderColor = WearthTealGreen,
            unfocusedBorderColor = WearthBorder
          ),
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("search_text_field")
        )
      }

      // 2. Category Filter Chips
      item(span = { GridItemSpan(2) }) {
        LazyRow(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          modifier = Modifier.fillMaxWidth()
        ) {
          items(categories) { cat ->
            val isSelected = cat == selectedCategory
            FilterChip(
              selected = isSelected,
              onClick = { onCategorySelect(cat) },
              label = {
                Text(
                  text = cat,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              shape = RoundedCornerShape(20.dp),
              colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = WearthDarkGreen,
                selectedLabelColor = Color.White,
                containerColor = Color.White,
                labelColor = WearthTextPrimary
              ),
              border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = isSelected,
                borderColor = if (isSelected) WearthDarkGreen else WearthBorder
              )
            )
          }
        }
      }

      // 3. Level Filter Chips (ทั่วไป vs แบรนด์)
      item(span = { GridItemSpan(2) }) {
        Row(
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ระดับ:",
            style = MaterialTheme.typography.bodySmall,
            color = WearthTextSecondary,
            fontWeight = FontWeight.Bold
          )

          FilterChip(
            selected = selectedLevelFilter == null,
            onClick = { onLevelFilterSelect(null) },
            label = { Text("ทั้งหมด", fontSize = 12.sp) },
            shape = RoundedCornerShape(14.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = WearthLightTeal,
              selectedLabelColor = WearthDarkGreen
            )
          )

          FilterChip(
            selected = selectedLevelFilter == ClothingLevel.GENERAL,
            onClick = { onLevelFilterSelect(ClothingLevel.GENERAL) },
            label = { Text("ทั่วไป (ใช้แต้มได้ 100%)", fontSize = 12.sp) },
            shape = RoundedCornerShape(14.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = WearthLightTeal,
              selectedLabelColor = WearthDarkGreen
            )
          )

          FilterChip(
            selected = selectedLevelFilter == ClothingLevel.BRAND,
            onClick = { onLevelFilterSelect(ClothingLevel.BRAND) },
            label = { Text("แบรนด์ (ใช้แต้มได้ 70%)", fontSize = 12.sp) },
            shape = RoundedCornerShape(14.dp),
            colors = FilterChipDefaults.filterChipColors(
              selectedContainerColor = WearthShopPurpleBg,
              selectedLabelColor = WearthShopPurple
            )
          )
        }
      }

      // 4. Promo Banner in Grid
      item(span = { GridItemSpan(2) }) {
        Card(
          shape = RoundedCornerShape(16.dp),
          colors = CardDefaults.cardColors(containerColor = Color.Transparent),
          modifier = Modifier
            .fillMaxWidth()
            .height(76.dp)
        ) {
          Box(
            modifier = Modifier
              .fillMaxSize()
              .background(
                Brush.horizontalGradient(
                  colors = listOf(WearthDarkGreen, WearthTealGreen)
                )
              )
              .padding(horizontal = 16.dp, vertical = 12.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxSize(),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Column {
                Text(
                  text = "แฟชั่นมือสอง ใส่ได้ไม่รู้จบ 🌿",
                  color = Color.White,
                  style = MaterialTheme.typography.titleSmall,
                  fontWeight = FontWeight.Bold
                )
                Text(
                  text = "ประหยัดกว่า • คุณภาพคัดสรร • รักษ์โลก",
                  color = Color(0xFFA7F3D0),
                  style = MaterialTheme.typography.bodySmall
                )
              }
            }
          }
        }
      }

      // 5. Product Grid Items (2 columns)
      items(items) { item ->
        ProductGridCard(
          item = item,
          onClick = { onSelectItem(item) }
        )
      }
    }
  }
}

@Composable
fun ProductGridCard(
  item: ClothingItem,
  onClick: () -> Unit
) {
  val context = LocalContext.current
  var isFavorite by remember { mutableStateOf(false) }

  Card(
    shape = RoundedCornerShape(18.dp),
    colors = CardDefaults.cardColors(containerColor = Color.White),
    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
    modifier = Modifier
      .fillMaxWidth()
      .clickable { onClick() }
      .testTag("product_card_${item.id}")
  ) {
    Column {
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(160.dp)
      ) {
        Image(
          painter = painterResource(id = ImageHelper.getDrawableId(context, item.imageDrawableName)),
          contentDescription = item.name,
          modifier = Modifier.fillMaxSize(),
          contentScale = ContentScale.Crop
        )

        // Top badges: แนะนำ & Level
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (item.isRecommended) {
              Text(
                text = "แนะนำ",
                color = Color.White,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                  .clip(RoundedCornerShape(6.dp))
                  .background(Color(0xFF047857))
                  .padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
            Text(
              text = item.level.label,
              color = Color.White,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(if (item.isBrand) WearthShopPurple else WearthDarkGreen)
                .padding(horizontal = 6.dp, vertical = 2.dp)
            )
          }

          // Wishlist heart
          Box(
            modifier = Modifier
              .size(28.dp)
              .clip(CircleShape)
              .background(Color.White.copy(alpha = 0.85f))
              .clickable { isFavorite = !isFavorite },
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
              contentDescription = "ถูกใจ",
              tint = if (isFavorite) Color.Red else WearthTextSecondary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Column(modifier = Modifier.padding(12.dp)) {
        Text(
          text = item.name,
          style = MaterialTheme.typography.titleSmall,
          fontWeight = FontWeight.Bold,
          maxLines = 1
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "ไซซ์ ${item.size} • สภาพ${item.condition.label}",
            style = MaterialTheme.typography.bodySmall,
            color = WearthTextSecondary,
            fontSize = 11.sp
          )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "฿ ${item.price}",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.ExtraBold,
            color = WearthDarkGreen
          )

          // Status indicator
          Text(
            text = item.status.label,
            style = MaterialTheme.typography.labelSmall,
            fontSize = 10.sp,
            color = when (item.status) {
              ItemStatus.AVAILABLE -> WearthTealGreen
              ItemStatus.RESERVED -> WearthShopPurple
              ItemStatus.SOLD -> Color.Gray
            }
          )
        }
      }
    }
  }
}
