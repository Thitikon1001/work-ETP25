package com.example.wearth

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.WearthDarkGreen
import com.example.ui.theme.WearthLightTeal
import com.example.ui.theme.WearthTealGreen

@Composable
fun WelcomeScreen(
  onStartClicked: () -> Unit
) {
  val context = LocalContext.current

  Box(
    modifier = Modifier
      .fillMaxSize()
      .testTag("welcome_screen")
  ) {
    // 1. Natural fabric draped background
    Image(
      painter = painterResource(id = ImageHelper.getDrawableId(context, "bg_welcome_fabric")),
      contentDescription = null,
      modifier = Modifier.fillMaxSize(),
      contentScale = ContentScale.Crop
    )

    // Subtle dark vignette overlay
    Box(
      modifier = Modifier
        .fillMaxSize()
        .background(
          Brush.verticalGradient(
            colors = listOf(
              Color.Black.copy(alpha = 0.35f),
              Color.Black.copy(alpha = 0.55f),
              Color.Black.copy(alpha = 0.85f)
            )
          )
        )
    )

    // Center Branding
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(horizontal = 28.dp, vertical = 40.dp),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.SpaceBetween
    ) {
      Spacer(modifier = Modifier.height(20.dp))

      // Center Icon & Title
      Column(
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        // Hanger Leaf Icon
        Box(
          modifier = Modifier
            .size(90.dp)
            .clip(CircleShape)
            .background(Color.White.copy(alpha = 0.15f)),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = Icons.Default.Spa,
            contentDescription = "WEARTH Logo",
            tint = Color.White,
            modifier = Modifier.size(52.dp)
          )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
          text = "WEARTH",
          fontSize = 42.sp,
          fontWeight = FontWeight.ExtraBold,
          color = Color.White,
          letterSpacing = 4.sp,
          style = MaterialTheme.typography.displayLarge
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
          text = "“แลกเสื้อ สะสมแต้ม ลดขยะสิ่งทอ”",
          fontSize = 18.sp,
          fontWeight = FontWeight.Medium,
          color = Color(0xFFA7F3D0),
          textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
          text = "ร้านรับซื้อ แลกเปลี่ยน และขายเสื้อผ้ามือสอง\nแต้มทำงานเหมือนบัตรกำนัลของร้าน (1 แต้ม = 1 บาท)",
          fontSize = 13.sp,
          color = Color(0xFFD1D5DB),
          textAlign = TextAlign.Center,
          lineHeight = 18.sp
        )
      }

      // Bottom Button
      Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Button(
          onClick = onStartClicked,
          shape = RoundedCornerShape(24.dp),
          colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1E5336)),
          modifier = Modifier
            .fillMaxWidth(0.85f)
            .height(54.dp)
            .testTag("welcome_start_button")
        ) {
          Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
          ) {
            Text(
              text = "เริ่มต้นใช้งาน",
              fontSize = 17.sp,
              fontWeight = FontWeight.Bold,
              color = Color.White
            )
            Spacer(modifier = Modifier.width(8.dp))
            Icon(
              imageVector = Icons.Default.ArrowForward,
              contentDescription = null,
              tint = Color.White
            )
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = "ต้นแบบแอปพลิเคชัน (Prototype Demo)",
          fontSize = 12.sp,
          color = Color(0xFF9CA3AF)
        )
      }
    }
  }
}
